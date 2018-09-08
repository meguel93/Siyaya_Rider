package com.example.valtron.siyaya_rider.Common;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.valtron.siyaya_rider.Home;
import com.example.valtron.siyaya_rider.Model.DataMessage;
import com.example.valtron.siyaya_rider.Model.FCMResponse;
import com.example.valtron.siyaya_rider.Model.Rider;
import com.example.valtron.siyaya_rider.Model.Token;
import com.example.valtron.siyaya_rider.Remote.FCMClient;
import com.example.valtron.siyaya_rider.Remote.GoogleMapsAPI;
import com.example.valtron.siyaya_rider.Remote.IFCMService;
import com.example.valtron.siyaya_rider.Remote.IGoogleAPI;
import com.example.valtron.siyaya_rider.Remote.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Common {

    public static Rider current_user = new Rider();

    public static final String driver_tbl = "Drivers";
    public static final String user_driver_tbl = "DriversInformation";
    public static final String user_rider_tbl = "RidersInformation";
    public static final String pickup_request_tbl = "PickupRequest";
    public static final String token_tbl = "Tokens";

    public static final String fcmURL = "https://fcm.googleapis.com/";
    public static final String googleAPIURL = "https://maps.googleapis.com";

    private static double base_fare = 2.55;
    private static double time_rate = 2.55;
    private static double distance_rate = 2.55;
    public static final String user_field = "user";
    public static final String pwd_field = "password";
    public static final int PICK_IMAGE_REQUEST = 9999;
    public static Location mLastLocation = null;
    public static String driverId = "";

    public static double getPrice(double km, int min)
    {
        return (base_fare + (time_rate * min) + (distance_rate * km));
    }

    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

    public static IGoogleAPI getGoogleAPI()
    {
        return GoogleMapsAPI.getClient(googleAPIURL).create(IGoogleAPI.class);
    }

    public static void sendRequestToDriver(String driverId, final IFCMService mService,
                                           final Context context, final Location currentLocation) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);

        tokens.orderByKey().equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                            Token token = postSnapShot.getValue(Token.class);

                            // String json_lat_lng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                            String riderToken = FirebaseInstanceId.getInstance().getToken();

                            Map<String,String> content = new HashMap<>();
                            content.put("customer", riderToken);
                            content.put("lat", String.valueOf(currentLocation.getLatitude()));
                            content.put("lng", String.valueOf(currentLocation.getLongitude()));
                            DataMessage dataMessage = new DataMessage(token.getToken(), content);

                            mService.sendMessage(dataMessage)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            if (response.body().success == 1)
                                                Toast.makeText(context, "Request sent!", Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
