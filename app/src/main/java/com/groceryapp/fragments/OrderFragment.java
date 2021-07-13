package com.groceryapp.fragments;

import android.content.Intent;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.groceryapp.R;
import com.groceryapp.activities.ShopActivity;
import com.groceryapp.adapters.ShopCardAdapter;
import com.groceryapp.database.entities.User;
import com.groceryapp.utility.FirebaseUtils;

public class OrderFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<User, ShopCardViewHolder> mAdapter;
    ShopCardAdapter adapter;
    ChildEventListener mChildEventListener;
    Query query;

    public OrderFragment() {
        // Required empty public constructor
    }

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            initView(view);
        }

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        query = FirebaseUtils.getDatabaseRef().child("shops");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<User, ShopCardViewHolder>(options) {

            @Override
            public ShopCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ShopCardViewHolder(inflater.inflate(R.layout.shop_layout, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(ShopCardViewHolder holder, int position, final User user) {
                final DatabaseReference shopRef = getRef(position);

                // Set click listener for the whole post view
                final String shopKey = shopRef.getKey();
                Log.e("Shop_id", shopKey);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailFragment
                        Intent intent = new Intent(requireContext(), ShopActivity.class);
                        intent.putExtra("shop_id", shopKey);
                        intent.putExtra("shop_name", user.user_name);
                        intent.putExtra("shop_loc", user.address);
                        intent.putExtra("shop_photo", user.photo);
                        startActivity(intent);

                    }
                });

                holder.shopTitle.setText(user.user_name);
                holder.shopLoc.setText(user.address);
                loadImage(user.photo, holder.shopImage, 3);

            }
        };

        recyclerView.setAdapter(mAdapter);

    }

    void loadImage(String url, ImageView view, int tryLeft) {
        Glide.with(requireActivity())
                .load(url)
                .centerCrop()
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loadImage(url, view, tryLeft - 1);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })

                .into(view);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
        super.onDestroy();
    }

    static class ShopCardViewHolder extends RecyclerView.ViewHolder {

        TextView shopTitle, shopLoc;
        ImageView shopImage;
        CardView parentLayout;


        ShopCardViewHolder(@NonNull View itemView) {
            super(itemView);
            this.shopTitle = itemView.findViewById(R.id.shopTitle);
            this.shopLoc = itemView.findViewById(R.id.shopLoc);
            this.shopImage = itemView.findViewById(R.id.shopImage);
            this.parentLayout = itemView.findViewById(R.id.parent_layout);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                shopImage.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), (view.getHeight() + 15), 15f);
                    }
                });
            }

            shopImage.setClipToOutline(true);
        }

    }
}