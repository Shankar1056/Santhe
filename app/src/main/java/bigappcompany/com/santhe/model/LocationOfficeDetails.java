package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pushpanjali on 9/9/2017.
 */

public class LocationOfficeDetails {


    @SerializedName("loc_id")
    private String loc_id;
    @SerializedName("office_loc")
    private String office_loc;
    @SerializedName("office_address")
    private String office_address;
    @SerializedName("office_name")
    private String office_name;
    @SerializedName("office_phone")
    private String office_phone;

    public String getLoc_id() {
        return loc_id;
    }

    public void setLoc_id(String loc_id) {
        this.loc_id = loc_id;
    }

    public String getOffice_loc() {
        return office_loc;
    }

    public void setOffice_loc(String office_loc) {
        this.office_loc = office_loc;
    }

    public String getOffice_address() {
        return office_address;
    }

    public void setOffice_address(String office_address) {
        this.office_address = office_address;
    }

    public String getOffice_name() {
        return office_name;
    }

    public void setOffice_name(String office_name) {
        this.office_name = office_name;
    }

    public String getOffice_phone() {
        return office_phone;
    }

    public void setOffice_phone(String office_phone) {
        this.office_phone = office_phone;
    }


}
