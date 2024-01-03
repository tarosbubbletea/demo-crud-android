package com.example.myapplication2;

public class Persona {
    private int user_id;
    private String name;
    private String rut;
    private int campus_id;
    private int career_id;
    private int semester;
    private int year;

    // constructor
    public Persona(int user_id, String name, String rut, int campus_id, int career_id, int semester, int year){
        this.user_id = user_id;
        this.name = name;
        this.rut = rut;
        this.campus_id = campus_id;
        this.career_id = career_id;
        this.semester = semester;
        this.year = year;
    }

    // getters and setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public int getCampus_id() {
        return campus_id;
    }

    public void setCampus_id(int campus_id) {
        this.campus_id = campus_id;
    }

    public int getCareer_id() {
        return career_id;
    }

    public void setCareer_id(int career_id) {
        this.career_id = career_id;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
