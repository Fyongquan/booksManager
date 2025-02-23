package web.ssm.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;

public class BookDTO {
  @ExcelProperty(value = "图书ID", index = 0)
  private Integer bookId;

  @ExcelProperty(value = "书名", index = 1)
  private String bookName;

  @ExcelProperty(value = "作者", index = 2)
  private String author;

  @ExcelProperty(value = "出版社", index = 3)
  private String publisher;

  @ExcelProperty(value = "ISBN", index = 4)
  private String isbn;

  @ExcelProperty(value = "价格", index = 5)
  private BigDecimal price;

  @ExcelProperty(value = "库存", index = 6)
  private Integer stock;

  @ExcelProperty(value = "分类", index = 7)
  private String category;

  @ExcelProperty(value = "出版日期", index = 8)
  @DateTimeFormat("yyyy-MM-dd")
  private Date publishDate;

  @ExcelProperty(value = "描述", index = 9)
  private String description;

  public Integer getBookId() {
    return bookId;
  }

  public void setBookId(Integer bookId) {
    this.bookId = bookId;
  }

  public String getBookName() {
    return bookName;
  }

  public void setBookName(String bookName) {
    this.bookName = bookName;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Integer getStock() {
    return stock;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public Date getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
  }
}