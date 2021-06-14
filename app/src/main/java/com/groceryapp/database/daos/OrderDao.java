package com.groceryapp.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.groceryapp.database.entities.Item;
import com.groceryapp.database.entities.Order;
import com.groceryapp.database.entities.OrderWithItems;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertOrder(Order order);

    @Query("SELECT * FROM item_table WHERE order_id=:order_id")
    public List<Item> getItems(String order_id);

//    @Transaction
//    @Query("SELECT * FROM order_table WHERE (user_id=:userID AND delivered = :delivered) ORDER BY time DESC ")
//    public LiveData<ArrayList<Order>> getUserOrders(String userID, Boolean delivered);

    @Transaction
    @Query("SELECT * FROM order_table WHERE (user_id=:userID AND delivered = :delivered) ORDER BY time DESC ")
    public LiveData<List<OrderWithItems>> getOrderWithItems(String userID, Boolean delivered);

    @Query("UPDATE order_table SET delivered=:delivered WHERE order_id=:orderId")
    public void updateDelivered(String orderId,boolean delivered);


}
