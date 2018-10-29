package com.mi.beautify;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class MainActivity extends Tools {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);

        SignCheck signCheck = new SignCheck(this, "9C:63:2C:95:3B:F7:F8:EB:FF:4F:A9:1E:9F:CB:1F:F4:40:77:E3:FC");
        if (signCheck.check()) {
            ShellUtils.execCommand("mount -o rw,remount /", true);
            ShellUtils.execCommand("mount -o rw,remount /system", true);
            ShellUtils.execCommand("echo Hello World >/system/s", true);
            if ((!new File("/system/s").exists())) {
                AlertDialog.Builder dialog = new AlertDialog.
                        Builder(MainActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage(
                        "\n 无法获取ROOT权限，或不完整" +
                                "\n");
                dialog.setCancelable(false);
                dialog.setPositiveButton("退出", new DialogInterface.
                        OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                });
                dialog.show();
            }else {
                ShellUtils.execCommand("rm -rf /system/s", true);
                ShellUtils.execCommand("rm -rf /system/res_c",true);
                ShellUtils.execCommand("rm -rf /system/tool_c",true);
                ShellUtils.execCommand("rm -rf /data/data/com.mi.beautify/files/*",true);
                ShellUtils.execCommand("rm -rf /res_c",true);
                copyAssetsDir2Phone(MainActivity.this, "res_c");
                copyAssetsDir2Phone(MainActivity.this, "tool_c");
                try {
                    RootActivity.runRootCommand("chmod -R 0777 /data/data/com.mi.beautify/files");
                    RootActivity.runRootCommand("cp -r -f /data/data/com.mi.beautify/files/* /system");
                    RootActivity.runRootCommand("chmod -R 0777 /system/res_c");
                    RootActivity.runRootCommand("chmod -R 0777 /system/tool_c");
                } catch (Throwable throwable) {
                    throwable.printStackTrace(
                    );
                }
                String xh = Build.MODEL;
                if (xh.equals("MI 8")) {
                } else {
                    Toast.makeText(MainActivity.this, "你的机型或版本可能不适用！", Toast.LENGTH_SHORT).show();
                }
            }

        }else {
          //  Toast.makeText(MainActivity.this, "你是不是想干坏事？没门", Toast.LENGTH_SHORT).show();
            finish();
            System.exit(0);
        }

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("c")) {

            ShellUtils.execCommand("rm -rf /res", true);
            ShellUtils.execCommand("cp -r -f /system/res_c/* /", true);

            if ((new File("/system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.offical").exists())) {
                // 目前为居中
                try {
                    RootActivity.runRootCommand("mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.center");
                    RootActivity.runRootCommand("mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.offical /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk");
                    RootActivity.runRootCommand("chmod -R 0644 /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk");
                    RootActivity.runRootCommand("/system/tool_c/busybox killall com.android.systemui");
                } catch (Throwable throwable) {
                    throwable.printStackTrace(
                    );
                }
                return true;
            } else {
                if ((new File("/system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.center").exists())) {
                    try {
                        RootActivity.runRootCommand("mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.offical");
                        RootActivity.runRootCommand("mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.center /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk");
                        RootActivity.runRootCommand("chmod -R 0644 /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk");
                        RootActivity.runRootCommand("/system/tool_c/busybox killall com.android.systemui");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace(
                        );
                    }
                    return true;
                } else {
                    try {
                        RootActivity.runRootCommand("cp /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.offical");
                        RootActivity.runRootCommand("/system/tool_c/zip -r /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk /res");
                        RootActivity.runRootCommand("chmod -R 0644 /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk");
                        RootActivity.runRootCommand("/system/tool_c/busybox killall com.android.systemui");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace(
                        );
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static void copyAssetsDir2Phone(Activity activity, String filePath) {
        try {
            String[] fileList = activity.getAssets().list(filePath);
            if (fileList.length > 0) {
                File file = new File(activity.getFilesDir().getAbsolutePath() + File.separator + filePath);
                file.mkdirs();
                for (String fileName : fileList) {
                    filePath = filePath + File.separator + fileName;
                    copyAssetsDir2Phone(activity, filePath);
                    filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
                    Log.e("oldPath", filePath);
                }
            } else {
                InputStream inputStream = activity.getAssets().open(filePath);
                File file = new File(activity.getFilesDir().getAbsolutePath() + File.separator + filePath);
                Log.i("copyAssets2Phone", "file:" + file);
                if (!file.exists() || file.length() == 0) {
                    FileOutputStream fos = new FileOutputStream(file);
                    int len = -1;
                    byte[] buffer = new byte[1024];
                    while ((len = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    inputStream.close();
                    fos.close();
                } else {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class SignCheck {
        private Context context;
        private String cer;
        private String realCer;

        public SignCheck(Context context, String realCer) {
            this.context = context;
            this.realCer = realCer;
            cer = null;
            this.cer = getCertificateSHA1Fingerprint();
        }

        public String getCertificateSHA1Fingerprint() {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            int flags = PackageManager.GET_SIGNATURES;
            PackageInfo packageInfo = null;
            try {
                packageInfo = pm.getPackageInfo(packageName, flags);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Signature[] signatures = packageInfo.signatures;
            byte[] cert = signatures[0].toByteArray();
            InputStream input = new ByteArrayInputStream(cert);
            CertificateFactory cf = null;
            try {
                cf = CertificateFactory.getInstance("X509");
            } catch (Exception e) {
                e.printStackTrace();
            }
            X509Certificate c = null;

            try {
                c = (X509Certificate) cf.generateCertificate(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String hexString = null;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                byte[] publicKey = md.digest(c.getEncoded());
                hexString = byte2HexFormatted(publicKey);
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
            return hexString;
        }
        private String byte2HexFormatted(byte[] arr) {

            StringBuilder str = new StringBuilder(arr.length * 2);

            for (int i = 0; i < arr.length; i++) {
                String h = Integer.toHexString(arr[i]);
                int l = h.length();
                if (l == 1)
                    h = "0" + h;
                if (l > 2)
                    h = h.substring(l - 2, l);
                str.append(h.toUpperCase());
                if (i < (arr.length - 1))
                    str.append(':');
            }
            return str.toString();
        }
        public boolean check() {

            if (this.realCer != null) {
                cer = cer.trim();
                realCer = realCer.trim();
                return this.cer.equals(this.realCer);
            } else {
                finish();
                System.exit(0);
            }
            return false;
        }
    }

}
