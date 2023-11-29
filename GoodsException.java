package org.example.app.entities;

public class GoodsException extends Exception{
    private Goods item;
   public GoodsException(String er){
        super(er);
        item = null;
    }
    public GoodsException(String er, Goods g){
        super(er);
        item = g;
    }
    public String showGoods(){
        return item==null ? "" : item.getItem().getName();
    }
    public Goods getGoods() {
        return item;
    }

    public void setGoods(Goods item) {
        this.item = item;
    }
}
