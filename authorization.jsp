<%@page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Chocolate_shop - auth.jsp</title>

            <style>
                body {
                  background-image: url("../images/choco6.jpg");
                  background-repeat: no-repeat;
                  background-position: center center;
                  background-attachment: fixed;
                  -webkit-background-size: cover;
                  -moz-background-size: cover;
                  -o-background-size: cover;
                  background-size: cover;
                }
            </style>
            <style>
            .snackbar {
              visibility: hidden;
              min-width: 250px;
              margin-left: -125px;
              color: #fff;
              text-align: center;
              border-radius: 2px;
              padding: 16px;
              position: fixed;
              z-index: 1;
              left: 50%;
              bottom: 30px;
              font-size: 17px;
            }
            .success{
            background-color: #4caf50;
            }
            .error{
            background-color: #ff3131;
            }
            .snackbar.show {
              visibility: visible;
              -webkit-animation: fadein 0.5s, fadeout 0.5s 1.5s;
              animation: fadein 0.5s, fadeout 0.5s 1.5s;
            }
            .snackbar.sshow {
              visibility: visible;
              -webkit-animation: fadein 0.5s, fadeout 0.5s 1.0s;
              animation: fadein 0.5s, fadeout 0.5s 1.0s;
            }

            @-webkit-keyframes fadein {
              from {bottom: 0; opacity: 0;}
              to {bottom: 30px; opacity: 1;}
            }

            @keyframes fadein {
              from {bottom: 0; opacity: 0;}
              to {bottom: 30px; opacity: 1;}
            }
            @-webkit-keyframes fadeout {
              from {bottom: 30px; opacity: 1;}
              to {bottom: 0; opacity: 0;}
            }

            @keyframes fadeout {
              from {bottom: 30px; opacity: 1;}
              to {bottom: 0; opacity: 0;}
            }
       </style>
       <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">

    </head>

    <body class="w3-sand ">
         <div class="w3-container w3-padding" style="margin-top:80px; margin-bottom:20px">
        <div class="w3-card-2 w3-round-large w3-sand w3-centered" style=" margin-top: 50px; margin: auto; width:30%">
            <div class="w3-container w3-center w3-round-large w3-padding-large w3-text-brown w3-sand">
                <h2> Авторизация  </h2>
            </div>
            <form method="POST" action = "/authorization?action=signin" class="w3-selection w3-sand w3-padding-large">
                <label class="w3-text-brown">Логин:
                <%
                List<String> al = (List<String>) request.getAttribute("emptyFields");
                boolean b = false;
                if (al != null && !al.isEmpty())
                  for (String s : al)
                    if (s.equals("name")) {
                      b = true; break;}
                if (!b) {
                  String s = request.getParameter("name");
                  if (s == null) s = "";
                  out.println("<input type=\"text\" name=\"name\" class=\"w3-input  w3-border w3-round-large\" style=\"width: 100%\"value=\"" + s + "\">");
                } else {
                  out.println("<input type=\"text\" name=\"name\" class=\"w3-input  w3-border w3-round-large w3-border-red\"style=\"width: 100%\">");
                }
                %>
                <br />
                </label>
                <label class="w3-text-brown">Пароль:
                <%
                    b = false;
                    if (al != null && !al.isEmpty())
                      for (String s : al)
                        if (s.equals("pass")) {
                          b = true;}
                    if (!b) {
                        out.println("<input type=\"password\" name=\"pass\" class=\"w3-input  w3-border w3-round-large\" style=\"width: 100%\">");
                        }
                    else{
                        out.println("<input type=\"password\" name=\"pass\" class=\"w3-input w3-border w3-round-large w3-border-red\" style=\"width: 100%\">");
                        }
                %>
                <br />
                </label>
                <button type="submit" class="w3-btn  w3-text-white w3-brown w3-round-large w3-margin-bottom">Войти</button>

            </form>
            <div class="w3-container  w3-right-align w3-padding">
               <button class="w3-button w3-sand w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 11pt" onclick="location.href='?action=signup'">Перейти к регистрации</button>
            </div>
        </div>
    </div>
    <form method="POST" id="toStockForm" action = "/authorization?action=toStock"></form>
     <div id="snackbar1" class="snackbar success"><h5>Авторизовался пользователь <%= request.getAttribute("userName") %>!</h5> </div>
     <div id="snackbar2" class="snackbar error"><h5>Заполните все поля!</h5></div>
     <div id="snackbar3" class="snackbar error"><h5>Неверный логин или пароль!</h5></div>
     <div id="snackbar4" class="snackbar error"><h5>Пользователь <%= request.getAttribute("urBlocked") %> заблокирован!</h5></div>

</body>

<% if (request.getAttribute("userName") != null) { %>
<script>
var x = document.getElementById("snackbar1");
  x.className = "snackbar success sshow";
  setTimeout(function(){ x.className = x.className.replace("show", ""); document.getElementById("toStockForm").submit();}, 1500);
</script>
<% } %>

<% if (request.getAttribute("emptyFields") != null) { %>
<script>
var x = document.getElementById("snackbar2");
  x.className = "snackbar error show";
  setTimeout(function(){ x.className = x.className.replace("show", "");}, 2000);
</script>
<% } %>

<% if (request.getAttribute("wrongPasswordOrLogin") != null) { %>
<script>
var x = document.getElementById("snackbar3");
  x.className = "snackbar error show";
  setTimeout(function(){ x.className = x.className.replace("show", "");}, 2000);
</script>
<% } %>

<% if (request.getAttribute("urBlocked") != null) { %>
<script>
var x = document.getElementById("snackbar4");
  x.className = "snackbar error show";
  setTimeout(function(){ x.className = x.className.replace("show", "");}, 2000);
</script>
<% } %>


</html>
