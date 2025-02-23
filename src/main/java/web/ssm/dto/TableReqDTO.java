package web.ssm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 表格请求数据传输对象
 * 用于封装分页查询的请求参数
 */
public class TableReqDTO {

  /** 每页显示的记录数 */
  private Integer pageSize;

  /** 当前页码 */
  @JsonProperty("pageNum")
  private Integer pageNum;

  /** 搜索关键词 */
  private String queryText;

  /** 用户ID，用于筛选特定用户的数据 */
  private Integer userId;

  /**
   * 无参构造函数
   * 设置默认值：
   * - pageSize: 10
   * - currentPage: 1
   * - queryText: ""
   */
  public TableReqDTO() {
    this.pageSize = 10;
    this.pageNum = 1;
    this.queryText = "";
  }

  /**
   * 带参构造函数
   * 设置分页和查询参数，并提供默认值
   * 
   * @param pageSize    每页记录数，默认5
   * @param currentPage 当前页码，默认1
   * @param queryText   搜索关键词，默认空字符串
   */
  public TableReqDTO(Integer pageSize, Integer currentPage, String queryText) {
    this.pageSize = pageSize == null ? 5 : pageSize;
    this.pageNum = currentPage == null ? 1 : currentPage;
    this.queryText = queryText == null ? "" : queryText;
  }

  /**
   * 获取每页记录数
   * 
   * @return 每页记录数
   */
  public Integer getPageSize() {
    return pageSize;
  }

  /**
   * 设置每页记录数
   * 
   * @param pageSize 每页记录数
   */
  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * 获取当前页码
   * 
   * @return 当前页码
   */
  public Integer getPageNum() {
    return pageNum == null ? 1 : pageNum;
  }

  /**
   * 设置当前页码
   * 
   * @param pageNum 当前页码
   */
  public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
  }

  /**
   * 获取查询起始位置
   * 根据页码和每页记录数计算SQL查询的起始位置
   * 
   * @return 查询起始位置
   */
  public Integer getStart() {
    if (this.pageNum == null || this.pageSize == null) {
      return 0;
    }
    return (this.pageNum - 1) * this.pageSize;
  }

  /**
   * 获取搜索关键词
   * 
   * @return 搜索关键词
   */
  public String getQueryText() {
    return queryText;
  }

  /**
   * 设置搜索关键词
   * 
   * @param queryText 搜索关键词
   */
  public void setQueryText(String queryText) {
    this.queryText = queryText;
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

  @Override
  public String toString() {
    return "TableReqDTO{" +
        "pageSize=" + pageSize +
        ", pageNum=" + pageNum +
        ", queryText='" + queryText + '\'' +
        ", userId=" + userId +
        "}";
  }
}
