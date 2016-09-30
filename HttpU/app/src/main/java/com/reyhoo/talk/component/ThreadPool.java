package com.reyhoo.talk.component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

	private static ThreadPool instance = new ThreadPool();

	public static ThreadPool getInstance() {
		return instance;
	}

	private ThreadPool() {

	}

	private ExecutorService executorService;

	public synchronized ExecutorService getExecutorService() {
		if (executorService == null || executorService.isShutdown() || executorService.isTerminated()) {
			executorService = Executors.newCachedThreadPool();
		}
		return executorService;
	}

	public void execute(Runnable task) {
		if (task == null)
			return;
		ExecutorService executorService = getExecutorService();
		executorService.execute(task);
	}

}
