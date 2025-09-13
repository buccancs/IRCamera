package com.topdon.commons.poster;

import androidx.annotation.NonNull;

/**
 * date: 2019/8/7 09:44
* author: chuanfeng.bi
 */
interface Poster {
 /**
 * Comment removed (contained Chinese characters)
 * 
 * Comment removed (contained Chinese characters)
 */
 void enqueue(@NonNull Runnable runnable);

 /**
 * Comment removed (contained Chinese characters)
 */
 void clear();
}
