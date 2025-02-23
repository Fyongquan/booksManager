package web.ssm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户数据传输对象
 * 用于在各层之间传递用户信息
 * 支持JSON序列化和反序列化
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 登录账号
   * 用户的唯一标识符
   */
  @JsonProperty("loginCode")
  private String loginCode;

  /**
   * 登录密码
   * 在toString方法中会被保护，不会明文显示
   */
  @JsonProperty("loginPwd")
  private String loginPwd;

  /** 用户ID */
  private Integer userId;

  /** 用户名称 */
  private String userName;

  /** 用户头像（旧版） */
  private String userImage;

  /** 用户邮箱 */
  private String userEmail;

  /** 用户手机号 */
  private String userPhone;

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
   * 创建时间
   * 使用GMT+8时区
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date gmtCreate;

  /** 用户头像（新版） */
  private String avatar;

  /**
   * 无参构造函数
   */
  public UserDTO() {
  }

  /**
   * 全参构造函数
   * 
   * @param userId    用户ID
   * @param loginCode 登录账号
   * @param loginPwd  登录密码
   * @param userName  用户名称
   * @param userImage 用户头像（旧版）
   * @param userEmail 用户邮箱
   * @param userPhone 用户手机号
   * @param userType  用户类型
   * @param roleType  角色类型
   * @param avatar    用户头像（新版）
   * @param gmtCreate 创建时间
   */
  public UserDTO(Integer userId, String loginCode, String loginPwd, String userName, String userImage, String userEmail,
      String userPhone, String userType, String roleType, String avatar, Date gmtCreate) {
    this.userId = userId;
    this.loginCode = loginCode;
    this.loginPwd = loginPwd;
    this.userName = userName;
    this.userImage = userImage;
    this.userEmail = userEmail;
    this.userPhone = userPhone;
    this.userType = userType;
    this.roleType = roleType;
    this.avatar = avatar;
    this.gmtCreate = gmtCreate;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getLoginCode() {
    return loginCode;
  }

  public void setLoginCode(String loginCode) {
    this.loginCode = loginCode;
  }

  public String getLoginPwd() {
    return loginPwd;
  }

  public void setLoginPwd(String loginPwd) {
    this.loginPwd = loginPwd;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserImage() {
    return userImage;
  }

  public void setUserImage(String userImage) {
    this.userImage = userImage;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getUserPhone() {
    return userPhone;
  }

  public void setUserPhone(String userPhone) {
    this.userPhone = userPhone;
  }

  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getRoleType() {
    return roleType;
  }

  public void setRoleType(String roleType) {
    this.roleType = roleType;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  /**
   * 重写toString方法
   * 密码字段会被保护，显示为[PROTECTED]
   * 
   * @return 对象的字符串表示
   */
  @Override
  public String toString() {
    return "UserDTO{" +
        "loginCode='" + loginCode + '\'' +
        ", loginPwd='[PROTECTED]'" +
        ", userId=" + userId +
        ", userName='" + userName + '\'' +
        ", userImage='" + userImage + '\'' +
        ", userEmail='" + userEmail + '\'' +
        ", userPhone='" + userPhone + '\'' +
        ", userType='" + userType + '\'' +
        ", roleType='" + roleType + '\'' +
        ", gmtCreate=" + gmtCreate +
        ", avatar='" + avatar + '\'' +
        '}';
  }
}
