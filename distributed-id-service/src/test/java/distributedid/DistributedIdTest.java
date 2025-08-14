package distributedid;

import distributedid.redis.DistributedIdForRedis;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class DistributedIdTest {

    @Resource
    private DistributedIdForRedis distributedIdForRedis;

    @Test
    public void test() {
        System.out.println(distributedIdForRedis.nextId("tbl_order_info"));
        System.out.println(distributedIdForRedis.nextId("tbl_order_info"));
        System.out.println(distributedIdForRedis.nextId("tbl_order_info"));
        System.out.println(distributedIdForRedis.nextId("tbl_order_info"));
    }

}
