package web.ssm.mapper;

import org.apache.ibatis.annotations.*;
import web.ssm.entity.BorrowEntity;
import java.util.List;

@Mapper
public interface BorrowMapper {
  @Select("SELECT b.*, bo.book_name, u.user_name " +
      "FROM tb_borrow b " +
      "LEFT JOIN tb_book bo ON b.book_id = bo.book_id " +
      "LEFT JOIN tb_user u ON b.user_id = u.user_id " +
      "WHERE (#{keyword} IS NULL OR #{keyword} = '' OR " +
      "bo.book_name LIKE CONCAT('%',#{keyword},'%') OR " +
      "u.user_name LIKE CONCAT('%',#{keyword},'%')) " +
      "AND (#{userId} IS NULL OR b.user_id = #{userId}) " +
      "ORDER BY " +
      "CASE status " +
      "  WHEN 'BORROWING' THEN 1 " +
      "  WHEN 'OVERDUE' THEN 2 " +
      "  WHEN 'RETURNED' THEN 3 " +
      "END, " +
      "b.borrow_time DESC " +
      "LIMIT #{start}, #{pageSize}")
  List<BorrowEntity> list4Table(@Param("start") Integer start,
      @Param("pageSize") Integer pageSize,
      @Param("keyword") String keyword,
      @Param("userId") Integer userId);

  @Select("SELECT COUNT(*) FROM tb_borrow b " +
      "LEFT JOIN tb_book bo ON b.book_id = bo.book_id " +
      "LEFT JOIN tb_user u ON b.user_id = u.user_id " +
      "WHERE (#{keyword} IS NULL OR #{keyword} = '' OR " +
      "bo.book_name LIKE CONCAT('%',#{keyword},'%') OR " +
      "u.user_name LIKE CONCAT('%',#{keyword},'%')) " +
      "AND (#{userId} IS NULL OR b.user_id = #{userId})")
  Integer count4Table(@Param("keyword") String keyword,
      @Param("userId") Integer userId);

  @Insert("INSERT INTO tb_borrow (book_id, user_id, borrow_time, return_time, " +
      "status, gmt_create) VALUES (#{bookId}, #{userId}, #{borrowTime}, " +
      "#{returnTime}, #{status}, #{gmtCreate})")
  @Options(useGeneratedKeys = true, keyProperty = "borrowId")
  Integer add(BorrowEntity borrow);

  @Update("UPDATE tb_borrow SET actual_return_time = #{actualReturnTime}, " +
      "status = #{status}, gmt_modified = #{gmtModified} " +
      "WHERE borrow_id = #{borrowId}")
  Integer return_(BorrowEntity borrow);

  @Update("UPDATE tb_borrow SET return_time = #{returnTime}, " +
      "gmt_modified = #{gmtModified} " +
      "WHERE borrow_id = #{borrowId}")
  Integer renew(BorrowEntity borrow);

  @Select("SELECT * FROM tb_borrow WHERE borrow_id = #{borrowId}")
  BorrowEntity getById(@Param("borrowId") Integer borrowId);

  /**
   * 更新所有逾期的借阅记录状态
   */
  @Update("UPDATE tb_borrow SET status = 'OVERDUE' WHERE status = 'BORROWING' AND return_time < NOW()")
  int updateOverdueBorrows();
}