package com.example.myapplication2;

import java.io.Serializable;

public class User2 implements Serializable {
    private Integer id;
    private String email;
    private String password;
    private Integer usertype_id;

    // constructor
    public User2(Integer id, String email, String password, Integer usertype_id){
        this.id = id;
        this.email = email;
        this.password = password;
        this.usertype_id = usertype_id;
    }

    // getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
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
