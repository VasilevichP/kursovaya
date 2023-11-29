package org.example.app.entities;

public class ChocolateBars extends Chocolate {

    public ChocolateBars(String name, int calories, double price,String image) {
        super(Types.bars, name, calories, price, Measures.piece,image);
    }

    public ChocolateBars() {
        super(Types.bars);
        name = "";
        calories = 0;
        measure = Measures.piece;
        price = 0;
    }

    public ChocolateBars(ChocolateBars c) {
        this(c.getName(), c.getCalories(), c.getPrice(),c.getImage());
        this.article = c.getArticle();
    }

    public Chocolate clone() {
        return new ChocolateBars(this);
    }

    public String showChocolates() {
        return "Тип: шоколадный батончик ; название: " + getName() + " ; " +
                "\n\t\t каллорийность: " + getCalories() + " ккал ; цена за штуку(в рублях): " + getPrice() + " ; ";
    }
}
