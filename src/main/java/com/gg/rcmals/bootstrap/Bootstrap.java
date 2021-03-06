package com.gg.rcmals.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * @author daigouwei
 * @date 2018/11/5
 */
public class Bootstrap {
    private static final Logger LOG = LoggerFactory.getLogger("service");

    public static void main(String[] args) {
        GenericXmlApplicationContext genericXmlApplicationContext = new GenericXmlApplicationContext();
        try {
            LOG.info("rcm-als bootstrapping...");
            genericXmlApplicationContext.load("applicationContext.xml");
            genericXmlApplicationContext.refresh();
            LOG.info("rcm-als finish bootstrap.");
        }
        catch (BeansException | IllegalStateException e) {
            genericXmlApplicationContext.close();
            LOG.error("fail bootstrap rcm-als!!!", e);
            System.exit(1);
        }
    }
}
