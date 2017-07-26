package com.github.wechat.swing;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;

import com.github.wechat.User;
import com.github.wechat.Wechat;

import me.biezhi.wechat.ui.QRCodeFrame;

public class WechatSwing {

	private QRCodeFrame qrCodeFrame;
	private Wechat wechat;

	public WechatSwing(Wechat wechat) {
		super();
		this.wechat = wechat;
	}

	public User login(String uid) throws IOException {
		User user = wechat.init("100");
		final File output = new File("temp.jpg");
		
		FileUtils.writeByteArrayToFile(output, user.getImages());
		
		if (null != output && output.exists() && output.isFile()) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
						qrCodeFrame = new QRCodeFrame(output.getPath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		wechat.login(user);
		wechat.start(user);
		return user;
	}

	public void close() {
		qrCodeFrame.dispose();
	}

}
