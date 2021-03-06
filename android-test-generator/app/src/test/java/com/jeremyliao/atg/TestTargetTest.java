package com.jeremyliao.atg;

import java.lang.Exception;
import java.lang.String;
import org.junit.Assert;
import org.junit.Test;

public class TestTargetTest {
  @Test
  public void add() throws Exception {
    TestTarget target = new TestTarget();
    int result = target.add(3,2);
    Assert.assertEquals(result,5);
  }

  @Test
  public void mergeIntString() throws Exception {
    TestTarget target = new TestTarget();
    String result = target.mergeIntString(3,"ret");
    Assert.assertEquals(result,"ret: 3");
  }

  @Test
  public void setValueOne() throws Exception {
    TestTarget target = new TestTarget();
    target.setValueOne();
    Assert.assertEquals(target.getValue(),1);
  }
}
