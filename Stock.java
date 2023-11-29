package org.example.app.model;

import com.mysql.cj.xdevapi.*;
import org.example.app.DBConnection.DBConnection;
import org.example.app.entities.*;

import java.io.*;
import java.util.HashMap;

public class Stock extends ChocolateList implements Serializable {
    private static final String FILE_NAME = "Chocolates.dat";
    public static final String FILE_ACCOUNTS_NAME = "Accounts.dat";
    private static final String CH_TABLE_NAME = "Chocolates";
    private static final String ST_TABLE_NAME = "Stock";
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
    private static final String ST_ARTICLE = "st_article";
    private static final String ST_QUANTITY = "st_quantity";
    private static Stock instance;

    public static Stock getInstance() {
        if (instance == null)
            instance = new Stock();
        return instance;
    }

    private Stock() {
        super();
        readFromDB();
    }

    public Chocolate findChoco(int article){
        for(Goods g: getList()){
            if (g.getItem().getArticle()==article)
                return g.getItem();
        }
        return null;
    }

    public double checkQuantity(Goods item) throws GoodsException {
        double n=0;
        boolean isFound=false;
        for (Goods g:getList()){
            if(item.getItem().getArticle()==g.getItem().getArticle()){
                n=item.getQuantity()- g.getQuantity();
                isFound=true;
                break;
            }
        }
        if(!isFound) throw new GoodsException("Товар не найден на складе", item);
        if(n<0) n=0;
        return n;
    }

    private void readFromDB() {
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table chocoTable = connection.getTable(CH_TABLE_NAME);
            Table stockTable = connection.getTable(ST_TABLE_NAME);
            if (chocoTable == null) System.out.println("Невозможно открыть таблицу " + CH_TABLE_NAME);
            if (stockTable == null) System.out.println("Невозможно открыть таблицу " + ST_TABLE_NAME);
            if (chocoTable != null && stockTable != null) {
                RowResult stockRows = stockTable.select("*").execute();
                if (stockRows.count() == 0) System.out.println("Нет записей");
                while (stockRows.hasNext()) {
                    Row stockRow = stockRows.next();
                    int article = stockRow.getInt(ST_ARTICLE);
                    RowResult chocoRows = chocoTable.select().where(CH_ARTICLE + " = :ch_article").bind("ch_article", article).execute();
                    Row chocoRow = chocoRows.next();
                    if (chocoRow == null) {
                        System.out.println("No such good!");
                        continue;
                    }
                    Types type = Types.getInstance(chocoRow.getString(CH_TYPE));
                    Chocolate c;
                    switch (type) {
                        case tile:
                            c = new TiledChocolate(chocoRow.getString(CH_NAME),
                                    chocoRow.getInt(CH_CALORIES), chocoRow.getDouble(CH_PRICE),
                                    chocoRow.getString(CH_FILLING), chocoRow.getByte(CH_CACAO_PERCENT),
                                    ChocolateKinds.getInstance(chocoRow.getString(CH_CHOCO_KIND)),chocoRow.getString(CH_IMAGE));
                            break;
                        case sweets:
                            c = new Sweets(chocoRow.getString(CH_NAME),
                                    chocoRow.getInt(CH_CALORIES), chocoRow.getDouble(CH_PRICE), Measures.getInstance(chocoRow.getString(CH_MEASURE)),chocoRow.getString(CH_IMAGE));
                            break;
                        case bars:
                            c = new ChocolateBars(chocoRow.getString(CH_NAME),
                                    chocoRow.getInt(CH_CALORIES), chocoRow.getDouble(CH_PRICE),chocoRow.getString(CH_IMAGE));
                            break;
                        case cookies:
                            c = new Cookies(chocoRow.getString(CH_NAME),
                                    chocoRow.getInt(CH_CALORIES), chocoRow.getDouble(CH_PRICE), Measures.getInstance(chocoRow.getString(CH_MEASURE)),chocoRow.getString(CH_IMAGE));
                            break;
                        case powder:
                            c = new ChocolatePowder(chocoRow.getString(CH_NAME),
                                    chocoRow.getInt(CH_CALORIES), chocoRow.getDouble(CH_PRICE),chocoRow.getString(CH_IMAGE));
                            break;
                        case paste:
                            c = new ChocolatePaste(chocoRow.getString(CH_NAME),
                                    chocoRow.getInt(CH_CALORIES), chocoRow.getDouble(CH_PRICE), Measures.getInstance(chocoRow.getString(CH_MEASURE)),chocoRow.getString(CH_IMAGE));
                            break;
                        case nuts:
                            c = new NutsInChocolate(chocoRow.getString(CH_NAME),
                                    chocoRow.getInt(CH_CALORIES), chocoRow.getDouble(CH_PRICE),
                                    ChocolateKinds.getInstance(chocoRow.getString(CH_CHOCO_KIND)), Nuts.getInstance(chocoRow.getString(CH_NUTS)),chocoRow.getString(CH_IMAGE));
                            break;
                        default:
                            c = null;
                            System.out.println("Тип " + type + " для значения " + chocoRow.getString(CH_TYPE) + " не определен.");
                    }
                    if (c != null) {
                        c.setArticle(stockRow.getInt(ST_ARTICLE));
                        Goods g = new Goods(c, stockRow.getDouble(ST_QUANTITY));
                        addChocolateToList(g);
                    }
                }
            }
            //   connection.close();
        }
    }

    public int addChocolate(Goods g){
        int r = super.addChocolate(g);
        addToDB(g);
        return r;
    }
    private void addToDB(Goods g) {
        if (g == null) {
            System.out.println("Передан null для сохранения");
            return;
        }
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table stockTable = connection.getTable(ST_TABLE_NAME);
            Table chocoTable = connection.getTable(CH_TABLE_NAME);
            if (stockTable == null)
                System.out.println("Невозможно открыть таблицу " + ST_TABLE_NAME);
            if (chocoTable == null)
                System.out.println("Невозможно открыть таблицу " + CH_TABLE_NAME);
            if (chocoTable != null && stockTable != null) {
                InsertResult chRes = null;
                Types type = g.getItem().getType();
                switch (type) {
                    case tile:
                        chRes = chocoTable.insert(CH_TYPE, CH_NAME, CH_CALORIES, CH_PRICE, CH_MEASURE, CH_FILLING, CH_CHOCO_KIND, CH_CACAO_PERCENT,CH_IMAGE).values
                                (g.getItem().getType().getText(), g.getItem().getName(),
                                        g.getItem().getCalories(), g.getItem().getPrice(), g.getItem().getMeasure().getText(), ((TiledChocolate) g.getItem()).getFilling(),
                                        ((TiledChocolate) g.getItem()).getChocolateKind().getText(), ((TiledChocolate) g.getItem()).getCacaoPercent(),g.getItem().getImage()).execute();
                        break;
                    case sweets, bars, cookies, powder, paste:
                        chRes = chocoTable.insert(CH_TYPE, CH_NAME, CH_CALORIES, CH_PRICE, CH_MEASURE,CH_IMAGE).values
                                (g.getItem().getType().getText(), g.getItem().getName(),
                                        g.getItem().getCalories(), g.getItem().getPrice(), g.getItem().getMeasure().getText(),g.getItem().getImage()).execute();
                        break;
                    case nuts:
                        chRes = chocoTable.insert(CH_TYPE, CH_NAME, CH_CALORIES, CH_PRICE, CH_MEASURE, CH_CHOCO_KIND, CH_NUTS,CH_IMAGE).values
                                (g.getItem().getType().getText(), g.getItem().getName(),
                                        g.getItem().getCalories(), g.getItem().getPrice(), g.getItem().getMeasure().getText(),
                                        ((NutsInChocolate) g.getItem()).getChocolateKind().getText(), ((NutsInChocolate) g.getItem()).getTypeOfNuts().getText(),g.getItem().getImage()).execute();
                        break;
                    default:
                        System.out.println("Тип " + type + " не поддерживается");
                }
                System.out.printf("Insert to table " + CH_TABLE_NAME + " completed. Number of affected counts: %d. Number of warnings: %d\n", chRes.getAffectedItemsCount(), chRes.getWarningsCount());
                while (chRes.getWarnings().hasNext()) {
                    Warning w = chRes.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }
                g.getItem().setArticle(chRes.getAutoIncrementValue().intValue());
                InsertResult stRes = stockTable.insert(ST_QUANTITY)
                        .values(g.getQuantity()).execute();
                System.out.printf("Insert to table " + ST_TABLE_NAME + " completed. Number of affected counts: %d. Number of warnings: %d\n", stRes.getAffectedItemsCount(), stRes.getWarningsCount());
                while (stRes.getWarnings().hasNext()) {
                    Warning w = stRes.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }
            }
//            connection.close();
        }
    }

    public void updateDB(Goods g) {
        if (g == null) {
            System.out.println("Передан null для сохранения");
        } else {
            DBConnection connection = DBConnection.getInstance();
            if (connection != null) {
                Table stockTable = connection.getTable(ST_TABLE_NAME);
                Table chocoTable = connection.getTable(CH_TABLE_NAME);
                if (stockTable == null)
                    System.out.println("Невозможно открыть таблицу " + ST_TABLE_NAME);
                if (chocoTable == null)
                    System.out.println("Невозможно открыть таблицу " + CH_TABLE_NAME);
                if (chocoTable != null && stockTable != null) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    Chocolate c = g.getItem();
                    map.put(CH_ARTICLE, c.getArticle());
                    map.put(CH_TYPE, c.getType().getText());
                    map.put(CH_NAME, c.getName());
                    map.put(CH_CALORIES, c.getCalories());
                    map.put(CH_PRICE, c.getPrice());
                    map.put(CH_MEASURE, c.getMeasure().getText());
                    map.put(CH_IMAGE,c.getImage());
                    if (c.getType() == Types.tile) {
                        map.put(CH_FILLING, ((TiledChocolate) c).getFilling());
                        map.put(CH_CACAO_PERCENT, ((TiledChocolate) g.getItem()).getCacaoPercent());
                    }
                    if (c.getType() == Types.nuts || c.getType() == Types.tile)
                        map.put(CH_CHOCO_KIND, (c.getType() == Types.tile ? ((TiledChocolate) c).getChocolateKind().getText() : ((NutsInChocolate) c).getChocolateKind().getText()));
                    if (c.getType() == Types.nuts)
                        map.put(CH_NUTS, ((NutsInChocolate) c).getTypeOfNuts().getText());
                    Result res = chocoTable.update().set(map).where(CH_ARTICLE + " = :ch_article").bind("ch_article", g.getItem().getArticle()).execute();
                    System.out.printf("Update " + CH_TABLE_NAME + " completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                    while (res.getWarnings().hasNext()) {
                        Warning w = res.getWarnings().next();
                        System.out.printf("\t%s\n", w.getMessage());
                    }
                    HashMap<String, Object> smap = new HashMap<String, Object>();
//                    smap.put(ST_ARTICLE, g.getItem().getArticle());
                    smap.put(ST_QUANTITY, g.getQuantity());
                    Result sres = stockTable.update().set(smap).where(ST_ARTICLE + " = :st_article").bind("st_article", g.getItem().getArticle()).execute();
                    System.out.printf("Update " + ST_TABLE_NAME + " completed. Number of affected counts: %d. Number of warnings: %d\n", sres.getAffectedItemsCount(), sres.getWarningsCount());
                    while (sres.getWarnings().hasNext()) {
                        Warning w = sres.getWarnings().next();
                        System.out.printf("\t%s\n", w.getMessage());
                    }
                }
                //connection.close();
            }
        }
    }

    public void removeFromDB(Goods g) {
        if (g == null) {
            System.out.println("Передан null для сохранения");
        } else {
            DBConnection connection = DBConnection.getInstance();
            if (connection != null) {
                Table stockTable = connection.getTable(ST_TABLE_NAME);
                if (stockTable == null)
                    System.out.println("Невозможно открыть таблицу " + ST_TABLE_NAME);
                else {
                    Result sres = stockTable.delete().where(ST_ARTICLE + " = :st_article").bind("st_article", g.getItem().getArticle()).execute();
                    System.out.printf("Delete completed. Number of affected counts: %d. Number of warnings: %d\n", sres.getAffectedItemsCount(), sres.getWarningsCount());
                    while (sres.getWarnings().hasNext()) {
                        Warning w = sres.getWarnings().next();
                        System.out.printf("\t%s\n", w.getMessage());
                    }
                }
//                connection.close();
            }
        }
    }

}
