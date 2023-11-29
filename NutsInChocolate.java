package org.example.app.entities;

public class NutsInChocolate extends Chocolate {

    protected Nuts typeOfNuts;
    protected ChocolateKinds chocolateKind;

    public ChocolateKinds getChocolateKind() {
        return chocolateKind;
    }

    public Nuts getTypeOfNuts() {
        return typeOfNuts;
    }

    public void setChocolateKind(ChocolateKinds chocolateKind) {
        this.chocolateKind = chocolateKind;
    }

    public void setTypeOfNuts(Nuts typeOfNuts) {
        this.typeOfNuts = typeOfNuts;
    }

    public NutsInChocolate() {
        super(Types.nuts);
        measure = Measures.kilo;
        name = "";
        calories = 0;
        price = 0;
        chocolateKind = null;
        typeOfNuts = null;
    }

    public NutsInChocolate(String name, int calories, double price, ChocolateKinds ck, Nuts nuts,String image) {
        super(Types.nuts, name, calories, price, Measures.kilo,image);
        this.chocolateKind = ck;
        this.typeOfNuts = nuts;
    }

    public NutsInChocolate(NutsInChocolate c) {
        this(c.getName(), c.getCalories(), c.getPrice(), c.getChocolateKind(), c.getTypeOfNuts(),c.getImage());
        this.article = c.getArticle();
    }

    public Chocolate clone() {
        return new NutsInChocolate(this);
    }

    @Override
    public boolean equals(Chocolate c) {
        boolean r = super.equals(c);
        if (r)
            return (typeOfNuts == ((NutsInChocolate) c).getTypeOfNuts()) && (chocolateKind == ((NutsInChocolate) c).getChocolateKind());
        else return r;
    }

    public String showChocolates() {
        return "Тип: орехи в шоколаде ; название: " + getName() + " ; вид шоколада: " + getChocolateKind().getText() + " ; вид орехов: " + getTypeOfNuts().getText() + " ; " +
                "\n\t\t каллорийность: " + getCalories() + " ккал ; цена за 1 кг (в рублях): " + getPrice() + " ; "+getImage()+";";
    }
}
