package com.github.oryanmat.trellowidget.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.github.oryanmat.trellowidget.R;
import com.github.oryanmat.trellowidget.util.color.ColorPreference;
import com.github.oryanmat.trellowidget.widget.TrelloWidgetProvider;

public class GeneralPreferenceFragment extends PreferenceFragment {
    static final String COLOR_FORMAT = "#%08X";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        listener.onSharedPreferenceChanged(preferences, getString(R.string.pref_text_size_key));
        listener.onSharedPreferenceChanged(preferences, getString(R.string.pref_back_color_key));
        listener.onSharedPreferenceChanged(preferences, getString(R.string.pref_fore_color_key));
        listener.onSharedPreferenceChanged(preferences, getString(R.string.pref_title_back_color_key));
        listener.onSharedPreferenceChanged(preferences, getString(R.string.pref_title_fore_color_key));
        listener.onSharedPreferenceChanged(preferences, getString(R.string.pref_title_use_unique_color_key));
        listener.onSharedPreferenceChanged(preferences, getString(R.string.pref_update_interval_key));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener);
        TrelloWidgetProvider.updateWidgets(getActivity());
        TrelloWidgetProvider.updateWidgetsData(getActivity());
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    setPreferenceChanges(key);
                }
            };

    private void setPreferenceChanges(String key) {
        if (key.equals(getString(R.string.pref_update_interval_key))) {
            ListPreference preference = (ListPreference) findPreference(key);
            int index = preference.findIndexOfValue(preference.getValue());
            preference.setSummary(String.format(getActivity()
                    .getString(R.string.pref_update_interval_value_desc), preference.getEntries()[index]));
        } else if (key.equals(getString(R.string.pref_text_size_key))) {
            ListPreference preference = (ListPreference) findPreference(key);
            int index = preference.findIndexOfValue(preference.getValue());
            preference.setSummary(preference.getEntries()[index]);
        } else if (key.equals(getString(R.string.pref_back_color_key))) {
            ColorPreference preference = (ColorPreference) findPreference(key);
            preference.setSummary(String.format(COLOR_FORMAT, preference.getColor()));
        } else if (key.equals(getString(R.string.pref_fore_color_key))) {
            ColorPreference preference = (ColorPreference) findPreference(key);
            preference.setSummary(String.format(COLOR_FORMAT, preference.getColor()));
        } else if (key.equals(getString(R.string.pref_title_back_color_key))) {
            ColorPreference preference = (ColorPreference) findPreference(key);
            preference.setSummary(String.format(COLOR_FORMAT, preference.getColor()));
        } else if (key.equals(getString(R.string.pref_title_fore_color_key))) {
            ColorPreference preference = (ColorPreference) findPreference(key);
            preference.setSummary(String.format(COLOR_FORMAT, preference.getColor()));
        } else if (key.equals(getString(R.string.pref_title_use_unique_color_key))) {
            SwitchPreference preference = (SwitchPreference) findPreference(key);
            boolean enableTitleSettings = preference.isChecked();
            if (enableTitleSettings) {
                preference.setSummary(R.string.pref_title_use_unique_color_enabled_desc);
            } else {
                preference.setSummary(R.string.pref_title_use_unique_color_disabled_desc);
            }
            ColorPreference titleFgPref = (ColorPreference) findPreference(getString(R.string.pref_title_fore_color_key));
            ColorPreference titleBgPref = (ColorPreference) findPreference(getString(R.string.pref_title_back_color_key));
            titleFgPref.setEnabled(enableTitleSettings);
            titleBgPref.setEnabled(enableTitleSettings);
        }
    }
}

