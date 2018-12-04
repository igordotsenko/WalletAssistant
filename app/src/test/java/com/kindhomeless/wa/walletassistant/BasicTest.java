package com.kindhomeless.wa.walletassistant;

import android.os.Build;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        application = TestApp.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class BasicTest {

    @After
    public void tearDown() {
        Cache.openDatabase().close();
        ActiveAndroid.clearCache();
    }

    @Test
    public void empty() {

    }
}
