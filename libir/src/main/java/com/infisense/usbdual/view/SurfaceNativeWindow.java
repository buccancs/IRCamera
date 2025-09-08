package com.infisense.usbdual.view;

/**
 * Utility class.
 * 
 * @author brilliantzhao
 * @since 2022.9.8 10:25
 */
public class SurfaceNativeWindow {

    static {
        System.loadLibrary("native-window");
    }

    public native void onCreateSurface(Object surface, int width, int height);

    public native void onDrawFrame(byte[] ARGBdata, int width, int height);

    public native void onReleaseSurface();

    public native void drawBitmap(Object surface, Object bitmap);
}
