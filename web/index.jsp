<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/sql" prefix = "sql"%>
<!DOCTYPE html>

 
<t:layout>
    <jsp:attribute name = "title" >Welcome - Zadanie UPB</jsp:attribute>
    <jsp:body>
        <c:choose>
            <c:when test="${sessionScope.UserName != null}">
                <div class="col">
                    <h1>Your files</h1>
                    <sql:setDataSource var = "FileDB" driver = "com.mysql.jdbc.Driver" url = "jdbc:mysql://url/FileDB" user = "example user"  password = "example passwd"/>
                    <sql:query dataSource = "${FileDB}" var = "result" sql="SELECT * from FILES where OWNER = '${sessionScope.UserName}'"></sql:query>
                <c:choose>
                    <c:when test="${result.rowCount == 0}"><div class="btn-warning disabled rounded">You don't have any files</div></c:when>
                    <c:otherwise>
                        <div class='table-responsive'>
                            <table class="table">
                                <tr>
                                    <th>Filename</th>
                                    <th>Uploader</th>
                                    <th>Download</th>
                                 </tr>
                                 <c:forEach var = "row" items = "${result.rows}">
                                    <tr>
                                       <td><c:out value = "${row.filename}"/></td>
                                       <td class="text-truncate"><c:out value = "${row.uploader}"/></td>
                                       <td><a href="${pageContext.request.contextPath}/download?id=${row.id}" class="btn btn-block rounded btn-dark">Download</a></td>
                                    </tr>
                                 </c:forEach>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
                    <h4>Your private key:</h4>
                    <p class="text-truncate">${sessionScope.PrivateKey}</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="col m-4 p-4 bg-light rounded">
                    <form action="register" method="post">
                        <input type="text" name="userName" class="form-control"/>
                        <input type="password" name="pass" autocomplete="off" class="form-control mt-2"/>
                        <input type="submit" value="register" class="btn w-100 mt-2"/>
                    </form>
                </div>
                <div class="col m-4 p-4 bg-light rounded">
                    <form action="login" method="post">
                        <input type="text" name="userName" class="form-control"/>
                        <input type="password" name="pass" autocomplete="off" class="form-control mt-2"/>
                        <input type="submit" value="login" class="btn w-100 mt-2"/>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>
    </jsp:body>
</t:layout>