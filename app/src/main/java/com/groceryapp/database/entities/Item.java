package com.groceryapp.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "item_table")
public class Item {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    public long itemId;

    @ColumnInfo(name = "order_id")
    public long orderId;

    @ColumnInfo(name = "item_name")
    public String itemName;

    @ColumnInfo(name = "item_price")
    public float itemPrice;


    @ColumnInfo(name = "item_count")
    public int itemCount;


    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public void itemIncrement(){
        itemCount++;
    }

    public void itemDecrement(){
        itemCount--;
    }
}
