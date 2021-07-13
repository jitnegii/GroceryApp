package com.groceryapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
@Entity(tableName = "item_table" ,primaryKeys = {"user_id","shop_id","item_id"})
public class Item {

    @NonNull
    public String item_id;

    @NonNull
    public String user_id;

    @NonNull
    public String shop_id;

    public String item_name;

    public String item_price;

    public String user_name;

    public String shop_name;

    public String latitude;

    public String longitude;

    public long modified;

    public String status;

}
