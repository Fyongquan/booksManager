package web.ssm.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;

/**
 * 路径工具类
 * 用于处理前端项目和文件上传的路径
 * 支持配置化的路径管理
 */
@Component
public class PathUtils {

  /**
   * 前端项目名称
   * 从配置文件中读取，如：vite-project
   */
  @Value("${file.frontend-project}")
  private String frontendProject;

  /**
   * 资源文件路径
   * 从配置文件中读取，如：assets/upload
   */
  @Value("${file.frontend-assets}")
  private String assetsPath;

  /**
   * 获取前端项目的根路径
   * 从当前目录向上查找前端项目目录
   * 
   * @return 前端项目的绝对路径
   * @throws RuntimeException 找不到前端项目目录时抛出异常
   */
  public String getFrontendPath() {
    // 从当前项目根目录向上查找前端项目
    File currentDir = new File("").getAbsoluteFile();
    while (currentDir != null && !new File(currentDir, frontendProject).exists()) {
      currentDir = currentDir.getParentFile();
    }

    if (currentDir == null) {
      throw new RuntimeException("找不到前端项目目录: " + frontendProject);
    }

    return new File(currentDir, frontendProject).getAbsolutePath();
  }

  /**
   * 获取上传文件的完整路径
   * 组合前端项目路径和资源路径
   * 
   * @return 上传目录的绝对路径
   */
  public String getUploadPath() {
    return new File(getFrontendPath(), assetsPath).getAbsolutePath();
  }

  /**
   * 获取文件的相对路径
   * 用于生成可访问的URL路径
   * 
   * @param fileName 文件名
   * @return 相对于前端项目的URL路径，如：/assets/upload/xxx.jpg
   */
  public String getRelativePath(String fileName) {
    return "/" + assetsPath + "/" + fileName;
  }
}