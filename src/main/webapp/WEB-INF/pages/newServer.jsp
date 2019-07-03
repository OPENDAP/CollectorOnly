<!-- 12/19/18 - SBL - Initial Code
	 12/26/18 - SBL - Modified -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OPENDAP New Server</title>
</head>
<body>
	<!-- you have reached the Opendap new server page.<br>
	we are currently down for maintenance,<br>
	please try again later.<br> -->
	<div>
		<form:form method="POST" action="./AddNewServer">
			<table>
				<tr>
					<td>*Server URL : </td>
					<td><input id="serverUrl" name="serverUrl" type="text"/></td>
				</tr>
				<tr>
					<td>Reporter URL : </td>
					<td><input id="reporterUrl" name="reporterUrl" type="text"/></td>
				</tr>
				<tr>
					<td>*Ping : </td>
					<td><input id="ping" name="ping" type="text"/></td>
				</tr>
				<tr>
					<td>*Log : </td>
					<td><input id="log" name="log" type="text"/></td>
				</tr>
				<tr>
					<td><input type="button" onclick="this.form.reset();" value="Clear"/></td>
					<td><input type="submit" value="Submit"/></td>
				</tr>
			</table>
		</form:form>
	</div>
</body>
</html>