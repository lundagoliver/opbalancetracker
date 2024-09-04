package com.systems88.opbalancetracker.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionFactoryCreator {
	private static final Log log = LogFactory.getLog(ConnectionFactoryCreator.class);
	private final SSLContext context;
	
	public ConnectionFactoryCreator(SSLContext context) {
		this.context = context;
	}

	public SSLSocketFactory getSocketFactory(String pathToPemKey)
			throws IOException, CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException,
			UnrecoverableKeyException, KeyManagementException {

		KeyStore keystore = KeyStore.getInstance("JKS");
		keystore.load(null);
		log.info("CHECKING THE FILE : " + pathToPemKey);
		InputStream resourceAsStream = getClass().getResourceAsStream(pathToPemKey);
		byte[] certAndKey = IOUtils.toByteArray(resourceAsStream);

		byte[] certBytes = parseDERFromPEM(certAndKey, "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");
		byte[] keyBytes = parseDERFromPEM(certAndKey, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");

		X509Certificate cert = generateCertificateFromDER(certBytes);

		PrivateKey key = generatePrivateKeyFromDER(keyBytes);

		keystore.setCertificateEntry("cert-alias", cert);
		keystore.setKeyEntry("key-alias", key, "changeit".toCharArray(), new Certificate[] { cert });

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keystore, "changeit".toCharArray());

		KeyManager[] km = kmf.getKeyManagers();

		context.init(km, null, null);

		return context.getSocketFactory();
	}

	private byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter) {
		String data = new String(pem);
		String[] tokens = data.split(beginDelimiter);
		tokens = tokens[1].split(endDelimiter);
		return DatatypeConverter.parseBase64Binary(tokens[0]);
	}

	private PrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

		KeyFactory factory = KeyFactory.getInstance("RSA");

		return factory.generatePrivate(spec);
	}

	private X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
		CertificateFactory factory = CertificateFactory.getInstance("X.509");

		return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
	}
	
}