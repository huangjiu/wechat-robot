package com.github.wechat.swing;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.blade.kit.base.Config;
import com.github.wechat.Wechat;
import com.github.wechat.WechatService;
import com.github.wechat.impl.WechatImpl;
import com.github.wechat.impl.WechatServiceImpl;

import me.biezhi.wechat.Constant;

public class WechatSwingTest {


	public static void main(String[] args) throws IOException {
		Constant.config = Config.load("classpath:config.properties");
		WechatService wechatService = new WechatServiceImpl();
		Wechat wechat = new WechatImpl(wechatService);
		
		WechatSwing window = new WechatSwing(wechat);
		window.login("100");
		window.close();
		
		window.login("200");
		window.close();
	}

}
