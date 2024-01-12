package com.example.stopmindlessscrolling.adapter;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.retrofit.model.Article;
import com.example.stopmindlessscrolling.utility.AppConstants;
import com.example.stopmindlessscrolling.views.WebViewActivity;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private final List<Article> articleList;
    private final Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Drawable icon;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTxt;
        public TextView titleTxt;
        public ConstraintLayout parentLyt;


        public MyViewHolder(View view) {
            super(view);
            authorTxt = view.findViewById(R.id.authorTxt);
            titleTxt = view.findViewById(R.id.titleTxt);
            parentLyt = view.findViewById(R.id.parentLyt);
        }
    }

    public NewsAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
        sharedPreferences= context.getSharedPreferences(AppConstants.SHAREDPREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list_row, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @SuppressLint("MutatingSharedPrefs")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.authorTxt.setText(article.getAuthor());
        holder.titleTxt.setText(article.getTitle());
        holder.parentLyt.setOnClickListener(v -> {
            showWebPage(article.getUrl(), article.getAuthor());
        });

    }
    private void showWebPage(String url,String title) {
        Intent intent=new Intent(context, WebViewActivity.class);
        intent.putExtra(AppConstants.WEBURL, url);
        intent.putExtra(AppConstants.WEBTITLE, title);
        context.startActivity(intent);

    }
    @Override
    public int getItemCount() {
        return articleList.size();
    }

}