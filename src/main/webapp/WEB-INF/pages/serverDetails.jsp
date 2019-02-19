<!-- 2/12/19 - SBL - Initial Code -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Server Details</title>
</head>
<body>
	<h1>Server Details</h1>
	<div id="Details">
		<table>
			<tr><td><h4>Server URL: ${serverUrl}</h4></td></tr>
			<tr><td><h4>Reporter URL: ${reporterUrl}</h4></td></tr>
			<tr><td><h4>Ping Interval: ${ping}</h4></td></tr>
			<tr><td><h4>Number of Logs to Pull: ${log}</h4></td></tr>
			<tr><td><h4>Software Version: ${version}</h4></td></tr>
			<tr><td><h4>Date Registered: ${registrationTime}</h4></td></tr>
			<tr><td><h4>Last Accessed: ${lastAccessTime}</h4></td></tr>
			<tr><td><h4>Currently Active: ${active}</h4></td></tr>	
		</table>
		<a href="/harvester/logLines?hyraxInstanceName=${name}">
			Server Access Logs
		</a>
	</div>
</body>
</html>