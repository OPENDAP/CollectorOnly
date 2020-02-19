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
<script src="https://d3js.org/d3.v5.js" charset="UTF-8"></script>
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
	 /* Style the tab */
	.tab {
	  overflow: hidden;
	  border: 1px solid #ccc;
	  background-color: #f1f1f1;
	}
	
	/* Style the buttons that are used to open the tab content */
	.tab button {
	  background-color: inherit;
	  float: left;
	  border: none;
	  outline: none;
	  cursor: pointer;
	  padding: 14px 16px;
	  transition: 0.3s;
	}
	
	/* Change background color of buttons on hover */
	.tab button:hover {
	  background-color: #ddd;
	}
	
	/* Create an active/current tablink class */
	.tab button.active {
	  background-color: #ccc;
	}
	
	/* Style the tab content */
	.tabcontent {
	  display: none;
	  padding: 6px 12px;
	  border: 1px solid #ccc;
	  border-top: none;
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
	<div class="tab">
		<button class="tablinks" onclick="openTab(event, 'Table')" id="defaultOpen">LogLine Table</button>
		<button class="tablinks" onclick="openTab(event, 'Graph1')">Monthly Access Graph</button>
	</div>
	<div id="Table" class="tabcontent">
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
	<div id="Graph1" class="tabcontent">
		<svg id="mGraph" width="1200" height="600"></svg>
	</div>
	
	<script>	
		function openTab(evt, tabName){
			// Declare all variables
			var i, tabcontent, tablinks;
			
			// Get all elements with class="tabcontent" and hide them
			tabcontent = document.getElementsByClassName("tabcontent");
			for (i = 0; i < tabcontent.length; i++){
				tabcontent[i].style.display = "none";
			}
			
			// Get all elements with class="tablinks" and remove the class "active"
			tablinks = document.getElementsByClassName("tablinks");
			for (i = 0; i < tablinks.length; i++) {
			  tablinks[i].className = tablinks[i].className.replace(" active", "");
			}
	
			// Show the current tab, and add an "active" class to the button that opened the tab
			document.getElementById(tabName).style.display = "block";
			evt.currentTarget.className += " active";
		}
		
		document.getElementById("defaultOpen").click();
		
		//var graph_1_data = ${graph1Data};
		
		var msvg = d3.select("#mGraph"), 
			margin = 100, 
			width = msvg.attr("width") - margin,
			height = msvg.attr("height") - margin;
		
		var data = [10, 50, 15, 30, 20, // 5
					20, 21, 22, 23, 24, // 10
					25, 26, 27, 28, 29, // 15
					30, 31, 32, 33, 34, // 20
					35, 36, 37, 38, 39, // 25
					40, 41, 42, 43, 44, // 30
					45]; // 31
		
		var xScale = d3.scaleBand()
				//.domain([0, 32])
				.range ([0, width - 10]).padding(0.4),
			yScale = d3.scaleLinear()
				.domain([0, d3.max(data)])
				.range ([height, 0]);
					
		var tickNumber = 0;
					
		if(data.length == 31){
			xScale.domain([1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
							11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
							21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31]);
			tickNumber = 31;
		}
		else if(data.lenght == 28){
			xScale.domain([0,28]);
			tickNumber = 28;
		}
		else{
			xScale.domain([0,30]);
			tickNumber = 30;
		}
				
		var x_axis = d3.axisBottom().scale(xScale).ticks(tickNumber);
		var y_axis = d3.axisLeft().scale(yScale);
		
		msvg.append("g")
			.attr("transform", "translate(100,10)")
			.call(y_axis);
	
		msvg.append("g")
			.attr("transform", "translate(100," + (height + 10) + ")")
			.call(x_axis);
		
		msvg.selectAll(".bar")
			.data(data)
			.enter().append("rect")
			.attr("class", "bar")
			.attr("x", function(d,i) { return xScale(i+1) + 100; })
			.attr("y", function(d,i) { return yScale(d) + 10; })
			.attr("width", xScale.bandwidth())
			.attr("height", function(d,i) { return height - yScale(d); });
		
		
		
		
	</script>
</body>
</html>
