package com.example.submarines.helpers;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache {
    private static final BitmapCache instance = new BitmapCache();
    private final LruCache<String, Bitmap> bitmapLruCache = new LruCache<>(100); // שומר ביאטמפים לפי השם שלהם והביטמאפ עצמו
    public static BitmapCache getInstance() { return instance; }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) { // אם לא קיים ביטמאפ כזה כבר אז תוסיף
            bitmapLruCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) { // מחזיר את השם של הביטמאפ
        return bitmapLruCache.get(key);
    }
}
