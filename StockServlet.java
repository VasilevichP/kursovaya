package org.example.app.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.app.model.AccountModel;
import org.example.app.entities.*;
import org.example.app.model.ChocolateFilter;
import org.example.app.model.Stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class StockServlet extends HttpServlet {
    final static String SESSION_ATTRIBUTE_LOGIN = "login";
    ChocolateFilter filter;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Stock => doGet");
        HttpSession session = req.getSession();
        String accLogin = null;
        if (session != null) {
            accLogin = (String) session.getAttribute(SESSION_ATTRIBUTE_LOGIN);
        }
        if (accLogin == null) {
            noLoginFound(req, resp);
        } else {
            if (AccountModel.getInstance().getAccount(accLogin).getRole() == AccountRole.user) {
                req.setAttribute("userSession", accLogin);
            } else req.setAttribute("adminSession", accLogin);
            String action = (String) req.getParameter("action");
            System.out.println("action=" + action);
            if (action == null) {
                doGetNullAction(req, resp);
                return;
            } else {
                if ("signout".equals(action)) {
                    signOut(req, resp);
                    return;
                }
                if ("toAccountMenu".equals(action)) {
                    moveToAccount(req, resp);
                    return;
                }
                Account acc = AccountModel.getInstance().getAccount(accLogin);
                if (acc == null) {
                    System.out.println("Account " + accLogin + " not found in the list");
                    noLoginFound(req, resp);
                } else {
                    RequestDispatcher requestDispatcher;
                    if (acc.getRole() == AccountRole.admin) {
                        doGetAdmin(req, resp, acc);
                    } else if (acc.getRole() == AccountRole.user) {
                        doGetUser(req, resp, acc);
                    } else {
                        System.out.println("Role is " + acc.getRole());
                        noLoginFound(req, resp);
                    }
                }
            }
        }
    }

//    private boolean validateAccess(HttpServletRequest req) {
//        boolean ex = false;
//        HttpSession session = req.getSession();
//        String accLogin = null;
//        if (session != null)
//            accLogin = (String) session.getAttribute(StockServlet.SESSION_ATTRIBUTE_LOGIN);
//        if (accLogin != null && AccountModel.getInstance().getAccount(accLogin).getRole() == AccountRole.admin)
//            ex = true;
//        return ex;
//    }

    private void doGetNullAction(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Stock => doGetNullAction");
        HttpSession session = req.getSession();
        String accLogin = null;
        if (session != null) {
            accLogin = (String) session.getAttribute(SESSION_ATTRIBUTE_LOGIN);
        }
        filter = new ChocolateFilter(Stock.getInstance().getList());
        if (session.getAttribute("noType")!=null) filter.removeByType();
        if (session.getAttribute("type")!=null) filter.setByType(Types.getInstance((String) session.getAttribute("type")));
        if (session.getAttribute("search")!=null) filter.setByName((String) session.getAttribute("search"));
        filter.apply();
        ArrayList<Goods> filteredList = filter.toList();
        if (session.getAttribute("asc")!=null) filteredList.sort(ChocolateList.ComparatorByPrice());
        else if (session.getAttribute("desc")!=null)filteredList.sort(ChocolateList.ComparatorByPrice().reversed());
        RequestDispatcher requestDispatcher;
        req.setAttribute("stock", filteredList);
        if (AccountModel.getInstance().getAccount(accLogin).getRole() == AccountRole.admin)
            requestDispatcher = req.getRequestDispatcher("views/admin-stock.jsp");
        else requestDispatcher = req.getRequestDispatcher("views/user_stock.jsp");
        requestDispatcher.forward(req, resp);
    }

    private void moveToAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("move to account menu");
        resp.sendRedirect("/accounts");
    }

    private void toAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("move to add menu");
        resp.sendRedirect("/choco");
    }
    private void sort(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("sorter");
        HttpSession session = req.getSession();
        if (req.getParameter("sortPrice").equals("По возрастанию")) {
            session.removeAttribute("desc");
            session.setAttribute("asc", "asc");
        } else if ((req.getParameter("sortPrice").equals("По убыванию"))){
            session.removeAttribute("asc");
            session.setAttribute("desc", "desc");
        }
        doGetNullAction(req, resp);
    }

    private void applyFilter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.removeAttribute("noType");
        session.removeAttribute("type");
        System.out.println("filterr");
        if (req.getParameter("typeBox").equals("Без фильтра")) {
            session.setAttribute("noType", "Без фильтра");
        } else {
            Types type = Types.getInstance(req.getParameter("typeBox"));
            session.setAttribute("type", type.getText());
        }
        doGetNullAction(req, resp);
    }
    private void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("searrch");
        HttpSession session = req.getSession();
        session.removeAttribute("search");
        if (req.getParameter("searchText")!=null) {
            String text = (String) req.getParameter("searchText");
            session.setAttribute("search",text);
        }
        doGetNullAction(req, resp);
    }

    private void change(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
        removeFilters(req, resp);
        int article = Integer.parseInt(req.getParameter("tarticle"));
        Stock stock = Stock.getInstance();
        Goods g = stock.getItem(stock.findItem(article));
        HttpSession session = req.getSession(true);
        System.out.println("log change: " + session.getAttribute("login"));
        session.setAttribute("item", stock.getItem(stock.findItem(article)));
        System.out.println("move to change menu");
        resp.sendRedirect("/change");
    }

    private void toCart(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
        removeFilters(req, resp);
        System.out.println("move to cart menu");
        resp.sendRedirect("/cart");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("AdminStock => doPost");
        HttpSession session = req.getSession();
        String accLogin = null;
        if (session != null) {
            accLogin = (String) session.getAttribute(SESSION_ATTRIBUTE_LOGIN);
            System.out.println("log: " + accLogin);
        }
        if (accLogin == null) {
            noLoginFound(req, resp);
        } else {
            String action = (String) req.getParameter("action");
            System.out.println("action=" + action);
            if (action == null) {
                doGetNullAction(req, resp);
            } else {
                if ("signout".equals(action)) {
                    signOut(req, resp);
                    return;
                }
                if ("toAccountMenu".equals(action)) {
                    moveToAccount(req, resp);
                    return;
                }
                if ("filter".equals(action)) {
                    applyFilter(req, resp);
                    return;
                }
                if ("sort".equals(action)) {
                    sort(req, resp);
                    return;
                }
                if ("search".equals(action)) {
                    search(req, resp);
                    return;
                }
                if ("addItem".equals(action)) {
                    toAdd(req, resp);
                    return;
                }
                if ("change".equals(action)) {
                    change(req, resp);
                    return;
                }
                if ("delete".equals(action)) {
                    delete(req, resp);
                    doGetNullAction(req, resp);
                    return;
                }
                if ("toCart".equals(action)) {
                    toCart(req, resp);
                    return;
                }
                Account acc = AccountModel.getInstance().getAccount(accLogin);
                if (acc == null) {
                    System.out.println("Account " + accLogin + " not found in the list");
                    noLoginFound(req, resp);
                } else {
                    RequestDispatcher requestDispatcher;
                    if (acc.getRole() == AccountRole.admin) {
                        doGetAdmin(req, resp, acc);
                    } else if (acc.getRole() == AccountRole.user) {
                        doGetUser(req, resp, acc);
                    } else {
                        System.out.println("Role is " + acc.getRole());
                        noLoginFound(req, resp);
                    }
                }
            }
        }
    }

    private void noLoginFound(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Login was not found");
        if (req.getSession() == null)
            System.out.println("Session was not found");
        else {
            System.out.println("login is null");
            req.getSession().invalidate();
        }

        resp.sendRedirect("/authorization");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("delete choco method");
        System.out.println("article = " + req.getParameter("tarticle"));
        int article = Integer.parseInt(req.getParameter("tarticle"));
        Stock stock = Stock.getInstance();
        Goods g = stock.getItem(stock.findItem(article));
        System.out.println(g.showItem());
        if (stock.removeChocolate(article) >= 0) {
            req.setAttribute("deleted", g.getItem().getName());
            stock.removeFromDB(g);
        } else req.setAttribute("error", g.getItem().getName());
    }

    private void doGetAdmin(HttpServletRequest req, HttpServletResponse resp, Account account) throws ServletException, IOException {
        System.out.println("admin found");
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("views/admin-stock.jsp");
        req.setAttribute("login", account.getLogin());
        requestDispatcher.forward(req, resp);
    }

    private void doGetUser(HttpServletRequest req, HttpServletResponse resp, Account account) throws ServletException, IOException {
        System.out.println("user found");
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("views/user-stock.jsp");
        req.setAttribute("login", account.getLogin());
        req.setAttribute("accounts", AccountModel.getInstance().getList());
        requestDispatcher.forward(req, resp);
    }
    private void removeFilters(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        if (req.getSession()!=null) {
            req.getSession().removeAttribute("noType");
            req.getSession().removeAttribute("type");
            req.getSession().removeAttribute("search");
            req.getSession().removeAttribute("asc");
            req.getSession().removeAttribute("desc");
        }
    }

    private void signOut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        removeFilters(req, resp);
        System.out.println("SignOut");
        if (req.getSession() != null)
            req.getSession().removeAttribute(SESSION_ATTRIBUTE_LOGIN);
        resp.sendRedirect("/authorization");
    }
}
