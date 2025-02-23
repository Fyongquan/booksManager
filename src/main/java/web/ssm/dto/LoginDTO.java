package web.ssm.dto;

/**
 * 登录数据传输对象
 * 用于封装登录请求的相关参数
 */
public class LoginDTO {

  /** 登录账号 */
  private String loginCode;

  /** 登录密码 */
  private String loginPwd;

  /**
   * 验证码标识
   * 用于关联服务端存储的验证码
   */
  private String uuid;

  /** 用户输入的验证码 */
  private String imageCode;

  /**
   * 登录类型
   * - admin: 管理员登录
   * - user: 普通用户登录
   */
  private String loginType;

  /**
   * 无参构造函数
   */
  public LoginDTO() {
  }

  /**
   * 构造函数
   * 
   * @param loginCode 登录账号
   * @param loginPwd  登录密码
   * @param uuid      验证码标识
   * @param imageCode 验证码
   */
  public LoginDTO(String loginCode, String loginPwd, String uuid, String imageCode) {
    this.loginCode = loginCode;
    this.loginPwd = loginPwd;
    this.uuid = uuid;
    this.imageCode = imageCode;
  }

  /**
   * 获取登录账号
   * 
   * @return 登录账号
   */
  public String getLoginCode() {
    return loginCode;
  }

  /**
   * 设置登录账号
   * 
   * @param loginCode 登录账号
   */
  public void setLoginCode(String loginCode) {
    this.loginCode = loginCode;
  }

  /**
   * 获取登录密码
   * 
   * @return 登录密码
   */
  public String getLoginPwd() {
    return loginPwd;
  }

  /**
   * 设置登录密码
   * 
   * @param loginPwd 登录密码
   */
  public void setLoginPwd(String loginPwd) {
    this.loginPwd = loginPwd;
  }

  /**
   * 获取验证码标识
   * 
   * @return 验证码标识
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * 设置验证码标识
   * 
   * @param uuid 验证码标识
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * 获取验证码
   * 
   * @return 验证码
   */
  public String getImageCode() {
    return imageCode;
  }

  /**
   * 设置验证码
   * 
   * @param imageCode 验证码
   */
  public void setImageCode(String imageCode) {
    this.imageCode = imageCode;
  }

  /**
   * 获取登录类型
   * 
   * @return 登录类型
   */
  public String getLoginType() {
    return loginType;
  }

  /**
   * 设置登录类型
   * 
   * @param loginType 登录类型
   */
  public void setLoginType(String loginType) {
    this.loginType = loginType;
  }
}
