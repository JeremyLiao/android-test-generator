package com.jeremyliao.bddespresso.steps;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;


import com.jeremyliao.bddespresso.common.PageManager;
import com.jeremyliao.bddespresso.common.ViewIdManager;

import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.StringContains;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class CommonSteps {

    @When("^点击(.+)$")
    public void clickView(String name) {
        if (name == null || name.length() == 0) {
            return;
        }
        if (name.startsWith(".")) {
            int id = ViewIdManager.getManager().getId(name.substring(1));
            if (id > 0) {
                onView(withId(id)).perform(click());
            }
        } else {
            onView(withText(name)).perform(click());
        }
    }

    @When("^滚动到显示(.+)$")
    public void scrollUntilVisible(String name) {
        if (name == null || name.length() == 0) {
            return;
        }
        if (name.startsWith(".")) {
            int id = ViewIdManager.getManager().getId(name.substring(1));
            if (id > 0) {
                onView(withId(id)).perform(scrollTo());
            }
        } else {
            onView(withText(name)).perform(scrollTo());
        }
    }

    @When("^点击列表中的条目(.+)$")
    public void clickListItem(String name) {
        if (name == null || name.length() == 0) {
            return;
        }
        onData(AllOf.allOf(Is.is(IsInstanceOf.instanceOf(String.class)), Is.is(name))).perform(click());

    }

    @Then("^等待(.+)$")
    public void wait(String text) {
        try {
            if (text.endsWith("ms")) {
                String timeStr = text.substring(0, text.indexOf("ms"));
                long time = Long.parseLong(timeStr);
                Thread.sleep(time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("^显示(.+)$")
    public void showText(String text) {
        onView(withText(text)).check(matches(isDisplayed()));
    }

    @Given("^进入(.+)$")
    public void enterPage(String name) {
        ActivityTestRule<?> rule = PageManager.getManager().getActivityTestRule(name);
        if (rule == null) {
            return;
        }
        rule.launchActivity(new Intent());
    }

    @Then("^退出(.+)$")
    public void quitPage(String name) {
        ActivityTestRule<?> rule = PageManager.getManager().getActivityTestRule(name);
        if (rule == null) {
            return;
        }
        rule.finishActivity();
    }

    @When("^在(.+)中输入(.+)$")
    public void inputText(String name, String text) {
        int id = ViewIdManager.getManager().getId(name.substring(1));
        if (id > 0) {
            onView(withId(id)).perform(typeText(text));
        }
    }

    @Then("^控件(.+)显示文字(.+)$")
    public void showText(String name, String text) {
        int id = ViewIdManager.getManager().getId(name.substring(1));
        if (id > 0) {
            onView(withId(id)).check(matches(withText(text)));
        }
    }

    @Then("^控件(.+)含有文字(.+)$")
    public void containsText(String name, String text) {
        int id = ViewIdManager.getManager().getId(name.substring(1));
        if (id > 0) {
            onView(withId(id)).check(matches(withText(StringContains.containsString(text))));
        }
    }
}