package me.lovell.shadownetwork;

public class Contact {
    private String name;
    private String phone;
    private String email;
    private String uid;

    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public Contact(String name, String phone, String email, String uid) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.uid = uid;
    }

    public Contact(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    @Override
    public String toString() {

        String toreturn = "Name: " + this.name + " Email: " + this.email + " From " + this.uid + " || ";

        return toreturn;
    }
}
