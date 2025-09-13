package com.example.open3d

/**
 * 统一的c++Debug
 * @author: CaiSongL
 * @date: 2023/10/23 17:57
 */
object JNITool {
    external fun draw_edge_from_temp_reigon_bitmap_argb_psd(
        image: ByteArray,
        temperature: ByteArray,
        image_h: Int,
        image_w: Int,
        high_t: Float,
        low_t: Float,
        color_h: Int,
        color_l: Int,
        type: Int,
    ): ByteArray
}
