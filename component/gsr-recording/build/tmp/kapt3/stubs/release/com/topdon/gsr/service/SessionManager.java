package com.topdon.gsr.service;

import java.lang.System;

/**
 * Session lifecycle and metadata management
 */
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 $2\u00020\u0001:\u0003$%&B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000bJ\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\b0\u0010J\u0010\u0010\u0011\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0012\u001a\u00020\u0007J@\u0010\u0013\u001a\u00020\b2\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u00072\u0014\b\u0002\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u0017J\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\b0\u0010J\u0010\u0010\u0019\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0012\u001a\u00020\u0007J\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u0012\u001a\u00020\u0007J\u000e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0012\u001a\u00020\u0007J\u000e\u0010\u001e\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000bJ\u0016\u0010\u001f\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u00072\u0006\u0010 \u001a\u00020\u0007J\"\u0010!\u001a\u00020\u001d2\u0006\u0010\u0012\u001a\u00020\u00072\u0012\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\r0#R\u001a\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/topdon/gsr/service/SessionManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "activeSessions", "Ljava/util/concurrent/ConcurrentHashMap;", "", "Lcom/topdon/gsr/model/SessionInfo;", "sessionListeners", "", "Lcom/topdon/gsr/service/SessionManager$SessionListener;", "addSessionListener", "", "listener", "completeAllSessions", "", "completeSession", "sessionId", "createSession", "participantId", "studyName", "metadata", "", "getActiveSessions", "getSession", "getSessionStats", "Lcom/topdon/gsr/service/SessionManager$SessionStats;", "isSessionActive", "", "removeSessionListener", "reportSessionError", "error", "updateSession", "updates", "Lkotlin/Function1;", "Companion", "SessionListener", "SessionStats", "gsr-recording_release"})
public final class SessionManager {
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    public static final com.topdon.gsr.service.SessionManager.Companion Companion = null;
    private static final java.lang.String TAG = "SessionManager";
    @kotlin.jvm.Volatile
    private static volatile com.topdon.gsr.service.SessionManager INSTANCE;
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, com.topdon.gsr.model.SessionInfo> activeSessions = null;
    private final java.util.List<com.topdon.gsr.service.SessionManager.SessionListener> sessionListeners = null;
    
    public SessionManager(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    public final void addSessionListener(@org.jetbrains.annotations.NotNull
    com.topdon.gsr.service.SessionManager.SessionListener listener) {
    }
    
    public final void removeSessionListener(@org.jetbrains.annotations.NotNull
    com.topdon.gsr.service.SessionManager.SessionListener listener) {
    }
    
    /**
     * Create a new session
     */
    @org.jetbrains.annotations.NotNull
    public final com.topdon.gsr.model.SessionInfo createSession(@org.jetbrains.annotations.Nullable
    java.lang.String sessionId, @org.jetbrains.annotations.Nullable
    java.lang.String participantId, @org.jetbrains.annotations.Nullable
    java.lang.String studyName, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.String> metadata) {
        return null;
    }
    
    /**
     * Get active session by ID
     */
    @org.jetbrains.annotations.Nullable
    public final com.topdon.gsr.model.SessionInfo getSession(@org.jetbrains.annotations.NotNull
    java.lang.String sessionId) {
        return null;
    }
    
    /**
     * Get all active sessions
     */
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.topdon.gsr.model.SessionInfo> getActiveSessions() {
        return null;
    }
    
    /**
     * Update session metadata
     */
    public final boolean updateSession(@org.jetbrains.annotations.NotNull
    java.lang.String sessionId, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.topdon.gsr.model.SessionInfo, kotlin.Unit> updates) {
        return false;
    }
    
    /**
     * Complete a session (mark as ended)
     */
    @org.jetbrains.annotations.Nullable
    public final com.topdon.gsr.model.SessionInfo completeSession(@org.jetbrains.annotations.NotNull
    java.lang.String sessionId) {
        return null;
    }
    
    /**
     * Force complete all active sessions
     */
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.topdon.gsr.model.SessionInfo> completeAllSessions() {
        return null;
    }
    
    /**
     * Check if session is active
     */
    public final boolean isSessionActive(@org.jetbrains.annotations.NotNull
    java.lang.String sessionId) {
        return false;
    }
    
    /**
     * Get session statistics
     */
    @org.jetbrains.annotations.Nullable
    public final com.topdon.gsr.service.SessionManager.SessionStats getSessionStats(@org.jetbrains.annotations.NotNull
    java.lang.String sessionId) {
        return null;
    }
    
    /**
     * Report session error
     */
    public final void reportSessionError(@org.jetbrains.annotations.NotNull
    java.lang.String sessionId, @org.jetbrains.annotations.NotNull
    java.lang.String error) {
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0018\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH&J\u0010\u0010\u000b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\f"}, d2 = {"Lcom/topdon/gsr/service/SessionManager$SessionListener;", "", "onSessionCompleted", "", "session", "Lcom/topdon/gsr/model/SessionInfo;", "onSessionCreated", "onSessionError", "sessionId", "", "error", "onSessionUpdated", "gsr-recording_release"})
    public static abstract interface SessionListener {
        
        public abstract void onSessionCreated(@org.jetbrains.annotations.NotNull
        com.topdon.gsr.model.SessionInfo session);
        
        public abstract void onSessionUpdated(@org.jetbrains.annotations.NotNull
        com.topdon.gsr.model.SessionInfo session);
        
        public abstract void onSessionCompleted(@org.jetbrains.annotations.NotNull
        com.topdon.gsr.model.SessionInfo session);
        
        public abstract void onSessionError(@org.jetbrains.annotations.NotNull
        java.lang.String sessionId, @org.jetbrains.annotations.NotNull
        java.lang.String error);
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0014\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\bH\u00c6\u0003J\t\u0010\u0018\u001a\u00020\nH\u00c6\u0003J;\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\n2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\bH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006\u001e"}, d2 = {"Lcom/topdon/gsr/service/SessionManager$SessionStats;", "", "sessionId", "", "duration", "", "sampleCount", "syncMarkCount", "", "isActive", "", "(Ljava/lang/String;JJIZ)V", "getDuration", "()J", "()Z", "getSampleCount", "getSessionId", "()Ljava/lang/String;", "getSyncMarkCount", "()I", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "gsr-recording_release"})
    public static final class SessionStats {
        @org.jetbrains.annotations.NotNull
        private final java.lang.String sessionId = null;
        private final long duration = 0L;
        private final long sampleCount = 0L;
        private final int syncMarkCount = 0;
        private final boolean isActive = false;
        
        @org.jetbrains.annotations.NotNull
        public final com.topdon.gsr.service.SessionManager.SessionStats copy(@org.jetbrains.annotations.NotNull
        java.lang.String sessionId, long duration, long sampleCount, int syncMarkCount, boolean isActive) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull
        @java.lang.Override
        public java.lang.String toString() {
            return null;
        }
        
        public SessionStats(@org.jetbrains.annotations.NotNull
        java.lang.String sessionId, long duration, long sampleCount, int syncMarkCount, boolean isActive) {
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
        
        public final long getDuration() {
            return 0L;
        }
        
        public final long component3() {
            return 0L;
        }
        
        public final long getSampleCount() {
            return 0L;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final int getSyncMarkCount() {
            return 0;
        }
        
        public final boolean component5() {
            return false;
        }
        
        public final boolean isActive() {
            return false;
        }
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tR\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/topdon/gsr/service/SessionManager$Companion;", "", "()V", "INSTANCE", "Lcom/topdon/gsr/service/SessionManager;", "TAG", "", "getInstance", "context", "Landroid/content/Context;", "gsr-recording_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.topdon.gsr.service.SessionManager getInstance(@org.jetbrains.annotations.NotNull
        android.content.Context context) {
            return null;
        }
    }
}