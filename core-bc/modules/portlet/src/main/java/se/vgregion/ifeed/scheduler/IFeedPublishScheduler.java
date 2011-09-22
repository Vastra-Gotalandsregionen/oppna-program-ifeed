package se.vgregion.ifeed.scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.push.IFeedPublisher;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;

/**
 * @author Anders Asplund - Callista Enterprise
 *
 */
public class IFeedPublishScheduler implements MessageListener {
    /**
     *
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(IFeedPublishScheduler.class);

    /**
     *
     */
    private ApplicationContext context;
    /**
     *
     */
    private IFeedPublisher iFeedPublisher;
    /**
     *
     */
    private IFeedService iFeedService;
    /**
     *
     */
    private IFeedSolrQuery iFeedSolrQuery;


    @Override
    @Transactional
    public final void receive(final Message message) {
        LOGGER.debug("Schedule task is started {}",
                ToStringBuilder.reflectionToString(message));

        loadContext("classpath*:spring/ifeed-*.xml");
        initBeans();

        Collection<IFeed> modifiedIFeeds = new HashSet<IFeed>();

        Collection<IFeed> ifeeds = iFeedService.getIFeeds();
        for (IFeed iFeed : ifeeds) {
            LOGGER.debug("Checking for updates since {} in feed {} (id: {})",
                new Object[] {iFeed.getTimestamp(),
                    iFeed.getName(), iFeed.getId() });
            Collection<Map<String, Object>> iFeedResults =
                iFeedSolrQuery.getIFeedResults(iFeed, iFeed.getTimestamp());

            if (!isEmpty(iFeedResults) || iFeed.getTimestamp() == null) {
                LOGGER.debug("{} new documents found in feed {} (id: {})",
                        new Object[] {iFeedResults.size(),
                        iFeed.getName(), iFeed.getId() });
                LOGGER.debug("Sending feed {} (id: {}) to PuSH server.",
                        new Object[] {iFeed.getName(), iFeed.getId()});
                iFeedPublisher.addIFeed(iFeed);
                modifiedIFeeds.add(iFeed);
            }
        }

        if (modifiedIFeeds.size() > 0 && iFeedPublisher.publish()) {
            for (IFeed iFeed : modifiedIFeeds) {
                iFeed.setTimestamp();
                iFeedService.updateIFeed(iFeed);
            }
        }
    }

    /**
     *
     */
    private void initBeans() {
        iFeedPublisher = context.getBean(IFeedPublisher.class);
        iFeedService = context.getBean(IFeedService.class);
        iFeedSolrQuery = context.getBean(IFeedSolrQuery.class);
    }

    /**
     * @param configLocation
     */
    private void loadContext(String configLocation) {
        LOGGER.debug("Loading spring context");
        if (context == null) {
            try {
                LOGGER.debug("Creating new application context using config "
                        + "location: {}", configLocation);
                context = new ClassPathXmlApplicationContext(configLocation);
                LOGGER.debug("Context created: {}", context);
            } catch (BeansException e) {
                e.printStackTrace();
                LOGGER.error("Context is null, failed to inialize: {}",
                        e.getCause());
            }
        }
    }

    /**
     * @param c
     * @return
     */
    private boolean isEmpty(final Collection<?> c) {
        return (c == null || c.size() == 0);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        IFeedPublishScheduler scheduler = new IFeedPublishScheduler();
        scheduler.receive(null);
    }

}