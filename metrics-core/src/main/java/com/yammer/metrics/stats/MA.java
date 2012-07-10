package com.yammer.metrics.stats;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A moving average.   (By Csaba)
 *
 */
public class MA {
    private static final int SLOT_PER_MINUTE = 12;
    private static final int FIVE_MINUTES = 5;

    private volatile double rate = 0.0;

    private final AtomicLong uncounted = new AtomicLong();
    private final Buffer m5Fifo = BufferUtils.synchronizedBuffer(new CircularFifoBuffer(60));
    private final AtomicLong m5Sum = new AtomicLong();

    public static MA fiveMinuteMA() {
        return new MA(SLOT_PER_MINUTE * FIVE_MINUTES);
    }

    /**
     * Create a new MA
     *
     */
    public MA(int slots) {
        for (int i = 0; i < slots; i++) {
            this.m5Fifo.add(new Long(0));
        }
    }

    /**
     * Update the moving average with a new value.
     *
     * @param n the new value
     */
    public void update(long n) {
        uncounted.addAndGet(n);
    }

    public void tick() {
        final long count = uncounted.getAndSet(0);
       // System.out.println("count in last 5 s: " + count);
        m5Sum.addAndGet(count);

        Long first = (Long) m5Fifo.remove();
        long f = first.longValue();
        if (f != 0) {
            f = (-1) * f;
        }
        m5Sum.addAndGet(f);

        rate = m5Sum.get() / 60.0;
        m5Fifo.add(new Long(count));

    }

    /**
     * Returns the rate in the given units of time.
     *
     * @param rateUnit the unit of time
     * @return the rate
     */
    public double rate(TimeUnit rateUnit) {
        //return rate * (double) rateUnit.toNanos(1);
        return rate;
    }
}

