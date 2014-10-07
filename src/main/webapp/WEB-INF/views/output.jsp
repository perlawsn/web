<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.4/jquery.mobile-1.4.4.min.css"/>
<link rel="stylesheet" href="/perla-web/console/style.css"/>
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
$(document).bind("mobileinit", function () {
    $.mobile.ajaxEnabled = false;
});
</script>
<script src="http://code.jquery.com/mobile/1.4.4/jquery.mobile-1.4.4.min.js"></script>
<script src="/perla-web/console/stomp.js"></script>
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