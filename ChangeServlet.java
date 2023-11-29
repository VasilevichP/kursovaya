package org.example.app.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import org.example.app.entities.*;
import org.example.app.model.AccountModel;
import org.example.app.model.Cart;
import org.example.app.model.Stock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.app.servlets.AddChocoServlet.JSP_PARAMETER_FILE;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class ChangeServlet extends HttpServlet {
    enum Action {
        edit("edit"), change("change"), addToCart("addToCart"), cancel("cancel"),remove("remove"),img("img");
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
    public static final String JSP_PARAMETER_URL = "urlname";
    public static final String JSP_PARAMETER_FILE = "file";

    private String filePath;

    @Override
    public void init() throws ServletException {
        super.init();
        filePath = "D:\\Work\\Java\\Test JavaFX\\demoweb\\web\\images\\";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("ChangeChoco => doGet");
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
        if (action == null) {
            doGetNullAction(req, resp);
        } else {
            switch (action) {
                case cancel:
                    cancel(req, resp);
                    break;
                case addToCart:
                    addToCart(req, resp);
                    break;
                case change:
                    change(req, resp);
                    break;
                case remove:
                    remove(req, resp);
                    break;
                default:
                    System.out.println("unsupported action");
            }
        }
    }

    private void doGetNullAction(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Goods g = null;
        String log = null;
        if (session != null) {
            g = (Goods) session.getAttribute("item");
            log = (String) session.getAttribute("login");
            System.out.println("Ch log:" + log);
        }
        if (g != null) req.setAttribute("itemToChange", g);
        if (log != null) {
            if (AccountModel.getInstance().getAccount(log).getRole() == AccountRole.user)
                req.setAttribute("userSession", log);
            else req.setAttribute("adminSession", log);
        }
        System.out.println(g.showItem());
        System.out.println(log);
        RequestDispatcher requestDispatcher;
        requestDispatcher = req.getRequestDispatcher("views/change.jsp");
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
        HttpSession session = req.getSession();
        String log = null;
        if (session != null) {
            log = (String) session.getAttribute("login");
            System.out.println("Ch log:" + log);
        }
        if (log != null) {
            if (AccountModel.getInstance().getAccount(log).getRole() == AccountRole.user) resp.sendRedirect("/ustock");
            else resp.sendRedirect("/stock");
        }
    }
    private String getImage(HttpServletRequest req, int article) throws ServletException, IOException {
        String name = "";
        String fileName = Integer.toString(article);
//        HttpSession session = req.getSession();
//        String loadedImage="";
//        if (session.getAttribute(SES_ATTRIBUTE_FILE) != null)
//            loadedImage = (String) session.getAttribute(SES_ATTRIBUTE_FILE);
        Part filePart = req.getPart(JSP_PARAMETER_FILE);
        FileUtility fileUtil = FileUtility.getInstance();
        if (filePart!=null)
            name = fileUtil.loadLocalImage(filePart, fileName);
        if (name.length()==0){
            String url = req.getParameter(JSP_PARAMETER_URL);
            if (url!="" && url!=null)
                name = fileUtil.loadRemoteImage(url, fileName);
        }
//        if (name.length()==0){
//            name = fileUtil.restoreTempImage(loadedImage, fileName);
//        }
//        fileUtil.deleteTempFile(loadedImage);
        System.out.println("name="+name);
        return name;
    }
//    private String setImg(HttpServletRequest req, HttpServletResponse resp, Goods g) throws ServletException, IOException {
//        Part filePart = req.getPart("file");
//        String fileName = "";
//        if (filePart != null) {
//            fileName = getLocalFile(filePart,g.getItem().getArticle());
//        }
//        if (fileName.length() == 0) {
//            Part urlPart = req.getPart("urlname");
//            if (urlPart != null) {
//                fileName = getRemoteFile(urlPart,g.getItem().getArticle());
//            }
//
//        }
//        if (fileName.length() == 0) {
//            req.setAttribute("noImg", fileName);
//            resp.getWriter().println("nothing was uploaded");
//        } else
//            req.setAttribute("url", fileName);
//        System.out.println("fname: "+fileName);
//        String urlname = (String) req.getParameter("urlname");
//        System.out.println("Urln:"+urlname);
//        if ((fileName=="") && (g.getItem().getImage()!="") && (!(urlname.length()==0)) && (!(urlname==null))) {
//            System.out.println("Still here");
//            return g.getItem().getImage();
//        }
//        return fileName;
//    }
//
//    private String getLocalFile(Part filePart, int a) {
//        System.out.println("localFile");
//        String name = "";
//        if ((filePart.getSubmittedFileName().length() == 0) || (!filePart.getContentType().contains("image"))) {
//            System.out.println("sit1");
//            return name;
//        }
//        String sourceName = filePart.getSubmittedFileName();
//        if (sourceName != null && sourceName.length() > 0) {
//            Optional<String> extension = Optional.ofNullable(sourceName)
//                    .filter(f -> f.contains(".")).map(f -> f.substring(sourceName.lastIndexOf(".") + 1));
//            name = Integer.toString(a) + "." + (extension.isPresent() ? extension.get() : "");
//            System.out.println("sit2(name):"+name);
//            try {
//                filePart.write(filePath + name);
//                System.out.println("sit3");
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//                name = "";
//            }
//        }
//        return name;
//    }
//
//    private String getRemoteFile(Part urlPart, int a) {
//        System.out.println("remoteFile");
//        String name = "";
//        String url;
//        try {
//            url = new BufferedReader(new InputStreamReader(urlPart.getInputStream()))
//                    .lines().collect(Collectors.joining("\n"));
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            return name;
//        }
//        System.out.println(url);
//        Optional<String> extension = Optional.ofNullable(url)
//                .filter(f -> f.contains(".")).map(f -> f.substring(url.lastIndexOf(".") + 1));
//        name = Integer.toString(a) + "." + (extension.isPresent() ? extension.get() : "");
//        System.out.println(name);
//        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
//             FileOutputStream fileOS = new FileOutputStream(filePath + name)) {
//            byte data[] = new byte[1024];
//            int byteContent;
//            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
//                fileOS.write(data, 0, byteContent);
//            }
//        } catch (IOException e) {
//            System.out.println("exc1: "+e.getMessage());
//            return "";
//        }
//        File f = new File(filePath + name);
//        try {
//            Image img = ImageIO.read(f);
//            if (img == null || img.getHeight(null) <= 0 || img.getWidth(null) <= 0) {
//                System.out.println("The file is not an image");
//                f.delete();
//                return "";
//            }
//        } catch (Exception e) {
//            System.out.println("exc2: "+e.getMessage());
//            f.delete();
//            return "";
//        }
//        return name;
//    }

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

    private void addToCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Goods stockGoods = (Goods) session.getAttribute("item");
        String log = (String) session.getAttribute("login");
        System.out.println(stockGoods.showItem());
        Goods g = new Goods(stockGoods.getItem().clone());
        g.setQuantity(Double.parseDouble(req.getParameter(JSP_PARAMETER_QUANTITY)));
        System.out.println(g.showItem());
        Cart c = new Cart(log);
        c.showCart();
        c.addToCart(g);
        c.showCart();
        req.setAttribute("added",g.getItem().getName());
        doGetNullAction(req,resp);
    }

    private void change(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
        System.out.println("change item method");
        Stock model = Stock.getInstance();
        HttpSession session = req.getSession();
        Goods g = (Goods) session.getAttribute("item");
        System.out.println(g.showItem());
        g=populateItem(req,resp,g);
        g.getItem().setImage(getImage(req,g.getItem().getArticle()));
        System.out.println(g.showItem());
        model.updateDB(g);
        req.setAttribute("changed",g.getItem().getName());
        doGetNullAction(req,resp);
    }

    private void remove(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
        System.out.println("remove item method");
        Stock model = Stock.getInstance();
        HttpSession session = req.getSession();
        Goods g = (Goods) session.getAttribute("item");
        System.out.println(g.showItem());
        if(model.removeChocolate(g.getItem().getArticle())>=0){
            req.setAttribute("removed",g.getItem().getName());
            model.removeFromDB(g);
        }
        doGetNullAction(req,resp);
    }
    private Goods populateItem(HttpServletRequest req,HttpServletResponse resp, Goods g) throws ServletException, IOException {
        if ((g != null)) {
            String name = req.getParameter(JSP_PARAMETER_NAME);
            if (name != null) g.getItem().setName(name);
            int calories;
            if (req.getParameter(JSP_PARAMETER_CALORIES) != null) {
                try {
                    calories = Integer.parseInt(req.getParameter(JSP_PARAMETER_CALORIES));
                } catch (NumberFormatException e) {
                    calories = 0;
                }
                g.getItem().setCalories(calories);
            }
            double price;
            if (req.getParameter(JSP_PARAMETER_PRICE) != null) {
                try {
                    price = Double.parseDouble(req.getParameter(JSP_PARAMETER_PRICE));
                } catch (NumberFormatException e) {
                    price = 0;
                }
                g.getItem().setPrice(price);
            }
            if (g.getItem().getType() == Types.tile) {
                String filling = req.getParameter(JSP_PARAMETER_FILLING);
                if (filling != null) ((TiledChocolate) g.getItem()).setFilling(filling);
                else ((TiledChocolate) g.getItem()).setFilling("-");
                if (req.getParameter(JSP_PARAMETER_PERCENT) != null) {
                    byte percent;
                    try {
                        percent = Byte.parseByte(req.getParameter(JSP_PARAMETER_PERCENT));
                    } catch (NumberFormatException e) {
                        percent = 0;
                    }
                    ((TiledChocolate) g.getItem()).setCacaoPercent(percent);
                }
                ((TiledChocolate) g.getItem()).setChocolateKind(ChocolateKinds.getInstance(req.getParameter(JSP_PARAMETER_KIND)));
            }
            if (g.getItem().getType() == Types.nuts) {
                ((NutsInChocolate) g.getItem()).setTypeOfNuts(Nuts.getInstance(req.getParameter(JSP_PARAMETER_NUTS)));
                ((NutsInChocolate) g.getItem()).setChocolateKind(ChocolateKinds.getInstance(req.getParameter(JSP_PARAMETER_KIND)));
            }
            if (req.getParameter(JSP_PARAMETER_QUANTITY) != null) {
                double quantity;
                try {
                    quantity = Double.parseDouble(req.getParameter(JSP_PARAMETER_QUANTITY));
                }catch (NumberFormatException e) {
                    quantity = 0;
                }
                g.setQuantity(quantity);
            }
        }
        return g;
    }

}
