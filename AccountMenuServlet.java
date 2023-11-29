package org.example.app.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.app.entities.Account;
import org.example.app.entities.AccountRole;
import org.example.app.entities.Goods;
import org.example.app.entities.Types;
import org.example.app.model.*;

import java.io.IOException;
import java.util.ArrayList;

public class AccountMenuServlet extends HttpServlet {
    AccountFilter filter;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("AdminAccount => doGet");
        HttpSession session = req.getSession();
        String accLogin = null;
        if (session != null) {
            accLogin = (String) session.getAttribute(StockServlet.SESSION_ATTRIBUTE_LOGIN);
        }
        if (accLogin == null || AccountModel.getInstance().getAccount(accLogin).getRole()== AccountRole.user) {
            noLoginFound(req, resp);
        } else {
            String action = (String) req.getParameter("action");
            if(action==null){
                doGetNullAction(req, resp);
                return;
            }else {
                if ("signout".equals(action)) {
                    signOut(req, resp);
                    return;
                }
                if ("toStock".equals(action)) {
                    moveToStock(req, resp);
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
    private void doGetNullAction(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher;
        HttpSession session = req.getSession();
        AccountModel model = AccountModel.getInstance();
        filter = new AccountFilter(model.getList());
        if (session.getAttribute("noFilt")!=null) filter.removeAllFilters();
        if (session.getAttribute("blocked")!=null) filter.setBlocked();
        if (session.getAttribute("notBlocked")!=null) filter.setNotBlocked();
        filter.apply();
        filter.displayList();
        ArrayList<Account> filteredList = filter.toList();
        req.setAttribute("accounts", filteredList);
        requestDispatcher = req.getRequestDispatcher("views/account_menu.jsp");
        requestDispatcher.forward(req, resp);
    }
    private void moveToStock(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("move to stock menu");
        resp.sendRedirect("/stock");
    }
    private void moveToRestore(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        ArrayList<Account> delAccs = AccountModel.getDeletedAccs();
        req.setAttribute("delAccs",delAccs);
        RequestDispatcher requestDispatcher;
        requestDispatcher = req.getRequestDispatcher("views/restore_accs.jsp");
        requestDispatcher.forward(req, resp);
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
    private void applyFilter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.removeAttribute("noFilt");
        session.removeAttribute("blocked");
        session.removeAttribute("notBlocked");
        if (req.getParameter("filtBox").equals("Не заблокированные")) {
            session.setAttribute("notBlocked", "Не заблокированные");
        } else if(req.getParameter("filtBox").equals("Заблокированные")){
            session.setAttribute("blocked","Заблокированные");
        }else session.setAttribute("noFilt", "Без фильтра");
        doGetNullAction(req, resp);
    }
    private void signOut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("SignOut");
        if (req.getSession() != null)
            req.getSession().removeAttribute(StockServlet.SESSION_ATTRIBUTE_LOGIN);
        resp.sendRedirect("/authorization");
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
    private void deleteAcc(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("delete acc method");
        System.out.println("tlogin = "+ req.getParameter("tlogin"));
        String log= req.getParameter("tlogin");
        AccountModel model = AccountModel.getInstance();
        if(model.deleteAcc(log)) req.setAttribute("deleted",log);
        else req.setAttribute("adminDel",log);
    }
    private void blockAcc(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("block acc method");
        System.out.println("tlogin = "+ req.getParameter("tlogin"));
        String log= req.getParameter("tlogin");
        AccountModel model = AccountModel.getInstance();
        if(model.changeAcc(log)) req.setAttribute("blocked",log);
        else req.setAttribute("adminBlock",log);
    }
    private void restore(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("restore");
        System.out.println("tlogin = "+ req.getParameter("tlogin"));
        String log= req.getParameter("tlogin");
        AccountModel model = AccountModel.getInstance();
        model.restoreAcc(log);
        req.setAttribute("restored",log);
    }

    private void deleteForever(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("restore");
        System.out.println("tlogin = "+ req.getParameter("tlogin"));
        String log= req.getParameter("tlogin");
        AccountModel model = AccountModel.getInstance();
        model.removeFromDB(log);
        req.setAttribute("removed",log);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("AccountMenu => doPost");
        HttpSession session = req.getSession();
        String accLogin = null;
        if (session != null) {
            accLogin = (String) session.getAttribute(StockServlet.SESSION_ATTRIBUTE_LOGIN);
        }
        if (accLogin == null || AccountModel.getInstance().getAccount(accLogin).getRole()== AccountRole.user) {
            noLoginFound(req, resp);
        } else {
            String action = (String) req.getParameter("action");
            if(action==null){
                doGetNullAction(req, resp);
                return;
            }else {
                if ("signout".equals(action)) {
                    signOut(req, resp);
                    return;
                }
                if ("toStock".equals(action)) {
                    moveToStock(req, resp);
                    return;
                }
                if ("delete".equals(action)) {
                    deleteAcc(req, resp);
                    doGetNullAction(req, resp);
                    return;
                }
                if ("block".equals(action)) {
                    blockAcc(req, resp);
                    doGetNullAction(req, resp);
                    return;
                }
                if ("filter".equals(action)) {
                    applyFilter(req, resp);
                    return;
                }
                if ("toRestoreMenu".equals(action)) {
                    moveToRestore(req, resp);
                    return;
                }
                if ("toAccounts".equals(action)) {
                    doGetNullAction(req, resp);
                    return;
                }
                if ("restore".equals(action)) {
                    restore(req, resp);
                    moveToRestore(req, resp);
                    return;
                }
                if ("deleteForever".equals(action)) {
                    deleteForever(req, resp);
                    moveToRestore(req, resp);
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
}
