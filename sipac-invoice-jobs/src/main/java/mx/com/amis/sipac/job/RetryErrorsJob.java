package mx.com.amis.sipac.job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.com.amis.sipac.invoice.persistence.repository.InvoiceOrdersRepository;

@Service("retryErrorsJob")
public class RetryErrorsJob {
	private static final Logger logger = LoggerFactory.getLogger(RetryErrorsJob.class);

	@Autowired(required = true)
	private InvoiceOrdersRepository repository;

	public void process() {
		logger.debug("Start quartz process...");
		repository.resetErrors();
		logger.debug("End quartz process.");
	}
}
