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

public class SubCategory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<FetchedListOfItem> listItems;
    private ProgressDialog progressDialog;
    public TextView notification;
    public TextView category_name;
    private CartDatabaseHelper db;
    private ImageView backButton;
    private ImageView cart;

    private String category_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(SubCategory.this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        category_name = (TextView) findViewById(R.id.category_name);
        Bundle b = getIntent().getExtras();
        if(b!=null){
            category_id = b.getString("category_id");
            category_name.setText(b.getString("category_name"));
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
        adapter = new ContactAdapterSubCategory(listItems,getApplicationContext());
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
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

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



        String uri = getResources().getString(R.string.base_url)+"sub_category_list.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String data = "Y";
                        try {
                            JSONObject JO = new JSONObject(s);

                            if(JO.getString("data_code").equals("200")){
                                JSONArray JA = JO.getJSONArray("subcategory_list");

                                for(int i=0; i<JA.length(); i++){
                                    JSONObject JO1 = JA.getJSONObject(i);
                                    FetchedListOfItem flp = new FetchedListOfItem(JO1.getString("subcategory_id"),
                                            JO1.getString("subcategory"),
                                            JO1.getString("userfile"),"","","","","");
                                    listItems.add(flp);
                                }
                                adapter = new ContactAdapterSubCategory(listItems,getApplicationContext());
                                recyclerView.setAdapter(adapter);
                                progressDialog.dismiss();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(SubCategory.this, JO.getString("message"), Toast.LENGTH_LONG).show();
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
            params.put("category_id", category_id);
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
