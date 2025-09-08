package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.data.CandleData;

    /**
     * CandleDataProvider class.
     *
     * Provides candledataprovider functionality.
     */
    public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
