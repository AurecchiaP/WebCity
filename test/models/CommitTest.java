package models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommitTest {

    Commit commit;

    @Before
    public void setUp() throws Exception {
        commit = new Commit("name", "author", "01-01-2017", "description");
    }

    @Test
    public void getName() throws Exception {
        assertEquals("name", commit.getName());
    }

    @Test
    public void setName() throws Exception {
        commit.setName("newName");
        assertEquals("newName", commit.getName());
    }

    @Test
    public void getAuthor() throws Exception {
        assertEquals("author", commit.getAuthor());
    }

    @Test
    public void setAuthor() throws Exception {
        commit.setAuthor("newAuthor");
        assertEquals("newAuthor", commit.getAuthor());
    }

    @Test
    public void getDate() throws Exception {
        assertEquals("01-01-2017", commit.getDate());
    }

    @Test
    public void setDate() throws Exception {
        commit.setDate("05-02-2018");
        assertEquals("05-02-2018", commit.getDate());
    }


    @Test
    public void getDescription() throws Exception {
        assertEquals("description", commit.getDescription());
    }

    @Test
    public void setDescription() throws Exception {
        commit.setDescription("new description");
        assertEquals("new description", commit.getDescription());
    }

}