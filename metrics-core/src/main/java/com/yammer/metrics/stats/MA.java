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
    private static final int ONE_MINUTES = 1;

    private volatile double rate = 0.0;
    private double seconds;

    private final AtomicLong uncounted = new AtomicLong();
    private Buffer fifo;
    private final AtomicLong sumInWindow = new AtomicLong();

    public static MA fiveMinuteMA() {
        return new MA(FIVE_MINUTES);
    }

    public static MA oneMinuteMA() {
        return new MA(ONE_MINUTES);
    }

    /**
     * Create a new MA
     *
     */
    public MA(int minutes) {
        int slots = SLOT_PER_MINUTE * minutes;
        seconds = minutes * 60.0;
        fifo = BufferUtils.synchronizedBuffer(new CircularFifoBuffer(slots));
        for (int i = 0; i < slots; i++) {
            this.fifo.add(new Long(0));
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
        sumInWindow.addAndGet(count);

        Long first = (Long) fifo.remove();
        long f = first.longValue();
        if (f != 0) {
            f = (-1) * f;
        }
        sumInWindow.addAndGet(f);

        rate = sumInWindow.get() / seconds;
        fifo.add(new Long(count));

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

    public long sumInWindow() {
        return sumInWindow.get();
    }

    public long sumInWindowNow() {
        long result = sumInWindow() + uncounted.get();
        return result;
    }
}

