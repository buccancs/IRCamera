package com.topdon.gsr.model;

import java.lang.System;

/**
 * Represents a single GSR data sample with timestamp information
 */
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0004\b\u0086\b\u0018\u0000 %2\u00020\u0001:\u0001%B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\nH\u00c6\u0003JE\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001J\u0011\u0010!\u001a\b\u0012\u0004\u0012\u00020\n0\"\u00a2\u0006\u0002\u0010#J\t\u0010$\u001a\u00020\nH\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010\u00a8\u0006&"}, d2 = {"Lcom/topdon/gsr/model/GSRSample;", "", "timestamp", "", "utcTimestamp", "conductance", "", "resistance", "sampleIndex", "sessionId", "", "(JJDDJLjava/lang/String;)V", "getConductance", "()D", "getResistance", "getSampleIndex", "()J", "getSessionId", "()Ljava/lang/String;", "getTimestamp", "getUtcTimestamp", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toCsvRow", "", "()[Ljava/lang/String;", "toString", "Companion", "gsr-recording_release"})
public final class GSRSample {
    private final long timestamp = 0L;
    private final long utcTimestamp = 0L;
    private final double conductance = 0.0;
    private final double resistance = 0.0;
    private final long sampleIndex = 0L;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String sessionId = null;
    @org.jetbrains.annotations.NotNull
    public static final com.topdon.gsr.model.GSRSample.Companion Companion = null;
    
    /**
     * Represents a single GSR data sample with timestamp information
     */
    @org.jetbrains.annotations.NotNull
    public final com.topdon.gsr.model.GSRSample copy(long timestamp, long utcTimestamp, double conductance, double resistance, long sampleIndex, @org.jetbrains.annotations.NotNull
    java.lang.String sessionId) {
        return null;
    }
    
    /**
     * Represents a single GSR data sample with timestamp information
     */
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    /**
     * Represents a single GSR data sample with timestamp information
     */
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    /**
     * Represents a single GSR data sample with timestamp information
     */
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public GSRSample(long timestamp, long utcTimestamp, double conductance, double resistance, long sampleIndex, @org.jetbrains.annotations.NotNull
    java.lang.String sessionId) {
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
    
    public final double component3() {
        return 0.0;
    }
    
    public final double getConductance() {
        return 0.0;
    }
    
    public final double component4() {
        return 0.0;
    }
    
    public final double getResistance() {
        return 0.0;
    }
    
    public final long component5() {
        return 0L;
    }
    
    public final long getSampleIndex() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSessionId() {
        return null;
    }
    
    /**
     * Convert sample to CSV row format
     */
    @org.jetbrains.annotations.NotNull
    public final java.lang.String[] toCsvRow() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J&\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n\u00a8\u0006\u000b"}, d2 = {"Lcom/topdon/gsr/model/GSRSample$Companion;", "", "()V", "createSimulated", "Lcom/topdon/gsr/model/GSRSample;", "timestamp", "", "utcTimestamp", "sampleIndex", "sessionId", "", "gsr-recording_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Create a simulated GSR sample for testing/demo purposes
         */
        @org.jetbrains.annotations.NotNull
        public final com.topdon.gsr.model.GSRSample createSimulated(long timestamp, long utcTimestamp, long sampleIndex, @org.jetbrains.annotations.NotNull
        java.lang.String sessionId) {
            return null;
        }
    }
}