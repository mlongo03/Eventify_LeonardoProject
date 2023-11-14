package com.eventify.app.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    @Value("${google.maps.apikey}")
    private String googleMapsApiKey;

    public double[] getCoordinatesFromAddress(String address) {
        try {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(googleMapsApiKey)
                    .build();

            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();

            if (results != null && results.length > 0) {
                double latitude = results[0].geometry.location.lat;
                double longitude = results[0].geometry.location.lng;
                return new double[]{latitude, longitude};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
