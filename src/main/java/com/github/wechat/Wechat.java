package com.github.wechat;

public interface Wechat {

	User login(String uid);

	void waitForLogin(User user);

	void start(User user);

}
