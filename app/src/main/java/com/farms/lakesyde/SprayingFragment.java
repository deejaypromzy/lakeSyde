package com.farms.lakesyde;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SprayingFragment extends Fragment {
    private static final String TAG = "SprayingFragment";
    TextInputEditText user_name, amt, time, reason;
    Button submit;
    private PrefManager prefManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spraying, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        prefManager = new PrefManager(getActivity());

        user_name = view.findViewById(R.id.user_name);
        amt = view.findViewById(R.id.amt);
        time = view.findViewById(R.id.time);
        reason = view.findViewById(R.id.reason);
        submit = view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validated()) {
                    if (Utils.haveNetworkConnection(getActivity())) {
                        new ActivityAsync().execute("chemical",
                                user_name.getText().toString(),
                                amt.getText().toString(),
                                time.getText().toString(),
                                reason.getText().toString(),
                                prefManager.getUserPhone());

                    }
                }
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SprayingFragment.this)
                        .navigate(R.id.action_SprayingFragment_to_FirstFragment);
            }
        });
    }

    private boolean Validated() {
        if (user_name.getText().toString().trim().equals("")) {
            user_name.requestFocus();
            user_name.setError("This Field is Required");
            return false;
        } else if (user_name.length() < 3) {
            user_name.requestFocus();
            user_name.setError("Field can't be less than 3 characters");
            return false;
        } else {
            user_name.setError(null);
        }

        if (amt.getText().toString().trim().equals("")) {
            amt.requestFocus();
            amt.setError("This Field is Required");
            return false;
        } else {
            amt.setError(null);
        }
        if (time.getText().toString().trim().equals("")) {
            time.requestFocus();
            time.setError("This Field is Required");
            return false;
        } else {
            time.setError(null);
        }
        return true;

    }

    private class ActivityAsync extends AsyncTask<String, String, JSONObject> {
        private static final String LOGIN_URL = "http://www.lakesyde.pewgapp.com/index.php";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        JSONParser jsonParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            submit.setText("Sending Report");
            submit.setEnabled(false);

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("category", args[0]);
                params.put("item_name", args[1]);
                params.put("amount", args[2]);
                params.put("time", args[3]);
                params.put("reason", args[4]);
                params.put("phone", args[5]);

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

            submit.setText("Submit");
            submit.setEnabled(true);


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
                Toast.makeText(getContext(), "Report Sent",
                        Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(SprayingFragment.this)
                        .navigate(R.id.action_SprayingFragment_to_FirstFragment);
                Log.d("Success!", message);
            } else {
                Toast.makeText(getContext(), "Oops, Report not sent - try again", Toast.LENGTH_SHORT).show();
                Log.d("Failure", message);

            }
        }

    }

}
