package com.topdon.gsr.model;

import java.lang.System;

/**
 * Represents synchronization marks for cross-modal alignment
 */
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0014\b\u0002\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0006H\u00c6\u0003J\u0015\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\tH\u00c6\u0003JG\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\u0014\b\u0002\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\tH\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001J\u0011\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00060\u001f\u00a2\u0006\u0002\u0010 J\t\u0010!\u001a\u00020\u0006H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001d\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011\u00a8\u0006\""}, d2 = {"Lcom/topdon/gsr/model/SyncMark;", "", "timestamp", "", "utcTimestamp", "eventType", "", "sessionId", "metadata", "", "(JJLjava/lang/String;Ljava/lang/String;Ljava/util/Map;)V", "getEventType", "()Ljava/lang/String;", "getMetadata", "()Ljava/util/Map;", "getSessionId", "getTimestamp", "()J", "getUtcTimestamp", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toCsvRow", "", "()[Ljava/lang/String;", "toString", "gsr-recording_debug"})
public final class SyncMark {
    private final long timestamp = 0L;
    private final long utcTimestamp = 0L;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String eventType = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String sessionId = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.String, java.lang.String> metadata = null;
    
    /**
     * Represents synchronization marks for cross-modal alignment
     */
    @org.jetbrains.annotations.NotNull
    public final com.topdon.gsr.model.SyncMark copy(long timestamp, long utcTimestamp, @org.jetbrains.annotations.NotNull
    java.lang.String eventType, @org.jetbrains.annotations.NotNull
    java.lang.String sessionId, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.String> metadata) {
        return null;
    }
    
    /**
     * Represents synchronization marks for cross-modal alignment
     */
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    /**
     * Represents synchronization marks for cross-modal alignment
     */
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    /**
     * Represents synchronization marks for cross-modal alignment
     */
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public SyncMark(long timestamp, long utcTimestamp, @org.jetbrains.annotations.NotNull
    java.lang.String eventType, @org.jetbrains.annotations.NotNull
    java.lang.String sessionId, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.String> metadata) {
        super();
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final long getTimestamp() {
        return 0L;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final long getUtcTimestamp() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getEventType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSessionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.String> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.String> getMetadata() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String[] toCsvRow() {
        return null;
    }
}