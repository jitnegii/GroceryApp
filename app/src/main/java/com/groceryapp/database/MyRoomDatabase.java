package com.groceryapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.groceryapp.database.daos.OrderDao;
import com.groceryapp.database.daos.UserDao;
import com.groceryapp.database.entities.Item;
import com.groceryapp.database.entities.Order;
import com.groceryapp.database.entities.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Order.class, Item.class}, version = 1, exportSchema = false)
public abstract class MyRoomDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract OrderDao orderDao();

    private static volatile MyRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MyRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyRoomDatabase.class, "neighborhood_grocery")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public static UserDao getUserDao(Context context){
        return getDatabase(context).userDao();
    }

    public static UserDao getOrderDao(Context context){
        return getDatabase(context).userDao();
    }

}
