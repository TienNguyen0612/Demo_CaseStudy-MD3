package dao;

import connection.MyConnection;
import model.Post;
import model._ListOfPost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConnectionDBOf_Post implements IConnectionDB_Post {
    private static final String INSERT_POST_SQL = "insert into post (title, content, date_create, id_account, id_category) value (?, ?, ?, ?, ?);";
    private static final String SELECT_ALL_LIST_OF_POST = "select id_post, title, content, username, date_create, name_category from post\n" +
            "inner join account on post.id_account = account.id_account\n" +
            "inner join category on post.id_category = category.id_category\n" +
            "where account.status = 1\n" +
            "order by id_post desc;";
    private static final String SELECT_LIST_OF_POST_BY_ID_CATEGORY = "select id_post, title, content, username, date_create, name_category from post\n" +
            "inner join account on post.id_account = account.id_account\n" +
            "inner join category on post.id_category = category.id_category\n" +
            "where account.status = 1 and category.id_category = ?\n" +
            "order by id_post desc;";
    private static final String UPDATE_POST_SQL = "update post set title = ?, content = ?, id_category = ? where id_post = ?;";
    private static final String DELETE_POST_SQL = "delete from post where id_post = ?;";
    private static final String SELECT_ALL_POST = "select * from post";
    private static final String SELECT_POST_BY_ID = "select id_post, title, content, username, date_create, name_category from post\n" +
            "inner join account on post.id_account = account.id_account\n" +
            "inner join category on post.id_category = category.id_category\n" +
            "where account.status = 1 and id_post = ?";

    public ConnectionDBOf_Post() {
    }

    private final MyConnection myConnection = new MyConnection();

    public List<_ListOfPost> selectListOfPostByIdCategory(int id_category) {
        List<_ListOfPost> listOfPosts = new ArrayList<>();
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LIST_OF_POST_BY_ID_CATEGORY)) {
            preparedStatement.setInt(1, id_category);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id_post = rs.getInt("id_post");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String author = rs.getString("username");
                LocalDate date_created = LocalDate.parse(rs.getString("date_create"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String category = rs.getString("name_category");
                listOfPosts.add(new _ListOfPost(id_post, title, content, author, date_created, category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfPosts;
    }

    public List<_ListOfPost> selectListOfPost() {
        List<_ListOfPost> listOfPosts = new ArrayList<>();
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LIST_OF_POST)) {
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_post");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String author = rs.getString("username");
                LocalDate date_created = LocalDate.parse(rs.getString("date_create"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String category = rs.getString("name_category");
                listOfPosts.add(new _ListOfPost(id, title, content, author, date_created, category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfPosts;
    }

    @Override
    public void insertPost(Post post) throws SQLException {
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_POST_SQL)) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setDate(3, java.sql.Date.valueOf(post.getDate_created()));
            preparedStatement.setInt(4, post.getId_account());
            preparedStatement.setInt(5, post.getId_category());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public _ListOfPost selectPost(int id) {
        _ListOfPost post = null;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POST_BY_ID)) {
            System.out.println(preparedStatement);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String content = rs.getString("content");
                String username = rs.getString("username");
                LocalDate date_create = LocalDate.parse(rs.getString("date_create"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String name_category = rs.getString("name_category");
                post = new _ListOfPost(id, title, content, username, date_create, name_category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public boolean updatePost(Post post) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POST_SQL)) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setInt(3, post.getId_category());
            preparedStatement.setInt(4, post.getId_post());

            rowUpdated = preparedStatement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    @Override
    public boolean deletePost(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_POST_SQL)) {
            statement.setInt(1, id);

            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    @Override
    public List<Post> selectAllPost() {
        List<Post> postList = new ArrayList<>();
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_POST)) {
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id_post = rs.getInt("id_post");
                String title = rs.getString("title");
                String content = rs.getString("content");
                LocalDate date_create = LocalDate.parse(rs.getString("date_create"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                int id_account = rs.getInt("id_account");
                int id_category = rs.getInt("id_category");
                postList.add(new Post(id_post, title, content, date_create, id_account, id_category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postList;
    }
}
