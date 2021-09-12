package de.cats.restcat.controller;

import de.cats.restcat.service.Cat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CatDTO {
    private String id;
    private String name;
    private String age;
    private String date;
    private String weight;
    private String chubby;
    private String sweet;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String vaccineDate) {
        this.date = vaccineDate;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
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
        Integer idInt = Integer.parseInt(id);
        Integer ageInt = Integer.parseInt(age);
        Date dateSimpleDate;
        try {
            dateSimpleDate = new SimpleDateFormat("yyyy-MM-dd").
                    parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        LocalDate vaccineDate = dateSimpleDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Float weightFloat = Float.parseFloat(weight);
        Boolean chubbyBoolean = chubby.equals("true");
        Boolean sweetBoolean = sweet.equals("true");

        return new Cat(idInt, name, ageInt, vaccineDate, weightFloat, chubbyBoolean, sweetBoolean);
    }
}