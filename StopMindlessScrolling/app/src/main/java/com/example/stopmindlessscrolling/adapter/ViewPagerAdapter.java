package com.example.stopmindlessscrolling.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.utility.AppConstants;
import com.example.stopmindlessscrolling.views.QuizActivity;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private final Context context;
    private final List<String> activitiesSet;
    private Intent intent;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private TextView titletextView;
    private TextView desTextView;
    private TextView textViewClickMore;
    private Button quizButton;
    private Button exploreButton;

    public ViewPagerAdapter(List<String> activitiesSet, Context context) {
        this.activitiesSet = activitiesSet;
        this.context = context;
        sharedPreferences= context.getSharedPreferences(AppConstants.SHAREDPREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public int getCount() {
        return activitiesSet.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.custom_layout, null);


        titletextView = view.findViewById(R.id.titletextView);
        desTextView = view.findViewById(R.id.desTextView);
        textViewClickMore = view.findViewById(R.id.textViewClickMore);
        exploreButton = view.findViewById(R.id.exploreButton);
        quizButton = view.findViewById(R.id.quizButton);

        if (activitiesSet.get(position).contains("Quiz")){
            quizButton.setVisibility(View.VISIBLE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.GONE);
//            desTextView.setText("Test your knowledge. Let's take quiz");
            desTextView.setText("Grow yourself, let's take quiz to improve your knowledge.");

        }else if (activitiesSet.get(position).contains("Explore Motivational Quotes")){
            quizButton.setVisibility(View.GONE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.VISIBLE);
//            desTextView.setText("Get inspire yourself let's explore motivational quotes");
            desTextView.setText("Get inspire yourself, it's time to explore motivational quotes.");

        }else if (activitiesSet.get(position).contains("Explore Articles")){
            quizButton.setVisibility(View.GONE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.VISIBLE);
            desTextView.setText("");

        }else if (activitiesSet.get(position).contains("It's time to do Yoga")){
            quizButton.setVisibility(View.GONE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.GONE);
            desTextView.setText("Do yoga be healthy.");
        }else if (activitiesSet.get(position).contains("Sports")){
            quizButton.setVisibility(View.GONE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.GONE);
            desTextView.setText("");
        }else if (activitiesSet.get(position).contains("Tech News")){
            quizButton.setVisibility(View.GONE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.VISIBLE);
            exploreButton.setText("Get Tech News");
            desTextView.setText("Would you like to explore Tech News?");

        }else if (activitiesSet.get(position).contains("Current Affairs")){
            quizButton.setVisibility(View.GONE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.VISIBLE);
            exploreButton.setText("Know Current Affairs");
            desTextView.setText("Would you like to know what's happening around you? \nTap on below Know Current Affairs.");

        }else if (activitiesSet.get(position).contains("Business News")){
            quizButton.setVisibility(View.GONE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.VISIBLE);
            exploreButton.setText("Get Business News");
            desTextView.setText("Would you like to know latest Business News?");

        }else if (activitiesSet.get(position).contains("Today's News")){
            quizButton.setVisibility(View.GONE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.VISIBLE);
            exploreButton.setText("Get Today's News");
            desTextView.setText("Would you like to read the latest today's news? \nTap on below Get Today's News.");

        }else if (activitiesSet.get(position).contains("Take a Walk")){
            quizButton.setVisibility(View.GONE);
            textViewClickMore.setVisibility(View.GONE);
            exploreButton.setVisibility(View.GONE);
            desTextView.setText("Walk and get healthy.");
        }


        titletextView.setText(activitiesSet.get(position));
        textViewClickMore.setOnClickListener(v -> clickEvent(activitiesSet.get(position)));
        quizButton.setOnClickListener(v -> clickEvent(activitiesSet.get(position)));
        exploreButton.setOnClickListener(v -> clickEvent(activitiesSet.get(position)));

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }

    private void clickEvent(String activity) {

        if (activity.contains("Quiz")){

            editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
            editor.apply();

            intent = new Intent(context, QuizActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } else if (activity.contains("Explore Motivational Quotes")) {
            try {
                editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
                editor.apply();
//                intent=new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.unfoldlabs.unfoldquotes"));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);

                PackageManager pm = context.getPackageManager();
                intent = pm.getLaunchIntentForPackage("com.unfoldlabs.unfoldquotes");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
                editor.apply();
                intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.unfoldlabs.unfoldquotes" ));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        } else if (activity.contains("Explore Articles")) {
            editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
            editor.apply();
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Explore Articles");
            context.startActivity(intent);

        }else if (activity.contains("It's time to do Yoga")) {
            editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
            editor.apply();
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "It's time to do Yoga");
            context.startActivity(intent);

        }else if (activity.contains("Sports")) {
            editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
            editor.apply();
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Sports");
            context.startActivity(intent);

        }else if (activity.contains("Tech News")) {
            editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
            editor.apply();
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Tech News");
            context.startActivity(intent);

        }else if (activity.contains("Current Affairs")) {
            editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
            editor.apply();
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Current Affairs");
            context.startActivity(intent);

        }else if (activity.contains("Business News")) {
            editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
            editor.apply();
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Business News");
            context.startActivity(intent);

        }else if (activity.contains("Today's News")) {
            editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
            editor.apply();
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Today's News");
            context.startActivity(intent);

        }else if (activity.contains("Take a Walk")) {
            editor.putBoolean(AppConstants.MOREINFOOPENEDVALIDATION,true);
            editor.apply();
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Take a Walk");
            context.startActivity(intent);

        }

    }
}