package com.github.wechat;

import static org.junit.Assert.*;

import org.junit.Test;

import com.blade.kit.base.Config;
import com.github.wechat.impl.WechatImpl;
import com.github.wechat.impl.WechatServiceImpl;

import me.biezhi.wechat.Constant;

public class WechatTest {

	@Test
	public void testInit() {
		
		Constant.config = Config.load("classpath:config.properties");
		WechatService wechatService = new WechatServiceImpl();
		Wechat wechat = new WechatImpl(wechatService);
		
		User user = wechat.init("100");
		wechat.login(user);
		wechat.start(user);
	}

	@Test
	public void testLogin() {
		fail("Not yet implemented");
	}

	@Test
	public void testStart() {
		fail("Not yet implemented");
	}

}
