package lz.dubbo.trace;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lz.dubbo.trace.model.IdTypeEnum;

public class IdFactory {


	private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public static final String  HASH_SYMBOL     = "#";
 
    private static final long   HOUR            = 60 * 60 * 1000L;
    private static IdFactory    instance        = new IdFactory();
    private String              domain;
    private String              ipAddress;
    private AtomicLong          generateSpanId  = new AtomicLong(1);
    private AtomicLong          generateTraceId = new AtomicLong(1);
    private volatile boolean isInit = false;

    private IdFactory() {

    }

    public static IdFactory getInstance() {
        return instance;
    }

    public void init(String domain, String ipAddress) {
        if (!isInit) {
            synchronized (this) {
                if (!isInit) {

                    this.domain = domain;
                    this.ipAddress = ipAddress;
                    isInit = true;

                    logger.info("init idfactory success! domain{} ipaddess {}", domain, ipAddress);

                }
            }
        }

    }

    public String getNextId(int type, String itemName) {
     
        long index = 1;
        if (type == IdTypeEnum.SPAN_ID.getType()) {
            index = generateSpanId.getAndIncrement();

        } else if (type == IdTypeEnum.TRACE_ID.getType()) {
            index = generateTraceId.getAndIncrement();
        } else {
            throw new IllegalArgumentException("invalid type :" + type);
        }

        long timestamp = getTimestamp();

        StringBuilder sb = new StringBuilder(domain.length() + 128);

        sb.append(domain);
        sb.append(HASH_SYMBOL);
        sb.append(ipAddress);
        sb.append(HASH_SYMBOL);
        sb.append(itemName);
        sb.append(HASH_SYMBOL);
        sb.append(timestamp);
        sb.append(HASH_SYMBOL);
        sb.append(index);

        return sb.toString();
    }

    protected long getTimestamp() {

        long timestamp = MillSecondTimer.currentTimeMillis();

        return timestamp / HOUR; // version 2
    }

}
