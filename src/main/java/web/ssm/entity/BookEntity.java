package web.ssm.entity;

import java.util.Date;
import java.math.BigDecimal;

public class BookEntity {
  private Integer bookId;
  private String bookName;
  private String author;
  private String publisher;
  private BigDecimal price;
  private Integer stock;
  private String category;
  private String description;
  private String isbn;
  private Date publishDate;
  private Date gmtCreate;
  private Date gmtModified;

  // Getters
  public Integer getBookId() {
    return bookId;
  }

  public String getBookName() {
    return bookName;
  }

  public String getAuthor() {
    return author;
  }

  public String getPublisher() {
    return publisher;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Integer getStock() {
    return stock;
  }

  public String getCategory() {
    return category;
  }

  public String getDescription() {
    return description;
  }

  public String getIsbn() {
    return isbn;
  }

  public Date getPublishDate() {
    return publishDate;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public Date getGmtModified() {
    return gmtModified;
  }

  // Setters
  public void setBookId(Integer bookId) {
    this.bookId = bookId;
  }

  public void setBookName(String bookName) {
    this.bookName = bookName;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  // toString方法，用于日志打印
  @Override
  public String toString() {
    return "BookEntity{" +
        "bookId=" + bookId +
        ", bookName='" + bookName + '\'' +
        ", author='" + author + '\'' +
        ", publisher='" + publisher + '\'' +
        ", price=" + price +
        ", stock=" + stock +
        ", category='" + category + '\'' +
        ", description='" + description + '\'' +
        ", isbn='" + isbn + '\'' +
        ", publishDate=" + publishDate +
        ", gmtCreate=" + gmtCreate +
        ", gmtModified=" + gmtModified +
        '}';
  }
}