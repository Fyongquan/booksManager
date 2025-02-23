package web.ssm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.ssm.dto.BookDTO;
import web.ssm.dto.TableReqDTO;
import web.ssm.dto.TableRspDTO;
import web.ssm.entity.BookEntity;
import web.ssm.mapper.BookMapper;
import web.ssm.service.BookService;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;

/**
 * 图书服务实现类
 * 实现图书的增删改查、分页查询、批量导入等功能
 */
@Service
public class BookServiceImpl implements BookService {

  private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
  private final BookMapper bookMapper;
  private final MapperFactory mapperFactory; // 用于对象转换

  @Autowired
  public BookServiceImpl(BookMapper bookMapper) {
    this.bookMapper = bookMapper;
    this.mapperFactory = new DefaultMapperFactory.Builder().build();
  }

  /**
   * 实体转DTO
   * 将数据库实体对象转换为数据传输对象
   * 
   * @param entity 图书实体
   * @return 图书DTO
   */
  private BookDTO convertToDTO(BookEntity entity) {
    if (entity == null)
      return null;
    BookDTO dto = new BookDTO();
    BeanUtils.copyProperties(entity, dto);
    return dto;
  }

  /**
   * 实体列表转DTO列表
   * 
   * @param entities 图书实体列表
   * @return 图书DTO列表
   */
  private List<BookDTO> convertToDTOList(List<BookEntity> entities) {
    if (entities == null)
      return null;
    return entities.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  /**
   * 根据ID获取图书
   * 
   * @param bookId 图书ID
   * @return 图书信息
   * @throws RuntimeException 获取失败时抛出异常
   */
  @Override
  public BookDTO getById(Integer bookId) {
    try {
      BookEntity book = bookMapper.getById(bookId);
      return book != null ? mapperFactory.getMapperFacade().map(book, BookDTO.class) : null;
    } catch (Exception e) {
      logger.error("获取图书失败: bookId={}", bookId, e);
      throw new RuntimeException("获取图书失败", e);
    }
  }

  @Override
  public List<BookDTO> list() {
    try {
      logger.info("开始获取图书列表");
      List<BookEntity> books = bookMapper.list();
      logger.info("查询到{}本图书", books.size());
      return convertToDTOList(books);
    } catch (Exception e) {
      logger.error("获取图书列表失败: {}", e.getMessage(), e);
      throw new RuntimeException("获取图书列表失败", e);
    }
  }

  /**
   * 添加图书
   * 
   * @param bookDTO 图书信息
   * @return 新增图书的ID
   * @throws RuntimeException 添加失败时抛出异常
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer add(BookDTO bookDTO) {
    try {
      logger.info("开始添加图书: {}", bookDTO);
      BookEntity book = mapperFactory.getMapperFacade().map(bookDTO, BookEntity.class);
      book.setGmtCreate(new Date());
      bookMapper.add(book);
      logger.info("添加图书成功: bookId={}", book.getBookId());
      return book.getBookId();
    } catch (Exception e) {
      logger.error("添加图书失败: book={}", bookDTO, e);
      throw new RuntimeException("添加图书失败", e);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean update(BookDTO bookDTO) {
    try {
      logger.info("开始更新图书: {}", bookDTO);
      BookEntity book = mapperFactory.getMapperFacade().map(bookDTO, BookEntity.class);
      book.setGmtModified(new Date());
      boolean success = bookMapper.update(book) > 0;
      logger.info("更新图书{}: bookId={}", success ? "成功" : "失败", bookDTO.getBookId());
      return success;
    } catch (Exception e) {
      logger.error("更新图书失败: book={}", bookDTO, e);
      throw new RuntimeException("更新图书失败", e);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delete(Integer bookId) {
    try {
      logger.info("开始删除图书: bookId={}", bookId);

      // 先检查是否有借阅记录
      int borrowCount = bookMapper.checkBorrowRecord(bookId);
      if (borrowCount > 0) {
        logger.warn("图书存在借阅记录，无法删除: bookId={}", bookId);
        throw new RuntimeException("该图书存在借阅记录，无法删除");
      }

      boolean success = bookMapper.delete(bookId) > 0;
      logger.info("删除图书{}: bookId={}", success ? "成功" : "失败", bookId);
      return success;
    } catch (Exception e) {
      logger.error("删除图书失败: bookId={}", bookId, e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * 分页查询图书列表
   * 支持按关键词搜索
   * 
   * @param tableReqDTO 分页查询参数
   * @return 分页结果
   */
  @Override
  public TableRspDTO list4Table(TableReqDTO tableReqDTO) {
    try {
      logger.debug("开始获取图书分页列表: {}", tableReqDTO);

      // 使用 pageNum 计算偏移量
      int start = (tableReqDTO.getPageNum() - 1) * tableReqDTO.getPageSize();

      // 获取数据和总数
      List<BookEntity> books = bookMapper.search(
          tableReqDTO.getQueryText(),
          start,
          tableReqDTO.getPageSize());
      Integer total = bookMapper.count(tableReqDTO.getQueryText());

      // 转换并返回结果
      TableRspDTO tableRspDTO = new TableRspDTO();
      tableRspDTO.setTotal(total);
      tableRspDTO.setList(convertToDTOList(books));

      logger.debug("获取图书分页列表成功: total={}", total);
      return tableRspDTO;
    } catch (Exception e) {
      logger.error("获取图书分页列表失败", e);
      throw new RuntimeException("获取图书分页列表失败", e);
    }
  }

  @Override
  @Deprecated
  public List<BookDTO> getPageList(String keyword, Integer pageNum, Integer pageSize, MutableInt total) {
    throw new UnsupportedOperationException("This method is deprecated. Please use list4Table instead.");
  }

  @Override
  public List<BookDTO> getAllBooks() {
    try {
      List<BookEntity> books = bookMapper.getAllBooks();
      return convertToDTOList(books);
    } catch (Exception e) {
      logger.error("获取所有图书失败", e);
      throw new RuntimeException("获取所有图书失败", e);
    }
  }

  /**
   * 批量导入图书
   * 支持新增和更新库存
   * 
   * @param books 图书列表
   * @throws RuntimeException 导入失败时抛出异常
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void batchSave(List<BookDTO> books) {
    try {
      if (books == null || books.isEmpty()) {
        return;
      }

      List<BookEntity> newBooks = new ArrayList<>();

      for (BookDTO book : books) {
        // 验证必填字段
        if (StringUtils.isBlank(book.getBookName())) {
          throw new IllegalArgumentException("书名不能为空");
        }
        if (StringUtils.isBlank(book.getIsbn())) {
          throw new IllegalArgumentException("ISBN不能为空");
        }

        // 库存设置默认值
        if (book.getStock() == null) {
          book.setStock(0);
        }
        if (book.getPrice() == null) {
          book.setPrice(BigDecimal.ZERO);
        }

        // 检查ISBN是否已存在
        BookEntity existingBook = bookMapper.getByIsbn(book.getIsbn());
        if (existingBook != null) {
          // 如果存在，更新库存
          bookMapper.updateStockByIsbn(book.getIsbn(), book.getStock());
          logger.info("更新图书库存: ISBN={}, 新增库存={}", book.getIsbn(), book.getStock());
        } else {
          // 如果不存在，添加到新书列表
          BookEntity entity = new BookEntity();
          BeanUtils.copyProperties(book, entity);
          entity.setGmtCreate(new Date());
          entity.setGmtModified(new Date());
          newBooks.add(entity);
        }
      }

      // 批量保存新书
      if (!newBooks.isEmpty()) {
        bookMapper.batchSave(newBooks);
        logger.info("批量保存新图书: count={}", newBooks.size());
      }

    } catch (Exception e) {
      logger.error("批量保存图书失败", e);
      throw new RuntimeException("批量保存图书失败: " + e.getMessage(), e);
    }
  }
}