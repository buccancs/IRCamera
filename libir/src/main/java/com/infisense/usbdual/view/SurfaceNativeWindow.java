package com.infisense.usbdual.view;

/*
 * Comment removed (contained Chinese characters)
* @Author: brilliantzhao
 * @CreateDate: 2022.9.8 10:26
 * @UpdateUser:
 * @UpdateDate: 2022.9.8 10:26
 * @UpdateRemark:
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
