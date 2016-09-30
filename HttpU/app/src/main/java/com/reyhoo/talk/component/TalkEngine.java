package com.reyhoo.talk.component;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.reyhoo.talk.App;
import com.reyhoo.talk.entity.Message;
import com.reyhoo.talk.entity.User;
import com.reyhoo.talk.exception.NoConnectionException;
import com.reyhoo.talk.mvp.model.UserAccountModel;
import com.reyhoo.talk.util.ByteUtil;
import com.reyhoo.talk.util.GlobalConst;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/7/14.
 */
public class TalkEngine {


    //    private static final String SERVER_IP = "192.168.1.14";
    private static final String SERVER_IP = "172.16.26.49";
    private static final int SERVER_PORT = 9999;
    private static final long HEART_TIMEOUT = 10000;

    private static final int READ_TIMEOUT = 10000;

    private String mobile = null;
    private String password = null;
    private long lastHeartTime = 0l;
    private static TalkEngine instance = new TalkEngine();

    private Map<Req, RequestCallback> sentReq;

    public static TalkEngine getInstance() {
        return instance;
    }

    private static App context;

    private TalkEngine() {
        context = App.instance;
        sentReq = new ConcurrentHashMap<>();
    }

    public void connectServer() {
        context.getHandler().post(ConnectionTask);
        reader.start();
    }

    private Socket mSocket;

    private boolean isNetworkAvailable;
    private boolean isConnected;


    public synchronized boolean isConnected() {
        return isConnected;
    }

    public synchronized void setConnected(boolean connected) {
        isConnected = connected;
    }

    private synchronized boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    private synchronized void setNetworkAvailable(boolean networkAvailable) {
        isNetworkAvailable = networkAvailable;
    }


    public synchronized void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public synchronized String getMobile() {
        return mobile;
    }

    public synchronized void setPassword(String password) {
        this.password = password;
    }

    public synchronized String getPassword() {
        return password;
    }

    public void closeSocket() {
        synchronized (ConnectionTask) {
            try {
                mSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setConnected(false);
        UserAccountModel.setLogin(false);
        notifyAllRequestConnectionClosed();
    }

    private synchronized void notifyAllRequestConnectionClosed() {
        Iterator<Map.Entry<Req, RequestCallback>> it = sentReq.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Req, RequestCallback> entry = it.next();
            if (entry.getValue() != null) {
                entry.getValue().onConnectionClosed();
            }
            it.remove();
        }
    }

    private void startWait(final Integer requestId) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(READ_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                notifyRequest(requestId);
            }
        });
    }

    private synchronized void notifyRequest(Integer requestId, Resp<Object> resp) {
        if (requestId == null) {
            return;
        }
        Req req = new Req(requestId);
        RequestCallback callback = sentReq.remove(req);
        if (callback != null) {
            callback.onReceive(resp);
        }
    }

    private synchronized void notifyRequest(Integer requestId) {
        if (requestId == null) {
            return;
        }
        Req req = new Req(requestId);
        RequestCallback callback = sentReq.remove(req);
        if (callback != null) {
            callback.onReadTimeout();
        }
    }

    private synchronized void addRequest(Req req, RequestCallback callback) {
        sentReq.put(req, callback);
    }

    public void sendData(Req req, RequestCallback callback) {
        context.getHandler().post(new SendDataTask(req, callback));
    }

    public void sendData(Req req) {
        sendData(req, null);
    }

    private class SendDataTask implements Runnable {

        private Req data;
        private String jsonData;
        private RequestCallback callback;

        public SendDataTask(Req req, RequestCallback callback) {
            this.data = req;
            jsonData = new Gson().toJson(req);
            this.callback = callback;
        }

        @Override
        public void run() {

            if (isConnected()) {
                send();
            } else {
                Log.i("TalkEngine", "TalkEngine::SendDataTask::no connection:" + jsonData);
                if (callback != null) {
                    callback.onSendException(new NoConnectionException("no connection"));
                }
            }

            if (GlobalConst.CMD_OFFLINE.equals(data.type)) {
                closeSocket();
            }

        }

        private void send() {
            synchronized (ConnectionTask) {
                try {
                    OutputStream out = mSocket.getOutputStream();
                    byte[] sendData = null;
                    sendData = jsonData.getBytes("utf-8");
                    byte[] lenData = ByteUtil.intToByte(sendData.length);
                    out.write(lenData);
                    out.write(sendData);
                    out.flush();
                    Log.i("TalkEngine", "TalkEngine::SendDataTask::send Success:" + jsonData);
                    if (callback != null) {
                        callback.onSent(data);
                        if (data.id != null) {
                            addRequest(data, callback);
                            startWait(data.id);
                        }
                    }

                } catch (Exception e) {
                    Log.e("TalkEngine", "TalkEngine::SendDataTask::send Error:" + jsonData + ";e:" + e);
                    e.printStackTrace();
                    closeSocket();
                    if (callback != null) {
                        callback.onSendException(e);
                    }
                }
            }
        }


    }

    private Runnable ConnectionTask = new Runnable() {
        @Override
        public void run() {

            Boolean isConnected = tryConnect();
            Log.i("TalkEngine", "TalkEngine::ConnectionTask::isConnected:" + isConnected);
            if (isConnected == null) {
                Req req = new Req(null);
                req.type = GlobalConst.CMD_HEART;
                sendData(req, null);
                tryLogin();
            } else {
                if (isConnected) {
                    //刚刚建立连接更新心跳时间戳
                    setLastHeartTime(System.currentTimeMillis());
                    setConnected(isConnected);
                    synchronized (ConnectionTask) {
                        try {
                            ConnectionTask.notifyAll();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    tryLogin();
                }

            }

            long now = System.currentTimeMillis();
            context.getHandler().postDelayed(ConnectionTask, 3000);
            if (isConnected() && now - getLastHeartTime() >= HEART_TIMEOUT) {
                Req req = new Req(null);
                req.type = GlobalConst.CMD_OFFLINE;
                sendData(req, null);
            }
        }
    };

    public Thread reader = new Thread() {
        @Override
        public void run() {
            while (true) {
                InputStream in = null;
                synchronized (ConnectionTask) {
                    while (!isConnected()) {
                        try {
                            ConnectionTask.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("TalkEngine", "TalkEngine::reader::isConnected:" + mSocket.isConnected());
                    try {
                        in = mSocket.getInputStream();
                    } catch (Exception e) {
                        e.printStackTrace();
                        closeSocket();
                    }
                }
                if (in != null) {
                    Log.i("TalkEngine", "TalkEngine::reader::startRead:");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        byte[] buf = new byte[1024 * 3];
                        int count;
                        while ((count = in.read(buf)) != -1) {
                            baos.write(buf, 0, count);
                            handleData(baos);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        baos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("TalkEngine", "TalkEngine::reader::startEnd:");

                    closeSocket();


                }

            }
        }
    };

    /****
     * @return 返回是true连接成功，返回是false连接失败，返回是null 已经有连接无需再连
     */

    private Boolean tryConnect() {
        synchronized (ConnectionTask) {
            if (!isConnected()) {
                mSocket = new Socket();
                try {
                    mSocket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), 10000);
                    return mSocket.isConnected();
                } catch (Exception e) {
                    e.printStackTrace();
                    closeSocket();
                    return false;
                }
            }
            return null;
        }

    }


    private UserAccountModel.UserAccountModelCallback loginCallback = new UserAccountModel.UserAccountModelCallback() {
        @Override
        public void onEvent(int type, Object obj) {
            switch (type){
                case LOGIN_FAIL_DATA_ERROR:
//                    App.instance.sendBroadcast(new Intent(GlobalConst.BROADCAST_ACTION_AUTO_LOGIN_ERROR));
//                    Toast.makeText(App.instance, "", Toast.LENGTH_SHORT).show();
                    App.showOfflineDialog("自动登录身份过期，请重新登录");
                    break;

            }
        }
    };
    private void tryLogin() {
        User u = UserAccountModel.getLoginUser();
        if (!UserAccountModel.isLogin() && u != null) {
            new UserAccountModel().loginAuto(u.getMobile(),u.getPassword(),loginCallback);
        }
    }


    private boolean handleData(ByteArrayOutputStream baos) throws Exception {
        byte[] data = baos.toByteArray();
        if (data.length <= 4) {
            return false;
        }
        int len = ByteUtil.bytesToInt(data);
//        System.out.println("len" + len);
        if (len == data.length - 4) {
            baos.reset();
            byte[] realData = new byte[len];
            System.arraycopy(data, 4, realData, 0, len);
            handleCMD(realData);
            return true;
        }

        if (len < data.length - 4) {
            baos.reset();
            byte[] realData = new byte[len];
            System.arraycopy(data, 4, realData, 0, len);
            handleCMD(realData);
            byte[] remainData = new byte[data.length - 4 - len];
            System.arraycopy(data, 4 + len, remainData, 0, remainData.length);
            baos.write(remainData);
            return handleData(baos);
        }
        return false;
    }

    private void handleCMD(final byte[] data) throws Exception {
        String jsonStr = new String(data, "utf-8");
        Log.i("TalkEngine", "TalkEngine::handleCMD::jsonStr:" + jsonStr);
        Resp<Object> resp = new Gson().fromJson(jsonStr, new TypeToken<Resp<Object>>() {
        }.getType());
        boolean needNotify = true;

        if (GlobalConst.CMD_HEART.equals(resp.type)) {
            setLastHeartTime(System.currentTimeMillis());
            resp = new Gson().fromJson(jsonStr, new TypeToken<Resp<String>>() {
            }.getType());
        } else if (GlobalConst.CMD_OFFLINE.equals(resp.type)) {
            closeSocket();
        } else if (GlobalConst.CMD_LOGIN.equals(resp.type)) {
            resp = new Gson().fromJson(jsonStr, new TypeToken<Resp<User>>() {
            }.getType());
        }else if(GlobalConst.PUSH_FRIEND_LIST.equals(resp.type)){


        }else if(GlobalConst.PUSH_LOGOUT.equals(resp.type)){
            needNotify = false;
            UserAccountModel.clearLoginUser();
            UserAccountModel.setLogin(false);
            App.instance.sendBroadcast(new Intent(GlobalConst.BROADCAST_ACTION_PUSH_LOGOUT));
        }else if(GlobalConst.PUSH_MESSAGE.equals(resp.type)){
            needNotify = false;
            resp = new Gson().fromJson(jsonStr, new TypeToken<Resp<Message>>() {
            }.getType());
            Intent intent = new Intent(GlobalConst.BROADCAST_ACTION_PUSH_MSG);
            intent.putExtra(GlobalConst.INTENT_EXTRA_MSG_KEY,(Message)resp.content);
            App.instance.sendBroadcast(intent);

            //response push
            Req req = new Req(null);
            req.id = resp.requestId;
            req.type = resp.type;
            Message msg = new Message();
            msg.setId(((Message)resp.content).getId());
            req.content = msg;
            sendData(req);

        }
        if(needNotify){
            notifyRequest(resp.requestId, resp);
        }

    }

    public synchronized void setLastHeartTime(long lastHeartTime) {
        this.lastHeartTime = lastHeartTime;
    }

    public synchronized long getLastHeartTime() {
        return lastHeartTime;
    }

    public interface RequestCallback {
        void onSent(Req req);

        void onSendException(Exception e);

        void onConnectionClosed();

        void onReadTimeout();

        void onReceive(Resp resp);
    }

//    private class RequestAndCallback{
//        Req req;
//        RequestCallback callback;
//        long time;
//        RequestAndCallback(Req req,RequestCallback callback){
//            this.req = req;
//            this.callback = callback;
//            time = System.currentTimeMillis();
//            ThreadPool.getInstance().execute(new Runnable() {
//                @Override
//                public void run() {
//                    while(true){
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        if(System.currentTimeMillis()-time>=READ_TIMEOUT){
//                            RequestAndCallback requestAndCallback = sentReq.get(RequestAndCallback.this.req);
//                            if(requestAndCallback!=null){
//                                RequestCallback callback1 = requestAndCallback.callback;
//
//                            }
//
//                            RequestAndCallback.this.callback.onReadTimeout();
//                            break;
//                        }
//                    }
//                }
//            });
//        }
//    }
}
