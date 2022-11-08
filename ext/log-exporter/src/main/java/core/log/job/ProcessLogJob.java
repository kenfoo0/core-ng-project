package core.log.job;

import core.framework.inject.Inject;
import core.framework.scheduler.Job;
import core.framework.scheduler.JobContext;
import core.log.service.ArchiveService;

import java.time.LocalDate;

/**
 * @author neo
 */
public class ProcessLogJob implements Job {
    @Inject
    ArchiveService archiveService;

    @Override
    public void execute(JobContext context) {
        LocalDate today = context.scheduledTime.toLocalDate();
        archiveService.uploadArchive(today.minusDays(1));
        archiveService.cleanupArchive(today.minusDays(5));
    }
}
