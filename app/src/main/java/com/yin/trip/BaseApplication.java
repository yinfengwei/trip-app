package com.yin.trip;

import android.app.Application;
import android.app.ProgressDialog;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.yin.trip.util.LogUtil;

/**
 * Created by yinfeng on 2017/3/10 0010.
 */
public class BaseApplication extends Application {

    private static BaseApplication instance;


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        initTbs();
    }


    private void initTbs() {
        //搜集本地tbs内核信息并上报服务器，由服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean arg0) {
                LogUtil.i("onViewInitFinished is " + arg0);
            }
        };

        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                LogUtil.i("onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                LogUtil.i("onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                LogUtil.i("onDownloadProgress:" + i);
            }
        });

        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}
