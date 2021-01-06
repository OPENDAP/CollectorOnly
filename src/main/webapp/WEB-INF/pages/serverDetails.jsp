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
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
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
<title>Server Details</title>
</head>
<body>
	<div id="Header" style="clear:right;">
		<h1 style="float:left;">Server Details</h1>
		<div style="float:right; text-align:right;">
				Collector Version : ${version} <br/>
				Page Generated : ${time}
		</div>
	</div>
	<div id="Details" style="float:left; width:80%; height:95%; clear:left;">
		<div style="clear:right;">
			<table id="ServerInfo" style="float:left">
				<tr>
					<th colspan="2">Server Information</th>
				</tr>
				<tr>
					<th>URL: </th>
					<td>${serverUrl}</td>
				</tr>
				<tr>
					<th>Version: </th>
					<td>${serverVer}</td>
				</tr>
				<tr>
					<th>Running: </th>
					<td>${serverRun}</td>
				</tr>
				<tr>
					<th>Last Access Time: </th>
					<td>${serverLAT}</td>
				</tr>
				<tr>
					<th>Last Error Time: </th>
					<td>${serverLET}</td>
				</tr>	
			</table>
			<table id="ServerHist" style="float:left">
				<tr>
					<th colspan="3">Last 5 Server Outages</th>
				</tr>
				<tr>
					<th>Start DateTime</th>
					<th>End DateTime</th>
					<th>Outage Interval</th>
				</tr>
				<c:forEach items="${serverHistory}" var="server">
				<tr>
					<td>${server[0]}</td>
					<td>${server[1]}</td>
					<td>${server[2]}</td>
				</tr>
				</c:forEach>
			</table>
		</div>
		<div style="clear:left;">
			<table id="ReporterInfo" style="float:left">
				<tr>
					<th colspan="2">Reporter Information</th>
				</tr>
				<tr>
					<th>ID:</th>
					<td>${serverId}</td>
				</tr>
				<tr>
					<th>URL: </th>
					<td>${reporterUrl}</td>
				</tr>
				<tr>
					<th>Version: </th>
					<td>${reporterVer}</td>
				</tr>
				<tr>
					<th>Running: </th>
					<td>${reporterRun}</td>
				</tr>
				<tr>
					<th>Ping Interval:</th>
					<td>${ping}</td>
				</tr>
				<tr>
					<th># of Logs to Pull: </th>
					<td>${log}</td>
				</tr>
				<tr>
					<th># of Logs Pulled to Date: </th>
					<td>${number}</td>
				</tr>
				<tr>
					<th>Date Registered: </th>
					<td>${reporterRT}</td>
				</tr>
				<tr>
					<th>Last Accessed Time: </th>
					<td>${reporterLAT}</td>
				</tr>
				<tr>
					<th>Last Successful Pull Time: </th>
					<td>${reporterLSP}</td>
				</tr>
				<tr>
					<th>Last Error Time: </th>
					<td>${reporterLET}</td>
				</tr>
				<tr>
					<th>Currently Active: </th>
					<td>${active}</td>
				</tr>
			</table>
			<table id="ReporterHist" style="float:left">
				<tr>
					<th colspan="3">Last 5 Reporter Outages</th>
				</tr>
				<tr>
					<th>Start DateTime</th>
					<th>End DateTime</th>
					<th>Outage Interval</th>
				</tr>
				<c:forEach items="${reporterHistory}" var="reporter">
				<tr>
					<td>${reporter[0]}</td>
					<td>${reporter[1]}</td>
					<td>${reporter[2]}</td>
				</tr>
				</c:forEach>
			</table>
		</div>
	</div>
	<div id="Controls" style="float:right; width:20%;">
	<h2>Controls</h2>
		Server Access Logs
		<br/> &emsp; <!-- &emsp; is the code for a tab -->
		<a href="./harvester/logLines?hyraxInstanceName=${name}">
			(Last 500)
		</a>
		<br/> &emsp;
		<a href="./harvester/logLines?hyraxInstanceName=${name}&bonus=true">
			(All)
		</a>
		<br/>
		<a href="./months?hyraxInstanceName=${name}">
			View monthly breakdowns
		</a>
		<br/>
		<button type="button" id="repullBtn" onclick="javascript:repull()">
			Clear and Re-Pull Server Logs
		</button> 
		<!-- 
		<a href="./repull?hyraxInstanceName=${name}">
			Clear and Re-Pull Server Logs
		</a> 
		 -->
		<br/>
		<a href="./toggleActive?hyraxInstanceName=${name}">
			Toggle Reporter Active Status
		</a>
		<br/>
		<button type="button" id="removeBtn" onclick="javascript:remove()">
			Remove Server
		</button> 
		<!-- 
		<a href="./remove?hyraxInstanceName=${name}">
			Remove Server
		</a>
		 -->
		<br/>
		<br/>
	</div>
	
	<script>
		var serverName = '${name}';
		//console.log("Name: "+ serverName);
	
		function repull() {
			//console.log("repull() called");
			if (confirm("Are you sure you want to clear and repull all log data?")){
				//console.log("Yes: beginning repull ...");
				
				var fctData = {
						hyraxInstanceName : serverName
				}
				
				$.ajax({
					type: "POST",
					url: "./repull",
					data: fctData,
					success: function (textStatus, status) {
				        //console.log(textStatus);
				        //console.log(status);
				        location.reload();
					},
					error: function(xhr, textStatus, error) {
				        //console.log(xhr.responseText);
				        //console.log(xhr.statusText);
				        //console.log(textStatus);
				        //console.log(error);
					}
				});
			}
			else{
				//console.log("No: cancelling ...");
			}
		}// end repull()
		
		function remove() {
			//console.log("remove() called");
			if (confirm("Are you sure you want to remove this reporter?")){
				//console.log("Yes: beginning remove ...");
				
				var fctData = {
						hyraxInstanceName : serverName
				}
				
				$.ajax({
					type: "POST",
					url: "./remove",
					data: fctData,
					success: function (textStatus, status) {
				        //console.log(textStatus);
				        //console.log(status);
				        location.replace("./opendap");
					},
					error: function(xhr, textStatus, error) {
				        //console.log(xhr.responseText);
				        //console.log(xhr.statusText);
				        //console.log(textStatus);
				        //console.log(error);
					}
				});
			}
			else{
				//console.log("No: cancelling ...");
			}
		}// end repull()
		
	</script>
</body>
</html>