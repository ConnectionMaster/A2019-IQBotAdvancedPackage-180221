package com.automationanywhere.botcommand.sk;


public class IQBotConnection {

	public String url;
	public String token;
	
	public IQBotConnection(String url, String token) {
		this.url = url;
		this.token = token;
	}
	
	public String getURL() {
		return this.url;
	}
	
	public String getToken() {
		return this.token ;
	}
	
}

