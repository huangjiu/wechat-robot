package com.github.wechat;

import me.biezhi.wechat.model.WechatContact;
import me.biezhi.wechat.model.WechatMeta;

public class User {

	private String uid;
	private String uuid;
	private byte[] images;
	private WechatMeta meta;
	private WechatListener listener;
	private WechatContact contact;

	public User(String uid, String uuid) {
		this(uid, uuid, new WechatMeta(), null);
	}

	public User(String uid, String uuid, WechatMeta meta, WechatListener listener) {
		super();
		this.uid = uid;
		this.uuid = uuid;
		this.meta = meta;
		this.listener = listener;
	}

	public String getUid() {
		return uid;
	}

	public String getUuid() {
		return uuid;
	}

	public WechatMeta getMeta() {
		return meta;
	}

	public WechatListener getListener() {
		return listener;
	}

	public void setListener(WechatListener listener) {
		this.listener = listener;
	}

	public byte[] getImages() {
		return images;
	}

	public void setImages(byte[] images) {
		this.images = images;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setMeta(WechatMeta meta) {
		this.meta = meta;
	}

	public WechatContact getContact() {
		return contact;
	}

	public void setContact(WechatContact contact) {
		this.contact = contact;
	}

}
