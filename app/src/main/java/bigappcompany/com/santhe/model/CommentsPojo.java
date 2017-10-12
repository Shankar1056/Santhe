package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class CommentsPojo {

    @SerializedName("data")
    private CommentsDataPojo data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public CommentsDataPojo getData() {
        return data;
    }

    public void setData(CommentsDataPojo data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}