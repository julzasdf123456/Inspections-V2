package com.lopez.julz.inspectionv2.api;

import com.lopez.julz.inspectionv2.classes.Login;
import com.lopez.julz.inspectionv2.database.Barangays;
import com.lopez.julz.inspectionv2.database.LocalServiceConnectionInspections;
import com.lopez.julz.inspectionv2.database.MastPoles;
import com.lopez.julz.inspectionv2.database.PayTransactions;
import com.lopez.julz.inspectionv2.database.Photos;
import com.lopez.julz.inspectionv2.database.Towns;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @GET("get-barangays")
    Call<List<Barangays>> getBarangays();

    @GET("get-towns")
    Call<List<Towns>> getTowns();

    @Multipart
    @POST("save-uploaded-images")
    Call<ResponseBody> saveUploadedImages(@Query("svcId") String svcId, @Part MultipartBody.Part file);

    @POST("receive-mast-poles")
    Call<MastPoles> uploadMastPoles(@Body MastPoles mastPoles);

    @POST("receive-bill-deposits")
    Call<PayTransactions> receiveBillDeposits(@Body PayTransactions payTransactions);
}
