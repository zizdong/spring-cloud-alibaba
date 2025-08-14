package cn.dzz.sentinel.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sentinel-test")
public class SentinelTestController {

    @GetMapping("/flow-rule")
    public String test() {
        return "hello sentinel";
    }

}
