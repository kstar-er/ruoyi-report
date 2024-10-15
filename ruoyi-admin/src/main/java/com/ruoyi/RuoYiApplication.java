package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 启动程序
 *
 * @author ruoyi
 */

@SpringBootApplication
public class RuoYiApplication {

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication application = new SpringApplication(RuoYiApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  ruoyi-report数据报表系统启动成功   ლ(´ڡ`ლ)ﾞ");
        System.out.println("本地前端启动端口：http://127.0.0.1:8834/index.html ");
    }

}
