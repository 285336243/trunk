package com.socialtv.core;

import android.content.Context;


/**
 * Created by wlanjie on 14-2-14.
 */
public abstract class ThrowableLoader<D> extends AbstractLoader<D> {

    private static final String TAG = "ThrowableLoader";
    private Exception exception;

    /**
     * Create loader for context and seeded with initial data
     *
     * @param context
     */
    public ThrowableLoader(Context context) {
        super(context);
    }

    @Override
    public D load(final Object object) {
        exception = null;
        try {
            return loadData();
        } catch (Exception e) {
            Log.d(TAG, "Exception loading data", e);
            exception = e;
            return null;
        }
    }

    /**
     * Get exception
     * @return exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * clear the stored exception and return it
     * @return exception
     */
    public Exception clearException() {
        final Exception throwable = exception;
        exception = null;
        return throwable;
    }

    /**
     * load data
     * @return data
     * @throws Exception
     */
    public abstract D loadData() throws Exception;
}
