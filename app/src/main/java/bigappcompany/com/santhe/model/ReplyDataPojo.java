package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ReplyDataPojo {

    @SerializedName("commented_by")
    private String commentedBy;

    @SerializedName("replies")
    private List<RepliesItem> replies;

    @SerializedName("commented_on")
    private String commentedOn;

    @SerializedName("comment")
    private String comment;

    @SerializedName("user_profile")
    private String userProfile;

    @SerializedName("comment_id")
    private String commentId;

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public List<RepliesItem> getReplies() {
        return replies;
    }

    public void setReplies(List<RepliesItem> replies) {
        this.replies = replies;
    }

    public String getCommentedOn() {
        return commentedOn;
    }

    public void setCommentedOn(String commentedOn) {
        this.commentedOn = commentedOn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

}