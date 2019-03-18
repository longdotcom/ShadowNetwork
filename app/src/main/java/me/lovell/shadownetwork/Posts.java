package me.lovell.shadownetwork;


public class Posts {
    public String uid;
    public String time;
    public String date;
    public String postimage;
    public String description;
    public String profileimage;
    public String fullname;

    public Posts(String usrUid, String crntTime, String crntDate, String crntPostimage, String crntDesc, String crntProfileimg, String usrName) {
        this.uid = usrUid;
        this.time = crntTime;
        this.date = crntDate;
        this.postimage = crntPostimage;
        this.description = crntDesc;
        this.profileimage = crntProfileimg;
        this.fullname = usrName;
    }

    public Posts(String usrUid2, String crntTime2, String crntDate2) {
        this.uid = usrUid2;
        this.time = crntTime2;
        this.date = crntDate2;
    }

    public Posts(){}
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String crnttime) {
        this.time = crnttime;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String crntdate) {
        this.date = crntdate;
    }
    public String getPostimage() {
        return postimage;
    }
    public void setPostimage(String crntpostimage) {
        this.postimage = crntpostimage;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String crntdesc) {
        this.description = crntdesc;
    }
    public String getProfileimage() {
        return profileimage;
    }
    public void setProfileimage(String crntprofileimg) {
        this.profileimage = crntprofileimg;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String usrfullname) {
        this.fullname = usrfullname;
    }
}
