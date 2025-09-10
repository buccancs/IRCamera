package com.topdon.module.thermal.tools.medie

interface IYapVideoProvider<Bitmap> {
    /**
     * bitmap list size, you can set like
     *
     * return bitmapList.size()
     */
    fun size(): Int

    /**
     * the next bitmap
     */
    operator fun next(): Bitmap

    /**
     * progress
     * If 1f is returned, progress is complete
     * A return of -1 indicates failure
     */
    fun progress(progress: Float)
}
