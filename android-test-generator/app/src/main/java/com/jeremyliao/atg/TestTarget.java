package com.jeremyliao.atg;

import com.jeremyliao.atg.annotation.AtgTest;
import com.jeremyliao.atg.annotation.TestCase;
import com.jeremyliao.atg.type.TestType;

/**
 * Created by liaohailiang on 2019/1/7.
 */
@AtgTest(TestType.JUnit4)
public class TestTarget {

    private int value = 0;

    public int getValue() {
        return value;
    }

    @TestCase(inputs = "3,2", checkReturn = true, expect = "5")
    public int add(int a, int b) {
        return a + b;
    }

    @TestCase(inputs = "3,ret", checkReturn = true, expect = "ret: 3")
    public String mergeIntString(int a, String b) {
        return b + ": " + a;
    }

    @TestCase(checkVariable = "value", expect = "1")
    public void setValueOne() {
        value = 1;
    }
}
