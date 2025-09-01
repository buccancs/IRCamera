package com.topdon.gsr.service;

import java.lang.System;

/**
 * Multi-modal recording service that coordinates GSR and thermal recording
 */
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\f\u0018\u0000 $2\u00020\u0001:\u0001$B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0004H\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0002J\u0014\u0010\u0012\u001a\u0004\u0018\u00010\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0016J\b\u0010\u0016\u001a\u00020\u0011H\u0016J\b\u0010\u0017\u001a\u00020\u0011H\u0016J\"\u0010\u0018\u001a\u00020\u00192\b\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u0019H\u0016J$\u0010\u001c\u001a\u00020\u00112\u0006\u0010\u001d\u001a\u00020\u00042\b\u0010\u001e\u001a\u0004\u0018\u00010\u00042\b\u0010\u001f\u001a\u0004\u0018\u00010\u0004H\u0002J\b\u0010 \u001a\u00020\u0011H\u0002J\u0010\u0010!\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u0004H\u0002J\u0010\u0010#\u001a\u00020\u00112\u0006\u0010\u000f\u001a\u00020\u0004H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/topdon/gsr/service/MultiModalRecordingService;", "Landroid/app/Service;", "()V", "currentSessionId", "", "gsrListener", "Lcom/topdon/gsr/service/GSRRecorder$GSRRecordingListener;", "gsrRecorder", "Lcom/topdon/gsr/service/GSRRecorder;", "isRecording", "", "sessionManager", "Lcom/topdon/gsr/service/SessionManager;", "createNotification", "Landroid/app/Notification;", "content", "createNotificationChannel", "", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onCreate", "onDestroy", "onStartCommand", "", "flags", "startId", "startRecording", "sessionId", "participantId", "studyName", "stopRecording", "triggerSyncEvent", "eventType", "updateNotification", "Companion", "gsr-recording_debug"})
public final class MultiModalRecordingService extends android.app.Service {
    @org.jetbrains.annotations.NotNull
    public static final com.topdon.gsr.service.MultiModalRecordingService.Companion Companion = null;
    private static final java.lang.String TAG = "MultiModalService";
    private static final int NOTIFICATION_ID = 12345;
    private static final java.lang.String CHANNEL_ID = "gsr_recording_channel";
    private static final java.lang.String ACTION_START_RECORDING = "action_start_recording";
    private static final java.lang.String ACTION_STOP_RECORDING = "action_stop_recording";
    private static final java.lang.String ACTION_SYNC_EVENT = "action_sync_event";
    private static final java.lang.String EXTRA_SESSION_ID = "extra_session_id";
    private static final java.lang.String EXTRA_PARTICIPANT_ID = "extra_participant_id";
    private static final java.lang.String EXTRA_STUDY_NAME = "extra_study_name";
    private static final java.lang.String EXTRA_EVENT_TYPE = "extra_event_type";
    private com.topdon.gsr.service.GSRRecorder gsrRecorder;
    private com.topdon.gsr.service.SessionManager sessionManager;
    private boolean isRecording = false;
    private java.lang.String currentSessionId;
    private final com.topdon.gsr.service.GSRRecorder.GSRRecordingListener gsrListener = null;
    
    public MultiModalRecordingService() {
        super();
    }
    
    @java.lang.Override
    public void onCreate() {
    }
    
    @java.lang.Override
    public int onStartCommand(@org.jetbrains.annotations.Nullable
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable
    @java.lang.Override
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable
    android.content.Intent intent) {
        return null;
    }
    
    private final void startRecording(java.lang.String sessionId, java.lang.String participantId, java.lang.String studyName) {
    }
    
    private final void stopRecording() {
    }
    
    private final void triggerSyncEvent(java.lang.String eventType) {
    }
    
    private final void createNotificationChannel() {
    }
    
    private final android.app.Notification createNotification(java.lang.String content) {
        return null;
    }
    
    private final void updateNotification(java.lang.String content) {
    }
    
    @java.lang.Override
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J.\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00042\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0004J\u000e\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u0016\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/topdon/gsr/service/MultiModalRecordingService$Companion;", "", "()V", "ACTION_START_RECORDING", "", "ACTION_STOP_RECORDING", "ACTION_SYNC_EVENT", "CHANNEL_ID", "EXTRA_EVENT_TYPE", "EXTRA_PARTICIPANT_ID", "EXTRA_SESSION_ID", "EXTRA_STUDY_NAME", "NOTIFICATION_ID", "", "TAG", "startRecording", "", "context", "Landroid/content/Context;", "sessionId", "participantId", "studyName", "stopRecording", "triggerSyncEvent", "eventType", "gsr-recording_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final void startRecording(@org.jetbrains.annotations.NotNull
        android.content.Context context, @org.jetbrains.annotations.NotNull
        java.lang.String sessionId, @org.jetbrains.annotations.Nullable
        java.lang.String participantId, @org.jetbrains.annotations.Nullable
        java.lang.String studyName) {
        }
        
        public final void stopRecording(@org.jetbrains.annotations.NotNull
        android.content.Context context) {
        }
        
        public final void triggerSyncEvent(@org.jetbrains.annotations.NotNull
        android.content.Context context, @org.jetbrains.annotations.NotNull
        java.lang.String eventType) {
        }
    }
}