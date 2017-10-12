package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MyUpdatesDataPojo implements Serializable {

    @SerializedName("product_id")
    private String product_id;
    @SerializedName("product_name")
    private String product_name;
    @SerializedName("product_image")
    private String product_image;
    @SerializedName("quantity")
    private String quantity;
    @SerializedName("price_per_kg")
    private String price_per_kg;
    @SerializedName("total_quantity")
    private String total_quantity;
    @SerializedName("category")
    private String category;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice_per_kg() {
        return price_per_kg;
    }

    public void setPrice_per_kg(String price_per_kg) {
        this.price_per_kg = price_per_kg;
    }

    public String getTotal_quantity() {
        return total_quantity;
    }

    public void setTotal_quantity(String total_quantity) {
        this.total_quantity = total_quantity;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
