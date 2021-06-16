package com.softhub.omsai;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterOrderDetails extends RecyclerView.Adapter<AdapterOrderDetails.ViewHolder> {

    private List<FetchedOrderDetails> listItems;
    private Context context;

    public AdapterOrderDetails(List<FetchedOrderDetails> listItems, Context context) {
        this.listItems = (List<FetchedOrderDetails>) listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_details, parent, false);


        context = (Context) parent.getContext();
        return new AdapterOrderDetails.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final FetchedOrderDetails listItem = listItems.get(position);

       /* holder.cartId.setText(listItem.getCartId());
        holder.id.setText(listItem.getId());*/
        holder.name.setText("Product Name :"+listItem.getName());
        holder.SaleType.setText("Rate Type :"+listItem.getSaleType());
        holder.qty.setText(listItem.getQty());
        holder.price.setText("Price :"+listItem.getPrice());
        // holder.total.setText(listItem.getTotal());

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView name;
        public TextView SaleType;
        public TextView qty;
        public TextView price;
        /*public TextView total;
        public TextView cartId;
        public TextView id;*/

        public ViewHolder(View itemView) {
            super(itemView);


            name = (TextView) itemView.findViewById(R.id.item_name);
            SaleType = (TextView) itemView.findViewById(R.id.SaleType);
            qty = (TextView) itemView.findViewById(R.id.qty);
            price = (TextView) itemView.findViewById(R.id.price);
           /* total = (TextView) itemView.findViewById(R.id.total);
             id= (TextView) itemView.findViewById(R.id.id);
            cartId = (TextView) itemView.findViewById(R.id.cart_id);*/
        }
    }
}
