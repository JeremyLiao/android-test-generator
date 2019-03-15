package com.jeremyliao.atg;

import java.lang.Exception;
import java.lang.String;
import org.junit.Assert;
import org.junit.Test;

public class DateUtilTest {
  @Test
  public void dateToStamp() throws Exception {
    long result = DateUtil.dateToStamp("2017-10-15 16:00:02");
    Assert.assertEquals(result,1508054402000L);
  }

  @Test
  public void stampToDate() throws Exception {
    String result = DateUtil.stampToDate(1508054402000L);
    Assert.assertEquals(result,"2017-10-15 16:00:02");
  }
}
