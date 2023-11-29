<%@page import="java.util.List" %>
<%@page import="org.example.app.entities.Types" %>
<%@page import="org.example.app.entities.TiledChocolate" %>
<%@page import="org.example.app.entities.NutsInChocolate" %>
<%@page import="org.example.app.entities.Nuts" %>
<%@page import="org.example.app.entities.Measures" %>
<%@page import="org.example.app.entities.ChocolateKinds" %>
<%@page import="org.example.app.entities.Goods" %>
<%@page import="org.example.app.model.Stock" %>
<%@page import="org.example.app.entities.Chocolate" %>
<%@page import="org.example.app.servlets.AddChocoServlet" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Chocolate_shop </title>
        <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
        <% Chocolate choco = (Chocolate) request.getAttribute(AddChocoServlet.JSP_ATTRIBUTE_CHOCO); %>
        <% Goods item = (Goods) request.getAttribute(AddChocoServlet.JSP_ATTRIBUTE_ITEM); %>
        <style>
           body {
             background-image: url("../images/choco12.jpg");
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
    #snackbar {
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

    #snackbar.show {
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
    </head>

    <body class="w3-sand">

        <div class="w3-container w3-padding" style="margin-top:25px"></div>
        <div class="w3-container w3-sand w3-padding w3-round-large"  style="margin-top:25px; width:90%; margin:auto;">
        <div class="w3-container w3-center w3-padding">
           <div class="w3-container w3-center w3-text-brown ">
               <h2> Создание единицы товара </h2>
           </div>
           <form id="chocoForm" method="POST" action="/choco?action=edit" class="w3-sand w3-padding" enctype="multipart/form-data">
            <table class="w3-text-brown w3-sand w3-padding-small w3-left w3-border w3-border-sand w3-margin" style="width:80%;margin-bottom:0px">
                <TR><td style="width:50%">Тип товара: </td><td class="w3-left-align">
                  <select class="w3-select w3-white " name="<%= AddChocoServlet.JSP_PARAMETER_TYPE %>" id="<%= AddChocoServlet.JSP_PARAMETER_TYPE %>" required>
                    <option value="" disabled selected></option>
                    <% Types type = (choco != null)?choco.getType():null;
                    for (Types t: Types.values()){
                    if (t==type)
                    out.println("<option value=\"" + t.getText() + "\" selected>" + t.getText() + "</option>");
                    else
                    out.println("<option value=\"" + t.getText() + "\">" + t.getText() + "</option>");
                    }
                    %>
                  </select>
                </td></TR>

              <TR><td>Название: </td><td><input type="text" name="<%= AddChocoServlet.JSP_PARAMETER_NAME %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 100%"
                        value='<%= (choco!=null) ? String.valueOf(choco.getName()) : "" %>' required></td></TR>

              <TR><td>Калорийность (от 400 до 700): </td><td><input type="number" min="400" max="700" required name="<%= AddChocoServlet.JSP_PARAMETER_CALORIES %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                                      value="<%= (choco!=null) ? String.valueOf(choco.getCalories()) : "" %>"></td></TR>

              <% if ((choco.getType()==Types.sweets)|| (choco.getType()==Types.cookies) || (choco.getType()==Types.paste)) { %>
              <TR><td>Единица измерения товара: </td><td>
                        <select class="w3-select " name="<%= AddChocoServlet.JSP_PARAMETER_MEASURE %>" id="<%= AddChocoServlet.JSP_PARAMETER_MEASURE %>" required>
                         <option value="" disabled selected></option>
                         <% Measures measure = (choco != null)?choco.getMeasure():null;
                         if (choco.getType()!=Types.paste){
                            if (measure==Measures.piece){
                              out.println("<option value=\"шт.\" selected>шт.</option>");}
                              else{
                              out.println("<option value=\"шт.\" >шт.</option>");
                            }
                         }
                         if (measure==Measures.gram){
                           out.println("<option value=\"г\" selected>г</option>");}
                           else{
                           out.println("<option value=\"г\">г</option>");
                         }
                         if (measure==Measures.kilo){
                            out.println("<option value=\"кг\" selected>кг</option>");}
                            else{
                            out.println("<option value=\"кг\">кг</option>");
                          }
                         %>
                       </select>
                </td></TR><% } %>

            <% if ((choco != null) && (choco.getType()==Types.nuts)) { %>
             <TR><td>Вид шоколада: </td><td>
                    <select class="w3-select w3-round-large w3-border " name="<%= AddChocoServlet.JSP_PARAMETER_KIND %>" id="<%= AddChocoServlet.JSP_PARAMETER_KIND %>" required>
                     <option value="" disabled selected></option>
                     <% ChocolateKinds chkind = (choco != null)?((NutsInChocolate) choco).getChocolateKind():null;
                     for (ChocolateKinds t: ChocolateKinds.values()){
                     if (t==chkind)
                     out.println("<option value=\"" + t.getText() + "\" selected>" + t.getText() + "</option>");
                     else
                     out.println("<option value=\"" + t.getText() + "\">" + t.getText() + "</option>");
                     }
                     %>
                   </select>
            </td></TR> <% } %>

            <% if((choco!=null)&&(choco.getType()==Types.tile)) {
               out.println("<TR><td>Вид шоколада: </td><td>");
               out.println("<select class=\"w3-select\" name=\"" + AddChocoServlet.JSP_PARAMETER_KIND + "\" id=\"" + AddChocoServlet.JSP_PARAMETER_KIND + "\" required>");
               out.println("<option value=\"\" disabled selected></option>");
                ChocolateKinds chokind = (choco != null)?((TiledChocolate) choco).getChocolateKind():null;
                for (ChocolateKinds t: ChocolateKinds.values()){
                if (t==chokind){
                out.println("<option value=\"" + t.getText() + "\" selected>" + t.getText() + "</option>");}
                else{
                out.println("<option value=\"" + t.getText() + "\">" + t.getText() + "</option>");}
                } %>
                </select></td></TR>
            <% } %>

            <% if ((choco != null) && (choco.getType()==Types.tile)) { %>
                <TR><td>Начинка: </td>
                <td><input type="text" name="<%= AddChocoServlet.JSP_PARAMETER_FILLING %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                     value="<%= (choco!=null) ? ((TiledChocolate) choco).getFilling() : "" %>" >
            </td></TR> <% } %>

            <% if ((choco != null) && (choco.getType()==Types.tile)) { %>
                <TR><td>Процент содержания какао (от 20% до 99%): </td>
                <td><input type="number" min="20" max="99" required name="<%= AddChocoServlet.JSP_PARAMETER_PERCENT %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                          value="<%= (choco!=null) ? String.valueOf( ((TiledChocolate) choco).getCacaoPercent()) : "" %>">
            </td></TR> <% } %>

            <% if ((choco != null) && (choco.getType()==Types.nuts)) { %>
             <TR><td>Тип орехов: </td><td>
                    <select class="w3-select " name="<%= AddChocoServlet.JSP_PARAMETER_NUTS %>" id="<%= AddChocoServlet.JSP_PARAMETER_NUTS %>" required>
                     <option value="" disabled selected></option>
                     <% Nuts nut = (choco != null)?((NutsInChocolate) choco).getTypeOfNuts():null;
                     for (Nuts t: Nuts.values()){
                     if (t==nut)
                     out.println("<option value=\"" + t.getText() + "\" selected>" + t.getText() + "</option>");
                     else
                     out.println("<option value=\"" + t.getText() + "\">" + t.getText() + "</option>");
                     }
                     %>
                   </select>
            </td></TR> <% } %>
            <% if(choco.getMeasure()==Measures.kilo) { %>
                                <tr><td>Цена (за 1 килограмм): <td><input type="number" min="0.01" step="0.01" required name="<%= AddChocoServlet.JSP_PARAMETER_PRICE %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                                   value=<%= ((String.valueOf(request.getAttribute("price"))!="")&&(request.getAttribute("price"))!=null) ? String.valueOf(request.getAttribute("price")) : "0.01" %>></td></TR><% } %>
            <% if(choco.getMeasure()==Measures.piece) { %>
                                <tr><td>Цена (за 1 штуку): <td><input type="number" min="0.01" step="0.01"  required name="<%= AddChocoServlet.JSP_PARAMETER_PRICE %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                                   value=<%= ((String.valueOf(request.getAttribute("price"))!="")&&(request.getAttribute("price"))!=null) ? String.valueOf(request.getAttribute("price")) : "0.01" %>></td></TR><% } %>
            <% if(choco.getMeasure()==Measures.gram) { %>
                                <tr><td>Цена (за 100 грамм): <td><input type="number" min="0.01" step="0.01"  required name="<%= AddChocoServlet.JSP_PARAMETER_PRICE %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                                   value=<%= ((String.valueOf(request.getAttribute("price"))!="")&&(request.getAttribute("price"))!=null) ? String.valueOf(request.getAttribute("price")) : "0.01" %>></td></TR><% } %>

            <% if((choco==null)) { %>
                    <tr><td>Количество: </td><td><input type="number" min="0" required name="<%= AddChocoServlet.JSP_PARAMETER_QUANTITY %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                                                value="<%= (choco!=null) ? String.valueOf(item.getQuantity()) : "" %>"></td></TR><% } %>

                <% if(((choco!=null)&& choco.getMeasure()==Measures.kilo) || (choco.getType()==Types.nuts)) { %>
                    <tr><td>Количество (в килограммах): </td><td><input type="number" min="0.00" step="0.01" required name="<%= AddChocoServlet.JSP_PARAMETER_QUANTITY %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                                                value=<%= ((String.valueOf(request.getAttribute("quantity"))!="")&&(request.getAttribute("quantity"))!=null) ? String.valueOf(request.getAttribute("quantity")) : "0.00" %>></td></TR><% } %>

               <% if(((choco!=null)&& choco.getMeasure()==Measures.piece) || (choco.getType()==Types.bars)|| (choco.getType()==Types.tile)) { %>
                   <tr><td>Количество (в штуках): </td><td><input type="number" min="0" step="1" required name="<%= AddChocoServlet.JSP_PARAMETER_QUANTITY %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                                               value=<%= ((String.valueOf(request.getAttribute("quantity"))!="")&&(request.getAttribute("quantity"))!=null) ? String.valueOf(request.getAttribute("quantity")) : "0" %>></td></TR><% } %>

                <% if(((choco!=null)&& choco.getMeasure()==Measures.gram)|| (choco.getType()==Types.powder)) { %>
                   <tr><td>Количество (в граммах): </td><td><input type="number" min="0"  required name="<%= AddChocoServlet.JSP_PARAMETER_QUANTITY %>" class="w3-input w3-animate-input w3-border w3-round-large" style="width: 30%"
                                               value=<%= ((String.valueOf(request.getAttribute("quantity"))!="")&&(request.getAttribute("quantity"))!=null) ? String.valueOf(request.getAttribute("quantity")) : "0" %>></td></TR><% } %>
               </table>

                <% if ((choco != null)&&(choco.getType()!=null)) { %>
                   <table class="w3-text-brown w3-sand w3-padding-small w3-left w3-border w3-border-sand w3-margin" style="margin-top:0px"><TR>
                       <td style="width:20%">Изображение (ссылка или путь к файлу): </td>
                       <td>
                       <input type="text" name="<%= AddChocoServlet.JSP_PARAMETER_URL %>" id="<%= AddChocoServlet.JSP_PARAMETER_URL %>" style="width:59%" class="w3-input w3-border w3-right w3-round-large "
                              value=<%= (request.getAttribute(AddChocoServlet.JSP_ATTRIBUTE_IMAGE)!=null)?(String) request.getAttribute(AddChocoServlet.JSP_ATTRIBUTE_IMAGE):"" %>></td>
                       <td style="width:20%;"><input type="file" name="<%= AddChocoServlet.JSP_PARAMETER_FILE %>" id="<%= AddChocoServlet.JSP_PARAMETER_FILE %>" class="w3-right w3-text-sand"
                              onchange="document.getElementById('<%= AddChocoServlet.JSP_PARAMETER_URL %>').value=document.getElementById('<%= AddChocoServlet.JSP_PARAMETER_FILE %>').value;" >
                       </td></tr></table><% } %>
               </form>



          <div class="w3-container w3-padding-small w3-padding">
                   <button type="submit" class="w3-btn w3-text-white w3-brown w3-round-large w3-left" form="chocoForm"
                                         style="margin-left:20px" formaction="/choco?action=add" >Добавить в ассортимент</button>
            </div>


        </div>

        <div class="w3-container w3-sand w3-text-brown w3-right-align w3-padding">
           <form method="POST" id="cancelForm" action="/choco?action=cancel"  enctype="multipart/form-data"> <button class="w3-button w3-sand w3-text-brown w3-hover-text-grey w3-hover-sand w3-padding-small w3-round-large"
                                        style="font-size: 11pt;margin-right:20px"  formaction="/choco?action=cancel">Вернуться</button> </form>
        </div>
        </div>
        <div id="snackbar">Был добавлен товар <%= request.getAttribute("isAdded") %>!</div>


</body>

<script>
document.getElementById("typeBox").addEventListener("change", ({ target }) => target.form.submit());
document.getElementById("measureBox").addEventListener("change", ({ target }) => target.form.submit());
</script>

<% if (request.getAttribute("isAdded") != null) { %>
<script>
var x = document.getElementById("snackbar");
  x.className = "show";
  setTimeout(function(){ x.className = x.className.replace("show", "");document.getElementById("cancelForm").submit(); }, 1500);
</script>
<% } %>
</html>
