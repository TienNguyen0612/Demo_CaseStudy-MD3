package dao;

import connection.MyConnection;
import model.Account;
import regex.Validate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDBOf_Account implements IConnectionDB_Account {
    private static final String INSERT_ACCOUNT_SQL = "insert into account (username, password, phone_number, email, address) value (?, ?, ?, ?, ?);";
    private static final String UPDATE_ACCOUNT_SQL = "update account set password = ?, phone_number = ?, email = ?, address = ? where id_account = ?;";
    private static final String SELECT_ACCOUNT_BY_ID = "select * from account where id_account = ?";
    private static final String SELECT_ALL_ACCOUNT = "select * from account";
    private static final String BLOCK_ACCOUNT_SQL = "update account set status = 0 where id_account = ?;";
    private static final String UNBLOCK_ACCOUNT_SQL = "update account set status = 1 where id_account = ?;";

    public ConnectionDBOf_Account() {
    }

    private final MyConnection myConnection = new MyConnection();
    private final Validate validate = new Validate();
    @Override
    public void insertAccount(Account account) throws SQLException {
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ACCOUNT_SQL)) {
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.setString(3, account.getPhoneNumber());
            preparedStatement.setString(4, account.getEmail());
            preparedStatement.setString(5, account.getAddress());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Account> selectAllAccount() {
        List<Account> accountList = new ArrayList<>();
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ACCOUNT)) {
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id_account = rs.getInt("id_account");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                String address = rs.getString("address");
                int rollno = rs.getInt("roll_no");
                int status = rs.getInt("status");
                accountList.add(new Account(id_account, username, password, phoneNumber, email, address, rollno, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountList;
    }

    @Override
    public Account selectAccount(int id) {
        Account account = null;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                String address = rs.getString("address");
                account = new Account(id, username, password, phoneNumber, email, address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public boolean updateAccount(Account account) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ACCOUNT_SQL)) {
            statement.setString(1, account.getPassword());
            statement.setString(2, account.getPhoneNumber());
            statement.setString(3, account.getEmail());
            statement.setString(4, account.getAddress());
            statement.setInt(5, account.getId_account());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    @Override
    public boolean blockAccount(int id) throws SQLException {
        boolean rowBlocked;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(BLOCK_ACCOUNT_SQL)) {
            statement.setInt(1, id);

            rowBlocked = statement.executeUpdate() > 0;
        }
        return rowBlocked;
    }

    @Override
    public boolean unblockAccount(int id) throws SQLException {
        boolean rowUnblocked;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UNBLOCK_ACCOUNT_SQL)) {
            statement.setInt(1, id);

            rowUnblocked = statement.executeUpdate() > 0;
        }
        return rowUnblocked;
    }

    @Override
    public Account checkLogin(String username, String password) {
        Account account = null;
        List<Account> accounts = selectAllAccount();
        for (Account acc : accounts) {
            if (acc.getUsername().equals(username) && acc.getPassword().equals(password) ||
                    acc.getEmail().equals(username) && acc.getPassword().equals(password)) {
                account = acc;
            }
        }
        return account;
    }

    public Account logout() {
        return null;
    }

    public Account getAccountById(int id) {
        Account account = null;
        try (Connection connection = myConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID)) {
            System.out.println(preparedStatement);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id_account = rs.getInt("id_account");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                String address = rs.getString("address");
                int rollno = rs.getInt("roll_no");
                int status = rs.getInt("status");
                account = new Account(id_account, username, password, phoneNumber, email, address, rollno, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    public boolean checkRegister(String username, String password, String phoneNumber) {
       if (validate.validateAccount(username) && validate.validatePassword(password) && validate.validatePhone(phoneNumber)) {
           return true;
       } else {
           return false;
       }
    }

    public boolean checkExitingAccount(String username, String email) {
        List<Account> accounts = selectAllAccount();
        for (Account account: accounts) {
            if (account.getEmail().equals(email) || account.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }
}
