package model;

public class Book extends LibraryItem {
    private String author;
    private int categoryId;
    private int stock;

    public Book(int id, String title, String author, int categoryId, int stock) {
        super(id, title);
        this.author = author;
        this.categoryId = categoryId;
        this.stock = stock;
    }

    // Getters and Setters
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String getDetails() {
        return "Book: " + name + ", Author: " + author + ", Stock: " + stock;
    }
}