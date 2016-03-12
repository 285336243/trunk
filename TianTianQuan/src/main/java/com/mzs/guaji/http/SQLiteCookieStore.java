package com.mzs.guaji.http;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SQLiteCookieStore extends SQLiteOpenHelper implements CookieStore {

    private final static Integer DATABASE_VERSION = 2;
    private final static String DATABASE_NAME = "cookie.db";

    private final static String TABLE_NAME = "cache_cookie";

    private List<Cookie> cookies = Collections.synchronizedList(new ArrayList<Cookie>());


    private final static Object lock = new Object();

    public SQLiteCookieStore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "
                + TABLE_NAME
                + "(comment varchar(100), commentURL varchar(100), domain varchar(100), expiredDate integer, name varchar(100), path varchar(500), ports varchar(100), "
                + "value varchar(200), version biginteger);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME + ";");
        onCreate(db);
    }

    public void addCookie(Cookie cookie) {
        try {
            if (cookie.getExpiryDate() != null && cookie.getExpiryDate().before(new Date())) {
                clear(cookie.getDomain(), cookie.getName());
                return;
            }


            ContentValues values = new ContentValues();
            String comment = cookie.getComment();
            if (comment != null) {
                values.put("comment", comment);
            }
            String commentURL = cookie.getCommentURL();
            if (commentURL != null) {
                values.put("commentURL", commentURL);
            }
            String domain = cookie.getDomain();
            if (domain != null) {
                values.put("domain", domain);
            }
            Date expiryDate = cookie.getExpiryDate();
            if (expiryDate != null) {
                values.put("expiredDate", expiryDate.getTime());
            }
            String name = cookie.getName();
            if (name != null) {
                values.put("name", name);
            }
            String path = cookie.getPath();
            if (path != null) {
                values.put("path", path);
            }
            int[] ports = cookie.getPorts();
            if (ports != null) {
                String port = int2str(ports);
                values.put("ports", port);
            }
            String value = cookie.getValue();
            if (value != null) {
                values.put("value", value);
            }
            int version = cookie.getVersion();
            values.put("version", version);
            SQLiteDatabase database = getWritableDatabase();

            database.delete(TABLE_NAME,"domain = ? AND name =? AND path = ?" , new String[]{domain, name, path});
            database.insert(TABLE_NAME, null, values);
            database.close();

            cookies.clear();
        } finally {

            Log.d(SQLiteCookieStore.class.getName(), "addCookie ");
        }

    }

    public List<Cookie> getCookies() {
        Cursor cur = null;
        try {

            if (!cookies.isEmpty()) {
                return cookies;
            }

            cur = getWritableDatabase().query(TABLE_NAME,
                    new String[]{"comment", "commentURL", "domain", "expiredDate", "name", "path", "ports", "value", "version"}, null, null, "domain,path,name", null, "expiredDate desc");
            while (cur.moveToNext()) {
                String name = cur.getString(cur.getColumnIndex("name"));
                String value = cur.getString(cur.getColumnIndex("value"));
                String port = cur.getString(cur.getColumnIndex("ports"));
                BasicClientCookie cookie = null;
                if (port == null || port.length() == 0) {
                    cookie = new BasicClientCookie(name, value);
                } else {
                    cookie = new BasicClientCookie2(name, value);
                    ((BasicClientCookie2) cookie).setPorts(str2int(port));
                }
                cookie.setComment(cur.getString(cur.getColumnIndex("comment")));
                cookie.setDomain(cur.getString(cur.getColumnIndex("domain")));
                Long expiredDate = cur.getLong(cur.getColumnIndex("expiredDate"));
                if (expiredDate != null) {
                    Date expiryDate = new Date(expiredDate);
                    cookie.setExpiryDate(expiryDate);
                }
                cookie.setPath(cur.getString(cur.getColumnIndex("path")));
                cookie.setVersion(cur.getInt(cur.getColumnIndex("version")));
                cookies.add(cookie);
            }
            return cookies;
        } finally {
            if (cur != null) {
                cur.close();
            }
            cur = null;
            Log.d(SQLiteCookieStore.class.getName(), "load cookies ");
        }

    }

    private int[] str2int(String port) {
        if (port == null || port.length() == 0)
            return null;
        String[] ports = port.split(",");
        int[] rPort = new int[ports.length];

        for (int i = 0; i < ports.length; i++) {
            rPort[i] = Integer.valueOf(ports[i]);
        }

        return rPort;
    }

    private String int2str(int[] ports) {
        if (ports == null)
            return null;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ports.length; i++) {
            sb.append(ports[i]).append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public void clear() {
        getWritableDatabase().delete(TABLE_NAME, null, null);
        cookies.clear();
    }

    @Override
    public boolean clearExpired(Date date) {
        return false;
    }

    public void clear(String domain, String name) {
        getWritableDatabase().delete(TABLE_NAME, "domain=? and name=?", new String[]{domain, name});
        cookies.clear();
    }

}
