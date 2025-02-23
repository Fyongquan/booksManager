package web.ssm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserEntity {
  private Integer userId;
  private String loginCode;
  private String loginPwd;
  private String userName;
  private String userImage;
  private String userEmail;
  private String userPhone;
  private String userType;
  private String roleType;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date gmtCreate;
  private Date gmtModified;

  public UserEntity() {
  }

  public UserEntity(Integer userId, String loginCode, String loginPwd, String userName, String userImage,
      String userEmail, String userPhone, String userType, Date gmtCreate) {
    this.userId = userId;
    this.loginCode = loginCode;
    this.loginPwd = loginPwd;
    this.userName = userName;
    this.userImage = userImage;
    this.userEmail = userEmail;
    this.userPhone = userPhone;
    this.userType = userType;
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

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public String getRoleType() {
    return roleType;
  }

  public void setRoleType(String roleType) {
    this.roleType = roleType;
  }
}
