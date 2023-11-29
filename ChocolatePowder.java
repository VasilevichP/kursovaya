package org.example.app.entities;

public class ChocolatePowder extends Chocolate {


    public ChocolatePowder() {
        super(Types.powder);
        name = "";
        calories = 0;
        measure = Measures.gram;
        price = 0;
    }

    public ChocolatePowder(ChocolatePowder c) {
        this(c.getName(), c.getCalories(), c.getPrice(),c.getImage());
        this.article = c.getArticle();
    }

    public ChocolatePowder(String name, int calories, double price,String image) {
        super(Types.powder, name, calories, price, Measures.gram,image);
    }

    public Chocolate clone() {
        return new ChocolatePowder(this);
    }

    public String showChocolates() {
        return "Тип: шоколадный порошок ; название: " + getName() + " ; " +
                "\n\t\t каллорийность: " + getCalories() + " ккал ; цена за 100г (в рублях): " + getPrice() + " ; ";
    }
}
