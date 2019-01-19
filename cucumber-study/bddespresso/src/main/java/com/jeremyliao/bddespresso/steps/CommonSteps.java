package com.jeremyliao.bddespresso.steps;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;


import com.jeremyliao.bddespresso.common.PageManager;
import com.jeremyliao.bddespresso.common.ViewIdManager;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class CommonSteps {

    @When("^点击(.+)$")
    public void onPressButton(String name) {
        onView(withText(name)).perform(click());
    }

    @Then("^显示(.+)$")
    public void onShow(String text) {
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
    public void input(String name, String text) {
        int id = ViewIdManager.getManager().getId(name.substring(1));
        onView(withId(id)).perform(typeText(text));
    }
}