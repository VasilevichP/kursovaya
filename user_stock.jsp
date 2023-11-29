<%@page import="java.util.List" %>
<%@page import="org.example.app.entities.Goods" %>
<%@page import="org.example.app.model.Stock" %>
<%@page import="org.example.app.entities.Chocolate" %>
<%@page import="org.example.app.entities.Types" %>
<%@page import="org.example.app.entities.Measures" %>
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
                document.getElementById("tarticle").innerText=tds[0].innerText;
                document.getElementById("tarticle").value=tds[0].innerText;
                document.getElementById("detForm").submit();
           }
        </script>
        <script>
           function searchFun(){
              document.getElementById("searchForm").submit();
           }
        </script>
        <style>
            body {
              background-image: url("../images/choco4.jpg");
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
            height: 350px;
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


    </head>

    <body class="w3-sand">

        <div class="w3-container w3-padding" style="margin-top:40px"></div>
        <div class="w3-container w3-sand w3-round-large w3-padding" style="margin-top:20px; width:96%; margin:auto;">
        <div class="w3-container w3-center w3-padding-small">
            <div class="w3-container w3-text-brown  w3-center">
                <h2>Товары</h2>
            </div>

            <div class="w3-container w3-padding-small">

                <table border="0" class="w3-text-brown" style="width:100%">
                   <TR>
                   <form method="POST" action="/stock?action=filter">
                   <td class="w3-left-align" style="width:15%">
                     <select class="w3-select " name="typeBox" id="typeBox" required>
                       <option value="" disabled selected>Тип шоколада:</option>
                       <% if(request.getSession().getAttribute("noType")!=null){
                         out.println("<option value=\"Без фильтра\" selected>Без фильтра</option>");}
                         else{
                         out.println("<option value=\"Без фильтра\" >Без фильтра</option>");
                       }
                       Types type = Types.getInstance((String)request.getSession().getAttribute("type"));
                       for (Types t: Types.values()){
                       if (t==type)
                       out.println("<option value=\"" + t.getText() + "\" selected>" + t.getText() + "</option>");
                       else
                       out.println("<option value=\"" + t.getText() + "\">" + t.getText() + "</option>");
                       }
                       %>
                     </select>
                   </td></form>
                   <form method="POST" action="/stock?action=sort">
                      <td class="w3-left-align" style="width:15%">
                        <select class="w3-select " name="sortPrice" id="sortPrice" required>
                          <option value="" disabled selected>Цена:</option>
                          <%
                            if(request.getSession().getAttribute("asc")!=null){
                            out.println("<option value=\"По возрастанию\" selected>По возрастанию</option>");}
                            else{
                            out.println("<option value=\"По возрастанию\" >По возрастанию</option>");
                          }
                            if(request.getSession().getAttribute("desc")!=null){
                            out.println("<option value=\"По убыванию\" selected>По убыванию</option>");}
                            else{
                            out.println("<option value=\"По убыванию\" >По убыванию</option>");
                          }
                          %>
                        </select>
                      </td></form>
                   <td>
                        <form method="POST" id="searchForm" action="/stock?action=search">
                         <input type="text" name="searchText" id="searchText" oninput="searchFun()" value='<%= (request.getSession().getAttribute("search")!=null)?request.getSession().getAttribute("search"):"" %>'
                                    style="width:50%" class="w3-input w3-border w3-round-large w3-left" placeholder="Введите название товара" autofocus onfocus="var temp_value=this.value; this.value=''; this.value=temp_value">
                       </form>
                   </td>
                   </TR>
                </table>

            </div>

            <%
                List<Goods> stock = (List<Goods>) request.getAttribute("stock");
                if (stock == null) out.println("GoodsList is missing");
                else{
                out.println("<div class=\"scroll-table\"><table class=\"w3-hoverable w3-brown w3-text-white w3-border w3-bordered w3-centered\"><thead><tr><th>Артикул</th><th>Тип товара</th><th>Название товара</th><th>Цена</th></tr></thead></table> ");
                out.println("<div placeholder=\"Нет товаров, удовлетворяющих условиям поиска\" class=\"scroll-table-body w3-hoverable w3-white w3-text-brown w3-border w3-bordered w3-centered\"><table ><tbody>");
                if(stock.size()==0){
                out.println("<tr><td class=\"w3-centered w3-border-0\">Товар не найден</td></tr>");
                }
                else{
                for (Goods g : stock){
                    if(g.getQuantity()>0){
                    out.println("<tr onclick=\"func(this)\" class=\"w3-text-brown\"><td>" + g.getItem().getArticle() + "</td><td>" + g.getItem().getType().getText() + "</td><td>" + g.getItem().getName() + "</td><td>" + g.getItem().getPrice() +(g.getItem().getMeasure()==Measures.gram? " р/100":" р/")+ g.getItem().getMeasure().getText() +"</td></tr>");
                }
                }
                }
                out.println("</tbody></table></div></div>");
            }
            %>

        <form method="POST" id="detForm" action="/stock?action=change" style="margin-top: 20px">
               <input type="text" id="tarticle" name="tarticle" readonly style="display:none">
        </form>
        </div>

        <div class="w3-container w3-text-brown  w3-padding">
            <form method="POST">
                <button type="submit" class="w3-button w3-sand w3-right w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 12pt"  formaction="/stock?action=toCart" >Корзина</button>
                <button type="submit" class="w3-button w3-sand  w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large" style="font-size: 12pt"  formaction="/stock?action=signout" >В главное меню</button>
            </form>
        </div>
        </div>

    </body>

    <script>
    document.getElementById("typeBox").addEventListener("change", ({ target }) => target.form.submit());
    document.getElementById("sortPrice").addEventListener("change", ({ target }) => target.form.submit());
    </script>

</html>