package com.topdon.pseudo.constant

 * @author: CaiSongL
 * @date: 2023/8/8 19:37
object ColorRecommend {

    /** colorList1 property */
    val colorList1 = intArrayOf(
        0xff0000ff.toInt(),
        0xffff0000.toInt(),
        0xffffff00.toInt(),
    )

    /** colorList2 property */
    val colorList2 = intArrayOf(
        0xff000000.toInt(),
        0xffffffff.toInt(),
        0xffff0000.toInt(),
    )
    /** colorList3 property */
    val colorList3 = intArrayOf(
        0xff0000ff.toInt(),
        0xff00ff00.toInt(),
        0xffffff00.toInt(),
        0xffff0000.toInt(),
    )
    /** colorList3TC007 property */
    val colorList3TC007 = intArrayOf(
        0xff0000ff.toInt(),
        0xff00ff00.toInt(),
        0xffff0000.toInt(),
    )
    /** colorList4 property */
    val colorList4 = intArrayOf(
        0xff000000.toInt(),
        0xFF840000.toInt(),
        0xffff0000.toInt(),
    )
    /** colorList5 property */
    val colorList5 = intArrayOf(
        0xff0000ff.toInt(),
        0xFF7B7B83.toInt(),
        0xffffff00.toInt(),
    )

     * @param index 0- 1- 2- 3- 4
    /**
     * Function description.
     */
    fun getColorByIndex(isTC007: Boolean, index: Int): IntArray = when (index) {
        0 -> colorList1
        1 -> colorList2
        2 -> if (isTC007) colorList3TC007 else colorList3
        3 -> colorList4
        else -> colorList5
    }

}