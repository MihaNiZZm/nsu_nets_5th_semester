package ru.nsu.fit.mihanizzm.api_services;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.nsu.fit.mihanizzm.data.LocationsList;

import java.io.IOException;

public class GetPlacesAndDescriptions implements Runnable, Callback {
    private final LocationsList locationsList;
    private static final String API_KEY = "5ae2e3f221c38a28845f05b606ae16724d7d953eeaf1905d83dcedb2";
    private static final int RADIUS = 500;

    public GetPlacesAndDescriptions(LocationsList locationsList) {
        this.locationsList = locationsList;
    }

    private String HTMLToString(String str) {
        Document document = Jsoup.parse(str);
        return document.text();
    }

    private void getPlacesAsync() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.opentripmap.com/0.1/en/places/radius?" +
                        "radius=" + RADIUS +
                        "&lon=" + locationsList.getSelectedLocation().coordinates().lon() +
                        "&lat=" + locationsList.getSelectedLocation().coordinates().lat() +
                        "&apikey=" + API_KEY +
                        "&lang=ru")
                .get()
                .build();

        client.newCall(request).enqueue(this);
    }

    private void getDescriptionsAsync(JSONArray features) {
        OkHttpClient client = new OkHttpClient();

        System.out.println("\nInteresting places in this area:");
        for (Object feature : features) {
            JSONObject jsonFeature = (JSONObject) feature;

            String placeName = jsonFeature.getJSONObject("properties").getString("name");
            if (!placeName.equals("")) {
                Request request = new Request.Builder()
                        .url("https://api.opentripmap.com/0.1/en/places/xid/" +
                                jsonFeature.getJSONObject("properties").getString("xid") +
                                "?apikey=" + API_KEY +
                                "&lang=ru")
                        .get()
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            assert responseBody != null;
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if (jsonObject.has("info")) {
                                JSONObject infoObject = jsonObject.getJSONObject("info");
                                String descr = HTMLToString(infoObject.getString("descr"));
                                System.out.println(placeName + " - " + descr);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void makeRequestAsync() {
        getPlacesAsync();
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
            assert responseBody != null;
            JSONArray features = new JSONObject(responseBody.string()).getJSONArray("features");
            getDescriptionsAsync(features);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        e.printStackTrace();
    }

    @Override
    public void run() {
        makeRequestAsync();
    }
}
