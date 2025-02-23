package web.ssm.entity;

import java.util.Date;

public class LoginLogEntity {
  private Integer id;
  private String loginCode;
  private String ipAddress;
  private String loginStatus; // SUCCESS/FAILED
  private String loginMessage;
  private Date loginTime;
  private String userAgent; // 浏览器信息

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getLoginCode() {
    return loginCode;
  }

  public void setLoginCode(String loginCode) {
    this.loginCode = loginCode;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getLoginStatus() {
    return loginStatus;
  }

  public void setLoginStatus(String loginStatus) {
    this.loginStatus = loginStatus;
  }

  public String getLoginMessage() {
    return loginMessage;
  }

  public void setLoginMessage(String loginMessage) {
    this.loginMessage = loginMessage;
  }

  public Date getLoginTime() {
    return loginTime;
  }

  public void setLoginTime(Date loginTime) {
    this.loginTime = loginTime;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  @Override
  public String toString() {
    return "LoginLogEntity{" +
        "id=" + id +
        ", loginCode='" + loginCode + '\'' +
        ", ipAddress='" + ipAddress + '\'' +
        ", loginStatus='" + loginStatus + '\'' +
        ", loginMessage='" + loginMessage + '\'' +
        ", loginTime=" + loginTime +
        ", userAgent='" + userAgent + '\'' +
        '}';
  }
}