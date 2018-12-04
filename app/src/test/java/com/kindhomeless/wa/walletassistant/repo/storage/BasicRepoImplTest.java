package com.kindhomeless.wa.walletassistant.repo.storage;

import com.activeandroid.ActiveAndroid;
import com.kindhomeless.wa.walletassistant.BasicTest;

import org.junit.Before;
import org.junit.Test;

public class BasicRepoImplTest extends BasicTest {

    @Before
    public void setUp() {
        ActiveAndroid.beginTransaction();
    }

    @Test
    public void empty() {

    }
}
