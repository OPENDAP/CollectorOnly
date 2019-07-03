<!--11/15/18 - SBL - Initial Code 
	12/19/18 - SBL - Modified -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OPENDAP Collector Home</title>
</head>
<body>
	<div id="Header" >
		<h1>${message}</h1>
	</div>
	<div id="List" style="float:left;">
		<table>
			<c:forEach items="${items}" var="listItem">
				<tr>
					<td>
						<!-- <a href="/harvester/logLines?hyraxInstanceName=${listItem}"> -->
						<!-- <a href="/healthcheck/server?hyraxInstanceName=${listItem}"> -->
						<a href="./server?hyraxInstanceName=${listItem}">
							<c:out value="${listItem}"></c:out>
						</a>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div id="Buttons">
		<div id="AddServerBtn">
			Add New Sever:
			<form:form method="POST" action="./NewServerRedirect">
				<input type="submit" value="Add Server"/>
			</form:form>
		</div>
		<div id="StatisticsBtn">
		</div>
	</div>
</body>
</html>