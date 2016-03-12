package com.shengzhish.xyj.core;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.github.kevinsawicki.wishlist.ViewFinder;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import java.io.Serializable;

/**
 * Created by wlanjie on 14-2-14.
 * Base fragment capable of receiving dialog callbacks
 */
public class DialogFragment extends RoboSherlockFragment implements DialogResultListener {

    /**
     * View finder bound to the value last specified to
     * {@link #onViewCreated(android.view.View, android.os.Bundle)}
     */
    protected ViewFinder finder;

    protected CacheRepository repository;

    /**
     * Is this fragment usable from the UI-thread
     *
     * @return true if usable, false otherwise
     */
    protected boolean isUsable() {
        return getActivity() != null;
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
        // Intentionally left blank
    }

    /**
     * Get serializable extra from activity's intent
     *
     * @param name
     * @return extra
     */
    @SuppressWarnings("unchecked")
    protected <V extends Serializable> V getSerializableExtra(final String name) {
        Activity activity = getActivity();
        if (activity != null) {
            return (V) activity.getIntent().getSerializableExtra(name);
        } else {
            return null;
        }
    }

    /**
     * Get string extra from activity's intent
     *
     * @param name
     * @return extra
     */
    protected String getStringExtra(final String name) {
        Activity activity = getActivity();
        if (activity != null) {
            return activity.getIntent().getStringExtra(name);
        } else {
            return null;
        }
    }

    /**
     * Get long extra from activity's intent
     *
     * @param name
     * @return extra
     */
    protected long getLongExtra(final String name) {
        Activity activity = getActivity();
        if (activity != null) {
            return activity.getIntent().getLongExtra(name, -1);
        } else {
            return -1;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        finder = new ViewFinder(view);
        repository = CacheRepository.getInstance().fromContext(getActivity());
    }

}
