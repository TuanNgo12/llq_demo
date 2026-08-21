package com.example.demo.quartz;

import com.example.demo.service.GroupCategoryService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupCategoryActiveJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(GroupCategoryActiveJob.class);

    @Autowired
    private GroupCategoryService groupCategoryService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            int updateIsActive = groupCategoryService.activateParams();
            if (updateIsActive > 0){
                log.info("Đã tự động kích hoạt {} tham số (EFFECTIVE_DATE <= now)", updateIsActive);
            }
        }catch (Exception e){
            log.error("Error executing GroupCategoryActiveJob: ", e);
            throw new JobExecutionException(e);
        }
    }
}
