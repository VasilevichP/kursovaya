package org.example.app.entities;

public class Sweets extends Chocolate {
    public String enterPrice() {
        if (getMeasure() == Measures.gram)
            return "Введите цену за 100 " + getMeasure().getText() + " конфет (в рублях)";
        else return "Введите цену за 1 " + getMeasure().getText() + " конфет (в рублях)";
    }

    public Sweets(String name, int calories, double price, Measures measures,String image) {
        super(Types.sweets, name, calories, price, measures,image);
    }

    public Sweets() {
        super(Types.sweets);
        name = "";
        calories = 0;
        measure = Measures.gram;
        price = 0;
    }

    public Sweets(Sweets c) {

        this(c.getName(), c.getCalories(), c.getPrice(), c.getMeasure(),c.getImage());
        this.article = c.getArticle();
    }

    public Chocolate clone() {
        return new Sweets(this);
    }

    public String showChocolates() {
        if (getMeasure() == Measures.gram)
            return "Тип: конфеты ; название: " + getName() + " ; " +
                    "\n\t\t каллорийность: " + getCalories() +
                    " ккал ; цена за 100 " + getMeasure().getText() + " (в рублях): " + getPrice() + " ;";
        return "Тип: конфеты ; название: " + getName() + " ; " +
                "\n\t\t каллорийность: " + getCalories() +
                " ккал ; цена за 1 " + getMeasure().getText() + " (в рублях): " + getPrice() + " ;";
    }
}
