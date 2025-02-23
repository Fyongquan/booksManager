package web.ssm.entity;

import java.util.Date;

public class OperationLogEntity {
  private Integer id;
  private Integer userId;
  private String userName;
  private String operation;
  private String method;
  private String params;
  private String ip;
  private Date gmtCreate;

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  @Override
  public String toString() {
    return "OperationLogEntity{" +
        "id=" + id +
        ", userId=" + userId +
        ", userName='" + userName + '\'' +
        ", operation='" + operation + '\'' +
        ", method='" + method + '\'' +
        ", params='" + params + '\'' +
        ", ip='" + ip + '\'' +
        ", gmtCreate=" + gmtCreate +
        '}';
  }
}