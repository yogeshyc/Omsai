package com.softhub.omsai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Map;

public class AddressSelect extends AppCompatActivity {

    private EditText a_addressEdit;
    private EditText a_pincode;
    private TextView a_city;
    private TextView a_or;
    private TextView a_printAddress1;
    private TextView a_printAddress2;
    private TextView a_selectOne;
    private TextView a_selectTwo;
    private TextView a_removeOne;
    private TextView a_removeTwo;
    private Button a_add;
    private CardView a_storedAddress1;
    private CardView a_storedAddress2;
    private ImageView backButton;
    private Spinner a_society;
    private Spinner a_area;

    private Session session;
    private String Mobile;
    private String City;
    private String addressOne;
    private String addressTwo;
    private String addressLine;
    private String pincode;
    private String fullAddress;
    private String addressId;
    private String area_name;
    private String soc_name;

    private String flag;

    private ArrayList<String> socList;
    private ArrayList<String> areaList;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(AddressSelect.this);

        a_addressEdit = (EditText) findViewById(R.id.a_addressEdit);
        a_pincode = (EditText) findViewById(R.id.a_pincode);
        a_city = (TextView) findViewById(R.id.a_city);
        a_or = (TextView) findViewById(R.id.a_or);
        a_printAddress1 = (TextView) findViewById(R.id.a_printAddress1);
        a_printAddress2 = (TextView) findViewById(R.id.a_printAddress2);
        a_selectOne = (TextView) findViewById(R.id.a_selectOne);
        a_selectTwo = (TextView) findViewById(R.id.a_selectTwo);
        a_removeOne = (TextView) findViewById(R.id.a_removeOne);
        a_removeTwo = (TextView) findViewById(R.id.a_removeTwo);
        a_add = (Button) findViewById(R.id.a_add);
        a_storedAddress1 = (CardView) findViewById(R.id.a_storedAddress1);
        a_storedAddress2 = (CardView) findViewById(R.id.a_storedAddress2);
        a_society = (Spinner) findViewById(R.id.a_society);
        a_area = (Spinner) findViewById(R.id.a_area);
        backButton = (ImageView) findViewById(R.id.backButton);

        session = new Session(getApplicationContext());
        Mobile = session.prefs.getString("Mbl", null);

        socList = new ArrayList<>();
        areaList = new ArrayList<>();

        getSoc();
        getArea();

        Bundle b = new Bundle();
        b = getIntent().getExtras();
        flag = b.getString("flag");

        a_storedAddress1.setVisibility(View.GONE);
        a_storedAddress2.setVisibility(View.GONE);
        findAddress();

        a_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area_name = a_area.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        a_society.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soc_name = a_society.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        a_selectOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flag.equals("account")){
                    Intent i = new Intent(getApplicationContext(), Account.class);
                    i.putExtra("address", a_printAddress1.getText().toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }else if(flag.equals("cart")){
                    Intent i = new Intent(getApplicationContext(), CartActivity.class);
                    i.putExtra("address", a_printAddress1.getText().toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }

            }
        });
        a_selectTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flag.equals("account")){
                    Intent i = new Intent(getApplicationContext(), Account.class);
                    i.putExtra("address", a_printAddress2.getText().toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }else if(flag.equals("cart")){
                    Intent i = new Intent(getApplicationContext(), CartActivity.class);
                    i.putExtra("address", a_printAddress2.getText().toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        });
        a_removeOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressId = "1";
                removeAddress();
            }
        });
        a_removeTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressId = "2";
                removeAddress();
            }
        });
        a_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressLine = a_addressEdit.getText().toString();
                pincode = a_pincode.getText().toString();

                /*if (soc_name.equals("Select Society") && area_name.equals("Select Area")){
                    Toast.makeText(AddressSelect.this, "Select Society or Area", Toast.LENGTH_SHORT).show();
                } else if (soc_name.equals("Skip") && area_name.equals("Select Area")){
                    Toast.makeText(AddressSelect.this, "Select Society or Area", Toast.LENGTH_SHORT).show();
                } else if (soc_name.equals("Select Society") && area_name.equals("Skip")){
                    Toast.makeText(AddressSelect.this, "Select Society or Area", Toast.LENGTH_SHORT).show();
                } else if (soc_name.equals("Skip") && area_name.equals("Skip")){
                    Toast.makeText(AddressSelect.this, "Select Society or Area", Toast.LENGTH_SHORT).show();
                } else{
                    if(soc_name.equals("Select Society")||soc_name.equals("Skip")){
                        soc_name = "";
                    }
                    if(area_name.equals("Select Area")||area_name.equals("Skip")){
                        area_name = "";
                    }
                    if(soc_name.equals("")){
                        fullAddress = addressLine+"\n"+area_name+"\n"+"PUNE"+"\n"+pincode;
                    }else if(area_name.equals("")){
                        fullAddress = addressLine+"\n"+soc_name+"\n"+"PUNE"+"\n"+pincode;
                    }else if(soc_name.equals("") && area_name.equals("")){
                        fullAddress = addressLine+"\n"+"PUNE"+"\n"+pincode;
                    }else{
                        fullAddress = addressLine+"\n"+soc_name+"\n"+area_name+"\n"+"PUNE"+"\n"+pincode;
                    }
                    addAddress();
                }*/

                if(addressLine.equals("")){
                    Toast.makeText(AddressSelect.this, "Please add address", Toast.LENGTH_SHORT).show();
                }else{
                    fullAddress = addressLine;
                    addAddress();
                }


            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


    private void removeAddress() {

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        String uri = getResources().getString(R.string.base_url)+"remove_address.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    String code;
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject JO = new JSONObject(s);
                            code = JO.getString("result");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Address Removed", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Toast.makeText(getApplicationContext(), "Error.Response",Toast.LENGTH_SHORT).show();
                    }
                }){@Override
        public Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("mobile", Mobile);
            params.put("address_id", addressId);
            return params;
        }
        };
        queue.add(stringRequest);

    }

    private void addAddress() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        String uri = getResources().getString(R.string.base_url)+"add_address.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    String code;
                    String result;
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject JO = new JSONObject(s);
                            code = JO.getString("data_code");
                            result = JO.getString("result");
                            if(code.equals("200")){
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }else{
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(getApplicationContext(), "Error.Response",Toast.LENGTH_SHORT).show();
                    }
                }){@Override
        public Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("mobile", Mobile);
            params.put("address", fullAddress);
            return params;
        }
        };
        queue.add(stringRequest);

    }

    private void findAddress() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        String uri = getResources().getString(R.string.base_url)+"check_address.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    String code;
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject JO = new JSONObject(s);
                            code = JO.getString("data_code");
                            if(code.equals("500")){
                                JSONArray JA = JO.getJSONArray("result");
                                for(int i=0; i<JA.length(); i++){
                                    JSONObject JO1 = JA.getJSONObject(i);
                                    addressOne = JO1.getString("address1");
                                    addressTwo = JO1.getString("address2");
                                }
                                if(addressOne.equals("")){
                                    a_storedAddress1.setVisibility(View.GONE);
                                }else{
                                    a_storedAddress1.setVisibility(View.VISIBLE);
                                    a_printAddress1.setText(addressOne);
                                }
                                if(addressTwo.equals("")){
                                    a_storedAddress2.setVisibility(View.GONE);
                                }else{
                                    a_storedAddress2.setVisibility(View.VISIBLE);
                                    a_printAddress2.setText(addressTwo);
                                }
                            }else{

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
                        // Toast.makeText(getApplicationContext(), "Error.Response",Toast.LENGTH_SHORT).show();
                    }
                }){@Override
        public Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("mobile", Mobile);
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void getSoc() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        String uri = getResources().getString(R.string.base_url)+"soc_list.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        socList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        socList.add("Select Society");
                        socList.add("Skip");
                        try {
                            JSONObject JO = new JSONObject(s);
                            JSONArray JA = JO.getJSONArray("menu_banner");
                            for(int i=0; i<JA.length(); i++){
                                JSONObject JO1 = JA.getJSONObject(i);
                                socList.add(JO1.get("soc_name").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        setSoc();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error.Response",Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(stringRequest);

    }

    private void setSoc(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, socList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        a_society.setAdapter(arrayAdapter);
    }

    private void getArea() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        String uri = getResources().getString(R.string.base_url)+"area_list.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        areaList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        areaList.add("Select Area");
                        areaList.add("Skip");
                        try {
                            JSONObject JO = new JSONObject(s);
                            JSONArray JA = JO.getJSONArray("menu_banner");
                            for(int i=0; i<JA.length(); i++){
                                JSONObject JO1 = JA.getJSONObject(i);
                                areaList.add(JO1.get("area_name").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setArea();
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error.Response",Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(stringRequest);

    }

    private void setArea(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, areaList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        a_area.setAdapter(arrayAdapter);
    }

}
