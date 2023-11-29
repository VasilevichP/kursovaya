package org.example.app.entities;

public class ChocolatePaste extends Chocolate {

    public ChocolatePaste() {
        super(Types.paste);
        name = "";
        calories = 0;
        measure = Measures.gram;
        price = 0;
    }

    public ChocolatePaste(String name, int calories, double price, Measures measure,String image) {
        super(Types.paste, name, calories, price, measure,image);
    }

    public ChocolatePaste(ChocolatePaste c) {
        this(c.getName(), c.getCalories(), c.getPrice(), c.getMeasure(),c.getImage());
        this.article = c.getArticle();
    }

    public Chocolate clone() {
        return new ChocolatePaste(this);
    }

    public String showChocolates() {
        if (measure == Measures.gram)
            return "Тип: шоколадная паста ; название: " + getName() + " ; " +
                    "\n\t\t каллорийность: " + getCalories() + " ккал ; цена за 100г (в рублях): " + getPrice() + " ;";
        else
            return "Тип: шоколадная паста ; название: " + getName() + " ; " +
                    "\n\t\t каллорийность: " + getCalories() + " ккал ; цена за 1 " + measure.getText() + ": " + getPrice() + " ;";
    }
}
