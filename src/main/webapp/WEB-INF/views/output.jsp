<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <%@include file="include/common_script.jsp"%>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript" src="/perla-web/console/static/stomp.js"></script>
    <script type="text/javascript" src="/perla-web/console/static/moment.js"></script>
    <script type="text/javascript">
        var client;
        var query;
        var chart;

        google.load('visualization', '1.0', {'packages':['corechart']});
        google.setOnLoadCallback(function() {
            $(function() {
                $.ajax({
                    url: '/perla-web/rest/v1/query/running/${id}',
                    dataType: 'json',
                    async: true,
                    success: function(q) {
                        query = q;
                        createTableHeader(q.attributes);
                        chart = createChart();
                        if (chart !== null) {
                            chart.draw();
                        }
                        client = Stomp.client('ws://<%=request.getServerName()%>:<%=request.getServerPort()%>/perla-web/query');
                        client.connect('guest', 'guest', connectCback, errorCback);
                    },
                    error: function(resp) {
                        var msg = resp.responseJSON.message;
                        alert('Error while retrieving query ${id}: ' + msg);
                    }
                });
            })
        });

        function createTableHeader(atts) {
            $.each(atts, function(i, a) {
                var elemId = 'header-' + a.id;
                var elem = '<th id="' + elemId + '">';
                if (a.id !== 'id' && a.type === 'FLOAT' || a.type === 'INTEGER') {
                    elem += '<a href="#">' + a.id + '</a>';
                } else {
                    elem += a.id;
                }
                elem += '</td>';
                $('#table-head').append(elem);
                $('#' + elemId).click(function() {
                    chart = createChart(a);
                });
            });
            $('#table').table('refresh');
        }

        function addRow(sample) {
            // Update table
            var row = '<tr>';
            $.each(query.attributes, function(i, a) {
                row += '<td>' + sample[a.id] + '</td>';
            });
            row += '</tr>';
            $('#table').prepend(row);

            // Update chart
            if (chart === null) {
                return; // Skip update if chart wasn't created
            }
            var i = chart.colMap[sample['id']];
            chart.row[i] = parseFloat(sample[chart.att.id]);
            row = chart.row.slice();
            row[0] = moment(sample['timestamp'].substring(0, 29)).toDate();
            chart.data.addRows([
                row
            ]);
            chart.draw();
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

        function createChart(att) {
            var chart = {};

            // Set attribute
            att = typeof att !== 'undefined' ? att : selectAttribute(query.attributes);
            if (att === null) {
                return null;
            }
            chart['att'] = att;
            // Set chart options
            chart['options'] = {
                title: att.id,
                width: 800,
                height: 300,
                animation: {
                    duration: 200,
                    easing: 'out'
                },
                backgroundColor: {
                    fill:'transparent'
                }
            }
            // Create DataTable
            var data = new google.visualization.DataTable();
            data.addColumn('datetime', 'Timestamp');
            $.each(query.fpcs, function(i, f) {
                data.addColumn('number', 'node-' + f);
            });
            chart['data'] = data;
            // Create template row
            var row = [];
            var colMap = {};
            row[0] = null;
            $.each(query.fpcs, function(i, f) {
                row[i + 1] = 0;
                colMap[f] = i + 1;
            });
            chart['row'] = row;
            chart['colMap'] = colMap;
            // Create chart
            chart['chart'] = new google.visualization.LineChart(document.getElementById('chart'));
            // Add draw function
            chart.draw = function draw() {
                chart.chart.draw(chart.data, chart.options);
            }

            return chart;
        }

        function selectAttribute(atts) {
            var att = null
            $.each(atts, function(i, a) {
                if (a.id != "timestamp" && a.id != "id") {
                    att = a;
                    return false;
                }
            });
            return att;
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

        <div id="chart">

        </div>

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
