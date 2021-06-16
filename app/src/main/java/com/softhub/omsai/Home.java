package com.softhub.omsai;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<FetchedListOfImages> listItems;

    private RecyclerView recyclerViewSubCategory;
    private RecyclerView.Adapter adapterSubCategory;
    private List<FetchedListOfItem> listItemsSubCategory;

    private RecyclerView recyclerViewCategory;
    private RecyclerView.Adapter adapterCategory;
    private List<FetchedListOfItem> listItemsCategory;

    private RecyclerView recyclerViewBanner;
    private RecyclerView.Adapter adapterBanner;
    private List<FetchedListOfImages> listItemsBanner;

    private ImageView cart;
    public TextView notification;

    private LinearLayout fresh_fruits;
    private LinearLayout fresh_vegetables;
    private LinearLayout dry_fruits;
    private LinearLayout cuts_sprouts;

    private CartDatabaseHelper db;
    private TextView userMobileNumber;
    private TextView userName;
    private ImageView search;

    private Session session;
    private DrawerLayout drawer;
    private ProgressDialog progressDialog;

    @Override
    protected void onPostResume() {
        notification.setText(String.valueOf(db.getCartItemCount()));
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cart = (ImageView) findViewById(R.id.cart);
        notification = (TextView) findViewById(R.id.notification);
        //search = findViewById(R.id.search);

        fresh_fruits = (LinearLayout) findViewById(R.id.fresh_fruits);
        fresh_vegetables = (LinearLayout) findViewById(R.id.fresh_vegetables);
        dry_fruits = (LinearLayout) findViewById(R.id.dry_fruits);
        cuts_sprouts = (LinearLayout) findViewById(R.id.cuts_sprouts);

        db = new CartDatabaseHelper(getApplicationContext());
        notification.setText(String.valueOf(db.getCartItemCount()));
        session = new Session(getApplicationContext());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        View nView = navigationView.inflateHeaderView(R.layout.nav_header_home);
        Menu menuNave = navigationView.getMenu();
        MenuItem logout = menuNave.findItem(R.id.logout);
        MenuItem login = menuNave.findItem(R.id.login);
        MenuItem account = menuNave.findItem(R.id.account);
        MenuItem orders = menuNave.findItem(R.id.orders);

        userMobileNumber = (TextView) nView.findViewById(R.id.userMobileNumber);
        userName = (TextView) nView.findViewById(R.id.userName);


        if (session.loggedin()) {

            logout.setVisible(true);
            account.setVisible(true);
            orders.setVisible(true);
            login.setVisible(false);

            userMobileNumber.setText(session.prefs.getString("Mbl", null));
            userName.setText(session.prefs.getString("Unm", null));

        }

        recyclerViewSubCategory = (RecyclerView) findViewById(R.id.recyclerViewSubCategory);
        recyclerViewSubCategory.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagerSubCategory = new LinearLayoutManager(Home.this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSubCategory.setLayoutManager(horizontalLayoutManagerSubCategory);
        listItemsSubCategory = new ArrayList<>();

        recyclerViewCategory = (RecyclerView) findViewById(R.id.recyclerViewCategory);
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setLayoutManager(new GridLayoutManager(this, 3));
        listItemsCategory = new ArrayList<>();

        recyclerViewBanner = (RecyclerView) findViewById(R.id.recyclerViewBanner);
        recyclerViewBanner.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagerBanner = new LinearLayoutManager(Home.this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBanner.setLayoutManager(horizontalLayoutManagerBanner);
        listItemsBanner = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listItems = new ArrayList<>();


        progressDialog = new ProgressDialog(Home.this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        new A().execute();

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CartActivity.class);
                i.putExtra("address", "abc");
                startActivity(i);
            }
        });
        fresh_fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Fruits.class);
                startActivity(i);
            }
        });
        fresh_vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), SubCategory.class);
//                startActivity(i);
            }
        });
        dry_fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DryFruits.class);
                startActivity(i);
            }
        });
        cuts_sprouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), ItemActivity.class);
//                startActivity(i);
            }
        });
        /*search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,AllProductSearch.class);
                startActivity(intent);
            }
        });*/
    }

    class A extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            loadMealOffers();
            loadCategories();
            loadItems();
            return null;
        }
    }

    private void loadCategories() {

        String uri = getResources().getString(R.string.base_url)+"category_list.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    String path;

                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject JO = new JSONObject(s);
                            JSONArray JA = JO.getJSONArray("category");
                            for (int i = 0; i < JA.length(); i++) {
                                JSONObject JO1 = JA.getJSONObject(i);
                                FetchedListOfItem flp = new FetchedListOfItem(JO1.getString("type_id"),
                                        JO1.getString("type"),
                                        JO1.getString("userfile"),"","","","","");
                                listItemsCategory.add(flp);
                            }
                            adapterCategory = new ContactAdapterFruits(listItemsCategory,getApplicationContext());
                            recyclerViewCategory.setAdapter(adapterCategory);

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
                });
        queue.add(stringRequest);

    }

    private void loadItems() {

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

                            if(JO.getString("data_code").equals("200")){
                                JSONArray JA = JO.getJSONArray("item_list");

                                if(JA.length()>0){
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
                                        listItemsSubCategory.add(flp);
                                    }

                                    adapterSubCategory = new ContactAdapterItem(listItemsSubCategory,getApplicationContext());
                                    recyclerViewSubCategory.setAdapter(adapterSubCategory);
                                }
                            }

                            progressDialog.dismiss();

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
            //params.put("category_name", "HOME");
            params.put("category_name", "मोकळ्या शेतातातील तननाशके");
            //params.put("user_type", session.prefs.getString("Utype","NA"));
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void loadMealOffers() {

        String uri = getResources().getString(R.string.base_url)+"menu_banner.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    String path;

                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject JO = new JSONObject(s);
                            JSONArray JA = JO.getJSONArray("tiffin_banner");
                            for (int i = 0; i < JA.length(); i++) {
                                JSONObject JO1 = JA.getJSONObject(i);
                                FetchedListOfImages flp = new FetchedListOfImages(JO1.getString("image_path"),
                                        JO1.getString("description"));
                                listItems.add(flp);
                            }
                            adapter = new ContactAdapterBanner(listItems,getApplicationContext());
                            recyclerView.setAdapter(adapter);
                            JSONArray JA1 = JO.getJSONArray("menu_banner");
                            for (int i = 0; i < JA1.length(); i++) {
                                JSONObject JO1 = JA1.getJSONObject(i);
                                FetchedListOfImages flp = new FetchedListOfImages(JO1.getString("image_path"),
                                        JO1.getString("description"));
                                listItemsBanner.add(flp);
                            }
                            adapterBanner = new ContactAdapterBanner(listItemsBanner, getApplicationContext());
                            recyclerViewBanner.setAdapter(adapterBanner);
                            if(listItemsBanner.size()>1){
                                scrollItems();
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
                });
        queue.add(stringRequest);
    }

    private void scrollItems() {

        final int speedScroll = 4000;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;

            @Override
            public void run() {
                if (count < adapterBanner.getItemCount()) {
                    if (count == adapterBanner.getItemCount() - 1) {
                        flag = false;
                    } else if (count == 0) {
                        flag = true;
                    }
                    if (flag) count++;
                    else count--;

                    recyclerViewBanner.smoothScrollToPosition(count);
                    handler.postDelayed(this, speedScroll);
                }
            }
        };

        handler.postDelayed(runnable, speedScroll);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            drawer.closeDrawer(Gravity.LEFT);
        }
//        else if (id == R.id.fruit) {
//            Intent i = new Intent(getApplicationContext(), Fruits.class);
//            startActivity(i);
//        } else if (id == R.id.vegetables) {
//            Intent i = new Intent(getApplicationContext(), Vegetables.class);
//            startActivity(i);
//        } else if (id == R.id.dryfruit) {
//            Intent i = new Intent(getApplicationContext(), DryFruits.class);
//            startActivity(i);
//        } else if (id == R.id.cutsandsprouts) {
//            Intent i = new Intent(getApplicationContext(), CutsandSprouts.class);
//            startActivity(i);
//        }
        /*else if (id == R.id.payment) {
            Intent i = new Intent(getApplicationContext(), PaymentMode.class);
            startActivity(i);
        }*/
        else if (id == R.id.account) {
            Intent i = new Intent(getApplicationContext(), Account.class);
            startActivity(i);
        } else if (id == R.id.orders) {
            Intent i = new Intent(getApplicationContext(), OrderHistory.class);
            startActivity(i);
        } else if (id == R.id.login) {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
        } else if (id == R.id.logout) {
            logOut();
        } else if (id == R.id.aboutus) {
            Intent i = new Intent(getApplicationContext(), AboutUs.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                if (session.loggedin()) {
                    session.setMobile("");
                    session.setUserName("");
                    session.setEmailId("");
                    session.setUserType("");
                    session.setLoggedin(false);
                    db.deleteCart(new Cart());
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
