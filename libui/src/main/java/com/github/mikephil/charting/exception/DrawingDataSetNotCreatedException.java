package com.github.mikephil.charting.exception;

public class DrawingDataSetNotCreatedException extends RuntimeException {

	/**
     * 
     */
    /**
     * Private method description.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Method description.
     */
    public DrawingDataSetNotCreatedException() {
		super("Have to create a new drawing set first. Call ChartData's createNewDrawingDataSet() method");
	}

}
