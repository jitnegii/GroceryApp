package com.groceryapp.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.groceryapp.R;
import com.groceryapp.database.entities.Item;
import com.groceryapp.utility.FirebaseUtils;
import com.groceryapp.viewholders.BucketItemViewHolder;

import org.jetbrains.annotations.NotNull;

public class RequestFragment extends Fragment {

    Query query;
    View view;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    private FirebaseRecyclerAdapter<Item, BucketItemViewHolder> mAdapter;
    GradientDrawable accept_drawable, decline_drawable, dull_drawable;
    int btnColor[];

    public RequestFragment() {
        // Required empty public constructor
    }

    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
        return fragment;
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
            view = inflater.inflate(R.layout.fragment_request, container, false);
            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnColor = new int[3];
        btnColor[0] = getResources().getColor(R.color.green);
        btnColor[1] = getResources().getColor(R.color.red);
        btnColor[2] = getResources().getColor(R.color.dull);

        accept_drawable = new GradientDrawable();
        accept_drawable.setCornerRadius(10);
        accept_drawable.setStroke(2, btnColor[0]);

        decline_drawable = new GradientDrawable();
        decline_drawable.setCornerRadius(10);
        decline_drawable.setStroke(2, btnColor[1]);

        dull_drawable = new GradientDrawable();
        dull_drawable.setCornerRadius(10);
        dull_drawable.setStroke(2, btnColor[2]);

        manager = new LinearLayoutManager(requireActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        query = FirebaseUtils.getDatabaseRef().child("requests").child("shops").child(FirebaseUtils.getUserId());

        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Item, BucketItemViewHolder>(options) {

            @Override
            public BucketItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new BucketItemViewHolder(inflater.inflate(R.layout.order_card_layout, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(BucketItemViewHolder holder, int position, final Item item) {

                Log.e("item_id", item.modified + "");


                holder.item_name.setText(item.item_name);
                holder.item_price.setText("₹"+item.item_price);

                holder.shop_name.setText(item.user_name);

                holder.button_positive.setText("Accept");
                holder.button_positive.setTextColor(btnColor[0]);
                holder.button_negative.setBackground(accept_drawable);

                holder.button_negative.setText("Decline");
                holder.button_negative.setTextColor(btnColor[1]);
                holder.button_negative.setBackground(decline_drawable);

                holder.button_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateStatusAccepted(item);
                    }
                });

                if (item.status.equals("Accepted")) {
                    holder.button_positive.setText("Delivered");
                    holder.button_negative.setClickable(false);
                    holder.button_negative.setTextColor(btnColor[2]);
                    holder.button_negative.setBackground(dull_drawable);
                    holder.button_positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateStatusDelivered(item);
                        }
                    });
                }


            }

        };

        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null)
            mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null)
            mAdapter.stopListening();
    }

    void updateStatusDelivered(Item item) {


        String key = String.valueOf(item.modified);

        FirebaseUtils.getDatabaseRef().child("order_history")
                .child("users")
                .child(item.user_id)
                .child(key)
                .child("status")
                .setValue("Delivered");

        item.status = "Delivered";

        FirebaseUtils.getDatabaseRef().child("order_history")
                .child("shops")
                .child(item.shop_id)
                .child(key)
                .setValue(item);

        FirebaseUtils.getDatabaseRef().child("requests")
                .child("shops")
                .child(item.shop_id)
                .child(key)
                .removeValue();
    }

    void updateStatusAccepted(Item item) {

        String key = String.valueOf(item.modified);

        FirebaseUtils.getDatabaseRef().child("requests")
                .child("shops")
                .child(item.shop_id)
                .child(key)
                .child("status")
                .setValue("Accepted");

        FirebaseUtils.getDatabaseRef().child("order_history")
                .child("users")
                .child(item.user_id)
                .child(key)
                .child("status")
                .setValue("Accepted");

    }

}