package org.volkszaehler.volkszaehlerapp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.volkszaehler.volkszaehlerapp.BuildConfig;
import org.volkszaehler.volkszaehlerapp.PresenterActivityInterface;
import org.volkszaehler.volkszaehlerapp.model.ValueDeserializer;
import org.volkszaehler.volkszaehlerapp.generic.Channel;
import org.volkszaehler.volkszaehlerapp.generic.Entity;
import org.volkszaehler.volkszaehlerapp.model.ResponseRoot;
import org.volkszaehler.volkszaehlerapp.model.ResponseValue;
import org.volkszaehler.volkszaehlerapp.stetho.StethoHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.volkszaehler.volkszaehlerapp.UnsafeOkHttpClient.getUnsafeOkHttpClientBuilder;

public class MainActivityPresenter {
    private VolkszaehlerApiInterface apiInterface;
    private PresenterActivityInterface callbackInterface;
    private Context context;
    private CompositeDisposable disposables = new CompositeDisposable();

    public MainActivityPresenter(String baseUrl, PresenterActivityInterface callbackInterface, Context context) {
        this.callbackInterface = callbackInterface;
        this.context = context;

        // Use a custom client to allow unsafe Https and debugging with Stetho
        OkHttpClient.Builder clientBuilder = getUnsafeOkHttpClientBuilder();
        if (BuildConfig.DEBUG) {
            clientBuilder = new StethoHelper().configureInterceptor(clientBuilder);
        }
        OkHttpClient client = clientBuilder.build();

        // Create some custom deserializer for easier handling of the max and min values
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ResponseValue.class, new ValueDeserializer())
                .create();
        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());
        // Create a retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)
                .build();
        apiInterface = retrofit.create(VolkszaehlerApiInterface.class);


    }

    public void clearRxSubscriptions() {
        disposables.clear();
    }

    private static String getAuth(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String uname = sharedPref.getString("username", "");
        String pwd = sharedPref.getString("password", "");
        if (!uname.isEmpty() && !pwd.isEmpty()) {
            return "Basic " + Base64.encodeToString((uname + ":" + pwd).getBytes(), Base64.NO_WRAP);
        }
        return "";
    }

    public void loadChannelData(List<Channel> channels) {
        List<String> uuidStrings = new ArrayList<>();
        for (Channel info : channels) {
            uuidStrings.add(info.getUuid());
        }
        disposables.add(apiInterface.getChannelsData("now", uuidStrings, getAuth(context))
                // Do the processing in background (async)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                // Iterate through all values objects (UUIDs)
                .flatMapIterable(root -> root.values)
                // Convert the response object to a Channel object
                .flatMap(response -> {
                    for (Channel channel : channels) {
                        if (channel.getUuid().equals(response.uuid)) {
                            channel.setUuid(response.uuid);
                            channel.setAverage(response.average);
                            channel.setConsumption(response.consumption);
                            channel.setMaxValue(response.maxWerte.value);
                            channel.setMinValue(response.minWerte.value);
                            channel.setValue(response.werte.get(0).value);
                            channel.setTime(response.werte.get(0).timestamp);
                            return Observable.just(channel);
                        }
                    }
                    return Observable.empty();
                })
                .toList()
                //fixme .repeatWhen(completed -> completed.delay(3, TimeUnit.SECONDS))
                // Switch back to main Thread for calling the MainActivity methods
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(channelInfos -> callbackInterface.loadingChannelValuesSuccess(channelInfos),
                        e -> {
                            callbackInterface.adapterFailedCallback(e.getMessage());
                            e.printStackTrace();
                        }));

    }

    public void loadChannelMeta(List<String> uuids) {
        channelMetaProccessing(apiInterface.getChannelsMeta(uuids, getAuth(context)));
    }

    public void loadAllChannels() {
        channelMetaProccessing(apiInterface.getAllChannels(getAuth(context)));
    }

    private void channelMetaProccessing(Observable<ResponseRoot> observable) {
        disposables.add(observable
                .observeOn(Schedulers.io())
                // Do the processing in background (async)
                .subscribeOn(Schedulers.io())
                // Iterate through all values objects (UUIDs)
                .flatMapIterable(root -> root.infos)
                // Convert the response object to a ChannelInfo object
                .map(response -> {
                    Channel channel = new Channel();
                    channel.setUuid(response.uuid);
                    channel.setType(response.type);
                    channel.setCost(response.cost);
                    channel.setResolution(response.resolution);
                    channel.setInitialConsumption(response.initialConsumption);
                    channel.setColor(response.color);
                    channel.setPublic(response.isPublic);
                    channel.setStyle(response.style);
                    channel.setTitle(response.title);
                    channel.setDescription(response.description);
                    return channel;
                })
                .toList()
                // Switch back to main Thread for calling the MainActivity methods
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(channelInfos -> callbackInterface.loadingChannelInfosSuccess(channelInfos),
                        e -> {
                            callbackInterface.adapterFailedCallback(e.getMessage());
                            e.printStackTrace();
                        }));
    }


    private String getTranslation(HashMap<String, String> translations) {
        String systemLocale = Locale.getDefault().getLanguage();
        Iterator it = translations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (systemLocale.equals(pair.getKey().toString())) {
                return pair.getValue().toString();
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return translations.get("en");
    }

    public void loadEntityDefinitions() {
        disposables.add(apiInterface.getChannelDefinitions(getAuth(context))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map(root -> root.capabilities)
                .flatMapIterable(capabilities -> capabilities.definitions.entities)
                .map(entityResponse -> {
                    Entity entity = new Entity();
                    entity.setName(entityResponse.name);
                    entity.setHasConsumption(entityResponse.hasConsumption);
                    entity.setScale(entityResponse.scale);
                    entity.setUnit(entityResponse.unit);
                    entity.setFriendlyName(getTranslation(entityResponse.translations));
                    return entity;
                })
                .toList()
                .subscribe(list -> callbackInterface.loadingEntitiesSuccess(list),
                        e -> {
                            callbackInterface.adapterFailedCallback(e.getMessage());
                            e.printStackTrace();
                        }));
    }

    public void loadTotalConsumption(String uuid) {
        apiInterface.getSingleChannelData("0", "1", "day", uuid, getAuth(context))
                .flatMapIterable(root -> root.values)
                .flatMapIterable(values -> values.werte)
                .map(werte -> werte.value)
                .subscribe(callbackInterface::loadingTotalConsumptionSuccess,
                        e -> callbackInterface.adapterFailedCallback(e.getMessage()));
    }
}
