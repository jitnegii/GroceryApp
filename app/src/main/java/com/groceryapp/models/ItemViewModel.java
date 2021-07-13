package com.groceryapp.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.Item;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {

    private final MyRoomDatabase db;
    private LiveData<List<Item>> item;
    private LiveData<List<String>> bucketItemIds;

    public ItemViewModel(@NonNull @NotNull Application application) {
        super(application);
        this.db = MyRoomDatabase.getDatabase(application);
    }

    public LiveData<List<String>> getBucketItemForShopLive(String userId, String shopId) {
        if (bucketItemIds == null)
            bucketItemIds = db.itemDao().getBucketItemForShopLive(userId, shopId);
        return bucketItemIds;
    }

    public LiveData<List<Item>> getUserBucketItems(String userId) {
        if (item == null)
            item = db.itemDao().getUserBucketItems(userId);
        return item;
    }

    public void insertItem(Item item) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            db.itemDao().insertItem(item);
        });

    }

    public void deleteItem(String userId, String shopId, String itemId) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            db.itemDao().deleteItem(userId, shopId, itemId);
        });

    }
}
