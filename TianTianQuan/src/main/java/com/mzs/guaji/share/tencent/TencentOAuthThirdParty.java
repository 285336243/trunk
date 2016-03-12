package com.mzs.guaji.share.tencent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.mzs.guaji.share.AuthorizeListener;
import com.mzs.guaji.util.CacheRepository;
import com.mzs.guaji.share.OAuthThirdParty;
import com.mzs.guaji.share.OAuthToken;
import com.mzs.guaji.util.ToastUtil;
import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.Authorize;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;
import com.tencent.weibo.sdk.android.model.AccountModel;
import com.tencent.weibo.sdk.android.model.BaseVO;
import com.tencent.weibo.sdk.android.model.ModelResult;
import com.tencent.weibo.sdk.android.network.HttpCallback;

/**
 * Created by wlanjie on 14-1-2.
 */
public class TencentOAuthThirdParty implements OAuthThirdParty {

    protected Context context;
    protected long appId;
    protected String appSecket;
    protected WeiboAPI mWeiboAPI;
    protected String mAccessToken;
    private String requestFormat = "json";
    protected CacheRepository mRepository;
    private AuthorizeListener mAuthorizeListener;

    public TencentOAuthThirdParty(Context context, long appId, String appSecket) {
        this.context = context;
        this.appId = appId;
        this.appSecket = appSecket;
    }

    @Override
    public void authorize(AuthorizeListener authorizeListener) {
        this.mAuthorizeListener = authorizeListener;
        AuthHelper.register(context, appId, appSecket, mAuthListener);
        AuthHelper.auth(context, "");
    }

    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void shareText(String text) {
        initWeiboApi();
        mWeiboAPI.addWeibo(context, text, requestFormat, 0d, 0d, 0, 0, mCallBack, null, BaseVO.TYPE_JSON);
    }

    public void initWeiboApi() {
        mRepository = CacheRepository.getInstance().fromContext(context);
        mAccessToken = Util.getSharePersistent(context, "ACCESS_TOKEN");
        if (mAccessToken == null || "".equals(mAccessToken)) {
            ToastUtil.showToast(context, "请先授权");
            return ;
        }
        AccountModel mAccount = new AccountModel(mAccessToken);
        mWeiboAPI = new WeiboAPI(mAccount);

    }

    @Override
    public void sharePic(ImageView imageView, String text) {
        initWeiboApi();
        if(imageView != null) {
            Drawable mDrawable = imageView.getDrawable();
            if(mDrawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mDrawable;
                if(bitmapDrawable != null) {
                    Bitmap mBitmap = bitmapDrawable.getBitmap();
                    if(mWeiboAPI != null && mBitmap != null && context != null) {
                        mWeiboAPI.addPic(context, text, requestFormat, 0d, 0d, mBitmap, 0, 0, mCallBack, null, BaseVO.TYPE_JSON);
                    }else {
                        ToastUtil.showToast(context, "分享出错");
                    }
                }else {
                    ToastUtil.showToast(context, "分享出错");
                }
            }
        }
    }

    @Override
    public void sharePic(Bitmap bitmap, String text) {
        initWeiboApi();
        AccountModel mAccount = new AccountModel(mAccessToken);
        mWeiboAPI = new WeiboAPI(mAccount);
        if(mWeiboAPI != null && bitmap != null && context != null) {
            mWeiboAPI.addPic(context, text, requestFormat, 0d, 0d, bitmap, 0, 0, mCallBack, null, BaseVO.TYPE_JSON);
        }else {
            ToastUtil.showToast(context, "分享出错");
        }
    }

    @Override
    public void sharePage(String text, ImageView imageView, String pageTitle, String pageDesc, Bitmap pageThumbImage, String pageLink, String defaultText) {

    }

    @Override
    public void sharePage(String text, Bitmap bitmap, String pageTitle, String pageDesc, Bitmap pageThumbImage, String pageLink, String defaultText) {

    }

    HttpCallback mCallBack = new HttpCallback() {
        @Override
        public void onResult(Object object) {
            ModelResult result = (ModelResult) object;
            if(result!=null){
                System.out.println(result.getError_message());
                ToastUtil.showToast(context, "分享腾讯微博成功");
            }else{
                ToastUtil.showToast(context,"分享腾讯微博失败");
                System.out.println(result.getObj().toString());
            }
        }
    };

    OnAuthListener mAuthListener = new OnAuthListener() {
        @Override
        public void onWeiBoNotInstalled() {
            Intent mIntent = new Intent(context, Authorize.class);
            context.startActivity(mIntent);
        }

        @Override
        public void onWeiboVersionMisMatch() {
            Intent mIntent = new Intent(context, Authorize.class);
            context.startActivity(mIntent);
        }

        @Override
        public void onAuthFail(int result, String msg) {
            ToastUtil.showToast(context, "分享失败:"+msg);
        }

        @Override
        public void onAuthPassed(String name, WeiboToken token) {
            ToastUtil.showToast(context, "授权成功");
            Util.saveSharePersistent(context, "ACCESS_TOKEN", token.accessToken);
            Util.saveSharePersistent(context, "EXPIRES_IN", String.valueOf(token.expiresIn));
            Util.saveSharePersistent(context, "OPEN_ID", token.openID);
            Util.saveSharePersistent(context, "OPEN_KEY", token.omasKey);
            Util.saveSharePersistent(context, "REFRESH_TOKEN", "");
            Util.saveSharePersistent(context, "NAME", name);
            Util.saveSharePersistent(context, "NICK", name);
            Util.saveSharePersistent(context, "CLIENT_ID", Util.getConfig().getProperty("APP_KEY"));
            Util.saveSharePersistent(context, "AUTHORIZETIME",
                    String.valueOf(System.currentTimeMillis() / 1000l));
            if(mAuthorizeListener != null) {
                mAuthorizeListener.onComplete(new OAuthToken(token.accessToken, token.openID, token.expiresIn + (System.currentTimeMillis() / 1000l)));
            }
            AuthHelper.unregister(context);
        }
    };
}
