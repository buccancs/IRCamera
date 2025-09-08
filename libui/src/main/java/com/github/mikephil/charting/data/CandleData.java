package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

import java.util.ArrayList;
import java.util.List;

public class CandleData extends BarLineScatterCandleBubbleData<ICandleDataSet> {

    /**
     * Method description.
     */
    public CandleData() {
        super();
    }

    /**
     * Method description.
     */
    public CandleData(List<ICandleDataSet> dataSets) {
        super(dataSets);
    }

    /**
     * Method description.
     */
    public CandleData(ICandleDataSet... dataSets) {
        super(dataSets);
    }
}
