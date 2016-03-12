package feipai.qiangdan.util.http;

import java.io.File;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import feipai.qiangdan.core.ToastUtils;


public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

    private final Map<String, String> map;
    private Activity context;
	private File filePath;
	private ProgressDialog pd;
	private long totalSize;
    private String uri;

    public HttpMultipartPost(Activity context, String uri, File filePath, Map<String, String> paramap) {
		this.context = context;
		this.filePath = filePath;
        this.uri=uri;
        this.map=paramap;
	}

	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("Uploading Picture...");
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected String doInBackground(String... params) {
		String serverResponse = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
//		HttpPost httpPost = new HttpPost("上传URL， 如：http://www.xx.com/upload.php");
        HttpPost   httpPost=new HttpPost(uri);

		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new CustomMultipartEntity.ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			// We use FileBody to transfer an image
			multipartContent.addPart("img", new FileBody(filePath));
            multipartContent.addPart("orderid",new StringBody(map.get("orderid")));
            multipartContent.addPart("token",new StringBody(map.get("token")));
            multipartContent.addPart("sid",new StringBody(map.get("sid")));
			totalSize = multipartContent.getContentLength();

			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());
            System.out.println(" getStatusCode: " +response.getStatusLine().getStatusCode() );
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return serverResponse;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String result) {
		System.out.println("result: " + result);
        JSONObject json = null;
        int code = 0;
        try {
            json = new JSONObject(result);
           code = json.getInt("state");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(code==1){
            ToastUtils.show(context,"上传成功");
        }else{
            ToastUtils.show(context,"照片已上传，勿重复上传");
        }
		pd.dismiss();
        context.finish();
	}

	@Override
	protected void onCancelled() {
		System.out.println("cancle");
	}

}
