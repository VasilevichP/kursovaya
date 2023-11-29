package org.example.app.model;

import org.example.app.entities.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Filter<T> {
    protected ArrayList<T> sourceList;
    protected ArrayList<T> filteredList;
    protected Comparator<T> sorter;

    public Filter() {
        removeAllFilters();
    }

    public Filter(ArrayList<T> list) {
        this.sourceList = list;
        removeAllFilters();
    }

    public abstract void removeAllFilters();

    public abstract ArrayList<T> apply();

    public void setSource(ArrayList<T> sourceList) {
        this.sourceList = sourceList;
    }

    public void setSorter(Comparator<T> c) {
        this.sorter = c;
    }

    public ArrayList<T> toList() {
        return filteredList;
    }
}

