package org.example.app.model;

import org.example.app.entities.Account;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountFilter extends Filter<Account> {
    private boolean isBlocked, isNotBlocked;

    public AccountFilter(ArrayList<Account> list) {
        super(list);
    }

    @Override
    public void removeAllFilters() {
        isBlocked = false;
        isNotBlocked = false;
    }

    public ArrayList<Account> apply() {
        Stream<Account> s = sourceList.stream();
        s = filterByBlocked(s);
        s = filterByNotBlocked(s);
        filteredList = (ArrayList<Account>) s.collect(Collectors.toList());
        return filteredList;
    }

    public void setBlocked() {
        isBlocked = true;
        isNotBlocked = false;
    }

    public void setNotBlocked() {
        isNotBlocked = true;
        isBlocked = false;
    }

    private Stream<Account> filterByBlocked(Stream<Account> s) {
        if (isBlocked)
            return s.filter(x -> x.getBlocked());
        else return s;
    }

    private Stream<Account> filterByNotBlocked(Stream<Account> s) {
        if (isNotBlocked)
            return s.filter(x -> x.getBlocked() == false);
        else return s;
    }
    public void displayList() {
        if (filteredList.size() != 0)
            filteredList.stream().forEach(x -> System.out.println(x.getLogin()));
        else System.out.println("Нет подходящих элеметов");
    }
}
