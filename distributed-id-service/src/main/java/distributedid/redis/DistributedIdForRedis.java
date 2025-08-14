package distributedid.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Redis主键自增设计如下：
 *
 * 主键由：符号位[1]+时间戳[31]+自增序列组成[32] 共计64位组成
 */
@Component
public class DistributedIdForRedis {

    @Resource
    private RedisTemplate<String, Integer> redisTemplate;

    private static final long BASE_TIMESTAMP = LocalDateTime.of(2025, 8, 1, 0, 0, 0)
            .toEpochSecond(ZoneOffset.UTC);

    /**
     * 生成逻辑如下
     * 1、 获取当前时间减去基准时间的时间戳
     * 2、 生成序号，需要通过Redis increment命令去生成，因此需要根据tableName和日期作为Key
     * 3、 将时间戳左移32位，因为前32位是自增序列，所以左移32位给自增序列让出位置
     * 4、 将左移32位后的时间戳与自增序列进行或操作进行合并
     *
     * @param tableName 表名
     * @return 该表对应的id
     */
    public long nextId(String tableName) {
        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.toEpochSecond(ZoneOffset.UTC) - BASE_TIMESTAMP;
        return timestamp << 32 | increment(tableName, now);
    }

    private Long increment(String tableName, LocalDateTime localDateTime) {
        String formatted = localDateTime.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        String key = formatted + ":id:"  + tableName;

        return redisTemplate.opsForValue().increment(key);
    }

}
