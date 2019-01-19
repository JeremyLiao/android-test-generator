package com.jeremyliao.bddespresso.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaohailiang on 2019/1/14.
 */
public class PageManager {

    private static final String PAGES_ASSET_PATH = "bdd/pages";

    private static class SingletonHolder {
        private static final PageManager PAGE_MANAGER = new PageManager();
    }

    public static PageManager getManager() {
        return SingletonHolder.PAGE_MANAGER;
    }

    private final Context context;
    private final Map<String, String> pageNameMap = new HashMap<>();
    private final Map<String, ActivityTestRule> pageRuleMap = new HashMap<>();

    private PageManager() {
        context = InstrumentationRegistry.getContext();
        AssetManager asset = context.getAssets();
        try {
            String[] files = asset.list(PAGES_ASSET_PATH);
            if (files != null && files.length > 0) {
                for (String file : files) {
                    resolveConfigFile(PAGES_ASSET_PATH + "/" + file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resolveConfigFile(String file) {
        AssetManager asset = context.getAssets();
        try {
            InputStream inputStream = asset.open(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] items = line.split(":");
                if (items != null && items.length > 1) {
                    pageNameMap.put(items[0], items[1]);
                }
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Class<?> getPageClass(String name) throws ClassNotFoundException {
        if (name == null || name.length() == 0) {
            return null;
        }
        if (!pageNameMap.containsKey(name)) {
            return null;
        }
        return Class.forName(pageNameMap.get(name));
    }

    public ActivityTestRule<?> getActivityTestRule(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        if (pageRuleMap.containsKey(name)) {
            return pageRuleMap.get(name);
        }
        Class<?> type = null;
        try {
            type = getPageClass(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (type == null) {
            return null;
        }
        ActivityTestRule rule = new ActivityTestRule(type);
        pageRuleMap.put(name, rule);
        return rule;
    }

}
