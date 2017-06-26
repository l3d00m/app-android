package org.volkszaehler.volkszaehlerapp;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
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
import org.volkszaehler.volkszaehlerapp.dao.AppDatabase;
import org.volkszaehler.volkszaehlerapp.generic.Channel;
import org.volkszaehler.volkszaehlerapp.generic.Entity;
import org.volkszaehler.volkszaehlerapp.presenter.MainActivityPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("Convert2streamapi")
public class MainActivity extends AppCompatActivity {
    private Context myContext;
    // Hashmaps for ListView
    private static final List<Channel> channels = new ArrayList<>();
    private static final List<Entity> entities = new ArrayList<>();
    private boolean bAutoReload = false;
    private AppDatabase db;
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

        myContext = this;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String url = sharedPref.getString("volkszaehlerURL", "") + "/";

        presenter = new MainActivityPresenter(url, this); //fixme
        setupRecyclerView();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        refreshLayout.setOnRefreshListener(() -> {
            presenter.loadChannelData(channels);
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-databasee").build();

        loadEntitiesFromDb();
        refreshLayout.setRefreshing(true);
    }

    private void loadEntitiesFromDb() {
        db.entityDao().getAll()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe((c) -> {
                    if (c.isEmpty()) {
                        presenter.loadEntityDefinitions();
                    } else {
                        entities.addAll(c);
                        loadChannelMetaFromDb();
                    }
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
        db.channelMetaDao().getAll()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe((c) -> {
                    if (c.isEmpty()) {
                        presenter.loadChannelMeta(getChannelsToShow());
                    } else {
                        channels.addAll(c);
                        presenter.loadChannelData(channels);
                    }
                });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        presenter.clearRxSubscriptions();
        super.onPause();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        adapter = new CustomAdapter(channels, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<String> getChannelsToShow() {
        List<String> channels = new ArrayList<>();
        // adding uuids that are checked in preferences
        for (String preference : PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getAll().keySet()) {
            // assume its a UUID of a channel
            if (preference.contains("-") && preference.length() == 36) {
                // is preference checked?
                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(preference, false)) {
                    channels.add(preference);
                }
            }
        }
        return channels;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        //fixme bAutoReload = sharedPref.getBoolean("autoReload", false);
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
                startActivityForResult(new Intent(this, Preferences.class), 1);
                return (true);
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
                return Tools.showAboutDialog(myContext);

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private class GetJSONData extends AsyncTask<String, Void, Void> {

        boolean JSONFehler = false;
        String fehlerAusgabe = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (bAutoReload || jsonStr.equals("")) {
                // Showing progress dialog
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage(getString(R.string.please_wait));
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {

                JSONArray werte;
                SharedPreferences prefs = getSharedPreferences("JSONChannelPrefs", Activity.MODE_PRIVATE);
                String JSONChannels = prefs.getString("JSONChannels", "");
                // are there really channels in the Prefs?
                if (JSONChannels.equals("")) {
                    JSONFehler = true;
                    fehlerAusgabe = getString(R.string.no_Channelinformation_found);
                    return null;
                }
                String uRLUUIDs = arg0[0];
                Log.d("MainActivity", "uRLUUIDs first: " + uRLUUIDs);

                // workaround removing empty string
                String[] channelsAusParameterMitLeerstring = arg0[0].split("&uuid\\[\\]=");
                ArrayList<String> allUUIDs = new ArrayList<>();
                for (String aChannelsAusParameterMitLeerstring : channelsAusParameterMitLeerstring) {
                    if (aChannelsAusParameterMitLeerstring.equals("")) {
                        // empty element
                        continue;
                    }
                    // add all checked channels
                    allUUIDs.add(aChannelsAusParameterMitLeerstring);
                    // check for childs if above is a group
                    String childUUIDs = Tools.getPropertyOfChannel(myContext, aChannelsAusParameterMitLeerstring, Tools.TAG_CHUILDUUIDS);
                    if (null != childUUIDs && !"".equals(childUUIDs)) {
                        if (childUUIDs.contains("|")) {
                            String[] children = (childUUIDs.split("\\|"));

                            for (String child : children) {
                                // add child
                                if (!allUUIDs.contains(child)) {
                                    allUUIDs.add(child);
                                    uRLUUIDs = uRLUUIDs + "&uuid[]=" + child;
                                    Log.d("MainActivity", " uRLUUIDs in Loop: " + uRLUUIDs);
                                }
                            }
                        }
                        // only one child
                        else {
                            if (!allUUIDs.contains(childUUIDs)) {
                                allUUIDs.add(childUUIDs);
                                uRLUUIDs = uRLUUIDs + "&uuid[]=" + childUUIDs;
                                Log.d("MainActivity", "uRLUUIDs only one Child: " + uRLUUIDs);
                            }
                        }
                        //fix Exception "Getting data is not supported for groups", remove group UUID
                        uRLUUIDs = uRLUUIDs.replace("&uuid[]=" + aChannelsAusParameterMitLeerstring, "");
                    }
                }

                //
                ArrayList<HashMap<String, String>> allChannelsMapList = Tools.getChannelsFromJSONStringEntities(JSONChannels);

                for (HashMap<String, String> channelMap : allChannelsMapList) {
                    for (String channelAusParameter : allUUIDs) {
                        if (channelAusParameter.equals(channelMap.get(Tools.TAG_UUID))) {
                            if (!channelValueList.contains(channelMap)) {
                                channelValueList.add(channelMap);
                            }
                            break;
                        }
                    }
                }

                if (bAutoReload || jsonStr.equals("")) {
                    // Creating service handler class instance
                    ServiceHandler sh = new ServiceHandler();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    String url = sharedPref.getString("volkszaehlerURL", "");

                    long millisNow = System.currentTimeMillis();
                    url = url + "/data.json?from=now" + uRLUUIDs;

                    Log.d("MainActivity: ", "url: " + url);

                    String uname = sharedPref.getString("username", "");
                    String pwd = sharedPref.getString("password", "");

                    // Making a request to url and getting response
                    if (uname.equals("")) {
                        jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
                    } else {
                        jsonStr = sh.makeServiceCall(url, ServiceHandler.GET, null, uname, pwd);
                    }
                    Log.d("MainActivity", "response: " + jsonStr);
                }

                if (jsonStr.startsWith("Error: ")) {
                    JSONFehler = true;
                    fehlerAusgabe = android.text.Html.fromHtml(jsonStr).toString();


                } else {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    if (!jsonObj.has(Tools.TAG_DATA)) {
                        JSONFehler = true;
                        fehlerAusgabe = fehlerAusgabe + "\n" + getString(R.string.no_ChannelsSelected);
                    } else {

                        // Getting JSON Array node
                        werte = jsonObj.getJSONArray(Tools.TAG_DATA);
                        // looping through All channels
                        for (int i = 0; i < werte.length(); i++) {
                            String maxWert = "";
                            String tuplesZeit = "";
                            String tuplesWert = "";
                            String minZeit = "";
                            String maxZeit = "";
                            String minWert = "";

                            JSONObject c = werte.getJSONObject(i);

                            String id = c.has(Tools.TAG_UUID) ? c.getString(Tools.TAG_UUID) : "";
                            // really the correct one?

                            String from = c.has(Tools.TAG_FROM) ? c.getString(Tools.TAG_FROM) : "";
                            String to = c.has(Tools.TAG_TO) ? c.getString(Tools.TAG_TO) : "";
                            if (c.has(Tools.TAG_MIN)) {
                                JSONArray minWerte = c.getJSONArray(Tools.TAG_MIN);
                                minZeit = minWerte.getString(0);
                                minWert = minWerte.getString(1);
                            }

                            if (c.has(Tools.TAG_MAX)) {
                                JSONArray maxWerte = c.getJSONArray(Tools.TAG_MAX);
                                maxZeit = maxWerte.getString(0);
                                maxWert = maxWerte.getString(1);
                            }
                            String average = c.has(Tools.TAG_AVERAGE) ? c.getString(Tools.TAG_AVERAGE) : "";
                            String consumption = c.has(Tools.TAG_CONSUMPTION) ? c.getString(Tools.TAG_CONSUMPTION) : "";
                            String rows = c.has(Tools.TAG_ROWS) ? c.getString(Tools.TAG_ROWS) : "";
                            if (c.has(Tools.TAG_TUPLES)) {
                                JSONArray tuples = c.getJSONArray(Tools.TAG_TUPLES);
                                if (tuples.length() < 1) {
                                    continue;
                                }
                                // only one tuple (in URL), otherwise loop here
                                JSONArray tupleWert = tuples.getJSONArray(0);
                                tuplesZeit = tupleWert.getString(0);
                                tuplesWert = tupleWert.getString(1);
                            }

                            int listSize = channelValueList.size();
                            // add values to existing Channels in List
                            String unit;
                            for (int j = 0; j < listSize; j++) {
                                HashMap<String, String> currentChannelFromList = channelValueList.get(j);
                                if (currentChannelFromList.containsValue(id)) {
                                    // add unit
                                    unit = Tools.getUnit(myContext, currentChannelFromList.get(Tools.TAG_TYPE), null);
                                    // adding each child node to HashMap key =>
                                    // value
                                    currentChannelFromList.put(Tools.TAG_FROM, from);
                                    currentChannelFromList.put(Tools.TAG_TO, to);
                                    currentChannelFromList.put("minZeit", minZeit);
                                    currentChannelFromList.put("minWert", minWert);
                                    currentChannelFromList.put("maxZeit", maxZeit);
                                    currentChannelFromList.put("maxWert", maxWert);
                                    currentChannelFromList.put(Tools.TAG_AVERAGE, average);
                                    currentChannelFromList.put("tuplesZeit", tuplesZeit);
                                    currentChannelFromList.put("tuplesWert", tuplesWert + " " + unit);
                                    currentChannelFromList.put(Tools.TAG_CONSUMPTION, consumption);
                                    currentChannelFromList.put(Tools.TAG_ROWS, rows);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JSONFehler = true;
                fehlerAusgabe = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (JSONFehler) {
                jsonStr = "";
                new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.Error)).setMessage(fehlerAusgabe).setNeutralButton(getString(R.string.Close), null).show();
            } else {
                addValuesToListView();
            }

        }
    }*/

    public void loadingChannelValuesSuccess(List<Channel> values) {
        channels.clear();
        channels.addAll(values);
        adapter.notifyDataSetChanged();
        if (refreshLayout != null) refreshLayout.setRefreshing(false);
    }

    public void loadingChannelInfosSuccess(List<Channel> channels) {
        presenter.loadChannelData(channels);
    }

    public void loadingEntitiesSuccess(List<Entity> entities) {
        this.entities.addAll(entities);
        presenter.loadChannelMeta(getChannelsToShow());
        db.entityDao().insertAll(entities);
    }

    public void adapterFailedCallback(String errorMessage) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.Error))
                .setMessage(errorMessage)
                .setNeutralButton(getString(R.string.Close), null)
                .show();
        if (refreshLayout != null) refreshLayout.setRefreshing(false);
    }
}
