package com.trackmapoop.web;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.trackmapoop.data.BRConstants;

import java.lang.reflect.Type;

import retrofit.RestAdapter;

/**
 * Created by mike on 8/13/14.
 */
public class BRWebClient {
    private static String TAG = "BR_WEB_CLIENT";

    private Context context;

    public BRWebClient(Context context)
    {
        this.context = context;
    }

    public static GsonBuilder gsonBuilder()
    {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Boolean.class, new JsonDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException
            {
                String wheelChair = json.getAsString();
                if (wheelChair.length() > 0 && wheelChair.equalsIgnoreCase("yes"))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        return builder;
    }

    public BRWebService buildRequest()
    {
        try
        {
            // Just for now since we're only making one web call
            String protocol = BRConstants.HTTP;

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(protocol + BRConstants.NEAREST_BR_API_HOST).build();

            return restAdapter.create(BRWebService.class);
        }
        catch(Exception e)
        {
            Log.e("Exception caught building web request", e.getMessage());
        }

        return null;
    }
}
