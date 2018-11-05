package com.datacollection.service.managejobs;

import com.datacollection.common.config.Configuration;

public interface JobManager {

    JobDetail getJob(String name);

    void updateJob(JobDetail job);

    static JobManager create(Configuration conf) {
        return new JobManagerImpl(conf);
    }
}
