package de.cats.restcat.service;

import java.util.Comparator;

public class SortName implements Comparator<Cat> {
    @Override
    public int compare(final Cat a1, final Cat a2) {
        return a1.getName().compareToIgnoreCase(a2.getName());
    }
}