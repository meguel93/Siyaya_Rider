package com.example.valtron.siyaya_rider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.valtron.siyaya_rider.Common.Common;
import com.example.valtron.siyaya_rider.Helper.CustomInfoWindow;
import com.example.valtron.siyaya_rider.Model.DataMessage;
import com.example.valtron.siyaya_rider.Model.FCMResponse;
import com.example.valtron.siyaya_rider.Model.Rider;
import com.example.valtron.siyaya_rider.Model.Token;
import com.example.valtron.siyaya_rider.Remote.IFCMService;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.valtron.siyaya_rider.Common.Common.current_user;
import static com.example.valtron.siyaya_rider.Common.Common.mLastLocation;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        ValueEventListener,
        GoogleMap.OnInfoWindowClickListener {

    SupportMapFragment mapFragment;

    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;


    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICE_RES_REQUEST = 300193;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    //private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference ref;
    GeoFire geoFire;

    Marker mUserMarker, DestinationMarker;

    RadioGroup radioGroup;
    RadioButton radioButton;


    ButtonSheetRiderFragment mButtonSheet;

    /*ImageView imgExpandable;*/
    //Button btnPickupRequest;

    boolean isDriverFound = false;
    int radius = 1; //In km
    int distance = 1;
    private static final int LIMIT = 3;

    IFCMService mService;

    DatabaseReference availableDrivers;

    PlaceAutocompleteFragment place_location, place_destination;

    AutocompleteFilter typeFilter;

    String mPlaceLocation, mPlaceDestination;

    CircleImageView imageAvatar;
    TextView txtRiderName, txtStars, txtHeading, txtFare, txtRink,
    txtTime;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    //BottomAppBar bottomAppBar;
    View bottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior1;
    LinearLayout tapactionlayout;
    FloatingActionButton floatingActionButton;
    String route_ = "";

    private BroadcastReceiver mCancelBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Common.driverId = "";
            isDriverFound = false;

            floatingActionButton.setBackgroundResource(R.drawable.ic_phone_24dp);
            floatingActionButton.setBackgroundColor(Color.parseColor("#0652DD"));
            floatingActionButton.setEnabled(true);
            mUserMarker.hideInfoWindow();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        floatingActionButton = findViewById(R.id.fab);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mCancelBroadCast, new IntentFilter(Common.CANCEL_BROADCAST_STRING));

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mCancelBroadCast, new IntentFilter(Common.BROADCAST_ARRIVED));

        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = this.getWindow();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        mService = Common.getFCMService();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        tapactionlayout = (LinearLayout) findViewById(R.id.tap_action_layout);
        bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(100);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tapactionlayout.setVisibility(View.VISIBLE);
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tapactionlayout.setVisibility(View.GONE);
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tapactionlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        tapactionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBottomSheetBehavior1.getState()==BottomSheetBehavior.STATE_COLLAPSED)
                {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_route = inflater.inflate(R.layout.layout_update_route, null);

        final RadioButton town = layout_route.findViewById(R.id.town_route);
        final RadioButton central = layout_route.findViewById(R.id.central_route);
        final RadioButton summer = layout_route.findViewById(R.id.summer_route);
        final RadioButton Forrest = layout_route.findViewById(R.id.forest_route);
        final RadioButton green = layout_route.findViewById(R.id.green_route);

        txtHeading = findViewById(R.id.heading);
        txtFare = findViewById(R.id.txt_payment);
        txtRink = findViewById(R.id.txt_rink);
        txtTime = findViewById(R.id.txt_operation_times);

        switch (current_user.getRoute()) {
            case "Town":
                town.setChecked(true);
                route_ = "Town";
                availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Town");
                availableDrivers.addValueEventListener(Home.this);
                break;
            case "Central":
                central.setChecked(true);
                route_ = "Central";
                availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Central");
                availableDrivers.addValueEventListener(Home.this);
                break;
            case "Summerstrand":
                summer.setChecked(true);
                route_ = "Summerstrand";
                availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Summerstrand");
                availableDrivers.addValueEventListener(Home.this);
                break;
            case "Forrest Hill":
                Forrest.setChecked(true);
                route_ = "Forrest Hill";
                availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Forrest Hill");
                availableDrivers.addValueEventListener(Home.this);
                break;
            case "Greenacres":
                green.setChecked(true);
                route_ = "Greenacres";
                availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Greenacres");
                availableDrivers.addValueEventListener(Home.this);
                break;
        }
        UpdateBottomSheet(route_);
        /*if (availableDrivers !=null)
            availableDrivers.removeEventListener(Home.this);*/
        View navigationHeaderView = navigationView.getHeaderView(0);
        txtRiderName = (TextView) navigationHeaderView.findViewById(R.id.txtRiderName);
        txtRiderName.setText(Common.current_user.getName());
        txtStars = (TextView) navigationHeaderView.findViewById(R.id.txtRating);
        txtStars.setText("0.0");
        imageAvatar = (CircleImageView) navigationHeaderView.findViewById(R.id.imageAvatar);

        if (Common.current_user.getAvatarUrl() != null && !TextUtils.isEmpty(Common.current_user.getAvatarUrl()))
            Picasso.with(this)
                    .load(Common.current_user.getAvatarUrl())
                    .into(imageAvatar);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDriverFound)
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            requestPickupHere(account.getId());
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                        }
                    });
                else {
                    floatingActionButton.setEnabled(false);
                    Common.sendRequestToDriver(Common.driverId, mService, getBaseContext(), mLastLocation);
                }
            }
        });

        place_destination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_destination);
        place_location = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_location);
        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(3)
                .build();

        place_location.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mPlaceLocation = place.getAddress().toString();

                mMap.clear();

                mUserMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                        .icon(BitmapDescriptorFactory.defaultMarker())
                        .title("Pickup here"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));
            }

            @Override
            public void onError(Status status) {

            }
        });
        place_destination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mPlaceDestination = place.getAddress().toString();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));

                /*ButtonSheetRiderFragment mBottomSheet = ButtonSheetRiderFragment.newInstance(mPlaceLocation, mPlaceDestination, false);
                mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());*/
            }

            @Override
            public void onError(Status status) {

            }
        });
        setUpLocation();

        updateFirebaseToken();
    }

    private void updateFirebaseToken() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                final DatabaseReference tokens = db.getReference(Common.token_tbl);

                FirebaseInstanceId.getInstance()
                        .getInstanceId()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                Token token = new Token(instanceIdResult.getToken());
                                tokens.child(account.getId())
                                        .setValue(token);
                            }
                        });
            }

            @Override
            public void onError(AccountKitError accountKitError) {

            }
        });
    }

    //BottomSheet

    private void requestPickupHere(String uid) {
        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);
        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Fix crash
                    }
                });

        if (mUserMarker.isVisible())
            mUserMarker.remove();

        mUserMarker = mMap.addMarker(new MarkerOptions()
                .title("Pickup Here")
                .snippet("")
                .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mUserMarker.showInfoWindow();

        floatingActionButton.setBackgroundResource(R.drawable.ic_phone_in_talk_black_24dp);
        Toast.makeText(Home.this, "Getting your driver", Toast.LENGTH_SHORT).show();
        //btnPickupRequest.setText("Getting your Driver...");

        findDriver();
    }

    private void findDriver() {
        DatabaseReference driverLocation;
        switch (route_) {
            case "Town":
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Town");
                break;
            case "Summerstrand":
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Summerstrand");
                break;
            case "Forrest Hill":
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Forrest Hill");
                break;
            case "Central":
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Central");
                break;
            default:
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Greenacres");
                break;
        }
        GeoFire gf = new GeoFire(driverLocation);

        final GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),
                mLastLocation.getLongitude()), radius);

        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!isDriverFound) {
                    isDriverFound = true;
                    Common.driverId = key;
                    floatingActionButton.setBackgroundColor(Color.parseColor("#32ff7e"));
                    //btnPickupRequest.setText("CALL DRIVER");
                    //Toast.makeText(Home.this, ""+key, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!isDriverFound && radius < LIMIT) {
                    radius++;
                    findDriver();
                } else {
                    Toast.makeText(Home.this, "No available driver near you", Toast.LENGTH_SHORT).show();
                    floatingActionButton.setBackgroundResource(R.drawable.ic_phone_24dp);
                    floatingActionButton.setBackgroundColor(Color.parseColor("#0652DD"));
                    geoQuery.removeAllListeners();
                    //btnPickupRequest.setText("REQUEST PICKUP");
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpLocation();
                }
                break;
        }
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            //buildLocationRequest();
            buildLocationCallBack();
            createLocationRequest();
            displayLocation();
        }
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLocations().get(locationResult.getLocations().size() - 1);
                displayLocation();
            }
        };
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLastLocation = location;
                if (mLastLocation != null) {
                    LatLng center = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    LatLng northSide = SphericalUtil.computeOffset(center, 100000, 0);
                    LatLng southSide = SphericalUtil.computeOffset(center, 100000, 180);

                    LatLngBounds bounds = LatLngBounds.builder()
                            .include(northSide)
                            .include(southSide)
                            .build();

                    place_location.setBoundsBias(bounds);
                    place_location.setFilter(typeFilter);

                    place_destination.setBoundsBias(bounds);
                    place_destination.setFilter(typeFilter);

                    switch (route_) {
                        case "Town":
                            availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Town");
                            break;
                        case "Summerstrand":
                            availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Summerstrand");
                            break;
                        case "Forrest Hill":
                            availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Forrest Hill");
                            break;
                        case "Central":
                            availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Central");
                            break;
                        default:
                            availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Greenacres");
                            break;
                    }
                    availableDrivers.addValueEventListener(Home.this);

                    final double latitude = mLastLocation.getLatitude();
                    final double longitude = mLastLocation.getLongitude();
                    //Comment os
                    if (mUserMarker != null)
                        mUserMarker.remove();
                    mUserMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(String.format("Your Location")));

                    /*.icon(BitmapDescriptorFactory.fromResource(R.drawable.top))*/
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

                    //rotateMarker(mCurrent,-360,mMap);

                    loadAllAvailableDrivers(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    Log.d("EDMTDEV", String.format("Your location was changed : %f / %f", latitude, longitude));
                } else {
                    Log.d("ERROR", "Can not get your location");
                }
            }
        });

    }

    private void loadAllAvailableDrivers(final LatLng location) {

        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(location)
                .title("You"));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));

        DatabaseReference driverLocation;
        switch (route_) {
            case "Town":
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Town");
                break;
            case "Summerstrand":
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Summerstrand");
                break;
            case "Forrest Hill":
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Forrest Hill");
                break;
            case "Central":
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Central");
                break;
            default:
                driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Greenacres");
                break;
        }
        GeoFire gf = new GeoFire(driverLocation);

        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(location.latitude, location.longitude), distance);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Rider rider = dataSnapshot.getValue(Rider.class);

                                switch (route_) {
                                    case "Town":
                                        if(rider.getRoute().equals("Town")){
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(location.latitude, location.longitude))
                                                    .flat(true)
                                                    .title(rider.getName())
                                                    .snippet("Driver ID : " + dataSnapshot.getKey())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.top)));

                                        }
                                        break;
                                    case "Summerstrand":
                                        if(rider.getRoute().equals("Summerstrand")){
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(location.latitude, location.longitude))
                                                    .flat(true)
                                                    .title(rider.getName())
                                                    .snippet("Driver ID : " + dataSnapshot.getKey())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.top)));

                                        }
                                        break;
                                    case "Forrest Hill":
                                        if(rider.getRoute().equals("Forrest Hill")){
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(location.latitude, location.longitude))
                                                    .flat(true)
                                                    .title(rider.getName())
                                                    .snippet("Driver ID : " + dataSnapshot.getKey())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.top)));

                                        }
                                        break;
                                    case "Central":
                                        if(rider.getRoute().equals("Central")){
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(location.latitude, location.longitude))
                                                    .flat(true)
                                                    .title(rider.getName())
                                                    .snippet("Driver ID : " + dataSnapshot.getKey())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.top)));

                                        }
                                        break;
                                    case "Greenacres":
                                        if(rider.getRoute().equals("Greenacres")){
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(location.latitude, location.longitude))
                                                    .flat(true)
                                                    .title(rider.getName())
                                                    .snippet("Driver ID : " + dataSnapshot.getKey())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.top)));

                                        }
                                        break;
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= LIMIT) {
                    distance++;
                    loadAllAvailableDrivers(location);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_trip_history) {
            // Handle the camera action
        } else if (id == R.id.nav_route) {
            showRouteDialog();

        } else if (id == R.id.nav_update_info) {
            showUpdateInfoDialog();

        } else if (id == R.id.nav_change_pwd) {

        } else if (id == R.id.nav_sing_out) {
            signout();
        }
        /*
        if(id == R.id.place_city_mode){
            ((SwitchCompat)item.getActionView()).toggle();
            if(((SwitchCompat)item.getActionView()).isChecked())
                Toast.makeText(this, "On", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Off", Toast.LENGTH_SHORT).show();

        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showRouteDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose Your Route");
        //dialog.setMessage("Please use email to sign in");

        final LayoutInflater inflater = LayoutInflater.from(this);
        View layout_route = inflater.inflate(R.layout.layout_update_route, null);

        final RadioButton town = layout_route.findViewById(R.id.town_route);
        final RadioButton central = layout_route.findViewById(R.id.central_route);
        final RadioButton summer = layout_route.findViewById(R.id.summer_route);
        final RadioButton Forrest = layout_route.findViewById(R.id.forest_route);
        final RadioButton green = layout_route.findViewById(R.id.green_route);

        switch (current_user.getRoute()) {
            case "Town":
                town.setChecked(true);
                route_ = "Town";
                break;
            case "Central":
                central.setChecked(true);
                route_ = "Central";
                break;
            case "Summerstrand":
                summer.setChecked(true);
                route_ = "Summerstrand";
                break;
            case "Forrest Hill":
                Forrest.setChecked(true);
                route_ = "Forrest Hill";
                break;
            case "Greenacres":
                green.setChecked(true);
                route_ = "Greenacres";
                break;
        }

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {

                Map<String, Object> updateInfo = new HashMap<>();
                if (town.isChecked())
                    updateInfo.put("route", town.getText().toString());
                else if (central.isChecked())
                    updateInfo.put("route", central.getText().toString());
                else if (summer.isChecked())
                    updateInfo.put("route", summer.getText().toString());
                else if (Forrest.isChecked())
                    updateInfo.put("route", Forrest.getText().toString());
                else if (green.isChecked())
                    updateInfo.put("route", green.getText().toString());

                DatabaseReference CommuterInformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                CommuterInformation.child(account.getId())
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(Home.this, "Route Updated!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(Home.this, "Route Updated error!", Toast.LENGTH_SHORT).show();

                                //waitingDialog.dismiss();
                            }
                        });
                CommuterInformation.child(account.getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                current_user = dataSnapshot.getValue(Rider.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onError(AccountKitError accountKitError) {

            }
        });

        dialog.setView(layout_route);

        dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {

                        Map<String, Object> updateInfo = new HashMap<>();
                        if (town.isChecked()) {
                            updateInfo.put("route", town.getText().toString());
                        }
                        else if (central.isChecked()) {
                            updateInfo.put("route", central.getText().toString());}
                        else if (summer.isChecked()) {
                            updateInfo.put("route", summer.getText().toString());}
                        else if (Forrest.isChecked()) {
                            updateInfo.put("route", Forrest.getText().toString());}
                        else if (green.isChecked()) {
                            updateInfo.put("route", green.getText().toString());
                            }

                        mMap.clear();
                        if(availableDrivers != null)
                            availableDrivers.removeEventListener(Home.this);
                        availableDrivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child(route_);
                        availableDrivers.addValueEventListener(Home.this);
                        loadAllAvailableDrivers(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                        UpdateBottomSheet(route_);

                        DatabaseReference CommuterInformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                        CommuterInformation.child(account.getId())
                                .updateChildren(updateInfo)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = getIntent();
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            finish();
                                            overridePendingTransition( 0, 0);
                                            startActivity(intent);
                                            overridePendingTransition( 0, 0);
                                            Toast.makeText(Home.this, "Route Updated!", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(Home.this, "Route Updated error!", Toast.LENGTH_SHORT).show();

                                        waitingDialog.dismiss();
                                    }
                                });
                        CommuterInformation.child(account.getId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        current_user = dataSnapshot.getValue(Rider.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {

                    }
                });
                waitingDialog.dismiss();

                mMap.clear();
                loadAllAvailableDrivers(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showUpdateInfoDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Edit Account");
        //dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_editInfo = inflater.inflate(R.layout.layout_edit_information, null);

        final MaterialEditText edtName = layout_editInfo.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = layout_editInfo.findViewById(R.id.edtPhone);
        final ImageView profile_picture = layout_editInfo.findViewById(R.id.image_upload);
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        dialog.setView(layout_editInfo);

        dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                        waitingDialog.show();

                        String name = edtName.getText().toString();
                        String phone = edtPhone.getText().toString();

                        Map<String, Object> updateInfo = new HashMap<>();
                        if (!TextUtils.isEmpty(name))
                            updateInfo.put("name", name);
                        if (!TextUtils.isEmpty(phone))
                            updateInfo.put("phone", phone);

                        DatabaseReference CommuterInformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                        CommuterInformation.child(account.getId())
                                .updateChildren(updateInfo)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            Toast.makeText(Home.this, "Account Updated!", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(Home.this, "Account Updated error!", Toast.LENGTH_SHORT).show();

                                        waitingDialog.dismiss();
                                    }
                                });
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {

                    }
                });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture: "), Common.PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri saveUri = data.getData();
            if (saveUri != null) {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Uploading...");
                mDialog.show();

                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("images/" + imageName);
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();
                                //Toast.makeText(Home.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                                            @Override
                                            public void onSuccess(Account account) {
                                                Map<String, Object> avatarUpdate = new HashMap<>();
                                                avatarUpdate.put("avatarUrl", uri.toString());

                                                DatabaseReference CommuterInformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                                                CommuterInformation.child(account.getId())
                                                        .updateChildren(avatarUpdate)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                    Toast.makeText(Home.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                                                                else
                                                                    Toast.makeText(Home.this, "Uploaded error!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                            }

                                            @Override
                                            public void onError(AccountKitError accountKitError) {

                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mDialog.setMessage("Uploaded " + progress + "%");
                            }
                        });
            }
        }
    }

    private void signout() {
        AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(this);

        builder = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccountKit.logOut();
                        Intent intent = new Intent(Home.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean isSuccess = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.dull_map)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (DestinationMarker != null)
                    DestinationMarker.remove();
                DestinationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .position(latLng)
                        .title("Destination"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                //ButtonSheetRiderFragment mBottomSheet = new ButtonSheetRiderFragment();
                floatingActionButton.show();
            }
        });

        mMap.setOnInfoWindowClickListener(this);

        if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        /*
        googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(37.7750,-122.4183))
                    .title("Humewood"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.7750,-122.4183), 12));*/
    }

    @SuppressLint("SetTextI18n")
    public void UpdateBottomSheet(String route) {
        txtHeading.setText(route);
        switch (route) {
            case "Town":
                txtFare.setText("R10");
                txtTime.setText("06:00 - 19:00");
                txtRink.setText("Town");
                break;
            case "Central":
                txtFare.setText("R10");
                txtTime.setText("06:00 - 19:00");
                txtRink.setText("Town");
                break;
            case "Summerstrand":
                txtFare.setText("R12");
                txtTime.setText("06:00 - 19:00");
                txtRink.setText("Town");
                break;
            case "Forrest Hill":
                txtFare.setText("R10");
                txtTime.setText("06:00 - 19:00");
                txtRink.setText("Town");
                break;
            case "Greenacres":
                txtFare.setText("R10");
                txtTime.setText("06:00 - 19:00");
                txtRink.setText("Town");
                break;
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        //loadAllAvailableDrivers(new LatLng(mLastLocation.getLatitude(),
                //mLastLocation.getLongitude()));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(!marker.getTitle().equals("You")) {
            Intent intent = new Intent(Home.this, CallDriver.class);
            intent.putExtra("driverId", marker.getSnippet().replaceAll("\\D+", ""));
            intent.putExtra("lat", mLastLocation.getLatitude());
            intent.putExtra("lng", mLastLocation.getLongitude());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCancelBroadCast);
        super.onDestroy();
    }
}









    /*private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }*/

/*
    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);
        if(radioButton.getText().equals("Town"))
            route_ = "Town";
        else if(radioButton.getText().equals("Central"))
            route_ = "Central";
        else if(radioButton.getText().equals("Summerstrand"))
            route_ = "Summerstrand";
        else if(radioButton.getText().equals("Forrest Hill"))
            route_ = "Forrest Hill";
        else if(radioButton.getText().equals("Greenacres"))
            route_ = "Greenacres";
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }
private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

 */