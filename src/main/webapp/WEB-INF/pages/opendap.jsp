<!--
 Copyright (c) 2019 OPeNDAP, Inc.
 Please read the full copyright statement in the file LICENSE.

 Authors: 
	James Gallagher	 <jgallagher@opendap.org>
    Samuel Lloyd	 <slloyd@opendap.org>

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 You can contact OPeNDAP, Inc. at PO Box 112, Saunderstown, RI. 02874-0112.
 -->

<!--
 11/15/18 - SBL - Initial Code 
 12/19/18 - SBL - Modified
 -->
 
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<!-- <link type="text/css" href="<c:url value="/Style.css"/> "/> -->
<style type="text/css">
table {
	border-collapse: collapse;
	margin: 5px;"
}

table, th, td {
	border: 1px solid black;
}

th, td {
	padding: 5px;
	text-align: left;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OPENDAP Collector Home</title>
</head>
<body>
	<div id="Header" >
		<h1>${message}</h1>
	</div>
	<div id="List" style="float:left;">
		<table>
			<tr>
				<th>Reporters</th>
				<th>Accessible</th>
				<th>Failed Accesses</th>
				<th>Currently Active</th>
				<th>Number Pulled</th>
				<th>Last Successful Pull</th>
			</tr>
			<c:forEach items="${items}" var="listItem">
			
				<tr>
					<td>
						<!-- <a href="/harvester/logLines?hyraxInstanceName=${listItem}"> -->
						<!-- <a href="/healthcheck/server?hyraxInstanceName=${listItem}"> -->
						<a href="./server?hyraxInstanceName=${listItem[0]}"> 
							<c:out value="${listItem[0]}"></c:out>
						</a>
						</td>
					<td>${listItem[1]}</td>
					<td>${listItem[2]}</td>
					<td>${listItem[3]}</td>
					<td>${listItem[4]}</td>
					<td>${listItem[5]}</td>
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