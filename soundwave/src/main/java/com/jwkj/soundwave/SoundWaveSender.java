package com.jwkj.soundwave;

import android.content.Context;
import android.util.Log;

import com.hdl.udpsenderlib.UDPReceiver;
import com.hdl.udpsenderlib.UDPResult;
import com.hdl.udpsenderlib.UDPResultCallback;
import com.jwkj.soundwave.bean.NearbyDevice;
import com.lsemtmf.genersdk.tools.emtmf.EMTMFSDK;
import com.lsemtmf.genersdk.tools.emtmf.EMTMFSDKListener;

/**
 * 声波发送器
 * Created by jwkj on 2017/5/17.
 */

public class SoundWaveSender {
    private String ssid;
    private String pwd;
    private static SoundWaveSender sender;
    private Context mContext;
    //接收端口，默认为9988
    private int port = 9988;
    private ResultCallback callback;

    private SoundWaveSender() {
    }

    public static SoundWaveSender getInstance() {
        if (sender == null) {
            sender = new SoundWaveSender();
        }
        return sender;
    }

    public SoundWaveSender with(Context context) {
        this.mContext = context;
        EMTMFSDK.getInstance(context).setListener(emtmfsdkListener);//设置监听器
        return this;
    }

    /**
     * 设置端口
     *
     * @param port
     * @return
     */
    public SoundWaveSender setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * 设置wifi信息
     *
     * @param ssid wifi名
     * @param pwd  wifi密码
     * @return
     */
    public SoundWaveSender setWifiSet(String ssid, String pwd) {
        this.ssid = ssid;
        this.pwd = pwd;
        return this;
    }

    /**
     * 开始发送了
     *
     * @param callback
     */
    public void send(ResultCallback callback) {
        this.callback = callback;
        EMTMFSDK.getInstance(mContext).sendWifiSet(mContext, ssid, pwd);//发送声波--传入wifi名字和wifi密码
        UDPReceiver.getInstance().with(mContext).setPort(port).receive(callback);//开始接收数据
    }

    UDPResultCallback ca = new UDPResultCallback() {
        @Override
        public void onNext(UDPResult udpResult) {
            Log.e("hdl", udpResult.toString());
            NearbyDevice device = NearbyDevice.getDeviceInfoByByteArray(udpResult.getResultData());
            device.setIp(udpResult.getIp());
            Log.e("hdl", "收到结果了------" + device);
            UDPReceiver.getInstance().stopReceive();
            EMTMFSDK.getInstance(mContext).stopSend();
        }

        @Override
        public void onError(Throwable throwable) {
            Log.e("hdl", "出错了" + throwable);
        }
    };

    /**
     * 停止发送
     *
     * @return
     */
    public SoundWaveSender stopSend() {
        EMTMFSDK.getInstance(mContext).stopSend();//停止发送声波
        UDPReceiver.getInstance().stopReceive();//停止接收数据
        return this;
    }

    /**
     * 声波发送监听
     */
    private EMTMFSDKListener emtmfsdkListener = new EMTMFSDKListener() {

        public void didEndOfPlay() {//第一次声波发送完成了
            callback.onStopSend();
        }
    };
}
