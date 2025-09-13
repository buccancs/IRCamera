package com.topdon.ble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

/**
 * date: 2019/8/11 15:34
* author: bichuanfeng
 */
public interface Request {
 /**
 * Comment removed (contained Chinese characters)
 */
 @NonNull
 Device getDevice();

 /**
 * Comment removed (contained Chinese characters)
 */
 @NonNull
 RequestType getType();

 /**
 * Comment removed (contained Chinese characters)
 */
 @Nullable
 String getTag();

 /**
 * Comment removed (contained Chinese characters)
 */
 @Nullable
 UUID getService();

 /**
 * Comment removed (contained Chinese characters)
 */
 @Nullable
 UUID getCharacteristic();

 /**
 * Comment removed (contained Chinese characters)
 */
 @Nullable
 UUID getDescriptor();

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 void execute(Connection connection);
}
