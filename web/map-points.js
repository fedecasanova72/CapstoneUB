var chart = echarts.init(document.getElementById('main-map'));

var date1 = new Date(2015, 00, 01)
var date2 = new Date(2015, 00, 8)
var date3 = new Date(2015, 00, 01)
var interval

var default_option = {
    backgroundColor: '#9fa4ac',
}
chart.setOption(default_option);

var datos = []
var datos2 = []
var datos3 = []

$.get('map/json/map.json', function(vlcJson) {
    echarts.registerMap('vlc', vlcJson);
    chart.setOption({
        geo: [{
            map: 'vlc',
            zoom: 15,
            roam: false,
            center: [-0.359802, 39.475889],
            label: {
                normal: {
                    show: false
                },
                emphasis: {
                    show: true,
                    textStyle: {
                        color: '#fff'
                    }
                }
            },
            animation: true,
            animationDuration: 500,
            animationEasing: 'cubicInOut',
            animationDurationUpdate: 500,
            animationEasingUpdate: 'cubicInOut',
            itemStyle: {
                normal: {
                    color: '#323c48',
                    borderColor: '#111'
                },
                emphasis: {
                    color: '#2a333d'
                }
            }
        }],
        title: {
            text: 'Pagos en Valencia (Cajamar)',
            subtext: '01/01/2015',
            textStyle: {
                color: '#fff'
            },
            subtextStyle: {
                color: '#fff',
                fontSize: 14
            },
            top: 80,
            left: 20
        },
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
        series: [{
            type: 'scatter',
            coordinateSystem: 'geo',
            data: datos
        }]
    });
});

function getDataFromApi(fromDate, toDate) {
    $.ajax({
        url: 'http://api.ziggy.ovh/geopoints?metric=count&fromDate=' + fromDate + '&toDate=' + toDate,
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            datos = datos.concat(response.geoPoints)
            datos2 = datos2.concat(response.geoPointsMid)
            datos3 = datos3.concat(response.geoPointsBig)
        }
    });
}

interval = setInterval(function() {
    checkIfStop(interval, date2);
    getDataFromApi(formatDate(date1), formatDate(date2));

    date1.setDate(date1.getDate() + 7);
    date2.setDate(date2.getDate() + 7);

    datos = datos.map(function(serieData, idx) {
        var plusOrMinus1 = Math.random() < 0.5 ? -1 : 1;
        var plusOrMinus2 = Math.random() < 0.5 ? -1 : 1;
        var px = Math.random() / 350;
        var py = Math.random() / 350;
        var res = [serieData[0] + (plusOrMinus1 * px), serieData[1] + (plusOrMinus2 * py)];
        return res;
    });

    datos2 = datos2.map(function(serieData, idx) {
        var plusOrMinus1 = Math.random() < 0.5 ? -1 : 1;
        var plusOrMinus2 = Math.random() < 0.5 ? -1 : 1;
        var px = Math.random() / 800;
        var py = Math.random() / 800;
        var res = [serieData[0] + (plusOrMinus1 * px), serieData[1] + (plusOrMinus2 * py)];
        return res;
    });

    datos3 = datos3.map(function(serieData, idx) {
        var plusOrMinus1 = Math.random() < 0.5 ? -1 : 1;
        var plusOrMinus2 = Math.random() < 0.5 ? -1 : 1;
        var px = Math.random() / 1500;
        var py = Math.random() / 1500;
        var res = [serieData[0] + (plusOrMinus1 * px), serieData[1] + (plusOrMinus2 * py)];
        return res;
    });

    chart.setOption({
        title: {
            subtext: formatDateBeauty(date2)
        },
        series: [{
            type: 'scatter',
            symbolSize: 1,
            large: true,
            largeThreshold: 1,
            itemStyle: {
                normal: {
                    shadowBlur: 2,
                    shadowColor: 'rgba(37, 140, 249, 0.8)',
                    color: 'rgba(37, 140, 249, 0.8)'
                }
            },
            coordinateSystem: 'geo',
            data: datos
        }, {
            type: 'scatter',
            symbolSize: 1,
            large: true,
            largeThreshold: 1,
            itemStyle: {
                normal: {
                    shadowBlur: 2,
                    shadowColor: 'rgba(14, 241, 242, 0.8)',
                    color: 'rgba(14, 241, 242, 0.8)'
                }
            },
            coordinateSystem: 'geo',
            data: datos2
        }, {
            type: 'scatter',
            symbolSize: 1,
            large: true,
            largeThreshold: 1,
            itemStyle: {
                normal: {
                    shadowBlur: 2,
                    shadowColor: 'rgba(255, 255, 255, 0.8)',
                    color: 'rgba(255, 255, 255, 0.8)'
                }
            },
            coordinateSystem: 'geo',
            data: datos3
        }]
    });
}, 700);

function formatDate(d) {
    month = '' + (d.getMonth() + 1)
    day = '' + d.getDate()
    year = d.getFullYear()

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}

function formatDateBeauty(d) {
    if (d > (new Date(2016, 11, 31))) {
        return ['31', '12', '2016'].join('/');
    }
    month = '' + (d.getMonth() + 1)
    day = '' + d.getDate()
    year = d.getFullYear()

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [day, month, year].join('/');
}

function checkIfStop(interval, date) {
    if (date > (new Date(2016, 11, 31))) {
        clearInterval(interval)
    }
}