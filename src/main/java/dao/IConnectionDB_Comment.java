package dao;

import model.Comment;

import java.sql.SQLException;
import java.util.List;

public interface IConnectionDB_Comment {
    public void insertComment(Comment comment) throws SQLException;

    public Comment selectComment(int id);

    public boolean updateComment(Comment comment) throws SQLException;

    public boolean deleteComment(int id) throws SQLException;

    public List<Comment> selectAllCommentById_post(int id);
}
