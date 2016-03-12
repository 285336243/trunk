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
package com.socialtv.core;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.ViewFinder;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.socialtv.R;
import com.socialtv.util.AppManager;
import com.socialtv.util.IConstant;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.stat.StatService;

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

    private AlertDialog scoreDialog = null;

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
        registerReceiver(receiver, new IntentFilter(IConstant.SHOW_SCORE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        unregisterReceiver(receiver);
        if (scoreDialog != null) {
            if (scoreDialog.isShowing()) {
                scoreDialog.dismiss();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        XGPushClickedResult clicked = XGPushManager.onActivityStarted(this);
        if (clicked != null) {
            //来自信鸽的打开方式
        }
        //MTA 统计
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        XGPushManager.onActivityStoped(this);
        //MTA 统计
        StatService.onPause(this);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String score = intent.getStringExtra(IConstant.SCORE);
                if (scoreDialog == null) {
                    scoreDialog = new AlertDialog.Builder(DialogFragmentActivity.this).create();
                    scoreDialog.setCanceledOnTouchOutside(true);
                }
                if (!scoreDialog.isShowing()) {
                    scoreDialog.show();
                }
                Window window = scoreDialog.getWindow();
                window.setContentView(R.layout.sore_dialog);
                final TextView scoreText = (TextView) window.findViewById(R.id.sore_dialog_text);
                scoreText.setText(score);
                scoreText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (scoreDialog != null && scoreDialog.isShowing()) {
                            scoreDialog.dismiss();
                        }
                    }
                }, 2000);
            }
        }
    };

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
