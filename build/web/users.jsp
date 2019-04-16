<%-- 
    Document   : users
    Created on : 17.11.2018, 12:26:59
    Author     : Rikpat
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/sql" prefix = "sql"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<t:layout>
    <jsp:attribute name = "title" >Users - Zadanie UPB</jsp:attribute>
    <jsp:body>
        <sql:setDataSource var = "FileDB" driver = "com.mysql.jdbc.Driver" url = "jdbc:mysql://url/FileDB" user = "example user"  password = "example passwd"/>
        <sql:query dataSource = "${FileDB}" var = "result" sql="SELECT * from USERS;"></sql:query>
        <div class='table-responsive'>
            <table class="table">
               <tr>
                  <th>Username</th>
                  <th>Send File</th>
               </tr>

               <c:forEach var = "row" items = "${result.rows}">
                  <tr>
                     <td><c:out value = "${row.name}"/></td>
                     <td>
                         <form enctype="multipart/form-data" action="${pageContext.request.contextPath}/upload" method="post">
                             <input type="hidden" name="userName" value="${row.name}"/>
                             <input type="hidden" name="publicKey" value="${row.publickey}"/>
                             <input type="file" onchange="this.form.submit()" name="file" class="form-control-file"/>
                         </form>
                     </td>
                  </tr>
               </c:forEach>
            </table>
        </div>
    </jsp:body>
</t:layout>