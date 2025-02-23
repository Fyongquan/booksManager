package web.ssm.dto;

import java.util.List;

/**
 * 表格响应数据传输对象
 * 用于封装分页查询的响应结果
 * 包含总记录数和当前页数据列表
 */
public class TableRspDTO {

  /**
   * 总记录数
   * 用于前端计算总页数和分页显示
   */
  private Integer total;

  /**
   * 数据列表
   * 使用泛型List，可以存储任意类型的对象列表
   * 包含当前页的所有记录
   */
  private List<?> list;

  /**
   * 无参构造函数
   */
  public TableRspDTO() {
  }

  /**
   * 全参构造函数
   * 
   * @param total 总记录数
   * @param list  数据列表
   */
  public TableRspDTO(Integer total, List<?> list) {
    this.total = total;
    this.list = list;
  }

  /**
   * 获取总记录数
   * 
   * @return 总记录数
   */
  public Integer getTotal() {
    return total;
  }

  /**
   * 设置总记录数
   * 
   * @param total 总记录数
   */
  public void setTotal(Integer total) {
    this.total = total;
  }

  /**
   * 获取数据列表
   * 
   * @return 当前页的数据列表
   */
  public List<?> getList() {
    return list;
  }

  /**
   * 设置数据列表
   * 
   * @param list 当前页的数据列表
   */
  public void setList(List<?> list) {
    this.list = list;
  }
}
