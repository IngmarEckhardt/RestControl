package de.cats.restcat.controller;

import de.cats.restcat.service.Cat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CatDTO {
    private Integer id = 0;
    private String name = "MaxMusterkatz";
    private Integer age = 15;
    private Date date;
    private Float weight = 15f;
    private String chubby = "true";
    private String sweet = "true";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date vaccineDate) {
        this.date = vaccineDate;
    }

    public Date getRealDate() {
        return date;
    }

    public void setRealDate(Date realDate) {
        this.date = realDate;
    }


    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getChubby() {
        return chubby;
    }

    public void setChubby(String chubby) {
        this.chubby = chubby;
    }

    public String getSweet() {
        return sweet;
    }

    public void setSweet(String sweet) {
        this.sweet = sweet;
    }

    public Cat getCat (){

        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Boolean chubbyBoolean = chubby.equals("true");
        Boolean sweetBoolean = sweet.equals("true");

        return new Cat(id, name, age, localDate, weight, chubbyBoolean, sweetBoolean);
    }
}