/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.factory;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author developer
 */
public class MaintenanceFactory {
    
    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Value("${mq.homePageQueueName}")
    private String homePageQueueName;

    @Value("${mq.robotsQueueName}")
    private String robotsQueueName;

    @Value("${mq.sitemapQueueName}")
    private String sitemapQueueName;

    @Value("${mq.wwwRedirectQueueName}")
    private String wwwRedirectQueueName;

    @Value("${mq.compressQueueName}")
    private String compressQueueName;
    
    @Value("${mq.error404QueueName}")
    private String error404QueueName;
    
    @Value("${mq.feedQueueName}")
    private String feedQueueName;

    @Value("${mq.styleQueueName}")
    private String styleQueueName;

    @Value("${mq.scriptQueueName}")
    private String scriptQueueName;
   
    @Value("${mqQueueName.script2}")
    private String queueScript2;
    
    @Value("${mqQueueName.style2}")
    private String queueStyle2;

    @Value("${mqQueueName.eq}")
    private String queResolution;
    
    @Value("${mqQueueName.request.save}")
    private String queueRequestSave;

    /**
     * Vaciar todas las colas.
     *
     * @Deprecated
     */
    private void emptyQueues() {
        rabbitAdmin.purgeQueue(homePageQueueName, true);
        rabbitAdmin.purgeQueue(robotsQueueName, true);
        rabbitAdmin.purgeQueue(sitemapQueueName, true);
        rabbitAdmin.purgeQueue(wwwRedirectQueueName, true);
        rabbitAdmin.purgeQueue(compressQueueName, true);
        rabbitAdmin.purgeQueue(error404QueueName, true);
        rabbitAdmin.purgeQueue(feedQueueName, true);
        rabbitAdmin.purgeQueue(styleQueueName, true);
        rabbitAdmin.purgeQueue(scriptQueueName, true);
        rabbitAdmin.purgeQueue(queueScript2, true);
        rabbitAdmin.purgeQueue(queueStyle2, true);
        rabbitAdmin.purgeQueue(queueRequestSave, true);
    }
    
}
