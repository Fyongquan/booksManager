package web.ssm.utils;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Properties;

/**
 * 验证码工具类
 * 用于生成图形验证码和Base64编码的图片
 * 基于 Kaptcha 实现
 */
public class CaptchaUtil {
  /** 验证码生成器 */
  private static Producer captchaProducer;

  /**
   * 静态初始化块
   * 配置验证码的样式和参数
   */
  static {
    DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
    Properties properties = new Properties();
    // 设置边框
    properties.setProperty("kaptcha.border", "yes");
    properties.setProperty("kaptcha.border.color", "105,179,90");
    // 设置字体颜色
    properties.setProperty("kaptcha.textproducer.font.color", "blue");
    // 设置图片宽高
    properties.setProperty("kaptcha.image.width", "100");
    properties.setProperty("kaptcha.image.height", "40");
    // 设置字体大小
    properties.setProperty("kaptcha.textproducer.font.size", "30");
    // 设置session key
    properties.setProperty("kaptcha.session.key", "code");
    // 设置验证码长度
    properties.setProperty("kaptcha.textproducer.char.length", "4");
    Config config = new Config(properties);
    defaultKaptcha.setConfig(config);
    captchaProducer = defaultKaptcha;
  }

  /**
   * 生成验证码文本
   * 
   * @param length 验证码长度
   * @return 验证码文本
   */
  public static String generateVerifyCode(int length) {
    return captchaProducer.createText();
  }

  /**
   * 生成验证码图片并输出到流
   * 
   * @param w    图片宽度
   * @param h    图片高度
   * @param os   输出流
   * @param code 验证码文本
   * @throws IOException 输出异常
   */
  public static void outputImage(int w, int h, OutputStream os, String code) throws IOException {
    BufferedImage image = captchaProducer.createImage(code);
    ImageIO.write(image, "jpg", os);
  }

  /**
   * 验证码对象
   * 包含验证码文本和Base64编码的图片
   */
  public static class Captcha {
    /** 验证码文本 */
    private String code;
    /** Base64编码的图片数据 */
    private String imageBase64;

    // Getter和Setter方法
    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getImageBase64() {
      return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
      this.imageBase64 = imageBase64;
    }
  }

  /**
   * 生成完整的验证码对象
   * 包含验证码文本和Base64编码的图片
   * 
   * @return Captcha对象
   * @throws RuntimeException 生成失败时抛出异常
   */
  public static Captcha generateCaptcha() {
    // 生成验证码文本
    String code = generateVerifyCode(4);
    // 生成验证码图片
    BufferedImage image = captchaProducer.createImage(code);

    try {
      // 将图片转换为Base64
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "png", baos);
      byte[] bytes = baos.toByteArray();
      String base64Image = Base64.getEncoder().encodeToString(bytes);

      // 创建并返回验证码对象
      Captcha captcha = new Captcha();
      captcha.setCode(code);
      captcha.setImageBase64("data:image/png;base64," + base64Image);
      return captcha;
    } catch (IOException e) {
      throw new RuntimeException("生成验证码失败", e);
    }
  }
}