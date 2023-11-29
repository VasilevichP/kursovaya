<%@page import="java.util.List" %>
<%@page import="org.example.app.entities.Account" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Admin_stock</title>
        <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
            <%
            String name = (String) request.getAttribute("login");
            %>
       <script>
       function func(row){
            var tds = row.cells;
            document.getElementById("tlogin").innerText=tds[0].innerText;
            document.getElementById("tlogin").value=tds[0].innerText;
            document.getElementById("tpassword").innerText=tds[1].innerText;
            document.getElementById("tblocked").innerText=tds[2].innerText;
            document.getElementById("deleteBtn").disabled=false;
            document.getElementById("blockBtn").disabled=false;
       }
       </script>
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
         .scroll-table-body {
            height: 200px;
            overflow-x: auto;
            border: none;
            border-bottom: 1px solid #f3f3f3;
         }
         .scroll-table table {
            width:100%;
            table-layout: fixed;
         }
         .scroll-table thead th {
           padding: 10px 15px;
            font-weight: bold;
         }
         .scroll-table tbody td {
           font-color: #f3f3f3
            text-align: center;
            border: none;
            padding: 10px 15px;
            font-size: 14px;
            vertical-align: top;
            border-top: 2px solid #f3f3f3;
            border-bottom: 2px solid #f3f3f3;
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

    </head>

    <body class="w3-sand">

        <div class="w3-container w3-padding" style="margin-top:40px"></div>
        <div class="w3-container w3-sand w3-padding w3-round-large"  style="margin-top:20px; width:96%; margin:auto;">
        <div class="w3-container " style="margin-top: 20px">
        </div>
        <div class="w3-w3-container w3-center w3-padding">
            <div class="w3-container w3-text-brown  w3-center">
                <h2>Учетные записи</h2>
            </div>

            <form method="POST" action="/accounts?action=filter">
             <select class="w3-select w3-left" name="filtBox" id="filtBox" style="width:15%; margin-top:5px; margin-bottom:5px" required>
               <option value="" disabled selected>Статус:</option>
               <% if(request.getSession().getAttribute("noFilt")!=null){
                 out.println("<option value=\"Без фильтра\" selected>Без фильтра</option>");}
                 else{
                 out.println("<option value=\"Без фильтра\" >Без фильтра</option>");
               }
                 if(request.getSession().getAttribute("blocked")!=null){
                 out.println("<option value=\"Заблокированные\" selected>Заблокированные</option>");}
                 else{
                 out.println("<option value=\"Заблокированные\" >Заблокированные</option>");
               }
                 if(request.getSession().getAttribute("notBlocked")!=null){
                 out.println("<option value=\"Не заблокированные\" selected>Не заблокированные</option>");}
                 else{
                 out.println("<option value=\"Не заблокированные\" >Не заблокированные</option>");
               }
               %>
             </select></form>
            <%
                List<Account> accounts = (List<Account>) request.getAttribute("accounts");
                if (accounts == null) out.println("Account list is missing");
                else{

                        out.println("<div class=\"scroll-table\"><table class=\"w3-hoverable w3-brown  w3-text-white w3-border w3-bordered w3-centered\"><thead><tr><th>Логин</th><th>Пароль</th><th>Статус блокировки</th></tr></thead></table> ");
                        out.println("<div class=\"scroll-table-body w3-hoverable w3-white w3-text-brown w3-border w3-bordered w3-centered\"><table ><tbody>");
                        if(accounts.size()==0){
                        out.println("<tr><td class=\"w3-centered w3-border-0\">Нет подходящих учетных записей</td></tr>");
                        }
                        else{
                        for (Account a : accounts){
                            out.println("<tr class=\"w3-text-brown\" onclick=\"func(this)\"><td>" + a.getLogin() + "</td><td>" + a.getPassword() + "</td><td>" + (a.getBlocked()?"Заблокирован":"Не заблокирован") + "</td></tr>");
                        }
                        }
                        out.println("</tbody></table></div></div>");
                }
            %>
        </div>

        <div class="w3-container w3-sand w3-text-brown w3-padding">
            <form method="POST">
            <button type="submit" class="w3-btn w3-right w3-round-large" formaction="/accounts?action=toRestoreMenu" >Восстановить удаленные аккаунты</button>
            </form>
        </div>

        <div class="w3-container w3-padding">

        <form method="POST">
            <table border="0" class="w3-text-brown">
               <TR><td>Логин: </td><td><input type="text" class = "w3-right-align w3-border-0 w3-sand" id="tlogin" name="tlogin" readonly> </td></TR>
               <TR><td>Пароль: </td><td class=w3-right-align id="tpassword" readonly></td></TR>
               <TR><td>Статус блокировки: </td><td class=w3-right-align id="tblocked" readonly> </td></TR>
            </table>
            <div>

            </div>

            <div class="w3-container w3-sand w3-text-brown w3-padding-small" style="width:27%">
                <button type="submit" class="w3-btn w3-text-white w3-brown w3-padding-small w3-round-large w3-left" onclick="return confirm('Вы точно хотите удалить этого пользователя?')" formaction="/accounts?action=delete" id="deleteBtn" disabled>Удалить</button>
                <button type="submit" class="w3-btn w3-text-white w3-brown w3-padding-small w3-round-large w3-right" formaction="/accounts?action=block" id="blockBtn" disabled>(За/раз)блокировать</button>
            </div>
        </form>
    </div>

    <div class="w3-container  w3-text-brown w3-padding">
           <form method="POST">
               <button type="submit" class="w3-button w3-sand w3-right w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 11pt"  formaction="/accounts?action=toStock" >Управление товарами</button>
               <button type="submit" class="w3-button w3-sand w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 11pt"  formaction="/accounts?action=signout" >В главное меню</button>
           </form>
   </div>
   </div>

   <div id="snackbar1" class="snackbar success"><h5>Удален пользователь <%= request.getAttribute("deleted") %>!</h5> </div>
   <div id="snackbar2" class="snackbar success"><h5>Статус пользователя <%= request.getAttribute("blocked") %> был изменен!</h5></div>
   <div id="snackbar3" class="snackbar error"><h5>Нельзя заблокировать администратора!</h5></div>
   <div id="snackbar4" class="snackbar error"><h5>Нельзя удалить администратора!</h5></div>

</body>
<script>
    document.getElementById("filtBox").addEventListener("change", ({ target }) => target.form.submit());
</script>

<% if (request.getAttribute("deleted") != null) { %>
<script>
var x = document.getElementById("snackbar1");
  x.className = "snackbar success show";
  setTimeout(function(){ x.className = x.className.replace("show", ""); }, 1500);
</script>
<% } %>

<% if (request.getAttribute("blocked") != null) { %>
<script>
var x = document.getElementById("snackbar2");
  x.className = "snackbar success show";
  setTimeout(function(){ x.className = x.className.replace("show", "");},1500);
</script>
<% } %>

<% if (request.getAttribute("adminBlock") != null) { %>
<script>
var x = document.getElementById("snackbar3");
  x.className = "snackbar error show";
  setTimeout(function(){ x.className = x.className.replace("show", "");}, 1500);
</script>
<% } %>

<% if (request.getAttribute("adminDel") != null) { %>
<script>
var x = document.getElementById("snackbar4");
  x.className = "snackbar error show";
  setTimeout(function(){ x.className = x.className.replace("show", "");}, 1500);
</script>
<% } %>

</html>