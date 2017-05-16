package models;

/**
 * model for git commits; a Commit contains information about a specific git repository commit, that will then be
 * serialised and sent to the client
 */
public class Commit {

    private String name;
    private String author;
    private String date;
    private String description;

    public Commit(String name, String author, String date, String description) {
        this.name = name;
        this.author = author;
        this.date = date;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
