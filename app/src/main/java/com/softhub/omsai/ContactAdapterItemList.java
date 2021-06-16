package com.softhub.omsai;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapterItemList extends RecyclerView.Adapter<ContactAdapterItemList.ViewHolder> {

    private List<FetchedListOfItem> listItems;
    private Context context;

    String rupee;
    double total;



    public ContactAdapterItemList(List<FetchedListOfItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);


        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FetchedListOfItem listItem = listItems.get(position);

        holder.itemName.setText(listItem.getItemName());
        holder.itemPrice.setText("Rs "+listItem.getItemMrp()+"/"+listItem.getItemSType());
        Picasso.with(context).load(listItem.getItemImage()).into(holder.itemImage);
        total = Integer.valueOf(listItem.getItemMrp())*Integer.valueOf(holder.itemQuantity.getText().toString());
        holder.itemTotal.setText("Total: Rs "+String.valueOf(total));
        int i = Integer.valueOf(listItem.getItemDiscount());
        double j = Double.valueOf(listItem.getItemPrice());
        //double k = (j*i)/100;
        //j = j+k;
        if(i>0){
            holder.itemDiscount.setText("Rs "+j);
            holder.itemDiscountRate.setText("("+listItem.getItemDiscount()+" % OFF)");
            holder.itemDiscount.setPaintFlags(holder.itemDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.itemDiscount.setVisibility(View.GONE);
            holder.itemDiscountRate.setVisibility(View.GONE);
        }

        holder.itemQuantityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int j = Integer.valueOf(editable.toString());
                    holder.itemQuantity.setText(String.valueOf(j));
                    total = Integer.valueOf(listItem.getItemMrp())*j;
                    holder.itemTotal.setText("Total: Rs "+total);
                }catch (Exception e){

                }
            }
        });


        holder.decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int j = Integer.valueOf(holder.itemQuantity.getText().toString());
                if(j==1){}
                else{
                    j--;
                    holder.itemQuantity.setText(String.valueOf(j));
                    total = Integer.valueOf(listItem.getItemPrice())*j;
                    holder.itemTotal.setText("Total: Rs "+String.valueOf(total));
                }
            }
        });
        holder.increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int j = Integer.valueOf(holder.itemQuantity.getText().toString());
                j++;
                holder.itemQuantity.setText(String.valueOf(j));
                total = Integer.valueOf(listItem.getItemPrice())*j;
                holder.itemTotal.setText("Total: Rs "+String.valueOf(total));
            }
        });
        holder.itemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.itemQuantityET.getText().toString().equals("")){
                    Toast.makeText(context, "Enter Quantity", Toast.LENGTH_SHORT).show();
                }else{
                    total = Integer.valueOf(listItem.getItemPrice())*Integer.valueOf(holder.itemQuantityET.getText().toString());

                    holder.db.addCart(new Cart(listItem.getItemId(),
                            listItem.getItemName(),
                            listItem.getItemSType(),
                            listItem.getItemImage(),
                            holder.itemQuantity.getText().toString(),
                            listItem.getItemPrice(),
                            String.valueOf(total)));
                    int s = holder.db.getCartItemCount();
                    Toast.makeText(context, listItem.getItemName()+" Added to Cart", Toast.LENGTH_SHORT).show();
                    ((ItemActivity) context).notification.setText(String.valueOf(s));
                }

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
        public TextView itemPrice;
        public TextView itemDiscount;
        public TextView itemDiscountRate;
        public ImageView itemImage;
        public CardView itemInfo;
        public String Id;
        public TextView itemQuantity;
        public TextView itemTotal;
        public TextView increment;
        public TextView decrement;
        public TextView itemAdd;
        public EditText itemQuantityET;
        public SubCategory v;
        CartDatabaseHelper db;



        public ViewHolder(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            itemDiscount = (TextView) itemView.findViewById(R.id.itemDiscount);
            itemDiscountRate = (TextView) itemView.findViewById(R.id.itemDiscountRate);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemQuantity = (TextView) itemView.findViewById(R.id.itemQuantity);
            itemTotal = (TextView) itemView.findViewById(R.id.itemTotal);
            increment = (TextView) itemView.findViewById(R.id.increment);
            decrement = (TextView) itemView.findViewById(R.id.decrement);
            itemAdd = (TextView) itemView.findViewById(R.id.itemAdd);
            itemQuantityET = (EditText) itemView.findViewById(R.id.itemQuantityET);

            Resources res = itemView.getResources();
            db = new CartDatabaseHelper(context);
            v = new SubCategory();
            //rupee = res.getString(R.string.rupees_symbol);

        }
    }
}

