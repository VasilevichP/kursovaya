package org.example.app.entities;

import java.io.Serializable;
import java.util.Scanner;

public class Chocolate implements Serializable {
    private static int nextArticle = 1;
    protected int article;
    protected Types type;
    protected String name;
    protected int calories;
    protected double price;
    protected Measures measure;
    protected String image;

    public String showChocolates() {
        return String.format("Тип: %s, название: %s, калорийность: %d, цена: %f, единицы измерения: %s", type.getText(),
                name, calories, price, measure.getDescription());
    }



    public Chocolate(Types type, String name, int calories, double price, Measures measures,String image) {
        this.type = type;
        this.name = name;
        this.calories = calories;
        this.price = price;
        this.measure = measures;
        this.image=image;
    }

    public Chocolate(Types type) {
        this.type = type;
    }

    public Chocolate() {
        article = 0;
        type = null;
        name = "";
        calories = 0;
        price = 0;
        measure = null;
    }

    public Chocolate(Chocolate c) {
        this(c.getType(), c.getName(), c.getCalories(), c.getPrice(), c.getMeasure(),c.getImage());
        article = c.getArticle();
    }

    public int getArticle() {
        return article;
    }

    public Types getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    public String getImage(){
            return image;
    }
    public int getCalories() {
        return calories;
    }

    public double getPrice() {
        return price;
    }

    public Measures getMeasure() {
        return measure;
    }

    public Nuts getTypeOfNuts() {
        switch (type) {
            case nuts:
                return ((NutsInChocolate) this).getTypeOfNuts();
            default:
                return null;
        }
    }

    public ChocolateKinds getChocolateKind() {
        switch (type) {
            case tile:
                return ((TiledChocolate) this).getChocolateKind();
            case nuts:
                return ((NutsInChocolate) this).getChocolateKind();
            default:
                return null;
        }
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setMeasure(Measures measure) {
        this.measure = measure;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImage(String image){this.image=image;}

    public void setArticle(int article) {
        this.article = article;
    }

    public static Chocolate AddChocolate(Types userType) {
//        Types userType = selectType();
        Chocolate choco = null;
        switch (userType) {
            case tile:
                choco = new TiledChocolate();
                break;
            case sweets:
                choco = new Sweets();
                break;
            case bars:
                choco = new ChocolateBars();
                break;
            case powder:
                choco = new ChocolatePowder();
                break;
            case nuts:
                choco = new NutsInChocolate();
                break;
            case cookies:
                choco = new Cookies();
                break;
            case paste:
                choco = new ChocolatePaste();
                break;
            default:
                return null;
        }
        choco.article = nextArticle++;
        return choco;
    }

    public Chocolate clone() {
        return new Chocolate(this);
    }

    public boolean equals(Chocolate c) {
        if (c == null) return false;
        return (type==null?c.getType()==null:type==c.getType()) && (name==null?c.getName()==null:name.equals(c.getName())) &&
                (calories == c.getCalories()) && (price == c.getPrice()) &&
                (measure ==null? c.getMeasure()==null:measure==c.getMeasure());
    }

}

