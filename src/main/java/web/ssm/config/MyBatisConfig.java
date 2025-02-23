package web.ssm.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 * 用于配置MyBatis的Mapper扫描
 * 自动扫描并注册Mapper接口
 */
@Configuration // 标记为Spring配置类
@MapperScan("web.ssm.mapper") // 指定Mapper接口所在的包路径
public class MyBatisConfig {
}