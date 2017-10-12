package bigappcompany.com.santhe.network;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bigappcompany.com.santhe.interfaces.ApiRetrofitService;
import bigappcompany.com.santhe.model.CitiesPojo;
import bigappcompany.com.santhe.model.CommentPojo;
import bigappcompany.com.santhe.model.CommentsPojo;
import bigappcompany.com.santhe.model.DiscussionPojo;
import bigappcompany.com.santhe.model.GenerateOtpPojo;
import bigappcompany.com.santhe.model.InstitutionsPojo;
import bigappcompany.com.santhe.model.Likepojo;
import bigappcompany.com.santhe.model.LocationOfficePojo;
import bigappcompany.com.santhe.model.MobileRegisterPojo;
import bigappcompany.com.santhe.model.PersonalisePojo;
import bigappcompany.com.santhe.model.PersonalizedFormingPojo;
import bigappcompany.com.santhe.model.ProductDetailPojo;
import bigappcompany.com.santhe.model.ProfilePojo;
import bigappcompany.com.santhe.model.RegistrationPojo;
import bigappcompany.com.santhe.model.RplyPojo;
import bigappcompany.com.santhe.model.UpdatePojo;
import bigappcompany.com.santhe.other.Constants;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitDataProvider implements ServiceMethods {

    private Call<CustomerWrapper> customer;
    private OkHttpClient.Builder httpClient;

//    private ApiRetrofitService createRetrofitService() {
//
//        // If ur requesting through basic authentication
//        final String credential = Credentials.basic("", "");
//
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS);
//
//        httpClient.addInterceptor(new Interceptor() {
//            @Override
//            public okhttp3.Response intercept(Chain chain) throws IOException {
//                Request original = chain.request();
//
//
//                Request request = original.newBuilder()
//                        .header("Content-Type", "form-data")
//                        .header("Authorization", credential)
//                        .method(original.method(), original.body())
//                        .build();
//
//                Log.w("Retrofit@Response", chain.proceed(request).body().string());
//                return chain.proceed(request);
//
//
//            }
//        });
//        OkHttpClient client = httpClient.build();
//
//        // Basic for request
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build();
//
//        return retrofit.create(ApiRetrofitService.class);
//    }


    private ApiRetrofitService createRetrofitService() {


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiRetrofitService.class);
    }





    @Override
    public void getMobileRegister(String mobileNo, final DownlodableCallback<GenerateOtpPojo> callback) {
        createRetrofitService().getMobileRegistrationApi(mobileNo).enqueue(
                new Callback<GenerateOtpPojo>() {
                    @Override
                    public void onResponse(@NonNull Call<GenerateOtpPojo> call, @NonNull Response<GenerateOtpPojo> response) {
                        if (response.isSuccessful()) {
                            Request request = response.raw().request();

                            Log.d("Result", "Status" + request.toString());

                            GenerateOtpPojo mobileRegisterPojo = response.body();
                            Log.d("Result", "Status" + mobileRegisterPojo.getStatus());
                            callback.onSuccess(mobileRegisterPojo);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GenerateOtpPojo> call, @NonNull Throwable t) {
                        Log.d("Result", "t" + t.getMessage());
                        callback.onFailure(t.getMessage());

                    }
                }
        );

    }

    @Override
    public void getOtpVerify(String mobileNo, String otp, final DownlodableCallback<RegistrationPojo> callback) {
        createRetrofitService().getOtpVerify(mobileNo, otp).enqueue(
                new Callback<RegistrationPojo>() {
                    @Override
                    public void onResponse(@NonNull Call<RegistrationPojo> call, @NonNull Response<RegistrationPojo> response) {
                        if (response.isSuccessful()) {

                            RegistrationPojo mobileRegisterPojo = response.body();
                            Log.d("Result", "otp" + mobileRegisterPojo.getStatus());

                            callback.onSuccess(mobileRegisterPojo);

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RegistrationPojo> call, @NonNull Throwable t) {
                        Log.d("Result", "t" + t.getMessage());
                        callback.onFailure(t.getMessage());

                    }
                }
        );

    }

    @Override
    public void registerCustomer(RequestBody name, RequestBody email, RequestBody user_id, MultipartBody.Part profile_img, final DownlodableCallback<RegistrationPojo> callback) {
        createRetrofitService().registerCustomer(name, email, user_id, profile_img).enqueue(new Callback<RegistrationPojo>() {
            @Override
            public void onResponse(Call<RegistrationPojo> call, Response<RegistrationPojo> response) {

                if (response.isSuccessful()) {

                    Log.d("Result", "response" + response.raw().request().body());

                    RegistrationPojo registrationPojo = response.body();
                    callback.onSuccess(registrationPojo);

                }

            }

            @Override
            public void onFailure(Call<RegistrationPojo> call, Throwable t) {
                Log.d("Result", "t" + t.getMessage());
                callback.onFailure(t.getMessage());

            }
        });
    }

    @Override
    public void MultipleImage(List<MultipartBody.Part> profile_img, DownlodableCallback<ResponseBody> callback) {

        createRetrofitService().multipleImage(profile_img).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Request request = response.raw().request();
                Log.d("Result", "resonse" + request);
                Log.d("Result", "resonse" + response);

                if (response.isSuccessful()) {

                    try {
                        Log.d("Result", "CC" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Result", "resonse" + response.body().toString());

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Result", "t" + t.getMessage());

            }
        });
    }


    @Override
    public void getCategories(final DownlodableCallback<PersonalisePojo> callback) {
        createRetrofitService().getCategoryDetails().enqueue(new Callback<PersonalisePojo>() {
            @Override
            public void onResponse(Call<PersonalisePojo> call, Response<PersonalisePojo> response) {

                if (response.isSuccessful()) {

                    PersonalisePojo registrationPojo = response.body();
                    Log.d("Result", "register Sucess" + registrationPojo.getData().size());
                    callback.onSuccess(registrationPojo);
                }
            }

            @Override
            public void onFailure(Call<PersonalisePojo> call, Throwable t) {
                Log.d("Result", "register failure" + t.getMessage());
                callback.onFailure(t.getMessage());


            }
        });
    }


    @Override
    public void getUfiCategories(final DownlodableCallback<PersonalisePojo> callback) {
        createRetrofitService().getUfiCategoryDetails().enqueue(new Callback<PersonalisePojo>() {
            @Override
            public void onResponse(Call<PersonalisePojo> call, Response<PersonalisePojo> response) {

                if (response.isSuccessful()) {

                    PersonalisePojo registrationPojo = response.body();
                    Log.d("Result", "register Sucess" + registrationPojo.getData().size());
                    callback.onSuccess(registrationPojo);
                }
            }

            @Override
            public void onFailure(Call<PersonalisePojo> call, Throwable t) {
                Log.d("Result", "register failure" + t.getMessage());
                callback.onFailure(t.getMessage());


            }
        });
    }


    @Override
    public void getCitiesDetail(String dealer, String category,final DownlodableCallback<CitiesPojo> callback) {
        createRetrofitService().getCitiesDetails(dealer,category).enqueue(new Callback<CitiesPojo>() {
            @Override
            public void onResponse(Call<CitiesPojo> call, Response<CitiesPojo> response) {

                if (response.isSuccessful()) {

                    CitiesPojo citiesPojo = response.body();

                    Log.d("Result", "register Sucess" + citiesPojo.getData().get(0).getCity_name());

                    callback.onSuccess(citiesPojo);
                }
            }

            @Override
            public void onFailure(Call<CitiesPojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });
    }

    @Override
    public void getLocationDetails(String city_id, String category, final DownlodableCallback<LocationOfficePojo> callback) {
        createRetrofitService().getLocationDetails(city_id, category).enqueue(new Callback<LocationOfficePojo>() {
            @Override
            public void onResponse(Call<LocationOfficePojo> call, Response<LocationOfficePojo> response) {

                if (response.isSuccessful()) {

                    LocationOfficePojo locationOfficePojo = response.body();

                    Log.d("Result", "register Sucess" + locationOfficePojo.getData());

                    callback.onSuccess(locationOfficePojo);
                }

            }

            @Override
            public void onFailure(Call<LocationOfficePojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });


    }

    @Override
    public void getInstitutionDetails(String city_id, final DownlodableCallback<InstitutionsPojo> callback) {
        createRetrofitService().getInstitutionDetails(city_id).enqueue(new Callback<InstitutionsPojo>() {
            @Override
            public void onResponse(Call<InstitutionsPojo> call, Response<InstitutionsPojo> response) {

                if (response.isSuccessful()) {

                    InstitutionsPojo locationOfficePojo = response.body();

                    Log.d("Result", "register Sucess" + locationOfficePojo.getData());

                    callback.onSuccess(locationOfficePojo);
                }

            }

            @Override
            public void onFailure(Call<InstitutionsPojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });


    }


    @Override
    public void getProfileDetails(String user_id, final DownlodableCallback<ProfilePojo> callback) {

        createRetrofitService().getProfileDetails(user_id).enqueue(new Callback<ProfilePojo>() {
            @Override
            public void onResponse(Call<ProfilePojo> call, Response<ProfilePojo> response) {

                if (response.isSuccessful()) {

                    ProfilePojo profilePojo = response.body();
                    Log.d("Result", "inside p" + response.body().getData().getName());
                    callback.onSuccess(profilePojo);
                }

            }

            @Override
            public void onFailure(Call<ProfilePojo> call, Throwable t) {

                Log.d("Result", "inside pt" + t.getMessage());
                callback.onFailure(t.getMessage());

            }
        });

    }

    @Override
    public void sellProduct(List<MultipartBody.Part> profile_img, RequestBody product_name, RequestBody quantity,
                            RequestBody user_id, RequestBody negotiable, RequestBody price, RequestBody category, RequestBody
                                    total_quantity, RequestBody longitude, RequestBody lat, final DownlodableCallback<MobileRegisterPojo> callback) {
        createRetrofitService().sellProductForPost(profile_img, product_name, quantity, user_id, negotiable, price, category, total_quantity,
                longitude, lat
        ).enqueue(new Callback<MobileRegisterPojo>() {
            @Override
            public void onResponse(Call<MobileRegisterPojo> call, Response<MobileRegisterPojo> response) {

                Request request = response.raw().request();

                Log.d("Result", "sell request" + request);
                Log.d("Result", "sell response" + response);

                if (response.isSuccessful()) {

                    if (response.body().getStatus().contains("0"))

                        callback.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<MobileRegisterPojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());

                callback.onFailure(t.getMessage());

            }
        });
    }

    @Override
    public void getMyUpdates(String user_id, final DownlodableCallback<UpdatePojo> callback) {
        createRetrofitService().getMyUpdates(user_id).enqueue(new Callback<UpdatePojo>() {
            @Override
            public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {


                Request request = response.raw().request();

                Log.d("Result", "resonse" + request);

                if (response.isSuccessful()) {

                    UpdatePojo updatePojo = response.body();
                    callback.onSuccess(updatePojo);

                }

            }

            @Override
            public void onFailure(Call<UpdatePojo> call, Throwable t) {

                Log.d("Result", "onFailure" + t.getMessage());
                callback.onFailure("something went wrong please try again latter");

            }
        });
    }

    @Override
    public void editCustomer(RequestBody name, RequestBody email, RequestBody user_id, MultipartBody.Part profile_img, RequestBody old_profile_img, final DownlodableCallback<MobileRegisterPojo> callback) {
        createRetrofitService().editProfile(name, email, user_id, profile_img, old_profile_img).enqueue(new Callback<MobileRegisterPojo>() {
            @Override
            public void onResponse(Call<MobileRegisterPojo> call, Response<MobileRegisterPojo> response) {
                if (response.isSuccessful()) {
                    Log.d("Result", "sucess" + response.toString());
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<MobileRegisterPojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });

    }


    @Override
    public void postDetails(RequestBody user_id, RequestBody category, RequestBody post_desc, MultipartBody.Part post_img, final DownlodableCallback<MobileRegisterPojo> callback) {
        createRetrofitService().postDiscussion(user_id, category, post_desc, post_img).enqueue(new Callback<MobileRegisterPojo>() {
            @Override
            public void onResponse(Call<MobileRegisterPojo> call, Response<MobileRegisterPojo> response) {

                if (response.isSuccessful()) {

                    Log.d("Result", "response" + response.raw().request().body());
                    callback.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<MobileRegisterPojo> call, Throwable t) {

                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());

            }
        });
    }

    @Override
    public void getBuyListing(String category, final DownlodableCallback<UpdatePojo> callback) {
        createRetrofitService().getBuyList(category).enqueue(new Callback<UpdatePojo>() {
            @Override
            public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {


                Request request = response.raw().request();

                Log.d("Result", "resonse" + request);

                if (response.isSuccessful()) {


                    UpdatePojo updatePojo = response.body();
                    callback.onSuccess(updatePojo);

                }

            }

            @Override
            public void onFailure(Call<UpdatePojo> call, Throwable t) {

                Log.d("Result", "onFailure" + t.getMessage());
                callback.onFailure("something went wrong please try again latter");

            }
        });
    }

    @Override
    public void getProductDetail(String product_id, final DownlodableCallback<ProductDetailPojo> callback) {
        createRetrofitService().getProductDetails(product_id).enqueue(new Callback<ProductDetailPojo>() {
            @Override
            public void onResponse(Call<ProductDetailPojo> call, Response<ProductDetailPojo> response) {


                Request request = response.raw().request();

                Log.d("Result", "resonse" + request);

                if (response.isSuccessful()) {

                    ProductDetailPojo productDetailPojo = response.body();
                    callback.onSuccess(productDetailPojo);
                }
            }

            @Override
            public void onFailure(Call<ProductDetailPojo> call, Throwable t) {

                Log.d("Result", "onFailure" + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });
    }

    @Override
    public void getDiscussiomListing(String page, String category, final DownlodableCallback<DiscussionPojo> callback) {
        createRetrofitService().getDiscussionList(page, category).enqueue(new Callback<DiscussionPojo>() {
            @Override
            public void onResponse(Call<DiscussionPojo> call, Response<DiscussionPojo> response) {
                if (response.isSuccessful()) {

                    Request request = response.raw().request();

                    Log.d("Result", "scucess" + request);
                    Log.d("Result", "" + response.body().toString());
                    DiscussionPojo discussionPojo = response.body();
                    callback.onSuccess(discussionPojo);

                }


            }


            @Override
            public void onFailure(Call<DiscussionPojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());

            }
        });
    }

    @Override
    public void commentOnPost(String user_id, String post_id, String comment, final DownlodableCallback<CommentPojo> callback) {
        createRetrofitService().commentOnPost(user_id, post_id, comment).enqueue(new Callback<CommentPojo>() {
            @Override
            public void onResponse(Call<CommentPojo> call, Response<CommentPojo> response) {
                if (response.isSuccessful()) {

                    Log.d("Result", "onsucess" + response.body().toString());


                    callback.onSuccess(response.body());


                }
            }

            @Override
            public void onFailure(Call<CommentPojo> call, Throwable t) {
                Log.d("Result", "onfailure" + t.toString());
                callback.onFailure(t.getMessage());

            }
        });
    }

    @Override
    public void listOfComments(String post_id, final DownlodableCallback<CommentsPojo> callback) {
        createRetrofitService().listOfComment(post_id).enqueue(new Callback<CommentsPojo>() {
            @Override
            public void onResponse(Call<CommentsPojo> call, Response<CommentsPojo> response) {

                if (response.isSuccessful()) {

                    Log.d("Result", "onsucess" + response.body().toString());


                    callback.onSuccess(response.body());


                }

            }

            @Override
            public void onFailure(Call<CommentsPojo> call, Throwable t) {

                callback.onFailure(t.getMessage());
                Log.d("Result", "onfailure" + t.getMessage());

            }
        });
    }

    @Override
    public void replyOnComment(String user_id, String comment_id, String reply, final DownlodableCallback<MobileRegisterPojo> callback) {
        createRetrofitService().replyOnComment(user_id, comment_id, reply).enqueue(new Callback<MobileRegisterPojo>() {
            @Override
            public void onResponse(Call<MobileRegisterPojo> call, Response<MobileRegisterPojo> response) {
                if (response.isSuccessful()) {

                    Log.d("Result", "sucess" + response.body().toString());


                    callback.onSuccess(response.body());


                }
            }

            @Override
            public void onFailure(Call<MobileRegisterPojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());

            }
        });
    }

    @Override
    public void listOfReplies(String comment_id, final DownlodableCallback<RplyPojo> callback) {

        createRetrofitService().listOfReplies(comment_id).enqueue(new Callback<RplyPojo>() {
            @Override
            public void onResponse(Call<RplyPojo> call, Response<RplyPojo> response) {

                if (response.isSuccessful()) {

//                    Log.d("Rakesss", "sucess" + response.body().getData().getReplies().size());


                    callback.onSuccess(response.body());


                }

            }

            @Override
            public void onFailure(Call<RplyPojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());

            }
        });

    }

    @Override
    public void likeComment(String comment_id, final DownlodableCallback<Likepojo> callback) {
        createRetrofitService().likeComment(comment_id).enqueue(new Callback<Likepojo>() {
            @Override
            public void onResponse(Call<Likepojo> call, Response<Likepojo> response) {
                if (response.isSuccessful()) {

//                    Log.d("Result", "sucess" + response.body().toString() + response.body().getLikeCount());


                    callback.onSuccess(response.body());


                }
            }

            @Override
            public void onFailure(Call<Likepojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());

            }
        });

    }

    @Override
    public void deleteProductItem(String product_id, final DownlodableCallback<MobileRegisterPojo> callback) {
        createRetrofitService().deleteProduct(product_id).enqueue(new Callback<MobileRegisterPojo>() {
            @Override
            public void onResponse(Call<MobileRegisterPojo> call, Response<MobileRegisterPojo> response) {

                if (response.isSuccessful()) {

                    Log.d("Result", "Sucess" + response.body().getMessage());


                    callback.onSuccess(response.body());


                }

            }

            @Override
            public void onFailure(Call<MobileRegisterPojo> call, Throwable t) {

                callback.onFailure(t.getMessage());
                Log.d("Result", "failure" + t.getMessage());

            }
        });
    }

    @Override
    public void deleteDiscussionItem(String post_id, final DownlodableCallback<MobileRegisterPojo> callback) {
        createRetrofitService().deleteDiscussionItem(post_id).enqueue(new Callback<MobileRegisterPojo>() {
            @Override
            public void onResponse(Call<MobileRegisterPojo> call, Response<MobileRegisterPojo> response) {

                if (response.isSuccessful()) {

                    Log.d("Result", "sucess" + response.body().getMessage());


                    callback.onSuccess(response.body());


                }

            }

            @Override
            public void onFailure(Call<MobileRegisterPojo> call, Throwable t) {
                Log.d("Result", "failure" + t.getMessage());
                callback.onFailure(t.getMessage());

            }
        });

    }

    @Override
    public void editPostDiscussion(String post_id, String user_id, String category, String post_desc, final DownlodableCallback<MobileRegisterPojo> callback) {
        createRetrofitService().editPostDiscussion(post_id, user_id, category, post_desc).enqueue(new Callback<MobileRegisterPojo>() {
            @Override
            public void onResponse(Call<MobileRegisterPojo> call, Response<MobileRegisterPojo> response) {

                if (response.isSuccessful()) {

                    Log.d("Result", "sucess" + response.body().getMessage());

                    callback.onSuccess(response.body());
                }

            }

            @Override
            public void onFailure(Call<MobileRegisterPojo> call, Throwable t) {

                callback.onFailure(t.getMessage());
                Log.d("Result", "failure" + t.getMessage());

            }
        });
    }

    @Override
    public void UpdateProductForPost(String product_id, String product_name, String quantity, String user_id,
                                     String negotiable, String price, String category,
                                     String total_quantity, String longitude, String lat, final DownlodableCallback<MobileRegisterPojo> callback) {

        createRetrofitService().UpdateProductForPost(product_id, product_name, quantity, user_id, negotiable, price, category, total_quantity
                , longitude, lat).enqueue(new Callback<MobileRegisterPojo>() {
            @Override
            public void onResponse(Call<MobileRegisterPojo> call, Response<MobileRegisterPojo> response) {

                if (response.isSuccessful()) {

                    Log.d("Result", "Sucess" + response.body().getMessage());

                    callback.onSuccess(response.body());
                }

            }

            @Override
            public void onFailure(Call<MobileRegisterPojo> call, Throwable t) {

                callback.onFailure(t.getMessage());

                Log.d("Result", "failure" + t.getMessage());


            }
        });

    }


    @Override
    public void getPersonalizeDetails(String user_id, String personalize_cat, final DownlodableCallback<PersonalizedFormingPojo> callback) {

        createRetrofitService().getPersonalizeDetails(user_id, personalize_cat).enqueue(new Callback<PersonalizedFormingPojo>() {
            @Override
            public void onResponse(Call<PersonalizedFormingPojo> call, Response<PersonalizedFormingPojo> response) {

                if (response.isSuccessful()) {

                    PersonalizedFormingPojo personalizedFormingPojo = response.body();

                    Log.d("Result", "register Sucess" + personalizedFormingPojo.getStatus());

                    callback.onSuccess(personalizedFormingPojo);
                }

            }

            @Override
            public void onFailure(Call<PersonalizedFormingPojo> call, Throwable t) {

                Log.d("Result", "register failure" + t.getMessage());
                callback.onFailure(t.getMessage());

            }
        });

    }

//    public void cancel() {
//        if (customer != null || !customer.isCanceled()) {
//            customer.cancel();
//        }
//    }
}
