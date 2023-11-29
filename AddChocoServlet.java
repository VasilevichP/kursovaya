package org.example.app.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import org.example.app.entities.*;
import org.example.app.model.AccountModel;
import org.example.app.model.Stock;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialStruct;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class AddChocoServlet extends HttpServlet {
    enum Action {
        edit("edit"), add("add"), cancel("cancel"), url("setUrl");
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
    public static final String JSP_ATTRIBUTE_IMAGE = "imageField";
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
    public static final String JSP_PARAMETER_URL = "urlname";
    public static final String JSP_PARAMETER_FILE = "file";
    public static final String SES_ATTRIBUTE_FILE = "tempFile";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("AddChoco => doGet");
        if (!validateAccess(req))
            noLoginFound(req, resp);
        else {
            doGetNullAction(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("AddChoco => doPost");
        if (!validateAccess(req)) {
            noLoginFound(req, resp);
            return;
        }
        Action action = Action.getInstance(req.getParameter(JSP_PARAMETER_ACTION));
        System.out.println(action);
        for (Part part : req.getParts()) {
            System.out.println(part.getName());
        }
        System.out.println("Parameters:");

        if (action == null) {
            doGetNullAction(req, resp);
        } else {
            switch (action) {
                case cancel:
                    cancel(req, resp);
                    break;
                case add:
                    add(req, resp);
                    break;
                case edit,url:
                    edit(req, resp);
                    break;
                default:
                    System.out.println("unsupported action");
            }
        }
    }


    private void doGetNullAction(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Chocolate choco = (Chocolate) req.getAttribute(JSP_ATTRIBUTE_CHOCO);
        if (choco == null) {
            choco = new Chocolate();
            req.setAttribute(JSP_ATTRIBUTE_CHOCO, choco);
        }
        RequestDispatcher requestDispatcher;
        requestDispatcher = req.getRequestDispatcher("views/add-choco.jsp");
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

    private void cancel(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("cancelled");
        resp.sendRedirect("/stock");
    }


    private boolean validateAccess(HttpServletRequest req) {
        boolean ex = false;
        HttpSession session = req.getSession();
        String accLogin = null;
        if (session != null)
            accLogin = (String) session.getAttribute(StockServlet.SESSION_ATTRIBUTE_LOGIN);
        if (accLogin != null && AccountModel.getInstance().getAccount(accLogin).getRole() == AccountRole.admin)
            ex = true;
        return ex;
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("editing");
        Chocolate choco = (Chocolate) req.getAttribute(JSP_ATTRIBUTE_CHOCO);
        Types type = Types.getInstance(req.getParameter(JSP_PARAMETER_TYPE));
        System.out.println(type.getText());
        if ((type != null) && ((choco == null) || (choco.getType() != type))) {
            choco = createChoco(type);
        }
        choco = populateChoco(req, resp, choco);
        if (choco != null) req.setAttribute(JSP_ATTRIBUTE_CHOCO, choco);
        setImage(req);
        doGetNullAction(req, resp);
    }

    private Chocolate createChoco(Types type) {
        System.out.println("Creating");
        switch (type) {
            case tile:
                return new TiledChocolate();
            case sweets:
                return new Sweets();
            case bars:
                return new ChocolateBars();
            case nuts:
                return new NutsInChocolate();
            case cookies:
                return new Cookies();
            case paste:
                return new ChocolatePaste();
            case powder:
                return new ChocolatePowder();
            default:
                return null;
        }
    }

    private void setImage(HttpServletRequest req) throws ServletException, IOException {
        Part filePart = req.getPart(JSP_PARAMETER_FILE);
        HttpSession session = req.getSession();
        String loadedImage;
        FileUtility fileUtil = FileUtility.getInstance();
        fileUtil.createDirectory();
        if (session.getAttribute(SES_ATTRIBUTE_FILE) != null)
            loadedImage = (String) session.getAttribute(SES_ATTRIBUTE_FILE);
        else loadedImage = "";

        String url = req.getParameter(JSP_PARAMETER_URL);
        if ((url != null) && (url.length()>0))
            req.setAttribute(JSP_ATTRIBUTE_IMAGE, url);
        if (loadedImage!=null)
            loadedImage = fileUtil.loadTempImage(filePart, loadedImage);
        if (loadedImage.length()>0){
            session.setAttribute(SES_ATTRIBUTE_FILE, loadedImage);
            req.setAttribute(JSP_ATTRIBUTE_IMAGE, loadedImage);
        }
    }

    private String getImage(HttpServletRequest req, int article) throws ServletException, IOException {
        String name = "";
        String fileName = Integer.toString(article);
        HttpSession session = req.getSession();
        String loadedImage="";
        if (session.getAttribute(SES_ATTRIBUTE_FILE) != null)
            loadedImage = (String) session.getAttribute(SES_ATTRIBUTE_FILE);
        Part filePart = req.getPart(JSP_PARAMETER_FILE);
        FileUtility fileUtil = FileUtility.getInstance();
        if (filePart!=null)
            name = fileUtil.loadLocalImage(filePart, fileName);
        if (name.length()==0){
            String url = req.getParameter(JSP_PARAMETER_URL);
            if (url!="" && url!=null)
                name = fileUtil.loadRemoteImage(url, fileName);
        }
        if (name.length()==0){
            name = fileUtil.restoreTempImage(loadedImage, fileName);
        }
        fileUtil.deleteTempFile(loadedImage);
        System.out.println("name="+name);
        session.removeAttribute(SES_ATTRIBUTE_FILE);
        req.removeAttribute(JSP_ATTRIBUTE_IMAGE);
        return name;
    }

    private Chocolate populateChoco(HttpServletRequest req, HttpServletResponse resp, Chocolate choco) throws ServletException, IOException {
        if ((choco != null) && (choco.getType() != null)) {
            System.out.println("Populating");

            String name = req.getParameter(JSP_PARAMETER_NAME);
            if (name != null) choco.setName(name);
            int calories;
            if (req.getParameter(JSP_PARAMETER_CALORIES) != null) {
                try {
                    calories = Integer.parseInt(req.getParameter(JSP_PARAMETER_CALORIES));
                } catch (NumberFormatException e) {
                    calories = 0;
                }
                choco.setCalories(calories);
            }
            double price;
            if (req.getParameter(JSP_PARAMETER_PRICE) != null) {
                try {
                    price = Double.parseDouble(req.getParameter(JSP_PARAMETER_PRICE));
                } catch (NumberFormatException e) {
                    price = 0;
                }
                choco.setPrice(price);
            }
            if (req.getParameter(JSP_PARAMETER_MEASURE) != null) {
                if (choco.getType() == Types.sweets || choco.getType() == Types.cookies || choco.getType() == Types.paste)
                    choco.setMeasure(Measures.getInstance(req.getParameter(JSP_PARAMETER_MEASURE)));
            }
            if (choco.getType() == Types.tile) {
                String filling = req.getParameter(JSP_PARAMETER_FILLING);
                if (filling != null) ((TiledChocolate) choco).setFilling(filling);
                if (req.getParameter(JSP_PARAMETER_PERCENT) != null) {
                    byte percent;
                    try {
                        percent = Byte.parseByte(req.getParameter(JSP_PARAMETER_PERCENT));
                    } catch (NumberFormatException e) {
                        percent = 0;
                    }
                    ((TiledChocolate) choco).setCacaoPercent(percent);
                }
                ((TiledChocolate) choco).setChocolateKind(ChocolateKinds.getInstance(req.getParameter(JSP_PARAMETER_KIND)));
            }
            if (choco.getType() == Types.nuts) {
                ((NutsInChocolate) choco).setTypeOfNuts(Nuts.getInstance(req.getParameter(JSP_PARAMETER_NUTS)));
                ((NutsInChocolate) choco).setChocolateKind(ChocolateKinds.getInstance(req.getParameter(JSP_PARAMETER_KIND)));
            }
        }
        return choco;
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Chocolate choco = (Chocolate) req.getAttribute(JSP_ATTRIBUTE_CHOCO);
        Types type = Types.getInstance(req.getParameter(JSP_PARAMETER_TYPE));
        if ((type != null) && ((choco == null) || (choco.getType() != type))) {
            choco = createChoco(type);
        }
        choco = populateChoco(req, resp, choco);
        Goods g = new Goods(choco, Double.parseDouble(req.getParameter(JSP_PARAMETER_QUANTITY)));
        System.out.println(g.showItem());
        Stock model = Stock.getInstance();
        model.addChocolate(g);
        System.out.println("added");
        g.getItem().setImage(getImage(req, g.getItem().getArticle()));
        model.updateDB(g);
        //choco=new Chocolate();
        req.setAttribute(JSP_ATTRIBUTE_CHOCO, choco);
        req.setAttribute("isAdded", g.getItem().getName());
        req.getSession().removeAttribute("filepart");
        doGetNullAction(req, resp);
    }
}
