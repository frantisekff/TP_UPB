<%@ tag description="Overall Page template" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="title" %>

<%-- Redirect if not logged in --%>
<c:if test="${sessionScope.UserName == null && fn:replace(pageContext.request.requestURI,pageContext.request.contextPath,'') != '/'}">
    <c:redirect url="/" />
</c:if>

<html>
    <head>
        <title>${(empty title) ? 'Zadanie UPB' : title}</title>
        <link rel="stylesheet" href="resources/bootstrap.css"/>
        <script src="resources/jquery-3.3.1.js"></script>
        <script src="resources/bootstrap.bundle.js"></script>
    </head>
    <body>
        
        <c:set var="relativeURL" value=""></c:set>
        <div class="container text-center">
            <nav class="navbar navbar-expand-lg navbar-light bg-light">
                <a class="navbar-brand" href="#">UPB Zadanie 4</a>
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav mr-auto">
                        <li class="nav-item active">
                            <a class="nav-link" href="${pageContext.request.contextPath}">Home <span class="sr-only">(current)</span></a>
                        </li>
                <c:choose>
                    <c:when test="${sessionScope.UserName != null}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/users">Users</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/files">Files</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item">
                            <a class="nav-link disabled" href="#">Users</a>
                        </li>
                    </c:otherwise>
                </c:choose>
                    </ul>
                <c:if test="${sessionScope.UserName != null}">
                    
                    <form class="form-inline my-2 my-lg-0" action="${pageContext.request.contextPath}/search" method="GET">
                        <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" name="query">
                        <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
                    </form>
                    <a class="my-2 my-lg-0 btn text-secondary" href="${pageContext.request.contextPath}/logout">
                        Logout
                    </a>
                </c:if>
                </div>
            </nav>
        <c:if test="${sessionScope.errorMessage != null}">
            <div class="btn btn-danger disabled rounded w-100">${sessionScope.errorMessage}</div>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        <c:if test="${sessionScope.message != null}">
            <div class="btn btn-success disabled rounded w-100">${sessionScope.message}</div>
            <c:remove var="message" scope="session" />
        </c:if>
            <div id="body" class="row position-relative">
                <jsp:doBody/>
            </div>
        </div>
    </body>
</html>
