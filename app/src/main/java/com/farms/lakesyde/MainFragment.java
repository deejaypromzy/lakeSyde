package com.farms.lakesyde;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    protected LocationManager locationManager;
    FusedLocationProviderClient mFusedLocationClient;
    private String currentLocation;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private String[] permissionsRequired = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_up);
        bottomNavigationView.startAnimation(animation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        permissionStatus = getActivity().getSharedPreferences("permissionStatus", MODE_PRIVATE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        if (
//                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//        ) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, getContext());
//
//            getLastLocation();
//        }


        view.findViewById(R.id.others).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_FirstFragment_to_OthersFragment);

            }
        });
        view.findViewById(R.id.fertilizer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_FirstFragment_to_FertilizerFragment);
            }
        });
        view.findViewById(R.id.spraying).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SprayingFragment);
            }
        });
        view.findViewById(R.id.planting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_FirstFragment_to_PlantingFragment);
            }
        });
        view.findViewById(R.id.pest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_FirstFragment_to_PestFragment);
            }
        });
        view.findViewById(R.id.maps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (
                            ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setIcon(R.mipmap.ic_launcher_round);
                        builder.setTitle("LakeSyde Farms Needs Permission");
                        builder.setMessage("LakeSyde Farms needs Location permissions.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(getActivity(), permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else if (permissionStatus.getBoolean(permissionsRequired[0], false) || permissionStatus.getBoolean(permissionsRequired[1], false)) {
                        //Previously Permission Request was cancelled with 'Dont Ask Again',
                        // Redirect to Settings after showing Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setIcon(R.mipmap.ic_launcher_round);
                        builder.setTitle("LakeSyde Farms Needs Permission");
                        builder.setMessage("LakeSyde Farms needs Location permissions.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                sentToSettings = true;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                Toast.makeText(getActivity(), "Go to Permissions to Grant Location", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        //just request the permission
                        ActivityCompat.requestPermissions(getActivity(), permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }

                    //txtPermissions.setText("Permission Required");

                    SharedPreferences.Editor editor = permissionStatus.edit();
                    editor.putBoolean(permissionsRequired[0], true);
                    editor.putBoolean(permissionsRequired[1], true);
                    editor.apply();
                } else {
                    //You already have the permission, just go ahead.
                    //restart this activity
//                    overridePendingTransition(0, 0);
//                    finish();
//                    overridePendingTransition(0, 0);
//                    startActivity(getIntent());
                    permissionStatus = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    double Long = Double.parseDouble(permissionStatus.getString("longg", "0.00000"));
                    double lat = Double.parseDouble(permissionStatus.getString("latt", "0.00000"));
                    Toast.makeText(getActivity(), "Latitude " + lat + ", Longitude:" + Long, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", lat);
                    bundle.putDouble("long", Long);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    // proceedAfterPermission();
                }


            }
        });
    }

}
