package com.groceryapp.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groceryapp.R;

public class ItemCardViewHolder extends RecyclerView.ViewHolder {

    public TextView itemName, itemPrice, button;


    public ItemCardViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemName = itemView.findViewById(R.id.item_title);
        this.itemPrice = itemView.findViewById(R.id.item_price);
        this.button = itemView.findViewById(R.id.is_added);

    }

}