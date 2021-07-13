package com.groceryapp.viewholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groceryapp.R;

import org.jetbrains.annotations.NotNull;

public class BucketItemViewHolder extends RecyclerView.ViewHolder {

    public TextView item_name,shop_name,item_price, button_positive, button_negative,value;
    public LinearLayout buttons,status;
    public BucketItemViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        item_name = itemView.findViewById( R.id.item_name);
        shop_name = itemView.findViewById( R.id.shop_name);
        item_price = itemView.findViewById( R.id.item_price);
        button_positive = itemView.findViewById( R.id.buy);
        button_negative = itemView.findViewById( R.id.remove);

        buttons = itemView.findViewById(R.id.buttons);
        status = itemView.findViewById(R.id.status);
        value = itemView.findViewById(R.id.value);

    }
}
