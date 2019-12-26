<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
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
<meta charset="UTF-8">
<title>Host Details</title>
</head>
<body>
	<div>
		<h3>Server: ${serverName}</h3>
		<h3>${month}</h3><br/>
		<h3>${hostname}</h3>
	</div>
	<div>
		<table>
			<tr>
				<th>Resource Id</th>
				<th>Query</th>
				<th>Size</th>
				<th>Duration</th>
			</tr>
			<c:forEach items="${tableItems}" var="items">
				<tr>
					<td>${items[0]}</td>
					<td>${items[1]}</td>
					<td>${items[2]}</td>
					<td>${items[3]}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>