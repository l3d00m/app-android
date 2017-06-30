package org.volkszaehler.volkszaehlerapp.presenter;

import org.volkszaehler.volkszaehlerapp.model.ResponseRoot;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VolkszaehlerApiInterface {
    @GET("data.json")
    Observable<ResponseRoot> getChannelsData(@Query("from") String from,
                                             @Query("uuid[]") List<String> uuids);

    @GET("data.json")
    Observable<ResponseRoot> getSingleChannelData(@Query("from") Double from,
                                                  @Query("to") Double to,
                                                  @Query("tuples") Integer tuples,
                                                  @Query("group") String group,
                                                  @Query("uuid[]") String uuids);

    @GET("entity.json")
    Observable<ResponseRoot> getChannelsMeta(@Query("uuid[]") List<String> uuids);

    @GET("entity.json")
    Observable<ResponseRoot> getAllChannels();

    @GET("capabilities/definitions/entities.json")
    Observable<ResponseRoot> getChannelDefinitions();
}
