package com.example.stopmindlessscrolling.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.utility.AppConstants;
import com.example.stopmindlessscrolling.views.ConfigureAppsActivity;

import java.util.HashSet;
import java.util.Set;


public class SettingsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private TextView configureAppsTxt;
    private TextView appTimeLimitTxt;
    private TextView quizzTxt;
    private Intent intent;
    private Dialog dialog;
    private RadioGroup radioGroup;
    private Set<String> activities;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_settings, container, false);

        configureAppsTxt=view.findViewById(R.id.configureAppsTxt);
        appTimeLimitTxt=view.findViewById(R.id.appTimeLimitTxt);
        quizzTxt=view.findViewById(R.id.quizzTxt);

        configureAppsTxt.setOnClickListener(this);
        appTimeLimitTxt.setOnClickListener(this);
        quizzTxt.setOnClickListener(this);

        sharedPreferences= requireContext().getSharedPreferences(AppConstants.SHAREDPREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Inflate the layout for this fragment
        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.configureAppsTxt:
                intent=new Intent(getContext(), ConfigureAppsActivity.class);
                startActivity(intent);
                break;

            case R.id.appTimeLimitTxt:
                showAppTimeLimitPopup();
                break;
            case R.id.quizzTxt:
                showQuizPopup();
                break;
        }
    }

    /**
     * Popup for Quiz
     **/
    public  void showQuizPopup () {
        try {
            dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.quizz_popup);
            Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
            checkBox1 = (CheckBox) dialog.findViewById(R.id.CheckBox1);
            checkBox2 = (CheckBox) dialog.findViewById(R.id.CheckBox2);
            checkBox3 = (CheckBox) dialog.findViewById(R.id.CheckBox3);
            checkBox4 = (CheckBox) dialog.findViewById(R.id.CheckBox4);
            checkBox5 = (CheckBox) dialog.findViewById(R.id.CheckBox5);

            Set<String> activities=sharedPreferences.getStringSet(AppConstants.ACTIVITIES,new HashSet<>());


            for (String activity:activities) {
                if (activity.equalsIgnoreCase("Quiz")){
                    checkBoxValidation(activity,checkBox1,"Quiz");
                }else  if (activity.equalsIgnoreCase("Explore Motivational Quotes")){
                    checkBoxValidation(activity,checkBox2,"Explore Motivational Quotes");
                } if (activity.equalsIgnoreCase("Explore Articles")){
                    checkBoxValidation(activity,checkBox3,"Explore Articles");
                } if (activity.equalsIgnoreCase("It's time to do Yoga")){
                    checkBoxValidation(activity,checkBox4,"It's time to do Yoga");
                }if (activity.equalsIgnoreCase("Sports")){
                    checkBoxValidation(activity,checkBox5,"Sports");
                }


            }
            checkBox1.setOnCheckedChangeListener(this);
            checkBox2.setOnCheckedChangeListener(this);
            checkBox3.setOnCheckedChangeListener(this);
            checkBox4.setOnCheckedChangeListener(this);
            checkBox5.setOnCheckedChangeListener(this);


            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


            ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

            cancelButton.setOnClickListener(v -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            });

            okBtn.setOnClickListener(v -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            });


            dialog.setCancelable(false);
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkBoxValidation(String activity, CheckBox checkBox,String checkboxText) {
        checkBox.setChecked(activity.equalsIgnoreCase(checkboxText));
    }

    /**
     * Popup for App Time Limit
     **/



    public  void showAppTimeLimitPopup() {
        try {
            dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.apptimelimit_popup);


            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);



            Button okBtn = dialog.findViewById(R.id.okBtn);
            radioGroup = dialog.findViewById(R.id.radioGroup);

            ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

            cancelButton.setOnClickListener(v -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            });

            int radioButtonId=sharedPreferences.getInt(AppConstants.APPTIMELIMITID,0);
            if (radioButtonId!=0){
                radioGroup.check(radioButtonId);
            }
            radioGroup.setOnCheckedChangeListener((group, selectedId) -> {
                        RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);

                        editor.putInt(AppConstants.APPTIMELIMITID,radioButton.getId());
                        editor.putInt(AppConstants.APPTIMELIMITVALUE,Integer.parseInt(radioButton.getText().toString().replace(" Sec","")));
                        editor.apply();

                    }
            );

            okBtn.setOnClickListener(v -> {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(requireContext(),
                                    "No answer has been selected",
                                    Toast.LENGTH_SHORT)
                            .show();
                }
                else {

                    RadioButton radioButton
                            = (RadioButton)radioGroup
                            .findViewById(selectedId);

                    // Now display the value of selected item
                    // by the Toast message
                    Toast.makeText(requireContext(),
                                    radioButton.getText(),
                                    Toast.LENGTH_SHORT)
                            .show();
                    editor.putInt(AppConstants.APPTIMELIMITID,selectedId);
                    editor.putInt(AppConstants.APPTIMELIMITVALUE,Integer.parseInt(radioButton.getText().toString().replace(" Sec","")));
                    editor.apply();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

            });


            dialog.setCancelable(false);
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint({"NonConstantResourceId", "MutatingSharedPrefs"})
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){

            case R.id.CheckBox1:


                activities=sharedPreferences.getStringSet(AppConstants.ACTIVITIES,new HashSet<>());

                if (isChecked){
                    activities.add("Quiz");
                    Toast.makeText(requireContext(),"Quiz Selected",Toast.LENGTH_SHORT).show();

                }else {
                    activities.remove("Quiz");
                    Toast.makeText(requireContext(),"Quiz Unselected",Toast.LENGTH_SHORT).show();

                }
                editor.putStringSet(AppConstants.ACTIVITIES,activities);
                editor.apply();


                break;
            case R.id.CheckBox2:
                activities=sharedPreferences.getStringSet(AppConstants.ACTIVITIES,new HashSet<>());

                if (isChecked){
                    activities.add("Explore Motivational Quotes");
                    Toast.makeText(requireContext(),"Explore Motivational Quotes Selected",Toast.LENGTH_SHORT).show();
                }else {
                    activities.remove("Explore Motivational Quotes");
                    Toast.makeText(requireContext(),"Explore Motivational Quotes Unselected",Toast.LENGTH_SHORT).show();

                }
                editor.putStringSet(AppConstants.ACTIVITIES,activities);
                editor.apply();


                break;
            case R.id.CheckBox3:
                activities=sharedPreferences.getStringSet(AppConstants.ACTIVITIES,new HashSet<>());


                if (isChecked){
                    activities.add("Explore Articles");
                    Toast.makeText(requireContext(),"Explore Articles Selected",Toast.LENGTH_SHORT).show();

                }else {
                    activities.remove("Explore Articles");
                    Toast.makeText(requireContext(),"Explore Articles Unselected",Toast.LENGTH_SHORT).show();

                }
                editor.putStringSet(AppConstants.ACTIVITIES,activities);
                editor.apply();


                break;
            case R.id.CheckBox4:
                activities=sharedPreferences.getStringSet(AppConstants.ACTIVITIES,new HashSet<>());

                if (isChecked){
                    activities.add("It's time to do Yoga");
                    Toast.makeText(requireContext(),"It's time to do Yoga Selected",Toast.LENGTH_SHORT).show();

                }else {
                    activities.remove("It's time to do Yoga");
                    Toast.makeText(requireContext(),"It's time to do Yoga Unselected",Toast.LENGTH_SHORT).show();

                }
                editor.putStringSet(AppConstants.ACTIVITIES,activities);
                editor.apply();

                break;
                case R.id.CheckBox5:
                activities=sharedPreferences.getStringSet(AppConstants.ACTIVITIES,new HashSet<>());

                if (isChecked){
                    activities.add("Sports");
                    Toast.makeText(requireContext(),"Sports Selected",Toast.LENGTH_SHORT).show();

                }else {
                    activities.remove("Sports");
                    Toast.makeText(requireContext(),"Sports Unselected",Toast.LENGTH_SHORT).show();

                }
                editor.putStringSet(AppConstants.ACTIVITIES,activities);
                editor.apply();

                break;

        }
    }
}
