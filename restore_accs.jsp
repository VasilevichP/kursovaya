<%@page import="java.util.ArrayList" %>
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
            document.getElementById("deleteBtn").disabled=false;
            document.getElementById("restoreBtn").disabled=false;
       }
       </script>
       <style>
           body {
             background-image: url("../images/choco10.jpg");
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
           height: 250px;
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
        ::placeholder { color: #000; }
      </style>
      <style>
          .snackbar {
            visibility: hidden;
            min-width: 250px;
            margin-left: -125px;
            background-color: #4caf50;
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
            <div class="w3-container w3-brown  w3-center w3-text-white">
                <h2>Удаленные учетные записи</h2>
            </div>
            <%
                ArrayList<Account> accounts = (ArrayList<Account>) request.getAttribute("delAccs");
                if (accounts == null) out.println("Account list is missing");
                else{

                        out.println("<div class=\"scroll-table\"><table class=\"w3-hoverable w3-white w3-text-brown w3-border w3-bordered w3-centered\"><thead><tr><th>Логин</th><th>Пароль</th></tr></thead></table> ");
                        out.println("<div class=\"scroll-table-body w3-hoverable w3-white w3-text-brown w3-border w3-bordered w3-centered\"><table ><tbody>");
                        if(accounts.size()==0){
                        out.println("<tr><td class=\"w3-centered w3-border-0\">Нет удаленных учетных записей</td></tr>");
                        }
                        else{
                        for (Account a : accounts){
                            out.println("<tr class=\"w3-text-brown\" onclick=\"func(this)\"><td>" + a.getLogin() + "</td><td>" + a.getPassword() + "</td></tr>");
                        }
                        }
                        out.println("</tbody></table></div></div>");
                }
            %>
        </div>

        <div class="w3-container w3-padding">

        <form method="POST">
            <table border="0" class="w3-text-brown" style="width:25%;">
               <TR><td>Логин: </td><td><input type="text" class = "w3-right w3-right-align w3-border-0 w3-border-sand w3-text-brown w3-sand" id="tlogin" name="tlogin" readonly> </td></TR>
               <TR><td>Пароль: </td><td class=w3-right-align id="tpassword" readonly></td></TR>
            </table>
            <div>

            </div>

            <div class="w3-container w3-sand w3-text-brown w3-left-align w3-padding-small" style="width:25%; margin-top:10px">
                <button type="submit" class="w3-btn w3-text-white w3-brown w3-padding-small w3-round-large w3-right" onclick="return confirm('Вы точно хотите безвозвратно удалить этого пользователя?')" formaction="/accounts?action=deleteForever" id="deleteBtn" disabled>Удалить навсегда</button>
                <button type="submit" class="w3-btn w3-text-white w3-brown w3-padding-small w3-round-large w3-left" formaction="/accounts?action=restore" id="restoreBtn" disabled>Восстановить</button>
            </div>
        </form>
    </div>

    <div class="w3-container  w3-text-brown w3-padding">
           <form method="POST">
               <button type="submit" class="w3-button w3-sand w3-right w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 11pt"  formaction="/accounts?action=toAccounts" >Вернуться</button>
           </form>
   </div>
   </div>

     <div id="snackbar1" class="snackbar"><h5>Пользователь <%= request.getAttribute("restored") %> был восстановлен!</h5></div>
     <div id="snackbar2" class="snackbar"><h5>Пользователь <%= request.getAttribute("removed") %> был удален!</h5></div>

</body>
<% if (request.getAttribute("restored") != null) { %>
<script>
var x = document.getElementById("snackbar1");
  x.className = "snackbar show";
  setTimeout(function(){ x.className = x.className.replace("show", "");}, 1500);
</script>
<% } %>

<% if (request.getAttribute("removed") != null) { %>
<script>
var x = document.getElementById("snackbar2");
  x.className = "snackbar show";
  setTimeout(function(){ x.className = x.className.replace("show", "");}, 1500);
</script>
<% } %>

</html>