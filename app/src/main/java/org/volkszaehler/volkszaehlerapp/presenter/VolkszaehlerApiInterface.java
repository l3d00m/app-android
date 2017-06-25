package org.volkszaehler.volkszaehlerapp.presenter;

import org.volkszaehler.volkszaehlerapp.model.ResponseRoot;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface VolkszaehlerApiInterface {
    @GET("data.json")
    Observable<ResponseRoot> getChannelData(@Query("from") String from, @Query("uuid[]") List<String> uuids, @Header("Authorization") String auth);

    @GET("channel.json")
    Observable<ResponseRoot> getChannelMeta(@Query("uuid[]") List<String> uuids, @Header("Authorization") String auth);
}
