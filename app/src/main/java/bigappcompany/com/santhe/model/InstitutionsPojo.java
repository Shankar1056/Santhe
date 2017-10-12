package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class InstitutionsPojo {

    @SerializedName("data")
    private List<InstitutionDataItem> data;

    @SerializedName("status")
    private String status;

    public List<InstitutionDataItem> getData() {
        return data;
    }

    public void setData(List<InstitutionDataItem> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return
                "InstitutionsPojo{" +
                        "data = '" + data + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}