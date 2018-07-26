package com.example.valtron.siyaya_rider.Common;

import com.example.valtron.siyaya_rider.Model.Rider;
import com.example.valtron.siyaya_rider.Remote.FCMClient;
import com.example.valtron.siyaya_rider.Remote.GoogleMapsAPI;
import com.example.valtron.siyaya_rider.Remote.IFCMService;
import com.example.valtron.siyaya_rider.Remote.IGoogleAPI;
import com.example.valtron.siyaya_rider.Remote.RetrofitClient;

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
}
