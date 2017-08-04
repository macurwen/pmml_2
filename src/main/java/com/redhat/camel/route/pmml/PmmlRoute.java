package com.redhat.camel.route.pmml;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PmmlRoute {
    public static void main(String[] args) throws InterruptedException{
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("camel-context.xml");
        // Keep main thread alive for some time to let application finish processing the input files.
        Thread.sleep(10000);
        applicationContext.close();
    }
}
