jQuery(window).on('load', function () {
    var panZoom = svgPanZoom('#svgDocument', {
        zoomEnabled: true,
        controlIconsEnabled: true,
        fit: true,
        center: true,
        minZoom: 0.5,
        maxZoom: 15,
        zoomScaleSensitivity: 0.5
    });

    jQuery(window).resize(function () {
        panZoom.resize();
        panZoom.fit();
        panZoom.center();
    });
});