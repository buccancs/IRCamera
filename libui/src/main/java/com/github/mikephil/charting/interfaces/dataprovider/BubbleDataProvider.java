package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.data.BubbleData;

    /**
     * BubbleDataProvider class.
     *
     * Provides bubbledataprovider functionality.
     */
    public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
