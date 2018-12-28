<!-- 12/19/18 - SBL - Initial Code
	 12/26/18 - SBL - Modified -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>OPENDAP New Server</title>
</head>
<body>
	you have reached the Opendap new server page.<br>
	we are currently down for maintenance,<br>
	please try again later.<br>
	<div>
		<form:form method="POST" action="/AddNewServer">
			<div style="float:left;">
				*Server URL : <br>
				Reporter URL : <br>
				*Ping : <br>
				*Log : 
			</div>
			<div>
				<input id="serverUrl" name="serverUrl" type="text" /><br>
				<input id="reporterUrl" name="reporterUrl" type="text"/><br>
				<input id="ping" name="ping" type="text"/><br>
				<input id="log" name="log" type="text"/>
			</div>
			<input type="button" onclick="this.form.reset();" value="Clear"/> 
			<input type="submit" value="Submit"/>
		</form:form>
	</div>
</body>
</html>