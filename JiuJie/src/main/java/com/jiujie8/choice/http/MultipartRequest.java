package com.jiujie8.choice.http;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyLog;
import com.jiujie8.choice.http.entity.FilePart;
import com.jiujie8.choice.http.entity.StringPart;
import com.jiujie8.choice.http.entity.UploadMultipartEntity;
import com.jiujie8.choice.util.IConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import khandroid.ext.apache.http.entity.mime.MultipartEntity;
import khandroid.ext.apache.http.entity.mime.content.FileBody;
import khandroid.ext.apache.http.entity.mime.content.StringBody;

public class MultipartRequest<T> extends BaseRequest<T> {

//	private MultipartEntity entity = new MultipartEntity();
    private UploadMultipartEntity entity = new UploadMultipartEntity();

    private static final String PROTOCOL_CHARSET = "utf-8";

	public MultipartRequest(String url) {
		super(Method.POST, IConstant.DOMAIN + url);
		setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	public MultipartRequest<T> addMultipartFileEntity(String key, File mFilePart) {
//		entity.addPart(key, new FileBody(mFilePart));
        FilePart part = new FilePart(key, mFilePart, null, null);
        entity.addPart(part);
		return this;
	}

	public MultipartRequest<T> addMultipartStringEntity(String key, String mStringPart) {
//		try {
//			entity.addPart(key, new StringBody(mStringPart, Charset.forName("UTF-8")));
//		} catch (UnsupportedEncodingException e) {
//			VolleyLog.e("UnsupportedEncodingException");
//		}
        StringPart part = new StringPart(key, mStringPart, PROTOCOL_CHARSET);
        entity.addPart(part);
		return this;
	}

    public long getContentLength() {
        return entity.getContentLength();
    }

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
		} catch (IOException e) {
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

    public void setOnProgressListener(UploadMultipartEntity.OnProgressListener l) {
        if (l != null) {
            entity.setListener(l);
        }
    }
}