package com.groceryapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public
class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    public String user_id;

    @ColumnInfo(name = "user_name")
    public String userName;


    @ColumnInfo(name = "number_or_email")
    public String numberOrEmail;

    @ColumnInfo(name = "latitude")
    public String latitude;

    @ColumnInfo(name = "longitude")
    public String longitude;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "photo")
    public String photoUri;



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getLoginId() {
        return numberOrEmail;
    }


    public void setNumberOrEmail(String numberOrEmail) {
        this.numberOrEmail = numberOrEmail;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getNumberOrEmail() {
        return numberOrEmail;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }



}
