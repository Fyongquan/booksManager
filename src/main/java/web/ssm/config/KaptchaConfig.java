package web.ssm.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码配置类
 * 用于配置验证码的生成规则和样式
 * 基于 Google Kaptcha 实现
 */
@Configuration // 标记为Spring配置类
public class KaptchaConfig {
  @Bean
  public Producer kaptchaProducer() {
    Properties properties = new Properties();
    properties.setProperty("kaptcha.border", "yes");
    properties.setProperty("kaptcha.border.color", "119,119,119");
    properties.setProperty("kaptcha.image.width", "120");
    properties.setProperty("kaptcha.image.height", "40");
    properties.setProperty("kaptcha.textproducer.char.space", "8");
    properties.setProperty("kaptcha.textproducer.font.size", "26");
    properties.setProperty("kaptcha.textproducer.font.color", "51,122,183");
    properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYAZ");
    properties.setProperty("kaptcha.textproducer.char.length", "4");
    properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
    properties.setProperty("kaptcha.noise.color", "238,238,238");
    DefaultKaptcha kaptcha = new DefaultKaptcha();
    Config config = new Config(properties);
    kaptcha.setConfig(config);
    return kaptcha;
  }
}
