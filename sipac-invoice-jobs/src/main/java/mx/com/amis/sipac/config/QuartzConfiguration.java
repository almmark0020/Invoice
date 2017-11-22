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
    stFactory.setStartDelay(1000);
    stFactory.setName("mytrigger");
    stFactory.setGroup("mygroup");
//    stFactory.setCronExpression("0 0/1 * * * ?"); // every minute
    stFactory.setCronExpression("0/30 * * * * ?"); // every minute
    return stFactory;
  }
  
  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    System.out.println("Iniciando el llamado ......");
    scheduler.setTriggers(cronTriggerFactoryBean().getObject());
    return scheduler;
  }
  
  @Bean
  public MethodInvokingJobDetailFactoryBean methodInvokingCancelInvoicesJob() {
    MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
    obj.setTargetBeanName("cancelInvoicesJob");
    obj.setTargetMethod("process");
    return obj;
  }
  
  @Bean
  public CronTriggerFactoryBean cronTriggerCancelFactoryBean(){
    CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
    stFactory.setJobDetail(methodInvokingCancelInvoicesJob().getObject()); //Registrado
    stFactory.setStartDelay(1000);
    stFactory.setName("mytrigger2");
    stFactory.setGroup("mygroup2");
//    stFactory.setCronExpression("0 0/1 * * * ?"); // every minute
    stFactory.setCronExpression("0/30 * * * * ?"); // every minute
    return stFactory;
  }

  @Bean
  public SchedulerFactoryBean schedulerCancelFactoryBean() {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    System.out.println("Iniciando el llamado ......");
    scheduler.setTriggers(cronTriggerCancelFactoryBean().getObject());
    return scheduler;
  }
  
  @Bean
  public MethodInvokingJobDetailFactoryBean methodInvokingComplementInvoicesJob() {
    MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
    obj.setTargetBeanName("paymentComplementJob");
    obj.setTargetMethod("process");
    return obj;
  }
  
  @Bean
  public CronTriggerFactoryBean cronTriggerComplementFactoryBean(){
    CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
    stFactory.setJobDetail(methodInvokingComplementInvoicesJob().getObject()); 
    stFactory.setStartDelay(1000);
    stFactory.setName("mytrigger3");
    stFactory.setGroup("mygroup3");
//    stFactory.setCronExpression("0 0/1 * * * ?"); // every minute
    stFactory.setCronExpression("0/30 * * * * ?"); // every minute
    return stFactory;
  }

  @Bean
  public SchedulerFactoryBean schedulerComplementFactoryBean() {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    System.out.println("Iniciando el llamado ......");
    scheduler.setTriggers(cronTriggerComplementFactoryBean().getObject());
    return scheduler;
  }
  
  @Bean
  public MethodInvokingJobDetailFactoryBean methodInvokingCreditNoteInvoicesJob() {
    MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
    obj.setTargetBeanName("paymentCreditNoteJob");
    obj.setTargetMethod("process");
    return obj;
  }
  
  @Bean
  public CronTriggerFactoryBean cronTriggerCreditNoteFactoryBean(){
    CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
    stFactory.setJobDetail(methodInvokingCreditNoteInvoicesJob().getObject());
    stFactory.setStartDelay(1000);
    stFactory.setName("mytrigger4");
    stFactory.setGroup("mygroup4");
//    stFactory.setCronExpression("0 0/1 * * * ?"); // every minute
    stFactory.setCronExpression("0/30 * * * * ?"); // every minute
    return stFactory;
  }

  @Bean
  public SchedulerFactoryBean schedulerCreditNoteFactoryBean() {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    System.out.println("Iniciando el llamado ......");
    scheduler.setTriggers(cronTriggerCreditNoteFactoryBean().getObject());
    return scheduler;
  }
  
  //
  
  @Bean
  public MethodInvokingJobDetailFactoryBean methodInvokingAutAcceptanceInvoicesJob() {
    MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
    obj.setTargetBeanName("autAcceptanceJob");
    obj.setTargetMethod("process");
    return obj;
  }
  
  @Bean
  public CronTriggerFactoryBean cronTriggerAutAcceptanceFactoryBean(){
    CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
    stFactory.setJobDetail(methodInvokingAutAcceptanceInvoicesJob().getObject());
    stFactory.setStartDelay(1000);
    stFactory.setName("mytrigger6");
    stFactory.setGroup("mygroup6");
//    stFactory.setCronExpression("0 0/1 * * * ?"); // every minute
//    stFactory.setCronExpression("0 */12 * * * ?"); // every twelve hours
    stFactory.setCronExpression("0 1 5 * * ?"); // every day at 7:00 am
//    stFactory.setCronExpression("0 1 11 * * ?"); // every day at 7:00 am
    return stFactory;
  }  

  @Bean
  public SchedulerFactoryBean schedulerAutAcceptanceFactoryBean() {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    System.out.println("Iniciando el llamado ......");
    scheduler.setTriggers(cronTriggerAutAcceptanceFactoryBean().getObject());
    return scheduler;
  }
  
  @Bean
  public CronTriggerFactoryBean cronTriggerAutAcceptanceFactoryBeanAux(){
    CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
    stFactory.setJobDetail(methodInvokingAutAcceptanceInvoicesJob().getObject());
    stFactory.setStartDelay(1000);
    stFactory.setName("mytrigger6_Aux");
    stFactory.setGroup("mygroup6_Aux");
//    stFactory.setCronExpression("0 0/1 * * * ?"); // every minute
//    stFactory.setCronExpression("0 */12 * * * ?"); // every twelve hours
    stFactory.setCronExpression("0 13 2 * * ?"); // every day at 7:00 am
//    stFactory.setCronExpression("0 1 11 * * ?"); // every day at 7:00 am
    return stFactory;
  }  

  @Bean
  public SchedulerFactoryBean schedulerAutAcceptanceFactoryBeanAux() {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    System.out.println("Iniciando el llamado ......");
    scheduler.setTriggers(cronTriggerAutAcceptanceFactoryBeanAux().getObject());
    return scheduler;
  }
  
  //
  
  @Bean
  public MethodInvokingJobDetailFactoryBean methodInvokingRetryErrorsJob() {
    MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
    obj.setTargetBeanName("retryErrorsJob");
    obj.setTargetMethod("process");
    return obj;
  }
  
  @Bean
  public CronTriggerFactoryBean cronTriggerRetryErrorsFactoryBean(){
    CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
    stFactory.setJobDetail(methodInvokingRetryErrorsJob().getObject());
    stFactory.setStartDelay(1000);
    stFactory.setName("mytrigger5");
    stFactory.setGroup("mygroup5");
    stFactory.setCronExpression("0 0 0/8 * * ?"); // every eight hours
    return stFactory;
  }

  @Bean
  public SchedulerFactoryBean schedulerRetryErrorsFactoryBean() {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    System.out.println("Iniciando el llamado ......");
    scheduler.setTriggers(cronTriggerRetryErrorsFactoryBean().getObject());
    return scheduler;
  }
}
