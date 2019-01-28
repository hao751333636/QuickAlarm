package com.base.basemodule.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static java.lang.String.valueOf;

/**
 * Created by Yuki on 16/11/7.
 */

public class AsyncHttp {

    private static RequestQueue requestQueue;


    private static boolean isNativeResult =false;

    /**
     * 用来标志请求的what, 类似handler的what一样，这里用来区分请求
     */


    protected static RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = NoHttp.newRequestQueue(5);
        return requestQueue;
    }

    public static void stopHttpBySign(int... sign) {
        for (int key : sign) {
            getRequestQueue().cancelBySign(key);
        }
    }

    public static void stopAllHttpBySign() {
        getRequestQueue().cancelAll();
    }

    /**
     * @param url          接口名,不含host
     * @param map          必填
     * @param file         上传的file
     * @param jsonCallback 回调接口
     */
    public static void postFile(Context context, String url, Map<String, Object> map, File file, int sign, final JsonCallback jsonCallback) {
        // 创建请求对象
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.addHeader("Authorization", "Bearer " + SPUtils.getInstance().getString("token"));
        // 请求服务器成功则返回服务器数据，如果请求服务器失败，读取缓存数据返回
        // request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);

        if (map != null) {
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();

                // 添加请求参数
                request.add(key.toString(), val.toString());
            }
        }
        // 上传文件
        request.add(file.getName(), new FileBinary(file));
//        try {
//            FileInputStream fileStream = new FileInputStream(file);
//            request.setDefineRequestBody(fileStream, Headers.HEAD_VALUE_ACCEPT_APPLICATION_OCTET_STREAM);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
         * request: 请求对象
         * onResponseListener 回调对象，接受请求结果
         */
        getRequestQueue().add(sign, request, getMyResponseListener(context, sign, jsonCallback));
    }


    /**
     * @param url          接口名,不含host
     * @param map          必填
     * @param mapFile      上传的file
     * @param jsonCallback 回调接口
     */
    public static void postFile(Context context, String url, Map<String, String> map, Map<String, File> mapFile, boolean flag, int sign, final JsonCallback jsonCallback) {
        // 创建请求对象
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.addHeader("Authorization", "Bearer " + SPUtils.getInstance().getString("token"));
        // 请求服务器成功则返回服务器数据，如果请求服务器失败，读取缓存数据返回
        // request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);

        if (map != null) {
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                if (!TextUtils.isEmpty(val.toString())) {
                    // 添加请求参数
                    request.add(key.toString(), val.toString());
                }
            }
        }

        if (mapFile != null) {

            Iterator iter = mapFile.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                File file = (File) entry.getValue();
                if (file != null && file.exists()) {
                    // 添加请求参数
                    request.add(key.toString(), new FileBinary(file));

                }
            }
//            request.setContentType("multipart/form-data");
//            request.addHeader("content-type", "multipart/form-data");
            request.setMultipartFormEnable(false);

            /*
             * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
             * request: 请求对象
             * onResponseListener 回调对象，接受请求结果
             */
        }

        if (mapFile.size() < 1) {
            request.setMultipartFormEnable(true);
        }
        getRequestQueue().add(sign, request, getMyResponseListener(context, sign, jsonCallback));
    }


    /**
     * @param url          接口名
     * @param map          req报文
     * @param sign         对100取余=0,加入缓存
     * @param jsonCallback 回调接口
     */
    public static void post(Context context, String url, Map<String, Object> map, int sign, JsonCallback jsonCallback) {
        post(context,url,map,sign,false,jsonCallback);
    }
    /**
     * @param url          接口名
     * @param map          req报文
     * @param sign         对100取余=0,加入缓存
     * @param jsonCallback 回调接口
     */
    public static void post(Context context, String url, Map<String, Object> map, int sign,  boolean nativeResult,JsonCallback jsonCallback) {
        // 创建请求对象
         isNativeResult = nativeResult;
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, RequestMethod.POST);
        request.addHeader("Authorization", "Bearer " + SPUtils.getInstance().getString("token"));
        // 请求服务器成功则返回服务器数据，如果请求服务器失败，读取缓存数据返回
//        if (sign % 100 == 0)
//            request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);

        JSONObject jsonObject = new JSONObject();
        if (map != null) {
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                if (val != null) {
                    try {
                        request.add((String) key, val.toString());
                        // 添加请求参数
                        jsonObject.put((String) key, val.toString());
                    } catch (Exception e) {
                    }
                }
            }
        }
        LogUtils.e(jsonObject);
        request.setDefineRequestBodyForJson(jsonObject);

        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
         * request: 请求对象
         * onResponseListener 回调对象，接受请求结果
         */

        getRequestQueue().add(sign, request, getMyResponseListener(context, sign, jsonCallback));
    }




    /**
     * @param url          接口名
     * @param jsonObject          req报文
     * @param sign         对100取余=0,加入缓存
     * @param jsonCallback 回调接口
     */
    public static void post(Context context, String url, String  jsonObject, int sign, JsonCallback jsonCallback) {
        // 创建请求对象
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, RequestMethod.POST);
        request.addHeader("Authorization", "Bearer " + SPUtils.getInstance().getString("token"));
        // 请求服务器成功则返回服务器数据，如果请求服务器失败，读取缓存数据返回
//        request.setDefineRequestBodyForJson(jsonObject);
        request.setDefineRequestBodyForJson(jsonObject);

        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
         * request: 请求对象
         * onResponseListener 回调对象，接受请求结果
         */

        getRequestQueue().add(sign, request, getMyResponseListener(context, sign, jsonCallback));
    }

    /**
     * get
     *
     * @param url          全路径
     * @param sign         对100取余=0,加入缓存
     * @param jsonCallback 回调接口
     */
    public static void get(Context context, String url, int sign, JsonCallback jsonCallback) {
        // 创建请求对象
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.GET);
        request.addHeader("Authorization", "Bearer " + SPUtils.getInstance().getString("token"));
        getRequestQueue().add(sign, request, getMyResponseListener(context, sign, jsonCallback));
    }

    public static void get(Context context, String url, int sign, boolean nativeResult,JsonCallback jsonCallback) {
        // 创建请求对象
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.GET);
        request.addHeader("Authorization", "Bearer " + SPUtils.getInstance().getString("token"));
        getRequestQueue().add(sign, request, getMyResponseListener(context, sign, jsonCallback));
        isNativeResult = nativeResult;
    }

    protected static OnResponseListener getMyResponseListener(final Context context, final int sign, final JsonCallback jsonCallback) {

        OnResponseListener<Object> myListener = new OnResponseListener<Object>() {

            @SuppressWarnings("unused")
            @Override
            public void onSucceed(int what, Response<Object> response) {
                if (what == sign) {// 判断what是否是刚才指定的请求
                    JSONObject result = null;
                    // 请求成功
                    try {
                        result = new JSONObject(response.get().toString());// 响应结果
                        LogUtils.e("result:" + result);

                        if (response.responseCode() == 200 ||
                                response.responseCode() == 201 ||
                                response.responseCode() == 204) {
                        } else {
                            jsonCallback.onError(new JSONException(""), what);
                            return;
                        }
                        try {
                            if(isNativeResult){
                                jsonCallback.onResponse(result,what);
                                isNativeResult = false;
                            }else {
                                JSONObject resultJson = result.getJSONObject("result");
                                jsonCallback.onResponse(resultJson, what);
                            }
                        }catch (JSONException e) {
                            jsonCallback.onResponse(new JSONObject(), what);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        jsonCallback.onError(new JSONException(""), what);
                    }
                }
            }

            @Override
            public void onStart(int what) {
                jsonCallback.onBefore(what);
            }

            @Override
            public void onFinish(int what) {
                jsonCallback.onAfter(what);
            }

            @Override
            public void onFailed(int what, Response<Object> response) {
                Exception e = response.getException();
//                if (response.responseCode() == 200 ||
//                        response.responseCode() == 201 ||
//                        response.responseCode() == 204) {
//                    jsonCallback.onResponse(new JSONObject(), what);
//                } else {
                // 请求失败，这里可以根据demo判断错误类型
                jsonCallback.onError(e, what);
//                }
            }


        };

        return myListener;

    }


    /**
     * @param url          接口名
     * @param sign         对100取余=0,加入缓存
     * @param jsonCallback 回调接口
     */
    public static void delete(Context context, String url, int sign, JsonCallback jsonCallback) {
        // 创建请求对象
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.DELETE);
        request.addHeader("Authorization", "Bearer " + SPUtils.getInstance().getString("token"));
        // 请求服务器成功则返回服务器数据，如果请求服务器失败，读取缓存数据返回

        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
         * request: 请求对象
         * onResponseListener 回调对象，接受请求结果
         */
        getRequestQueue().add(sign, request, getMyResponseListener(context, sign, jsonCallback));
    }

    /**
     * @param url          接口名
     * @param sign         对100取余=0,加入缓存
     * @param jsonCallback 回调接口
     */
    public static void put(Context context, String url, Map<String, Object> map, int sign, JsonCallback jsonCallback) {
        // 创建请求对象
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.PUT);
        request.addHeader("Authorization", "Bearer " + SPUtils.getInstance().getString("token"));
        JSONObject jsonObject = new JSONObject();
        if (map != null) {
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                if (val != null) {
                    try {
                        request.add((String) key, val.toString());
                        // 添加请求参数
                        jsonObject.put((String) key, val.toString());
                    } catch (Exception e) {
                    }
                }
            }
        }
        request.setDefineRequestBodyForJson(jsonObject);
        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
         * request: 请求对象
         * onResponseListener 回调对象，接受请求结果
         */
        getRequestQueue().add(sign, request, getMyResponseListener(context, sign, jsonCallback));

    }

    /**
     * @param url          接口名
     * @param sign         对100取余=0,加入缓存
     * @param jsonCallback 回调接口
     */
    public static void put(Context context, String url, JSONObject jsonObject, int sign, JsonCallback jsonCallback) {
        // 创建请求对象
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.PUT);
        request.addHeader("Authorization", "Bearer " + SPUtils.getInstance().getString("token"));
        request.setDefineRequestBodyForJson(jsonObject);

        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
         * request: 请求对象
         * onResponseListener 回调对象，接受请求结果
         */
        getRequestQueue().add(sign, request, getMyResponseListener(context, sign, jsonCallback));

    }




}
