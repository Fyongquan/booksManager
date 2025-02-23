package web.ssm.mapper;

import org.apache.ibatis.annotations.*;
import web.ssm.entity.BookEntity;
import web.ssm.dto.BookDTO;
import web.ssm.annotation.OperationLog;
import java.util.List;

@Mapper
public interface BookMapper {

  @Select("SELECT * FROM tb_book WHERE book_id = #{bookId}")
  BookEntity getById(Integer bookId);

  @Select("SELECT * FROM tb_book")
  List<BookEntity> list();

  @Select("<script>" +
      "SELECT * FROM tb_book " +
      "<if test='keyword != null and keyword != \"\"'>" +
      "WHERE book_name LIKE CONCAT('%',#{keyword},'%') " +
      "OR author LIKE CONCAT('%',#{keyword},'%') " +
      "OR publisher LIKE CONCAT('%',#{keyword},'%') " +
      "OR isbn LIKE CONCAT('%',#{keyword},'%') " +
      "OR category LIKE CONCAT('%',#{keyword},'%') " +
      "</if>" +
      "ORDER BY gmt_create DESC " +
      "LIMIT #{offset}, #{pageSize}" +
      "</script>")
  List<BookEntity> search(@Param("keyword") String keyword,
      @Param("offset") Integer offset,
      @Param("pageSize") Integer pageSize);

  @Select("<script>" +
      "SELECT COUNT(*) FROM tb_book " +
      "<if test='keyword != null and keyword != \"\"'>" +
      "WHERE book_name LIKE CONCAT('%',#{keyword},'%') " +
      "OR author LIKE CONCAT('%',#{keyword},'%') " +
      "OR publisher LIKE CONCAT('%',#{keyword},'%') " +
      "OR isbn LIKE CONCAT('%',#{keyword},'%') " +
      "OR category LIKE CONCAT('%',#{keyword},'%') " +
      "</if>" +
      "</script>")
  Integer count(@Param("keyword") String keyword);

  @Insert("INSERT INTO tb_book(book_name, author, publisher, price, stock, category, description, isbn, publish_date, gmt_create) "
      +
      "VALUES(#{bookName}, #{author}, #{publisher}, #{price}, #{stock}, #{category}, #{description}, #{isbn}, #{publishDate}, now())")
  @Options(useGeneratedKeys = true, keyProperty = "bookId")
  @OperationLog("添加图书")
  int add(BookEntity book);

  @Update("UPDATE tb_book SET " +
      "book_name = #{bookName}, " +
      "author = #{author}, " +
      "publisher = #{publisher}, " +
      "price = #{price}, " +
      "stock = #{stock}, " +
      "category = #{category}, " +
      "description = #{description}, " +
      "isbn = #{isbn}, " +
      "publish_date = #{publishDate}, " +
      "gmt_modified = now() " +
      "WHERE book_id = #{bookId}")
  @OperationLog("更新图书")
  int update(BookEntity book);

  @Delete("DELETE FROM tb_book WHERE book_id = #{bookId}")
  @OperationLog("删除图书")
  int delete(@Param("bookId") Integer bookId);

  @Update("UPDATE tb_book SET stock = #{stock} WHERE book_id = #{bookId}")
  Integer updateStock(BookEntity book);

  /**
   * 获取所有图书
   */
  @Select("SELECT * FROM tb_book ORDER BY gmt_create DESC")
  List<BookEntity> getAllBooks();

  /**
   * 批量保存图书
   */
  @Insert("<script>" +
      "INSERT INTO tb_book (book_name, author, publisher, isbn, price, stock, " +
      "category, publish_date, description, gmt_create, gmt_modified) VALUES " +
      "<foreach collection='list' item='book' separator=','>" +
      "(#{book.bookName}, #{book.author}, #{book.publisher}, #{book.isbn}, " +
      "#{book.price}, #{book.stock}, #{book.category}, #{book.publishDate}, " +
      "#{book.description}, #{book.gmtCreate}, #{book.gmtModified})" +
      "</foreach>" +
      "</script>")
  void batchSave(@Param("list") List<BookEntity> books);

  /**
   * 根据ISBN查询图书
   */
  @Select("SELECT * FROM tb_book WHERE isbn = #{isbn} LIMIT 1")
  BookEntity getByIsbn(String isbn);

  /**
   * 更新图书库存
   */
  @Update("UPDATE tb_book SET stock = stock + #{stock} WHERE isbn = #{isbn}")
  int updateStockByIsbn(@Param("isbn") String isbn, @Param("stock") Integer stock);

  /**
   * 检查图书是否有借阅记录
   */
  @Select("SELECT COUNT(*) FROM tb_borrow WHERE book_id = #{bookId}")
  int checkBorrowRecord(Integer bookId);

  @Select("SELECT * FROM tb_book " +
      "WHERE (#{queryText} IS NULL OR #{queryText} = '' OR " +
      "book_name LIKE CONCAT('%',#{queryText},'%') OR " +
      "author LIKE CONCAT('%',#{queryText},'%')) " +
      "ORDER BY gmt_create DESC " +
      "LIMIT #{start}, #{pageSize}")
  List<BookDTO> list4Table(@Param("start") Integer start,
      @Param("pageSize") Integer pageSize,
      @Param("queryText") String queryText);
}