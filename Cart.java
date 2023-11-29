package org.example.app.model;

import com.mysql.cj.xdevapi.*;
import org.example.app.DBConnection.DBConnection;
import org.example.app.entities.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Cart extends ChocolateList {
    private static final String FILE_NAME = "Carts.dat";
    private static final String TABLE_CART_HEADER = "carts";
    private static final String TABLE_CART_ITEM = "cart";
    private static final String TABLE_CHOCOLATES = "chocolates";
    private static final String CH_ARTICLE = "ch_article";
    private static final String CH_TYPE = "ch_type";
    private static final String CH_NAME = "ch_name";
    private static final String CH_CALORIES = "ch_calories";
    private static final String CH_PRICE = "ch_price";
    private static final String CH_MEASURE = "ch_measure";
    private static final String CH_FILLING = "ch_filling";
    private static final String CH_CHOCO_KIND = "ch_choco_kind";
    private static final String CH_CACAO_PERCENT = "ch_cacao_percent";
    private static final String CH_NUTS = "ch_nuts";
    private static final String CH_IMAGE = "ch_image";
    private static final String CARTS_ID = "ca_id";
    private static final String CARTS_STATE = "ca_state";
    private static final String CARTS_DATE = "ca_date";
    private static final String CARTS_CUSTOMER = "ca_customer";
    private static final String CART_ID = "cart_id";
    private static final String CART_ARTICLE = "cart_article";
    private static final String CART_QUANTITY = "cart_quantity";


    private Date date;
    protected int id;
    private CartState state;
    private String customer;

    public static ArrayList<Cart> loadHistory(String log) {
        ArrayList<Cart> carts=new ArrayList<Cart>();
        carts=loadHistoryFromDB(carts,log);
        return carts;
    }

    public Cart(String customer) {
        super();
        this.customer = customer;
        state = CartState.Open;
        id = 0;
        this.readFromDB();
        if (id == 0)
            addNewCartToDB(this);

    }
    public Cart (String customer,CartState cartState) {
        super();
        this.customer = customer;
        state = cartState;
    }

    public CartState getState() {
        return state;
    }

    public String getCustomer() {
        return customer;
    }

    public int getId() {
        return id;
    }
    public Date getDate(){return date;}

    public String formatedDate() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(this.date.getTime());
        return formater.format(date);
    }

    public void showCart() {
        if (getList().size() != 0) {
            showChocolateList();
            System.out.println("Общее количество выбранного товара: " + calculateQuantity());
            System.out.format("Общая сумма выбранного товара: %.2f рублей", calculateValue());
            System.out.println("\n------------------------------------------------------------------------------------\n");
        } else System.out.println("Корзина пуста");
    }

    public void addToCart(Goods g) {
        int index = findItem(g.getItem().getArticle());
        if (index == -1) {
            addChocolateToList(g);
            addItemsToDB(g);
        } else {
            double q1=getList().get(index).getQuantity()+g.getQuantity();
            getList().get(index).setQuantity(q1);
            g.setQuantity(q1);
            updateItemInDB(g);
        }
    }

    public void clearCart() {
        getList().clear();
        clearCartDB();
    }

    public String payCart() {
        Stock stock=Stock.getInstance();
        showCart();
        if (getList().size() != 0) {
            boolean correspondence = true;
            for (Goods c : getList()) {
                try {
                    if (stock.checkQuantity(c) > 0) {
                        System.out.println("Товара" + c.getItem().getName() + " не хватает на складе.");
                        return c.getItem().getName();
                    }
                } catch (GoodsException e) {
                    System.out.println(e + ":");
                    System.out.println(e.showGoods());
                    return c.getItem().getName();
                }
            }
            if (correspondence) {
                for (Goods c : getList()) {
                    Goods stockItem = stock.getItem(c.getItem());
                    stockItem.setQuantity(stockItem.getQuantity() - c.getQuantity());
                    stock.updateDB(stockItem);
                }
                date = new Date();
                state = CartState.Closed;
                this.payCartDB();
                System.out.println("Заказ был оплачен.");
                setList(new ArrayList<Goods>());
                id = 0;
                state = CartState.Open;
                addNewCartToDB(this);
                return "";
            }
        }
        return "";
    }

    private void readFromDB() {
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table chocoTable = connection.getTable(TABLE_CHOCOLATES);
            Table cartsTable = connection.getTable(TABLE_CART_HEADER);
            Table cartTable = connection.getTable(TABLE_CART_ITEM);
            if (chocoTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CHOCOLATES);
            if (cartsTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_HEADER);
            if (cartTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_ITEM);
            if (chocoTable != null && cartsTable != null && cartTable != null) {
                RowResult cartsRows = cartsTable.select().where(CARTS_CUSTOMER + " = :ca_customer && " + CARTS_STATE + " = :ca_state").bind("ca_customer", this.getCustomer()).bind("ca_state", CartState.Open.ordinal()).execute();
                if (cartsRows.count() == 0) System.out.println("Нет нужной корзины");
                if (cartsRows.count() > 1)
                    System.out.println("В базе несколько открытых корзин у пользователя " + this.getCustomer());
                while (cartsRows.hasNext()) {
                    Row cartsRow = cartsRows.next();
                    int id = cartsRow.getInt(CARTS_ID);
                    this.id = id;
                    RowResult cartRows = cartTable.select().where(CART_ID + " = :cart_id").bind("cart_id", id).execute();
                    if (cartRows.count() == 0) System.out.println("Корзина пуста");
                    while (cartRows.hasNext()) {
                        Row cartRow = cartRows.next();
                        if (cartRow == null) {
                            System.out.println("No such item!");
                            continue;
                        }
                        int article = cartRow.getInt(CART_ARTICLE);
                        Row item = (Row) chocoTable.select().where(CH_ARTICLE + " = :ch_article").bind("ch_article", article).execute().next();
                        Types type = Types.getInstance(item.getString(CH_TYPE));
                        Chocolate c;
                        switch (type) {
                            case tile:
                                c = new TiledChocolate(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE),
                                        item.getString(CH_FILLING), item.getByte(CH_CACAO_PERCENT),
                                        ChocolateKinds.getInstance(item.getString(CH_CHOCO_KIND)),item.getString(CH_IMAGE));
                                break;
                            case sweets:
                                c = new Sweets(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE), Measures.getInstance(item.getString(CH_MEASURE)),item.getString(CH_IMAGE));
                                break;
                            case bars:
                                c = new ChocolateBars(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE),item.getString(CH_IMAGE));
                                break;
                            case cookies:
                                c = new Cookies(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE), Measures.getInstance(item.getString(CH_MEASURE)),item.getString(CH_IMAGE));
                                break;
                            case powder:
                                c = new ChocolatePowder(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE),item.getString(CH_IMAGE));
                                break;
                            case paste:
                                c = new ChocolatePaste(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE), Measures.getInstance(item.getString(CH_MEASURE)),item.getString(CH_IMAGE));
                                break;
                            case nuts:
                                c = new NutsInChocolate(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE),
                                        ChocolateKinds.getInstance(item.getString(CH_CHOCO_KIND)), Nuts.getInstance(item.getString(CH_NUTS)),item.getString(CH_IMAGE));
                                break;
                            default:
                                c = null;
                                System.out.println("Тип " + type + " для значения " + item.getString(CH_TYPE) + " не определен.");
                        }
                        if (c != null) {
                            c.setArticle(item.getInt(CH_ARTICLE));
                            Goods g = new Goods(c, cartRow.getDouble(CART_QUANTITY));
                            addChocolateToList(g);
                        }
                    }
                }
            }
            //   connection.close();
        }
    }

    public void addNewCartToDB(Cart c) {
        if (c == null) {
            System.out.println("Передан null для сохранения");
            return;
        }
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table cartsTable = connection.getTable(TABLE_CART_HEADER);
            if (cartsTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_HEADER);
            else {
                InsertResult cRes = cartsTable.insert(CARTS_CUSTOMER, CARTS_STATE)
                        .values(c.getCustomer(), 1).execute();
                c.id = cRes.getAutoIncrementValue().intValue();
                System.out.printf("Insert to table " + TABLE_CART_HEADER + " completed. Number of affected counts: %d. Number of warnings: %d\n", cRes.getAffectedItemsCount(), cRes.getWarningsCount());
                while (cRes.getWarnings().hasNext()) {
                    Warning w = cRes.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }
            }
//            connection.close();
        }
    }

    public void addItemsToDB(Goods g) {
        if (g == null) {
            System.out.println("Передан null для сохранения");
            return;
        }
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table cartTable = connection.getTable(TABLE_CART_ITEM);
            if (cartTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_ITEM);
            else {
                InsertResult cRes = cartTable.insert(CART_ID, CART_ARTICLE, CART_QUANTITY)
                        .values(this.getId(), g.getItem().getArticle(), g.getQuantity()).execute();
                System.out.printf("Insert to table " + TABLE_CART_ITEM + " completed. Number of affected counts: %d. Number of warnings: %d\n", cRes.getAffectedItemsCount(), cRes.getWarningsCount());
                while (cRes.getWarnings().hasNext()) {
                    Warning w = cRes.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }
            }
//            connection.close();
        }
    }

    public void updateItemInDB(Goods g) {
        if (g == null) {
            System.out.println("Передан null для сохранения");
            return;
        }
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table cartTable = connection.getTable(TABLE_CART_ITEM);
            if (cartTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_ITEM);
            else {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(CART_ID, this.getId());
                map.put(CART_ARTICLE, g.getItem().getArticle());
                map.put(CART_QUANTITY, g.getQuantity());
                Result res = cartTable.update().set(map).where(CART_ID + " = :cart_id && " + CART_ARTICLE + " = :cart_article").bind("cart_id", this.getId()).bind("cart_article", g.getItem().getArticle()).execute();
                System.out.printf("Insert to table " + TABLE_CART_ITEM + " completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                while (res.getWarnings().hasNext()) {
                    Warning w = res.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }
            }
//            connection.close();
        }
    }

    public void payCartDB() {
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table cartsTable = connection.getTable(TABLE_CART_HEADER);
            if (cartsTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_HEADER);
            else {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(CARTS_ID, this.getId());
                map.put(CARTS_DATE, Long.toString(this.getDate().getTime()));
                map.put(CARTS_CUSTOMER, this.getCustomer());
                map.put(CARTS_STATE, 0);
                Result res = cartsTable.update().set(map).where(CARTS_ID + " = :ca_id ").bind("ca_id", this.getId()).execute();
                System.out.printf("Pay table " + TABLE_CART_HEADER + " completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                while (res.getWarnings().hasNext()) {
                    Warning w = res.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }
            }
//            connection.close();
        }
    }

    public void clearCartDB() {
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table cartTable = connection.getTable(TABLE_CART_ITEM);
            if (cartTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_ITEM);
            else {
                Result res = cartTable.delete().where(CART_ID + " = :cart_id ").bind("cart_id", this.getId()).execute();
                System.out.printf("Clear table " + TABLE_CART_ITEM + " completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                while (res.getWarnings().hasNext()) {
                    Warning w = res.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }
            }
//            connection.close();
        }
    }

    public void removeItemDB(Goods g) {
        if (g == null) {
            System.out.println("Передан null для сохранения");
            return;
        }
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table cartTable = connection.getTable(TABLE_CART_ITEM);
            if (cartTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_ITEM);
            else {
                Result res = cartTable.delete().where(CART_ID + " = :cart_id && " + CART_ARTICLE + " = :cart_article").bind("cart_id", this.getId()).bind("cart_article",g.getItem().getArticle()).execute();
                System.out.printf("Remove from table " + TABLE_CART_ITEM + " completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                while (res.getWarnings().hasNext()) {
                    Warning w = res.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }
            }
        }
    }

    private static ArrayList<Cart> loadHistoryFromDB(ArrayList<Cart> carts,String log) {
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table chocoTable = connection.getTable(TABLE_CHOCOLATES);
            Table cartsTable = connection.getTable(TABLE_CART_HEADER);
            Table cartTable = connection.getTable(TABLE_CART_ITEM);
            if (chocoTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CHOCOLATES);
            if (cartsTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_HEADER);
            if (cartTable == null) System.out.println("Невозможно открыть таблицу " + TABLE_CART_ITEM);
            if (chocoTable != null && cartsTable != null && cartTable != null) {
                RowResult cartsRows = cartsTable.select().where(CARTS_CUSTOMER + " = :ca_customer && " + CARTS_STATE + " = :ca_state").bind("ca_customer", log).bind("ca_state", CartState.Closed.ordinal()).execute();
                if (cartsRows.count() == 0) System.out.println("Нет оплаченных корзин");
                while (cartsRows.hasNext()) {
                    Cart cart=new Cart(log,CartState.Closed);
                    Row cartsRow = cartsRows.next();
                    int id =cartsRow.getInt(CARTS_ID);
                    cart.id = id;
                    cart.date= new Date(cartsRow.getLong(CARTS_DATE));
                    RowResult cartRows = cartTable.select().where(CART_ID + " = :cart_id").bind("cart_id", id).execute();
                    if (cartRows.count() == 0) System.out.println("Корзина пуста");
                    while (cartRows.hasNext()) {
                        Row cartRow = cartRows.next();
                        if (cartRow == null) {
                            System.out.println("No such item!");
                            continue;
                        }
                        int article = cartRow.getInt(CART_ARTICLE);
                        Row item = (Row) chocoTable.select().where(CH_ARTICLE + " = :ch_article").bind("ch_article", article).execute().next();
                        Types type = Types.getInstance(item.getString(CH_TYPE));
                        Chocolate c;
                        switch (type) {
                            case tile:
                                c = new TiledChocolate(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE),
                                        item.getString(CH_FILLING), item.getByte(CH_CACAO_PERCENT),
                                        ChocolateKinds.getInstance(item.getString(CH_CHOCO_KIND)),item.getString(CH_IMAGE));
                                break;
                            case sweets:
                                c = new Sweets(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE), Measures.getInstance(item.getString(CH_MEASURE)),item.getString(CH_IMAGE));
                                break;
                            case bars:
                                c = new ChocolateBars(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE),item.getString(CH_IMAGE));
                                break;
                            case cookies:
                                c = new Cookies(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE), Measures.getInstance(item.getString(CH_MEASURE)),item.getString(CH_IMAGE));
                                break;
                            case powder:
                                c = new ChocolatePowder(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE),item.getString(CH_IMAGE));
                                break;
                            case paste:
                                c = new ChocolatePaste(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE), Measures.getInstance(item.getString(CH_MEASURE)),item.getString(CH_IMAGE));
                                break;
                            case nuts:
                                c = new NutsInChocolate(item.getString(CH_NAME),
                                        item.getInt(CH_CALORIES), item.getDouble(CH_PRICE),
                                        ChocolateKinds.getInstance(item.getString(CH_CHOCO_KIND)), Nuts.getInstance(item.getString(CH_NUTS)),item.getString(CH_IMAGE));
                                break;
                            default:
                                c = null;
                                System.out.println("Тип " + type + " для значения " + item.getString(CH_TYPE) + " не определен.");
                        }
                        if (c != null) {
                            c.setArticle(item.getInt(CH_ARTICLE));
                            Goods g = new Goods(c, cartRow.getDouble(CART_QUANTITY));
                            cart.addChocolateToList(g);

                        }
                    }
                    carts.add(cart);
                }
            }
            //   connection.close();
        }
        return carts;
    }

}
