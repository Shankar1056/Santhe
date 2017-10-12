package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationOfficePojo {

    @SerializedName("data")
    private List<LocationOfficeDetails> data;
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LocationOfficeDetails> getData() {
        return data;
    }

    public void setData(List<LocationOfficeDetails> data) {
        this.data = data;
    }


}