package cn.chenyukun.synapse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SynapseAI 主启动类
 */
@SpringBootApplication
@MapperScan({"cn.chenyukun.synapse.module.llm.mapper", "cn.chenyukun.synapse.module.agent.mapper"})
public class SynapseApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SynapseApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  SynapseAI 启动成功！");
        System.out.println("  访问: http://localhost:4396/api/health");
        System.out.println("========================================\n");
    }
}

