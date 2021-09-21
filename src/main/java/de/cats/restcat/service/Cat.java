package de.cats.restcat.service;

import java.time.LocalDate;


public class Cat {
    private Integer id;

    private String name;

    private Integer age;
    private LocalDate vaccineDate;
    private Float weight;
    private Boolean chubby;
    private Boolean sweet;
    public Cat() {}

    public Cat(int id, String name, int age, LocalDate vaccineDate, float weight, boolean chubby, boolean sweet) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.vaccineDate = vaccineDate;
        this.weight = weight;
        this.chubby = chubby;
        this.sweet = sweet;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"name\":" + name +
                ",\"age\":" + age +
                ",\"vaccineDate\":" + vaccineDate +
                ",\"weight\":" + weight +
                ",\"chubby\":" + chubby +
                ",\"sweet\"=" + sweet +
                "}";
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getAge() {
        return age;
    }

    public LocalDate getVaccineDate() {
        return vaccineDate;
    }

    public Float getWeight() {
        return weight;
    }

    public Boolean isChubby() {
        return chubby;
    }

    public Boolean isSweet() {
        return sweet;
    }

    public String getChubby() {
        return chubby ? "true" : "false";
    }

    public String getSweet() {
        return sweet ? "true" : "false";
    }

    public void setName(String name) {
        this.name = name;
    }

}