package model;

public abstract class LibraryItem {
    protected int id;
    protected String name;

    public LibraryItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract String getDetails();
}