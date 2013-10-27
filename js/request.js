$(document).ready(function() {

	function show_results(position) {
		var items = [],
		latitude = position.coords.latitude,
		longitude = position.coords.longitude,
		latitude = 52.49616, // hard-coding Berlin, since the wi-fi routers think we're in Ireland
		longitude = 13.45396, // same here
		fsURL = 'https://api.foursquare.com/v2/venues/trending?ll=' + latitude + ', ' + longitude + '&limit=30&radius=100000&client_id=Z34RZK1SCL3I0BH4HGQTEWEYIKCFEYAX4MZZFDWSVIJHIKYT&v=20131026&client_secret=VZLBRPB4FMSFIZ4PFY1UT0DZ1JSHPSPWVMT5YN2CVNFMICBT';
		console.log(fsURL);
		$.getJSON( fsURL, function(data) {

			$.each( data.response.venues, function(i, venues) {
				var distance = venues.location.distance,
					hereCount = venues.hereNow.count,
					temp = hotness();
				
				function hotness() {
										if (hereCount >= 0 && hereCount <= 5 ) { return 'cold'; }
										else if (hereCount >= 6 && hereCount <= 10 ) { return 'medium'; }
										else if (hereCount >= 11 && hereCount <= 15 ) { return 'active'; }
										else { return 'burning'; }
									}

				items.push( '<li class="event">',
						    	'<div class="venueType"><img id="' + venues.categories[0].id + '" alt="' + venues.categories[0].pluralName + '" src="' + venues.categories[0].icon.prefix + '64' + venues.categories[0].icon.suffix + '"/></div>',
						    	'<div class="eventInfo">',
						    		'<h2>' + venues.name + '</h2>',
						    		'<div class="temperature">',
						    			'<span class="' + temp + '">',
						    				'<i class="fa fa-circle"></i>',
						    			'</span>',
						    			'<span class="numCheckins">' + venues.stats.checkinsCount + ' Check-ins</span>',
						    		'</div><!-- .temperature -->',
						    		'<div class="distanceFromMe">',
						    			'<span>' + distance +  ' meters away</span>',
						    		'</div> <!-- where does this go? -->' ,
						    	'</div><!-- .eventInfo -->',
					    		'<div class="moreArrow"><i class="fa fa-angle-right"></i></div>',
							'</li>'
				);

			});
				
			$('<ul/>', { 'class': 'results-list', html: items.join('')}).appendTo('#giveMeDataBwaHaHa');
			
		});
	
	}
	
	function get_location() {
	  if (Modernizr.geolocation) {
	    navigator.geolocation.getCurrentPosition(show_results);
	  } else {
	    //alert('As it turns out, there\'s no way to do location-based services without location data. So, reload the page and enable the software to determine your location.');
	  }
	}
	
	get_location();






});