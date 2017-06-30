package org.volkszaehler.volkszaehlerapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.volkszaehler.volkszaehlerapp.adapter.CustomAdapter;
import org.volkszaehler.volkszaehlerapp.generic.Channel;
import org.volkszaehler.volkszaehlerapp.generic.Entity;
import org.volkszaehler.volkszaehlerapp.presenter.MainActivityPresenter;
import org.volkszaehler.volkszaehlerapp.stetho.StethoHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

import static org.volkszaehler.volkszaehlerapp.Preferences.RESULT_RELOAD;

@SuppressWarnings("Convert2streamapi")
public class MainActivity extends AppCompatActivity implements PresenterActivityInterface {
    private static final int SETTINGS_REQUEST = 63;
    private static final List<Channel> channels = new ArrayList<>();
    private static final List<Entity> entities = new ArrayList<>();

    private MainActivityPresenter presenter;
    private SwipeRefreshLayout refreshLayout;
    private CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (BuildConfig.DEBUG) {
            new StethoHelper().init(this);
        }

        setupRecyclerView();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        refreshLayout.setOnRefreshListener(() -> {
            if (presenter != null) presenter.loadChannelData(channels, false);
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }

    private void startLoading() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String url = sharedPref.getString("volkszaehlerURL", "");
        if (!url.isEmpty()) {
            presenter = new MainActivityPresenter(url + "/", this, this.getBaseContext());
            loadEntitiesFromDb();
            refreshLayout.setRefreshing(true);
        } else {
            // todo display info to set it
        }
    }

    private void loadEntitiesFromDb() {
        DatabaseHolder.getInstance(this).entityDao().getAll()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe((c) -> {
                    if (c.isEmpty()) {
                        presenter.loadEntityDefinitions();
                    } else {
                        entities.clear();
                        entities.addAll(c);
                    }
                    loadChannelMetaFromDb();
                });
    }

    @Nullable
    public static Channel getChannel(String uuid) {
        for (Channel channel : channels) {
            if (channel.getUuid().equals(uuid)) {
                return channel;
            }
        }
        return null;
    }

    @Nullable
    public static Entity getEntity(String name) {
        for (Entity entity : entities) {
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return null;
    }

    private void loadChannelMetaFromDb() {
        DatabaseHolder.getInstance(this).channelMetaDao().getAll()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe((c) -> {
                    if (c.isEmpty()) {
                        // todo display an info to select channels in settings
                    } else {
                        channels.clear();
                        channels.addAll(c);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        boolean shouldAutoReload = sharedPref.getBoolean("autoReload", false);
                        presenter.loadChannelData(channels, shouldAutoReload);
                    }
                });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        adapter = new CustomAdapter(channels, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_REQUEST) {
            if (resultCode == RESULT_RELOAD) {
                if (presenter != null) presenter.stopAllLoading();
                channels.clear();
                entities.clear();
                startLoading();
            }
        }
    }

    @Override
    protected void onPause() {
        if (presenter != null) presenter.stopAllLoading();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, Preferences.class), SETTINGS_REQUEST);
                return true;
            case R.id.backup_settings:
                boolean saved = Tools.saveFile(getApplicationContext());
                if (saved) {
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.notsaved, Toast.LENGTH_SHORT).show();
                }
                return (true);
            case R.id.restore_settings:

                boolean restored = Tools.loadFile(getApplicationContext());
                if (restored) {
                    Toast.makeText(this, R.string.restored, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.notrestored, Toast.LENGTH_SHORT).show();
                }
                return (true);
            case R.id.about:
                return Tools.showAboutDialog(this);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadingChannelValuesSuccess(List<Channel> values) {
        channels.clear();
        channels.addAll(values);
        adapter.notifyDataSetChanged();
        if (refreshLayout != null) refreshLayout.setRefreshing(false);
    }

    @Override
    public void loadingTotalConsumptionSuccess(Double totalConsumption) {
        // stub
    }

    @Override
    public void loadingChannelInfosSuccess(List<Channel> channels) {
        // stub
    }

    @Override
    public void loadingEntitiesSuccess(List<Entity> entities) {
        MainActivity.entities.addAll(entities);
        DatabaseHolder.getInstance(this).entityDao().insertAll(entities);
    }

    @Override
    public void adapterFailedCallback(String errorMessage) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.Error))
                .setMessage(errorMessage)
                .setNeutralButton(getString(R.string.Close), null)
                .show();
        if (refreshLayout != null) refreshLayout.setRefreshing(false);
    }
}
