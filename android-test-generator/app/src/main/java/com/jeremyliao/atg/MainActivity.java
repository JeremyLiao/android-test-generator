package com.jeremyliao.atg;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jeremyliao.atg.annotation.AtgTest;
import com.jeremyliao.atg.annotation.TestCase;
import com.jeremyliao.atg.type.TestType;

import java.util.concurrent.CountDownLatch;

@AtgTest(TestType.AndroidJUnit4)
public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testCountDownLatch();
    }

    @TestCase(expect = "5")
    public void doSomething() {

    }

    private void testCountDownLatch() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        countDownLatch.countDown();
                    }
                }, 3000);
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("test", "aaaaa");
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        countDownLatch.countDown();
//                    }
//                }, 3000);
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("test", "bbbbb");
            }
        }).start();
    }
}
