
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data container for the RadarChart.
 *
 * @author Philipp Jahoda
 */
public class RadarData extends ChartData<IRadarDataSet> {

    /**
     * Private method description.
     */
    private List<String> mLabels;

    /**
     * Method description.
     */
    public RadarData() {
        super();
    }

    /**
     * Method description.
     */
    public RadarData(List<IRadarDataSet> dataSets) {
        super(dataSets);
    }

    /**
     * Method description.
     */
    public RadarData(IRadarDataSet... dataSets) {
        super(dataSets);
    }

    /**
     * Sets the labels that should be drawn around the RadarChart at the end of each web line.
     *
     * @param labels
     */
    /**
     * Method description.
     */
    public void setLabels(List<String> labels) {
        this.mLabels = labels;
    }

    /**
     * Sets the labels that should be drawn around the RadarChart at the end of each web line.
     *
     * @param labels
     */
    /**
     * Method description.
     */
    public void setLabels(String... labels) {
        this.mLabels = Arrays.asList(labels);
    }

    /**
     * Method description.
     */
    public List<String> getLabels() {
        return mLabels;
    }

    @Override
    /**
     * Method description.
     */
    public Entry getEntryForHighlight(Highlight highlight) {
        return getDataSetByIndex(highlight.getDataSetIndex()).getEntryForIndex((int) highlight.getX());
    }
}
