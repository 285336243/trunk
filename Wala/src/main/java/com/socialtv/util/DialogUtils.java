package com.socialtv.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.Keyboard;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.SharePage;
import com.socialtv.ShareTemplete;
import com.socialtv.WalaApplication;
import com.socialtv.core.ThirdPartyShareActivity;
import com.socialtv.publicentity.Pics;
import com.socialtv.publicentity.Topic;
import com.socialtv.topic.ImageDetailAdapter;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

/**
 * Created by wlanjie on 14-10-14.
 */
public class DialogUtils {

    //分享
    private Dialog shareDialog;
    private View shareSina;
    private View shareTencent;
    private View shareWeixin;
    private View shareWeixinFriend;
    private View shareCancel;

    //图片浏览
    private Dialog imageDialog;
    private ImageDetailAdapter detailAdapter;
    private ViewPager viewPager;
    private ImageView singleImageView;
    private PhotoViewAttacher mAttacher;
    private ProgressBar singleProgressBar;
    private TextView imageDetailCountTextView;

    //评论
    private Dialog submitDialog;
    private EditText editText;
    private View sumbitView;
    private InputMethodManager inputMethodManager;
    private String editContentText;

    //积分
    private AlertDialog scoreDialog;
    private TextView scoreText;
    private WindowManager windowManager;
    private View scoreView;

    public final Dialog createShareDialog(final ThirdPartyShareActivity activity, final Topic data) {
        if (shareDialog == null) {
            shareDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            shareDialog.setContentView(R.layout.share);
            shareSina = shareDialog.findViewById(R.id.share_sina);
            shareTencent = shareDialog.findViewById(R.id.share_tencent);
            shareWeixin = shareDialog.findViewById(R.id.share_weixin);
            shareWeixinFriend = shareDialog.findViewById(R.id.share_weixin_friend);
            shareCancel = shareDialog.findViewById(R.id.share_cancel);
            shareCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (shareDialog != null && shareDialog.isShowing()) {
                        shareDialog.dismiss();
                    }
                }
            });
            shareMessage(activity, data);
        }
        return shareDialog;
    }

    private void shareMessage(final ThirdPartyShareActivity activity, final Topic data) {
        final ShareTemplete templete = data.getShareTemplete();
        shareSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (templete != null) {
                    if (TextUtils.isEmpty(templete.getShareImg())) {
                        activity.sinaShareText(templete.getShareText());
                    } else {
                        ImageLoader.getInstance().loadImage(templete.getShareImg(), ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                activity.sinaSharePic(loadedImage, templete.getShareText());
                            }
                        });
                    }
                }
                if (shareDialog != null && shareDialog.isShowing())
                    shareDialog.dismiss();
            }
        });

        shareTencent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (templete != null) {
                    if (TextUtils.isEmpty(templete.getShareImg())) {
                        activity.tencentShareText(templete.getShareText());
                    } else {
                        ImageLoader.getInstance().loadImage(templete.getShareImg(), ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                activity.tencentSharePic(loadedImage, templete.getShareText());
                            }
                        });
                    }
                }
                if (shareDialog != null && shareDialog.isShowing())
                    shareDialog.dismiss();
            }
        });

        shareWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (templete != null) {
                    final SharePage page = templete.getSharePage();
                    if (page != null) {
                        if (TextUtils.isEmpty(page.getIcon())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon);
                            activity.shareWeiXinWebPage(page.getUrl(), page.getTitle(), page.getDesc(), bitmap);
                        } else {
                            ImageLoader.getInstance().loadImage(page.getIcon(), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    activity.shareWeiXinWebPage(page.getUrl(), page.getTitle(), page.getDesc(), loadedImage);
                                }
                            });
                        }
                    }
                }
                if (shareDialog != null && shareDialog.isShowing())
                    shareDialog.dismiss();
            }
        });

        shareWeixinFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (templete != null) {
                    final SharePage page = templete.getSharePage();
                    if (page != null) {
                        if (TextUtils.isEmpty(page.getIcon())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon);
                            activity.shareWeiXinWebPage(page.getUrl(), page.getTitle(), page.getDesc(), bitmap);
                        } else {
                            ImageLoader.getInstance().loadImage(page.getIcon(), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    activity.shareWeiXinFriendWebPage(page.getUrl(), page.getTitle(), page.getDesc(), loadedImage);
                                }
                            });
                        }
                    }
                }
                if (shareDialog != null && shareDialog.isShowing())
                    shareDialog.dismiss();
            }
        });
    }

    private final Dialog createImageDialog(final Activity activity) {
        if (imageDialog == null) {
            imageDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            detailAdapter = new ImageDetailAdapter(activity);
            imageDialog.setContentView(R.layout.topic_detail_image_pager);
            viewPager = (ViewPager) imageDialog.findViewById(R.id.topic_detail_image_pager);
            singleImageView = (ImageView) imageDialog.findViewById(R.id.topic_detail_single_image);
            mAttacher = new PhotoViewAttacher(singleImageView);
            singleProgressBar = (ProgressBar) imageDialog.findViewById(R.id.topic_detail_single_progress);
            imageDetailCountTextView = (TextView) imageDialog.findViewById(R.id.topic_detail_image_count);
            viewPager.setAdapter(detailAdapter);
            detailAdapter.setOnPhotoTapListener(new ImageDetailAdapter.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (imageDialog.isShowing())
                        imageDialog.dismiss();
                }
            });
        }
        return imageDialog;
    }

    /**
     * 多张图
     * @param items
     * @param position
     */
    public final void showImageDetailDialog(final Activity activity, final List<Pics> items, int position) {
        createImageDialog(activity);
        viewPager.setVisibility(View.VISIBLE);
        singleImageView.setVisibility(View.GONE);
        detailAdapter.setItems(items);
        imageDetailCountTextView.setText((position + 1) + "/" + items.size());
        if (!imageDialog.isShowing())
            imageDialog.show();

        viewPager.setCurrentItem(position);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imageDetailCountTextView.setText((position + 1) + "/" + items.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 单图
     * @param view
     * @param imageUri
     */
    public final void setSingleImageOnClick(final Activity activity, final View view, final String imageUri) {
        createImageDialog(activity);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setVisibility(View.GONE);
                singleImageView.setVisibility(View.VISIBLE);
                imageDetailCountTextView.setText("1/1");
                if (!imageDialog.isShowing())
                    imageDialog.show();
                ImageLoader.getInstance().displayImage(imageUri, singleImageView, ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        singleProgressBar.setVisibility(View.GONE);
                        singleImageView.setImageBitmap(loadedImage);
                        mAttacher.update();
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        singleProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        super.onLoadingCancelled(imageUri, view);
                        singleProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        singleProgressBar.setVisibility(View.VISIBLE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        singleProgressBar.setMax(total);
                        singleProgressBar.setProgress(current);
                    }
                });
                mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        if (imageDialog.isShowing())
                            imageDialog.dismiss();
                    }
                });
            }
        });
    }

    public final Dialog createPostDialog(final Activity activity) {
        if (submitDialog == null) {
            submitDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            submitDialog.setContentView(R.layout.feed_submit_post);
            View rootView = submitDialog.findViewById(R.id.feed_submit_root);
            editText = (EditText) submitDialog.findViewById(R.id.feed_submit_edit);
            sumbitView = submitDialog.findViewById(R.id.feed_submit_text);
            inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            submitDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    submitDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    inputMethodManager.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    if (!TextUtils.isEmpty(editContentText)) {
                        editText.setText(editContentText);
                        editText.setSelection(editContentText.length());
                    } else {
                        editText.setText("");
                    }
                }
            });

            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (submitDialog != null && submitDialog.isShowing()) {
                            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            submitDialog.dismiss();
                            editContentText = editText.getText().toString();
                        }
                    }
                    return true;
                }
            });
        }
        return submitDialog;
    }

    public final void setHintText(final String text) {
        editText.setHint(text);
    }

    public final EditText getEditText() {
        return editText;
    }

    public final void setEditContentText(final String text) {
        editContentText = text;
    }

    public final void hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public final void setSubmitOnClickListener(final View.OnClickListener listener) {
        sumbitView.setOnClickListener(listener);
    }

    public final AlertDialog createScoreDialog(final Activity activity, final String text) {
        if (scoreDialog == null) {
            scoreDialog = new AlertDialog.Builder(activity).create();
            scoreDialog.setCanceledOnTouchOutside(true);
            scoreDialog.show();
            Window window = scoreDialog.getWindow();
            window.setContentView(R.layout.sore_dialog);
            scoreText = (TextView) window.findViewById(R.id.sore_dialog_text);
        }
        scoreText.setText(text);
        return scoreDialog;
    }

    public final void createScoreWindow(final String text) {
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) WalaApplication.getContext().getSystemService(WalaApplication.getContext().WINDOW_SERVICE);
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        LayoutInflater inflater = LayoutInflater.from(WalaApplication.getContext());
        scoreView = inflater.inflate(R.layout.sore_dialog, null);
        scoreView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        TextView textView = (TextView) scoreView.findViewById(R.id.sore_dialog_text);
        textView.setText(text);
        if (scoreView.getParent() != null) {
            windowManager.removeView(scoreView);
        }
        windowManager.addView(scoreView, windowParams);


//        windowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        LayoutInflater inflater = LayoutInflater.from(activity);
//        scoreView = inflater.inflate(R.layout.sore_dialog, null);
//        scoreView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        TextView textView = (TextView) scoreView.findViewById(R.id.sore_dialog_text);
//        textView.setText(text);
//        windowManager.addView(scoreView, windowParams);
    }

    public final void removeWindow() {
        windowManager.removeView(scoreView);
    }

}
