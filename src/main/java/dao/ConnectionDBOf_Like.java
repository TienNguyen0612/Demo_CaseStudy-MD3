package dao;

import connection.MyConnection;
import model.Like;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDBOf_Like {
    private static final String INSERT_LIKE_SQL = "insert into like_post (id_post, id_account, status) value (?, ?, ?);";
    private static final String UPDATE_LIKE_SQL = "update like_post set status = ? where id_post = ? and id_account = ?;";
    private static final String SELECT_ALL_LIKE = "select * from like_post;";
    private static final String SELECT_ALL_LIKE_BY_ID_POST = "select * from like_post WHERE id_post = ? and id_account = ?;";
    private static final String COUNT_LIKE_BY_ID_POST = "SELECT count(status) as quantity FROM like_post WHERE id_post = ? AND status = true;";

    public ConnectionDBOf_Like() {
    }

    private final MyConnection myConnection = new MyConnection();

    public void insertLike(Like like) throws SQLException {
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LIKE_SQL)) {
            preparedStatement.setInt(1, like.getId_post());
            preparedStatement.setInt(2, like.getId_account());
            preparedStatement.setBoolean(3, true);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateLike(Like like) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_LIKE_SQL)) {
            boolean like_flag = like.isStatus();
            statement.setBoolean(1, !like_flag);
            statement.setInt(2, like.getId_post());
            statement.setInt(3, like.getId_account());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    public List<Like> selectAllLike() {
        List<Like> likeList = new ArrayList<>();
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LIKE)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id_post = rs.getInt("id_post");
                int id_account = rs.getInt("id_account");
                boolean status = rs.getBoolean("status");
                likeList.add(new Like(id_post, id_account, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likeList;
    }

    public Like selectLikeById_Post(int id_post, int id_account) {
        Like like = null;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LIKE_BY_ID_POST)) {
            preparedStatement.setInt(1, id_post);
            preparedStatement.setInt(2, id_account);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int post = rs.getInt("id_post");
                int account = rs.getInt("id_account");
                boolean status = rs.getBoolean("status");
                like = new Like(post, account, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return like;
    }

    public int countLikeById_Post(int id_post) {
        int likes = 0;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_LIKE_BY_ID_POST)) {
            preparedStatement.setInt(1, id_post);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                likes = rs.getInt("quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likes;
    }

    public boolean checkExistingLike(int id_post, int id_account) {
        List<Like> likeList = selectAllLike();
        for (Like like : likeList) {
            if (like.getId_post() == id_post && like.getId_account() == id_account) {
                return false;
            }
        }
        return true;
    }
}
