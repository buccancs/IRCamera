package com.topdon.module.user.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.energy.commoncomponent.utils.NetworkUtil;
import com.topdon.lib.core.ui.TToast;
import com.topdon.module.user.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for ActivityUtil utility class
 */
@RunWith(AndroidJUnit4.class)
public class ActivityUtilTest {

    @Mock
    private Context mockContext;
    
    @Mock 
    private TToast mockTToast;

    private Context realContext;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        realContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void goSystemCustomer_withConnectedNetwork_startsActivity() {
        try (MockedStatic<NetworkUtil> mockedNetworkUtil = mockStatic(NetworkUtil.class);
             MockedStatic<TToast> mockedTToast = mockStatic(TToast.class)) {
            
            // Arrange
            mockedNetworkUtil.when(() -> NetworkUtil.isConnected(mockContext)).thenReturn(true);
            mockedTToast.when(() -> TToast.INSTANCE).thenReturn(mockTToast);
            
            // Act
            ActivityUtil.goSystemCustomer(mockContext);
            
            // Assert
            verify(mockContext).startActivity(any(Intent.class));
        }
    }

    @Test
    public void goSystemCustomer_withDisconnectedNetwork_showsToast() {
        try (MockedStatic<NetworkUtil> mockedNetworkUtil = mockStatic(NetworkUtil.class);
             MockedStatic<TToast> mockedTToast = mockStatic(TToast.class)) {
            
            // Arrange
            mockedNetworkUtil.when(() -> NetworkUtil.isConnected(mockContext)).thenReturn(false);
            mockedTToast.when(() -> TToast.INSTANCE).thenReturn(mockTToast);
            
            // Act
            ActivityUtil.goSystemCustomer(mockContext);
            
            // Assert
            verify(mockTToast).shortToast(eq(mockContext), eq(R.string.lms_setting_http_error));
            verify(mockContext, never()).startActivity(any(Intent.class));
        }
    }

    @Test
    public void goSystemBrowser_withValidUrl_startsActivityWithCorrectIntent() {
        try (MockedStatic<NetworkUtil> mockedNetworkUtil = mockStatic(NetworkUtil.class);
             MockedStatic<TToast> mockedTToast = mockStatic(TToast.class)) {
            
            // Arrange
            String testUrl = "https://example.com";
            mockedNetworkUtil.when(() -> NetworkUtil.isConnected(mockContext)).thenReturn(true);
            mockedTToast.when(() -> TToast.INSTANCE).thenReturn(mockTToast);
            
            // Act
            ActivityUtil.goSystemBrowser(mockContext, testUrl);
            
            // Assert
            verify(mockContext).startActivity(argThat(intent -> {
                return Intent.ACTION_VIEW.equals(intent.getAction()) &&
                       Uri.parse(testUrl).equals(intent.getData());
            }));
        }
    }

    @Test
    public void goSystemBrowser_withNoConnection_showsToastAndDoesNotStartActivity() {
        try (MockedStatic<NetworkUtil> mockedNetworkUtil = mockStatic(NetworkUtil.class);
             MockedStatic<TToast> mockedTToast = mockStatic(TToast.class)) {
            
            // Arrange
            String testUrl = "https://example.com";
            mockedNetworkUtil.when(() -> NetworkUtil.isConnected(mockContext)).thenReturn(false);
            mockedTToast.when(() -> TToast.INSTANCE).thenReturn(mockTToast);
            
            // Act
            ActivityUtil.goSystemBrowser(mockContext, testUrl);
            
            // Assert
            verify(mockTToast).shortToast(eq(mockContext), eq(R.string.lms_setting_http_error));
            verify(mockContext, never()).startActivity(any(Intent.class));
        }
    }

    @Test
    public void goSystemBrowser_withException_handlesGracefully() {
        try (MockedStatic<NetworkUtil> mockedNetworkUtil = mockStatic(NetworkUtil.class);
             MockedStatic<TToast> mockedTToast = mockStatic(TToast.class)) {
            
            // Arrange
            String testUrl = "https://example.com";
            mockedNetworkUtil.when(() -> NetworkUtil.isConnected(mockContext)).thenReturn(true);
            mockedTToast.when(() -> TToast.INSTANCE).thenReturn(mockTToast);
            
            // Mock startActivity to throw exception
            doThrow(new RuntimeException("Test exception")).when(mockContext).startActivity(any(Intent.class));
            
            // Act - should not throw exception
            ActivityUtil.goSystemBrowser(mockContext, testUrl);
            
            // Assert - exception was handled gracefully
            verify(mockContext).startActivity(any(Intent.class));
        }
    }

    @Test
    public void goSystemCustomer_usesCorrectUrl() {
        try (MockedStatic<NetworkUtil> mockedNetworkUtil = mockStatic(NetworkUtil.class);
             MockedStatic<TToast> mockedTToast = mockStatic(TToast.class)) {
            
            // Arrange
            mockedNetworkUtil.when(() -> NetworkUtil.isConnected(mockContext)).thenReturn(true);
            mockedTToast.when(() -> TToast.INSTANCE).thenReturn(mockTToast);
            
            // Act
            ActivityUtil.goSystemCustomer(mockContext);
            
            // Assert - verify the correct URL is used
            verify(mockContext).startActivity(argThat(intent -> {
                return Intent.ACTION_VIEW.equals(intent.getAction()) &&
                       Uri.parse("https://www.topdon.cc/tc-chat").equals(intent.getData());
            }));
        }
    }

    @Test
    public void goSystemBrowser_withNullUrl_handlesGracefully() {
        try (MockedStatic<NetworkUtil> mockedNetworkUtil = mockStatic(NetworkUtil.class);
             MockedStatic<TToast> mockedTToast = mockStatic(TToast.class)) {
            
            // Arrange
            mockedNetworkUtil.when(() -> NetworkUtil.isConnected(mockContext)).thenReturn(true);
            mockedTToast.when(() -> TToast.INSTANCE).thenReturn(mockTToast);
            
            // Act - should not throw exception
            ActivityUtil.goSystemBrowser(mockContext, null);
            
            // If no exception thrown, test passes
        }
    }

    @Test
    public void goSystemBrowser_withEmptyUrl_handlesGracefully() {
        try (MockedStatic<NetworkUtil> mockedNetworkUtil = mockStatic(NetworkUtil.class);
             MockedStatic<TToast> mockedTToast = mockStatic(TToast.class)) {
            
            // Arrange
            mockedNetworkUtil.when(() -> NetworkUtil.isConnected(mockContext)).thenReturn(true);
            mockedTToast.when(() -> TToast.INSTANCE).thenReturn(mockTToast);
            
            // Act - should not throw exception
            ActivityUtil.goSystemBrowser(mockContext, "");
            
            // If no exception thrown, test passes
        }
    }

    @Test
    public void goSystemBrowser_withInvalidUrl_handlesGracefully() {
        try (MockedStatic<NetworkUtil> mockedNetworkUtil = mockStatic(NetworkUtil.class);
             MockedStatic<TToast> mockedTToast = mockStatic(TToast.class)) {
            
            // Arrange
            mockedNetworkUtil.when(() -> NetworkUtil.isConnected(mockContext)).thenReturn(true);
            mockedTToast.when(() -> TToast.INSTANCE).thenReturn(mockTToast);
            
            // Act - should not throw exception
            ActivityUtil.goSystemBrowser(mockContext, "invalid-url");
            
            // If no exception thrown, test passes
        }
    }
}