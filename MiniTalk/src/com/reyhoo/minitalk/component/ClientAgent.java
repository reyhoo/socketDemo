package com.reyhoo.minitalk.component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.reyhoo.minitalk.entity.Message;
import com.reyhoo.minitalk.entity.Req;
import com.reyhoo.minitalk.entity.Resp;
import com.reyhoo.minitalk.entity.User;
import com.reyhoo.minitalk.entity.UserInfo;
import com.reyhoo.minitalk.util.ByteUtil;
import com.reyhoo.minitalk.util.CommonUtil;
import com.reyhoo.minitalk.util.ThreadPool;

public class ClientAgent implements Runnable {

	private static final long HEART_TIMEOUT = 10000;

	private static final long READ_TIMEOUT = 10000;

	private Map<Integer, RequestCallback> mCallbacks = new HashMap<Integer, ClientAgent.RequestCallback>();
	private Socket mSocket;

	private UserInfo mUserInfo;

	private long lastHeartTime = 0l;

	private boolean isRuning;

	public ClientAgent(Socket socket) {
		this.mSocket = socket;
		lastHeartTime = System.currentTimeMillis();
		isRuning = true;
		ThreadPool.getInstance().execute(new CheckHeartTask());
	}

	// public Socket getSocket() {
	// return mSocket;
	// }

	public synchronized User getUser() {
		if (mUserInfo == null)
			return null;
		return mUserInfo.getUser();
	}

	public synchronized UserInfo getUserInfo() {
		return mUserInfo;
	}

	public synchronized void setUserInfo(UserInfo u) {
		mUserInfo = u;
	}

	public synchronized void responseClient(Object obj) throws Exception {
		responseClient(obj, null);
	}

	private void startWait(final Integer id) {
		ThreadPool.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(READ_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				callbackError(id);
			}
		});
	}

	public String getTag(){
		User u = getUser();
		if(u == null){
			return ((InetSocketAddress)this.getSocket().getRemoteSocketAddress()).getHostString();
		}
		return u.getMobile()+"";
	}
	public synchronized void responseClient(Object obj, RequestCallback callback)
			throws Exception {
		try {
			byte[] data = new Gson().toJson(obj).getBytes("utf-8");
			byte[] lenData = ByteUtil.intToByte(data.length);
			OutputStream out = mSocket.getOutputStream();
			out.write(lenData);
			out.write(data);
			out.flush();
			if(!new Gson().toJson(obj).contains(CommandHandler.CMD_HEART)){
				System.out.println(CommonUtil.getCurrTime() + " "+getTag()
						+ " response::success:" + new Gson().toJson(obj));
			}
			if (callback != null) {
				if (obj instanceof Resp) {
					Resp resp = (Resp) obj;
					if (resp.requestId != null) {
						mCallbacks.put(resp.requestId, callback);
						startWait(resp.requestId);
					}
				}

			}
		} catch (Exception e) {
			System.err.println(CommonUtil.getCurrTime() + " "+getTag()
					+ " response::error:" + new Gson().toJson(obj) + ";e:"
					+ e);
			try {
				mSocket.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw e;
		}

	}

	@Override
	public void run() {
		try {
			InputStream in;
			synchronized (this) {
				in = mSocket.getInputStream();
			}
			byte[] buf = new byte[1024 * 3];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int count;
			while ((count = in.read(buf)) != -1) {
				baos.write(buf, 0, count);
				handleData(baos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mSocket != null) {
				try {
					mSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		setRuning(false);
		TalkServer.getInstance().removeClient(this);
		callbackAllRequestError();
		// TODO
		// 通知好友下线
	}

	private boolean handleData(ByteArrayOutputStream baos) throws Exception {
		byte[] data = baos.toByteArray();
		if (data.length <= 4) {
			return false;
		}
		int len = ByteUtil.bytesToInt(data);
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
		new CommandHandler().handleCMD(this, data);
	}

	public synchronized void updateHeartTime() {
		lastHeartTime = System.currentTimeMillis();
	}

	public synchronized boolean isRuning() {
		return isRuning;
	}

	public synchronized void setRuning(boolean isRuning) {
		this.isRuning = isRuning;
	}

	private class CheckHeartTask implements Runnable {

		@Override
		public void run() {
			while (isRuning()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (ClientAgent.this) {
					if (System.currentTimeMillis() - lastHeartTime >= HEART_TIMEOUT) {
						setRuning(false);
						System.out.println(CommonUtil.getCurrTime() + " "
								+ "CheckHeartTask::" + mSocket
								+ " heart timeout");
						Resp<Object> resp = new Resp<Object>();
						resp.type = CommandHandler.CMD_OFFLINE;
						try {
							responseClient(resp);
						} catch (Exception e) {
							e.printStackTrace();
						}
						close();
					}
				}
			}

		}

	}

	public synchronized void close() {
		try {
			mSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized Socket getSocket() {
		return mSocket;
	}

	public void offlineUser() {
		Req<String> req = new Req<String>();
		req.type = CommandHandler.PUSH_LOGOUT;
		try {
			responseClient(req);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setUserInfo(null);
		close();
	}

	public interface RequestCallback {

		void onError();

		void onResponse(Req req);
	}

	public synchronized void callback(Req req) {
		Integer id = req.id;
		if (id != null) {
			RequestCallback callback = mCallbacks.remove(id);
			if (callback != null) {
				callback.onResponse(req);
			}
		}
	}

	private synchronized void callbackError(Integer requestId) {
		if (requestId == null) {
			return;
		}
		RequestCallback callback = mCallbacks.remove(requestId);
		if (callback != null) {
			callback.onError();
		}
	}

	private synchronized void callbackAllRequestError(){
		Iterator<Integer> it = mCallbacks.keySet().iterator();
		if(it.hasNext()){
			Integer id = it.next();
			RequestCallback callback = mCallbacks.get(id);
			if (callback != null) {
				callback.onError();
			}
			it.remove();
		}
	}
}
