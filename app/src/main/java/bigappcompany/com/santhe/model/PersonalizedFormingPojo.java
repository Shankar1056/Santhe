package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pushpanjali on 9/4/2017.
 */

public class PersonalizedFormingPojo {

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

}
