package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class InstitutionDataItem {

    @SerializedName("city_name")
    private String cityName;

    @SerializedName("college_loc")
    private String collegeLoc;

    @SerializedName("college_name")
    private String collegeName;

    @SerializedName("college_address")
    private String collegeAddress;

    @SerializedName("college_phone")
    private String collegePhone;

    @SerializedName("university_name")
    private String universityName;

    @SerializedName("loc_id")
    private String locId;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCollegeLoc() {
        return collegeLoc;
    }

    public void setCollegeLoc(String collegeLoc) {
        this.collegeLoc = collegeLoc;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeAddress() {
        return collegeAddress;
    }

    public void setCollegeAddress(String collegeAddress) {
        this.collegeAddress = collegeAddress;
    }

    public String getCollegePhone() {
        return collegePhone;
    }

    public void setCollegePhone(String collegePhone) {
        this.collegePhone = collegePhone;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    @Override
    public String toString() {
        return
                "InstitutionDataItem{" +
                        "city_name = '" + cityName + '\'' +
                        ",college_loc = '" + collegeLoc + '\'' +
                        ",college_name = '" + collegeName + '\'' +
                        ",college_address = '" + collegeAddress + '\'' +
                        ",college_phone = '" + collegePhone + '\'' +
                        ",university_name = '" + universityName + '\'' +
                        ",loc_id = '" + locId + '\'' +
                        "}";
    }
}