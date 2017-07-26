package com.github.wechat;

public interface Wechat {

	User init(String uid);

	void login(User user);
	
	void start(User user);
	
}
