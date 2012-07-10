package com.yammer.metrics.stats.tests;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: csaba
 * Date: 2012.07.09.
 * Time: 20:20
 * To change this template use File | Settings | File Templates.
 */
public class MATest {

    public static void main(String[] args) {
        Meter m = Metrics.newMeter(MATest.class, "", "", TimeUnit.SECONDS);
        for (int i = 0; i < 100; i++) {

            m.mark(i);
            System.out.println(m.fiveMinuteMA());

            try {
                Thread.sleep(5000);
                System.out.println("-----------------------");
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
