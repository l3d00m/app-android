package org.volkszaehler.volkszaehlerapp;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;

import org.volkszaehler.volkszaehlerapp.generic.Channel;
import org.volkszaehler.volkszaehlerapp.generic.Entity;
import org.volkszaehler.volkszaehlerapp.presenter.MainActivityPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class Preferences extends PreferenceActivity implements PresenterActivityInterface {
    private static final int RESULT_RELOAD = 47;
    // URL to get contacts JSON
    private static String url = "http://demo.volkszaehler.org/middleware.php/entity.json";
    private static String uname;
    private static String pwd;
    private static String tuples;
    private static String privateChannelString;
    private List<Channel> channels = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();
    private MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        addPreferencesFromResource(R.xml.volkszaehler_preferences);

        getPreferenceManager().findPreference("volkszaehlerURL")
                .setOnPreferenceChangeListener((pref, newValue) -> {
                    String url = (String) newValue;
                    if (!url.isEmpty()) {
                        presenter = new MainActivityPresenter(url + "/", this, this.getBaseContext());
                    }
                    return true;
                });
        Preference button = getPreferenceManager().findPreference("getChannelsButton");
        if (button != null) {
            button.setOnPreferenceClickListener(pref -> {
                // call Channels from VZ installation
                presenter.loadAllChannels();
                return true;
            });
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Preferences.this);
        String url = sharedPref.getString("volkszaehlerURL", "");
        if (!url.isEmpty()) {
            presenter = new MainActivityPresenter(url + "/", this, this.getBaseContext());
        }
    }

    private boolean isUuuidPrefEnabled(String uuid) {
        for (String preference : PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getAll().keySet()) {
            // assume its a UUID of a channel
            if (preference.equals(uuid)) {
                // is preference checked?
                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(preference, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeUuidsFromPrefs() {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        for (String preference : PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getAll().keySet()) {
            // check if pref is a UUID
            if (preference.matches("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$")) {
                prefs.remove(preference);
            }
        }
        prefs.apply();
    }

    private void addPreferenceChannels() {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("channel_preference_category");
        targetCategory.removeAll();
        if (channels != null) {
            for (Channel channel : channels) {
                CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this);
                checkBoxPreference.setChecked(isUuuidPrefEnabled(channel.getUuid()));

                if ("group".equals(channel.getType())) {
                    checkBoxPreference.setTitle(channel.getTitle() + " " + getString(R.string.group));
                } else {
                    checkBoxPreference.setTitle(channel.getTitle());
                }
                checkBoxPreference.setKey(channel.getUuid());
                if (channel.getDescription() == null) {
                    checkBoxPreference.setSummary(channel.getType() + "\n" + channel.getUuid());
                } else {
                    checkBoxPreference.setSummary(channel.getDescription() + "\n" + channel.getUuid());
                }

                targetCategory.addPreference(checkBoxPreference);
            }
            removeUuidsFromPrefs();
        }
    }

    @Override
    protected void onPause() {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("channel_preference_category");
        for (int i = 0; i < targetCategory.getPreferenceCount(); i++) {
            CheckBoxPreference pref = (CheckBoxPreference) targetCategory.getPreference(i);
            if (pref.isChecked()) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putBoolean(pref.getKey(), true)
                        .apply();
            }
        }
        super.onPause();
    }

    @Override
    public void loadingEntitiesSuccess(List<Entity> entities) {
        DatabaseHolder.getInstance(this).entityDao().nukeTable();
        DatabaseHolder.getInstance(this).entityDao().insertAll(entities);
        Single.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(ign -> {
                    DatabaseHolder.getInstance(this).entityDao().nukeTable();
                    DatabaseHolder.getInstance(this).entityDao().insertAll(entities);
                });
    }

    @Override
    public void loadingChannelInfosSuccess(List<Channel> channels) {
        setResult(RESULT_RELOAD);

        this.channels.clear();
        this.channels.addAll(channels);
        addPreferenceChannels();
        presenter.loadEntityDefinitions();

        Single.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(ign -> {
                    DatabaseHolder.getInstance(this).channelMetaDao().nukeTable();
                    DatabaseHolder.getInstance(this).channelMetaDao().insertAll(channels);
                });
    }

    @Override
    public void adapterFailedCallback(String errorMessage) {
        new AlertDialog.Builder(Preferences.this)
                .setTitle(getString(R.string.Error))
                .setMessage(errorMessage)
                .setNeutralButton(getString(R.string.Close), null)
                .show();
    }

    @Override
    public void loadingChannelValuesSuccess(List<Channel> values) {
        // stub
    }

    @Override
    public void loadingTotalConsumptionSuccess(Double totalConsumption) {
        // stub
    }
}
