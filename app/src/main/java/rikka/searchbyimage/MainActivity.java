package rikka.searchbyimage;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.view,
                new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements
            OnSharedPreferenceChangeListener,
            Preference.OnPreferenceClickListener {

        PreferenceCategory mCategoryGeneral;
        SwitchPreferenceCompat mSafeSearch;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.preferences);

            mCategoryGeneral = (PreferenceCategory) findPreference("category_general");
            mSafeSearch = (SwitchPreferenceCompat) findPreference("safe_search_preference");

            setSafeSearchHide();

            Preference versionPref = findPreference("version");
            versionPref.setOnPreferenceClickListener(this);

            try {
                versionPref.setSummary(getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("search_engine_preference")) {
                setSafeSearchHide();
            }
        }

        private void setSafeSearchHide() {
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

            boolean isGoogle = sharedPreferences.getString("search_engine_preference", "0").equals("0");

            if (isGoogle) {
                mCategoryGeneral.addPreference(mSafeSearch);
            } else {
                mCategoryGeneral.removePreference(mSafeSearch);
            }
        }

        private int click = 0;

        private Runnable clearClickCount = new Runnable() {
            @Override
            public void run() {
                click = 0;
            }
        };

        @Override
        public boolean onPreferenceClick(Preference preference) {
            getActivity().getWindow().getDecorView().removeCallbacks(clearClickCount);
            getActivity().getWindow().getDecorView().postDelayed(clearClickCount, 3000);

            click ++;

            if (click == 5)
                Toast.makeText(getContext(), "OAO", Toast.LENGTH_LONG).show();
            else if (click == 10)
                Toast.makeText(getContext(), "><", Toast.LENGTH_LONG).show();
            else if (click == 25)
                Toast.makeText(getContext(), "QAQ", Toast.LENGTH_LONG).show();

            return false;
        }
    }
}