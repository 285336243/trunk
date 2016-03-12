package com.shengzhish.xyj.activity.specialnews;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.shengzhish.xyj.R;


public class SeleterPictureDialog extends Dialog {
    /**
     * Create a Dialog window that uses the default dialog frame style.
     *
     * @param context The Context the Dialog is to run it.  In particular, it
     *                uses the window manager and theme in this context to
     *                present its UI.
     */
    public SeleterPictureDialog(Context context, DialogActListener listener) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar, listener);
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
    public SeleterPictureDialog(Context context, int theme, final DialogActListener listener) {
        super(context, theme);
        setContentView(R.layout.selecor_piture_dialog);
        findViewById(R.id.taken_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    dismiss();
                }
                listener.takeCamera();
            }
        });
        findViewById(R.id.take_aibum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    dismiss();
                }
                listener.takeAlbum();
            }
        });
    }

    protected SeleterPictureDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    public interface DialogActListener {
        public void takeCamera();

        public void takeAlbum();

    }
}
