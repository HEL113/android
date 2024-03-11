package com.mk.myapplication;

public class Resident {
/*
* id
* 姓名
* 联系方式
* 密码
* 门牌号
* 性别
* 年龄
* 身份证*/
    private int resident_user_id;
    private String resident_name;
    private String contact_number;
    private String password;
    private int house_number;
    private String gender;
    private int age;
    private String id;

    public Resident() {
    }

    public Resident(String resident_name, String contact_number,
                    String password, int house_number, String gender, int age, String id) {
        this.resident_name = resident_name;
        this.contact_number = contact_number;
        this.password = password;
        this.house_number = house_number;
        this.gender = gender;
        this.age = age;
        this.id = id;
    }

    public int getResident_user_id() {
        return resident_user_id;
    }

    public void setResident_user_id(int resident_user_id) {
        this.resident_user_id = resident_user_id;
    }

    public String getResident_name() {
        return resident_name;
    }

    public void setResident_name(String resident_name) {
        this.resident_name = resident_name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHouse_number() {
        return house_number;
    }

    public void setHouse_number(int house_number) {
        this.house_number = house_number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}