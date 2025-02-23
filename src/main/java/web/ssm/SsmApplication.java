package web.ssm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Spring Boot 应用程序入口类
 * 
 * 配置说明：
 * - @SpringBootApplication:
 * 组合了@Configuration, @EnableAutoConfiguration 和 @ComponentScan
 * 
 * - @EnableAspectJAutoProxy:
 * 启用AspectJ自动代理，支持AOP功能
 * 用于：
 * - 操作日志记录
 * - 登录日志记录
 * - 权限验证等
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class SsmApplication {

  /**
   * 应用程序入口方法
   * 启动Spring Boot应用
   * 
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    SpringApplication.run(SsmApplication.class, args);
  }
}
