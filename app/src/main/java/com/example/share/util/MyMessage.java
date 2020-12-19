package com.example.share.util;

import android.os.Message;

public class MyMessage {
    private static Message message;
    private MyMessage(){}
    public static Message getMsg(int msg){
        message = new Message();
        message.what = msg;
        return message;
    }
}
