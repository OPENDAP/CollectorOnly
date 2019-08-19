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
			<tr>
				<td><h3>ID: </h3></td>
				<td><h4>${serverId}</h4></td>
			</tr>
			<tr>
				<td><h3>Server URL: </h3></td>
				<td><h4>${serverUrl}</h4></td>
			</tr>
			<tr>
				<td><h3>Reporter URL: </h3></td>
				<td><h4>${reporterUrl}</h4></td>
			</tr>
			<tr>
				<td><h3>Ping Interval: </h3></td>
				<td><h4>${ping}</h4></td>
			</tr>
			<tr>
				<td><h3># of Logs to Pull: </h3></td>
				<td><h4>${log}</h4></td>
			</tr>
			<tr>
				<td><h3># of Logs Pulled to Date: </h3></td>
				<td><h4>${number}</h4></td>
			</tr>
			<tr>
				<td><h3>Software Version: </h3></td>
				<td><h4>${version}</h4></td>
			</tr>
			<tr>
				<td><h3>Date Registered: </h3></td>
				<td><h4>${registrationTime}</h4></td>
			</tr>
			<tr>
				<td><h3>Last Accessed: </h3></td>
				<td><h4>${lastAccessTime}</h4></td>
			</tr>
			<tr>
				<td><h3>Currently Active: </h3></td>
				<td><h4>${active}</h4></td>
			</tr>	
		</table>
		Server Access Logs
		<a href="./harvester/logLines?hyraxInstanceName=${name}">
			(Last 500)
		</a>
		<a href="./harvester/logLines?hyraxInstanceName=${name}&bonus=true">
			(All)
		</a><br/>
		<a href="./repull?hyraxInstanceName=${name}">
			Clear and Re-Pull Server Logs
		</a><br/>
		<a href="./remove?hyraxInstanceName=${name}">
			Remove Server
		</a>
	</div>
</body>
</html>