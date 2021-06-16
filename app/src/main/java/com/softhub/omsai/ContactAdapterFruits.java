package com.softhub.omsai;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ContactAdapterFruits extends RecyclerView.Adapter<ContactAdapterFruits.ViewHolder> {

    private List<FetchedListOfItem> listItems;
    private Context context;

    String rupee;
    double total;



    public ContactAdapterFruits(List<FetchedListOfItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_category, parent, false);


        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FetchedListOfItem listItem = listItems.get(position);


        holder.itemName.setText(listItem.getItemName());
//        holder.itemPrice.setText("Rs "+listItem.getItemPrice()+"/"+listItem.getItemSType());
        if(!listItem.getItemImage().equals("")){
            Picasso.with(context).load(listItem.getItemImage()).into(holder.itemImage);
        }

//        int i = Integer.valueOf(listItem.getItemDiscount());
//        double j = Double.valueOf(listItem.getItemPrice());
//        double k = (j*i)/100;
//        j = j+k;
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SubCategory.class);
                i.putExtra("category_id", listItem.getItemId());
                i.putExtra("category_name", listItem.getItemName());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemName;
        public ImageView itemImage;
        public TextView itemPrice;
        public TextView itemAdd;
        public LinearLayout layout;
        public String Id;
        public Fruits f;
        CartDatabaseHelper db;



        public ViewHolder(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemAdd = (TextView) itemView.findViewById(R.id.itemAdd);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);

            f = new Fruits();
            Resources res = itemView.getResources();
            db = new CartDatabaseHelper(context);

            //rupee = res.getString(R.string.rupees_symbol);

        }
    }
}

