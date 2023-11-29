<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<%@page import="org.example.app.entities.Goods" %>
<%@page import="org.example.app.model.Stock" %>
<%@page import="org.example.app.model.Cart" %>
<%@page import="org.example.app.entities.Chocolate" %>
<%@page import="org.example.app.entities.Measures" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>History</title>
        <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
            <%
            ArrayList<Cart> carts = (ArrayList<Cart>) request.getAttribute("hist");
            %>

        <script>
        function myFunction(id) {
          var x = document.getElementById(id);
          if (x.className.indexOf("w3-show") == -1) {
            x.className += " w3-show";
          } else {
            x.className = x.className.replace(" w3-show", "");
          }
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
          	height: 500px;
          	overflow-x: auto;
          	border: none;
          }
          .scroll-table table {
          	width:100%;
          	table-layout: fixed;
          }

        </style>
    </head>

    <body class="w3-sand">

        <div class="w3-container w3-padding" style="margin-top:40px"></div>
        <div class="w3-container w3-sand w3-padding w3-round-large"  style="margin-top:20px; width:96%; margin:auto;">

        <div class="w3-container w3-center w3-padding">
            <div class="w3-container w3-text-brown w3-center" style="margin-bottom:5px;width:100%">
                <h2>Товары</h2>
            </div>

                <div class="scroll-table w3-border w3-border-brown">
                <div class="scroll-table-body w3-sand w3-text-brown "><table ><tbody>
                <% if(carts.size()==0) { %>
                <tr><td class="w3-center w3-border-0">История пуста</td></tr>
                <% }else{ %>

               <% for (Cart c : carts){ %>
                    <tr style="margin-bottom:5px"><td class="w3-card-2 w3-text-brown w3-border w3-border-brown">
                      <header class="w3-container w3-padding w3-center">
                          <h3>Корзина № <%= c.getId() %></h3>
                       </header>
                     <div class="w3-container w3-padding-small" style="margin-top: 5px; margin-left:10px">
                       <p>Время оплаты: <%= c.formatedDate() %></p>
                       <p>Количество товаров в корзине: <%= c.calculateQuantity() %></p>
                       <p>Сумма товаров в корзине: <%= c.calculateValue() %> р.</p>
                     </div>

                     <div class="w3-container" style="margin-top: 5px; margin-left:10px">
                     <button onclick="myFunction('<%= c.getId() %>')" class="w3-button w3-sand w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large"><p style="text-decoration: underline;">Товары:</p></button>
                     </div>
                     <div class="w3-container w3-hide" id="<%= c.getId() %>" style="margin-top: 5px; margin-left:10px">
                     <%
                        int i = 1;
                        for (Goods g : c.getList()){ %>
                        <p><%= i %>. <%= g.getItem().getName() %>, <%= g.getQuantity() %> <%= g.getItem().getMeasure().getText() %></p>
                        <% i++; %>
                     <% } %>
                     </div>



                    </td></tr>
               <% } %>
               <% } %>
                </tbody></table></div></div>
                </div>

        <div class="w3-container w3-text-brown w3-padding">
            <form method="POST">
                <button type="submit" class="w3-button w3-left w3-sand w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 11pt"  formaction="/cart?action=toCart" >Вернуться</button>
            </form>
        </div>

    </body>
</html>