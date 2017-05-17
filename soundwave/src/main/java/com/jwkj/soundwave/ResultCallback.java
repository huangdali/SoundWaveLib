package com.jwkj.soundwave;

import com.hdl.udpsenderlib.UDPResultCallback;

/**
 * 结果回调
 * Created by jwkj on 2017/5/17.
 */

public abstract class ResultCallback extends UDPResultCallback {
    /**
     * 当声波发送停止的时候回调
     */
    public abstract void onStopSend();
}
