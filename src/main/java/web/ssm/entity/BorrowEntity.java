package web.ssm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class BorrowEntity {
  private Integer borrowId;
  private Integer bookId;
  private Integer userId;
  private String bookName; // 冗余字段，方便查询
  private String userName; // 冗余字段，方便查询

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date borrowTime; // 借阅时间

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date returnTime; // 应还时间

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date actualReturnTime; // 实际归还时间

  private String status; // 状态：BORROWING-借阅中, RETURNED-已归还, OVERDUE-已逾期

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date gmtCreate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date gmtModified;

  // Getters and Setters
  public Integer getBorrowId() {
    return borrowId;
  }

  public void setBorrowId(Integer borrowId) {
    this.borrowId = borrowId;
  }

  public Integer getBookId() {
    return bookId;
  }

  public void setBookId(Integer bookId) {
    this.bookId = bookId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getBookName() {
    return bookName;
  }

  public void setBookName(String bookName) {
    this.bookName = bookName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Date getBorrowTime() {
    return borrowTime;
  }

  public void setBorrowTime(Date borrowTime) {
    this.borrowTime = borrowTime;
  }

  public Date getReturnTime() {
    return returnTime;
  }

  public void setReturnTime(Date returnTime) {
    this.returnTime = returnTime;
  }

  public Date getActualReturnTime() {
    return actualReturnTime;
  }

  public void setActualReturnTime(Date actualReturnTime) {
    this.actualReturnTime = actualReturnTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }
}