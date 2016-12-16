package me.veryyoung.wechat.luckymoney;


public class VersionParam {

    public static String receiveUIFunctionName = "d";
    public static String receiveUIParamName = "com.tencent.mm.v.k";
    public static String GET_LUCKY_MONEY_CLASS = "com.tencent.mm.model.ak";
    public static String getNetworkByModelMethod = "vw";
    public static String getMessageClass = "com.tencent.mm.e.b.by";

    public static void init(String version) {
        switch (version) {
            case "6.3.23":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.t.j";
                GET_LUCKY_MONEY_CLASS = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "vE";
                getMessageClass = "com.tencent.mm.e.b.bl";
                break;
            case "6.3.25":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.t.j";
                GET_LUCKY_MONEY_CLASS = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "vF";
                getMessageClass = "com.tencent.mm.e.b.bl";
                break;
            case "6.3.27":
                receiveUIFunctionName = "e";
                receiveUIParamName = "com.tencent.mm.u.k";
                GET_LUCKY_MONEY_CLASS = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "yj";
                getMessageClass = "com.tencent.mm.e.b.br";
                break;
            case "6.3.28":
                receiveUIFunctionName = "c";
                receiveUIParamName = "com.tencent.mm.v.k";
                GET_LUCKY_MONEY_CLASS = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "vP";
                getMessageClass = "com.tencent.mm.e.b.bu";
                break;
            case "6.3.30":
            case "6.3.31":
                receiveUIFunctionName = "c";
                receiveUIParamName = "com.tencent.mm.v.k";
                GET_LUCKY_MONEY_CLASS = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "vS";
                getMessageClass = "com.tencent.mm.e.b.bv";
                break;
            case "6.3.32":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.v.k";
                GET_LUCKY_MONEY_CLASS = "com.tencent.mm.model.ak";
                getNetworkByModelMethod = "vw";
                getMessageClass = "com.tencent.mm.e.b.by";
                break;
            default:
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.v.k";
                GET_LUCKY_MONEY_CLASS = "com.tencent.mm.model.ak";
                getNetworkByModelMethod = "vw";
                getMessageClass = "com.tencent.mm.e.b.by";
        }
    }
}
