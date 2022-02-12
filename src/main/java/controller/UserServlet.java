package controller;

import dao.*;
import model.*;
import regex.Validate;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = "/user")
public class UserServlet extends HttpServlet {
    ConnectionDBOf_Account connectionDBOf_account = new ConnectionDBOf_Account();
    ConnectionDBOf_Category connectionDBOf_category = new ConnectionDBOf_Category();
    ConnectionDBOf_Post connectionDBOf_post = new ConnectionDBOf_Post();
    ConnectionDBOf_Comment connectionDBOf_comment = new ConnectionDBOf_Comment();
    ConnectionDBOf_Like connectionDBOf_like = new ConnectionDBOf_Like();
    Validate validate = new Validate();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        action(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        action(request, response);
    }

    private void action(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        try {
            switch (action) {
                case "detailPost":
                    displayDetailPost(request, response);
                    break;
                case "createGet_Post":
                    createGet_Post(request, response);
                    break;
                case "createPost_Post":
                    createPost_Post(request, response);
                    break;
                case "displayPostById_Account":
                    displayPostById_Account(request, response);
                    break;
                case "editGet_Post":
                    editGet_Post(request, response);
                    break;
                case "editPost_Post":
                    editPost_Post(request, response);
                    break;
                case "deletePost":
                    deletePost(request, response);
                    break;
                case "searchPostByTitleOrCategory":
                    searchPostByTitleOrCategory(request, response);
                    break;
                case "createComment_Post":
                    createComment_Post(request, response);
                    break;
                case "likePost":
                    like_Post(request, response);
                    break;
                default:
                    displayAllPost(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void like_Post(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        int id_post = Integer.parseInt(request.getParameter("id"));
        _ListOfPost post = connectionDBOf_post.selectPost(id_post);
        request.setAttribute("post", post);
        Account account = null;
        if (checkAccount(request) != null) {
            account = checkAccount(request);
            request.setAttribute("account", account);
        }

        if (connectionDBOf_like.checkExistingLike(id_post, account.getId_account())) {
            Like like = new Like(id_post, account.getId_account());
            connectionDBOf_like.insertLike(like);
        } else {
            Like like = connectionDBOf_like.selectLikeById_Post(id_post, account.getId_account());
            connectionDBOf_like.updateLike(like);
        }

        Like like = connectionDBOf_like.selectLikeById_Post(id_post, account.getId_account());
        request.setAttribute("like", like);
        int quantity_like = connectionDBOf_like.countLikeById_Post(id_post);
        request.setAttribute("quantity_like", quantity_like);
        List<Category> categoryList = connectionDBOf_category.selectAllCategory();
        request.setAttribute("categoryList", categoryList);
        List<Comment> commentListById_post = connectionDBOf_comment.selectAllCommentById_post(id_post);
        request.setAttribute("commentListById_post", commentListById_post);
        List<Account> accountList = connectionDBOf_account.selectAllAccount();
        request.setAttribute("accountList", accountList);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/view/view_detail.jsp");
        requestDispatcher.forward(request, response);
    }

    private void createComment_Post(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String content = request.getParameter("content");
        int id_post = Integer.parseInt(request.getParameter("id"));
        int id_account = Integer.parseInt(request.getParameter("account_id"));
        Comment comment = new Comment(content, id_account, id_post);
        connectionDBOf_comment.insertComment(comment);
        displayDetailPost(request, response);
    }

    private void searchPostByTitleOrCategory(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String search = request.getParameter("search");
        List<_ListOfPost> postList = connectionDBOf_post.selectListOfPost();
        List<_ListOfPost> postListByTitleOrCategory = new ArrayList<>();
        for (_ListOfPost post : postList) {
            if (validate.validateTitleOrCategory(search, post.getTitle()) || validate.validateTitleOrCategory(search, post.getCategory())) {
                postListByTitleOrCategory.add(post);
            }
        }
        request.setAttribute("listOfPosts", postListByTitleOrCategory);
        List<Category> categoryList = connectionDBOf_category.selectAllCategory();
        request.setAttribute("categoryList", categoryList);
        if (checkAccount(request) != null) {
            request.setAttribute("account", checkAccount(request));
        }
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/view/home_view.jsp");
        requestDispatcher.forward(request, response);
    }

    private void deletePost(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        int id_post = Integer.parseInt(request.getParameter("id"));
        connectionDBOf_post.deletePost(id_post);
        if (checkAccount(request) != null) {
            request.setAttribute("account", checkAccount(request));
        }
        displayPostById_Account(request, response);
    }

    private void editPost_Post(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        int id_post = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        int id_category = Integer.parseInt(request.getParameter("categoryList"));
        Post post = new Post(id_post, title, content, id_category);
        connectionDBOf_post.updatePost(post);
        if (checkAccount(request) != null) {
            request.setAttribute("account", checkAccount(request));
        }
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/view/edit_post.jsp");
        requestDispatcher.forward(request, response);
    }

    private void editGet_Post(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        _ListOfPost post = connectionDBOf_post.selectPost(id);
        List<Category> categoryList = connectionDBOf_category.selectAllCategory();
        int id_category = -1;
        for (Category category : categoryList) {
            if (category.getName_category().equals(post.getCategory())) {
                id_category = category.getId_category();
            }
        }
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("post", post);
        request.setAttribute("id_category", id_category);
        if (checkAccount(request) != null) {
            request.setAttribute("account", checkAccount(request));
        }
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/view/edit_post.jsp");
        requestDispatcher.forward(request, response);
    }

    private void displayPostById_Account(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<_ListOfPost> postLists = connectionDBOf_post.selectListOfPost();
        List<_ListOfPost> postListById_Account = new ArrayList<>();
        Account account = null;
        if (checkAccount(request) != null) {
            account = checkAccount(request);
            request.setAttribute("account", account);
        }
        for (_ListOfPost post : postLists) {
            if (post.getAuthor().equals(account.getUsername())) {
                postListById_Account.add(post);
            }
        }
        request.setAttribute("postOfList", postListById_Account);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/view/view_post.jsp");
        requestDispatcher.forward(request, response);
    }

    private void createPost_Post(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        LocalDate date_created = LocalDate.now();
        int id_category = Integer.parseInt(request.getParameter("categoryList"));
        Account account = null;
        if (checkAccount(request) != null) {
            account = checkAccount(request);
            request.setAttribute("account", account);
        }
        Post post = new Post(title, content, date_created, account.getId_account(), id_category);
        connectionDBOf_post.insertPost(post);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/view/create_post.jsp");
        requestDispatcher.forward(request, response);
    }

    private void createGet_Post(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categoryList = connectionDBOf_category.selectAllCategory();
        request.setAttribute("categoryList", categoryList);
        if (checkAccount(request) != null) {
            request.setAttribute("account", checkAccount(request));
        }
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/view/create_post.jsp");
        requestDispatcher.forward(request, response);
    }

    private void displayDetailPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        _ListOfPost post = connectionDBOf_post.selectPost(id);
        request.setAttribute("post", post);
        Account account = null;
        if (checkAccount(request) != null) {
            account = checkAccount(request);
            request.setAttribute("account", account);
            Like like = connectionDBOf_like.selectLikeById_Post(id, account.getId_account());
            request.setAttribute("like", like);
        }
        int quantity_like = connectionDBOf_like.countLikeById_Post(id);
        request.setAttribute("quantity_like", quantity_like);
        List<Comment> commentListById_post = connectionDBOf_comment.selectAllCommentById_post(id);
        request.setAttribute("commentListById_post", commentListById_post);
        List<Account> accountList = connectionDBOf_account.selectAllAccount();
        request.setAttribute("accountList", accountList);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/view/view_detail.jsp");
        requestDispatcher.forward(request, response);
    }

    private void displayAllPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<_ListOfPost> listOfPosts = connectionDBOf_post.selectListOfPost();
        request.setAttribute("listOfPosts", listOfPosts);
        List<Category> categoryList = connectionDBOf_category.selectAllCategory();
        request.setAttribute("categoryList", categoryList);
        if (checkAccount(request) != null) {
            request.setAttribute("account", checkAccount(request));
        }
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/view/home_view.jsp");
        requestDispatcher.forward(request, response);
    }

    private Account checkAccount(HttpServletRequest request) {
        if ((request.getParameter("account_id")) != null) {
            int id = Integer.parseInt(request.getParameter("account_id"));
            return connectionDBOf_account.getAccountById(id);
        }
        return null;
    }
}
