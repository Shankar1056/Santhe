package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pushpa on 7/8/17.
 */

public class DiscussionPojo {

    @SerializedName("data")
    private List<DiscussionDataIPojo> data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

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

    public List<DiscussionDataIPojo> getData() {
        return data;
    }

    public void setData(List<DiscussionDataIPojo> data) {
        this.data = data;
    }

}
