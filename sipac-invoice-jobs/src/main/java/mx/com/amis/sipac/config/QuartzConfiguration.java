package mx.com.amis.sipac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@ComponentScan("mx.com.amis.sipac")
public class QuartzConfiguration {

  @Bean
  public MethodInvokingJobDetailFactoryBean methodInvokingJobsManageOrders() {
    MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
    obj.setTargetBeanName("jobsRegManageOrders");
    obj.setTargetMethod("procesaRegistrado");
    return obj;
  }

  @Bean
  public CronTriggerFactoryBean cronTriggerFactoryBean(){
    CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
    stFactory.setJobDetail(methodInvokingJobsManageOrders().getObject()); //Registrado
    stFactory.setStartDelay(3000);
    stFactory.setName("mytrigger");
    stFactory.setGroup("mygroup");
    //stFactory.setCronExpression("0 0/1 * 1/1 * ? *");
    //stFactory.setCronExpression("0 15 10 ? * MON-FRI"); //1 am
    stFactory.setCronExpression("15 0/2 * * * ?"); // every 2 minutes
    //.withSchedule(cronSchedule("15 0/2 * * * ?"))
    return stFactory;
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    System.out.println("Iniciando el llamado ......");
    scheduler.setTriggers(cronTriggerFactoryBean().getObject());
    return scheduler;
  }
}
