package com.groceryapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Relation;

import java.util.List;

@Entity(tableName = "order_table",primaryKeys = {"user_id","time"})
public class Order {

    @NonNull
    @ColumnInfo(name = "time")
    public long time;

    @NonNull
    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "order_id")
    public String orderId;


    @ColumnInfo(name = "shop_name")
    public String shopName;


    @ColumnInfo(name = "total_price")
    public float totalPrice;


    @ColumnInfo(name = "total_items")
    public int totalItems;

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
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

}
