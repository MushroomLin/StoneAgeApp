package com.example.miniresearchdatabase.fragment;

import com.example.miniresearchdatabase.Notifications.MyResponse;
import com.example.miniresearchdatabase.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAcAQ3VR0:APA91bEcbjP1dSl4_MFYpGpMN5iLSzDijyOXTii2vRZo0W4kTxIIhTkfWSVKcrSUBcmXKq9ZRzqMVCyKeRr3h0fzwwS7owd63-1Y13lBghBrKWnIqqc68HbhyIK57R-5OHAWbeqMhc_X"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
