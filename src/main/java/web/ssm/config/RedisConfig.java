package web.ssm.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.*;

/**
 * Redis配置类
 * 配置序列化方式和连接
 */
@Configuration
@EnableCaching // 开启缓存支持
public class RedisConfig extends CachingConfigurerSupport {

  private final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

  /**
   * 配置RedisTemplate
   * 设置序列化方式
   *
   * @param redisConnectionFactory Redis连接工厂
   * @return RedisTemplate实例
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    try {
      // 配置连接工厂
      template.setConnectionFactory(redisConnectionFactory);

      // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
      Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
      ObjectMapper om = new ObjectMapper();
      // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
      om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
      // 指定序列化输入的类型，类必须是非final修饰的
      om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
      jackson2JsonRedisSerializer.setObjectMapper(om);

      // key采用String的序列化方式
      template.setKeySerializer(new StringRedisSerializer());
      // value序列化方式采用jackson
      template.setValueSerializer(jackson2JsonRedisSerializer);
      // hash的key也采用String的序列化方式
      template.setHashKeySerializer(new StringRedisSerializer());
      // hash的value序列化方式采用jackson
      template.setHashValueSerializer(jackson2JsonRedisSerializer);

      template.afterPropertiesSet();

      // 测试连接
      template.getConnectionFactory().getConnection().ping();
      logger.info("Redis连接成功初始化");
    } catch (Exception e) {
      logger.error("Redis初始化失败: {}", e.getMessage(), e);
      throw new RuntimeException("Redis初始化失败", e);
    }
    return template;
  }
}