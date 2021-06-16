package com.softhub.omsai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.softhub.omsai.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private TextInputLayout mobile;
    private TextInputLayout password;
    private EditText m;
    private EditText p;
    private EditText otp;
    private TextView signup;
    private TextView forgot_password;
    private CardView login;
    private Button getOtp;
    ProgressDialog progressDialog;
    private Session session;
    private String fName, eMail, regi_mobile, userRole,mobileNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mobile = (TextInputLayout) findViewById(R.id.mobile);
        password = (TextInputLayout) findViewById(R.id.password);
        signup = (TextView) findViewById(R.id.signup);
        forgot_password = (TextView) findViewById(R.id.forgot_password);
        login = (CardView) findViewById(R.id.login);
        getOtp = findViewById(R.id.get_otp);

        m = (EditText) findViewById(R.id.m);
        p = (EditText) findViewById(R.id.p);
        otp = findViewById(R.id.enter_otp);


        session = new Session(getApplicationContext());
        progressDialog = new ProgressDialog(Login.this);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumber = m.getText().toString();
                if(mobileNumber.equals("")){
                    Toast.makeText(getApplicationContext(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }else{
                    sendOtp();
                }
            }
        });

    }

    private void sendOtp() {

        String uri = getResources().getString(R.string.base_url)+"login_otp.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject JO = new JSONObject(s);
                            String data_code = JO.getString("data_code");
                            if(data_code.equals("200")){
                                progressDialog.dismiss();
                                /*llOtp.setVisibility(View.VISIBLE);
                                llButton.setVisibility(View.VISIBLE);*/
                                getOtp.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "OTP is send to registered mobile number", Toast.LENGTH_LONG).show();
                                /*Intent intent = new Intent(getApplicationContext(),Login.class);
                                startActivity(intent);*/
                            }else if(data_code.equals("500")){
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Invalid mobile number", Toast.LENGTH_SHORT).show();
                            }


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
            params.put("mobile", mobileNumber);
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void loginUser() {

        progressDialog.setMessage("Verifying...");
        progressDialog.show();
        String uri = getResources().getString(R.string.base_url)+"login.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject JO = new JSONObject(s);
                            String code = JO.getString("data_code");
                            if(code.equals("200")){
                                JSONArray JA = JO.getJSONArray("item_list");
                                for(int i=0; i<JA.length(); i++){
                                    JSONObject JO1 = JA.getJSONObject(i);
                                    eMail = JO1.getString("email");
                                    fName = JO1.getString("first_name");
                                    regi_mobile = JO1.getString("mobile");
                                    userRole = JO1.getString("role");
                                }
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                openApp();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), JO.getString("Message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Registration.Error!",Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();

                params.put("mobile", m.getText().toString());
                params.put("password", p.getText().toString());
                params.put("otp", otp.getText().toString());

                return params;
            }
        };
        queue.add(stringRequest);

    }

    private void openApp() {
        session.setLoggedin(true);
        session.setMobile(regi_mobile);
        session.setUserName(fName);
        session.setEmailId(eMail);
        session.setUserRole(userRole);
        if(userRole.equals("Customer")){
            Intent i = new Intent(getApplicationContext(), Home.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else{
            Intent i = new Intent(getApplicationContext(), EmployeePage.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

    }
}
