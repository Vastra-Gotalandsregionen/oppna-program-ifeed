package se.vgregion.ifeed.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.vgregion.ifeed.service.metadata.MetadataService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MetadataJob implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataJob.class);

    private static MetadataService metadataService;

    private static MidnightRun midnightRun;

    public MetadataJob() {
        super();
        LOGGER.info("MetadataJob is created!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent context) {
        LOGGER.info("ServletContextListener destroyed");
        if (midnightRun == null) {
            midnightRun.stop();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent context) {
        loadContext("classpath*:spring/ifeed-*.xml");
        if (midnightRun == null) {
            midnightRun = new MidnightRun(() -> todo());
            midnightRun.startTimer();

            // If there are more than one hour to midnight, when the ordinary run is to take place, run once now.
            if (midnightRun.getTimeToNextRun() > (60 * 60 * 1000)) {
                final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
                executor.schedule(() -> midnightRun.run(), 200, TimeUnit.SECONDS);
            }
        }
    }

    synchronized void todo() {
        metadataService = null;
        // loadContext("classpath*:spring/ifeed-*.xml");
        if (metadataService != null) {
            LOGGER.info("Importing metadata");
            metadataService.importMetadata();
        } else {
            LOGGER.info("Couldn't find bean for MetadataService");
        }
    }

    protected void loadContext(final String configLocation) {
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(configLocation);
            MetadataService service = context.getBean(MetadataService.class);
            setMetadataService(service);
        } catch (BeansException e) {
            e.printStackTrace();
            LOGGER.error("Context is null, failed to inialize: {}", e);
        }
    }

    public MetadataService getMetadataService() {
        return MetadataJob.metadataService;
    }

    public void setMetadataService(MetadataService metadataService) {
        MetadataJob.metadataService = metadataService;
    }

}