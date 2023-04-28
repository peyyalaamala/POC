package com.example.stopmindlessscrolling.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.adapter.ConfigureAppsAdapter;
import com.example.stopmindlessscrolling.adapter.OverViewAppsAdapter;
import com.example.stopmindlessscrolling.utility.AppConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class OverViewFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private RecyclerView recyclerViewOverView;
    private TextView noAppsTxt;
    private TextView titleTxt;

    public OverViewFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_overview, container, false);
        recyclerViewOverView=view.findViewById(R.id.recyclerViewOverView);
        noAppsTxt=view.findViewById(R.id.noAppsTxt);
        titleTxt=view.findViewById(R.id.titleTxt);
        sharedPreferences= requireContext().getSharedPreferences(AppConstants.SHAREDPREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Set<String> slectedAppsStringSet=sharedPreferences.getStringSet(AppConstants.SELECTEDAPPS,new HashSet<>());
        Set<String> recentlyAppsSet=sharedPreferences.getStringSet(AppConstants.RECENTLYOPENAPPS,new LinkedHashSet<>());

        Set<String> stringSet=new LinkedHashSet<>();

            for (String recentlyAppName : recentlyAppsSet) {

                if ( slectedAppsStringSet.contains(recentlyAppName)){
                    stringSet.add(recentlyAppName);
                }

            }

        List<String> overViewAppsList = new ArrayList<>(stringSet);

            if (overViewAppsList.size()>0){
                recyclerViewOverView.setVisibility(View.VISIBLE);
                noAppsTxt.setVisibility(View.GONE);
                titleTxt.setVisibility(View.VISIBLE);

            }else {
                recyclerViewOverView.setVisibility(View.GONE);
                noAppsTxt.setVisibility(View.VISIBLE);
                titleTxt.setVisibility(View.GONE);
            }


        recyclerViewOverView.setLayoutManager(new LinearLayoutManager(requireContext()));
        OverViewAppsAdapter adapter = new OverViewAppsAdapter(overViewAppsList,requireContext());
        recyclerViewOverView.setAdapter(adapter);
    }
}
