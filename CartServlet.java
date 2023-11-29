package org.example.app.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.app.entities.*;
import org.example.app.model.AccountModel;
import org.example.app.model.Cart;
import org.example.app.model.Stock;

import java.io.IOException;
import java.util.ArrayList;

public class CartServlet extends HttpServlet {
    enum Action {
        change("changeQuantity"), history("history"), pay("pay"), main("toMainMenu"), cart("toCart"), stock("toStock"), remove("remove"), clear("clear");
        private String text;

        Action(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static Action getInstance(String s) {
            if (s == null) return null;
            Action act = null;
            for (Action a : Action.values())
                if (a.getText().equals(s)) {
                    act = a;
                    break;
                }
            return act;
        }
    }

    public static final String JSP_ATTRIBUTE_CHOCO = "choco";
    public static final String JSP_ATTRIBUTE_ITEM = "item";
    public static final String JSP_PARAMETER_ACTION = "action";
    public static final String JSP_PARAMETER_TYPE = "typeBox";
    public static final String JSP_PARAMETER_NAME = "nameField";
    public static final String JSP_PARAMETER_CALORIES = "caloriesField";
    public static final String JSP_PARAMETER_PRICE = "priceField";
    public static final String JSP_PARAMETER_MEASURE = "measureBox";
    public static final String JSP_PARAMETER_FILLING = "fillingField";

    public static final String JSP_PARAMETER_KIND = "chocoKindBox";
    public static final String JSP_PARAMETER_PERCENT = "percentField";
    public static final String JSP_PARAMETER_NUTS = "nutsBox";
    public static final String JSP_PARAMETER_QUANTITY = "quantityField";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("CartChoco => doGet");
        if (!validateAccess(req))
            noLoginFound(req, resp);
        else {
            doGetNullAction(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Cart => doPost");
        if (!validateAccess(req)) {
            noLoginFound(req, resp);
            return;
        }
        Action action = Action.getInstance(req.getParameter(JSP_PARAMETER_ACTION));
        System.out.println(action);
        if (action == null) {
            doGetNullAction(req, resp);
        } else {
            switch (action) {
                case stock:
                    change(req, resp);
                    toStock(req, resp);
                    break;
                case main:
                    change(req, resp);
                    toMain(req, resp);
                    break;
                case change:
                    change(req, resp);
                    doGetNullAction(req, resp);
                    break;
                case pay:
                    change(req, resp);
                    payCart(req, resp);
                    break;
                case remove:
                    removeFromCart(req, resp);
                    break;
                case clear:
                    clearCart(req, resp);
                    break;
                case history:
                    change(req, resp);
                    showHistory(req, resp);
                    break;
                case cart:
                    toCart(req, resp);
                    break;
                default:
                    System.out.println("unsupported action");
            }
        }
    }

    private void doGetNullAction(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String log = null;
        if (session != null) {
            log = (String) session.getAttribute("login");
            System.out.println("Cart log:" + log);
        }
        if (log != null) {
            if (AccountModel.getInstance().getAccount(log).getRole() == AccountRole.user) {
                Cart c = new Cart(log);
                req.setAttribute("cart", c);
            }
        }
        System.out.println(log);
        RequestDispatcher requestDispatcher;
        requestDispatcher = req.getRequestDispatcher("views/cart.jsp");
        requestDispatcher.forward(req, resp);
    }

    private void clearCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String log = null;
        if (session != null) {
            log = (String) session.getAttribute("login");
        }
        Cart c = new Cart(log);
        c.clearCart();
        req.setAttribute("cleared", "Корзина очищена!");
        doGetNullAction(req, resp);
    }

    private void payCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("tis pay timE");
        HttpSession session = req.getSession();
        String log = null;
        if (session != null) {
            log = (String) session.getAttribute("login");
        }
        Cart c = new Cart(log);
        String name = c.payCart();
        if (name != "") req.setAttribute("notEnough", name);
        else req.setAttribute("payed", c.getId());
        doGetNullAction(req, resp);
    }

    private void removeFromCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int article;
        if (req.getParameter("article") != "")
            article = Integer.parseInt(req.getParameter("article"));
        else
            article = Integer.parseInt(req.getParameter("qarticle"));
        System.out.println("remove");
        HttpSession session = req.getSession();
        String log = null;
        if (session != null) {
            log = (String) session.getAttribute("login");
        }
        Cart c = new Cart(log);
        Goods g = c.getList().get(c.findItem(article));
        g.showItem();
        c.removeItemDB(g);
        req.setAttribute("removed", g.getItem().getName());
        doGetNullAction(req, resp);
    }

    private void showHistory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("hist");
        HttpSession session = req.getSession();
        String log = null;
        if (session != null) {
            log = (String) session.getAttribute("login");
        }
        ArrayList<Cart> carts = Cart.loadHistory(log);
        req.setAttribute("hist", carts);
        RequestDispatcher requestDispatcher;
        requestDispatcher = req.getRequestDispatcher("views/history.jsp");
        requestDispatcher.forward(req, resp);
    }

    private void change(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("change q");
        HttpSession session = req.getSession();
        String log = null;
        if (session != null) {
            log = (String) session.getAttribute("login");
        }
        Cart c = new Cart(log);
        Goods g;
        int article;
        if (req.getParameter("article") != null) {
            article = Integer.parseInt(req.getParameter("article"));
            g = c.getList().get(c.findItem(article));
            if (g != null) {
                double qty = 0;
                String q = req.getParameter("quantity");
                if (req.getParameter("quantity") != "" && req.getParameter("quantity") != "0,"&& req.getParameter("quantity") != null)
                    if (Double.parseDouble(req.getParameter("quantity")) != 0)
                        qty = Double.parseDouble(req.getParameter("quantity"));
                if ((g.getQuantity() != qty) && (qty > 0)) {
                    g.setQuantity(qty);
                    c.updateItemInDB(g);
                    req.setAttribute("changed", g.getItem().getName());
                }
            }
        }
        if (req.getParameter("nextArt") != null) {
            req.setAttribute("changeArticle", req.getParameter("nextArt"));
        }
    }

    private void toStock(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("fromCartToStock");
        resp.sendRedirect("/stock");
    }

    private void toCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("fromHistToCart");
        doGetNullAction(req, resp);
    }

    private void toMain(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("toMainFromCart");
        if (req.getSession() != null)
            req.getSession().removeAttribute("login");
        resp.sendRedirect("/authorization");
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

    private boolean validateAccess(HttpServletRequest req) {
        boolean ex = false;
        HttpSession session = req.getSession();
        String accLogin = null;
        if (session != null)
            accLogin = (String) session.getAttribute(StockServlet.SESSION_ATTRIBUTE_LOGIN);
        if (accLogin != null)
            ex = true;
        return ex;
    }
}
