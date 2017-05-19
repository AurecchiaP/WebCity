package models;

/**
 * Model for git commits. A Commit contains information about a specific git repository commit, that will then be
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

    /**
     * @return the name of the commit
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the commit
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the author of the commit
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author of the commit
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the stringified date of when the commit was done
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the stringified date of when the commit was done
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the (short) description of the commit
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the (short) description of the commit
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
