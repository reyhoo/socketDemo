package com.reyhoo.minitalk.component;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.reyhoo.minitalk.component.ClientAgent.RequestCallback;
import com.reyhoo.minitalk.dao.MessageDao;
import com.reyhoo.minitalk.entity.Message;
import com.reyhoo.minitalk.entity.Req;
import com.reyhoo.minitalk.entity.Resp;
import com.reyhoo.minitalk.entity.User;
import com.reyhoo.minitalk.util.CommonUtil;
import com.reyhoo.minitalk.util.GenerateUtil;
import com.reyhoo.minitalk.util.ThreadPool;

public class TalkServer {

	private static TalkServer sTalkServer = new TalkServer();

	public static TalkServer getInstance() {
		return sTalkServer;
	}

	private TalkServer() {
		threadPool = ThreadPool.getInstance();
		clientList = new ArrayList<>();
	}

	private ThreadPool threadPool;

	private ServerSocket serverSocket;

	private List<ClientAgent> clientList;

	public void startServer() throws Exception {
		if (serverSocket != null && serverSocket.isBound()) {
			throw new Exception("server is already start");
		}
		try {
			serverSocket = new ServerSocket(GlobalConfig.serverPort);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println(CommonUtil.getCurrTime() + " " + "TalkServer:: client connect:" + socket);
				ClientAgent agent = new ClientAgent(socket);
				addClient(agent);
				threadPool.execute(agent);
			}
		} finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}

	}

	public synchronized void addClient(ClientAgent agent) {
		clientList.add(agent);
		System.out.println(CommonUtil.getCurrTime() + " " + "TalkServer::addClient::list size:" + clientList.size());
	}

	public synchronized void removeClient(ClientAgent agent) {
		clientList.remove(agent);
		System.out.println(CommonUtil.getCurrTime() + " " + "TalkServer::removeClient::list size:" + clientList.size());
	}

	public synchronized Boolean containUser(User u) {
		if (u == null || u.getId() == null) {
			return false;
		}
		for (int i = 0; i < clientList.size(); i++) {
			ClientAgent agent = clientList.get(i);
			if (u.equals(agent.getUser())) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized void offlineOtherAgent(User u){
		if (u == null || u.getId() == null) {
			return;
		}
		for (int i = 0; i < clientList.size(); i++) {
			ClientAgent agent = clientList.get(i);
			if (u.equals(agent.getUser())) {
				agent.offlineUser();
			}
		}
	}
	
	public synchronized void sendMsg(Message msg){
		Integer userId = msg.getTo();
		if(userId == null)return;
		for (int i = 0; i < clientList.size(); i++) {
			ClientAgent agent = clientList.get(i);
			User user = agent.getUser();
			if(user == null){
				continue;
			}
			if (userId.equals(user.getId())) {
				Resp<Message>resp = new Resp<Message>();
				resp.type = CommandHandler.PUSH_MESSAGE;
				resp.content = msg;
				resp.requestId =GenerateUtil.getID();
				try {
					agent.responseClient(resp,new RequestCallback() {
						
						@Override
						public void onResponse(Req req) {
							Message msg = (Message) req.content;
							Integer msgId = msg.getId();
							if(msgId == null){
								return;
							}
							try {
								new MessageDao().updateMsgStatus(msgId, 1);
							} catch (Exception e) {
								// TODO: handle exception
							}
							
						}
						
						@Override
						public void onError() {
							
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
