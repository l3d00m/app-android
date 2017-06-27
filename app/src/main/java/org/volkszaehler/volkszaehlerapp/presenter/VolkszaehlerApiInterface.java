package org.volkszaehler.volkszaehlerapp.presenter;

import org.volkszaehler.volkszaehlerapp.model.ResponseRoot;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface VolkszaehlerApiInterface {
    @GET("data.json")
    Observable<ResponseRoot> getChannelsData(@Query("from") String from,
                                             @Query("uuid[]") List<String> uuids,
                                             @Header("Authorization") String auth);

    @GET("data.json")
    Observable<ResponseRoot> getSingleChannelData(@Query("from") String from,
                                                  @Query("tuples") String tuples,
                                                  @Query("group") String group,
                                                  @Query("uuid[]") String uuids,
                                                  @Header("Authorization") String auth);

    @GET("entity.json")
    Observable<ResponseRoot> getChannelsMeta(@Query("uuid[]") List<String> uuids,
                                             @Header("Authorization") String auth);

    @GET("entity.json")
    Observable<ResponseRoot> getAllChannels(@Header("Authorization") String auth);

    @GET("capabilities/definitions/entities.json")
    Observable<ResponseRoot> getChannelDefinitions(@Header("Authorization") String auth);
}
