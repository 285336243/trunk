/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiujie8.choice.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import com.github.kevinsawicki.wishlist.ViewFinder;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.jiujie8.choice.util.AppManager;
import com.jiujie8.choice.util.IConstant;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;

import static com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS;

/**
 * Activity that display dialogs
 */
public abstract class DialogFragmentActivity<E> extends
        RoboSherlockFragmentActivity implements DialogResultListener {

    /**
     * Finder bound to this activity's view
     */
    protected ViewFinder finder;

    protected CacheRepository repository;

    protected int requestCount = 20;

    protected int requestPage = 1;

    @Inject
    public Activity activity;

    @TargetApi(9)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (IConstant.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .penaltyFlashScreen()
//                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        requestWindowFeature(FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setSupportProgressBarIndeterminateVisibility(false);
        finder = new ViewFinder(this);
        AppManager.getAppManager().addActivity(this);

        repository = CacheRepository.getInstance().fromContext(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        XGPushClickedResult clicked = XGPushManager.onActivityStarted(this);
        if (clicked != null) {
            //来自信鸽的打开方式
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        XGPushManager.onActivityStoped(this);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return serializable
     */
    @SuppressWarnings("unchecked")
    protected <V extends Serializable> V getSerializableExtra(final String name) {
        return (V) getIntent().getSerializableExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return int
     */
    protected int getIntExtra(final String name) {
        return getIntent().getIntExtra(name, -1);
    }

    /**
     * Get intent extra
     * @param name
     * @return
     */
    protected long getLongExtra(final String name) {
        return getIntent().getLongExtra(name, -1);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return int array
     */
    protected int[] getIntArrayExtra(final String name) {
        return getIntent().getIntArrayExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return boolean array
     */
    protected boolean[] getBooleanArrayExtra(final String name) {
        return getIntent().getBooleanArrayExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return string
     */
    protected String getStringExtra(final String name) {
        return getIntent().getStringExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return string array
     */
    protected String[] getStringArrayExtra(final String name) {
        return getIntent().getStringArrayExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return char sequence array
     */
    protected CharSequence[] getCharSequenceArrayExtra(final String name) {
        return getIntent().getCharSequenceArrayExtra(name);
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
        // Intentionally left blank
    }

    /**
     * Set view is gone
     * @param view
     * @return
     */
    public DialogFragmentActivity hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    /**
     * Set view is visible
     * @param view
     * @return
     */
    public DialogFragmentActivity show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }
}
