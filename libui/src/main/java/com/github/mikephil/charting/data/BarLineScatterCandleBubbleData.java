
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import java.util.List;

/**
 * Baseclass for all Line, Bar, Scatter, Candle and Bubble data.
 * 
 * @author Philipp Jahoda
 */
public abstract class BarLineScatterCandleBubbleData<T extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>
        extends ChartData<T> {
    
    /**
     * Method description.
     */
    public BarLineScatterCandleBubbleData() {
        super();
    }

    /**
     * Method description.
     */
    public BarLineScatterCandleBubbleData(T... sets) {
        super(sets);
    }

    /**
     * Method description.
     */
    public BarLineScatterCandleBubbleData(List<T> sets) {
        super(sets);
    }
}
