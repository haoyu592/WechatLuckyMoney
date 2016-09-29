package me.veryyoung.wechat.luckymoney;


public class VersionParam {

    public static String receiveUIParamName = "com.tencent.mm.u.k";
    public static String receiveUIFunctionName = "e";
    public static String getNetworkByModelMethod = "yj";

    public static void init(String version) {
        switch (version) {
            case "6.3.23":
                receiveUIFunctionName = "d";
                getNetworkByModelMethod = "vE";
                receiveUIParamName = "com.tencent.mm.t.j";
                break;
            case "6.3.25":
                receiveUIFunctionName = "d";
                getNetworkByModelMethod = "vF";
                receiveUIParamName = "com.tencent.mm.t.j";
                break;
            case "6.3.27":
                receiveUIFunctionName = "e";
                getNetworkByModelMethod = "yj";
                receiveUIParamName = "com.tencent.mm.u.k";
                break;
            default:
                receiveUIFunctionName = "e";
                getNetworkByModelMethod = "yj";
        }
    }
}
