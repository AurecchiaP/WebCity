package models;

/**
 * model for git commits; a Commit contains information about a specific git repository commit, that will then be
 * serialised and sent to the client
 */
public class Commit {

    private String name;
    private String author;
    private String date;

    public Commit(String name, String author, String date) {
        this.name = name;
        this.author = author;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
