package com.example.miniresearchdatabase.Notifications;

//reference: https://github.com/KODDevYouTube/ChatAppTutorial/tree/master/app/src/main/java/com/koddev/chatapp/Notifications
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
