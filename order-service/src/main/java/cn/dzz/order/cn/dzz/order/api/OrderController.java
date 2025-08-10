package cn.dzz.order.cn.dzz.order.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/stock")
    public String getStock() {
        // 调用Stock service 获取库存信息
        // 2021.0.4之前的版本，Spring Cloud默认集成了Ribbon，所以访问其他服务时，不需要显式集成Ribbon或者其他负载均衡的组件。
        // 在高版本中，Spring Cloud不会再默认集成Ribbon，所以需要显式的集成Ribbon或其他负载均衡组件
        // 并且RestTemplate需要使用@LoadBalanced注解声明
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://stock-service/stock", String.class);

        return forEntity.getBody();
    }

    @GetMapping("/stock/balance")
    public String getStockBalance() {

        // 当Service在Nacos中注册多个时，此时会应用上负载均衡，默认的负载均衡策略是轮询
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://stock-service/stock/port", String.class);

        return forEntity.getBody();
    }

}
