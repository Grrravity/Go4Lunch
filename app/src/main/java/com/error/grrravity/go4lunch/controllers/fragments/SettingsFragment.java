package com.error.grrravity.go4lunch.controllers.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.MainActivity;
import com.error.grrravity.go4lunch.utils.auth.AuthenticationActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment {

    @BindView(R.id.switch_notifications)
    Switch aSwitch;
    @BindView(R.id.spinner)
    Spinner spinner;

    private static final String currentLang = "current_lang";
    private String language;
    private boolean notif;

    private static final String PREFS = "PREFS" ;
    private SharedPreferences prefs;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        // Create new fragment

        return new SettingsFragment();
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        language = prefs.getString("language", "");
        notif = prefs.getBoolean("notif", true);
        configureSpinner();
        configureSwitch();
        return view;
    }

    private void configureSwitch(){
        if (notif ){
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }

        aSwitch.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("notif", b);
                    editor.apply();
            }
        });
    }

    // Spinner to language choice
    private void configureSpinner() {
        List<String> list = new ArrayList<>();

        Log.d("prefs", "configureSpinner: " + language);

        assert language != null;
        switch (language){
                case "":
                    list.add(getString(R.string.selection));
                    list.add(getString(R.string.english));
                    list.add(getString(R.string.french));
                    break;
                case "en":
                    list.add(getString(R.string.english));
                    list.add(getString(R.string.french));
                    break;
                case "fr":
                    list.add(getString(R.string.french));
                    list.add(getString(R.string.english));
                    break;

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<
                >(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position) {
                    case 0:
                        break;
                    case 1:
                        if(language.equals("en")){
                            setLocale("fr");
                        } else if (language.equals("fr")){
                            setLocale("en");
                        }
                        break;
                    case 2:
                        if(language.equals("")) {
                            setLocale("fr");
                        }

                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // Apply choice
    private void setLocale(String localeName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", localeName);
        editor.apply();

        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        showDialogToRestart(localeName);
    }

    // Show dialog box to restart app and apply modifications
    private void showDialogToRestart(String localeName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.reboot));
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.restart));
        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.popup_choice_yes), (dialog, which) -> signOutUserFromFirebase(localeName));
        // on pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.popup_choice_no), (dialog, which) -> dialog.cancel());
        // Showing Alert Message
        alertDialog.show();
    }

    // Sign out
    private void signOutUserFromFirebase(String localeName) {
        AuthUI.getInstance()
                .signOut(Objects.requireNonNull(getActivity()))
                .addOnSuccessListener(getActivity(), this.updateUIAfterRESTRequestsCompleted(localeName));
    }

    // SignOut from Firebase
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(String localeName) {
        return aVoid -> {
            Intent refresh = new Intent(getActivity(), AuthenticationActivity.class);
            refresh.putExtra(currentLang, localeName);
            refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(refresh);
        };
    }
}