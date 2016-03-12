package com.shengzhish.xyj.persionalcore;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.services.NotificationDownloadService;

/**
 * Created by wlanjie on 14-6-18.
 */
public class UpdateDialog extends Dialog {

    private String updateUrl;

    /**
     * Create a Dialog window that uses the default dialog frame style.
     *
     * @param context The Context the Dialog is to run it.  In particular, it
     *                uses the window manager and theme in this context to
     *                present its UI.
     */
    public UpdateDialog(Context context, String updateUrl) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.updateUrl = updateUrl;
    }

    /**
     * Create a Dialog window that uses a custom dialog style.
     *
     * @param context The Context in which the Dialog should run. In particular, it
     *                uses the window manager and theme from this context to
     *                present its UI.
     * @param theme   A style resource describing the theme to use for the
     *                window. See <a href="{@docRoot}guide/topics/resources/available-resources.html#stylesandthemes">Style
     *                and Theme Resources</a> for more information about defining and using
     *                styles.  This theme is applied on top of the current theme in
     */
    public UpdateDialog(Context context, int theme) {
        super(context, theme);
    }

    protected UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        View cancelView = view.findViewById(R.id.update_cancel_view);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        View updateView = view.findViewById(R.id.update_continue_view);
        updateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationDownloadService.class);
                intent.putExtra("id", 1);
                intent.putExtra("url", updateUrl);
                getContext().startService(intent);
                dismiss();
            }
        });
    }
}
