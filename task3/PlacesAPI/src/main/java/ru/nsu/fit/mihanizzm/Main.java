package ru.nsu.fit.mihanizzm;

import ru.nsu.fit.mihanizzm.api_services.GetLocation;
import ru.nsu.fit.mihanizzm.api_services.GetPlacesAndDescriptions;
import ru.nsu.fit.mihanizzm.api_services.GetWeather;
import ru.nsu.fit.mihanizzm.data.LocationsList;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LocationsList locationsList = new LocationsList(new HashMap<>());

        CompletableFuture<Void> completableFuture = CompletableFuture
                .runAsync(new GetLocation(locationsList))
                .thenRunAsync(new GetWeather(locationsList))
                .thenRunAsync(new GetPlacesAndDescriptions(locationsList));

        completableFuture.get();
    }
}