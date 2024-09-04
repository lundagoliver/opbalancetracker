package com.systems88.opbalancetracker.service;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import com.systems88.opbalancetracker.net.RESTHttpClient;

@Service
public class TelegramNotificationService {

	private final Logger log = LoggerFactory.getLogger(TelegramNotificationService.class);
	private RESTHttpClient restTemplate;
	

	public TelegramNotificationService(RESTHttpClient restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}
	
	@Async
	public void sendPostMessage(String string, String telegramChatId, String telegramApiUrl, String telegramBotMonitoringToken) {
		String url = MessageFormat.format(telegramApiUrl, telegramBotMonitoringToken);
		String result = null;
		try {
			MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
			param.add("chat_id", telegramChatId);
			param.add("text", string);
			param.add("parse_mode", "HTML");
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(param, null);
			restTemplate.getDefaultRestTemplate().postForEntity(url, request, String.class).getBody();

		} catch (RestClientException e) {
			log.error("error ", e);
		}
		log.info(result);
	}

}
