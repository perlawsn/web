<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="include/common_script.jsp" %>
<script src="/perla-web/console/static/stomp.js"></script>
<script type="text/javascript">
var client;
$(document).on('pageinit', function() {
	client = Stomp.client("ws://localhost:8080/perla-web/query");
	client.connect("guest", "guest", connect_cback, error_cback);
});

function connect_cback() {
	console.log("Successfully connected");
	subscription = client.subscribe("/output/${id}", function(d) {
		var out = $('#output');
		out.val(d.body + "\n" + out.val());
	});
}

function error_cback(error) {
	console.log("Connection error");
	console.log(error);
}
</script>
<title>PerLa Web Console</title>
</head>
<body>
<div data-role="page">

	<div data-role="header">
		<a href="/perla-web/console/query/" class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-carat-l">Back</a>
		<h1>PerLa Web Console</h1>
	</div>

	<div role="main" class="ui-content main-content">
		<h2>
		Query output (Id ${id})
		</h2>
		
		<label for="output">Records:</label>
		<textarea cols="40" rows="21" name="output" id="output"></textarea>
	</div>

	<div data-role="footer">
		<h4>PerLa 2014</h4>
	</div>
</div>
</body>
</html>