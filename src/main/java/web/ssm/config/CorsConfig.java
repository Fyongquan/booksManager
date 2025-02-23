package web.ssm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 * 用于解决前后端分离开发时的跨域访问问题
 * 允许前端应用访问后端API接口
 */
@Configuration // 标记为Spring配置类
public class CorsConfig implements WebMvcConfigurer {

  /**
   * 配置跨域请求的规则
   * 
   * @param registry 跨域注册器
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**") // 配置可以被跨域的路径，/**表示所有路径
        .allowedOriginPatterns("*") // 允许所有域名进行跨域调用
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
        .allowedHeaders("*") // 允许所有请求头
        .exposedHeaders("Content-Disposition") // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        .allowCredentials(true) // 是否允许发送Cookie信息
        .maxAge(3600); // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再次发送预检请求
  }
}
