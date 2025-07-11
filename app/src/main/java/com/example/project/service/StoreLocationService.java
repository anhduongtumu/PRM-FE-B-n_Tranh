package com.example.project.service;

import com.example.project.model.StoreLocation;
import com.example.project.dto.storeLocation.CreateStoreLocationDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StoreLocationService {
    @GET("/api/storelocations")
    Call<List<StoreLocation>> getAllStoreLocations();

    @GET("/api/storelocations/{id}")
    Call<StoreLocation> getStoreLocationById(@Path("id") int id);

    @POST("/api/storelocations")
    Call<StoreLocation> createStoreLocation(@Body CreateStoreLocationDto storeLocation);

    @PUT("/api/storelocations/{id}")
    Call<StoreLocation> updateStoreLocation(@Path("id") int id, @Body CreateStoreLocationDto storeLocation);

    @DELETE("/api/storelocations/{id}")
    Call<Void> deleteStoreLocation(@Path("id") int id);
}
