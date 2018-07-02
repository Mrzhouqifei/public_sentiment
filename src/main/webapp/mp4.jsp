<%@page import="org.gephi.service.PngService"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="org.gephi.service.SvgService"%>
<%@page import="org.gephi.entity.Node"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.gephi.utils.ReadAndProcess"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="description" lang="en"
	content="jQuery multiselect plugin with two sides. The user can select one or more items and send them to the other side." />
<meta name="keywords" lang="en" content="jQuery multiselect plugin" />
<title>舆情模拟</title>
<link rel="icon" type="image/x-icon"
	href="https://github.com/favicon.ico" />
<!-- <link rel="stylesheet"
	href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" /> -->
	<link rel="stylesheet" href="css/bootstrap.min.css" />
<link rel="stylesheet" href="lib/google-code-prettify/prettify.css" />
<link rel="stylesheet" href="css/style.css" />
<style type="text/css">
body {
	background: url('images/map_bg.png') ;
}

.demo {
	width: 600px;
	margin: 40px auto;
	color: #424242;
	background: #fff
}

.div {
	margin: 0 auto;
	width: 80%;
	border: 1px
}
</style>

</head>
<body>
	<%
		ReadAndProcess readAndProcess = (ReadAndProcess) request.getSession().getAttribute("readAndProcess");
		Node nodes[] = readAndProcess.allNodes;
		int time = readAndProcess.MAX_TIME + 1;
	%>

	<div id="wrap" class="container">
		<div class="row">
			<div class="col-sm-6">
				<h4 id="demo-search" style="color: gray">干预前推演仿真</h4>
				<video width="550" height="550" src="mp4/before.mp4"
					autoplay="autoplay" controls="controls" poster="images/poster.jpeg"></video>
				<select name="from[]" id="search" class="form-control" size="10"
					multiple="multiple" style="background: transparent;">
					<%
						if (readAndProcess != null) {
							ArrayList<Node> list = new ArrayList<Node>();
							for (Node node : nodes) {
								if (node.getActiveTime() <= time && node.getActiveTime() > 0) {
									list.add(node);
								}
							}
							Collections.sort(list);
					%>
					<%
						for (Node node : list) {
								int min = node.getActiveTime() / 60;
								int second = node.getActiveTime() % 60;
								String s = String.format("%02d", min) + ":" + String.format("%02d", second);
					%>

					<option value=<%=node.getNid()%>
						data-position=<%=node.getActiveTime()%>><%=s%>   <%=node.getNodeName()%></option>

					<%
						}
						}
					%>
				</select>
			</div>
			
			<div class="col-sm-6">
				<h4 id="demo-search" style="color: gray">干预后推演仿真</h4>
				<video width="550" height="550" src="mp4/after.mp4"
					autoplay="autoplay" controls="controls" poster="images/poster.jpeg"></video>
				<select name="search_to" id="search_to" class="form-control" size="10"
					multiple="multiple" style="background: transparent;">
					<%
						if (readAndProcess != null) {
							
							ArrayList<Node> list = new ArrayList<Node>();
							for (Node node : nodes) {
								if (node.getActiveTimeAfter() <= time && node.getActiveTimeAfter() > 0) {
									list.add(node);
									System.out.print(node);
								}
							}
							Collections.sort(list);
					%>
					<%
						for (Node node : list) {
								int min = node.getActiveTimeAfter() / 60;
								int second = node.getActiveTimeAfter() % 60;
								String s = String.format("%02d", min) + ":" + String.format("%02d", second);
					%>

					<option value=<%=node.getNid()%>
						data-position=<%=node.getActiveTimeAfter()%>><%=s%>   <%=node.getNodeName()%></option>

					<%
						}
						}
					%>
				</select>
			</div>
		</div>
	</div>

	<script type="text/javascript"
		src="//cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<script type="text/javascript"
		src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript"
		src="//cdnjs.cloudflare.com/ajax/libs/prettify/r298/prettify.min.js"></script>
	<script type="text/javascript" src="dist/js/multiselect.min.js"></script>

	<script>
		(function(i, s, o, g, r, a, m) {
			i['GoogleAnalyticsObject'] = r;
			i[r] = i[r] || function() {
				(i[r].q = i[r].q || []).push(arguments)
			}, i[r].l = 1 * new Date();
			a = s.createElement(o), m = s.getElementsByTagName(o)[0];
			a.async = 1;
			a.src = g;
			m.parentNode.insertBefore(a, m)
		})(window, document, 'script',
				'//www.google-analytics.com/analytics.js', 'ga');

		ga('create', 'UA-39934286-1', 'github.com');
		ga('send', 'pageview');
	</script>

	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							// make code pretty
							window.prettyPrint && prettyPrint();

							$('#search')
									.multiselect(
											{
												search : {
													left : '<input type="text" name="q" class="form-control" placeholder="Search..." />',
													right : '<input type="text" name="q" class="form-control" placeholder="Search..." />',
												},
												fireSearch : function(value) {
													return value.length > 3;
												}
											});
						});
	</script>

	<script>
		function test() {
			var selectedComs = document.getElementById("search_to");
			for (var i = 0; i < selectedComs.length; i++) {
				selectedComs.options[i].selected = true;

			}
			return true;
		}
	</script>
</body>
</html>