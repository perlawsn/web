<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="include/common_script.jsp"%>
<script src="/perla-web/console/static/stomp.js"></script>
<script type="text/javascript">
	var client;
	var query;
	
	$(document).on('pageinit', function() {
		$.ajax({
			url: '/perla-web/rest/v1/query/running/${id}',
			dataType: 'json',
			async: true,
			success: function(q) {
				query = q;
				createTableHeader(q.attributes);
				client = Stomp.client('ws://<%=request.getServerName()%>:<%=request.getServerPort()%>/perla-web/query');
				client.connect('guest', 'guest', connectCback, errorCback);
			},
			error: function(resp) {
				var msg = resp.responseJSON.message;
				alert('Error while retrieving query ${id}: ' + msg);
			}
		});
	});
	
	function createTableHeader(atts) {
		$.each(atts, function(i, a) {
			var elem = '<th>' + a.id + '</th>';
			$('#table-head').append(elem);
		});
		$('#table').table('refresh');
	}
	
	function addRow(record) {
		var row = '<tr>';
		$.each(query.attributes, function(i, a) {
			row += '<td>' + record[a.id] + '</td>';
		});
		row += '</tr>';
		$('#table').prepend(row);
		$('#table').table('refresh');
	}

	function connectCback() {
		console.log('Successfully connected');
		subscription = client.subscribe('/output/${id}', function(d) {
			addRow(JSON.parse(d.body));
		});
	}

	function errorCback(error) {
		console.log('Connection error');
		console.log(error);
	}
</script>
<title>PerLa Web Console</title>
</head>
<body>
	<div data-role="page">

		<div data-role="header">
			<a href="/perla-web/console/query/"
				class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-carat-l">Back</a>
			<h1>PerLa Web Console</h1>
		</div>

		<div role="main" class="ui-content wide-content">
			<h2>Query output (Id: ${id})</h2>
			
			<table data-role="table" id="table" class="ui-body-d ui-shadow table-stripe ui-responsive">
				<thead id="table-head">
				</thead>
				<tbody id="table-body">
				</tbody>
			</table>

		</div>

		<div data-role="footer">
			<h4>PerLa 2014</h4>
		</div>
	</div>
</body>
</html>