package web.ssm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 安全配置类
 * 用于配置系统的安全访问策略
 * 包括：URL访问权限、跨域、Session管理等
 */
@Configuration
@EnableWebSecurity // 启用Spring Security的Web安全支持
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

  /**
   * 配置HTTP安全策略
   * 
   * @param http HTTP安全构建器
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    logger.info("配置 Security");
    http
        // 禁用CSRF防护
        .csrf().disable()
        // 使用无状态会话
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        // 允许匿名访问的路径
        .antMatchers("/api/login/**").permitAll() // 登录接口
        .antMatchers("/api/register/**").permitAll() // 注册接口
        .antMatchers("/static/**").permitAll() // 静态资源
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS请求
        .antMatchers("/uploads/**").permitAll() // 允许访问上传文件
        // 其他所有请求都需要认证（当前为调试模式，允许所有请求）
        .anyRequest().permitAll();

    // 启用跨域支持
    http.cors();
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("http://localhost:5173");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
