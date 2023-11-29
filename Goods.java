package org.example.app.entities;

import java.io.Serializable;

public class Goods implements Serializable {
    private Chocolate item;
    private double quantity;
    private double value;
    private boolean delStatus;

    public Goods(Chocolate item) {
        this.item = item;
        setQuantity(1);
    }
    public Goods(Chocolate item, int quantity){
        this(item, (double) quantity);
    }
    public Goods(Chocolate item, double quantity){
        this.item = item;
        setQuantity(quantity);
        this.delStatus=false;
    }

    public void setItem(Chocolate item) {
        this.item = item;
        calculate();
    }

    public Chocolate getItem() {
        return item;
    }


    public void setQuantity(int quantity) {
        setQuantity( (double) quantity);
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        calculate();
    }

    public double getQuantity() {
        return quantity;
    }

    public double getValue() {
        return value;
    }

    public String showItem(){
        if (item != null){
            if (item.getMeasure() == Measures.kilo)
                return item.showChocolates() + " количество: " + getQuantity();
            else
                return item.showChocolates() + " количество: " + (int) getQuantity();
        } else
            return "Товар не выбран";
    }

    public double calculate(){
        double sum = 0;
        if (item.measure == Measures.gram){
            sum= item.getPrice() * quantity / 100;
        } else
            sum= value = item.getPrice() * quantity;
        return Math.round(sum * 100.0) / 100.0;
    }

    public Goods clone(){
        return new Goods(getItem().clone(), getQuantity());
    }
    public boolean equals(Goods g){
        if(g==null) return false;
        return (getItem()==null ? g.getItem() == null : getItem().equals(g.getItem())) && (getQuantity() == g.getQuantity());
    }
}
