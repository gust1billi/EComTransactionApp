package com.example.ecomtransactionapp.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecomtransactionapp.R;

import java.util.List;

public class StubAdapter extends RecyclerView.Adapter<StubAdapter.StubViewHolder> {
    Context ctx;
    List<Stub> stubList;

    public StubAdapter(Context ctx, List<Stub> list) {
        this.ctx = ctx;
        this.stubList = list;
    }

    @NonNull
    @Override
    public StubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext() )
               .inflate( R.layout.rv_stub, parent, false);
       return new StubAdapter.StubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StubViewHolder holder, int position) {
        Stub stub = stubList.get( position );
        holder.s_id.setText( String.valueOf( stub.getId() ));
        holder.s_name.setText( stub.getName() );
        holder.s_qty.setText( R.string.history_qty );
        holder.s_qty.append(" " + stub.getQty() );
        holder.s_price.setText( R.string.history_price);
        holder.s_price.append( " " + stub.getPrice() );
    }

    @Override
    public int getItemCount() {
        return stubList.size();
    }

    public static class StubViewHolder extends RecyclerView.ViewHolder {
        TextView s_id, s_name, s_qty, s_price;

        public StubViewHolder(@NonNull View itemView) {
            super(itemView);

            s_id = itemView.findViewById(R.id.stub_id);
            s_name = itemView.findViewById(R.id.stub_name);
            s_qty = itemView.findViewById(R.id.stub_qty_text);
            s_price = itemView.findViewById(R.id.stub_price_text);
        }
    }

    public void setFilteredList( List<Stub> filteredList ){
        this.stubList = filteredList; notifyDataSetChanged();
    }
}
