package com.jeremyliao.bddespresso.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaohailiang on 2019/1/14.
 */
public class ViewIdManager {

    private static final String ASSET_PATH = "bdd/ids";

    private static class SingletonHolder {
        private static final ViewIdManager MANAGER = new ViewIdManager();
    }

    public static ViewIdManager getManager() {
        return SingletonHolder.MANAGER;
    }

    private final Map<String, String> viewIdMap = new HashMap<>();
    private Class<?> rIdType;
    private Context context;

    private ViewIdManager() {
        getViewIdClass();
        getViewIds();
    }

    private void getViewIds() {
        context = InstrumentationRegistry.getContext();
        AssetManager asset = context.getAssets();
        try {
            String[] files = asset.list(ASSET_PATH);
            if (files != null && files.length > 0) {
                for (String file : files) {
                    resolveConfigFile(ASSET_PATH + "/" + file);
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
                    viewIdMap.put(items[0], items[1]);
                }
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getViewIdClass() {
        String packageName = InstrumentationRegistry.getContext().getPackageName();
        if (packageName.endsWith(".test")) {
            packageName = packageName.substring(0, packageName.length() - 5);
        }
        try {
            rIdType = Class.forName(packageName + "." + "R$id");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getId(String name) {
        try {
            return getIdInner(name);
        } catch (Exception e) {
            if (!viewIdMap.containsKey(name)) {
                return -1;
            }
            try {
                return getIdInner(viewIdMap.get(name));
            } catch (Exception e1) {
                return -1;
            }
        }
    }

    private int getIdInner(String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = rIdType.getDeclaredField(name);
        return field.getInt(rIdType);
    }
}
