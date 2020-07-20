package com.farms.lakesyde;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class FertilizerFragment extends Fragment {
    private static final String TAG = "FertilizerFragment";
    TextInputEditText user_name, amt, time, date;
    Button submit;
    private PrefManager prefManager;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fertilizer_app, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        bottomNavigationView.startAnimation(animation);
        bottomNavigationView.setVisibility(View.GONE);

        prefManager = new PrefManager(getActivity());

        user_name = view.findViewById(R.id.user_name);
        amt = view.findViewById(R.id.amt);
        time = view.findViewById(R.id.time);
        date = view.findViewById(R.id.date);
        submit = view.findViewById(R.id.submit);
        createOnClickListeners();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validated()) {
                    if (Utils.haveNetworkConnection(getActivity())) {
                        new ActivityAsync().execute("fertilizer",
                                user_name.getText().toString(),
                                amt.getText().toString(),
                                time.getText().toString(),
                                prefManager.getUserPhone());

                    }
                }
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FertilizerFragment.this)
                        .navigate(R.id.action_FertilizerFragment_to_FirstFragment);
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

    /**
     * Sets on click listeners for widgets in the inflated layout
     */
    private void createOnClickListeners() {
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        R.style.datepicker,
                        dateSetListener,
                        year, month, day);
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);

                if (dayOfMonth >= 1 && dayOfMonth <= 9) {
                    String newDay = "0" + dayOfMonth;
                    date.setText(newDay + "/" + month + "/" + year);
                }

                if (month >= 1 && month <= 9) {
                    String newMonth = "0" + month;
                    date.setText(dayOfMonth + "/" + newMonth + "/" + year);
                }

                if (dayOfMonth >= 1 && dayOfMonth <= 9 && month >= 1 && month <= 9) {
                    String newDay = "0" + dayOfMonth;
                    String newMonth = "0" + month;
                    date.setText(newDay + "/" + newMonth + "/" + year);
                } else {
                    date.setText(dayOfMonth + "/" + month + "/" + year);
                }
            }
        };

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), R.style.datepicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });

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
                params.put("phone", args[4]);
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
                NavHostFragment.findNavController(FertilizerFragment.this)
                        .navigate(R.id.action_FertilizerFragment_to_FirstFragment);
                Log.d("Success!", message);
            } else {
                Toast.makeText(getContext(), "Oops, Report not sent - try again", Toast.LENGTH_SHORT).show();
                Log.d("Failure", message);

            }
        }

    }

}
