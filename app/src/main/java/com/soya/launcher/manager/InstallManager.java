package com.soya.launcher.manager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.soya.launcher.App;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.enums.Atts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InstallManager {
    public static void installPackage(Context context, final AppItem item, final File file, Handler handler) throws Exception {
        String name = "base.apk";
        FileInputStream in = new FileInputStream(file);
        PackageManager packageManger = context.getPackageManager();
        PackageInstaller packageInstaller = packageManger.getPackageInstaller();
        packageInstaller.registerSessionCallback(new PackageInstaller.SessionCallback() {
            @Override
            public void onCreated(int sessionId) {

            }

            @Override
            public void onBadgingChanged(int sessionId) {
            }

            @Override
            public void onActiveChanged(int sessionId, boolean active) {
                if (!active) {
                    packageInstaller.unregisterSessionCallback(this);
                    item.setStatus(AppItem.STATU_INSTALL_FAIL);
                    App.retryDownload();
                }
            }

            @Override
            public void onProgressChanged(int sessionId, float progress) {
            }

            @Override
            public void onFinished(int sessionId, boolean success) {
                packageInstaller.unregisterSessionCallback(this);
                item.setStatus(success ? AppItem.STATU_INSTALL_SUCCESS : AppItem.STATU_INSTALL_FAIL);
                App.retryDownload();
            }
        }, handler);
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        PackageInstaller.Session session = null;
        int sessionId = packageInstaller.createSession(params);
        session = packageInstaller.openSession(sessionId);
        OutputStream out = session.openWrite(name, 0, -1);
        byte buffer[] = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }
        session.fsync(out);
        out.close();
        in.close();

        Intent intent = new Intent(context, InnerBroadcast.class);
        intent.setAction(Intent.ACTION_PACKAGE_ADDED);
        intent.putExtra(Atts.DOWNLOAD_URL, item.getAppDownLink());
        session.commit(PendingIntent.getBroadcast(context, sessionId, intent, PendingIntent.FLAG_MUTABLE).getIntentSender());
        session.close();
    }

    public static final class InnerBroadcast  extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (true) return;
            try {
                String url = intent.getStringExtra(Atts.DOWNLOAD_URL);
                Log.e("TAG", "onReceive: "+url);
                if (!TextUtils.isEmpty(url)){
                    int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
                    AppItem item = null;
                    for (AppItem child : App.PUSH_APPS){
                        if (url.equals(child.getAppDownLink())){
                            item = child;
                            break;
                        }
                    }
                    if (item != null){
                        switch (status){
                            case PackageInstaller.STATUS_SUCCESS:
                                item.setStatus(AppItem.STATU_INSTALL_SUCCESS);
                                break;
                            default:
                                item.setStatus(AppItem.STATU_INSTALL_FAIL);
                                break;
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                App.retryDownload();
            }
        }
    }
}