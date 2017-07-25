package com.github.wechat.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.kit.json.JSONObject;
import com.github.wechat.User;
import com.github.wechat.Wechat;
import com.github.wechat.WechatListener;
import com.github.wechat.WechatService2;

import me.biezhi.wechat.Constant;
import me.biezhi.wechat.exception.WechatException;
import me.biezhi.wechat.model.WechatMeta;
import me.biezhi.wechat.util.Matchers;

public class WechatSupport implements Wechat {
	private static final Logger LOGGER = LoggerFactory.getLogger(WechatSupport.class);
	private WechatService2 wechatService;

	@Override
	public User login(String uid) {
		String uuid = wechatService.getUuid();
		byte[] images = wechatService.getImage(uuid);
		User user = new User(uid, uuid);
		user.setImages(images);
		user.setListener(new WechatListenerSupport());
		return user;
	}

	@Override
	public void waitForLogin(User user) {
		while (!this.waitingForLogin(user)) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void start(User user) {

		user.getListener().start(user.getMeta());
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
		public void start(final WechatMeta meta) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					wechatService.choiceSyncLine(meta);
					while (true) {
						int[] arr = wechatService.syncCheck(meta);
						LOGGER.info("retcode={}, selector={}", arr[0], arr[1]);

						if (arr[0] == 1100) {
							LOGGER.info("你在手机上登出了微信，债见");
							break;
						}
						if (arr[0] == 0) {
							if (arr[1] == 2) {
								JSONObject data = wechatService.webwxsync(meta);
								wechatService.handleMsg(meta, data);
							} else if (arr[1] == 6) {
								JSONObject data = wechatService.webwxsync(meta);
								wechatService.handleMsg(meta, data);
							} else if (arr[1] == 7) {
								playWeChat += 1;
								LOGGER.info("你在手机上玩微信被我发现了 {} 次", playWeChat);
								wechatService.webwxsync(meta);
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
