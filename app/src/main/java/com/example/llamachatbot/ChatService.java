package com.example.llamachatbot;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatService {
    @Headers({"Accept: application/json"})
    @POST("chat")
    Call<ChatResponse> sendMessage(@Body JSONObject chatRequest);
}
