package me.lovell.shadownetwork;

public class FoundFriends {

    public String profile_image;
    public String full_name;
    public String profileStatus;

    public FoundFriends(String profile_image, String full_name, String profileStatus) {
        this.profile_image = profile_image;
        this.full_name = full_name;
        this.profileStatus = profileStatus;
    }
    public FoundFriends(String profile_image) {
        this.profile_image = profile_image;
    }
    public FoundFriends(){}
    public String getProfile_image() {
        return profile_image;
    }
    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
    public String getFull_name() {
        return full_name;
    }
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
    public String getProfileStatus() {
        return profileStatus;
    }
    public void setProfileStatus(String profileStatus) {
        this.profileStatus = profileStatus;
    }
}
