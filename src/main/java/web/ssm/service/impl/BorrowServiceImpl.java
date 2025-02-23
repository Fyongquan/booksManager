package web.ssm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.ssm.dto.BorrowDTO;
import web.ssm.dto.TableReqDTO;
import web.ssm.dto.TableRspDTO;
import web.ssm.entity.BorrowEntity;
import web.ssm.entity.BookEntity;
import web.ssm.mapper.BorrowMapper;
import web.ssm.mapper.BookMapper;
import web.ssm.service.BorrowService;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import web.ssm.utils.RedisUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {
  private static final Logger logger = LoggerFactory.getLogger(BorrowServiceImpl.class);

  private final BorrowMapper borrowMapper;
  private final MapperFactory mapperFactory;
  private final BookMapper bookMapper;

  @Autowired
  private RedisUtil redisUtil;

  private static final String LAST_CHECK_KEY = "borrow:status:lastcheck";
  private static final long CHECK_INTERVAL = 5 * 60 * 1000; // 5分钟

  @Autowired
  public BorrowServiceImpl(BorrowMapper borrowMapper, BookMapper bookMapper) {
    this.borrowMapper = borrowMapper;
    this.mapperFactory = new DefaultMapperFactory.Builder().build();
    this.bookMapper = bookMapper;
  }

  /**
   * 检查并更新借阅状态
   */
  private void checkAndUpdateStatus() {
    String lastCheckTime = redisUtil.get(LAST_CHECK_KEY);
    if (lastCheckTime == null ||
        System.currentTimeMillis() - Long.parseLong(lastCheckTime) > CHECK_INTERVAL) {
      updateOverdueStatus();
      redisUtil.set(LAST_CHECK_KEY, String.valueOf(System.currentTimeMillis()));
    }
  }

  /**
   * 更新逾期状态
   * 
   * @return 更新的记录数
   */
  @Transactional(rollbackFor = Exception.class)
  public int updateOverdueStatus() {
    // 更新所有已超过归还日期的借阅记录状态为逾期
    return borrowMapper.updateOverdueBorrows();
  }

  @Override
  public TableRspDTO list4Table(TableReqDTO tableReqDTO) {
    // 检查是否需要更新状态
    checkAndUpdateStatus();

    try {
      TableRspDTO tableRspDTO = new TableRspDTO();

      // 计算分页参数
      int offset = (tableReqDTO.getPageNum() - 1) * tableReqDTO.getPageSize();

      // 获取总数和数据
      int total = borrowMapper.count4Table(
          tableReqDTO.getQueryText(),
          tableReqDTO.getUserId() // 传入用户ID
      );
      List<?> rows = borrowMapper.list4Table(
          offset,
          tableReqDTO.getPageSize(),
          tableReqDTO.getQueryText(),
          tableReqDTO.getUserId() // 传入用户ID
      );

      tableRspDTO.setTotal(total);
      tableRspDTO.setList(rows);

      return tableRspDTO;
    } catch (Exception e) {
      logger.error("获取借阅列表失败", e);
      throw new RuntimeException("获取借阅列表失败", e);
    }
  }

  @Override
  @Transactional
  public Integer borrow(BorrowDTO borrowDTO) {
    try {
      // 检查图书库存
      BookEntity book = bookMapper.getById(borrowDTO.getBookId());
      if (book == null) {
        throw new RuntimeException("图书不存在");
      }
      if (book.getStock() <= 0) {
        throw new RuntimeException("图书库存不足");
      }

      // 减少库存
      book.setStock(book.getStock() - 1);
      bookMapper.updateStock(book);

      // 创建借阅记录
      BorrowEntity borrow = mapperFactory.getMapperFacade()
          .map(borrowDTO, BorrowEntity.class);

      Date now = new Date();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(now);
      calendar.add(Calendar.DAY_OF_MONTH, borrowDTO.getDays());

      borrow.setBorrowTime(now);
      borrow.setReturnTime(calendar.getTime());
      borrow.setStatus("BORROWING");
      borrow.setGmtCreate(now);

      return borrowMapper.add(borrow);
    } catch (Exception e) {
      throw new RuntimeException("借阅失败: " + e.getMessage(), e);
    }
  }

  @Override
  @Transactional
  public Integer return_(Integer borrowId) {
    try {
      BorrowEntity borrow = borrowMapper.getById(borrowId);
      if (borrow == null) {
        throw new RuntimeException("借阅记录不存在");
      }

      // 检查是否逾期
      Date now = new Date();
      boolean isOverdue = now.after(borrow.getReturnTime());

      // 增加库存
      BookEntity book = bookMapper.getById(borrow.getBookId());
      if (book != null) {
        book.setStock(book.getStock() + 1);
        bookMapper.updateStock(book);
      }

      // 设置归还信息
      borrow.setActualReturnTime(now);
      borrow.setStatus(isOverdue ? "OVERDUE_RETURNED" : "RETURNED"); // 新增状态：逾期归还
      borrow.setGmtModified(now);

      return borrowMapper.return_(borrow);
    } catch (Exception e) {
      throw new RuntimeException("归还失败: " + e.getMessage(), e);
    }
  }

  @Override
  @Transactional
  public Integer renew(Integer borrowId) {
    try {
      BorrowEntity borrow = borrowMapper.getById(borrowId);
      if (borrow == null) {
        throw new RuntimeException("借阅记录不存在");
      }

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(borrow.getReturnTime());
      calendar.add(Calendar.DAY_OF_MONTH, 7); // 续借7天

      borrow.setReturnTime(calendar.getTime());
      borrow.setGmtModified(new Date());

      return borrowMapper.renew(borrow);
    } catch (Exception e) {
      throw new RuntimeException("续借失败", e);
    }
  }
}