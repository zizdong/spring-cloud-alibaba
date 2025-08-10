package cn.dzz.stock.cn.dzz.stock.inner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Value("${server.port}")
    public int serverPort;

    @GetMapping
    public String getStock() {
        return "Get Stock from Stock Service!";
    }

    @GetMapping("/port")
    public String getPort() {
        return "Get Stock from Stock Service Port is :" + serverPort;
    }

}
