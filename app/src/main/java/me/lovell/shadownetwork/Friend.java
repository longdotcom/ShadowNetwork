package me.lovell.shadownetwork;

public class Friend {
    public String date;
    private String name;
    private String created;

    public Friend(){}

    public Friend(String date){
        this.date = date;
    }

    public Friend(String name, String created){this.name=name;this.created=created;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
