package com.systems88.opbalancetracker.net;

import org.springframework.web.client.RestTemplate;

public interface RESTHttpClient {
	
	public RestTemplate getDefaultRestTemplate();
	
	public RestTemplate getRestWithPem(String pemKeyMap);
}
