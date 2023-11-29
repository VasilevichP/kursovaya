package org.example.app.entities;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Comparator;

public class ChocolateList implements Serializable {
    private ArrayList<Goods> chocolates;

    public ChocolateList() {
        chocolates = new ArrayList<Goods>();
    }

    public int size() {
        return chocolates.size();
    }
    public int addChocolateToList(Goods g) {
        chocolates.add(g);
        return chocolates.size();
    }

    public int addChocolate(Goods g) {
        chocolates.add(g);
        return chocolates.size();
    }

    public ArrayList<Goods> getList() {
        return (ArrayList<Goods>) chocolates.stream().collect(Collectors.toList());
    }

    public void showChocolateList() {
        if (chocolates.size() != 0) {
            int i = 1;
            for (Goods с : chocolates) {
                System.out.println("№:" + (i++) + '\t' + с.showItem());
            }
        } else System.out.println("Список товаров пуст.");
    }

    public double calculateValue() {
        double totalValue = 0;
        for (Goods g : chocolates) {
            totalValue += g.calculate();
        }
        return Math.round(totalValue * 100.0) / 100.0;
    }

    public int calculateQuantity() {
        int totalQuantity = 0;
        for (Goods g : chocolates) {
            if (g.getItem().getMeasure() == Measures.piece) {
                totalQuantity += g.getQuantity();
            } else totalQuantity++;
        }
        return totalQuantity;
    }

    protected int setList(ArrayList<Goods> goods) {
        chocolates = new ArrayList<Goods>(goods);
        return chocolates.size();
    }

    public Stream<Goods> stream() {
        Stream<Goods> stream = chocolates.stream();
        return stream;
    }
    public static Comparator<Goods> ComparatorByPrice() {
        return new Comparator<Goods>() {
            @Override
            public int compare(Goods t1, Goods t2) {
                if (t1.getItem().getPrice() < t2.getItem().getPrice()) return -1;
                else if (t1.getItem().getPrice() > t2.getItem().getPrice()) return 1;
                else return 0;
            }
        };
    }

    public Goods getItem(int index) {
        Goods g = null;
        if (index >= 0 && index < chocolates.size()) {
            g = chocolates.get(index);
        } else System.out.println("Неправильно введен номер товара.");
        return g;
    }
    public Goods getItem(Chocolate choco){
        int i = findItem(choco.getArticle());
        if (i<0) return null;
        return getItem(i);
    }

    public int findItem(int article) {
        int index = 0;
        if(article==0) return -1;
        for (Goods i : chocolates) {
            if (i.getItem().getArticle() == article)
                break;
            index++;
        }
        if (index == chocolates.size()) return -1;
        else return index;
    }

    protected int removeChocolateFromList(int index) {
        chocolates.remove(index);
        return size();
    }

    public int removeChocolate(int article) {
        int i = findItem(article);
        if (i<0) return -1;
        return removeChocolateFromList(i);
    }
//
//    void changeChocolate(int index) {
//        Goods choco;
//        double q;
//        if (index >= 0 && index < chocolates.size()) {
//            choco = chocolates.get(index);
//            q = choco.getQuantity();
//            System.out.println(choco.showItem());
//            double new_quantity = choco.enterQuantity();
//            if (new_quantity != q) {
//                System.out.println(choco.showItem());
//                if (SafeInput.getConfirmation("Вы хотите сохранить изменения? (для подтверждения нажмите 'y')?", "y")) {
//                    chocolates.get(index).setQuantity(new_quantity);
//                    System.out.println("Количество товара было изменено");
//                    //writeToFile();
//                } else {
//                    choco.setQuantity(q);
//                    System.out.println("\nЛадно.");
//                }
//            }
//        } else System.out.println("\nНеправильно введен номер товара.");
//    }

//    Goods changeChocolate(Goods ch) {
//        if (ch == null) {
//            System.out.println("Передан пустой объект");
//            return null;
//        }
//        int i = findItem(ch.getItem().getArticle());
//        if (i >= 0) {
//            changeChocolate(i);
//            return getItem(i);
//        } else {
//            System.out.println("Товар не найден: ");
//            ch.showItem();
//            return null;
//        }
//    }
}
