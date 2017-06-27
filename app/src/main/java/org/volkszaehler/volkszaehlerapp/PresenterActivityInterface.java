package org.volkszaehler.volkszaehlerapp;

import org.volkszaehler.volkszaehlerapp.generic.Channel;
import org.volkszaehler.volkszaehlerapp.generic.Entity;

import java.util.List;

public interface PresenterActivityInterface {
    void loadingEntitiesSuccess(List<Entity> entities);

    void adapterFailedCallback(String errorMessage);

    void loadingChannelInfosSuccess(List<Channel> channels);

    void loadingChannelValuesSuccess(List<Channel> values);

    void loadingTotalConsumptionSuccess(Double totalConsumption);
}
