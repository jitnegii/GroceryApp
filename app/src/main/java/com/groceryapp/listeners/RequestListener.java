package com.groceryapp.listeners;

import com.groceryapp.database.entities.Item;

public interface RequestListener {
    void remove(Item item);
    void buy(Item item);
}
