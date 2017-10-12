package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class CommentsDataPojo {

    @SerializedName("posted_on")
    private String postedOn;
    @SerializedName("post_image")
    private String postImage;
    @SerializedName("comments")
    private List<CommentsItem> comments;
    @SerializedName("post_id")
    private String postId;
    @SerializedName("description")
    private String description;
    @SerializedName("posted_date")
    private String postedDate;
    //
    @SerializedName("profile_img")
    private String profile_img;
    //
    @SerializedName("posted_by")
    private String posted_by;

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    //
    public String getPosted_by() {
        return posted_by;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    //
    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
//





    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public List<CommentsItem> getComments() {
        return comments;
    }

    public void setComments(List<CommentsItem> comments) {
        this.comments = comments;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

}