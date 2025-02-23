package web.ssm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 借阅信息数据传输对象
 * 用于在各层之间传递借阅相关数据
 */
public class BorrowDTO {
  /** 借阅记录ID */
  private Integer borrowId;

  /** 图书ID */
  private Integer bookId;

  /** 用户ID */
  private Integer userId;

  /** 图书名称 */
  private String bookName;

  /** 用户名称 */
  private String userName;

  /** 借阅时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date borrowTime;

  /** 应还时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date returnTime;

  /** 实际归还时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date actualReturnTime;

  /**
   * 借阅状态
   * - BORROWING: 借阅中
   * - RETURNED: 已归还
   * - OVERDUE: 已逾期
   */
  private String status;

  /** 借阅天数 */
  private Integer days;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date gmtCreate;

  /** 修改时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date gmtModified;

  // Getters and Setters
  /**
   * 获取借阅记录ID
   * 
   * @return 借阅记录ID
   */
  public Integer getBorrowId() {
    return borrowId;
  }

  /**
   * 设置借阅记录ID
   * 
   * @param borrowId 借阅记录ID
   */
  public void setBorrowId(Integer borrowId) {
    this.borrowId = borrowId;
  }

  /**
   * 获取图书ID
   * 
   * @return 图书ID
   */
  public Integer getBookId() {
    return bookId;
  }

  /**
   * 设置图书ID
   * 
   * @param bookId 图书ID
   */
  public void setBookId(Integer bookId) {
    this.bookId = bookId;
  }

  /**
   * 获取用户ID
   * 
   * @return 用户ID
   */
  public Integer getUserId() {
    return userId;
  }

  /**
   * 设置用户ID
   * 
   * @param userId 用户ID
   */
  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  /**
   * 获取图书名称
   * 
   * @return 图书名称
   */
  public String getBookName() {
    return bookName;
  }

  /**
   * 设置图书名称
   * 
   * @param bookName 图书名称
   */
  public void setBookName(String bookName) {
    this.bookName = bookName;
  }

  /**
   * 获取用户名称
   * 
   * @return 用户名称
   */
  public String getUserName() {
    return userName;
  }

  /**
   * 设置用户名称
   * 
   * @param userName 用户名称
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * 获取借阅时间
   * 
   * @return 借阅时间
   */
  public Date getBorrowTime() {
    return borrowTime;
  }

  /**
   * 设置借阅时间
   * 
   * @param borrowTime 借阅时间
   */
  public void setBorrowTime(Date borrowTime) {
    this.borrowTime = borrowTime;
  }

  /**
   * 获取应还时间
   * 
   * @return 应还时间
   */
  public Date getReturnTime() {
    return returnTime;
  }

  /**
   * 设置应还时间
   * 
   * @param returnTime 应还时间
   */
  public void setReturnTime(Date returnTime) {
    this.returnTime = returnTime;
  }

  /**
   * 获取实际归还时间
   * 
   * @return 实际归还时间
   */
  public Date getActualReturnTime() {
    return actualReturnTime;
  }

  /**
   * 设置实际归还时间
   * 
   * @param actualReturnTime 实际归还时间
   */
  public void setActualReturnTime(Date actualReturnTime) {
    this.actualReturnTime = actualReturnTime;
  }

  /**
   * 获取借阅状态
   * 
   * @return 借阅状态
   */
  public String getStatus() {
    return status;
  }

  /**
   * 设置借阅状态
   * 
   * @param status 借阅状态
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * 获取借阅天数
   * 
   * @return 借阅天数
   */
  public Integer getDays() {
    return days;
  }

  /**
   * 设置借阅天数
   * 
   * @param days 借阅天数
   */
  public void setDays(Integer days) {
    this.days = days;
  }

  /**
   * 获取创建时间
   * 
   * @return 创建时间
   */
  public Date getGmtCreate() {
    return gmtCreate;
  }

  /**
   * 设置创建时间
   * 
   * @param gmtCreate 创建时间
   */
  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  /**
   * 获取修改时间
   * 
   * @return 修改时间
   */
  public Date getGmtModified() {
    return gmtModified;
  }

  /**
   * 设置修改时间
   * 
   * @param gmtModified 修改时间
   */
  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }
}