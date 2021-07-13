package com.groceryapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "order_table",primaryKeys = {"user_id","time"})
public class Order {

    @NonNull
    @ColumnInfo(name = "time")
    public long time;

    @NonNull
    @ColumnInfo(name = "user_id")
    public String user_id;

    @ColumnInfo(name = "order_id")
    public String order_id;


    @ColumnInfo(name = "shop_name")
    public String shop_name;


    @ColumnInfo(name = "total_price")
    public float total_price;


    @ColumnInfo(name = "total_items")
    public int total_items;

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    @ColumnInfo(name = "delivered")
    public boolean delivered;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String order_id) {
        this.order_id = order_id;
    }


    public String getShopName() {
        return shop_name;
    }

    public void setShopName(String shop_name) {
        this.shop_name = shop_name;
    }

    public float getTotalPrice() {
        return total_price;
    }

    public void setTotalPrice(float total_price) {
        this.total_price = total_price;
    }

    public int getTotalItems() {
        return total_items;
    }

    public void setTotalItems(int total_items) {
        this.total_items = total_items;
    }

}
