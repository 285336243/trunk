package feipai.qiangdan.core;

import android.content.Context;

import com.github.kevinsawicki.wishlist.AsyncLoader;

import roboguice.RoboGuice;

/**
 * Created by wlanjie on 14-2-20.
 */
public abstract class AbstractLoader<D> extends AsyncLoader<D> {

    /**
     * Create loader for context
     *
     * @param context
     */
    public AbstractLoader(final Context context) {
        super(context);
        RoboGuice.injectMembers(context, this);
    }

    /**
     * Get data to display when obtaining an account fails
     *
     * @return data
     */
    protected abstract D getFailureData();

    @Override
    public D loadInBackground() {
        try {
            return load(new Object());
        } catch (Throwable e) {
            return getFailureData();
        }
    }

    /**
     * Load data
     * @param data
     * @return
     */
    public abstract D load(Object data);
}
