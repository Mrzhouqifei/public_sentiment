<%-- 
    Document   : image
    Created on : 2017-10-1, 22:36:09
    Author     : Qifei_Zhou
--%>

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
<title>舆情可视化</title>
<link rel="icon" type="image/x-icon"
	href="https://github.com/favicon.ico" />


<link rel="stylesheet" href="lib/google-code-prettify/prettify.css" />

<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css"
	media="all">
<link href="css/font-awesome.min.css" rel="stylesheet" type="text/css"
	media="all">
<link href="css/owl.carousel.css" rel="stylesheet">
<link rel="stylesheet" href="css/lightbox.css">
<link href="css/style.css" rel="stylesheet" type="text/css" media="all" />
<link href="//fonts.googleapis.com/css?family=Open+Sans"
	rel="stylesheet">
<link href="//fonts.googleapis.com/css?family=Roboto+Condensed"
	rel="stylesheet">
<link
	href="//fonts.googleapis.com/css?family=Sintony:400,700&amp;subset=latin-ext"
	rel="stylesheet">

<style type="text/css">
body {
	background: url('images/map_bg.png');
}

.demo {
	width: 600px;
	margin: 40px auto;
	color: #424242;
	background: #fff
}

.div {
	margin: 0 auto;
	width: 75%;
	border: 1px
}

.w3_bandwn {
	padding: 1em 1em;
	background: url('images/title22.png');
	/* background-color: rgba(0,51，120, 0); */
}

.text {
	text-align: center
}
</style>


<script src="js/bootstrap.min.js"></script>
<script src="js/jquery-3.3.1.min.js"></script>
</head>
<body>
<img src="images/title22.png">
	<br>
	<div class="div">

		<div class="row">
			<%
				ArrayList<Node> notActived = new ArrayList<Node>();
				ArrayList<Node> actived = new ArrayList<Node>();
				ReadAndProcess readAndProcess = (ReadAndProcess) request.getSession().getAttribute("readAndProcess");
				if (readAndProcess != null) {
					for (Node node : readAndProcess.allNodes) {
						if (node.isIsolated())
							continue;
						if (node.isActived()) {
							actived.add(node);
						} else {
							notActived.add(node);
						}
					}
				}
			%>
			<div class="col-xs-2">
				<br>
				<form action="UploadHandleServlet" enctype="multipart/form-data"
					method="post">
					<h4 id="demo-search" style="color: gray">请选择一个文件：</h4>
					<input type="file" name="file1" style="color: gray">
					<h4></h4>
					<input type="submit" value=" 上传 ">
				</form>
			</div>
			<div class="col-xs-3">
				<h4 id="demo-search" style="color: gray">未传播节点</h4>
				<select name="from[]" id="search" class="form-control" size="6"
					multiple="multiple" style="background: transparent;">
					<%
						for (Node node : notActived) {
					%>
					<option value=<%=node.getNid()%> data-position=<%=node.getNid()%>><%=node.getNodeName()%></option>
					<%
						}
					%>
				</select> <a href="max_svg.jsp" class="btn btn-primary btn-block"
					target="_blank">查看网络结构</a>
			</div>

			<div class="col-xs-1">
				<br> <br> <br>
				<button type="button" id="search_rightAll"
					class="btn btn-primary btn-block">
					<i class="glyphicon glyphicon-forward"></i>
				</button>
				<button type="button" id="search_rightSelected"
					class="btn btn-block">
					<i class="glyphicon glyphicon-chevron-right"></i>
				</button>
				<button type="button" id="search_leftSelected" class="btn btn-block">
					<i class="glyphicon glyphicon-chevron-left"></i>
				</button>
				<button type="button" id="search_leftAll"
					class="btn btn-warning btn-block">
					<i class="glyphicon glyphicon-backward"></i>
				</button>
			</div>
			<div class="col-xs-3">
				<h4 id="demo-search" style="color: gray">已传播节点</h4>
				<form action="ActiveServlet" method="post" onsubmit="return test()">
					<select name="search_to" id="search_to" class="form-control"
						size="6" multiple="multiple" style="background: transparent;">
						<%
							for (Node node : actived) {
						%>
						<option value=<%=node.getNid()%> data-position=<%=node.getNid()%>><%=node.getNodeName()%></option>
						<%
							}
						%>
					</select>
					<div class="row">
						<div class="col-xs-6">
							<input class="btn btn-warning btn-block" type="submit"
								value="激活节点">
						</div>
						<div class="col-xs-6">
							<a href="ControlServlet" class="btn btn-warning btn-block"
								target="_blank">动态推演</a>
						</div>
					</div>
				</form>
			</div>
			<!-- class="col-sm-2" -->
			<div style="float: left">
				<br>
				<br> <a style="color: gray">事件主题</a> <select>
					<option value="audi">娱乐绯闻</option>
					<option value="volvo">政治新闻</option>
					<option value="saab">社会治安</option>
					<option value="opel">食品安全</option>
				</select> <br>
				<br> <a style="color: gray">时间单位</a> <select>
					<option value="audi">星期(秒)</option>
					<option value="volvo">分钟</option>
					<option value="saab">小时</option>
					<option value="opel">天</option>
				</select> <br>
				<br> <a style="color: gray">干预方案</a> <select>
					<option value="volvo">方案A</option>
					<option value="saab">方案B</option>
					<option value="opel">方案C</option>
				</select>
			<h5 style="color: gray">方案A:全网正向舆论引导</h5>
			<h5 style="color: gray">方案B:关键节点阻隔（大V）</h5>
			<h5 style="color: gray">方案C:方案AB结合</h5>
			</div>
		</div>
	</div>
	<br>
	<div class="container">

		<%
			SvgService list = (SvgService) request.getSession().getAttribute("svgService");
			if (list != null) {
				ArrayList<Integer> actived1 = (ArrayList<Integer>) request.getSession().getAttribute("actived");
				if (actived1 == null) {
		%>
		<div class="row">
			<div class="col-xs-3"></div>
			<img width=550 height=550 src="PngServlet">
		</div>


		<%
			} else {
		%>
		<div class="row">

			<!-- <img width=600 height=600 src="PngServlet"> -->

		</div>


		<div class="gallery" id="gallery">
			<div class="container-fluid">
				<div class="about-bottom  w3ls-team-info">
					<div class="col-md-12">
						<div id="Carousel" class="carousel slide">
							<!-- Carousel items -->
							<div class="carousel-inner">
								<div class="item active">
									<div class="row">
										<div class="col-md-4 col-sm-8 col-xs-8 img-gallery-w3l">
											<div class="text" style="text-align: center;">
												<h4 style="color: gray">潜伏期</h4>
											</div>
											<img width=350 height=380 src="png4/p1.png">
										</div>
										<div class="col-md-4 col-sm-8 col-xs-8 img-gallery-w3l">
											<div class="text" style="text-align: center;">
												<h4 style="color: gray">突发期</h4>
											</div>
											<img width=350 height=380 src="png4/p2.png">
										</div>
										<div class="col-md-4 col-sm-8 col-xs-8 img-gallery-w3l">
											<div class="text" style="text-align: center;">
												<h4 style="color: gray">蔓延期</h4>
											</div>
											<img width=350 height=380 src="png4/p3.png">
										</div>
									</div>
									<!--.row-->
								</div>
								<!--.item-->
							</div>

						</div>
						<!--.Carousel-->

					</div>

				</div>
			</div>

		</div>
		<!-- //gallery -->

		<%
			}
			}
		%>

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
													return value.length > 0;
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

