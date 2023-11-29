<%@page import="java.util.List" %>
<%@page import="org.example.app.entities.Types" %>
<%@page import="org.example.app.entities.TiledChocolate" %>
<%@page import="org.example.app.entities.NutsInChocolate" %>
<%@page import="org.example.app.entities.Nuts" %>
<%@page import="org.example.app.entities.Measures" %>
<%@page import="org.example.app.entities.ChocolateKinds" %>
<%@page import="org.example.app.entities.Goods" %>
<%@page import="org.example.app.model.Stock" %>
<%@page import="org.example.app.model.Cart" %>
<%@page import="org.example.app.entities.Chocolate" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>User_cart</title>
        <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
            <%
            Cart c = (Cart) request.getAttribute("cart");
            int changeArticle=0;
            if (request.getAttribute("changeArticle") != null && request.getAttribute("changeArticle") != "")
              {
              changeArticle=Integer.parseInt((String)request.getAttribute("changeArticle"));
              }
            else
              changeArticle=0;
            Goods cg;
            cg = changeArticle !=0 ? c.getItem(c.findItem(changeArticle)) : null;
            %>

        <script>
           var restValue;
           function func(row){
                document.getElementById("nextArt").value=row.cells[0].innerText;
                document.getElementById("quForm").submit();
                }

           function quFun(){
                  quan=Number(document.getElementById("quantity").value);
                  pr=Number(document.getElementById("price").innerText);
                  document.getElementById("selectedRow").cells[2].innerText=quan;
                  if(document.getElementById("tmeas").innerText=="г"){
                      document.getElementById("selectedRow").cells[4].innerText=(quan*pr/100).toFixed(2);
                      v=Number(document.getElementById("quantity").value) * Number(document.getElementById("price").innerText) / 100;}
                  else{
                      document.getElementById("selectedRow").cells[4].innerText=(quan*pr).toFixed(2);
                      v=Number(document.getElementById("quantity").value) * Number(document.getElementById("price").innerText);}
                  document.getElementById("totalValue").innerText=(v+restValue).toFixed(2);
               }
            </script>
            <style>
                body {
                  background-image: url("../images/choco11.jpg");
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
                  	height: 300px;
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
                      -webkit-animation: fadein 0.5s, fadeout 0.5s 1.5s;
                      animation: fadein 0.5s, fadeout 0.5s 1.5s;
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
    <style>
        input::-webkit-outer-spin-button,
        input::-webkit-inner-spin-button {
               -webkit-appearance: none;
                margin: 0;
        }

        input[type=number] {
            -moz-appearance: textfield;
        }
    </style>

    </head>

    <body class="w3-sand">

      <div class="w3-container w3-padding" style="margin-top:40px"></div>
              <div class="w3-container w3-sand w3-round-large w3-padding" style="margin-top:20px; width:96%; margin:auto;">
              <div class="w3-container w3-center w3-padding-small">
              <div class="w3-container w3-text-brown">
                 <h2 class="w3-center">Корзина</h2>
                 <button type="submit" form="quForm" class="w3-button w3-sand w3-right w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 11pt"  formaction="/cart?action=history" >Посмотреть историю заказов</button>
              </div>
              <div class="w3-row">
                <div class="w3-col s7 w3-left w3-padding-large">
                        <%
                            if (c == null) out.println("Cart is missing");
                            else{
                                    out.println("<div class=\"scroll-table\"><table class=\"w3-hoverable w3-white w3-text-brown w3-border w3-bordered w3-centered\"><thead><tr><th>Артикул</th><th>Название товара</th><th>Кол-во</th><th>Единица измерения</th><th>Цена</th><th style=\"display:none\">нач</th><th style=\"display:none\">Калл</th><th style=\"display:none\">тип</th><th style=\"display:none\">какао</th><th style=\"display:none\">шок-ор</th><th style=\"display:none\">шок-пл</th><th style=\"display:none\">орех</th><th style=\"display:none\">заодин</th></tr></thead></table> ");
                                    out.println("<div class=\"scroll-table-body w3-hoverable w3-white w3-text-brown w3-border w3-bordered w3-centered\"><table ><tbody>");
                                    if(c.getList().size()==0){
                                    out.println("<tr><td class=\"w3-centered w3-border-0\">Корзина пуста</td></tr>");
                                    }
                                    else{
                                    for (Goods g : c.getList()){
                                        if (g.getItem().getArticle() == changeArticle)
                                            out.println("<tr onclick=\"func(this)\" class=\"w3-text-brown\" id=\"selectedRow\" >");
                                        else
                                            out.println("<tr onclick=\"func(this)\" class=\"w3-text-brown\">");
                                        out.println("<td>" + g.getItem().getArticle() + "</td><td>" + g.getItem().getName() + "</td><td>" + g.getQuantity() +"</td><td>" + g.getItem().getMeasure().getText() + "</td><td>" + g.calculate() + "</td><td style=\"display:none\">" + (g.getItem().getType()==Types.tile? ((TiledChocolate) g.getItem()).getFilling():"") + "</td>");
                                        out.println("<td style=\"display:none\">" + g.getItem().getCalories() + "</td><td style=\"display:none\">" + g.getItem().getType().getText() + "</td><td style=\"display:none\">" + (g.getItem().getType()==Types.tile? ((TiledChocolate) g.getItem()).getCacaoPercent():"") + "</td><td style=\"display:none\">" + (g.getItem().getType()==Types.nuts? ((NutsInChocolate) g.getItem()).getChocolateKind().getText():"") + "</td>");
                                        out.println("<td style=\"display:none\">" + (g.getItem().getType()==Types.tile? ((TiledChocolate) g.getItem()).getChocolateKind().getText():"") + "</td><td style=\"display:none\">" + (g.getItem().getType()==Types.nuts? ((NutsInChocolate) g.getItem()).getTypeOfNuts().getText():"") + "</td><td style=\"display:none\">" + g.getItem().getPrice() + "</td></tr>");
                                    }
                                    }
                                    out.println("</tbody></table></div></div>");
                            }
                        %>
                        <table border="0" class="w3-text-brown" >
                           <TR><td id="str" class ="w3-text-brown" disabled>Сумма к оплате: </td><td id="totalValue"><%= c.calculateValue() %></td></TR>
                        </table>

                         <div class="w3-container w3-sand w3-text-white" style="margin-top: 15px">
                         <% if (c.getList().size()!=0) { %>
                            <button type="submit" form="quForm" onclick="return confirm('Оплатить заказ?')" class="w3-btn w3-brown w3-left w3-padding-small w3-round-large" formaction="/cart?action=pay" id="payBtn">Оплатить заказ</button>
                            <button type="submit" form="quForm" onclick="return confirm('Вы уверены, что хотите убрать все товары из корзины?')" class="w3-btn w3-brown w3-padding-small w3-right w3-round-large" formaction="/cart?action=clear" id="clearBtn">Очистить корзину</button>
                            <% } %>
                        </div>
                      </div>

                  <div class="w3-col s5 w3-padding-large">

                    <table class="w3-text-brown w3-sand w3-padding w3-border w3-border-sand">

                    <form method="POST" id="quForm" action="/cart?action=changeQuantity">
                    <input type="text" style="display:none" id="nextArt" name="nextArt" readonly>

                    <% if(cg!=null){ %>
                        <div class="w3-container" style="margin-left:10px;padding:0 0 0 5px">
                        <img src="../images/<%= cg.getItem().getImage() %>" onerror="this.src='../images/no_image.jpg'" class="w3-border w3-left" alt="item" width="40%" height="25%"></div>

                          <TR class="w3-padding"><td>Номер товара: </td>
                                 <td><input type="text" class = "w3-left-align w3-border-0 w3-sand"  id="article" name="article" value="<%= cg.getItem().getArticle() %>" readonly> </td></TR>
                          <TR class="w3-padding" ><td>Тип товара: </td>
                                 <td class="w3-left-align" readonly><%= cg.getItem().getType().getText() %> </td></TR>
                          <TR class="w3-padding-large" style="margin-top: 4px"><td>Название: </td>
                                 <td class="w3-left-align" readonly><%= cg.getItem().getName() %></td></TR>
                          <TR class="w3-padding"><td>Калорийность: </td>
                                  <td class="w3-left-align" readonly><%= cg.getItem().getType().getText() %></td></TR>
                            <TR class="w3-padding"><td>Единица измерения товара: </td>
                                  <td class="w3-left-align" readonly id="tmeas"><%= cg.getItem().getMeasure().getText() %> </td></TR>
                            <% if(cg.getItem().getType()==Types.tile) { %>
                            <TR class="w3-padding"><td>Начинка: </td>
                                  <td class="w3-left-align"  readonly><%= ((TiledChocolate) cg.getItem()).getFilling() %></td></TR>
                            <TR class="w3-padding"><td>Вид шоколада: </td>
                                   <td class="w3-left-align" readonly><%= ((TiledChocolate) cg.getItem()).getChocolateKind().getText() %></td></TR>
                            <TR class="w3-padding"><td>Процент содержания какао (от 20% до 99%): </td>
                                  <td class="w3-left-align" readonly><%= ((TiledChocolate) cg.getItem()).getCacaoPercent() %></td></TR> <% } %>
                          <% if(cg.getItem().getType()==Types.nuts) { %>
                            <TR class="w3-padding"><td>Тип орехов: </td>
                                 <td class="w3-left-align" readonly><%= ((NutsInChocolate) cg.getItem()).getTypeOfNuts().getText() %></td></TR>
                            <TR class="w3-padding"><td>Вид шоколада: </td>
                                 <td class="w3-left-align" readonly><%= ((NutsInChocolate) cg.getItem()).getChocolateKind().getText() %></td></TR> <% } %>
                          <TR>
                         <% if(cg.getItem().getMeasure()==Measures.kilo) { %><td>Цена (за 1 килограмм):</td><% } %>
                         <% if(cg.getItem().getMeasure()==Measures.piece) { %><td>Цена (за 1 штуку):</td><% } %>
                         <% if(cg.getItem().getMeasure()==Measures.gram) { %><td>Цена (за 100 грамм): </td><% } %>
                         <td class="w3-left-align w3-padding-small" id="price"><%= cg.getItem().getPrice() %> </td></TR>
                         <% if(cg.getItem().getMeasure()==Measures.kilo) { %>
                              <td>Количество (в килограммах): </td><td><input type="number" min="0.02" step="0.01" required name="quantity" id="quantity" class="w3-input w3-border w3-round-large" style="width: 50%"
                                       value=<%= cg.getQuantity() %> oninput="quFun()" autofocus onfocus="var temp_value=this.value; this.value=''; this.value=temp_value"></td></TR><% } %>
                          <% if(cg.getItem().getMeasure()==Measures.piece) { %>
                              <td>Количество (в штуках): </td><td><input type="number" min="1" required name="quantity" id="quantity" class="w3-input w3-border w3-round-large" style="width: 50%"
                                       value=<%= cg.getQuantity() %> oninput="quFun()" autofocus onfocus="var temp_value=this.value; this.value=''; this.value=temp_value"></td></TR><% } %>
                          <% if(cg.getItem().getMeasure()==Measures.gram) { %>
                              <td>Количество (в граммах): </td><td><input type="number" min="20"  required name="quantity" id="quantity" class="w3-input w3-border w3-round-large" style="width: 50%"
                                       value=<%= cg.getQuantity() %> oninput="quFun()" autofocus onfocus="var temp_value=this.value; this.value=''; this.value=temp_value"></td></TR><% } %>
                    <% } else { %>
                      <TR class="w3-padding"><td>Номер товара: </td><td>
                         <input type="text" class = "w3-text-brown w3-left w3-border-0 w3-sand" readonly> </td></TR>
                      <TR class="w3-padding" ><td>Тип товара: </td> <td class="w3-left-align" readonly> </td></TR>
                      <TR class="w3-padding-large" style="margin-top: 4px"><td>Название: </td><td class="w3-left-align" readonly> </td></TR>
                      <TR class="w3-padding"><td>Калорийность: </td><td class="w3-left-align" readonly> </td></TR>
                      <TR class="w3-padding"><td>Единица измерения товара: </td><td class="w3-left-align" readonly> </td></TR>
                    <% } %> </form></table>

                         <div class="w3-container w3-sand w3-text-brown" style="margin-top: 10px">
                         <% if(cg!=null) { %>
                             <button type="submit" onclick="return confirm('Убрать товар из корзины?')" form="quForm" class="w3-btn w3-right w3-round-large" formaction="/cart?action=remove" id="removeBtn">Убрать из корзины</button>
                         <% } %>
                        </div>

                  </div>

                </div>

             </div>

             <div class="w3-container w3-text-brown w3-padding">
                     <button type="submit" form="quForm" class="w3-button w3-sand w3-right w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 11pt"  formaction="/cart?action=toStock" >В меню товаров</button>
                     <button type="submit" form="quForm" class="w3-button w3-sand w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 11pt"  formaction="/cart?action=toMainMenu" >В главное меню</button>
             </div>
        </div>
           <div id="snackbar1" class="snackbar success"><h5>Корзина оплачена!</h5> </div>
           <div id="snackbar2" class="snackbar success"><h5>Корзина очищена!</h5></div>
           <div id="snackbar3" class="snackbar success"><h5>Товар <%= request.getAttribute("removed") %> убран из корзины!</h5></div>
           <div id="snackbar4" class="snackbar error"><h5>На складе недостаточно товара <%= request.getAttribute("notEnough") %>!</h5></div>
    </body>

    <% if(cg!=null) { %>
    <script>restValue=<%= c.calculateValue()-cg.getValue() %>;</script><% } %>

    <% if (request.getAttribute("payed") != null) { %>
    <script>
    var x = document.getElementById("snackbar1");
      x.className = "snackbar success show";
      setTimeout(function(){ x.className = x.className.replace("show", ""); }, 2000);
    </script>
    <% } %>

    <% if (request.getAttribute("cleared") != null) { %>
    <script>
    var x = document.getElementById("snackbar2");
      x.className = "snackbar success show";
      setTimeout(function(){ x.className = x.className.replace("show", "");}, 2000);
    </script>
    <% } %>

    <% if (request.getAttribute("removed") != null) { %>
    <script>
    var x = document.getElementById("snackbar3");
      x.className = "snackbar success show";
      setTimeout(function(){ x.className = x.className.replace("show", ""); }, 2000);
    </script>
    <% } %>

    <% if (request.getAttribute("notEnough") != null) { %>
    <script>
    var x = document.getElementById("snackbar4");
      x.className = "snackbar error show";
      setTimeout(function(){ x.className = x.className.replace("show", "");}, 2000);
    </script>
    <% } %>

</html>