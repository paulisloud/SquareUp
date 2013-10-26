$(document).ready(function() {
	var fsURL = 'https://api.foursquare.com/v2/venues/trending?ll=40.7,-74&client_id=Z34RZK1SCL3I0BH4HGQTEWEYIKCFEYAX4MZZFDWSVIJHIKYT&v=20131026&client_secret=VZLBRPB4FMSFIZ4PFY1UT0DZ1JSHPSPWVMT5YN2CVNFMICBT';
	$.getJSON( fsURL, function(data) {
			var items = [],
				distance = '2.4';
			$.each( data.response.venues, function(i, venues) {
				items.push( '<li>',
						    	'<div class="venueType"><i class="fa fa-ticket"></i></div>',
						    	'<div class="eventInfo">',
						    	'<h2>' + venues.name + '</h2>',
						    	'<div class="temperature">',
						    	'<span class="hot"><i class="fa fa-circle"></i></span><span class="numCheckins">' + venues.stats.checkinsCount + ' Check-ins</span>',
						    	'<div class="distanceFromMe"><span>' + distance +  ' km away</span>',
						    	'</div><!-- .temperature -->',
						    	'<div class="moreArrow"><i class="fa fa-angle-right"></i></div>',
						    	'</div><!-- .eventInfo -->',
							'</li>'
							);

			});
				$('<ul/>', { 'class': 'results-list', html: items.join('')}).appendTo('.event');
		});

});