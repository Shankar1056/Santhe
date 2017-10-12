package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class RepliesItem {

    @SerializedName("reply_id")
    private String replyId;

    @SerializedName("replied_on")
    private String repliedOn;

    @SerializedName("r_profile_pic")
    private String rProfilePic;

    @SerializedName("reply")
    private String reply;

    @SerializedName("replied_by")
    private String repliedBy;

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getRepliedOn() {
        return repliedOn;
    }

    public void setRepliedOn(String repliedOn) {
        this.repliedOn = repliedOn;
    }

    public String getRProfilePic() {
        return rProfilePic;
    }

    public void setRProfilePic(String rProfilePic) {
        this.rProfilePic = rProfilePic;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getRepliedBy() {
        return repliedBy;
    }

    public void setRepliedBy(String repliedBy) {
        this.repliedBy = repliedBy;
    }

    @Override
    public String toString() {
        return
                "RepliesItem{" +
                        "reply_id = '" + replyId + '\'' +
                        ",replied_on = '" + repliedOn + '\'' +
                        ",r_profile_pic = '" + rProfilePic + '\'' +
                        ",reply = '" + reply + '\'' +
                        ",replied_by = '" + repliedBy + '\'' +
                        "}";
    }
}