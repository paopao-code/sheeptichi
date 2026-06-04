package com.sheepfarm.controller;

import com.sheepfarm.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * 后端联通测试接口。
     *
     * 调用方式：
     * GET http://localhost:8083/api/health
     *
     * 说明：
     * 如果返回 code=1，说明后端服务已经正常启动。
     */
    @GetMapping("/health")
    public Result<Map<String, String>> health() {
        return Result.success(Map.of(
                "status", "ok",
                "service", "sheep-backend"
        ));
    }
}
