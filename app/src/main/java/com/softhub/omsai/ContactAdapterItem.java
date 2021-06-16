package com.softhub.omsai;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapterItem extends RecyclerView.Adapter<ContactAdapterItem.ViewHolder> {

    private List<FetchedListOfItem> listItems;
    private Context context;

    String rupee;
    double total;



    public ContactAdapterItem(List<FetchedListOfItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_subcategory_item, parent, false);


        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FetchedListOfItem listItem = listItems.get(position);

        holder.itemName.setText(listItem.getItemName());
        holder.itemPrice.setText("Rs "+listItem.getItemMrp()+"/"+listItem.getItemSType());
        Picasso.with(context).load(listItem.getItemImage()).into(holder.itemImage);
        int i = Integer.valueOf(listItem.getItemDiscount());
        double j = Double.valueOf(listItem.getItemPrice());
        double k = (j*i)/100;
        j = j+k;
        holder.itemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total = Integer.valueOf(listItem.getItemPrice())*1;

                holder.db.addCart(new Cart(listItem.getItemId(),
                        listItem.getItemName(),
                        listItem.getItemSType(),
                        listItem.getItemImage(),
                        "1",
                        listItem.getItemPrice(),
                        String.valueOf(total)));
                int s = holder.db.getCartItemCount();
                Toast.makeText(context, listItem.getItemName()+" Added to Cart", Toast.LENGTH_SHORT).show();
                ((Home) context).notification.setText(String.valueOf(s));
            }
        });
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fullImageIntent = new Intent(context, FullScreenImage.class);
                fullImageIntent.putExtra("image", listItem.getItemImage());
                fullImageIntent.putExtra("name", listItem.getItemName());
                fullImageIntent.putExtra("description", listItem.getItemDescription());
                context.startActivity(fullImageIntent);
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
        public String Id;
//        public SubCategory v;
        CartDatabaseHelper db;



        public ViewHolder(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemAdd = (TextView) itemView.findViewById(R.id.itemAdd);

            Resources res = itemView.getResources();
            db = new CartDatabaseHelper(context);
//            v = new SubCategory();
            //rupee = res.getString(R.string.rupees_symbol);

        }
    }
}

