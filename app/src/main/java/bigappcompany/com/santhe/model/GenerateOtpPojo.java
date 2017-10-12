package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pushpanjali on 9/4/2017.
 */

public class GenerateOtpPojo {

    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private DataGenerateOtp data;
    
    public String getOtp() {
        return otp;
    }
    
    public void setOtp(String otp) {
        this.otp = otp;
    }
    
    @SerializedName("otp")
    private String otp;

    public DataGenerateOtp getData() {
        return data;
    }

    public void setData(DataGenerateOtp data) {
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
