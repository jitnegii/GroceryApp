package com.groceryapp.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.groceryapp.database.entities.Item;

import java.util.List;
import java.util.Set;

@Dao
public interface ItemDao {

    @Query("SELECT item_id FROM item_table WHERE (user_id =:userId AND shop_id=:shopId)")
    LiveData<List<String>> getBucketItemForShopLive(String userId, String shopId);

    @Query("SELECT item_id FROM item_table WHERE (user_id =:userId AND shop_id=:shopId)")
    List<String> getBucketItemForShop(String userId, String shopId);

    @Query("SELECT * FROM item_table WHERE user_id =:userId")
    LiveData<List<Item>> getUserBucketItems(String userId);

    @Query("DELETE FROM item_table WHERE (user_id =:userId AND shop_id=:shopId AND item_id=:itemId)")
    void deleteItem(String userId, String shopId, String itemId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Item item);
}
