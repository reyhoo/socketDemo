package com.reyhoo.minitalk.component;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.reyhoo.minitalk.biz.UserBiz;
import com.reyhoo.minitalk.dao.FriendDao;
import com.reyhoo.minitalk.dao.MessageDao;
import com.reyhoo.minitalk.entity.Friend;
import com.reyhoo.minitalk.entity.Message;
import com.reyhoo.minitalk.entity.Req;
import com.reyhoo.minitalk.entity.Resp;
import com.reyhoo.minitalk.entity.User;
import com.reyhoo.minitalk.entity.UserInfo;
import com.reyhoo.minitalk.exception.BusniessException;
import com.reyhoo.minitalk.util.CommonUtil;
import com.reyhoo.minitalk.util.ThreadPool;

public class CommandHandler {
	public static final String CMD_LOGIN = "CMD_LOGIN";
	public static final String CMD_REGIST = "CMD_REGIST";
	public static final String CMD_HEART = "CMD_HEART";
	public static final String CMD_OFFLINE = "CMD_OFFLINE";
	public static final String CMD_SENDMSG = "CMD_SENDMSG";

	public static final String PUSH_MESSAGE = "PUSH_MESSAGE";
	public static final String PUSH_FRIEND_LIST = "PUSH_FRIEND_LIST";
	public static final String PUSH_LOGOUT = "PUSH_LOGOUT";

	public void handleCMD(ClientAgent agent, byte[] data) throws Exception {
		ThreadPool.getInstance().execute(new HandlerTask(agent, data));
	}

	private class HandlerTask implements Runnable {
		private byte[] data;
		private ClientAgent agent;

		public HandlerTask(ClientAgent agent, byte[] data) {
			this.data = data;
			this.agent = agent;
		}

		@Override
		public void run() {
			String s;
			try {
				s = new String(data, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}

			Req<Object> req = new Gson().fromJson(s,
					new TypeToken<Req<Object>>() {
					}.getType());

			if (!CMD_HEART.equals(req.type)) {
				System.out.println(CommonUtil.getCurrTime() + " "+agent.getTag()
						+ " request:" + s);
			}
			if (CMD_LOGIN.equals(req.type)) {
				login(s);
			} else if (CMD_REGIST.equals(req.type)) {
				// login(s);
			} else if (CMD_HEART.equals(req.type)) {

				agent.updateHeartTime();
				Resp<String> resp = new Resp<String>();
				resp.requestId = req.id;
				resp.type = req.type;
				resp.content = "server response";
				try {
					agent.responseClient(resp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (CMD_OFFLINE.equals(req.type)) {
				try {
					agent.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (CMD_SENDMSG.equals(req.type)) {
				sendMsg(s);
			} else if (PUSH_MESSAGE.equals(req.type)) {
				callbackPushMsgResponse(s);
			}
		}

		private void callbackPushMsgResponse(String content) {
			Req<Message> req = new Gson().fromJson(content,
					new TypeToken<Req<Message>>() {
					}.getType());
			agent.callback(req);
		}

		private void sendMsg(String content) {
			Req<Message> req = new Gson().fromJson(content,
					new TypeToken<Req<Message>>() {
					}.getType());
			Message msg = req.content;
			Resp<Object> resp = new Resp<Object>();
			resp.requestId = req.id;
			resp.type = req.type;
			boolean isSuccess = false;
			if (agent.getUser() == null) {
				resp.errCode = "0001";
				resp.errMsg = "用户未登录";
			} else {
				if (agent.getUser().getId().equals(msg.getFrom())) {
					isSuccess = true;
					resp.errCode = "0000";
					resp.errMsg = "发送成功";
				} else {
					resp.errCode = "0001";
					resp.errMsg = "参数非法";
				}

			}

			try {
				agent.responseClient(resp);
				if (isSuccess) {
					Integer msgId = null;
					try {
						msgId = new MessageDao().add(msg.getFrom(),
								msg.getTo(), msg.getMsgBody());
						msg.setId(msgId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					TalkServer.getInstance().sendMsg(msg);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void login(String content) {

			Req<User> req = new Gson().fromJson(content,
					new TypeToken<Req<User>>() {
					}.getType());
			// System.out.println(req.content);
			User u = null;
			Resp<User> resp = new Resp<User>();
			resp.requestId = req.id;
			resp.type = req.type;
			boolean isSuccess = false;
			try {
				// if(agent.getUser() !=null){
				// throw new BusniessException("重复登录1");
				// }
				u = new UserBiz().login(req.content);
				TalkServer.getInstance().offlineOtherAgent(u);

				// if(TalkServer.getInstance().containUser(u)){
				// throw new BusniessException("重复登录2");
				// }
				resp.errCode = "0000";
				resp.errMsg = "登录成功";
				resp.content = u;
				isSuccess = true;
			} catch (Exception e1) {
				e1.printStackTrace();
				if (e1 instanceof BusniessException) {
					resp.errCode = "0001";
					resp.errMsg = e1.getMessage();
				} else {
					resp.errCode = "0002";
					resp.errMsg = "系统异常";
				}

			}
			try {
				agent.responseClient(resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isSuccess) {
				UserInfo userinfo = new UserInfo();
				userinfo.setUser(u);
				try {
					userinfo.setFriends(new HashSet<Friend>(new FriendDao()
							.getFriendsByOwnerId(u.getId())));
				} catch (Exception e) {
					e.printStackTrace();
				}
				agent.setUserInfo(userinfo);
				Resp<Set<Friend>> res = new Resp<>();
				res.type = PUSH_FRIEND_LIST;
				res.content = userinfo.getFriends();
				try {
					agent.responseClient(res);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

}
