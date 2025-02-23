package web.ssm.dto;

/**
 * 验证码数据传输对象
 * 用于在前后端之间传递验证码相关信息
 */
public class KaptchaDTO {
  /**
   * 验证码唯一标识
   * 用于关联验证码图片和验证码文本
   */
  private String uuid;

  /**
   * 验证码图片的Base64编码
   * 包含图片数据，可直接用于前端显示
   * 格式：data:image/png;base64,xxxxx
   */
  private String base64;

  /**
   * 无参构造函数
   */
  public KaptchaDTO() {
  }

  /**
   * 全参构造函数
   * 
   * @param uuid   验证码唯一标识
   * @param base64 验证码图片的Base64编码
   */
  public KaptchaDTO(String uuid, String base64) {
    this.uuid = uuid;
    this.base64 = base64;
  }

  /**
   * 获取验证码唯一标识
   * 
   * @return 验证码唯一标识
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * 设置验证码唯一标识
   * 
   * @param uuid 验证码唯一标识
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * 获取验证码图片的Base64编码
   * 
   * @return Base64编码的图片数据
   */
  public String getBase64() {
    return base64;
  }

  /**
   * 设置验证码图片的Base64编码
   * 
   * @param base64 Base64编码的图片数据
   */
  public void setBase64(String base64) {
    this.base64 = base64;
  }
}
