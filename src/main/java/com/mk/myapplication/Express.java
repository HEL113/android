package com.mk.myapplication;
//快递信息
public class Express {
    private String name;
    private String contact_number;
    private String exname;
    private String token;
    public Express() {

    }

    public Express(String name, String contact_number, String exname) {
        this.name = name;
        this.contact_number = contact_number;
        this.exname = exname;
    }



    @Override
    public String toString() {
        return "Express{" +
                "name='" + name + '\'' +
                ", contact_number='" + contact_number + '\'' +
                ", exname='" + exname + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getExname() {
        return exname;
    }

    public void setExname(String exname) {
        this.exname = exname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
