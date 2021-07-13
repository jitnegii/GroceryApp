package com.groceryapp.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.groceryapp.database.entities.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUser(User user);

    @Query("SELECT * FROM user_table WHERE user_id=:id")
    public User getUser(String id);

    @Query("SELECT * FROM user_table WHERE user_id=:id")
    public LiveData<User> getUserLive(String id);

    @Query("UPDATE user_table SET longitude=:longitude , latitude=:latitude  WHERE user_id=:id")
    public void updateLocation(String id,String latitude,String longitude);

    @Query("UPDATE user_table SET address=:address WHERE user_id=:id")
    public void updateAddress(String id,String address);

    @Query("UPDATE user_table SET photo=:photo WHERE user_id=:id")
    public void updatePhoto(String id,String photo);

    @Query("UPDATE user_table SET user_name =:userName WHERE user_id=:id")
    public void updateUserName(String id,String userName);

    @Query("UPDATE user_table SET number =:number WHERE user_id=:id")
    public void updateNumber(String id,String number);

    @Query("UPDATE user_table SET is_shop =:isShop WHERE user_id=:id")
    public void updateIsShop(String id,boolean isShop);
}
