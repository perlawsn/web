<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="include/common_script.jsp" %>
<script type="text/javascript">
$(document).on('pageinit', function() {
	
	// Load FPC list
	$.ajax({
		url: '/perla-web/rest/v1/task',
		dataType: 'json',
		async: true,
		success: function(queries) {
			display(queries);
		},
		error: function(request, error) {
			alert('Error while retrieving FPC data: ' + error);
		}
	});
});

function display(queries) {
	if (queries.length == 0) {
		$('#query-list').append('<li>No query found</li>');
		$('#query-list').listview('refresh');
		return;
	}

	$.each(queries, function(i, q) {
		var elemId = 'query-' + q.id;
		var item = '<li>';
		item += '<a href="/perla-web/console/query/' + q.id + '">'
		item += '<h1>Id: ' + q.id + '</h1>';
		item += '<p class="wrap">';
		item += '<strong>Attributes:</strong> ';
		$.each(q.attributes, function(i, a) {
			item += a.id + ' (' + a.type + ')';
			if (i != q.attributes.length - 1) {
				item += ', ';
			}
		});
		item += '</p>';
		item += '<p class="wrap">'
		item += '<strong>FPCs:</strong> ';
		$.each(q.fpcs, function(i, f) {
			item += f;
			if (i != q.fpcs.length - 1) {
				item += ', ';
			} 
		});
		item += '</p>';
		item += '</a>';
		item += '<a id="' + elemId + '">Stop Query</a>';
		item += '</li>';
		$('#query-list').append(item);
		
		$('#' + elemId).click(function() {
			stopQuery(q.id);
		});
	});
	$('#query-list').listview('refresh');
}

function stopQuery(id) {
	$.ajax({
		url: '/perla-web/rest/v1/task/' + id,
		dataType: 'json',
		type: 'delete',
		async: true,
		success: function(queries) {
			location.reload();
		},
		error: function(request, error) {
			alert('Error while stopping query: ' + error);
		}
	});
}
</script>
<title>PerLa Web Console</title>
</head>
<body>
<div data-role="page">

	<div data-role="header">
		<a href="/perla-web/console/" class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-carat-l">Back</a>
		<h1>PerLa Web Console</h1>
	</div>

	<div role="main" class="ui-content main-content">
		<h2>
		Query Management
		</h2>
		
		<ul data-role="listview" data-split-icon="delete" data-inset="true" id="query-list">

        </ul>
	</div>

	<div data-role="footer">
		<h4>PerLa 2014</h4>
	</div>
</div>
</body>
</html>