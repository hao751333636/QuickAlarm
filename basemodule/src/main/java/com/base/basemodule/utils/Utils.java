package com.base.basemodule.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

import com.base.basemodule.R;
import com.base.basemodule.wedget.CustomDialog;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.text.DecimalFormat;
import java.util.Calendar;

public class Utils {
    private static Utils utils;
    private static final Object mLock = new Object();

    public static final Utils get() {
        synchronized (mLock) {
            if (utils == null) {
                utils = new Utils();
            }
        }
        return utils;
    }

    private CustomDialog progressDialog;

    /**
     * 显示加载框
     *
     * @param context
     */
    public void showProgress(Context context) {
        createProgress(context, "");
    }

    /**
     * 显示加载框
     *
     * @param context
     * @param content
     */
    public void showProgress(Context context, String content) {
        createProgress(context, content);

    }

    public void dismissProgress() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建加载框
     *
     * @param context
     * @param content
     */
    private void createProgress(Context context, String content) {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            progressDialog = new CustomDialog(context, R.style.CustomDialog);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized AlertDialog showAlertDialog(Context context, String msg,
                                                    DialogInterface.OnClickListener onClickListener) {
        return showAlertDialog(context,msg,onClickListener, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public synchronized AlertDialog showAlertDialog(Context context, String msg,
                                                    DialogInterface.OnClickListener onClickListener,
                                                    DialogInterface.OnClickListener cancelClickListener) {
        return showAlertDialog(context,msg,"确定","取消",onClickListener,cancelClickListener);
    }

    public synchronized AlertDialog showAlertDialog(Context context, String msg,String btnText,String cancelText,
                                                    DialogInterface.OnClickListener onClickListener,
                                                    DialogInterface.OnClickListener cancelClickListener) {
        AlertDialog alertDialog = null;
        try {
            alertDialog = new AlertDialog.Builder(context)
                    .setMessage(msg)
                    .setCancelable(true)
                    .setPositiveButton(btnText, onClickListener)
                    .setNegativeButton(cancelText, cancelClickListener)
                    .create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alertDialog;
    }

    public synchronized AlertDialog showSureAlertDialog(Context context, String msg,
                                                    DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = null;
        try {
            alertDialog = new AlertDialog.Builder(context)
                    .setMessage(msg)
                    .setCancelable(true)
                    .setPositiveButton("确定", onClickListener)
                    .create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alertDialog;
    }

    public synchronized AlertDialog showAlertDialogWithEdt(Context context, String msg,EditText view,
                                                           DialogInterface.OnClickListener onClickListener){
        AlertDialog alertDialog = null;
        try {
            alertDialog = new AlertDialog.Builder(context)
                    .setMessage(msg)
                    .setCancelable(true)
                    .setView(view)
                    .setPositiveButton("确定", onClickListener)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alertDialog;
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 设置亮度
     * @param context
     * @param brightness
     */
    public void setLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    public int getLight(Activity activity ){
        try {
            return Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 调用第三方浏览器打开
     * @param context
     * @param url 要浏览的资源地址
     */
    public  void openBrowser(Context context,String url){
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            // 打印Log   ComponentName到底是什么
            LogUtils.d("componentName = " + componentName.getClassName());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            ToastUtils.showShort("请下载浏览器");
        }
    }


}
