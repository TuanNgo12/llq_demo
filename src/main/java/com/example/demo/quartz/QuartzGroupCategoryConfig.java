package com.example.demo.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.quartz.autoconfigure.SchedulerFactoryBeanCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzGroupCategoryConfig {

    @Bean
    public AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory(ApplicationContext ctx) {
        AutowiringSpringBeanJobFactory factory = new AutowiringSpringBeanJobFactory();
        factory.setApplicationContext(ctx);
        return factory;
    }

    @Bean
    public SchedulerFactoryBeanCustomizer jobFactoryCustomizer(AutowiringSpringBeanJobFactory jobFactory) {
        return schedulerFactoryBean -> schedulerFactoryBean.setJobFactory(jobFactory);
    }

    @Bean
    public JobDetail groupCategoryActivationJobDetail() {
        return JobBuilder.newJob(GroupCategoryActiveJob.class)
                .withIdentity("groupCategoryActivationJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger groupCategoryActivationTrigger(
            JobDetail groupCategoryActivationJobDetail,
            @Value("${scheduler.group-category-activation.cron:0 * * * * ?}") String cron) {
        return TriggerBuilder.newTrigger()
                .forJob(groupCategoryActivationJobDetail)
                .withIdentity("groupCategoryActivationTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
    }

}
