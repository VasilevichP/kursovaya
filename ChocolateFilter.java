package org.example.app.model;

import org.example.app.entities.*;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChocolateFilter extends Filter<Goods> {
    private boolean isByType, isByName, isByChocolateKind, isByNutsType, isByQuantity;
    private double quantityValue;
    private Types typeValue;
    private String nameValue;
    private ChocolateKinds chocolateKindValue;
    private Nuts nutsValue;

    public ChocolateFilter(ArrayList<Goods> l) {
        super(l);
    }

    public void setSource(ArrayList<Goods> l) {
        sourceList = l;
    }

    public void setSource(ChocolateList cl) {
        sourceList = (ArrayList<Goods>) cl.stream().collect(Collectors.toList());
        filteredList = new ArrayList<Goods>(sourceList);
    }

    public void removeAllFilters() {
        isByType = false;
        isByName = false;
        isByChocolateKind = false;
        isByNutsType = false;
        typeValue = null;
        nameValue = "";
        chocolateKindValue = null;
        nutsValue = null;
    }

    public void setByQuantity() {
        isByQuantity = true;
    }

    public void setByType(Types filterValue) {
        isByType = true;
        typeValue = filterValue;
    }

    public void removeByType() {
        isByType = false;
        typeValue = null;
    }

    public void setByName(String filterValue) {
        isByName = true;
        nameValue = filterValue;
    }

    public void removeByName() {
        isByName = false;
        nameValue = "";
    }

    public void setByChocolateKind(ChocolateKinds filterValue) {
        isByChocolateKind = true;
        chocolateKindValue = filterValue;
    }

    public void removeByChocolateKind() {
        isByChocolateKind = false;
        chocolateKindValue = null;
    }

    public void setByNutsType(Nuts filterValue) {
        isByNutsType = true;
        nutsValue = filterValue;
    }

    public void removeByNutsType() {
        isByNutsType = false;
        nutsValue = null;
    }

    private Stream<Goods> filterByQuantity(Stream<Goods> s) {
        if (isByQuantity)
            return s.filter(x -> x.getQuantity() > 0);
        else
            return s;
    }

    private Stream<Goods> filterByType(Stream<Goods> s) {
        if (isByType)
            return s.filter(x -> x.getItem().getType() == typeValue);
        else
            return s;
    }

    private Stream<Goods> filterByName(Stream<Goods> s) {
        if (isByName)
            return s.filter(x -> x.getItem().getName().toLowerCase().indexOf(nameValue.toLowerCase()) >= 0);
        else
            return s;
    }

    private Stream<Goods> filterByNuts(Stream<Goods> s) {
        if (isByNutsType)
            return s.filter(x -> x.getItem().getTypeOfNuts() == nutsValue);
        else
            return s;
    }

    private Stream<Goods> filterByChocolateKind(Stream<Goods> s) {
        if (isByChocolateKind)
            return s.filter(x -> x.getItem().getChocolateKind() == chocolateKindValue);
        else
            return s;
    }

    private Stream<Goods> sort(Stream<Goods> s) {
        if (sorter != null)
            return s.sorted(sorter);
        else
            return s;
    }

    public ArrayList<Goods> apply() {
        Stream<Goods> s = sourceList.stream();
        s = filterByType(s);
        s = filterByName(s);
        s = filterByChocolateKind(s);
        s = filterByNuts(s);
        s = filterByQuantity(s);
        s = sort(s);

        filteredList = (ArrayList<Goods>) s.collect(Collectors.toList());
        return filteredList;
    }

    public void displayList() {
        if (filteredList.size() != 0)
            filteredList.stream().forEach(x -> System.out.println(x.showItem()));
        else System.out.println("Нет подходящих элеметов");
    }

    public ArrayList<Goods> toList() {
        return filteredList;
    }

}
