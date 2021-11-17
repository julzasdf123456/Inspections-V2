package com.lopez.julz.inspectionv2.api;

import com.lopez.julz.inspectionv2.classes.Login;
import com.lopez.julz.inspectionv2.database.LocalServiceConnectionInspections;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestPlaceHolder {

    @POST("login")
    Call<Login> login(@Body Login login);

    @GET("get-service-connections")
    Call<List<ServiceConnections>> getServiceConnections(@Query("userid") String userid);

    @GET("get-service-inspections")
    Call<List<ServiceConnectionInspections>> getServiceConnectionInspections(@Query("userid") String userid);

    @POST("update-service-inspections")
    Call<LocalServiceConnectionInspections> updateServiceConnections(@Body LocalServiceConnectionInspections localServiceConnectionInspections);
}
