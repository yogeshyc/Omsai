package com.softhub.omsai;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryOrderDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterOrderDetails adapter;
    private List<FetchedOrderDetails> listItems;
    private String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_order_details);

        /*Bundle b = getIntent().getExtras();
        if(b!=null){
            order_id = b.getString("order_id");
        }*/
        Intent intent = getIntent();
        order_id = getIntent().getStringExtra("order_id");

        recyclerView = (RecyclerView) findViewById(R.id.show_orders_details);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listItems = new ArrayList<>();

        adapter  = new AdapterOrderDetails(listItems,getApplicationContext());
        recyclerView.setAdapter(adapter);
       /* adapter = new AdapterOrderDetails(listItems,getApplicationContext());
        recyclerView.setAdapter(adapter);*/
        new A().execute();
    }

    class A extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            loadRecyclerViewData();
            return null;
        }
    }

    private void loadRecyclerViewData(){

        String uri = getResources().getString(R.string.base_url)+"order_details.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String data = "Y";
                        try {
                            //s = s.replace("[]"","");
                            JSONObject JO = new JSONObject(s);
                            String code = JO.getString("data_code");
                            if(code.equals("200")){

                                //System.out.println("Hi");

                                JSONArray JA = JO.getJSONArray("order");


                                for(int i=0; i<JA.length(); i++){
                                    JSONObject JO1 = JA.getJSONObject(i);
                                    FetchedOrderDetails flp = new FetchedOrderDetails(

                                           /* JO1.getString("cartId"),
                                            JO1.getString("id"),*/
                                            JO1.getString("name"),
                                            JO1.getString("SaleType"),
                                            JO1.getString("qty"),
                                            JO1.getString("price"));
                                    listItems.add(flp);
                                }

                                adapter = new AdapterOrderDetails(listItems,getApplicationContext());
                                recyclerView.setAdapter(adapter);

                            }else{
                                Toast.makeText(DeliveryOrderDetails.this, JO.getString("message"), Toast.LENGTH_SHORT).show();
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


}

