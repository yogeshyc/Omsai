package com.softhub.omsai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import java.util.Random;

public class CartActivity extends AppCompatActivity {

    private CartDatabaseHelper db;
    public JSONObject JObject;
    public JSONArray jsonArray;
    public List cartItemList;
    private List<FetchedListOfCartItem> listItems;
    public List<Cart> cart;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private double totalItemCart = 0;
    private double totalWalletAmount = 0;

    private TextView subtotal;
    private TextView grandtotal;
    private TextView selected_address;
    private Button select_address;
    private ImageView backButton;
    private TextView checkout;
    private TextView continue_shopping;
    private TextView wallet_amount;
    private CheckBox walletCheckBox;

    private String address;
    private String mobile;
    private String walletAmountUsed="NA";
    private Session session;
    public Bundle b;

    private int oid;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(CartActivity.this);
        progressDialog.setMessage("Please Wait...");

        updateCartList();

        selected_address = (TextView) findViewById(R.id.selected_address);
        select_address = (Button) findViewById(R.id.select_address);
        checkout = (TextView) findViewById(R.id.checkout);
        continue_shopping = (TextView) findViewById(R.id.continue_shopping);
        wallet_amount = (TextView) findViewById(R.id.wallet_amount);
        backButton = (ImageView) findViewById(R.id.backButton);
        walletCheckBox = (CheckBox) findViewById(R.id.walletCheckBox);

        walletCheckBox.setEnabled(false);

        walletCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(totalItemCart>499){
                    if(walletCheckBox.isChecked()){
                        if(totalItemCart > totalWalletAmount){
                            Double newAmount = totalItemCart - totalWalletAmount;
                            grandtotal.setText("Rs. "+String.valueOf(newAmount));
                            walletAmountUsed = totalWalletAmount+"";
                        }else{
                            Double newAmount = totalWalletAmount - totalItemCart;
                            wallet_amount.setText("Rs. "+String.valueOf(newAmount));
                            grandtotal.setText("Rs. "+"0.00");
                            walletAmountUsed = String.valueOf(totalItemCart);
                        }

                    }else{
                        grandtotal.setText("Rs. "+String.valueOf(totalItemCart));
                        wallet_amount.setText("Rs. "+String.valueOf(totalWalletAmount));
                    }
                }else{
                    walletCheckBox.setChecked(false);
                    Toast.makeText(CartActivity.this, "Your purchase is less than 500", Toast.LENGTH_SHORT).show();
                }
            }
        });


        session = new Session(getApplicationContext());
        mobile = session.prefs.getString("Mbl", null);

        b = new Bundle();
        b = getIntent().getExtras();
        address = b.getString("address");

        Random rand = new Random();
        oid = rand.nextInt(9999) + 1000;

        if(address.equals("abc")){
            //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
        }else{
            selected_address.setText(address);
            //Toast.makeText(getApplicationContext(), address+"K", Toast.LENGTH_SHORT).show();
        }



        //Log.d("CART", cartItemList.toString());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        continue_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(session.loggedin()){
                    Intent i = new Intent(getApplicationContext(), AddressSelect.class);
                    i.putExtra("flag", "cart");
                    startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(), "Please Login First or sign up",Toast.LENGTH_SHORT).show();
                }
            }
        });


        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(session.loggedin()){
                    if(db.getCartItemCount()<1){
                        Toast.makeText(getApplicationContext(), "No Items in Cart",Toast.LENGTH_SHORT).show();
                    }else{
                        if(totalItemCart<100){
                            Toast.makeText(getApplicationContext(), "Minimum Order Rs: 100", Toast.LENGTH_SHORT).show();
                        }else{
                            if(address.equals("abc")){
                                if(session.loggedin()){
                                    Intent i = new Intent(getApplicationContext(), AddressSelect.class);
                                    i.putExtra("flag", "cart");
                                    startActivity(i);
                                }else{
                                    Toast.makeText(getApplicationContext(), "Please Login First or sign up",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                addToCart();
                                //Toast.makeText(getApplicationContext(), address+"K", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Please Login First or sign up",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void updateCartList() {
        subtotal = (TextView) findViewById(R.id.subtotal);
        grandtotal = (TextView) findViewById(R.id.grandtotal);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listItems = new ArrayList<>();
        adapter = new ContactAdapter(listItems,getApplicationContext());
        recyclerView.setAdapter(adapter);
        db = new CartDatabaseHelper(getApplicationContext());
        JObject = new JSONObject();
        jsonArray = new JSONArray();
        cartItemList = new ArrayList();

        cart = db.getAllCart();
        totalItemCart = 0;
        for(Cart c: cart){

            FetchedListOfCartItem flp = new FetchedListOfCartItem(c.getId(), c.getItem_id(),c.getItem_name(),c.getItem_image(),
                    c.getItem_saleType(),c.getItem_quantity(),c.getItem_price(), c.getItem_total());
            listItems.add(flp);
            totalItemCart = totalItemCart + (Double.parseDouble(c.getItem_total()));
            try {
                JObject.put("cartId", c.getId());
                JObject.put("id",c.getItem_id());
                JObject.put("name", c.getItem_name());
                JObject.put("Image", c.getItem_image());
                JObject.put("SaleType", c.getItem_saleType());
                JObject.put("qty", c.getItem_quantity());
                JObject.put("price", c.getItem_price());
                JObject.put("total", c.getItem_total());
                jsonArray.put(JObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cartItemList.add(String.valueOf(JObject));
            Log.d("DATA", cartItemList.toString());
        }

        subtotal.setText("Rs. "+String.valueOf(totalItemCart));
        grandtotal.setText("Rs. "+String.valueOf(totalItemCart));

        setItemToCart();
        loadWalletAmount();

    }


    private void loadWalletAmount() {

        String uri = getResources().getString(R.string.base_url)+"wallet_amount.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    String path;

                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject JO = new JSONObject(s);
                            wallet_amount.setText("Rs. "+JO.getString("wallet_amount"));
                            if(Double.valueOf(JO.getString("wallet_amount"))>0){
                                totalWalletAmount = Double.valueOf(JO.getString("wallet_amount"));
                                walletCheckBox.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "Registration.Error!", Toast.LENGTH_SHORT).show();

                    }
                }){@Override
        public Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            return params;
        }
        };
        queue.add(stringRequest);

    }

    private void addToCart() {
        progressDialog.show();
        String uri = getResources().getString(R.string.base_url)+"add_to_cart.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    String code;
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject JO = new JSONObject(s);
                            code = JO.getString("code");
                            if(code.equals("200")){
                                progressDialog.dismiss();
                                Intent i = new Intent(getApplicationContext(), Home.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                db.deleteCart(new Cart());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), "Order Placed Successfully", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Toast.makeText(getApplicationContext(), "Error.Response",Toast.LENGTH_SHORT).show();
                    }
                }){@Override
        public Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            params.put("order", cartItemList.toString());
            params.put("total", String.valueOf(totalItemCart));
            params.put("address", address);
            params.put("walletAmountUsed", walletAmountUsed);
            params.put("order_id", mobile+String.valueOf(oid));

            return params;
        }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void setItemToCart() {

        try {

            JSONArray JA = jsonArray;

            for(int i=0; i<JA.length(); i++){
                JSONObject JO1 = JA.getJSONObject(i);
                FetchedListOfCartItem flp = new FetchedListOfCartItem(JO1.getInt("cartId"),
                        JO1.getString("itemId"),
                        JO1.getString("itemName"),
                        JO1.getString("itemImage"),
                        JO1.getString("itemSType"),
                        JO1.getString("itemQuantity"),
                        JO1.getString("itemPrice"),
                        JO1.getString("itemTotal"));
                listItems.add(flp);
            }
            adapter = new ContactAdapter(listItems,getApplicationContext());
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
