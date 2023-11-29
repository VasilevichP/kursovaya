<%@ page import="org.example.app.model.AccountModel" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<style>
    body {
      background-image: url("../images/choco6.jpg");
      background-color:rgba(0,0,0,.3);
      background-repeat: no-repeat;
      background-position: center center;
      background-attachment: fixed;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }
</style>
</head>
<%
    if(AccountModel.getInstance().checkAdmin()) {
      out.println("<head><title>Chocolate_shop - index.jsp</title>");
      out.println("<link rel=\"stylesheet\" href=\"https://www.w3schools.com/w3css/4/w3.css\">");
      out.println("</head><body class=\"w3-sand\">");
      out.println("<div  style=\" margin-top: 80px\"></div><div class=\"w3-container w3-padding\"><div class=\"w3-card-2 w3-sand w3-round-large w3-centered\" style=\" margin-top: 80px; margin: auto; width:30%\">");
      out.println("<div class=\"w3-container w3-center w3-round-large w3-padding-large w3-text-brown w3-sand\"><h2> Авторизация </h2>");
      out.println("</div><form action=\"/authorization?action=signin\" method=\"POST\" class=\"w3-selection w3-sand w3-padding-large\">");
      out.println("<label class=\"w3-text-brown\">Логин:<input type=\"text\" name=\"name\" class=\"w3-input  w3-border w3-round-large\" style=\"width: 100%\"><br />");
      out.println("</label><label class=\"w3-text-brown\">Пароль:<input type=\"password\" name=\"pass\" class=\"w3-input w3-border w3-round-large\" style=\"width: 100%\"><br />");
      out.println("</label><button type=\"submit\" onclick=\"location.href='/authorization'\"class=\"w3-btn  w3-brown w3-text-white w3-round-large w3-margin-bottom\">Войти</button></form>");
      out.println("<div class=\"w3-container w3-text-brown w3-right-align w3-padding\">");
      out.println("<button class=\"w3-button w3-sand w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large\" style=\"font-size: 11pt\" onclick=\"location.href='/authorization?action=signup'\">Зарегистрироваться</button></div></div></div>");
    }
    else{
      out.println("<head><title>Chocolate_shop - index.jsp</title>");
      out.println("<link rel=\"stylesheet\" href=\"https://www.w3schools.com/w3css/4/w3.css\">");
      out.println("</head><body class=\"w3-sand\">");
      out.println("<div style=\" margin-top: 80px\"></div><div class=\"w3-container w3-round-large w3-padding\"><div class=\"w3-card-2 w3-sand w3-round-large w3-centered\" style=\" margin-top: 80px; margin: auto; width:50%\">");
      out.println("<div class=\"w3-container w3-center w3-round-large w3-padding-large w3-text-brown w3-sand\"><h2>Создание первого администратора</h2>");
      out.println("</div><form action=\"/authorization?action=signup\" method=\"POST\" class=\"w3-selection w3-sand w3-padding\">");
      out.println("<label class=\"w3-text-brown\">Логин:<input type=\"text\" name=\"name\" class=\"w3-input  w3-round-large\" style=\"width: 100%\"><br />");
      out.println("</label><label class=\"w3-text-brown\">Пароль:<input type=\"password\" name=\"pass\" class=\"w3-input w3-border w3-round-large\" style=\"width: 100%\"><br />");
      out.println("</label><button type=\"submit\" class=\"w3-btn w3-brown w3-text-white w3-round-large w3-margin-bottom\">Создать</button></form></div>");
      out.println("</div>");
    }
%>

</body>
</html>
