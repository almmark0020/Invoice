package mx.com.amis.sipac;
import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Payment orders registration microservice application.
 */
@SpringBootApplication
@EnableAsync
public class InvoiceProcessApplication {

	/**
	 * Main launcher method.
	 *
	 * @param args Array of {@link String} with any argument to the application.
	 */
	public static void main(String[] args) {
		// close the application context to shut down the custom ExecutorService
		SpringApplication.run(InvoiceProcessApplication.class, args).close();
	}

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(1500);
		executor.setThreadNamePrefix("sipac-invoice-process-");
		executor.initialize();
		return executor;
	}
}
