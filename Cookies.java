package org.example.app.entities;

public class Cookies extends Chocolate {
    public Cookies() {
        super(Types.cookies);
        name = "";
        calories = 0;
        measure = Measures.gram;
        price = 0;
    }

    public Cookies(String name, int calories, double price, Measures measure,String image) {
        super(Types.cookies, name, calories, price, measure,image);
    }

    public Cookies(Cookies c) {
        this(c.getName(), c.getCalories(), c.getPrice(), c.getMeasure(), c.getImage());
        this.article = c.getArticle();
    }

    public Chocolate clone() {
        return new Cookies(this);
    }

    public String showChocolates() {
        if (getMeasure() == Measures.gram)
            return "Тип: печенье ; название: " + getName() + " ; " +
                    "\n\t\t каллорийность: " + getCalories() + " ккал ; цена за 100г (в рублях): " + getPrice() + " ;";
        return "Тип: печенье ; название: " + getName() + " ; " +
                "\n\t\t каллорийность: " + getCalories() + " ккал ; цена за 1 " + getMeasure() + " (в рублях): " + getPrice() + " ;";
    }
}
