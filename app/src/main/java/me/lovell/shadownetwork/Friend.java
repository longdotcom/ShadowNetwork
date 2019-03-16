package me.lovell.shadownetwork;

public class Friend {
    public String date;
    private String name;
    private String created;

    public Friend(){}

    public Friend(String date){
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
