package com.topdon.gsr.model;

import java.lang.System;

/**
 * Represents session information and metadata
 */
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\b \n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001Bi\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u0012\u0014\b\u0002\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u000e\u00a2\u0006\u0002\u0010\u000fJ\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010&\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0011J\u000b\u0010\'\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010*\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u00c6\u0003J\u0015\u0010+\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u000eH\u00c6\u0003Jv\u0010,\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\t\u001a\u00020\u00052\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0014\b\u0002\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u000eH\u00c6\u0001\u00a2\u0006\u0002\u0010-J\u0013\u0010.\u001a\u00020/2\b\u00100\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\u0006\u00101\u001a\u00020\u0005J\t\u00102\u001a\u000203H\u00d6\u0001J\u0006\u00104\u001a\u00020/J\t\u00105\u001a\u00020\u0003H\u00d6\u0001R\u001e\u0010\u0006\u001a\u0004\u0018\u00010\u0005X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u0014\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001d\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001a\u0010\t\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0018R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001aR\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0018R \u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#\u00a8\u00066"}, d2 = {"Lcom/topdon/gsr/model/SessionInfo;", "", "sessionId", "", "startTime", "", "endTime", "participantId", "studyName", "sampleCount", "syncMarks", "", "Lcom/topdon/gsr/model/SyncMark;", "metadata", "", "(Ljava/lang/String;JLjava/lang/Long;Ljava/lang/String;Ljava/lang/String;JLjava/util/List;Ljava/util/Map;)V", "getEndTime", "()Ljava/lang/Long;", "setEndTime", "(Ljava/lang/Long;)V", "Ljava/lang/Long;", "getMetadata", "()Ljava/util/Map;", "getParticipantId", "()Ljava/lang/String;", "getSampleCount", "()J", "setSampleCount", "(J)V", "getSessionId", "getStartTime", "getStudyName", "getSyncMarks", "()Ljava/util/List;", "setSyncMarks", "(Ljava/util/List;)V", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(Ljava/lang/String;JLjava/lang/Long;Ljava/lang/String;Ljava/lang/String;JLjava/util/List;Ljava/util/Map;)Lcom/topdon/gsr/model/SessionInfo;", "equals", "", "other", "getDurationMs", "hashCode", "", "isActive", "toString", "gsr-recording_debug"})
public final class SessionInfo {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String sessionId = null;
    private final long startTime = 0L;
    @org.jetbrains.annotations.Nullable
    private java.lang.Long endTime;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String participantId = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String studyName = null;
    private long sampleCount;
    @org.jetbrains.annotations.NotNull
    private java.util.List<com.topdon.gsr.model.SyncMark> syncMarks;
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.String, java.lang.String> metadata = null;
    
    /**
     * Represents session information and metadata
     */
    @org.jetbrains.annotations.NotNull
    public final com.topdon.gsr.model.SessionInfo copy(@org.jetbrains.annotations.NotNull
    java.lang.String sessionId, long startTime, @org.jetbrains.annotations.Nullable
    java.lang.Long endTime, @org.jetbrains.annotations.Nullable
    java.lang.String participantId, @org.jetbrains.annotations.Nullable
    java.lang.String studyName, long sampleCount, @org.jetbrains.annotations.NotNull
    java.util.List<com.topdon.gsr.model.SyncMark> syncMarks, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.String> metadata) {
        return null;
    }
    
    /**
     * Represents session information and metadata
     */
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    /**
     * Represents session information and metadata
     */
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    /**
     * Represents session information and metadata
     */
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public SessionInfo(@org.jetbrains.annotations.NotNull
    java.lang.String sessionId, long startTime, @org.jetbrains.annotations.Nullable
    java.lang.Long endTime, @org.jetbrains.annotations.Nullable
    java.lang.String participantId, @org.jetbrains.annotations.Nullable
    java.lang.String studyName, long sampleCount, @org.jetbrains.annotations.NotNull
    java.util.List<com.topdon.gsr.model.SyncMark> syncMarks, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.String> metadata) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSessionId() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final long getStartTime() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long getEndTime() {
        return null;
    }
    
    public final void setEndTime(@org.jetbrains.annotations.Nullable
    java.lang.Long p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getParticipantId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getStudyName() {
        return null;
    }
    
    public final long component6() {
        return 0L;
    }
    
    public final long getSampleCount() {
        return 0L;
    }
    
    public final void setSampleCount(long p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.topdon.gsr.model.SyncMark> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.topdon.gsr.model.SyncMark> getSyncMarks() {
        return null;
    }
    
    public final void setSyncMarks(@org.jetbrains.annotations.NotNull
    java.util.List<com.topdon.gsr.model.SyncMark> p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.String> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.String> getMetadata() {
        return null;
    }
    
    public final long getDurationMs() {
        return 0L;
    }
    
    public final boolean isActive() {
        return false;
    }
}