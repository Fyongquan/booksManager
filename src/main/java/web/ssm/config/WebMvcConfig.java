package web.ssm.config;

import web.ssm.interceptor.TokenInterceptor;
import web.ssm.interceptor.AdminAuthInterceptor;
import web.ssm.utils.PathUtils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;

/**
 * Web MVC 配置类
 * 统一配置：
 * - 静态资源映射
 * - 拦截器（Token验证）
 * - 跨域配置
 * - 消息转换器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final PathUtils pathUtils;

  @Autowired
  private TokenInterceptor tokenInterceptor;

  @Autowired
  private AdminAuthInterceptor adminAuthInterceptor;

  @Autowired
  public WebMvcConfig(PathUtils pathUtils) {
    this.pathUtils = pathUtils;
  }

  /**
   * 配置跨域请求
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
  }

  /**
   * 配置拦截器
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tokenInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns(
            "/api/login/**", // 登录相关接口
            "/api/register/**" // 注册相关接口
        );
    registry.addInterceptor(adminAuthInterceptor)
        .addPathPatterns(
            "/api/admin/**", // 管理员接口
            "/api/user/add", // 添加用户
            "/api/user/delete" // 删除用户
        );
  }

  /**
   * 配置静态资源映射
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/static/");
    registry.addResourceHandler("/resource/**")
        .addResourceLocations("classpath:/templates/");
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + System.getProperty("user.dir") + "/src/main/resources/static/uploads/");
  }

  /**
   * 配置消息转换器
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(0, new CustomMappingJackson2HttpMessageConverter());
  }
}
