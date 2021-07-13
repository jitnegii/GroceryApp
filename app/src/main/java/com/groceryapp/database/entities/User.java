package com.groceryapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
@Entity(tableName = "user_table")
public
class User {

    @PrimaryKey
    @NonNull
    public String user_id;

    public String user_name;

    public String email;

    public String number;

    public String latitude;

    public String longitude;

    public String address;

    public String photo;

    public boolean is_shop;


    public User() {
    }

//    public String getNumber() {
//        return number;
//    }
//
//    public void setNumber(String number) {
//        this.number = number;
//    }
//
//    public String getUserId() {
//        return user_id;
//    }
//
//    public void setUserId(String user_id) {
//        this.user_id = user_id;
//    }
//
//    public String getUserName() {
//        return user_name;
//    }
//
//    public void setUserName(String user_name) {
//        this.user_name = user_name;
//    }
//
//
//    public String getEmail() {
//        return email;
//    }
//
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//
//    public String getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(String latitude) {
//        this.latitude = latitude;
//    }
//
//    public String getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(String longitude) {
//        this.longitude = longitude;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//
//    public String getPhotoUri() {
//        return photo;
//    }
//
//    public void setPhotoUri(String photo) {
//        this.photo = photo;
//    }
//
//    public boolean isShop() {
//        return is_shop;
//    }
//
//    public void setShop(boolean is_shop) {
//        this.is_shop = is_shop;
//    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("user_id", user_id);
        map.put("user_name", user_name);
        map.put("number", number);
        map.put("email", email);
        map.put("is_shop", is_shop);
        map.put("photo", photo);
        map.put("address", address);
        map.put("latitude", latitude);
        map.put("longitude", longitude);

        return map;
    }

}
