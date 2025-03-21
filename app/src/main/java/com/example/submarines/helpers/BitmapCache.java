package com.example.submarines.helpers;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache {
    private static final BitmapCache instance = new BitmapCache();
    private final LruCache<String, Bitmap> bitmapLruCache = new LruCache<>(100);
    public static BitmapCache getInstance() { return instance; }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            bitmapLruCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return bitmapLruCache.get(key);
    }
}
