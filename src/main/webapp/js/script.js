jQuery(window).on('load', function () {
    var panZoom = svgPanZoom('#svgDocument', {
        zoomEnabled: true,
        controlIconsEnabled: true,
        fit: true,
        center: true,
        minZoom: 0.5,
        maxZoom: 25,
        zoomScaleSensitivity: 0.5
    });

    jQuery(window).resize(function () {
        var width = jQuery('#svgDocument').width() - 40;

        var oldAttr = jQuery('#print-button', svgDom).attr('transform');
        var newAttr = oldAttr.replace(/translate[(]\d+[ ]/, 'translate(' + (width - 5) + ' ');
        jQuery('#print-button', svgDom).attr('transform', newAttr);

        panZoom.resize();
        panZoom.fit();
        panZoom.center();
    });

    var svgObject = document.getElementById('svgDocument');
    if ('contentDocument' in svgObject) {
        var svgDom = svgObject.contentDocument;
    }

    if (svgDom.getElementById('mnemonicSVG') != null) {
        var svgContent = svgDom.getElementById('mnemonicSVG');
        var width = jQuery("#svgDocument").width() - 40;

        var changeType = document.createElementNS('http://www.w3.org/2000/svg', 'g');
        changeType.setAttribute('id', 'print-button');
        changeType.setAttribute('transform', 'translate(' + (width - 5) + ' 7) scale(0.08)');
        changeType.setAttribute('class', 'svg-pan-zoom-control');
        changeType.addEventListener('click', changeTypeFunction);

        var changeTypeBackground = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        changeTypeBackground.setAttribute('width', '500');
        changeTypeBackground.setAttribute('height', '500');
        changeTypeBackground.setAttribute('class', 'svg-pan-zoom-control-background');
        changeType.appendChild(changeTypeBackground);

        var changeTypePath1 = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        changeTypePath1.setAttribute('d', 'M312.453,199.601c-6.066-6.102-12.792-11.511-20.053-16.128c-19.232-12.315-41.59-18.' +
            '859-64.427-18.859c-31.697-0.059-62.106,12.535-84.48,34.987L34.949,308.23c-22.336,22.379-34.89,52.7-34.91,' +
            '84.318c-0.042,65.98,53.41,119.501,119.39,119.543c31.648,0.11,62.029-12.424,84.395-34.816l89.6-89.6c1.628-1.614,' +
            '2.537-3.816,2.524-6.108c-0.027-4.713-3.87-8.511-8.583-8.484h-3.413c-18.72,0.066-37.273-3.529-54.613-10.581c-3.' +
            '195-1.315-6.867-0.573-9.301,1.877l-64.427,64.512c-20.006,20.006-52.442,20.006-72.448,0c-20.006-20.006-20.006-52.442,' +
            '0-72.448l108.971-108.885c19.99-19.965,52.373-19.965,72.363,0c13.472,12.679,34.486,12.679,47.957,0c5.796-5.801,' +
            '9.31-13.495,9.899-21.675C322.976,216.108,319.371,206.535,312.453,199.601z');
        changeType.appendChild(changeTypePath1);

        var changeTypePath2 = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        changeTypePath2.setAttribute('d', 'M477.061,34.993c-46.657-46.657-122.303-46.657-168.96,0l-89.515,89.429c-2.458,2.47-3.167,' +
            '6.185-1.792,9.387c1.359,3.211,4.535,5.272,8.021,5.205h3.157c18.698-0.034,37.221,3.589,54.528,10.667c3.195,1.315,' +
            '6.867,0.573,9.301-1.877l64.256-64.171c20.006-20.006,52.442-20.006,72.448,0c20.006,20.006,20.006,52.442,0,' +
            '72.448l-80.043,79.957l-0.683,0.768l-27.989,27.819c-19.99,19.965-52.373,19.965-72.363,0c-13.472-12.679-34.486-12.' +
            '679-47.957,0c-5.833,5.845-9.35,13.606-9.899,21.845c-0.624,9.775,2.981,19.348,9.899,26.283c9.877,9.919,21.433,18.008,' +
            '34.133,23.893c1.792,0.853,3.584,1.536,5.376,2.304c1.792,0.768,3.669,1.365,5.461,2.048c1.792,0.683,3.669,1.28,5.461,' +
            '1.792l5.035,1.365c3.413,0.853,6.827,1.536,10.325,2.133c4.214,0.626,8.458,1.025,12.715,1.195h5.973h0.512l5.' +
            '12-0.597c1.877-0.085,3.84-0.512,6.059-0.512h2.901l5.888-0.853l2.731-0.512l4.949-1.024h0.939c20.961-5.265,40.' +
            '101-16.118,55.381-31.403l108.629-108.629C523.718,157.296,523.718,81.65,477.061,34.993z');
        changeType.appendChild(changeTypePath2);

        var changeTypeTitle = document.createElementNS('http://www.w3.org/2000/svg', 'title');
        changeTypeTitle.appendChild(document.createTextNode('Линкованные/Не линкованные'));
        changeType.appendChild(changeTypeTitle);

        svgContent.appendChild(changeType);
    }
});