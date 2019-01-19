package com.jeremyliao.atg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jeremyliao.atg.annotation.AtgTest;
import com.jeremyliao.atg.annotation.TestCase;
import com.jeremyliao.atg.type.TestType;

@AtgTest(TestType.AndroidJUnit4)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @TestCase(expect = "5")
    public void doSomething() {

    }
}
