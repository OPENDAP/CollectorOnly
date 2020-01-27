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

<!-- 2/12/19 - SBL - Initial Code -->

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<!-- <link rel="stylesheet" type="text/css" href="Style.css"> -->
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
<title>Server Hosts</title>
</head>
<body>
	<div>
		<h3>Server: ${serverName}</h3><br/>
		<h3>${month}</h3>
		
	</div>
	<div>
		<table >
			<tr>
				<th>User Agents</th>
				<th>Number of Accesses</th>
				<th>Total Data Size</th>
			</tr>
			<tr>
				<th>${totals[0]}</th>
				<th>${totals[1]}</th>
				<th>${totals[2]}</th>
			</tr>
			<c:forEach items="${hostItems}" var="items">
				<tr>
					<td>
						<a href="./hostDetails?hyraxInstanceName=${serverName}&month=${month}&host=${items[2]}">
							${items[0]}
						</a>
					</td>
					<td>${items[1]}</td>
					<td>${items[3]}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>
