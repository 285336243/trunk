package com.jiujie8.choice.core;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

/**
 * Created by wlanjie on 14-3-4.
 */
public class Intents {

    private final Intent intent;

    public Intents(Context context, Class<?> clazz) {
        intent = new Intent(context, clazz);
    }

    /**
     * Add extra field data value to intent being built up
     *
     * @param fieldName
     * @param value
     * @return this builder
     */
    public Intents add(String fieldName, String value) {
        intent.putExtra(fieldName, value);
        return this;
    }

    /**
     * Add extra field data values to intent being built up
     *
     * @param fieldName
     * @param values
     * @return this builder
     */
    public Intents add(String fieldName, CharSequence[] values) {
        intent.putExtra(fieldName, values);
        return this;
    }

    /**
     * Add extra field data value to intent being built up
     *
     * @param fieldName
     * @param value
     * @return this builder
     */
    public Intents add(String fieldName, int value) {
        intent.putExtra(fieldName, value);
        return this;
    }

    /**
     * Add extra field data value to intent being built up
     *
     * @param fieldName
     * @param values
     * @return this builder
     */
    public Intents add(String fieldName, int[] values) {
        intent.putExtra(fieldName, values);
        return this;
    }

    /**
     * Add extra field data value to intent being built up
     *
     * @param fieldName
     * @param values
     * @return this builder
     */
    public Intents add(String fieldName, boolean[] values) {
        intent.putExtra(fieldName, values);
        return this;
    }

    /**
     * Add extra field data value to intent being built up
     *
     * @param fieldName
     * @param value
     * @return this builder
     */
    public Intents add(String fieldName, Serializable value) {
        intent.putExtra(fieldName, value);
        return this;
    }

    /**
     * Get built intent
     *
     * @return intent
     */
    public Intent toIntent() {
        return intent;
    }
}
