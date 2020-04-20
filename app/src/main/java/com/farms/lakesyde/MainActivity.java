package com.farms.lakesyde;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    public NavController navController;
    public DrawerLayout drawer;
    protected LocationManager locationManager;
    FusedLocationProviderClient mFusedLocationClient;
    private String currentLocation;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private String[] permissionsRequired = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private FloatingActionButton fab;
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            prefEditor.putString("longg", String.valueOf(mLastLocation.getLongitude()));
            prefEditor.putString("latt", String.valueOf(mLastLocation.getLatitude()));
            prefEditor.apply();
        }
    };


    //    @Override
//    public boolean onSupportNavigateUp() {
//        return NavigationUI.navigateUp(drawer, Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment));
//    }
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();


        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        //    getLocation();
        //  getAddress();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

            getLastLocation();
        }


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DailyInputs.class));
            }
        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(
//                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                                || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                )
//                {
//                    if(
//                            ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
//                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)){
//                        //Show Information about why you need the permission
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setIcon(R.mipmap.ic_launcher_round);
//                        builder.setTitle("LakeSyde Farms Needs Permission");
//                        builder.setMessage("LakeSyde Farms needs Location permissions.");
//                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                                ActivityCompat.requestPermissions(MainActivity.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
//                            }
//                        });
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        builder.show();
//                    } else if (permissionStatus.getBoolean(permissionsRequired[0],false) || permissionStatus.getBoolean(permissionsRequired[1],false)) {
//                        //Previously Permission Request was cancelled with 'Dont Ask Again',
//                        // Redirect to Settings after showing Information about why you need the permission
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setIcon(R.mipmap.ic_launcher_round);
//                        builder.setTitle("LakeSyde Farms Needs Permission");
//                        builder.setMessage("LakeSyde Farms needs Location permissions.");
//                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                                sentToSettings = true;
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                intent.setData(uri);
//                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//                                Toast.makeText(getBaseContext(), "Go to Permissions to Grant Location", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        builder.show();
//                    }  else {
//                        //just request the permission
//                        ActivityCompat.requestPermissions(MainActivity.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
//                    }
//
//                    //txtPermissions.setText("Permission Required");
//
//                    SharedPreferences.Editor editor = permissionStatus.edit();
//                    editor.putBoolean(permissionsRequired[0],true);
//                    editor.putBoolean(permissionsRequired[1],true);
//                    editor.apply();
//                }
//                else {
//                    //You already have the permission, just go ahead.
//                    //restart this activity
////                    overridePendingTransition(0, 0);
////                    finish();
////                    overridePendingTransition(0, 0);
////                    startActivity(getIntent());
// permissionStatus = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        double Long= Double.parseDouble(permissionStatus.getString("longg",  "0.00000"));
//        double lat= Double.parseDouble(permissionStatus.getString("latt",  "0.00000"));
//        Toast.makeText(MainActivity.this, "Latitude "+lat + ", Longitude:" + Long, Toast.LENGTH_SHORT).show();
//
//        Intent intent = new Intent(MainActivity.this,MapsActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putDouble("lat",lat);
//        bundle.putDouble("long",Long);
//        intent.putExtras(bundle);
//        startActivity(intent);
//                    // proceedAfterPermission();
//                }
//
//
//                }
//        });
        //setFragment(new MainFragment());

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, drawer);

        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

//
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


//        try {
//            final KenBurnsView imageView = findViewById(R.id.backdrop);
//            // header.setImageResource(R.drawable.header);
//            Glide.with(this).load(DropBack.getRandomChildDrawable()).into(imageView);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void initCollapsingToolbar() {
        final Toolbar collapsingToolbar =
                (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar.setTitle("LakeSyde Farms");
        // AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        // appBarLayout.setExpanded(true);

//        // hiding & showing the title when toolbar expanded & collapsed
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle(getString(R.string.app_name));
//                    isShow = true;
//                } else if (isShow) {
//                    collapsingToolbar.setTitle("");
//                    isShow = false;
//                }
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void setFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.nav_host_fragment, fragment);
        t.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("LakeSyde Farms");
            alertDialog.setIcon(R.mipmap.ic_launcher_round);
            // Setting Dialog Message
            alertDialog.setMessage("Do you really want to exit?");


            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    // Write your code here to invoke YES event
                    finish();
                }
            });


            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);

        drawer.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.nav_home:
                navController.navigate(R.id.FirstFragment);
                fab.setVisibility(View.VISIBLE);
                break;

            case R.id.nav_profile:
                navController.navigate(R.id.profileFragment);
                fab.setVisibility(View.GONE);
                break;

            case R.id.nav_info:
                navController.navigate(R.id.aboutFragment);
                fab.setVisibility(View.GONE);
                break;

        }
        return true;

    }

    //
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//            fab.setVisibility(View.VISIBLE);
//            setFragment(new MainFragment());
//        }else if (id == R.id.nav_profile) {
//            fab.setVisibility(View.GONE);
//            setFragment(new ProfileFragment());
//        } else if (id == R.id.nav_info) {
//            fab.setVisibility(View.GONE);
//         setFragment(new SlideshowFragment());
//
//        }
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//    private void getLocation() {
//
//        if (permissionStatus) {
//
//            try
//            {
//
//                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//            }
//            catch (SecurityException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//    public Address getAddress(double latitude, double longitude)
//    {
//        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(this, Locale.getDefault());
//
//        try {
//            addresses = geocoder.getFromLocation(latitude,longitude, 5); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            return addresses.get(0);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//
//    }
//    public void getAddress()
//    {
//
//       // prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        double Long= Double.parseDouble(permissionStatus.getString("long",  "0.00000"));
//        double lat= Double.parseDouble(permissionStatus.getString("lat",  "0.00000"));
//
//
//        Address locationAddress=getAddress(lat,Long);
//
//        if(locationAddress!=null)
//        {
//            String address = locationAddress.getAddressLine(0);
//            String address1 = locationAddress.getAddressLine(1);
//            String city = locationAddress.getLocality();
//            String state = locationAddress.getAdminArea();
//            String district = locationAddress.getSubAdminArea();
//            String country = locationAddress.getCountryName();
//            String postalCode = locationAddress.getPostalCode();
//
//
//            if(!TextUtils.isEmpty(address))
//            {
//                currentLocation=address;
//
//                if (!TextUtils.isEmpty(address1))
//                    currentLocation+="\n"+address1;
//
//
//                else
//                {
//                    if (!TextUtils.isEmpty(postalCode))
//                        currentLocation+="\n"+postalCode;
//                }
//
//                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
//                prefEditor.putString("location", currentLocation);
//                prefEditor.apply();
//            }
//        }
//
//    }
    @Override
    public void onLocationChanged(Location location) {
       /* SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        prefEditor.putString("long", String.valueOf(location.getLongitude()));
        prefEditor.putString("lat", String.valueOf(location.getLatitude()));
        prefEditor.apply();*/
        // txtLat="Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude();
        //Toast.makeText(MainActivity.this, "thee "+location.getLatitude() + ", Longitude:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                //restart this activity
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                // proceedAfterPermission();

            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                //txtPermissions.setText("Permissions Required");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.mipmap.ic_launcher_round);
                builder.setTitle("LakeSyde Farms Needs Permission");
                builder.setMessage("LakeSyde Farms needs Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED

            ) {
                //Got Permission
                //restart this activity
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);


            getLastLocation();

        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                prefEditor.putString("longg", String.valueOf(location.getLongitude()));
                                prefEditor.putString("latt", String.valueOf(location.getLatitude()));
                                prefEditor.apply();

                            }
                        }
                    }
            );
        } else {

            //Show Information about why you need the permission
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.mipmap.ic_launcher_round);
            builder.setTitle("LakeSyde Farms Need Your Location");
            builder.setMessage("You need to turn on your GPS for LakeSyde to determine your location");
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        //mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            {
                if (exit) {
                    finish(); // finish activity
                } else {
//                    Toast.makeText(this, "Press Back again to Exit.",
//                            Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 3000);

                }
            }
        } else if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
            {
                if (exit) {
                    finish(); // finish activity
                } else {
                    Toast.makeText(this, "Press Back again to Exit.",
                            Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 3000);

                }
            }
        }
    }

}

