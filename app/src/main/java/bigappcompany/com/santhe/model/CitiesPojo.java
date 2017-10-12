package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CitiesPojo {

    @SerializedName("data")
    private List<CitiesDataPojo> data;

    @SerializedName("status")
    private String status;

    public List<CitiesDataPojo> getData() {
        return data;
    }

    public void setData(List<CitiesDataPojo> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}