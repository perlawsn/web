<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="include/common_script.jsp" %>
<title>PerLa Web Console</title>
</head>
<body>
<div data-role="page">

    <div data-role="header">
        <h1>PerLa Web Console</h1>
    </div>

    <div role="main" class="ui-content narrow-content">
        <h2>Welcome to the PerLa Web Console!</h2>
        <p>
        Select FPC management to list the available devices, or to add a new one.
        Select Query Management to manage running queries or to start a new one
        </p>
        <a href="/perla-web/console/fpc/" class="ui-btn ui-corner-all ui-btn-icon-right ui-icon-carat-r">FPC management</a>
        <a href="/perla-web/console/query/" class="ui-btn ui-corner-all ui-btn-icon-right ui-icon-carat-r">Query management</a>
    </div>

    <%@include file="include/common_footer.jsp" %>

</div>
</body>
</html>
