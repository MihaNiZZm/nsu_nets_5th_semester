package ru.nsu.fit.mihanizzm.api_services;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.mihanizzm.data.*;
import org.json.*;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GetLocation implements Runnable {
    private final LocationsList locationsList;
    private static final String APIKey = "6749932f-aa47-4d9c-a531-a9db75dc4416";

    public GetLocation(LocationsList locationsList) {
        this.locationsList = locationsList;
    }

    public void makeRequestAsync(String location) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://graphhopper.com/api/1/geocode?q=" + location + "&locale=ru&key=" + APIKey)
                .addHeader("Accept-Charset", "UTF-8")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    assert responseBody != null;
                    JSONArray jsonArray = new JSONObject(responseBody.string()).getJSONArray("hits");

                    int locationNumber = 1;
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = (JSONObject) o;

                        double lng, lat;
                        String name, country, city, postCode, osmKey;

                        lng = jsonObject.getJSONObject("point").getDouble("lng");
                        lat = jsonObject.getJSONObject("point").getDouble("lat");
                        name = jsonObject.getString("name");
                        osmKey = jsonObject.getString("osm_key");
                        country = jsonObject.getString("country");
                        try {
                            city = jsonObject.getString("city");
                        } catch (JSONException e) {
                            city = "unknown";
                        }

                        try {
                            postCode = jsonObject.getString("postcode");
                        } catch (JSONException e) {
                            postCode = "unknown";
                        }

                        locationsList.getMap().put(locationNumber, new LocationInfo(name, new Coordinates(lng, lat)));

                        System.out.println(locationNumber + ")\nName: " + name + ", type: " + osmKey
                                + "\nCountry: " + country + ", city: " + city + ", postcode: " + postCode
                                + "\nLatitude: " + lat + ", longitude: " + lng + "\n");
                        ++locationNumber;
                    }
                    System.out.print("Choose one of these locations. Print an integer number from 1 to 5: ");
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

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.print("Enter location: ");

        String location = scanner.nextLine();
        makeRequestAsync(location);
    }
}
