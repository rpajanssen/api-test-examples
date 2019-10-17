package com.example.examples.domain.api;

import java.util.List;

/**
 * We use a SafeList instead of a List as the result of an rest API call to prevent CRSF attacks.
 *
 * As example, the person-resource returns a SafeList<Person>. This is because if you return a json like "[...]" - a
 * top level json array - then you are vulnerable to a CSRF attack! So it is best to always wrap a List / Array in a
 * wrapper object, which is exactly what our SafeList implementation does.
 */
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
