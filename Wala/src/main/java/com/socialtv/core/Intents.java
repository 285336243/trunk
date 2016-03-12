package com.socialtv.core;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

/**
 * Created by wlanjie on 14-3-4.
 */
public class Intents {

    /**
     * Prefix for all intents created
     */
    public static final String INTENT_PREFIX = "com.mzs.guaji.";

    /**
     * Prefix for all extra data added to intents
     */
    public static final String INTENT_EXTRA_PREFIX = INTENT_PREFIX + "extra.";

    public static final String INTENT_EXTRA_PRIVATE_LETTER = INTENT_EXTRA_PREFIX + "PRIVATELETTER";

    public static final String INTENT_EXTRA_PERSON_NICKNAME = INTENT_EXTRA_PREFIX + "PENSON_NICKNAME";

    public static final String INTENT_EXTRA_PERSON_USERID = INTENT_EXTRA_PREFIX + "PENSON_USERID";

    private final Intent intent;

    /**
     * Create builder with suffix
     *
     * @param actionSuffix
     */
    public Intents(String actionSuffix) {
        // actionSuffix = e.g. "repos.VIEW"
        intent = new Intent(INTENT_PREFIX + actionSuffix);
    }

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
