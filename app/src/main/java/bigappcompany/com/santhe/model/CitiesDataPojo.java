package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CitiesDataPojo implements Serializable {

    @SerializedName("city_id")
    private String city_id;
    @SerializedName("city_name")
    private String city_name;
    @SerializedName("city_status")
    private String city_status;
    @SerializedName("city_created_on")
    private String city_created_on;
    
    public String getLocation_status() {
        return location_status;
    }
    
    public void setLocation_status(String location_status) {
        this.location_status = location_status;
    }
    
    @SerializedName("location_status")
    private String location_status;

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCity_status() {
        return city_status;
    }

    public void setCity_status(String city_status) {
        this.city_status = city_status;
    }

    public String getCity_created_on() {
        return city_created_on;
    }

    public void setCity_created_on(String city_created_on) {
        this.city_created_on = city_created_on;
    }


}
