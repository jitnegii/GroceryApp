package com.groceryapp.activities;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.groceryapp.R;
import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.Item;
import com.groceryapp.database.entities.User;
import com.groceryapp.models.ItemViewModel;
import com.groceryapp.utility.FirebaseUtils;
import com.groceryapp.viewholders.ItemCardViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShopActivity extends AppCompatActivity {

    TextView shopTitle, shopLoc;
    ImageView searchBtn;
    RecyclerView recyclerView;
    Query query;
    String shopId, shopName, shopLocText, shopPhotoUrl;
    private FirebaseRecyclerAdapter<Item, ItemCardViewHolder> mAdapter;
    ItemViewModel itemViewModel;
    List<String> bucketItemIds = new ArrayList<>();
    GradientDrawable add_color_drawable, remove_color_drawable;
    int btnColor[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        shopId = intent.getStringExtra("shop_id");
        shopName = intent.getStringExtra("shop_name");
        shopLocText = intent.getStringExtra("shop_loc");
        shopPhotoUrl = intent.getStringExtra("shop_photo");

        btnColor = new int[2];
        btnColor[0] = getResources().getColor(R.color.green);
        btnColor[1] = getResources().getColor(R.color.red);

        add_color_drawable = new GradientDrawable();
        add_color_drawable.setCornerRadius(10);
        add_color_drawable.setStroke(2, btnColor[0]);

        remove_color_drawable = new GradientDrawable();
        remove_color_drawable.setCornerRadius(10);
        remove_color_drawable.setStroke(2, btnColor[1]);

        intView();

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        itemViewModel.getBucketItemForShopLive(FirebaseUtils.getUserId(), shopId).observe(this, list -> {
            if (list != null)
                bucketItemIds = list;
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();

//            Log.e("ItemList ", "Changed, List size = " + list.size());

        });

    }

    private void intView() {
        shopTitle = findViewById(R.id.shopTitle);
        shopTitle.setText(shopName);

        shopLoc = findViewById(R.id.shopLoc);
        shopLoc.setText(shopLocText);


        searchBtn = findViewById(R.id.backNav);
        searchBtn.setImageResource(R.drawable.ic_search);
        searchBtn.setClickable(false);
        searchBtn.setFocusable(false);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        query = FirebaseUtils.getDatabaseRef().child("items").child(shopId);

        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Item, ItemCardViewHolder>(options) {

            @Override
            public ItemCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ItemCardViewHolder(inflater.inflate(R.layout.item_card_layout, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(ItemCardViewHolder holder, int position, final Item item) {

                final DatabaseReference shopRef = getRef(position);

                // Set click listener for the whole post view
                item.item_id = Objects.requireNonNull(shopRef.getKey());
//                Log.e("item_id", item.item_id);


                holder.itemName.setText(item.item_name);
                holder.itemPrice.setText("₹"+item.item_price);
                toggleBtn(false, holder.button);

                if (bucketItemIds.contains(item.item_id)) {
                    toggleBtn(true, holder.button);
                }

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bucketItemIds.contains(item.item_id)) {
                            itemViewModel.deleteItem(FirebaseUtils.getUserId(), shopId, item.item_id);
                            toggleBtn(false, holder.button);
                        } else {
                            item.shop_id = shopId;
                            item.modified = System.currentTimeMillis();
                            item.user_id = FirebaseUtils.getUserId();
                            item.shop_name = shopName;
                            itemViewModel.insertItem(item);
                            toggleBtn(true, holder.button);

                        }

                    }
                });
            }

            private void toggleBtn(boolean isAdded, TextView view) {
                if (isAdded) {
                    view.setBackground(remove_color_drawable);
                    view.setText("Remove");
                    view.setTextColor(btnColor[1]);
                } else {
                    view.setBackground(add_color_drawable);
                    view.setText("Add");
                    view.setTextColor(btnColor[0]);
                }
            }
        };

        recyclerView.setAdapter(mAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null)
            mAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        if (mAdapter != null)
            mAdapter.stopListening();
        super.onDestroy();

    }

}
