package feipai.qiangdan.core;

import android.content.Context;

public class CacheRepository {

    private static final CacheRepository instance = new CacheRepository();

    private Context context;

    private CacheRepository() {

    }

    public static CacheRepository getInstance() {
        return instance;
    }

    public CacheRepository fromContext(Context context) {
        this.context = context;
        return instance;
    }

    public CacheRepository putString(String cacheName, String key, String value) {
        if (context == null) {
            return instance;
        }
        context.getSharedPreferences(cacheName, Context.MODE_PRIVATE).edit().putString(key, value).commit();

        return instance;
    }

    public CacheRepository putLong(String cacheName, String key, Long value) {
        if (context == null) {
            return instance;
        }
        context.getSharedPreferences(cacheName, Context.MODE_PRIVATE).edit().putLong(key, value).commit();
        return instance;
    }

    public CacheRepository putBoolean(String cacheName, String key, boolean value) {
        if (context == null) {
            return instance;
        }
        context.getSharedPreferences(cacheName, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
        return instance;
    }

    public boolean getBoolean(String cacheName, String key) {
        if (context == null) {
            return false;
        }
        return context.getSharedPreferences(cacheName, Context.MODE_PRIVATE).getBoolean(key, true);
    }

    public long getLong(String cacheName, String key) {
        if (context == null) {
            return -1;
        }
        return context.getSharedPreferences(cacheName, Context.MODE_PRIVATE).getLong(key, -1);
    }

    public String getString(String cacheName, String key) {
        if (context == null) {
            return null;
        }

        return context.getSharedPreferences(cacheName, Context.MODE_PRIVATE).getString(key, null);
    }

    public CacheRepository removeString(String cacheName, String key) {
        if (context == null) {
            return instance;
        }

        context.getSharedPreferences(cacheName, Context.MODE_PRIVATE).edit().remove(key).commit();
        return instance;
    }

    public void clear(String cacheName) {
        context.getSharedPreferences(cacheName, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public boolean isBind(String cacheName, String key) {
        String value = context.getSharedPreferences(cacheName, context.MODE_PRIVATE).getString(key, "");
        if (value == null || "".equals(value)) {
            return false;
        } else {
            return true;
        }
    }
}
