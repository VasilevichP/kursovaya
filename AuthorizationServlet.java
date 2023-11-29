package org.example.app.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.app.entities.Account;
import org.example.app.entities.AccountRole;
import org.example.app.model.AccountModel;

import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.print("Authorization doGet: ");
        String action = (String) req.getParameter("action");
        System.out.println("action = " + action);
        if (action==null){
            doGetNullAction(req, resp);
        }else{
            if ("signin".equals(action)){
                doGetSignin(req, resp);
            }
            if ("signup".equals(action)){
                doGetSignup(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.print("Authorization => doPost: ");
        String action = (String) req.getParameter("action");
        System.out.println("action = " + action);
        if (action == null){
            doPostNullAction(req, resp);
        }else {
            if ("signin".equals(action)){
                doPostSignin(req, resp);
                return;
            }
            if ("signup".equals(action)){
                doPostSignup(req, resp);
                return;
            }
            if ("toStock".equals(action)){
                toStock(req, resp);
                return;
            }
        }

    }

    private void doGetNullAction(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher;
        if (AccountModel.getInstance().checkAdmin())
            requestDispatcher = req.getRequestDispatcher("views/authorization.jsp");
        else {
            requestDispatcher = req.getRequestDispatcher("views/registration.jsp");
            req.setAttribute("noAdminError", "n");
        }
        requestDispatcher.forward(req, resp);
    }

    private void doGetSignin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("views/authorization.jsp");
        requestDispatcher.forward(req, resp);
    }

    private void doGetSignup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("views/registration.jsp");
        if (!AccountModel.getInstance().checkAdmin()) {
            req.setAttribute("noAdminError", "n");
        }
        requestDispatcher.forward(req, resp);
    }

    private void doPostNullAction(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.getOutputStream().println("doPost happened with no action parameter");
    }
    private void toStock(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("/stock");
    }

    private void doPostSignin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String name = req.getParameter("name");
        String password = req.getParameter("pass");
        boolean cont = true;
        if (name.length() == 0 || password.length() == 0) {
            ArrayList<String> al = new ArrayList<String>();
            if (name.length() == 0) al.add("name");
            if (password.length() == 0) al.add("pass");
            req.setAttribute("emptyFields", al);
        } else {
            AccountModel model = AccountModel.getInstance();
            Account acc = model.getAccount(name);
            if(acc==null) req.setAttribute("wrongPasswordOrLogin", name);
            else {
                if (!acc.checkPassword(password))
                    req.setAttribute("wrongPasswordOrLogin", password);
                else {
                    if(acc.getBlocked()) req.setAttribute("urBlocked", name);
                    else {
                        HttpSession session = req.getSession(true);
                        session.setAttribute(StockServlet.SESSION_ATTRIBUTE_LOGIN, acc.getLogin());
                        cont = false;
                        req.setAttribute("userName", name);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("views/authorization.jsp");
                        requestDispatcher.forward(req, resp);
                    }
                }
            }
        }
        if (cont)
            doGetSignin(req, resp);
    }

    private void doPostSignup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String name = req.getParameter("name");
        String password = req.getParameter("pass");
        System.out.println(name + " / " + password);
        if (name.length() == 0 || password.length() == 0) {
            ArrayList<String> al = new ArrayList<String>();
            if (name.length() == 0) al.add("name");
            if (password.length() == 0) al.add("pass");
            req.setAttribute("emptyFields", al);
            doGetSignup(req, resp);
            System.out.println("doGetSignup was called");
        } else {
            System.out.println("name = " + name + "; password = " + password + ".");
            AccountModel model = AccountModel.getInstance();
            Account acc = model.addAccount(name, password);
            if(acc==null) {
                req.setAttribute("exists", name);
                doGetSignup(req, resp);
            } else {
                HttpSession session = req.getSession(true);
                session.setAttribute(StockServlet.SESSION_ATTRIBUTE_LOGIN, acc.getLogin());
                if (acc.getRole()== AccountRole.admin) req.setAttribute("admin", name);
                else req.setAttribute("userName", name);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("views/registration.jsp");
                requestDispatcher.forward(req, resp);
            }
        }
    }
}
