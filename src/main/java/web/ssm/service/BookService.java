package web.ssm.service;

import web.ssm.dto.BookDTO;
import java.util.List;
import org.apache.commons.lang3.mutable.MutableInt;
import web.ssm.dto.TableReqDTO;
import web.ssm.dto.TableRspDTO;

public interface BookService {
  BookDTO getById(Integer bookId);

  TableRspDTO list4Table(TableReqDTO tableReqDTO);

  List<BookDTO> list();

  Integer add(BookDTO bookDTO);

  boolean update(BookDTO bookDTO);

  boolean delete(Integer bookId);

  List<BookDTO> getPageList(String keyword, Integer pageNum, Integer pageSize, MutableInt total);

  /**
   * 获取所有图书
   */
  List<BookDTO> getAllBooks();

  /**
   * 批量保存图书
   */
  void batchSave(List<BookDTO> books);
}