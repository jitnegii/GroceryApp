package com.groceryapp.adapters;

import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.groceryapp.R;
import com.groceryapp.database.entities.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShopCardAdapter extends RecyclerView.Adapter<ShopCardAdapter.MyViewHolder> {

    OnCardClickListener listener;
    private final List<User> userList;
    private final Set<String> ids;
    Context context;

    public ShopCardAdapter(Context context, OnCardClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.userList = new ArrayList<>();
        ids = new HashSet<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = userList.get(position);

        if (user == null) {
            userList.remove(position);
            notifyItemRemoved(position);
            notify();
            return;
        }

        holder.shopTitle.setText(user.user_name);
        holder.shopLoc.setText(user.address);

        if (user.photo != null) {
            Glide.with(context)
                    .load(user.photo)
                    .centerCrop()
                    .into(holder.shopImage);
        }


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(user.user_id);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView shopTitle, shopLoc;
        ImageView shopImage;
        CardView parentLayout;


        MyViewHolder(@NonNull View itemView) {
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

    public interface OnCardClickListener {
        public void onClick(String id);
    }

    public void addUsers(List<User> users) {
        userList.clear();
        userList.addAll(users);
        notifyDataSetChanged();
    }


    public void addAndNotify(User user) {
        if (ids.contains(user.user_id))
            return;
        userList.add(user);
        ids.add(user.user_id);
        notifyItemInserted(userList.size() - 1);
        Log.d("ITEM","Added "+user.user_id);
    }

    public void changeAndNotify(User user) {
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            User u = userList.get(i);
            if (u.user_id.equals(user.user_id)) {
                userList.set(i, user);
                notifyItemChanged(i);
                Log.d("ITEM","Changed "+user.user_id);
                break;
            }
        }


    }

}
