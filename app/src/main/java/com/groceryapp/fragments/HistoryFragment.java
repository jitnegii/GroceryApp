package com.groceryapp.fragments;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.groceryapp.R;
import com.groceryapp.database.entities.Item;
import com.groceryapp.utility.FirebaseUtils;
import com.groceryapp.viewholders.BucketItemViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HistoryFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    private FirebaseRecyclerAdapter<Item, BucketItemViewHolder> mAdapter;
    Query query;
    String key;

    public HistoryFragment(String key) {
        this.key = key;
    }

    public HistoryFragment() {

    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
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
            view = inflater.inflate(R.layout.fragment_history, container, false);
            initView(view);
        }

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manager = new LinearLayoutManager(requireActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        query = FirebaseUtils.getDatabaseRef().child("order_history").child(key).child(FirebaseUtils.getUserId());

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

                final DatabaseReference shopRef = getRef(position);

                // Set click listener for the whole post view
                item.item_id = Objects.requireNonNull(shopRef.getKey());
                Log.e("item_id", item.item_id);

                holder.buttons.setVisibility(View.GONE);
                holder.status.setVisibility(View.VISIBLE);

                holder.item_name.setText(item.item_name);
                holder.item_price.setText("₹"+item.item_price);

                if (key.equals("users"))
                    holder.shop_name.setText(item.shop_name);
                else
                    holder.shop_name.setText(item.user_name);

                holder.value.setText(item.status);

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
}