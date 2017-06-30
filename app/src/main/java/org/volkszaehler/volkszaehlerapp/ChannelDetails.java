package org.volkszaehler.volkszaehlerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.volkszaehler.volkszaehlerapp.generic.Channel;
import org.volkszaehler.volkszaehlerapp.generic.Entity;
import org.volkszaehler.volkszaehlerapp.presenter.MainActivityPresenter;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

public class ChannelDetails extends AppCompatActivity implements PresenterActivityInterface {

    private Context myContext;
    private boolean strom = false;
    private boolean gas = false;
    private boolean water = false;
    private Channel channel;
    private Entity entity;
    private MainActivityPresenter presenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.details);

        setTitle(getString(R.string.ChannelDetailHeader));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        myContext = this;

        Intent i = getIntent();
        channel = MainActivity.getChannel(i.getStringExtra(Tools.TAG_UUID));
        if (channel == null) finish();
        entity = MainActivity.getEntity(channel.getType());
        if (entity == null) finish();

        String typeOfChannel = entity.getName();
        switch (typeOfChannel) {
            case "power":
            case "powersensor":
                strom = true;
                break;
            case "temperature":
                //temp = true;
                break;
            case "gas":
                gas = true;
                break;
            case "water":
                water = true;
                break;
            default:
                Log.e("ChannelDetails", "Unknown channel type: " + typeOfChannel);
        }

        //empty color, default = blue
        String col = "".equals(channel.getColor()) ? "blue" : channel.getColor();

        if (col != null) {
            try {
                int cColor = Color.parseColor(col.toUpperCase(Locale.US));
                ((TextView) findViewById(R.id.textViewTitle)).setTextColor(cColor);
                ((TextView) findViewById(R.id.textViewValue)).setTextColor(cColor);
            } catch (IllegalArgumentException e) {
                Log.e("ChannelDetails", "Unknown Color: " + col);
            }
        }
        String myTitel = channel.getTitle();
        ((TextView) findViewById(R.id.textViewTitle)).setText(myTitel);
        if (entity.getUnit() != null) {
            ((TextView) findViewById(R.id.textViewValue)).setText(Tools.f00.format(channel.getValue()) + " " + entity.getUnit());
        } else {
            ((TextView) findViewById(R.id.textViewValue)).setText(Tools.f00.format(channel.getValue()));
        }

        try {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(channel.getTime());
            ((TextView) findViewById(R.id.textViewDateValue)).setText(currentDateTimeString);
        } catch (NumberFormatException nfe) {
            Log.e("ChannelDetails", "strange Tuple Time: " + channel.getTime());
        }

        ((TextView) findViewById(R.id.textViewDescription)).setText(channel.getDescription());
        try {
            Double sCost = channel.getCost();
            if (sCost > 0) {
                double dCost = sCost;
                if (strom || gas) {
                    ((TextView) findViewById(R.id.textViewCost)).setText(Tools.f0.format(dCost * 100) + Units.CENT);
                } else if (water) {
                    double sResolution = channel.getResolution();
                    ((TextView) findViewById(R.id.textViewCost)).setText(Tools.f00.format(dCost * sResolution * 1000) + Units.EUROpermmm);
                }
            } else {
                // no cost
                findViewById(R.id.textViewTitleCost).setVisibility(View.GONE);
                findViewById(R.id.textViewCost).setVisibility(View.GONE);
            }
        } catch (NumberFormatException nfe) {
            Log.e("ChannelDetails", "strange costs: " + channel.getCost());
        }

        ((TextView) findViewById(R.id.textViewUUID)).setText(channel.getUuid());
        ((TextView) findViewById(R.id.textViewType)).setText(entity.getFriendlyName());

        String childrenNames = "";
        if (channel.getChildNames() != null && !channel.getChildNames().isEmpty()) {
            for (String child : channel.getChildNames()) {
                childrenNames = childrenNames + "\n" + child;
            }
            ((TextView) findViewById(R.id.textViewChildren)).setText(childrenNames);
        } else {
            ((TextView) findViewById(R.id.textViewTitleChildren)).setText("");
        }

        if (channel.getInitialConsumption() > 0) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ChannelDetails.this);
            String url = sharedPref.getString("volkszaehlerURL", "") + "/";
            presenter = new MainActivityPresenter(url, this, this.getBaseContext());
            presenter.loadTotalConsumption(channel.getUuid());
        } else {
            //remove consumption from dialog
            findViewById(R.id.textViewTitleGesamt).setVisibility(View.GONE);
            findViewById(R.id.textViewGesamt).setVisibility(View.GONE);
        }
    }

    public void chartsDetailsHandler(View view) {
        switch (view.getId()) {
            case R.id.buttonViewChartHour:
                callChart("hour");
                break;
            case R.id.buttonViewChartDay:
                callChart("day");
                break;
            case R.id.buttonViewChartWeek:
                callChart("week");
                break;
            case R.id.buttonViewChartMonth:
                callChart("month");
                break;
            default:
                callChart("day");
                break;
        }
    }

    private void callChart(String zeitRaum) {
        long from;
        long to;
        long millisNow = System.currentTimeMillis();

        switch (zeitRaum) {
            case "hour":
                to = millisNow;
                from = millisNow - 3600000;
                break;
            case "week":
                to = millisNow;
                from = millisNow - 604800000;
                break;
            case "month":
                to = millisNow;
                from = millisNow - 2419200000L;
                break;
            case "day":
            default:
                to = millisNow;
                from = millisNow - 86400000;
                break;
        }

        Intent detailChartIntent = new Intent(ChannelDetails.this, ChartDetails.class);
        detailChartIntent.putExtra("MUUID", channel.getUuid());
        detailChartIntent.putExtra("From", from);
        detailChartIntent.putExtra("To", to);
        startActivity(detailChartIntent);
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
                startActivity(new Intent(this, Preferences.class));
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
                return true;


            case R.id.about:
                return Tools.showAboutDialog(myContext);

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        if (presenter != null) presenter.stopAllLoading();
        super.onStop();
    }

    @Override
    public void loadingEntitiesSuccess(List<Entity> entities) {
        // stub
    }

    @Override
    public void adapterFailedCallback(String errorMessage) {
        new AlertDialog.Builder(ChannelDetails.this)
                .setTitle(getString(R.string.Error))
                .setMessage(errorMessage)
                .setNeutralButton(getString(R.string.Close), null)
                .show();
    }

    @Override
    public void loadingChannelInfosSuccess(List<Channel> channels) {
        // stub
    }

    @Override
    public void loadingChannelValuesSuccess(List<Channel> values) {
        // stub
    }

    @Override
    public void loadingTotalConsumptionSuccess(Double totalConsumption) {
        String consumptionString = "";
        String unit = entity.getUnit();
        if ("gas".equals(channel.getType())) {
            unit = unit.substring(0, 2);
        } else if ("water".equals(channel.getType())) {
            unit = unit.substring(0, 1);
        } else {
            unit = "k" + unit + "h";
        }
        if (gas) {
            consumptionString = Tools.f000.format(totalConsumption + channel.getInitialConsumption());
        } else if (strom) {
            consumptionString = String.valueOf(Tools.f000.format(totalConsumption + channel.getInitialConsumption()));
        } else if (water) {
            consumptionString = String.valueOf(Tools.f0.format(totalConsumption + channel.getInitialConsumption()));
        }
        ((TextView) findViewById(R.id.textViewGesamt)).setText(consumptionString + " " + unit);
    }
}
