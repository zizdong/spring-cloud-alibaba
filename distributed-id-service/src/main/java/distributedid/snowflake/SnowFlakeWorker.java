package distributedid.snowflake;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;

public class SnowFlakeWorker {

    // 定义雪花算法的相关属性
    // 序号所占位数
    private final long sequenceBits = 12L;
    // workerId所占位数
    private final long workerIdBits = 5L;
    // dateCenterId所占位数
    private final long datacenterIdBits = 5L;

    // workerId的偏移量
    private final long workerIdShift = sequenceBits;
    // dataCenterId的偏移量
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    // 时间戳偏移量
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 最后一次生成时间
    private long lastTimestamp = -1L;
    // 同一毫秒内的序列号
    private long sequence;
    /**
     * 通过SequenceMask（掩码）的作用如下：
     * 1. 严格限制sequence的取值范围为0~2^SEQUENCE_BIT的范围，如果超过该范围，则会重置为0
     * 2. 避免二进制污染，如果不使用掩码序列号如果超过最大值，可能会影响时间序列的二进制，导致id冲突
     * 3. 保证唯一性，严格限制序列号范围，确保同一毫秒、同一机器生成的ID数不超过SequenceMask + 1个，从而避免重复
     * <p>
     * 掩码的计算方式 2^SEQUENCE_BIT - 1
     * 以下面的代码为例：
     * 1 << 12 = 1000000000000
     * 1000000000000 - 1 = 0111111111111 = 111111111111
     * 掩码和序列号进行与操作即可将序列号控制在其应有的范围内。
     * 比如：4097 & 4095
     * 4097的二进制：    0000000000000000000000000000000000000000000000000001000000000001
     * 4095的二进制：    0000000000000000000000000000000000000000000000000000111111111111
     * 与操作结果为 ：    0000000000000000000000000000000000000000000000000000000000000001
     */
    private final long sequenceMask = (1 << sequenceBits) - 1;

    // 工作机器ID
    private long workId;
    // 数据中心ID
    private long datacenterId;

    // 基准时间（毫秒）
    private static final long BASE_TIMESTAMP = LocalDateTime.of(2025, 8, 1, 0, 0, 0)
            .toInstant(ZoneOffset.UTC).toEpochMilli();

    public SnowFlakeWorker(long workId, long datacenterId) {
        if (workId > (1 << workerIdBits - 1)) {
            throw new RuntimeException();
        }
        if (datacenterId > (1 << datacenterIdBits - 1)) {
            throw new RuntimeException();
        }
        this.workId = workId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获取下一个id
     *
     * @return 返回下一个ID
     */
    public synchronized long nextId() {
        // 获取当前时间的毫秒值
        long currentMillis = System.currentTimeMillis();

        // 如果当前时间小于上次生成的时间，则说明系统的始终回退 or 回拨，这个时候应当抛出异常。
        if (currentMillis < lastTimestamp) {
            throw new RuntimeException(String.format("当前时间小于上次生成时间。在%d毫秒内拒绝生成id", lastTimestamp - currentMillis));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (currentMillis == lastTimestamp) {
            sequence = (sequence + 1L) & sequenceMask;
            if (sequence == 0L) {
                // 这里等于0 则代表当前毫秒值内生成id的数量大于了4096个，需要等待下一毫秒才能正常的生成
                currentMillis = this.tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentMillis;

        return ((currentMillis - BASE_TIMESTAMP) << timestampLeftShift)
                | datacenterId << datacenterIdShift
                | this.workId << workerIdShift
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) throws InterruptedException {
        SnowFlakeWorker worker = new SnowFlakeWorker(0, 0);
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        Thread.sleep(1);
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        Thread.sleep(1);
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
        System.out.println(worker.nextId());
        System.out.println(Long.toBinaryString(worker.nextId()));
    }

}
