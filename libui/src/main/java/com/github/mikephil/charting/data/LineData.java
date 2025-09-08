
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Data object that encapsulates all data associated with a LineChart.
 * 
 * @author Philipp Jahoda
 */
public class LineData extends BarLineScatterCandleBubbleData<ILineDataSet> {

    /**
     * Method description.
     */
    public LineData() {
        super();
    }

    /**
     * Method description.
     */
    public LineData(ILineDataSet... dataSets) {
        super(dataSets);
    }

    /**
     * Method description.
     */
    public LineData(List<ILineDataSet> dataSets) {
        super(dataSets);
    }
}
