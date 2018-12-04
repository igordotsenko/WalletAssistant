package com.kindhomeless.wa.walletassistant;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        application = TestApp.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class RoboTest {

    // This one just to make sure that Roboelectric test framework is configured as expected

    @Test
    public void runtimeApplicationShouldBeTestApp() {
        String actualName = RuntimeEnvironment.application.getClass().getName();
        String expectedName = TestApp.class.getName();
        assert(actualName).equals(expectedName);
    }
}
