package com.abnamro.examples.domain.api;

import java.util.List;

// todo : document (CRSF)
public class SafeList<T> {
    private List<T> items;

    public SafeList() {
        // required by (de)serialization framework
    }

    public SafeList(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }
}
