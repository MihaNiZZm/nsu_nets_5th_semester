package ru.nsu.fit.mihanizzm.api_services;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.nsu.fit.mihanizzm.data.LocationInfo;
import ru.nsu.fit.mihanizzm.data.LocationsList;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GetWeather implements Runnable {
    private static final String API_KEY = "340ca9d9016f0f8d32431ddd6f99984b";
    private static final String UNIT_SYSTEM = "metric";
    private static final String REQUEST_LANGUAGE = "ru";
    private static final double HPA_TO_MERCURY_COEFFICIENT = 0.750064;
    private final LocationsList locationsList;
    public GetWeather(LocationsList locationsList) {
        this.locationsList = locationsList;
    }

    private void handleJSONError(JSONObject json) {
        int errorCode = json.getInt("cod");
        String errorMessage = json.getString("message");
        throw new RuntimeException("Couldn't get info from JSON. Error code: " + errorCode
                + ", error message: \"" + errorMessage + "\"");
    }
    private void makeRequestAsync() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?lat="
                        + locationsList.getSelectedLocation().coordinates().lat()
                        + "&lon="
                        + locationsList.getSelectedLocation().coordinates().lon()
                        + "&appid="
                        + API_KEY
                        + "&units="
                        + UNIT_SYSTEM
                        + "&lang="
                        + REQUEST_LANGUAGE)
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
                    JSONObject receivedJSON = new JSONObject(responseBody.string());
                    if (receivedJSON.getInt("cod") >= 300) {
                        handleJSONError(receivedJSON);
                    }

                    JSONObject jsonMain = receivedJSON.getJSONObject("main");
                    JSONObject jsonWind = receivedJSON.getJSONObject("wind");

                    JSONArray weatherArray = receivedJSON.getJSONArray("weather");
                    String weatherDescription = weatherArray.getJSONObject(0).getString("description");

                    double temp = jsonMain.getDouble("temp");
                    double feelsLike = jsonMain.getDouble("feels_like");

                    double pressure = jsonMain.getDouble("pressure") * HPA_TO_MERCURY_COEFFICIENT;

                    double humidity = jsonMain.getDouble("humidity");

                    int visibility = receivedJSON.getInt("visibility");

                    double windSpeed = jsonWind.getDouble("speed");

                    double windDirection = jsonWind.getInt("deg");

                    String formattedPressure = String.format("%.2f", pressure);

                    System.out.print("\nThe weather in chosen location.\n"
                            + weatherDescription + "\n"
                            + "Temperature: " + temp + "°C, feels like " + feelsLike + "°C\n"
                            + "Atmospheric pressure: " + formattedPressure + "mm of mercury\n"
                            + "Humidity: " + humidity + "%\n"
                            + "Visibility: " + visibility + "m\n"
                            + "Wind speed: " + windSpeed + "m/s\n"
                            + "Wind direction: " + windDirection + "°\n");
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
        int locationNumber = scanner.nextInt();
        if (locationNumber < 1 || locationNumber > locationsList.getMap().size()) {
            throw new RuntimeException("Invalid location number");
        }

        LocationInfo location = this.locationsList.getMap().get(locationNumber);
        this.locationsList.setSelectedLocation(location);

        makeRequestAsync();
    }
}
