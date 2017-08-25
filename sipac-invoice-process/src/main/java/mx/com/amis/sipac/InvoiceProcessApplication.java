package mx.com.amis.sipac;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Payment orders registration microservice application.
 */
@SpringBootApplication
public class InvoiceProcessApplication {

  /**
   * Main launcher method.
   *
   * @param args Array of {@link String} with any argument to the application.
   */
	public static void main(final String[] args) {
		SpringApplication.run(InvoiceProcessApplication.class, args);
	}

}

