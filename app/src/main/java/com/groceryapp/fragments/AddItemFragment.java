package com.groceryapp.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.groceryapp.R;
import com.groceryapp.database.entities.Item;
import com.groceryapp.utility.FirebaseUtils;
import com.groceryapp.viewholders.ItemCardViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class AddItemFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager mManager;
    FirebaseRecyclerAdapter<Item, ItemCardViewHolder> mAdapter;
    GradientDrawable red_outline;
    DatabaseReference database;
    int colorId;
    View view;
    AlertDialog addItemDialog;

    FloatingActionButton fab;

    public AddItemFragment() {
        // Required empty public constructor
    }

    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
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
            view = inflater.inflate(R.layout.fragment_add_item, container, false);
            initView(view);
        }

        return view;
    }

    private void initView(View view) {

        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemDialog = createDialog();
            }
        });



    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mManager = new LinearLayoutManager(requireActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);

        database = FirebaseUtils.getDatabaseRef().child("items").child(FirebaseUtils.getUserId());
        colorId = getResources().getColor(R.color.red);

        red_outline = new GradientDrawable();
        red_outline.setCornerRadius(10);
        red_outline.setStroke(2, colorId);

        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(database, Item.class)
                .build();

        // Set click listener for the whole post view
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
                Log.e("item_id", item.item_id);


                holder.itemName.setText(item.item_name);
                holder.itemPrice.setText("₹"+item.item_price);
                holder.button.setTextColor(colorId);
                holder.button.setText(R.string.remove);
                holder.button.setBackground(red_outline);

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteItemFromDatabase(item.item_id);
                    }
                });
            }

        };

        recyclerView.setAdapter(mAdapter);

    }

    public AlertDialog createDialog(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_product,null);
        final EditText item_name = (EditText)mView.findViewById(R.id.item_name);
        final EditText item_price = (EditText)mView.findViewById(R.id.item_price);
        final CardView addBtn =  mView.findViewById(R.id.add);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = item_name.getText().toString();
                String price = item_price.getText().toString();

                if(name.isEmpty() || price.isEmpty()){
                    Toast.makeText(requireActivity(),"Invalid Input",Toast.LENGTH_SHORT).show();
                }else {
                    addItemToDatabase(name,price);
                }
                alertDialog.dismiss();
            }
        });


        alertDialog.show();
        return alertDialog;
    }

    private void addItemToDatabase(String name,String price){
        Item item = new Item();
        item.item_name = name;
        item.item_price = price;

        database.push().setValue(item, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                if(error==null){
                    Toast.makeText(requireActivity(),"Product Added",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(requireActivity(),"Failed to Added",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteItemFromDatabase(String key){


        database.child(key).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                if(error==null){
                    Toast.makeText(requireActivity(),"Product Removed",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(requireActivity(),"Failed to Remove",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if(addItemDialog!=null)
            addItemDialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
        super.onDestroy();
    }
}