package com.example.stopmindlessscrolling.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.utility.AppConstants;
import com.example.stopmindlessscrolling.views.ConfigureAppsActivity;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private TextView configureAppsTxt;
    private TextView appTimeLimitTxt;
    private TextView quizzTxt;
    private Intent intent;
    private Dialog dialog;
    private RadioGroup radioGroup;


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
            radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
            Log.e("TAG", "QUIZQUESTION: "+sharedPreferences.getString(AppConstants.QUIZQUESTION,"") );
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


            int radioButtonId=sharedPreferences.getInt(AppConstants.QUIZID,0);
            if (radioButtonId!=0){
                radioGroup.check(radioButtonId);
            }
            radioGroup.setOnCheckedChangeListener((group, selectedId) -> {
                  RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);
                        editor.putInt(AppConstants.QUIZID,radioButton.getId());
                        editor.putString(AppConstants.QUIZQUESTION,radioButton.getText().toString());
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

                    editor.putInt(AppConstants.QUIZID,selectedId);
                    editor.putString(AppConstants.QUIZQUESTION,radioButton.getText().toString());
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



            Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
            radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
            Log.e("TAG", "APPTIMELIMITVALUE: "+sharedPreferences.getInt(AppConstants.APPTIMELIMITVALUE,0) );

            int radioButtonId=sharedPreferences.getInt(AppConstants.APPTIMELIMITID,0);
            if (radioButtonId!=0){
                radioGroup.check(radioButtonId);
            }
            radioGroup.setOnCheckedChangeListener((group, selectedId) -> {
                        RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);

                        editor.putInt(AppConstants.APPTIMELIMITID,radioButton.getId());
                        editor.putInt(AppConstants.APPTIMELIMITVALUE,Integer.parseInt(radioButton.getText().toString()));
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
                    editor.putInt(AppConstants.APPTIMELIMITVALUE,Integer.parseInt(radioButton.getText().toString()));
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
}
