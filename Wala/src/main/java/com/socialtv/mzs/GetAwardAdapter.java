package com.socialtv.mzs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.SharePage;
import com.socialtv.ShareTemplete;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.mzs.entity.GetAwardResult;
import com.socialtv.mzs.entity.Lottery;
import com.socialtv.util.ImageUtils;

import java.util.List;

/**
 * Created by wlanjie on 14-9-9.
 * 领取奖励的Adapter
 */
public class GetAwardAdapter extends MultiTypeAdapter {

    private final static String EMPTY = "empty";
    private final GetAwardActivity activity;
    private final String id;
    private final MZSService service;

    private final static int TITLE = 0;
    private final static int MSG = 1;
    private final static int GET = 2;
    private final static int ROOT = 3;
    private final static int EMPTY_TEXT = 4;

    private Dialog dialog;

    private TextView dialogTitle;

    private TextView dialogNotice;

    private TextView dialogIKnow;

    private View dialogSina;

    private View dialogTencent;

    private View dialogWeixinFriendCircle;

    private View dialogWeixinFriend;

    private String type;

    public GetAwardAdapter(final GetAwardActivity activity, final String id, final MZSService service) {
        super(activity);
        this.activity = activity;
        this.id = id;
        this.service = service;
    }

    public void addItems(final List<GetAwardResult> items, final String type) {
        this.type = type;
        addItems(0, items);
    }

    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.get_award_item;
    }

    @Override
    protected int[] getChildViewIds(int type) {
        return new int[]{
                R.id.get_award_item_title,
                R.id.get_award_item_msg,
                R.id.get_award_item_get,
                R.id.get_award_item_root,
                R.id.get_award_item_empty
        };
    }

    @Override
    protected void update(int position, Object item, int type) {
        final GetAwardResult resultItem = (GetAwardResult) item;
        if (!EMPTY.equals(this.type)) {
            setGone(ROOT, false);
            setGone(EMPTY_TEXT, true);
            if (resultItem != null) {
                setText(TITLE, resultItem.getTitle());
                setText(MSG, resultItem.getMsg());
                if (resultItem.getStatus() == 1) {
                    setGone(GET, false);
                    setText(GET, "领取");
                    textView(GET).setBackgroundResource(R.drawable.get_bdg);
                    textView(GET).setEnabled(true);
                    textView(GET).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new ProgressDialogTask<Lottery>(activity) {
                                @Override
                                protected Lottery run(Object data) throws Exception {
                                    return (Lottery) HttpUtils.doRequest(service.createGetAwardReusltRequest(id, resultItem.getVid())).result;
                                }

                                @Override
                                protected void onSuccess(Lottery result) throws Exception {
                                    super.onSuccess(result);
                                    if (result != null) {
                                        if (result.getResponseCode() == 0) {
                                            showDialog(result);
                                            resultItem.setStatus(3);
                                            notifyDataSetChanged();
                                        } else {
                                            ToastUtils.show(activity, result.getResponseMessage());
                                        }
                                    }
                                }
                            }.start("请稍候");
                        }
                    });
                } else if (resultItem.getStatus() == 2) {
                    setGone(GET, true);
                } else if (resultItem.getStatus() == 3) {
                    setGone(GET, false);
                    setText(GET, "已领取");
                    textView(GET).setEnabled(false);
                    textView(GET).setBackgroundResource(R.drawable.transparent);
                }
            }
        } else {
            setGone(ROOT, true);
            setGone(EMPTY_TEXT, false);
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(metrics.widthPixels, (metrics.heightPixels - Utils.dip2px(activity, 48)) / 2);
            view(EMPTY_TEXT).setLayoutParams(params);
            setText(EMPTY_TEXT, resultItem.getMsg());
        }
    }

    private void showDialog(Lottery result) {
        if (dialog == null) {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.lottery_dialog);
            dialogTitle = (TextView) dialog.findViewById(R.id.lottery_dialog_title);
            dialogNotice = (TextView) dialog.findViewById(R.id.lottery_dialog_notice);
            dialogIKnow = (TextView) dialog.findViewById(R.id.lottery_i_know);
            dialogIKnow.setText(R.string.i_know);
            dialogSina = dialog.findViewById(R.id.lottery_dialog_share_sina);
            dialogTencent = dialog.findViewById(R.id.lottery_dialog_share_tencent);
            dialogWeixinFriendCircle = dialog.findViewById(R.id.lottery_dialog_share_weixin);
            dialogWeixinFriend = dialog.findViewById(R.id.lottery_dialog_share_weixin_friend);
        }
        if (result.getResult() != null) {
            dialogTitle.setText(result.getResult().getTitle());
            dialogNotice.setText(result.getResult().getNotice());

        }
        dialogIKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });
        final ShareTemplete templete = result.getShareTemplete();
        dialogSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        dialogTencent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        dialogWeixinFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        dialogWeixinFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (templete != null) {
                    final SharePage page = templete.getSharePage();
                    if (page != null) {
                        if (TextUtils.isEmpty(page.getIcon())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon);
                            activity.shareWeiXinFriendWebPage(page.getUrl(), page.getTitle(), page.getDesc(), bitmap);
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
            }
        });
        if (dialog != null && !dialog.isShowing())
            dialog.show();
    }
}
