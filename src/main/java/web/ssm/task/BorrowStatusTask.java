package web.ssm.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import web.ssm.service.BorrowService;

@Component
public class BorrowStatusTask {

  private static final Logger logger = LoggerFactory.getLogger(BorrowStatusTask.class);

  @Autowired
  private BorrowService borrowService;

  /**
   * 定时检查借阅状态
   * 每小时执行一次
   */
  @Scheduled(cron = "0 0 * * * ?")
  public void checkBorrowStatus() {
    logger.info("开始检查借阅状态");
    try {
      int count = borrowService.updateOverdueStatus();
      logger.info("更新了 {} 条逾期记录", count);
    } catch (Exception e) {
      logger.error("检查借阅状态失败", e);
    }
  }
}