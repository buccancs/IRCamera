package com.topdon.pseudo.constant

    fun getColorByIndex(
        isTC007: Boolean,
        index: Int,
    ): IntArray =
        when (index) {
            0 -> colorList1
            1 -> colorList2
            2 -> if (isTC007) colorList3TC007 else colorList3
            3 -> colorList4
            else -> colorList5
        }
}
