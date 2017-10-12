package bigappcompany.com.santhe.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ProfilePojo {

    @SerializedName("data")
    private Data data;

    @SerializedName("status")
    private String status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Data {

        @SerializedName("user_status")
        private String userStatus;

        @SerializedName("profile_image")
        private String profileImage;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("user_is_active")
        private String userIsActive;

        @SerializedName("name")
        private String name;

        @SerializedName("mobile")
        private String mobile;

        @SerializedName("email")
        private String email;

        @SerializedName("user_categories")
        private String user_categories;

        @SerializedName("image_name")
        private String image_name;

        public String getImage_name() {
            return image_name;
        }

        public void setImage_name(String image_name) {
            this.image_name = image_name;
        }

        public String getUser_categories() {
            return user_categories;
        }

        public void setUser_categories(String user_categories) {
            this.user_categories = user_categories;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserIsActive() {
            return userIsActive;
        }

        public void setUserIsActive(String userIsActive) {
            this.userIsActive = userIsActive;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}