package com.groceryapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.groceryapp.R;
import com.groceryapp.activities.MapActivity;
import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.Item;
import com.groceryapp.database.entities.User;
import com.groceryapp.listeners.RequestListener;
import com.groceryapp.models.ItemViewModel;
import com.groceryapp.models.UserViewModel;
import com.groceryapp.utility.FirebaseUtils;
import com.groceryapp.viewholders.BucketItemViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class BucketFragment extends Fragment implements RequestListener {

    View view;
    ItemViewModel itemViewModel;
    UserViewModel userViewModel;
    BucketAdapter adapter;
    RecyclerView recyclerView;
    DatabaseReference reference;

    public BucketFragment() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_bucket, container, false);
            initView(view);
        }

        itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        itemViewModel.getUserBucketItems(FirebaseUtils.getUserId()).observe(requireActivity(), list -> {

            if (list != null) {
                adapter = new BucketAdapter(requireActivity(), this, list);

                recyclerView.setAdapter(adapter);
                Log.e("ItemList ", "Changed, List size bucket = " + list.size());
            }
        });
        reference = FirebaseUtils.getDatabaseRef();

        return view;
    }

    private void initView(View view) {

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

    }

    @Override
    public void remove(Item item) {
        itemViewModel.deleteItem(item.user_id, item.shop_id, item.item_id);
    }

    @Override
    public void buy(Item item) {

        item.modified = System.currentTimeMillis();
        String time = String.valueOf(item.modified);

        User user = MyRoomDatabase.getUserDao(requireActivity()).getUser(item.user_id);

        if (user.address == null || user.address.isEmpty()) {
            new AlertDialog.Builder(requireActivity())
                    .setMessage("Address required before ordering")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(requireActivity(), MapActivity.class);
                            startActivity(intent);

                        }
                    }).show();
            return;
        }


        item.latitude = user.latitude;
        item.longitude = user.longitude;
        item.user_name = user.user_name;
        item.status = "Pending";

        reference.child("requests")
                .child("shops")
                .child(item.shop_id)
                .child(time)
                .setValue(item);

        reference.child("order_history")
                .child("users")
                .child(item.user_id)
                .child(time)
                .setValue(item);

        itemViewModel.deleteItem(item.user_id, item.shop_id, item.item_id);
    }


    public static class BucketAdapter extends RecyclerView.Adapter<BucketItemViewHolder> {

        List<Item> itemList;
        GradientDrawable buy_color_drawable, remove_color_drawable;
        int[] btnColor;
        RequestListener listener;

        public BucketAdapter(Context context, RequestListener listener, List<Item> items) {
            this.listener = listener;
            itemList = items;
            btnColor = new int[2];
            btnColor[0] = context.getResources().getColor(R.color.green);
            btnColor[1] = context.getResources().getColor(R.color.red);

            buy_color_drawable = new GradientDrawable();
            buy_color_drawable.setCornerRadius(10);
            buy_color_drawable.setStroke(2, btnColor[0]);

            remove_color_drawable = new GradientDrawable();
            remove_color_drawable.setCornerRadius(10);
            remove_color_drawable.setStroke(2, btnColor[1]);
        }

        @NonNull
        @NotNull
        @Override
        public BucketItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.order_card_layout, parent, false);

            return new BucketItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull BucketItemViewHolder holder, int position) {
            Item item = itemList.get(position);

            Log.e("item_id", item.item_name);
            holder.item_name.setText(item.item_name);
            holder.item_price.setText("₹"+item.item_price);
            holder.shop_name.setText(item.shop_name);
            holder.button_positive.setBackground(buy_color_drawable);
            holder.button_positive.setTextColor(btnColor[0]);
            holder.button_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.buy(item);
                }
            });

            holder.button_negative.setBackground(remove_color_drawable);
            holder.button_negative.setTextColor(btnColor[1]);
            holder.button_negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.remove(item);
                }
            });

        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public void insertItems(final List<Item> items) {
            itemList.clear();
            itemList = items;
            notifyDataSetChanged();
        }

    }


}