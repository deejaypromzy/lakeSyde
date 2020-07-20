package com.farms.lakesyde;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class VerifyPhoneNumber extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText etCode;
    private Button signinBtn;
    private TextView resend;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        etCode = findViewById(R.id.verification);
        signinBtn = findViewById(R.id.signinBtn);
        resend = findViewById(R.id.resend);
        signinBtn.setOnClickListener(this);
        resend.setOnClickListener(this);

        prefManager = new PrefManager(this);
        if (!prefManager.isVerified()) {
            launchMainActivity();
            finish();
        }
    }

    private void launchMainActivity() {
        prefManager.setVerified(false);
        startActivity(new Intent(VerifyPhoneNumber.this, MainActivity.class));
        finish();
    }


    public void Resend() {
        String phone = prefManager.getUserPhone();
        new SendVerificationAsync().execute("resendVerification", phone);
        resend.setText("Resend Again in 5mins");
        resend.setTextColor(getResources().getColor(R.color.material_blue_grey_80));
        resend.setEnabled(false);

    }

    public void VerifyMyEmail() {
        if (!TextUtils.isEmpty(etCode.getText().toString())) {
            new VerifyAsync().execute("verify", etCode.getText().toString().trim());

        } else {
            etCode.requestFocus();
            etCode.setError("Field is required", getResources().getDrawable(R.drawable.add));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signinBtn:
                v.requestFocusFromTouch();
                VerifyMyEmail();
                break;
            case R.id.resend:
                Resend();
                break;
            case R.id.signout:
                prefManager.setRegistered(true);
                startActivity(new Intent(VerifyPhoneNumber.this, Registration.class));
                finish();
                break;
        }
    }

    private class VerifyAsync extends AsyncTask<String, String, JSONObject> {
        private static final String LOGIN_URL = "http://www.lakesyde.pewgapp.com/index.php";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        JSONParser jsonParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            signinBtn.setText("Please Wait ...");
            signinBtn.setEnabled(false);
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("verify", args[0]);
                params.put("code", args[1]);
                params.put("phone-mode", "1");

                // params.put("message", args[2]);

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
                prefManager.setVerified(false);
                startActivity(new Intent(VerifyPhoneNumber.this, MainActivity.class));
                finish();
                Toast.makeText(VerifyPhoneNumber.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                Log.d("Success!", message);
            } else if (success == 2) {
                Toast.makeText(VerifyPhoneNumber.this, "Verification Code has expired, resend", Toast.LENGTH_SHORT).show();
                Log.d("Failure", message);
            } else {
                signinBtn.setText("Verify");
                signinBtn.setEnabled(true);
                Toast.makeText(VerifyPhoneNumber.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                Log.d("Failure", message);
            }
        }

    }

    private class SendVerificationAsync extends AsyncTask<String, String, JSONObject> {
        private static final String LOGIN_URL = "http://www.lakesyde.pewgapp.com/index.php";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        JSONParser jsonParser = new JSONParser();

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("resendVerification", args[0]);
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
                Toast.makeText(VerifyPhoneNumber.this, "Check SMS inbox for verification code", Toast.LENGTH_SHORT).show();
                Log.d("Success!", message);
            } else {

                Toast.makeText(VerifyPhoneNumber.this, "Failed", Toast.LENGTH_SHORT).show();
                Log.d("Failure", message);
            }
        }

    }
}
