$(document).ready(function() {

	

	function show_results(position) {
		var items = [],
		latitude = position.coords.latitude,
		longitude = position.coords.longitude,
		latitude = 52.49616, // hard-coding Berlin, since the wi-fi routers think we're in Ireland
		longitude = 13.45396, // same here
		fsURL = 'https://api.foursquare.com/v2/venues/trending?ll=' + latitude + ', ' + longitude + '&limit=30&radius=100000&client_id=Z34RZK1SCL3I0BH4HGQTEWEYIKCFEYAX4MZZFDWSVIJHIKYT&v=20131026&client_secret=VZLBRPB4FMSFIZ4PFY1UT0DZ1JSHPSPWVMT5YN2CVNFMICBT';
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
	    
	  }
	}
	
	get_location();

	
$("#giveMeDataBwaHaHa").on('click', '.moreArrow', function(){
		var detailsURL = 'https://api.foursquare.com/v2/venues/4ae778a5f964a520a5ab21e3?client_id=Z34RZK1SCL3I0BH4HGQTEWEYIKCFEYAX4MZZFDWSVIJHIKYT&v=20131027&client_secret=VZLBRPB4FMSFIZ4PFY1UT0DZ1JSHPSPWVMT5YN2CVNFMICBT',
			items = [],
			$this = $(this);
		$.getJSON( detailsURL, function(data) {

				var venue = data.response.venue,
					name = venue.name,
					hn = venue.hereNow.count;
					distance = '5';
					category = venue.categories[0].name;
					phone = venue.contact.phone || 'No phone number provided';
					url = venue.url || 'No URL provided';
					rating = venue.rating || 'No ratings for this venue';
					checkins = venue.stats.checkinsCount || 'No check-ins yet for this venue';
					directions = 'Get Directions';
					address = venue.location.address || 'No address listed for this venue';
					postalCode = venue.location.postalCode || 'No postal code';
					city = venue.location.city || 'No city';
					country = venue.location.country || 'No country';
					like = venue.like;
					dislike = venue.dislike;
				items.push( '<h3>' + category + '</h3>',
							'<div class="venueContact">' + phone + '</div>',
							'<div class="venueContact">' + url + '</div>',
							'<div class="venueDeets"><span class="redBox">' + Math.round(rating) + '</span> out of 10 people like this place</div>',
							'<div class="venueDeets"><span class="redBox">' + hn + '</span> People are here now</div>',
							'<div class="venueDeets"><span class="redBox">' + checkins + '</span> Check-ins</div>',
							'<a class="getDirections" href="">' + directions + '</a>',
							'<div class="venueDeets"><i class="fa fa-map-marker"></i>' + address + ', ' + postalCode + ' ' + city + ', ' + country + '</div>'

					);
			
			$this.after($('<div/>', { 'class': 'venue-info', html: items.join('')}));
			
		});
	});



});