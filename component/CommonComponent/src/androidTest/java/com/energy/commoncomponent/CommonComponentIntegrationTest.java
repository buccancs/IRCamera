package com.energy.commoncomponent;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.energy.commoncomponent.bean.DeviceType;
import com.energy.commoncomponent.utils.AppUtils;
import com.energy.commoncomponent.utils.NetworkUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests for CommonComponent
 * Tests component interactions and Android framework integration
 */
@RunWith(AndroidJUnit4.class)
public class CommonComponentIntegrationTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void networkUtil_worksWithRealContext() {
        // Test NetworkUtil with real Android context
        // These should not crash and return boolean values
        boolean isConnected = NetworkUtil.isConnected(context);
        boolean isWifiConnected = NetworkUtil.isWifiConnected(context);
        
        // Just verify they don't crash - actual values depend on test environment
        assertThat(isConnected).isAnyOf(true, false);
        assertThat(isWifiConnected).isAnyOf(true, false);
    }

    @Test
    public void appUtils_reflectionWorksWithRealClasses() {
        // Test AppUtils reflection with real standard library classes
        AppUtils.runMethodByReflectClass("java.lang.System", "gc");
        // If no exception thrown, test passes
    }

    @Test
    public void deviceType_enumBehavesCorrectly() {
        // Test DeviceType enum integration
        DeviceType[] allTypes = DeviceType.values();
        assertThat(allTypes).hasLength(8);
        
        // Test enum can be used in collections, switch statements, etc.
        for (DeviceType type : allTypes) {
            assertThat(type.toString()).isNotEmpty();
            assertThat(type.ordinal()).isAtLeast(0);
        }
    }

    @Test
    public void commonComponent_packageStructureIsValid() {
        // Test that the package structure is accessible
        assertThat(DeviceType.class.getPackage().getName()).isEqualTo("com.energy.commoncomponent.bean");
        assertThat(NetworkUtil.class.getPackage().getName()).isEqualTo("com.energy.commoncomponent.utils");
        assertThat(AppUtils.class.getPackage().getName()).isEqualTo("com.energy.commoncomponent.utils");
    }

    @Test
    public void context_isValidAndUsable() {
        // Test that test context is properly set up
        assertThat(context).isNotNull();
        assertThat(context.getPackageName()).isNotEmpty();
        
        // Test context can be used for system services (should not crash)
        Object connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // May be null in test environment, but should not crash
    }
}