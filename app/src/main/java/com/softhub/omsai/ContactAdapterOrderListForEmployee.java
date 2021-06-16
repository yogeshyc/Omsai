package com.softhub.omsai;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactAdapterOrderListForEmployee extends RecyclerView.Adapter<ContactAdapterOrderListForEmployee.ViewHolder> {

    private List<FetchedListOfOrderForEmployee> listItems;
    private Context context;

    String order_id,order_id1;
    double total;



    public ContactAdapterOrderListForEmployee(List<FetchedListOfOrderForEmployee> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_list_item, parent, false);


        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FetchedListOfOrderForEmployee listItem = listItems.get(position);

        holder.order_id.setText("ऑर्डर आयडी: "+listItem.getOrder_id());
        holder.order_date.setText("ऑर्डर तारीख: "+listItem.getOrder_date());
        holder.order_customer.setText(listItem.getOrder_customer());
        holder.order_address.setText(listItem.getOrder_address());
        holder.order_amount.setText("बिल रक्कम: "+listItem.getOrder_amount());

        holder.order_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listItem.getOrder_lat().equals("")){
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:"
                            + listItem.getOrder_lat() + "," + listItem.getOrder_long()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Only if initiating from a Broadcast Receiver
                    String mapsPackageName = "com.google.android.apps.maps";
                    if (isPackageExisted(context, mapsPackageName)) {
                        i.setClassName(mapsPackageName, "com.google.android.maps.MapsActivity");
                        i.setPackage(mapsPackageName);
                    }else{
                        Toast.makeText(context, "Google Maps Not Found", Toast.LENGTH_SHORT).show();
                    }
                    context.startActivity(i);
                }else{
                    Toast.makeText(context, "Location Not Attached", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.order_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+listItem.getOrder_mobile()));
                context.startActivity(intent);
            }
        });

        holder.order_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order_id=listItem.getOrder_id();
                orderComplete();
            }
        });

        holder.viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* order_id1=listItem.getOrder_id();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setData(Uri.parse("http://meradaftar.com/fasty/index.php/home/order_list"));
                intent.setData(Uri.parse("http://meradaftar.com/fasty/index.php/Order/order_list_app/"+order_id1));
                //intent.setData(Uri.parse("http://meradaftar.com/fasty/index.php/Order/order_list_app/97659376002368"));
                context.startActivity(intent);*/
               //order_id1=listItem.getOrder_id();
                Intent intent = new Intent(context,DeliveryOrderDetails.class);
                intent.putExtra("order_id",listItem.getOrder_id());
                //intent.putExtra("order_id",order_id1);
                context.startActivity(intent);
               /*Intent intent = new Intent(context,DeliveryOrderDetails.class);
               context.startActivity(intent);*/
            }
        });

    }

    private void orderComplete() {

        String uri = context.getResources().getString(R.string.base_url)+"order_complete.php";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String data = "Y";
                        try {
                            JSONObject JO = new JSONObject(s);

                            if(JO.getString("data_code").equals("200")){
                                Toast.makeText(context, JO.getString("message"), Toast.LENGTH_SHORT).show();
                                ((EmployeePage) context).loadMealOffers();
                            }else{
                                Toast.makeText(context, JO.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Toast.makeText(getContext(), "Error.Response",Toast.LENGTH_SHORT).show();
                    }
                }){@Override
        public Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("order_id", order_id);
            return params;
        }
        };
        queue.add(stringRequest);

    }

    private static boolean isPackageExisted(Context context, String targetPackage){
        PackageManager pm=context.getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView order_id;
        public TextView order_date;
        public TextView order_customer;
        public TextView order_address;
        public TextView order_amount;
        public TextView order_complete;
        public TextView viewOrder;



        public ViewHolder(View itemView) {
            super(itemView);

            order_id = (TextView) itemView.findViewById(R.id.order_id);
            order_date = (TextView) itemView.findViewById(R.id.order_date);
            order_customer = (TextView) itemView.findViewById(R.id.order_customer);
            order_address = (TextView) itemView.findViewById(R.id.order_address);
            order_amount = (TextView) itemView.findViewById(R.id.order_amount);
            order_complete = (TextView) itemView.findViewById(R.id.order_complete);
            viewOrder = (TextView) itemView.findViewById(R.id.view_order_details);

            //rupee = res.getString(R.string.rupees_symbol);

        }
    }
}

