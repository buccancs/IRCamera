package com.guide.zm04c.matrix.utils

import android.graphics.Paint
import android.graphics.Rect

        public fun getStringSize(
            str: String,
            textSizePxVal: Float,
        ): IntArray {
            if (textSizePxVal < 0) {
                throw IllegalArgumentException("textSizePxVal > 0 need")
            }
            val paint = Paint()
            paint.textSize = textSizePxVal
            val bounds = Rect()
            if (str.length > 0) {
                paint.getTextBounds(str, 0, str.length, bounds)
            }
            return intArrayOf(bounds.width(), bounds.height())
        }
    }
}
