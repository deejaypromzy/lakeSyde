package com.farms.lakesyde;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class OthersFragment extends Fragment {
    CircleImageView ivPest;

    TextView tvPest;
    BufferedReader bufferedReader;
    Bitmap pFixBitmap, dFixBitmap;
    StringBuilder stringBuilder;
    TextInputEditText user_name, amt, time;
    Button submit;
    boolean check = true;
    private ByteArrayOutputStream pByteArrayOutputStream;
    private PrefManager prefManager;
    private int GALLERY = 1, CAMERA = 2;
    private int GALLERY2 = 3, CAMERA2 = 4;
    private ProgressDialog progressDialog;
    private String pestConvertImage, diseaseConvertImage = "";
    private byte[] pByteArray, dByteArray;
    private String user = "";


    public OthersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_others, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pByteArrayOutputStream = new ByteArrayOutputStream();

        ivPest = view.findViewById(R.id.ivPest);
        tvPest = view.findViewById(R.id.tvPest);


        tvPest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }


        prefManager = new PrefManager(getActivity());

        user_name = view.findViewById(R.id.user_name);
        amt = view.findViewById(R.id.desc);

        submit = view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Validated()) {
                    if (Utils.haveNetworkConnection(getActivity())) {

                        if (null != pFixBitmap) {
                            pFixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, pByteArrayOutputStream);
                            pByteArray = pByteArrayOutputStream.toByteArray();
                            pestConvertImage = Base64.encodeToString(pByteArray, Base64.DEFAULT);
                        }

                        new ActivityAsync().execute("others",
                                user,
                                pestConvertImage,
                                "others",
                                prefManager.getUserPhone());


                    }
                }


            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(OthersFragment.this)
                        .navigate(R.id.action_OthersFragment_to_FirstFragment);
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

            user = user_name.getText().toString();
            user_name.setError(null);
        }
        return true;

    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    pFixBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    ivPest.setImageBitmap(pFixBitmap);
                    //      UploadImageOnServerButton.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            pFixBitmap = (Bitmap) data.getExtras().get("data");
            ivPest.setImageBitmap(pFixBitmap);
            //   UploadImageOnServerButton.setVisibility(View.VISIBLE);
            //  saveImage(thumbnail);
            Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            } else {
                Toast.makeText(getActivity(), "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();
            }
        }
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
            progressDialog = ProgressDialog.show(getActivity(), "Sending Report", "Please Wait", false, false);

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("category", args[0]);
                params.put("item_name", args[1]);
                params.put("pest_image_data", args[2]);
                params.put("pest_image_tag", args[3]);
                params.put("phone", args[4]);

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
            progressDialog.dismiss();


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
                NavHostFragment.findNavController(OthersFragment.this)
                        .navigate(R.id.action_OthersFragment_to_FirstFragment);
                Log.d("Success!", message);
            } else {
                Toast.makeText(getContext(), "Oops, Report not sent - try again", Toast.LENGTH_SHORT).show();
                Log.d("Failure", message);

            }
        }

    }


}
