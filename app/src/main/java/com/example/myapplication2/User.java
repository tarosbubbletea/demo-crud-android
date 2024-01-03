package com.example.myapplication2;

public class User {
    private String email;
    private String password;
    private Integer usertype_id;

    // constructor
    public User(String email, String password, Integer userType){
        this.email = email;
        this.password = password;
        this.usertype_id = userType;
    }

    // getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserType() {
        return usertype_id;
    }

    public void setUserType(Integer usertype_id) {
        this.usertype_id = usertype_id;
    }
}
