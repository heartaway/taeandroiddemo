package com.taobao.tae.buyingdemo.push;

import android.content.Context;
import com.alibaba.cchannel.push.receiver.CPushMessage;
import com.alibaba.cchannel.push.receiver.CPushMessageReceiver;

import java.io.UnsupportedEncodingException;

/**
 * Created by xinyuan on 14/9/8.
 */
public class MessageReciever extends CPushMessageReceiver {

    /**
     * 接收到消息命令的处理
     *
     * @param context
     * @param message
     */
    protected void onMessage(Context context, CPushMessage message) {
        try {
            String commandContext = new String(message.getContext(), "UTF-8");
            System.out.println("接收到消息命令："+commandContext);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    protected void onNotification(android.content.Context context, long messageId, java.lang.String title, java.lang.String summary, java.util.Map<java.lang.String,java.lang.String> extraMap) {
        System.out.println("接收到通知："+title);
    }

}
