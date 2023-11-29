package org.example.app.entities;

public class TiledChocolate extends Chocolate {
    protected byte cacaoPercent;
    protected String filling;
    protected ChocolateKinds chocolateKind;

    public TiledChocolate(String name, int calories, double price, String filling, byte cacaoPercent, ChocolateKinds kind_of_chocolate,String image) {
        super(Types.tile, name, calories, price, Measures.piece,image);
        this.filling = filling;
        this.cacaoPercent = cacaoPercent;
        this.chocolateKind = kind_of_chocolate;
    }

    public TiledChocolate() {
        super(Types.tile);
        measure = Measures.piece;
        name = "";
        calories = 0;
        price = 0;
        filling = "";
        chocolateKind = null;
        cacaoPercent = 0;
    }

    public TiledChocolate(TiledChocolate c) {
        this(c.getName(), c.getCalories(), c.getPrice(), c.getFilling(), c.getCacaoPercent(), c.getChocolateKind(), c.getImage());
        this.article = c.getArticle();
    }


    public Byte getCacaoPercent() {
        return cacaoPercent;
    }

    public ChocolateKinds getChocolateKind() {
        return chocolateKind;
    }

    public String getFilling() {
        return filling;
    }

    public void setCacaoPercent(Byte percent) {
        cacaoPercent = percent;
    }

    public void setFilling(String filling) {
        this.filling = filling;
    }

    public void setChocolateKind(ChocolateKinds kind) {
        chocolateKind = kind;
    }

    public String showChocolates() {
        return "Тип: плиточный шоколад  ; название: " + getName() + " ; вид шоколада: " + getChocolateKind().getText() +
                " ; начинка: " + getFilling() + " ; процент содержания какао: " + getCacaoPercent() + " ;" +
                "\n\t\t каллорийность: " + getCalories() + " ккал ; цена за 1 " + getMeasure().getText() + " (в рублях): " + getPrice() + " ;";
    }

    public Chocolate clone() {
        return new TiledChocolate(this);
    }

    @Override
    public boolean equals(Chocolate c) {
        boolean r = super.equals(c);
        if (r)
            return (cacaoPercent == ((TiledChocolate) c).getCacaoPercent()) && (filling==null?((TiledChocolate) c).getFilling()==null:filling.equals(((TiledChocolate) c).getFilling())) &&
                    (chocolateKind == null? ((TiledChocolate) c).getChocolateKind()==null:chocolateKind.equals(((TiledChocolate) c).getChocolateKind()));
        else return false;
    }
}
