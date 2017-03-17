package me.veryyoung.wechat.luckymoney;

/**
 * Created by veryyoung on 2017/3/17.
 */

public class LuckyMoneyMessage {

    private int msgType;

    private int channelId;

    private String sendId;

    private String nativeUrlString;

    private String talker;

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getNativeUrlString() {
        return nativeUrlString;
    }

    public void setNativeUrlString(String nativeUrlString) {
        this.nativeUrlString = nativeUrlString;
    }

    public String getTalker() {
        return talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }


    public LuckyMoneyMessage(int msgType, int channelId, String sendId, String nativeUrlString, String talker) {
        this.msgType = msgType;
        this.channelId = channelId;
        this.sendId = sendId;
        this.nativeUrlString = nativeUrlString;
        this.talker = talker;
    }

}
