package com.jessicathornsby.authenticated;


import com.jessicathornsby.authenticated.pojo.AuthMessage;
import com.jessicathornsby.authenticated.pojo.AuthRequest;
import com.jessicathornsby.authenticated.pojo.SimpleResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GetAuthenticatedData {

    @POST("/auth")
    Call<AuthMessage> getAuthentication(@Body AuthRequest authRquest);

    @GET("/protected")
    Call<SimpleResponse>  getAuthenticatedRoute(@Header("Authorization")String bearerToken);

    @GET("/admin")
    Call<SimpleResponse> checkAdmin(@Header("Authorization")String bearerToken);
}
