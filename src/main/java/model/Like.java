package model;

public class Like {
    int id_post;
    int id_account;
    boolean status;

    public Like() {
    }

    public Like(int id_post, int id_account) {
        this.id_post = id_post;
        this.id_account = id_account;
    }

    public Like(int id_post, int id_account, boolean status) {
        this.id_post = id_post;
        this.id_account = id_account;
        this.status = status;
    }

    public int getId_post() {
        return id_post;
    }

    public void setId_post(int id_post) {
        this.id_post = id_post;
    }

    public int getId_account() {
        return id_account;
    }

    public void setId_account(int id_account) {
        this.id_account = id_account;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
