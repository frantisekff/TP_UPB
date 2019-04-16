<%-- 
    Document   : files
    Created on : 17.11.2018, 12:27:15
    Author     : Rikpat
--%>

<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@ page contentType = "text/html" pageEncoding = "UTF-8" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/sql" prefix = "sql" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>

 <%
  String REGEX = "[a-zA-Z0-9.]{1,15}";
  String searchTerm = request.getParameter("query");
  Pattern p = Pattern.compile(REGEX);
  Matcher m = p.matcher(searchTerm);   // get a matcher object
  
  if(!m.matches()){
            request.getSession().setAttribute("errorMessage", "User name must contain only letters a-z, A-Z and numbers 0-9");
            response.sendRedirect(request.getContextPath());
            searchTerm = "Nespravny retazec";
            return;
        }
  
%>


<t:layout>
    <jsp:attribute name = "title" >Search - Zadanie UPB</jsp:attribute>
    <jsp:body>
        
        <sql:setDataSource var = "FileDB" driver = "com.mysql.jdbc.Driver" url = "jdbc:mysql://url/FileDB" user = "example user"  password = "example passwd"/>
        <sql:query dataSource = "${FileDB}" var = "result" sql="SELECT F.*, COUNT(C.COMMENT_ID) NUMCOMMENTS from FILES F LEFT JOIN COMMENTS C ON F.ID = C.FILE_ID WHERE F.FILENAME LIKE ? OR C.TEXT LIKE ? GROUP BY F.ID">
            <sql:param value = "${param.query}" />
            <sql:param value = "${param.query}" />
        </sql:query>
        <div id="files" class="col">
            <div class="row my-4 mx-2">
                <div class="col">
                    Filename
                </div>
                <div class="col">
                    Uploader
                </div>
                <div class="col">
                    Owner
                </div>
                <div class="col">
                    Comments
                </div>
                <div class="col"></div>
            </div>
        <c:forEach var = "row" items = "${result.rows}">
            <div class="card my-2">
                <div class="row p-2 m-0 align-items-center card-header">
                    <div class="col">
                        <c:out value = "${row.filename}"/>
                    </div>
                    <div class="col">
                        <c:out value = "${row.uploader}"/>
                    </div>
                    <div class="col">
                        <c:out value = "${row.owner}"/>
                    </div>
                    <div class="col">
                        <a data-toggle="collapse" href="#collapse-${row.id}" role="button" aria-expanded="false" aria-controls="collapse-${row.id}"><c:out value = "${row.numComments}"/> comments</a>
                    </div>
                    <div class="col">
                        <td><a href="${pageContext.request.contextPath}/download?id=${row.id}" class="btn btn-block rounded btn-dark">Download</a></td>
                    </div>
                </div>
                <div id="collapse-${row.id}" class="collapse" data-parent="#files">
                    <div class="card-body">
                <c:if test="${row.numComments > 0}">
                    <sql:query dataSource = "${FileDB}" var = "comments" sql="SELECT COMMENTER, TEXT from COMMENTS WHERE FILE_ID = ${row.id}"></sql:query>
                        <div class="row align-items-centerm-1 p-2 m-3">
                            <div class="col-2">
                                Commenter
                            </div>
                            <div class="col">
                                Comment
                            </div>
                        </div>
                    <c:forEach var = "comment" items = "${comments.rows}">
                        <div class="row align-items-center bg-light border p-2 m-3">
                            <div class="col-2">
                                <c:out value = "${comment.commenter}"/>
                            </div>
                            <div class="col text-left">
                                <c:out value = "${comment.text}"/>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
                        <form action="${pageContext.request.contextPath}/NewComment" method="post">
                            <div class="row align-items-center">
                                <input type="hidden" name="fileID" value="${row.id}"/>
                                <div class="col-2">
                                    New Comment:
                                </div>
                                <div class="col">
                                    <input type="text" name="comment" class="form-control w-100"/>
                                </div>
                                <div class="col-2">
                                    <input type="submit" class="form-control w-100"/>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>
            </table>
        </div>
    </jsp:body>
</t:layout>