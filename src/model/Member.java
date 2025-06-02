package model;

public class Member extends LibraryItem {
    private String email;

    public Member(int id, String name, String email) {
        super(id, name);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getDetails() {
        return "Memberss: " + name + ", Email: " + email;
    }
    @Override
    public String toString() {
        return name + " (" + email + ")"; // Menampilkan nama dan email
    }
}