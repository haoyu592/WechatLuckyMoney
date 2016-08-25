package me.veryyoung.wechat.luckymoney;


public class VersionParam {

    public static String getNetworkByModelMethod;

    public static void init(String version) {
        switch (version) {
            case "6.3.23":
                getNetworkByModelMethod = "vE";
                break;
            case "6.3.25":
                getNetworkByModelMethod = "vF";
                break;
            default:
                getNetworkByModelMethod = "vF";
        }
    }
}
