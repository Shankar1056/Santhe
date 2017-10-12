package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

public class Likepojo {

    @SerializedName("like_count")
    private String likeCount;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String status;

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
