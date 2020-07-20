package com.farms.lakesyde;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Registration extends AppCompatActivity {
    private static final String TAG = "Registration";
    TextView tvSignup, tvLogin, acc;

    TextInputEditText user_name, mphone, phone2, farm_name, location, size, crop, email;
    Button btnSignUp, btnLogin;
    ConstraintLayout lvparent;
    LinearLayout parentLogin, parentSignup;
    private ConnectionClass connectionClass;
    private SharedPreferences sharedpreferences;
    private PrefManager prefManager;
    private ProgressDialog progressDialog;

    private void setDarkMode(Window window) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        changeStatusBar(1, window);
    }

    public void changeStatusBar(int mode, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.contentBodyColor));
            //white mode
            if (mode == 1) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDarkMode(getWindow());
        setContentView(R.layout.activity_registration);

        // Making notification bar transparent
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }

        // making notification bar transparent
        // changeStatusBarColor();


        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isRegistered()) {
            launchMainActivity();
            finish();
        }
        connectionClass = new ConnectionClass();

        tvSignup = findViewById(R.id.tvSignup);
        tvLogin = findViewById(R.id.tvLogin);
        parentLogin = findViewById(R.id.parentLogin);
        parentSignup = findViewById(R.id.parentSignup);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        lvparent = findViewById(R.id.lvparent);
        user_name = findViewById(R.id.user_name);
        mphone = findViewById(R.id.phone);
        phone2 = findViewById(R.id.phone2);
        farm_name = findViewById(R.id.farm_name);
        location = findViewById(R.id.location);
        size = findViewById(R.id.size);
        crop = findViewById(R.id.crops);
        email = findViewById(R.id.email);
        acc = findViewById(R.id.acc);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLogin.setVisibility(View.GONE);
                parentSignup.setVisibility(View.VISIBLE);
                acc.setVisibility(View.VISIBLE);

            }
        });
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentSignup.setVisibility(View.GONE);
                parentLogin.setVisibility(View.VISIBLE);
                acc.setVisibility(View.GONE);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hideSoftKeyboard();
                v.requestFocusFromTouch();
                if (Utils.haveNetworkConnection(getApplicationContext())) {
                    if (!signUpValidate())
                        ShowSnackBar("Please enter all required fields");
                    else {
                        String username = user_name.getText().toString().trim();
                        String phone = mphone.getText().toString().trim();
                        String farm = farm_name.getText().toString().trim();
                        String farm_location = location.getText().toString().trim();
                        String farm_size = size.getText().toString().trim();
                        String crops = crop.getText().toString().trim();
                        String user_email = email.getText().toString().trim();

                        prefManager.setUserName(username);
                        prefManager.setUserPhone(phone);
                        prefManager.setFarm(farm);
                        prefManager.setFarmSize(farm_size);
                        prefManager.setUserLocation(farm_location);
                        prefManager.setCrops(crops);
                        prefManager.setUserEmail(user_email);

                        new RegAsync().execute("reg", username, phone, user_email, farm, farm_location, farm_size, crops);
                    }
                } else {
                    ShowSnackBar("No internet connection!");
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  startActivity(new Intent(Registration.this,MainActivity.class));
                // hideSoftKeyboard();
                v.requestFocusFromTouch();
                btnLogin.requestFocus();
                if (Utils.haveNetworkConnection(getApplicationContext())) {
                    if (!loginValidate())
                        ShowSnackBar("Please enter required fields");
                    else {
                        prefManager.setUserPhone(phone2.getText().toString().trim());
                        new LoginAsync().execute("Login", phone2.getText().toString().trim());
                    }
                } else {
                    ShowSnackBar("No internet connection!");
                }

            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void launchMainActivity() {
        prefManager.setRegistered(false);
        startActivity(new Intent(Registration.this, VerifyPhoneNumber.class));
        finish();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private boolean signUpValidate() {
        if (user_name.getText().toString().trim().equals("")) {
            user_name.requestFocus();
            user_name.setError("User Name cant be empty");
            return false;
        } else if (user_name.length() < 3) {
            user_name.requestFocus();
            user_name.setError("User Name cant be less than 3 characters");
            return false;
        } else {
            user_name.setError(null);

        }
        if (mphone.getText().toString().trim().equals("")) {
            mphone.requestFocus();
            mphone.setError("Phone number cant be empty");
            return false;
        } else if (!Patterns.PHONE.matcher(mphone.getText().toString()).matches() || mphone.length() < 10 || mphone.length() > 15) {
            mphone.requestFocus();
            mphone.setError("Enter a valid phone number");
            return false;
        } else {
            mphone.setError(null);

        }
        if (!TextUtils.isEmpty(email.getText().toString()) & !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.requestFocus();
            email.setError("Enter valid email address");
            return false;
        } else {
            email.setError(null);

        }
        if (location.getText().toString().trim().equals("")) {
            location.requestFocus();
            location.setError("Location cant be empty");
            return false;
        } else {
            location.setError(null);

        }
        if (crop.getText().toString().trim().equals("")) {
            crop.requestFocus();
            crop.setError("Crops cant be empty");
            return false;
        } else {
            crop.setError(null);

        }
        if (size.getText().toString().trim().equals("")) {
            size.requestFocus();
            size.setError("User Name cant be empty");
            return false;
        } else {
            size.setError(null);
        }
        return true;

    }

    private boolean loginValidate() {
        if (phone2.getText().toString().trim().equals("")) {
            phone2.requestFocus();
            phone2.setError("Phone number cant be empty");
            return false;
        } else if (!Patterns.PHONE.matcher(phone2.getText().toString()).matches() || phone2.length() < 10 || phone2.length() > 15) {
            phone2.requestFocus();
            phone2.setError("Enter valid phone number");
            return false;
        } else {
            phone2.setError(null);
        }
        return true;

    }

    public void ShowSnackBar(String message) {
        Snackbar.make(lvparent, message, Snackbar.LENGTH_LONG)

                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    private static class SMS extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {
                String recipient = params[0];
                String message = "Your verification code is Thank you!";
                String smsClientIdd = "wzyaaoyd";
                String smsSecrett = "hizayhei";

                String ozSURL = "https://api.hubtel.com/v1/messages/send?"; // where the SMS Gateway is running
                String f2 = "From=" + "LakeSyde" + "&To=";                                                                    //  string ozSURL2=
                String strUrl = ozSURL + f2 + recipient + "&Content=" + message + "&ClientReference=1234&ClientId=" + smsClientIdd + "&ClientSecret=" + smsSecrett + "&RegisteredDelivery=true";

                URL url = new URL(strUrl);
                HttpURLConnection uc = (HttpURLConnection) url.openConnection();

                res = uc.getResponseMessage();
                System.out.println(uc.getResponseMessage());

                uc.disconnect();

            } catch (Exception ex) {
                System.out.println(ex.getMessage());

            }
            return res;
        }
    }

    private class LoginAsync extends AsyncTask<String, String, JSONObject> {
        private static final String LOGIN_URL = "http://www.lakesyde.pewgapp.com/index.php";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        JSONParser jsonParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            btnLogin.setVisibility(View.GONE);
            progressDialog = ProgressDialog.show(Registration.this, "Login", "Please Wait", false, false);
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("login", args[0]);
                params.put("phone", args[1]);
                params.put("phone-mode", "1");

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                } else {
                    Log.d("JSON result", "json is null");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {

            int success = 0;
            String message = "";

            progressDialog.dismiss();
            btnLogin.setVisibility(View.VISIBLE);

            if (json != null) {
                // ShowSnackBar("Login Successful");


                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString(TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (success == 1) {
                Toast.makeText(Registration.this, "Login Successful",
                        Toast.LENGTH_LONG).show();
                launchMainActivity();
                Log.d("Success!", message);
            } else {
                Toast.makeText(Registration.this, "Login Failed",
                        Toast.LENGTH_LONG).show();
                Log.d("Failure", message);
            }
        }

    }

    private class RegAsync extends AsyncTask<String, String, JSONObject> {
        private static final String LOGIN_URL = "http://www.lakesyde.pewgapp.com/index.php";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        JSONParser jsonParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            btnSignUp.setVisibility(View.GONE);
            progressDialog = ProgressDialog.show(Registration.this, "Registration", "Please Wait", false, false);
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("reg", args[0]);
                params.put("username", args[1]);
                params.put("phone", args[2]);
                params.put("email", args[3]);
                params.put("farm_name", args[4]);
                params.put("location", args[5]);
                params.put("size_of_farm", args[6]);
                params.put("crops", args[7]);
                params.put("phone-mode", "1");

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                } else {
                    Log.d("JSON result", "json is null");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {

            int success = 0;
            String message = "";
            String sms = "";

            progressDialog.dismiss();
            btnSignUp.setVisibility(View.VISIBLE);

            if (json != null) {
                // ShowSnackBar("Login Successful");

                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString(TAG_MESSAGE);
                    sms = json.getString("sms");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (success == 1) {
                Toast.makeText(Registration.this, "Registration Successful",
                        Toast.LENGTH_LONG).show();
                launchMainActivity();
                Log.d(TAG, "onPostExecute: " + sms);
                Log.d("Success!", message);
            } else {
                if (message.equals("Oops, User Already Exist")) {
                    Toast.makeText(Registration.this, "Oops, User Already Exist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Registration.this, "Oops, Registration failed - try again", Toast.LENGTH_SHORT).show();
                    Log.d("Failure", message);
                    Log.d(TAG, "onPostExecute: " + sms);
                }


            }
        }

    }

}