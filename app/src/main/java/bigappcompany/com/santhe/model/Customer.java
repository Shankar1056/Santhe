package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

public class Customer {

    @SerializedName("id")
    private Long id;

    @SerializedName("email")
    private String email;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("phone")
    private String phone;

}