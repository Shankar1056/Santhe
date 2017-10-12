package bigappcompany.com.santhe.network;

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


public interface ServiceMethods {

    void getMobileRegister(String mobileNo, DownlodableCallback<GenerateOtpPojo> callback);


    void getOtpVerify(String mobileNo, String otpNumber, DownlodableCallback<RegistrationPojo> callback);

    void registerCustomer(RequestBody name, RequestBody email, RequestBody user_id, MultipartBody.Part profile_img,
                          DownlodableCallback<RegistrationPojo> callback);

    void MultipleImage(List<MultipartBody.Part> profile_img,
                       DownlodableCallback<ResponseBody> callback);

    void getCategories(DownlodableCallback<PersonalisePojo> callback);

    void getUfiCategories(DownlodableCallback<PersonalisePojo> callback);


    void getPersonalizeDetails(String user_id, String personalize_cat, DownlodableCallback<PersonalizedFormingPojo> callback);

    void getCitiesDetail(String dealer, String category, DownlodableCallback<CitiesPojo> callback);

    void getLocationDetails(String city_id, String category, DownlodableCallback<LocationOfficePojo> callback);

    void getProfileDetails(String user_id, DownlodableCallback<ProfilePojo> callback);

    void sellProduct(List<MultipartBody.Part> profile_img,
                     RequestBody product_name,
                     RequestBody quantity,
                     RequestBody user_id,
                     RequestBody negotiable,
                     RequestBody price,
                     RequestBody category,
                     RequestBody total_quantity,
                     RequestBody longitude,
                     RequestBody lat, DownlodableCallback<MobileRegisterPojo> callback);

    void getMyUpdates(String user_id, DownlodableCallback<UpdatePojo> callback);


    void editCustomer(RequestBody name, RequestBody email, RequestBody user_id, MultipartBody.Part profile_img, RequestBody old_profile_img,
                      DownlodableCallback<MobileRegisterPojo> callback);

    void postDetails(RequestBody user_id, RequestBody category,
                     RequestBody post_desc, MultipartBody.Part post_img, DownlodableCallback<MobileRegisterPojo> callback);

    void getBuyListing(String category, DownlodableCallback<UpdatePojo> callback);

    void getProductDetail(String product_id, DownlodableCallback<ProductDetailPojo> callback);

    void getDiscussiomListing(String page, String category, DownlodableCallback<DiscussionPojo> callback);

    void commentOnPost(String user_id, String post_id, String comment, DownlodableCallback<CommentPojo> callback);

    void listOfComments(String post_id, DownlodableCallback<CommentsPojo> callback);

    void replyOnComment(String user_id, String comment_id, String reply, DownlodableCallback<MobileRegisterPojo> callback);

    void listOfReplies(String comment_id, DownlodableCallback<RplyPojo> callback);

    void likeComment(String comment_id, DownlodableCallback<Likepojo> callback);

    void deleteProductItem(String product_id, DownlodableCallback<MobileRegisterPojo> callback);

    void deleteDiscussionItem(String post_id, DownlodableCallback<MobileRegisterPojo> callback);

    void editPostDiscussion(String post_id, String user_id,
                            String category, String post_desc, DownlodableCallback<MobileRegisterPojo> callback);


    void UpdateProductForPost(String product_id,
                              String product_name,
                              String quantity,
                              String user_id,
                              String negotiable,
                              String price,
                              String category,
                              String total_quantity,
                              String longitude,
                              String lat, DownlodableCallback<MobileRegisterPojo> callback
    );

    void getInstitutionDetails(String city_id, DownlodableCallback<InstitutionsPojo> callback);


}
