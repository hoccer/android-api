package com.hoccer.api.android;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.hoccer.api.BadModeException;
import com.hoccer.api.ClientActionException;
import com.hoccer.api.ClientConfig;
import com.hoccer.api.CollidingActionsException;
import com.hoccer.api.Linccer;

public class AsyncLinccer extends Linccer {

    public static class MessageType {
        public final static int SUCCSESS          = 0;
        public final static int FAILURE           = 1;
        public final static int BAD_MODE          = -1;
        public final static int BAD_CLIENT_ACTION = -2;
        public final static int COLLISION         = -3;
        public final static int UNKNOWN_EXCEPTION = -4;
    }

    public AsyncLinccer(ClientConfig config) {
        super(config);
    }

    public void asyncShare(final String mode, final JSONObject payload, final Handler handler) {
        new Thread(new Runnable() {
            public void run() {

                Message msg = handler.obtainMessage();
                try {
                    msg.obj = share(mode, payload);

                    if (msg.obj != null) {
                        msg.what = MessageType.SUCCSESS;
                    } else {
                        msg.what = MessageType.FAILURE;
                    }
                } catch (BadModeException e) {
                    msg.what = MessageType.BAD_MODE;
                    msg.obj = e;
                } catch (ClientActionException e) {
                    msg.what = MessageType.BAD_CLIENT_ACTION;
                    msg.obj = e;
                } catch (CollidingActionsException e) {
                    msg.what = MessageType.COLLISION;
                    msg.obj = e;
                } catch (Exception e) {
                    msg.what = MessageType.UNKNOWN_EXCEPTION;
                    msg.obj = e;
                }

                handler.handleMessage(msg);
            }
        }).start();
    }
}
