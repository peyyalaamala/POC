package com.example.stopmindlessscrolling.views;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.fragments.OverViewFragment;
import com.example.stopmindlessscrolling.fragments.SettingsFragment;
import com.example.stopmindlessscrolling.utility.AppConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    //viewPager
    private ViewPager viewPager;

    //Fragments
    OverViewFragment overViewFragment;
    SettingsFragment settingsFragment;
    MenuItem prevMenuItem;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initializing viewPager
        viewPager = findViewById(R.id.viewpager);

        sharedPreferences= getSharedPreferences(AppConstants.SHAREDPREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();


        //Initializing the bottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.overview_menuitem:
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.settings_menuitem:
                            viewPager.setCurrentItem(1);
                            break;
                    }
                    return false;
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page",""+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);



    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            installedApps();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        overViewFragment=new OverViewFragment();
        settingsFragment=new SettingsFragment();
        adapter.addFragment(overViewFragment);
        adapter.addFragment(settingsFragment);
        viewPager.setAdapter(adapter);
    }

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    @SuppressLint("MutatingSharedPrefs")
    public void installedApps()
    {

        Log.e("TAG", "installedApps:  Called" );
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);

        Set<String> installedAppSet=new HashSet<>();

        for (int i=0; i < packList.size(); i++)
        {
            PackageInfo packInfo = packList.get(i);

            if (  (packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                String packageName = packInfo.applicationInfo.packageName;

                installedAppSet.add(packageName);

            }

        }


        Set<String> totalInstallApps=sharedPreferences.getStringSet(AppConstants.TOTALINSTALLAPPS,new HashSet<>());
        Set<String> selectedApps=sharedPreferences.getStringSet(AppConstants.SELECTEDAPPS,new HashSet<>());
        Set<String> unselectedApps=sharedPreferences.getStringSet(AppConstants.UNSELECTEDAPPS,new HashSet<>());

        if (totalInstallApps.size()==0){
            Set<String> stringSet=new HashSet<>();
            editor.putStringSet(AppConstants.TOTALINSTALLAPPS,stringSet);
            editor.putStringSet(AppConstants.TOTALINSTALLAPPS,installedAppSet);
            editor.putStringSet(AppConstants.UNSELECTEDAPPS,installedAppSet);
            editor.putStringSet(AppConstants.SELECTEDAPPS,stringSet);
            editor.apply();
            Log.e("TAG", "all instlled apps inserted: " );

        }else if ((selectedApps.size()+unselectedApps.size())<totalInstallApps.size()){

            Set<String> stringSet=new HashSet<>();
            editor.putStringSet(AppConstants.TOTALINSTALLAPPS,stringSet);
            editor.putStringSet(AppConstants.TOTALINSTALLAPPS,installedAppSet);
            for (String packageName : totalInstallApps) {
                if (!unselectedApps.contains(packageName)){
                    unselectedApps.add(packageName);
                }
            }
            editor.putStringSet(AppConstants.UNSELECTEDAPPS,unselectedApps);

            Log.e("TAG", "new  instlled apps inserted: " );

        }


    }
}