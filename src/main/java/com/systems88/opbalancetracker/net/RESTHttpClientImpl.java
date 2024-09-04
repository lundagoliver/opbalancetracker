package com.systems88.opbalancetracker.net;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RESTHttpClientImpl implements RESTHttpClient {

	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 100;
	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 240 * 1000;
	private static final Logger log = LoggerFactory.getLogger(RESTHttpClientImpl.class);


	@Override
	public RestTemplate getDefaultRestTemplate() {
		RestTemplate restTemplate = new RestTemplate(getHttpRequestFactory(getHttpClient()));
		setMessageCustomConverters(restTemplate.getMessageConverters());
		return restTemplate;
	}

	private ClientHttpRequestFactory getHttpRequestFactory(HttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	private HttpClient getHttpClient() {

		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
				.setConnectionRequestTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
				.setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
				.build();

		return HttpClientBuilder.create()
				.setMaxConnTotal(DEFAULT_MAX_TOTAL_CONNECTIONS)
				.setMaxConnPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE)
				.setDefaultRequestConfig(requestConfig)
				.build();
	}

	protected void setMessageCustomConverters(List<HttpMessageConverter<?>> messageConverters) {
		List<MediaType> mediaTypes = new ArrayList<>();

		for(HttpMessageConverter<?> converter : messageConverters) {
			if(converter instanceof Jaxb2RootElementHttpMessageConverter) {	
				mediaTypes.add(MediaType.TEXT_HTML);
				mediaTypes.add(MediaType.TEXT_XML);
				mediaTypes.add(MediaType.APPLICATION_XML);
				mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
				((Jaxb2RootElementHttpMessageConverter) converter).setSupportedMediaTypes(mediaTypes);
			}
			if(converter instanceof MappingJackson2HttpMessageConverter) {
				mediaTypes.add(MediaType.TEXT_PLAIN);
				mediaTypes.add(MediaType.TEXT_HTML);
				mediaTypes.add(MediaType.APPLICATION_JSON);
				mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
				((MappingJackson2HttpMessageConverter) converter).setSupportedMediaTypes(mediaTypes);
			}
		}		
	}

	@Override
	public RestTemplate getRestWithPem(String pemKeyMap) {
		RestTemplate restTemplate;

		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e1) {
			log.error("NoSuchAlgorithmException :", e1);
		}
		ConnectionFactoryCreator connectionFactoryCreator = new ConnectionFactoryCreator(sslContext);
		HttpClient httpClient = null;
		try {
			httpClient = HttpClientBuilder.create()
					.setSSLContext(sslContext)
					.setSSLSocketFactory(new SSLConnectionSocketFactory(connectionFactoryCreator.getSocketFactory(pemKeyMap),new DefaultHostnameVerifier()))
					.setMaxConnTotal(DEFAULT_MAX_TOTAL_CONNECTIONS)
					.setMaxConnPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE)
					.build();
		} catch (UnrecoverableKeyException | KeyManagementException | CertificateException | InvalidKeySpecException
				| NoSuchAlgorithmException | KeyStoreException | IOException e) {
			log.error("Exception :", e);
		}
		restTemplate = new RestTemplate(getHttpRequestFactory(httpClient));
		setMessageCustomConverters(restTemplate.getMessageConverters());
		return restTemplate;
	}
}
