package com.topdon.tc001.view;

/**
 * @author: CaiSongL
 * @date: 2023/6/3 14:43
 */
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

    /**
     * MyGLSurfaceView class.
     *
     * Provides myglsurfaceview functionality.
     */
    public class MyGLSurfaceView extends GLSurfaceView {
    private MyRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        renderer = new MyRenderer();
        setRenderer(renderer);
    }

    /**
 * MyRenderer class.
 * 
 * Provides myrenderer functionality.
 */
private class MyRenderer implements GLSurfaceView.Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            // [Chinese text]OpenGL[Chinese text], Settings[Chinese text]
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            // [Chinese text]operation...
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            // [Chinese text], Settings[Chinese text]
            GLES20.glViewport(0, 0, width, height);
            // [Chinese text]...
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            // [Chinese text], [Chinese text]point[Chinese text]
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            // [Chinese text]point[Chinese text]...
        }
    }
}

