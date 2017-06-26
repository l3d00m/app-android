package org.volkszaehler.volkszaehlerapp.presenter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.volkszaehler.volkszaehlerapp.ChannelDetails;
import org.volkszaehler.volkszaehlerapp.ValueDeserializer;
import org.volkszaehler.volkszaehlerapp.model.ResponseValue;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.volkszaehler.volkszaehlerapp.UnsafeOkHttpClient.getUnsafeOkHttpClientBuilder;

public class ChannelDetailsPresenter {
    private final ChannelDetails context;
    private final VolkszaehlerApiInterface apiInterface;

    public ChannelDetailsPresenter(String baseUrl, ChannelDetails context) {
        this.context = context;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = getUnsafeOkHttpClientBuilder()
                .addInterceptor(logging) //fixme remove logger
                .build();
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

    public void loadTotalConsumption(String uuid) {
        apiInterface.getSingleChannelData("0", "1", "day", uuid, "")
                .flatMapIterable(root -> root.values)
                .flatMapIterable(values -> values.werte)
                .map(werte -> werte.value)
                .subscribe(context::totalConsumptionLoaded,
                        e -> context.presenterFailedCallback(e.getMessage()));
    }
}
