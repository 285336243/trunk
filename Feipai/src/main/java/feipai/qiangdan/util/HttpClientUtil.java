package feipai.qiangdan.util;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 通用的网络链接工具
 *
 * @author Administrator
 */
public class HttpClientUtil {
    private HttpClient client;

    private HttpGet get;
    private HttpPost post;
    private HttpPut put;


    private HttpResponse response;

    private static Header[] headers;

    static {
        headers = new BasicHeader[10];
        headers[0] = new BasicHeader("Appkey", "12343");
        headers[1] = new BasicHeader("Udid", "");// 手机串号
        headers[2] = new BasicHeader("Os", "android");//
        headers[3] = new BasicHeader("Osversion", "");//
        headers[4] = new BasicHeader("Appversion", "");// 1.0
        headers[5] = new BasicHeader("Sourceid", "");//
        headers[6] = new BasicHeader("Ver", "");

        headers[7] = new BasicHeader("Userid", "");
        headers[8] = new BasicHeader("Usersession", "");

        headers[9] = new BasicHeader("Unique", "");
    }

    public HttpClientUtil() {
        client = new DefaultHttpClient();

    }

    static HttpClientUtil instance = new HttpClientUtil();


    public static HttpClientUtil getInstance() {
        return instance;
    }

    /**
     * 发送请求
     *
     * @param uri
     * @param xml
     */
    public InputStream sendPost(String uri, String xml) {
        post = new HttpPost(uri);

        // 超时

        try {
            HttpEntity entity = new StringEntity(xml, "UTF-8");
            post.setEntity(entity);

            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                return response.getEntity().getContent();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get请求
     *
     * @param uri
     *            访问路径
     * @param params
     *            访问参数 Map<String, String>类型
     * @return 字符串
     */
    /*
     * public String sendGet(String uri, Map<String, String> params) { get = new
	 * HttpGet(uri); try { HttpParams httpParams = new BasicHttpParams();
	 * 
	 * // 设置参数 if (params != null && params.size() > 0) { for (Map.Entry<String,
	 * String> item : params.entrySet()) {
	 * httpParams.setParameter(item.getKey(), item.getValue()); }
	 * get.setParams(httpParams); } response = client.execute(get); if
	 * (response.getStatusLine().getStatusCode() == 200) { return
	 * EntityUtils.toString(response.getEntity(), "UTF-8"); } }
	 * catch (Exception e) { e.printStackTrace(); } return ""; }
	 */

    /*************************** 电商 **********************************/
    // post或get请求带参数
    // Get:url?params=xxx&
    // post：带参数（HttpEntity：参数+编码）

    /**
     * 发送Post请求
     *
     * @param uri
     * @param params ：参数
     * @return
     */
    public String sendNotDesPost(String uri, Map<String, String> params) {
        post = new HttpPost(uri);
        post.setHeaders(headers);
        Set<String> keySet = params.keySet();
        //DES des = new DES();
        for (String key : keySet) {
            String value = params.get(key);
            params.put(key, value);
        }
        // 处理超时
        HttpParams httpParams = new BasicHttpParams();//
        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
        post.setParams(httpParams);
        // 设置参数
        if (params != null && params.size() > 0) {

            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, String> item : params.entrySet()) {
                BasicNameValuePair pair = new BasicNameValuePair(item.getKey(),
                        item.getValue());
                parameters.add(pair);
            }
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        parameters, "UTF-8");
                post.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(response.getEntity(),
                        "UTF-8");
                String value = json;
                return value;
            }

        } catch (ConnectTimeoutException cte) {
            cte.printStackTrace();
            return "";
        } catch (SocketTimeoutException ste) {
            ste.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return "";
    }
    // 细节：设置头，设置超时限制

    /**
     * 发送Post请求
     *
     * @param uri
     * @param params ：参数
     * @return
     */
    public String sendPost(String uri, Map<String, String> params) {
        post = new HttpPost(uri);
        post.setHeaders(headers);
        // 处理超时
        HttpParams httpParams = new BasicHttpParams();//
        httpParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);

        post.setParams(httpParams);
        // 设置参数
        if (params != null && params.size() > 0) {

            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, String> item : params.entrySet()) {
                BasicNameValuePair pair = new BasicNameValuePair(item.getKey(),
                        item.getValue());
                parameters.add(pair);
            }
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        parameters, "UTF-8");
                post.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            response = client.execute(post);
            Log.v("kan", "response.getEntity = " + response.getEntity().toString());
            System.out.println(response.getEntity().toString());

            if (response.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(response.getEntity(),
                        "UTF-8");
                return json;
            }

        } catch (ConnectTimeoutException cte) {
            System.out.println("ConnectTimeoutException");
            cte.printStackTrace();
            return "";
        } catch (SocketTimeoutException ste) {
            System.out.println("SocketTimeoutException");
            ste.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return "";
    }



    public String sendUnDESPost(String uri, Map<String, String> params) {
        post = new HttpPost(uri);
        post.setHeaders(headers);
        // 处理超时
        HttpParams httpParams = new BasicHttpParams();//
        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
        post.setParams(httpParams);
        // 设置参数
        if (params != null && params.size() > 0) {

            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, String> item : params.entrySet()) {
                BasicNameValuePair pair = new BasicNameValuePair(item.getKey(),
                        item.getValue());
                parameters.add(pair);
            }
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        parameters, "UTF-8");
                post.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            response = client.execute(post);

            System.out.println(response.getEntity().toString());
            if (response.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(response.getEntity(),
                        "UTF-8");
                return json;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.v("kan", "Exception e = " + e.toString());
        }

        return "";
    }
    /**
     * 异步的Post请求
     *
     * @param urlStr
     * @param callBack
     */
    public  void doPostAsyn(final String urlStr, final Map<String, String> params,final CallBack callBack)
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    String result = getPostDta(urlStr, params);
                    if (callBack != null)
                    {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            };
        }.start();
    }

    public synchronized String getPostDta(String uri, Map<String, String> params) {
        Map<String, String> map = StringUtils.sorting(params);
        int code = sendFeiPost(uri, map);
        if (code == 200) {
            String json = null;
            try {
//                response = client.execute(post);
                json = EntityUtils.toString(response.getEntity(),
                        "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        } else {
            return IConstant.REQUST_FAIL + " code=" + code;
        }
    }

    private int sendFeiPost(String uri, Map<String, String> params) {
        post = new HttpPost(IConstant.DOMAIN + uri);
        post.setHeaders(headers);
        // 处理超时
        HttpParams httpParams;
        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
        post.setParams(httpParams);
        // 设置参数
        if (params != null && params.size() > 0) {

            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, String> item : params.entrySet()) {
                BasicNameValuePair pair = new BasicNameValuePair(item.getKey(),
                        item.getValue());
                parameters.add(pair);
            }
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        parameters, "UTF-8");
                post.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            response = client.execute(post);
//            System.out.println(response.getEntity().toString());
            int code = response.getStatusLine().getStatusCode();
            return code;
        } catch (Exception e) {
            e.printStackTrace();
//            Log.v("kan", "Exception e = " + e.toString());
        }

        return 0;
    }

    // post或get请求带参数
    // Get:url?params=xxx&
    // post：带参数（HttpEntity：参数+编码）
    // 细节：设置头，设置超时限制

    /**
     * 结果回调
     */
    public interface CallBack
    {
        void onRequestComplete(String result);
    }
    /**
     * 异步的Get请求
     *
     * @param urlStr
     * @param callBack
     */
    public  void doGetAsyn(final String urlStr, final Map<String, String> params,final CallBack callBack)
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    String result = getGetData(urlStr, params);
                    if (callBack != null)
                    {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            };
        }.start();
    }

    public synchronized String getGetData(String uri, Map<String, String> params) throws UnsupportedEncodingException {
        int code = sendFeiGet(uri, params);
        if (code == 200) {
            String json = null;
            try {
                json = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        } else {
            return IConstant.REQUST_FAIL + " code=" + code;
        }
    }

    public int sendFeiGet(String uri, Map<String, String> map) throws UnsupportedEncodingException {
        Map<String, String> params = StringUtils.sorting(map);
        // StringBuilder是用来组拼请求地址和参数
        StringBuilder sb = new StringBuilder();
        sb.append(uri).append("?");
        if (params != null && params.size() != 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                //如果请求参数中有中文，需要进行URLEncoder编码
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        get = new HttpGet(IConstant.DOMAIN + sb.toString());
        get.setHeaders(headers);
        // 处理超时
        HttpParams httpParams;
        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
        get.setParams(httpParams);
        try {
            response = client.execute(get);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 401;
    }
    /**
     * 异步的Putt请求
     *
     * @param urlStr
     * @param callBack
     */
    public  void doPutAsyn(final String urlStr, final Map<String, String> params,final CallBack callBack)
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    String result = getPutData(urlStr, params);
                    if (callBack != null)
                    {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            };
        }.start();
    }
    public synchronized String getPutData(String uri, Map<String, String> params) throws UnsupportedEncodingException {
        int code = sendFeiPut(uri, params);
        if (code == 200) {
            String json = null;
            try {
                json = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        } else {
            return IConstant.REQUST_FAIL + " code=" + code;
        }
    }
    /**
     * 发送put请求
     * @param uri
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     */
    public int sendFeiPut(String uri, Map<String, String> map) throws UnsupportedEncodingException {
        Map<String, String> params = StringUtils.sorting(map);
        // StringBuilder是用来组拼请求地址和参数
        StringBuilder sb = new StringBuilder();
        sb.append(uri).append("?");
        if (params != null && params.size() != 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                //如果请求参数中有中文，需要进行URLEncoder编码
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        put = new HttpPut(IConstant.DOMAIN + sb.toString());
        put.setHeaders(headers);
        // 处理超时
        HttpParams httpParams;
        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
        put.setParams(httpParams);
        try {
            response = client.execute(put);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 401;
    }

    public String sendGetss(String uri) {


        get = new HttpGet(uri);
        get.setHeaders(headers);

        // 处理超时
        HttpParams httpParams = new BasicHttpParams();
        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
        get.setParams(httpParams);
        try {
            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {

                return EntityUtils.toString(response.getEntity(),
                        "UTF-8");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


}
