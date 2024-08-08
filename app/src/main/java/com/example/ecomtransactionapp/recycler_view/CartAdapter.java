package com.example.ecomtransactionapp.recycler_view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecomtransactionapp.R;
import com.example.ecomtransactionapp.TransactionActivity;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private static final String TAG = "CartAdapter";

    Context ctx;

    List<Cart> cartList;
    List<String> inputTitle;

    public CartAdapter(Context ctx, List<Cart> cartList){
        this.ctx = ctx;
        this.cartList = cartList;
    }

    public CartAdapter(Context ctx, List<Cart> cartList, List<String> titleList) {
        this.ctx = ctx;
        this.cartList = cartList;
        this.inputTitle = titleList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_rv_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart item = cartList.get( position );
        holder.name.setText( item.getName() );
        holder.price.setText( item.getPrice() );
        holder.qty.setText( String.valueOf(item.getQuantity()) );

        holder.name.setOnClickListener(view -> {
            ((TransactionActivity)ctx).setQuantityDialog(
                    holder.name.getText().toString(),
                    holder.qty.getText().toString(),
                    position
            );
        });

        holder.qty.setOnClickListener(view -> {
            ((TransactionActivity)ctx).setQuantityDialog(
                    holder.name.getText().toString(),
                    holder.qty.getText().toString(),
                    position
            );
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void newItemAdded(int positionStack) {
        notifyItemInserted( positionStack );
        notifyItemRangeChanged(0, positionStack);
    }

    public void itemAdded(int positionStack) {
        Log.e(TAG, "size: " + cartList.size() );

        notifyItemChanged( positionStack );
        notifyItemRangeChanged(0, positionStack);
        // CHECK IF CART ALREADY HAVE THE ITEM
        // IF ITEM EXIST IN THE POSITION OF X
    }

    public void itemUpdated(int positionStack) {
        notifyItemChanged( positionStack );
    }

    public void itemRemoved(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged( position -1, cartList.size() );
    }

    public void itemPurged(int lastPosition){
        notifyItemRangeRemoved(0, lastPosition);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, qty;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cart_nama_produk);
            price = itemView.findViewById(R.id.cart_harga_produk);
            qty = itemView.findViewById(R.id.qty);
        }
    }
}
