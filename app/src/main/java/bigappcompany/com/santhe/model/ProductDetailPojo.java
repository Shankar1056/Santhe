package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ProductDetailPojo {

    @SerializedName("data")
    private ProduuctDetailDataPojo data;
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ProduuctDetailDataPojo getData() {
        return data;
    }

    public void setData(ProduuctDetailDataPojo data) {
        this.data = data;
    }


}