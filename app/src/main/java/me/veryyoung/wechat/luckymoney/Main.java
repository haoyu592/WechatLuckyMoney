package me.veryyoung.wechat.luckymoney;

import android.app.Activity;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findFirstFieldByExactType;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;


public class Main implements IXposedHookLoadPackage {

    private static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    private static final String LUCKY_MONEY_RECEIVE_UI_CLASS_NAME = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";

    private static String wechatVersion = "";

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(WECHAT_PACKAGE_NAME)) {
            if (TextUtils.isEmpty(wechatVersion)) {
                Context context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
                String versionName = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
                log("Found wechat version:" + versionName);
                wechatVersion = versionName;
                VersionParam.init(versionName);
            }
            findAndHookMethod("com.tencent.mm.ui.LauncherUI", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Activity activity = (Activity) param.thisObject;
                    if (activity != null) {
                        Intent intent = activity.getIntent();
                        if (intent != null) {
                            String className = intent.getComponent().getClassName();
                            if (!TextUtils.isEmpty(className) && className.equals("com.tencent.mm.ui.LauncherUI") && intent.hasExtra("donate")) {
                                Intent donateIntent = new Intent();
                                donateIntent.setClassName(activity, "com.tencent.mm.plugin.remittance.ui.RemittanceUI");
                                donateIntent.putExtra("scene", 1);
                                donateIntent.putExtra("pay_scene", 32);
                                donateIntent.putExtra("scan_remittance_id", "011259012001125901201468688368254");
                                donateIntent.putExtra("fee", 10.0d);
                                donateIntent.putExtra("pay_channel", 12);
                                donateIntent.putExtra("receiver_name", "yang_xiongwei");
                                donateIntent.removeExtra("donate");
                                activity.startActivity(donateIntent);
                                activity.finish();
                            }
                        }
                    }
                }
            });

            findAndHookMethod(VersionParam.getMessageClass, lpparam.classLoader, "b", Cursor.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!PreferencesUtils.open()) {
                        return;
                    }

                    int type = (int) getObjectField(param.thisObject, "field_type");
                    if (type == 436207665 || type == 469762097) {
                        int status = (int) getObjectField(param.thisObject, "field_status");
                        if (status == 4) {
                            return;
                        }

                        int isSend = (int) getObjectField(param.thisObject, "field_isSend");
                        if (PreferencesUtils.notSelf() && isSend != 0) {
                            return;
                        }

                        String talker = getObjectField(param.thisObject, "field_talker").toString();
                        if (PreferencesUtils.notWhisper() && !isGroupTalk(talker)) {
                            return;
                        }

                        if (!isGroupTalk(talker) && isSend != 0) {
                            return;
                        }

                        if (isGroupTalk(talker) && PreferencesUtils.notMute()) {
                            Object msgInfo = callStaticMethod(findClass("com.tencent.mm.storage.ak", lpparam.classLoader), "F", param.thisObject);
                            boolean notMute = (boolean) callStaticMethod(findClass("com.tencent.mm.booter.notification.c", lpparam.classLoader), "a", talker, msgInfo, 3, false);
                            if (!notMute) {
                                return;
                            }
                        }

                        String content = getObjectField(param.thisObject, "field_content").toString();

                        String nativeUrlString = getFromXml(content, "nativeurl");
                        Uri nativeUrl = Uri.parse(nativeUrlString);
                        int msgType = Integer.parseInt(nativeUrl.getQueryParameter("msgtype"));
                        int channelId = Integer.parseInt(nativeUrl.getQueryParameter("channelid"));
                        String sendId = nativeUrl.getQueryParameter("sendid");
                        final Object ab = newInstance(findClass("com.tencent.mm.plugin.luckymoney.c.ab", lpparam.classLoader),
                                msgType, channelId, sendId, nativeUrlString, "", "", talker, "v1.0");


                        int delayTime = PreferencesUtils.delay() ? PreferencesUtils.delayTime() : 0;

                        callMethod(callStaticMethod(findClass("com.tencent.mm.model.ah", lpparam.classLoader), VersionParam.getNetworkByModelMethod), "a", ab, delayTime);
                    }
                }
            });


            findAndHookMethod(LUCKY_MONEY_RECEIVE_UI_CLASS_NAME, lpparam.classLoader, VersionParam.receiveUIFunctionName, int.class, int.class, String.class, VersionParam.receiveUIParamName, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (PreferencesUtils.quickOpen()) {
                        Button button = (Button) findFirstFieldByExactType(param.thisObject.getClass(), Button.class).get(param.thisObject);
                        if (button.isShown() && button.isClickable()) {
                            button.performClick();
                        }
                    }
                }
            });
            hideModule(lpparam);

        }
    }

    private boolean isGroupTalk(String talker) {
        return talker.endsWith("@chatroom");
    }

    private String getFromXml(String xmlmsg, String node) throws XmlPullParserException, IOException {
        String xl = xmlmsg.substring(xmlmsg.indexOf("<msg>"));
        //nativeurl
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser pz = factory.newPullParser();
        pz.setInput(new StringReader(xl));
        int v = pz.getEventType();
        String result = "";
        while (v != XmlPullParser.END_DOCUMENT) {
            if (v == XmlPullParser.START_TAG) {
                if (pz.getName().equals(node)) {
                    pz.nextToken();
                    result = pz.getText();
                    break;
                }
            }
            v = pz.next();
        }
        return result;
    }

    private void hideModule(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getInstalledApplications", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List<ApplicationInfo> applicationList = (List) param.getResult();
                List<ApplicationInfo> resultapplicationList = new ArrayList<>();
                for (ApplicationInfo applicationInfo : applicationList) {
                    String packageName = applicationInfo.packageName;
                    if (isTarget(packageName)) {
                        log("Hid package: " + packageName);
                    } else {
                        resultapplicationList.add(applicationInfo);
                    }
                }
                param.setResult(resultapplicationList);
            }
        });
        findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getInstalledPackages", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List<PackageInfo> packageInfoList = (List) param.getResult();
                List<PackageInfo> resultpackageInfoList = new ArrayList<>();

                for (PackageInfo packageInfo : packageInfoList) {
                    String packageName = packageInfo.packageName;
                    if (isTarget(packageName)) {
                        log("Hid package: " + packageName);
                    } else {
                        resultpackageInfoList.add(packageInfo);
                    }
                }
                param.setResult(resultpackageInfoList);
            }
        });
        findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getPackageInfo", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String packageName = (String) param.args[0];
                if (isTarget(packageName)) {
                    param.args[0] = WECHAT_PACKAGE_NAME;
                    log("Fake package: " + packageName + " as " + WECHAT_PACKAGE_NAME);
                }
            }
        });
        findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getApplicationInfo", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String packageName = (String) param.args[0];
                if (isTarget(packageName)) {
                    param.args[0] = WECHAT_PACKAGE_NAME;
                    log("Fake package: " + packageName + " as " + WECHAT_PACKAGE_NAME);
                }
            }
        });
        findAndHookMethod("android.app.ActivityManager", loadPackageParam.classLoader, "getRunningServices", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List<RunningServiceInfo> serviceInfoList = (List) param.getResult();
                List<RunningServiceInfo> resultList = new ArrayList<>();

                for (RunningServiceInfo runningServiceInfo : serviceInfoList) {
                    String serviceName = runningServiceInfo.process;
                    if (isTarget(serviceName)) {
                        log("Hid service: " + serviceName);
                    } else {
                        resultList.add(runningServiceInfo);
                    }
                }
                param.setResult(resultList);
            }
        });
        findAndHookMethod("android.app.ActivityManager", loadPackageParam.classLoader, "getRunningTasks", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List<RunningTaskInfo> serviceInfoList = (List) param.getResult();
                List<RunningTaskInfo> resultList = new ArrayList<>();

                for (RunningTaskInfo runningTaskInfo : serviceInfoList) {
                    String taskName = runningTaskInfo.baseActivity.flattenToString();
                    if (isTarget(taskName)) {
                        log("Hid task: " + taskName);
                    } else {
                        resultList.add(runningTaskInfo);
                    }
                }
                param.setResult(resultList);
            }
        });
        findAndHookMethod("android.app.ActivityManager", loadPackageParam.classLoader, "getRunningAppProcesses", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List<RunningAppProcessInfo> runningAppProcessInfos = (List) param.getResult();
                List<RunningAppProcessInfo> resultList = new ArrayList<>();

                for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
                    String processName = runningAppProcessInfo.processName;
                    if (isTarget(processName)) {
                        log("Hid process: " + processName);
                    } else {
                        resultList.add(runningAppProcessInfo);
                    }
                }
                param.setResult(resultList);
            }
        });
    }


    private boolean isTarget(String name) {
        return name.contains("veryyoung") || name.contains("xposed");
    }

}
