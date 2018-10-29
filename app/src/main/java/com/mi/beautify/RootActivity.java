package com.mi.beautify;

import android.util.Log;

import java.io.DataOutputStream;

class RootActivity {
    static void runRootCommand(String command) throws Throwable {
        Exception e;
        Throwable th;
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os2 = new DataOutputStream(process.getOutputStream());
            try {
                os2.writeBytes(new StringBuilder(String.valueOf(command)).append("\n").toString());
                os2.writeBytes("exit\n");
                os2.flush();
                process.waitFor();
                if (os2 != null) {
                    try {
                        os2.close();
                    } catch (Exception e2) {
                    }
                }
                process.destroy();
                os = os2;
            } catch (Exception e3) {
                e = e3;
                os = os2;
                try {
                    Log.d("*** DEBUG ***", "Unexpected error: " + e.getMessage());
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e4) {
                            return;
                        }
                    }
                    process.destroy();
                } catch (Throwable th2) {
                    th = th2;
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e5) {
                            throw th;
                        }
                    }
                    process.destroy();
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                os = os2;
                if (os != null) {
                    os.close();
                }
                process.destroy();
                throw th;
            }
        } catch (Exception e6) {
            e = e6;
            Log.d("*** DEBUG ***", "Unexpected error: " + e.getMessage());
            if (os != null) {
                os.close();
            }
            process.destroy();
        }
    }
}
