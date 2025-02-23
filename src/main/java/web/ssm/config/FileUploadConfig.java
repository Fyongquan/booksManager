package web.ssm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件上传配置类
 * 负责：
 * - 配置上传路径
 * - 创建上传目录
 * - 处理文件上传相关配置
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

  private static final Logger logger = LoggerFactory.getLogger(FileUploadConfig.class);

  @Value("${file.frontend-assets}")
  private String uploadPath;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 配置静态资源映射
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:src/main/resources/static/uploads/");
  }

  /**
   * 初始化方法
   * Spring容器启动时自动执行
   * 用于创建文件上传目录
   */
  @PostConstruct
  public void init() {
    // 创建上传目录
    File uploadDir = new File("src/main/resources/static/uploads");
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }
  }
}