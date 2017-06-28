// load charts
var lineChart = echarts.init(document.getElementById('line-chart'));
var pieChart = echarts.init(document.getElementById('pie-chart'));
var mapChart = echarts.init(document.getElementById('map-chart'));
var riverChart = echarts.init(document.getElementById('river-chart'));

// initial values
var filterDimension = ''
var topDimensionPie = 'SECTOR'
var topDimensionRiver = 'SECTOR'
var filterValue = ''
var fromDate = '2015-01-01'
var toDate = '2017-01-01'
var granularity = 'week'
var filterMetric = 'avg_importe'
var filterMetricName = 'Importe medio'
var metrics = {
    'Importe medio': 'avg_importe',
    'Importe total': 'total_importe',
    'Num. operaciones total': 'total_num_op',
    'Num. operaciones medio': 'avg_num_op'
}
var extra = '€'
var granularities = {
    'Día':'Day',
    'Semana': 'Week', 
    'Mes': 'Month'
}

// line chart
var default_line_option = {
    dataZoom: {
        show: true,
        start: 0,
        fillerColor: 'rgba(184,69,60,0.2)'
    },
    tooltip: {
        trigger: 'axis',
        formatter: function(params, ticket, callback) {
            data = ''
            if (params.length > 0) {
                var date = new Date(params[0].value[0]);
                data = date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear() +
                    ' ' +
                    ("0" + date.getHours()).slice(-2) + ':' +
                    ("0" + date.getMinutes()).slice(-2);
                for (var i = 0; i < params.length; i++) {
                    data = data + '<br/>'
                    data = data + '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>'
                    data = data + Math.round(params[i].value[1] * 100) / 100 + extra
                }
            }
            return data
        }
    },
    xAxis: {
        type: 'time',
        data: []
    },
    yAxis: {
        type: 'value',
        axisLabel: {
            formatter: function(value, index) {
                return Math.round(value * 100) / 100 + extra;
            }
        }
    },
    series: [{
        type: 'line',
        data: []
    }]
}

lineChart.setOption(default_line_option);

function getLineData() {
    $.ajax({
        url: 'http://api.ziggy.ovh/data?metric=' + filterMetric + '&fromDate=' + fromDate + '&toDate=' + toDate +
            '&granularity=' + granularity + '&fDimension=' + filterDimension + '&fValue=' + filterValue,
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            times = response.timestamps
            lineChart.hideLoading()
            lineChart.setOption({
                legend: {
                    data: [filterMetricName]
                },
                color: ['#b8453c'],
                series: [{
                    name: filterMetricName,
                    type: 'line',
                    data: times,
                    markLine: {
                        data: [{
                            type: 'average'
                        }]
                    }

                }]
            });
        }
    });
}

// pie
var default_pie_option = {
    series: [{
        type: 'pie',
        data: []
    }]
}

pieChart.setOption(default_pie_option);

function getPieData() {
    $.ajax({
        url: 'http://api.ziggy.ovh/top?metric=' + filterMetric + '&fromDate=' + fromDate + '&toDate=' + toDate +
            '&granularity=' + granularity + '&dimension=' + topDimensionPie +
            '&fDimension=' + filterDimension + '&fValue=' + filterValue + '&limit=12',
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            pie = response.values
            legendData = []
            response.values.forEach(function(value, index, array) {
                legendData.push(value.name)
            })
            pieChart.hideLoading()
            pieChart.setOption({
                tooltip: {
                    trigger: 'item',
                    formatter: function(params, ticket, callback) {
                        var tooltipVar = ''
                        tooltipVar = params.name + '<br/>'
                        tooltipVar = tooltipVar + '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params.color + '"></span>'
                        tooltipVar = tooltipVar + Math.round(params.value * 100) / 100 + extra
                        return tooltipVar;
                    }
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    data: legendData,
                    textStyle: {
                        fontSize: 10
                    }
                },
                series: [{
                    name: filterMetricName,
                    radius: [0, '50%'],
                    center: ['65%', '50%'],
                    type: 'pie',
                    data: pie
                }]
            });
        }
    });
}

// map
// load geojson of valencia
$.get('map/json/map.json', function(vlcJson) {
    echarts.registerMap('vlc', vlcJson);
    mapChart.setOption({
        backgroundColor: '#a3ccff',
        geo: [{
            map: 'vlc',
            zoom: 15,
            roam: true,
            center: [-0.609823, 39.492500],
            itemStyle: {
                normal: {
                    areaColor: '#f3f1ed',
                    borderColor: '#938169'
                },
                emphasis: {
                    areaColor: '#cfbca3'
                }
            },
            label: {
                emphasis: {
                    show: false,
                }
            }
        }],
    });
});

function getMapData() {
    $.ajax({
        url: 'http://api.ziggy.ovh/geo?metric=' + filterMetric + '&fromDate=' + fromDate + '&toDate=' + toDate + '&type=cliente' +
            '&fDimension=' + filterDimension + '&fValue=' + filterValue,
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            geoData = response.geoData
            mapChart.hideLoading()
            mapChart.setOption({
                animation: true,
                animationDuration: 300,
                animationEasing: 'cubicInOut',
                animationDurationUpdate: 300,
                animationEasingUpdate: 'cubicInOut',
                xAxis: {
                    type: 'value',
                    axisLine: {
                        show: false
                    }
                },
                yAxis: {
                    type: 'category',
                    axisLine: {
                        show: false
                    }
                },
                tooltip: {
                    trigger: 'item',
                    formatter: function(params, ticket, callback) {
                        var tooltipVar = ''
                        tooltipVar = params.name + '<br/>'
                        tooltipVar = tooltipVar + '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params.color + '"></span>'
                        tooltipVar = tooltipVar + Math.round(params.value[2] * 100) / 100 + extra
                        return tooltipVar;
                    }
                },
                label: {
                    normal: {
                        position: 'right',
                        show: false
                    },
                    emphasis: {
                        show: false
                    }
                },
                itemStyle: {
                    normal: {
                        color: '#b8453c'
                    }
                },
                series: [{
                    name: 'chosenMetricName',
                    type: 'scatter',
                    coordinateSystem: 'geo',
                    symbolSize: function(val) {
                        return Math.min((val[2] / response.max * 25), 25);
                    },
                    data: geoData
                }]
            });
        }
    });
}

// themeriver
function getRiverData() {
    $.ajax({
        url: 'http://api.ziggy.ovh/river?metric=' + filterMetric + '&fromDate=' + fromDate + '&toDate=' + toDate +
            '&granularity=week&dimension=' + topDimensionRiver +
            '&fDimension=' + filterDimension + '&fValue=' + filterValue,
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            riverData = response.river
            legendData = []
            riverData.forEach(function(value, index, array) {
                if (legendData.indexOf(value[2]) < 0) {
                    legendData.push(value[2])
                }
            })
            riverChart.hideLoading()
            riverChart.setOption({
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'line',
                        lineStyle: {
                            color: 'rgba(0,0,0,0.2)',
                            width: 1,
                            type: 'solid'
                        }
                    }
                },
                legend: {
                    data: legendData,
                    textStyle: {
                        fontSize: 9
                    }
                },
                singleAxis: {
                    axisTick: {},
                    axisLabel: {
                        textStyle: {
                            fontSize: 10
                        }
                    },
                    type: 'time',
                    splitLine: {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            opacity: 0.2
                        }
                    }
                },
                series: [{
                    type: 'themeRiver',
                    label: [],
                    name: filterMetricName,
                    data: riverData
                }]
            });
        }
    });
}

getAllData();

function getAllData() {
    lineChart.showLoading()
    pieChart.showLoading()
    mapChart.showLoading()
    riverChart.showLoading()
    getLineData();
    getPieData();
    getMapData();
    getRiverData();
}
// resizing

window.onresize = function() {
    pieChart.resize();
    lineChart.resize();
    mapChart.resize();
    riverChart.resize();
};


// filtering
$(function() {
    $("#date-from").datepicker({
        onSelect: function(date) {
            fromDate = date;
            getAllData();
        },
        dateFormat: 'yy-mm-dd',
        changeMonth: true,
        changeYear: true,
        yearRange: '2015:2016',
        defaultDate: new Date(2015, 00, 01),
        firstDay: 1
    });
    $("#date-to").datepicker({
        onSelect: function(date) {
            toDate = date;
            getAllData();
        },
        dateFormat: 'yy-mm-dd',
        changeMonth: true,
        changeYear: true,
        yearRange: '2015:2016',
        defaultDate: new Date(2016, 11, 31),
        firstDay: 1
    });
});

// get the dimensions we can filter by, from the API
$(document).ready(
    function() {
        $.ajax({
            url: 'http://api.ziggy.ovh/dimensions',
            type: 'GET',
            dataType: 'json',
            success: function(response) {
                dims = response.dimensions
                dims.forEach(function(elem, index) {
                    $("#sel-filter-dim").append($("<option />").val(index).text(elem));
                    $("#sel-pie-dimension").append($("<option />").val(index).text(elem));
                    // river plot is strange by city...
                    if (elem.indexOf('CIUDAD') < 0)
                        $("#sel-river-dimension").append($("<option />").val(index).text(elem));
                });
            }
        });
    }
);
$("#sel-pie-dimension").val(0)
$("#sel-river-dimension").val(0)

// if dimension is selected, get 50 top values for it in the dateset
// and fill corresponding select
$('#sel-filter-dim').on('change', function() {
    if ($('#sel-filter-dim option:selected').text() == 'Sin filtro') {
        $('#sel-filter-val').html('').append($("<option />").val(0).text('-'))
        filterDimension = ''
        filterValue = ''
        getAllData();
    } else {
        $('#sel-filter-val').html('');
        filterDimension = $('#sel-filter-dim option:selected').text();
        $.ajax({
            url: 'http://api.ziggy.ovh/top?metric=count&fromDate=' + fromDate + '&toDate=' + toDate +
                '&granularity=' + granularity + '&dimension=' + filterDimension +
                '&fDimension=&fValue=&limit=50',
            type: 'GET',
            dataType: 'json',
            success: function(response) {
                values = response.values
                filterValue = values[0].name
                values.forEach(function(elem, index) {
                    $("#sel-filter-val").append($("<option />").val(index).text(elem.name));
                });
                getAllData();
            }
        });
    }
})

// fill metric and granularity selects
i = 0
for (var k in metrics) {
    $("#sel-metric").append($("<option />").val(i).text(k));
    i++;
}

i = 0
for (var k in granularities) {
    $("#sel-line-granularity").append($("<option />").val(i).text(k));
    i++;
}

// when value from selects changes, call the api
$('#sel-filter-val').on('change', function() {
    filterValue = $('#sel-filter-val option:selected').text()
    getAllData()
});

$("#sel-line-granularity").val(1);
$('#sel-line-granularity').on('change', function() {
    granularity = granularities[$('#sel-line-granularity option:selected').text()]
    lineChart.showLoading()
    getLineData();
});

$('#sel-pie-dimension').on('change', function() {
    topDimensionPie = $('#sel-pie-dimension option:selected').text()
    pieChart.showLoading()
    getPieData();
});

$('#sel-river-dimension').on('change', function() {
    topDimensionRiver = $('#sel-river-dimension option:selected').text()
    riverChart.showLoading()
    getRiverData();
});

$('#sel-metric').on('change', function() {
    filterMetricName = $('#sel-metric option:selected').text()
    filterMetric = metrics[filterMetricName]
    if (filterMetric.includes('importe')) {
        extra = '€'
    } else {
        extra = '';
    }
    getAllData()
});