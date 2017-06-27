package org.volkszaehler.volkszaehlerapp.stetho;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class StethoHelper {
    public void init(Context context) {
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    public OkHttpClient.Builder configureInterceptor(OkHttpClient.Builder httpClient) {
        return httpClient.addNetworkInterceptor(new StethoInterceptor());
    }

}
