package com.topdon.lib.ui.utils

 * @author: CaiSongL
 * @date: 2023/4/1 14:12
@Deprecated("--，")
enum class Direction {
    START {
        override fun applyTo(delta: Int): Int {
            return delta * -1
        }

        override fun sameAs(direction: Int): Boolean {
            return direction < 0
        }
    },
    END {
        override fun applyTo(delta: Int): Int {
            return delta
        }

        override fun sameAs(direction: Int): Boolean {
            return direction > 0
        }
    };

    abstract fun applyTo(delta: Int): Int
    abstract fun sameAs(direction: Int): Boolean

    companion object {
        @JvmStatic
        fun fromDelta(delta: Int): Direction {
            return if (delta > 0) END else START
        }
    }
}