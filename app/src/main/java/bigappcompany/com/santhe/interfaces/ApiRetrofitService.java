package bigappcompany.com.santhe.interfaces;

import java.util.List;

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
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiRetrofitService {

    @POST("generate_otp")
    @FormUrlEncoded
    Call<GenerateOtpPojo> getMobileRegistrationApi(@Field("mobile_no") String mobile_no);

    @POST("verify_otp")
    @FormUrlEncoded
    Call<RegistrationPojo> getOtpVerify(@Field("mobile_no") String mobile_no, @Field("otp") String otp);

    @Multipart
    @POST("user_registration")
    Call<RegistrationPojo> registerCustomer(@Part("name") RequestBody name, @Part("email") RequestBody email,
                                            @Part("user_id") RequestBody user_id, @Part MultipartBody.Part profile_img);

    @GET("categories")
    Call<PersonalisePojo> getCategoryDetails();


    @GET("ufi_categories")
    Call<PersonalisePojo> getUfiCategoryDetails();

    @Multipart
    @POST("/post_product_for_selling")
    Call<ResponseBody> multipleImage(@Part List<MultipartBody.Part> profile_img);
    
    @POST("cities")
    @FormUrlEncoded
    Call<CitiesPojo> getCitiesDetails(@Field("type") String type , @Field("category") String category);

    @POST("get_locations")
    @FormUrlEncoded
    Call<LocationOfficePojo> getLocationDetails(@Field("city_id") String city_id, @Field("category") String category);

    @POST("institute_listing")
    @FormUrlEncoded
    Call<InstitutionsPojo> getInstitutionDetails(@Field("city_id") String city_id);

    @POST("user_profile")
    @FormUrlEncoded
    Call<ProfilePojo> getProfileDetails(@Field("user_id") String user_id);

    @Multipart
    @POST("change_profile_picture")
    Call<MobileRegisterPojo> editProfile(@Part("name") RequestBody name, @Part("email") RequestBody email,
                                         @Part("user_id") RequestBody user_id, @Part MultipartBody.Part profile_img, @Part("old_profile_img") RequestBody old_profile_img);

    //done
    @POST("personalize_in_forming")
    @FormUrlEncoded
    Call<PersonalizedFormingPojo> getPersonalizeDetails(@Field("user_id") String user_id, @Field("personalize_cat") String personalize_cat);


    @Multipart
    @POST("post_product_for_selling")
    Call<MobileRegisterPojo> sellProductForPost(@Part List<MultipartBody.Part> profile_img,
                                                @Part("product_name") RequestBody product_name,
                                                @Part("quantity") RequestBody quantity,
                                                @Part("user_id") RequestBody user_id,
                                                @Part("negotiable") RequestBody negotiable,
                                                @Part("price") RequestBody price,
                                                @Part("category") RequestBody category,
                                                @Part("total_quantity") RequestBody total_quantity,
                                                @Part("longitude") RequestBody longitude,
                                                @Part("lat") RequestBody lat
    );


    @POST("my_updates")
    @FormUrlEncoded
    Call<UpdatePojo> getMyUpdates(@Field("user_id") String user_id);

    @Multipart
    @POST("post_discussion")
    Call<MobileRegisterPojo> postDiscussion(@Part("user_id") RequestBody user_id, @Part("category") RequestBody category,
                                            @Part("post_desc") RequestBody post_desc, @Part MultipartBody.Part post_img);


    @POST("product_list")
    @FormUrlEncoded
    Call<UpdatePojo> getBuyList(@Field("category") String category);


    @POST("post_list")
    @FormUrlEncoded
    Call<DiscussionPojo> getDiscussionList(@Field("page_no") String page_no, @Field("category") String category);


    @POST("comments_list")
    @FormUrlEncoded
    Call<CommentsPojo> listOfComment(@Field("post_id") String post_id);


    @POST("comment_on_post")
    @FormUrlEncoded
    Call<CommentPojo> commentOnPost(@Field("user_id") String user_id, @Field("post_id") String post_id,
                                    @Field("comment") String comment);

    @POST("post_reply_on_comment")
    @FormUrlEncoded
    Call<MobileRegisterPojo> replyOnComment(@Field("user_id") String user_id, @Field("comment_id") String comment_id,
                                            @Field("reply") String reply);


    @POST("reply_list")
    @FormUrlEncoded
    Call<RplyPojo> listOfReplies(@Field("comment_id") String comment_id);


    @POST("like_comment")
    @FormUrlEncoded
    Call<Likepojo> likeComment(@Field("comment_id") String comment_id);


    @POST("product_details")
    @FormUrlEncoded
    Call<ProductDetailPojo> getProductDetails(@Field("product_id") String product_id);


    @POST("delete_product")
    @FormUrlEncoded
    Call<MobileRegisterPojo> deleteProduct(@Field("product_id") String product_id);


    @POST("delete_post")
    @FormUrlEncoded
    Call<MobileRegisterPojo> deleteDiscussionItem(@Field("post_id") String post_id);


    // doingggg

    @POST("edit_post_discussion")
    @FormUrlEncoded
    Call<MobileRegisterPojo> editPostDiscussion(@Field("post_id") String post_id, @Field("user_id") String user_id,
                                                @Field("category") String category, @Field("post_desc") String post_desc);

    @POST("product_details_update")
    @FormUrlEncoded
    Call<MobileRegisterPojo> UpdateProductForPost(
            @Field("product_id") String product_id,
            @Field("product_name") String product_name,
            @Field("quantity") String quantity,
            @Field("user_id") String user_id,
            @Field("negotiable") String negotiable,
            @Field("price") String price,
            @Field("category") String category,
            @Field("total_quantity") String total_quantity,
            @Field("longitude") String longitude,
            @Field("lat") String lat
    );



}
