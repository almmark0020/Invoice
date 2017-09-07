package mx.com.amis.sipac.utils;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyStoreUtil {
	private static final Logger logger = LoggerFactory.getLogger(KeyStoreUtil.class);
	
	@Value("${ssl.key-store}")
	private String keyStore;

	@Value("${ssl.trust-store}")
	private String trustStore;

	@Value("${ssl.trust-store-password}")
	private String keyStorePassword;
	
	@Value("${ssl.trust-store-type}")
	private String trueStoreType;
	
	@PostConstruct
    public void init(){
		setTrustStoreParams();
    }
	
	public void setTrustStoreParams() {
		logger.info("Start setTrustStoreParams... ");
		logger.info("Path JKS: " + trustStore);
		
		if(!StringUtils.isEmpty(trustStore)) {
			System.setProperty("javax.net.ssl.trustStore", trustStore);
			System.setProperty("javax.net.ssl.trustStorePassword", keyStorePassword);
			System.setProperty("javax.net.ssl.trustStoreType", trueStoreType);
		}
	}

}
