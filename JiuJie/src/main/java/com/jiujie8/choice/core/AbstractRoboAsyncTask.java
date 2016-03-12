package com.jiujie8.choice.core;

import android.content.Context;

import com.jiujie8.choice.Response;
import com.jiujie8.choice.util.IConstant;

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

    @Override
    protected void onSuccess(ResultT resultT) throws Exception {
        super.onSuccess(resultT);
        if (resultT instanceof Response) {
//            System.out.println("response = " + ((Response) resultT).getCode());
            Response mResponse = (Response) resultT;
            if (IConstant.STATE_OK.equals(mResponse.getCode())) {
                onSuccessCallback(resultT);
            }
        }
    }

    protected abstract void onSuccessCallback(ResultT resultT);
}
