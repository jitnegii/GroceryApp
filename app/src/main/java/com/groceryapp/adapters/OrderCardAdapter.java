package com.groceryapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.groceryapp.R;

public class OrderCardAdapter extends RecyclerView.Adapter<OrderCardAdapter.MyViewHolder> {

    OnCardClickListener listener;

    public OrderCardAdapter(OnCardClickListener listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView shopTitle, shopLoc,totalPrice, totalItems, orderedOn;
        CardView parentLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            this.shopTitle = itemView.findViewById(R.id.shopTitle);
//            this.shopLoc = itemView.findViewById(R.id.shopLoc);
//            this.totalItems = itemView.findViewById(R.id.totalItem);
//            this.orderedOn = itemView.findViewById(R.id.orderedOn);
//            this.totalPrice = itemView.findViewById(R.id.totalPrice);
//            this.parentLayout = itemView.findViewById(R.id.parentLayout);

        }
    }

    public interface OnCardClickListener{
        public void onClick();
    }
}
