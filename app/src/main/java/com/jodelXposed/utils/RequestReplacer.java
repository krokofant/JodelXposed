package com.jodelXposed.utils;

import org.apache.commons.io.Charsets;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import static com.jodelXposed.utils.Log.xlog;

public class RequestReplacer {
    public static Pattern[] processables = {
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/users/location"),
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/users/"),
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/posts/"),
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/posts/location/combo\\?lat=\\d+\\.\\d+&lng=\\d+\\.\\d+"),
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/posts/location/\\?skip=\\d*&limit=\\d*\\&lat=\\d+.\\d+&lng=\\d+\\.\\d+"),
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/posts/location/(discussed|popular)?\\?after=\\w*\\&lat=\\d+.\\d+&lng=\\d+\\.\\d+")
    };

    public static boolean processable(String url) {
        for (Pattern p : processables) {
            if (p.matcher(url).matches())
                return true;
        }
        return false;
    }

    public static String processURL(String url) {
        Options options = Options.getInstance();

        if (!options.getLocationObject().isActive()) {
            xlog("Feature not active");
            return url;
        }
        url = url.replaceAll("lat=\\d+\\.\\d+", "lat=" + String.valueOf(options.getLocationObject().getLat()));
        url = url.replaceAll("lng=\\d+\\.\\d+", "lng=" + String.valueOf(options.getLocationObject().getLng()));
        xlog("Url processed");
        return url;
    }

    public static byte[] processBody(byte[] jsonBytes) {
        Options options = Options.getInstance();

            if (!options.getLocationObject().isActive()) {
                xlog("Feature not active");
                return jsonBytes;
            }

        try {
            JSONObject jsonObject = new JSONObject(new String(jsonBytes));
            JSONObject location = jsonObject
                .getJSONObject("location")
                .put("country", options.getLocationObject().getCountry())
                .put("name", options.getLocationObject().getCity())
                .put("city", options.getLocationObject().getCity());
            location.getJSONObject("loc_coordinates")
                .put("lat", options.getLocationObject().getLat())
                .put("lng", options.getLocationObject().getLng());
            xlog("Place JSON: " + jsonObject.toString());
            xlog("Body processed");
            return jsonObject.toString().getBytes(Charsets.UTF_8);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonBytes;
    }
}
