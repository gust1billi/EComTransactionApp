package com.example.ecomtransactionapp.recycler_view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends
        RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    Context ctx;
    List<Transaction> transactionList;

    public TransactionAdapter(Context ctx, List<Transaction> list) {
        this.ctx = ctx;
        this.transactionList = list;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
