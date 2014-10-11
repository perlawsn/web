<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="include/common_script.jsp" %>
<link rel="stylesheet" href="/perla-web/console/static/prettify.css"/>
<script src="/perla-web/console/static/prettify.js"></script>
<script type="text/javascript">
$(document).on('pageinit', function() {

	// Load FPC list
	$.ajax({
		url: '/perla-web/rest/v1/fpc',
		dataType: 'json',
		async: true,
		success: function(fpcs) {
			display(fpcs);
		},
		error: function(resp) {
			var msg = resp.responseJSON.message;
			alert('Error while retrieving FPC list: ' + msg);
		}
	});

	// Upload new Device Descriptor
	$('#descriptor').change(function() {
		var reader = new FileReader();
        var file = $('#descriptor')[0].files[0];
        reader.onload = function(evt) {
        	$.ajax({
        		beforeSend: function(xhrObj) {
        	        xhrObj.setRequestHeader("Content-Type","application/xml");
        	    },
        		url: '/perla-web/rest/v1/fpc',
        		dataType: 'json',
        		type: 'put',
        		async: true,
        		data: evt.target.result,
        		success: function(result) {
        			location.reload();
        		},
        		error: function(resp) {
        			var msg = resp.responseJSON.message;
        			alert('Error while uploading Device Descriptor: ' + msg);
        		}
        	});

		};
        reader.readAsBinaryString(file);
	});
});

function display(fpcs) {
	if (fpcs.length == 0) {
		$('#fpc-list').append('<li>No FPC found</li>');
		$('#fpc-list').listview('refresh');
		return;
	}

	$.each(fpcs, function(i, f) {
        // List element
        var btnPopId = 'btn-popup-' + f.id;
		var item = '<li>';
        item += '<a id="' + btnPopId + '">';
		item += '<h1>Id: ' + f.id + '</h1>';
        item += '<p class="wrap">' + f.type + '</p>';
		item += '<p class="wrap">';
		$.each(f.attributes, function(i, a) {
			item += a.id + ' (' + a.type + ')';
			if (i != f.attributes.length - 1) {
				item += ', ';
			}
		});
		item += '</p>';
        item += '</a>';
		item += '</li>';
		$('#fpc-list').append(item);

        // Popup
        var popup = $("<div/>").popup({
            dismissible : true,
            theme : "a",
            overlyaTheme : "a",
            transition : "pop"
        }).append('<h4>Device Descriptor - Id:' + f.id + '</h4>');

        $('<pre/>', {
            text : f.descriptor
        }).addClass('prettyprint lang-xml code').appendTo(popup);

        $('#' + btnPopId).click(function(){
            popup.popup("open");
            prettyPrint();
        });
	});
	$('#fpc-list').listview('refresh');
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

	<div role="main" class="ui-content narrow-content">
		<h2>FPC Management</h2>

		<label for="descriptor">Device descriptor:</label>
		<input type="file" name="descriptor" id="descriptor" value="">

		<ul data-role="listview" data-inset="true" id="fpc-list" data-icon="info">

        </ul>
	</div>

	<div data-role="footer">
		<h4>PerLa 2014</h4>
	</div>
</div>
</body>
</html>
