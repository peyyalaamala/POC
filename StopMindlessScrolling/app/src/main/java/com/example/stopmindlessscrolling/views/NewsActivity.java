package com.example.stopmindlessscrolling.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.adapter.NewsAdapter;
import com.example.stopmindlessscrolling.retrofit.ApiClient;
import com.example.stopmindlessscrolling.retrofit.ApiInterface;
import com.example.stopmindlessscrolling.retrofit.model.Example;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNews;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        loadNewsHeadlines();

        // set up the RecyclerView
        recyclerViewNews = findViewById(R.id.recyclerViewNews);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this));


    }

    private void loadNewsHeadlines() {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<Example> call = apiService.getTopHeadlines("top-headlines?sources=google-news-in&apiKey=2438ca2d5166428b88663efb66b68b07");
        call.enqueue(new Callback<Example>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Example> exampleCall,
                                   @NonNull Response<Example> exampleResponse) {

                try {
                    int statusCode = exampleResponse.code();
                    Log.e("TAG", "getExample statusCode---" + statusCode);
                    Log.e("TAG", "getExample body---" + Objects.requireNonNull(exampleResponse.body()).getArticles());

                    newsAdapter = new NewsAdapter(exampleResponse.body().getArticles(),NewsActivity.this);
                    recyclerViewNews.setAdapter(newsAdapter);

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
            @Override
            public void onFailure(@NonNull Call<Example> exampleCall, @NonNull Throwable t) {
            }
        });


    }
}