package com.softhub.omsai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<FetchedListOfItem> listItems;
    private ProgressDialog progressDialog;
    public TextView notification;
    private CartDatabaseHelper db;
    private ImageView backButton;
    private ImageView cart;

    private String subcategory_name;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(ItemActivity.this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        session = new Session(getApplicationContext());

        Bundle b = getIntent().getExtras();
        if(b!=null){
            subcategory_name = b.getString("subcategory_name");
            TextView header = (TextView) findViewById(R.id.header);
            header.setText(subcategory_name);
        }

        db = new CartDatabaseHelper(getApplicationContext());
        notification = (TextView) findViewById(R.id.notification);
        notification.setText(String.valueOf(db.getCartItemCount()));

        backButton = (ImageView) findViewById(R.id.backButton);
        cart = (ImageView) findViewById(R.id.cart);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listItems = new ArrayList<>();
        adapter = new ContactAdapterItemList(listItems,getApplicationContext());
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CartActivity.class);
                i.putExtra("address", "abc");
                startActivity(i);
            }
        });
        new A().execute();
    }

    class A extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            loadRecyclerViewData();
            return null;
        }
    }

    private void loadRecyclerViewData(){

        String uri = getResources().getString(R.string.base_url)+"item_list.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String data = "Y";
                        try {
                            JSONObject JO = new JSONObject(s);
                            String code = JO.getString("data_code");
                            if(code.equals("200")){
                                JSONArray JA = JO.getJSONArray("item_list");

                                for(int i=0; i<JA.length(); i++){
                                    JSONObject JO1 = JA.getJSONObject(i);
                                    FetchedListOfItem flp = new FetchedListOfItem(JO1.getString("id"),
                                            JO1.getString("name"),
                                            JO1.getString("image_path"),
                                            JO1.getString("rate_type"),
                                            JO1.getString("rate"),
                                            JO1.getString("discount"),
                                            JO1.getString("mrp"),
                                            JO1.getString("description"));
                                    listItems.add(flp);
                                }

                                adapter = new ContactAdapterItemList(listItems,getApplicationContext());
                                recyclerView.setAdapter(adapter);
                                progressDialog.dismiss();

                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(ItemActivity.this, JO.getString("message"), Toast.LENGTH_SHORT).show();
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
            params.put("category_name", subcategory_name);
            params.put("user_type", session.prefs.getString("Utype","NA"));
            return params;
        }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
