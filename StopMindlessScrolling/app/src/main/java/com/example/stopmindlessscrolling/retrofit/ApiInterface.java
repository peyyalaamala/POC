package com.example.stopmindlessscrolling.retrofit;


import com.example.stopmindlessscrolling.retrofit.model.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface ApiInterface {


    @GET
    Call<Example> getTopHeadlines(@Url String url);



}