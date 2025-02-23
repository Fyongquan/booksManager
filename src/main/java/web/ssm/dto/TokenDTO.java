package web.ssm.dto;

/**
 * Token数据传输对象
 * 用于封装JWT token中包含的用户信息
 */
public class TokenDTO {

  /** 用户ID */
  private Integer loginId;

  /** 登录账号 */
  private String loginCode;

  /** 用户名称 */
  private String loginName;

  /**
   * 用户类型
   * - admin: 管理员
   * - user: 普通用户
   */
  private String userType;

  /**
   * 角色类型
   * - ADMIN: 管理员角色
   * - USER: 普通用户角色
   */
  private String roleType;

  /**
   * 无参构造函数
   */
  public TokenDTO() {
  }

  /**
   * 基础构造函数
   * 
   * @param loginId   用户ID
   * @param loginCode 登录账号
   * @param loginName 用户名称
   */
  public TokenDTO(Integer loginId, String loginCode, String loginName) {
    this.loginId = loginId;
    this.loginCode = loginCode;
    this.loginName = loginName;
  }

  /**
   * 获取用户ID
   * 
   * @return 用户ID
   */
  public Integer getLoginId() {
    return loginId;
  }

  /**
   * 设置用户ID
   * 
   * @param loginId 用户ID
   */
  public void setLoginId(Integer loginId) {
    this.loginId = loginId;
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
   * 获取用户名称
   * 
   * @return 用户名称
   */
  public String getLoginName() {
    return loginName;
  }

  /**
   * 设置用户名称
   * 
   * @param loginName 用户名称
   */
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  /**
   * 获取用户类型
   * 
   * @return 用户类型
   */
  public String getUserType() {
    return userType;
  }

  /**
   * 设置用户类型
   * 
   * @param userType 用户类型
   */
  public void setUserType(String userType) {
    this.userType = userType;
  }

  /**
   * 获取角色类型
   * 
   * @return 角色类型
   */
  public String getRoleType() {
    return roleType;
  }

  /**
   * 设置角色类型
   * 
   * @param roleType 角色类型
   */
  public void setRoleType(String roleType) {
    this.roleType = roleType;
  }

  /**
   * 重写toString方法
   * 返回JSON格式的字符串表示
   * 
   * @return JSON格式的对象字符串
   */
  @Override
  public String toString() {
    return String.format("{" +
        "'loginId':'%d', " +
        "'loginCode':'%s', " +
        "'loginName':'%s', " +
        "'userType':'%s', " +
        "'roleType':'%s'" +
        "}", loginId, loginCode, loginName, userType, roleType);
  }
}
