package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class CommentsItem {

    @SerializedName("commented_by")
    private String commentedBy;

    @SerializedName("like_count")
    private String likeCount;

    @SerializedName("profile_pic")
    private String profilePic;

    @SerializedName("commented_on")
    private String commentedOn;

    @SerializedName("comment")
    private String comment;

    @SerializedName("commented_date")
    private String commentedDate;

    @SerializedName("comment_id")
    private String commentId;
    @SerializedName("reply_cnt")
    private String replyCnt;

    public String getReplyCnt() {
        return replyCnt;
    }

    public void setReplyCnt(String replyCnt) {
        this.replyCnt = replyCnt;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    public String getCommentedDate() {
        return commentedDate;
    }

    public void setCommentedDate(String commentedDate) {
        this.commentedDate = commentedDate;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }


}