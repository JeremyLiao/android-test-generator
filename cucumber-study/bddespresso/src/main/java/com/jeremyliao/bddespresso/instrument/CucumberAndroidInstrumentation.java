package com.jeremyliao.bddespresso.instrument;

import android.os.Bundle;
import android.support.test.runner.MonitoringInstrumentation;

import cucumber.api.android.CucumberInstrumentationCore;

/**
 * Created by liaohailiang on 2019/1/16.
 */

public class CucumberAndroidInstrumentation extends MonitoringInstrumentation {

    private CucumberInstrumentationCore cucumberInstrumentationCore = new CucumberInstrumentationCore(this);

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        cucumberInstrumentationCore.create(bundle);
        start();
    }

    @Override
    public void onStart() {
        cucumberInstrumentationCore.start();
    }
}
