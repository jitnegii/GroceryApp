package com.groceryapp.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.OrderWithItems;
import com.groceryapp.database.entities.User;

import java.util.List;


public class UserViewModel extends AndroidViewModel {

    private final MyRoomDatabase db;
    private LiveData<User> user;
    private LiveData<List<OrderWithItems>> deliveredOrders;
    private LiveData<List<OrderWithItems>> unDeliveredOrders;

    public UserViewModel(@NonNull Application application) {
        super(application);
        db = MyRoomDatabase.getDatabase(application);
    }

    public void insert(User user) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> db.userDao().insertUser(user));
    }

    public LiveData<User> getUser(String id){

        if(user ==null)
            user = db.userDao().getUserLive(id);
        return user;
    }

    public LiveData<List<OrderWithItems>> getDeliveredOrder(String id){
        if(deliveredOrders ==null){
            deliveredOrders = db.orderDao().getOrderWithItems(id,true);
        }
        return deliveredOrders;
    }

    public LiveData<List<OrderWithItems>> getUnDeliveredOrder(String id,boolean delivered){
        if(unDeliveredOrders ==null){
            unDeliveredOrders = db.orderDao().getOrderWithItems(id,false);
        }
        return unDeliveredOrders;
    }

    public void updateOrder(String id,boolean delivered){
        MyRoomDatabase.databaseWriteExecutor.execute(() ->db.orderDao().updateDelivered(id,delivered));
    }

    public void updateUserName(String id,String userName){
        MyRoomDatabase.databaseWriteExecutor.execute(() ->db.userDao().updateUserName(id,userName));
    }
}
