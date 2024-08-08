package com.example.ecomtransactionapp.recycler_view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecomtransactionapp.R;
import com.example.ecomtransactionapp.TransactionActivity;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.productViewHolder> {

    private final Context ctx;
    private List<Product> productList;

    public ProductAdapter(Context ctx, List<Product> cart) {
        this.ctx = ctx;
        this.productList = cart;
    }

    @NonNull
    @Override
    public productViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_rv_layout, parent, false);
        return new ProductAdapter.productViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull productViewHolder holder, int position) {
        Product product = productList.get(position);

        Glide.with(ctx).load(Uri.parse( product.getImageUri( ) ))
                .placeholder(R.drawable.ic_baseline_image_24 )
                .into( holder.productImage );
        holder.price.setText( product.getPrice() );
        holder.name.setText( product.getProductName() );

        holder.itemView.setOnClickListener( view ->
                ( (TransactionActivity)ctx).addToCart( product.getProductName(), product.getPrice() ) );
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class productViewHolder extends RecyclerView.ViewHolder {
        TextView name, price; ImageView productImage;

        public productViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_title);
            price = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }

    public void setFilteredList( List<Product> filteredList ){
        this.productList = filteredList; notifyDataSetChanged();
    }
}
