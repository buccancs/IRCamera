package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.data.ScatterData;

    /**
     * ScatterDataProvider class.
     *
     * Provides scatterdataprovider functionality.
     */
    public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
