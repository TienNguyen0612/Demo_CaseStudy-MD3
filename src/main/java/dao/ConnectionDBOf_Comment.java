package dao;

import connection.MyConnection;
import model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDBOf_Comment implements IConnectionDB_Comment {
    private static final String INSERT_COMMENT_SQL = "insert into comment (content, id_account, id_post) value (?, ?, ?);";
    private static final String UPDATE_COMMENT_SQL = "update comment set content = ? where id_comment = ?;";
    private static final String DELETE_COMMENT_SQL = "delete from comment where id_comment = ?;";
    private static final String SELECT_ALL_COMMENT_BY_ID_POST = "select * from comment where id_post = ? order by id_comment desc";
    private static final String SELECT_COMMENT_BY_ID = "select * from comment where id_comment = ?";

    public ConnectionDBOf_Comment() {
    }

    private final MyConnection myConnection = new MyConnection();

    @Override
    public void insertComment(Comment comment) throws SQLException {
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COMMENT_SQL)) {
            preparedStatement.setString(1, comment.getContent());
            preparedStatement.setInt(2, comment.getId_account());
            preparedStatement.setInt(3, comment.getId_post());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Comment selectComment(int id) {
        Comment comment = null;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COMMENT_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String content = rs.getString("content");
                int id_account = rs.getInt("id_account");
                int id_post = rs.getInt("id_post");
                comment = new Comment(id, content, id_account, id_post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comment;
    }

    @Override
    public boolean updateComment(Comment comment) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_COMMENT_SQL)) {
            statement.setString(1, comment.getContent());
            statement.setInt(2, comment.getId_comment());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    @Override
    public boolean deleteComment(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_COMMENT_SQL)) {
            statement.setInt(1, id);

            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    @Override
    public List<Comment> selectAllCommentById_post(int id) {
        List<Comment> commentList = new ArrayList<>();
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_COMMENT_BY_ID_POST)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id_comment = rs.getInt("id_comment");
                String content = rs.getString("content");
                int id_account = rs.getInt("id_account");
                commentList.add(new Comment(id_comment, content, id_account, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentList;
    }
}
