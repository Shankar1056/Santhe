package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Pushpanjali on 9/4/2017.
 */

public class PersonalisePojo {

    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private ArrayList<DataCategory> data;

    public ArrayList<DataCategory> getData() {
        return data;
    }

    public void setData(ArrayList<DataCategory> data) {
        this.data = data;
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
