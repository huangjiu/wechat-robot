package com.github.wechat.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.kit.json.JSONObject;
import com.github.wechat.User;
import com.github.wechat.Wechat;
import com.github.wechat.WechatListener;
import com.github.wechat.WechatService;

import me.biezhi.wechat.Constant;
import me.biezhi.wechat.exception.WechatException;
import me.biezhi.wechat.model.WechatContact;
import me.biezhi.wechat.model.WechatMeta;
import me.biezhi.wechat.util.Matchers;

public class WechatImpl implements Wechat {

	private static final Logger LOGGER = LoggerFactory.getLogger(WechatImpl.class);
	private WechatService wechatService;

	public WechatImpl(WechatService wechatService) {
		super();
		this.wechatService = wechatService;
	}

	@Override
	public User init(String uid) {
		String uuid = wechatService.getUuid();
		byte[] images = wechatService.getImage(uuid);
		User user = new User(uid, uuid);
		user.setImages(images);
		user.setListener(new WechatListenerSupport());
		return user;
	}

	@Override
	public void login(User user) {
		while (this.waitingForLogin(user)) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void start(User user) {
		
		WechatMeta meta = user.getMeta();
		wechatService.getRedirectInfo(meta);
		user.buildBaseRequest();
		
		LOGGER.info("微信登录成功");
		LOGGER.info("微信初始化...");
		wechatService.wxInit(meta);
		LOGGER.info("微信初始化成功");

		LOGGER.info("开启状态通知...");
		wechatService.openStatusNotify(meta);
		LOGGER.info("开启状态通知成功");

		LOGGER.info("获取联系人...");
		WechatContact contact = wechatService.getContact(meta);
		user.setContact(contact);
		LOGGER.info("获取联系人成功");
		LOGGER.info("共有 {} 位联系人", contact.getContactList().size());
		user.start();
	}

	/**
	 * 等待登录
	 */
	private boolean waitingForLogin(User user) throws WechatException {
		String res = wechatService.waitForLogin(user.getUuid());
		if (null == res) {
			throw new WechatException("扫描二维码验证失败");
		}
		String code = Matchers.match("window.code=(\\d+);", res);
		if (null == code) {
			throw new WechatException("扫描二维码验证失败");
		} else {
			if (code.equals("200")) {
				String pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res);
				String redirect_uri = pm + "&fun=new";
				user.getMeta().setRedirect_uri(redirect_uri);
				String base_uri = redirect_uri.substring(0, redirect_uri.lastIndexOf("/"));
				user.getMeta().setBase_uri(base_uri);
			} else if (code.equals("408")) {
				throw new WechatException("登录超时");
			}
		}
		return !Constant.HTTP_OK.equals(code);
	}

	class WechatListenerSupport implements WechatListener {
		int playWeChat = 0;
		
		@Override
		public void start(final User user) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					wechatService.choiceSyncLine(user.getMeta());
					while (true) {
						int[] arr = wechatService.syncCheck(user.getMeta());
						LOGGER.info("retcode={}, selector={}", arr[0], arr[1]);

						if (arr[0] == 1100) {
							LOGGER.info("你在手机上登出了微信，债见");
							break;
						}
						if (arr[0] == 0) {
							if (arr[1] == 2) {
								JSONObject data = wechatService.webwxsync(user.getMeta());
								wechatService.handleMsg(user, data);
							} else if (arr[1] == 6) {
								JSONObject data = wechatService.webwxsync(user.getMeta());
								wechatService.handleMsg(user, data);
							} else if (arr[1] == 7) {
								playWeChat += 1;
								LOGGER.info("你在手机上玩微信被我发现了 {} 次", playWeChat);
								wechatService.webwxsync(user.getMeta());
							} else if (arr[1] == 3) {
								continue;
							} else if (arr[1] == 0) {
								continue;
							}
						} else {
							//
						}
						try {
							LOGGER.info("等待2000ms...");
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}, "wechat-listener-thread").start();
		}
	}

}
