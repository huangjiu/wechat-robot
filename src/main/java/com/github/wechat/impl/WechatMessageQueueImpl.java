package com.github.wechat.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.wechat.User;
import com.github.wechat.WechatMessageQueue;
import com.github.wechat.WechatService;

public class WechatMessageQueueImpl implements WechatMessageQueue {

	private List<User> users;
	private WechatService wechatService;
	private ScheduledExecutorService scheduledExecutor;

	public WechatMessageQueueImpl(WechatService wechatService) {
		super();
		this.users = new CopyOnWriteArrayList<User>();
		this.wechatService = wechatService;
		this.scheduledExecutor = Executors.newScheduledThreadPool(20);
	}

	@Override
	public void add(User user) {
		this.users.add(user);
		this.scheduledExecutor.scheduleAtFixedRate( new MessageTask() , 100, 200, TimeUnit.MILLISECONDS);
	}
	
	class MessageTask implements Runnable {
		
		private User user ;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
