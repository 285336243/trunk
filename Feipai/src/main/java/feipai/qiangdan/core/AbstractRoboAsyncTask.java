package feipai.qiangdan.core;

import android.content.Context;

import java.util.concurrent.Executor;

import roboguice.util.RoboAsyncTask;

/**
 * Created by wlanjie on 14-2-25.
 */
public abstract class AbstractRoboAsyncTask<ResultT> extends RoboAsyncTask<ResultT> {

    public AbstractRoboAsyncTask(final Context context) {
        super(context);
    }

    public AbstractRoboAsyncTask(final Context context, final Executor executor) {
        super(context, executor);
    }

    @Override
    public ResultT call() throws Exception {
        return run(new Object());
    }

    /**
     * Execute task with an authenticated account
     *
     * @param data
     * @return result
     * @throws Exception
     */
    protected abstract ResultT run(Object data) throws Exception;
}
