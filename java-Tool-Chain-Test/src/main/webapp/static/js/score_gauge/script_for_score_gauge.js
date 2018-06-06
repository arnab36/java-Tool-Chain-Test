						
								
			// The following function will set the mandatory score result image
			function set_mandatory_score(userJsonData){			
				$('#mandatoryScorePic').empty();
				
				var class_name,check_flag,mandatory_flag,set_flag = true,temp_list = [];
				
				// Checking whther any mandatory class is missing
				for(var key in userJsonData) {
					temp_list = userJsonData[key];
					for(var i in temp_list) {
						class_name = temp_list[i]["class_name"];
						mandatory_flag = temp_list[i]["mandatory"];
						if(mandatory_flag) {
							// This function is in score_card.js
							check_flag = checkINKPIList(class_name);
							if(!check_flag){
								set_flag = false;
								
							}
						}
					}
				}
				
				// Depending on the set_flag we set the image
				var elem;
				if(set_flag){
					elem = './static/img/mandatory-class_green.png';
				}else {
					elem = './static/img/mandatory-class_red.png';
				}					
				document.getElementById("mandatoryScorePic").innerHTML = '<img alt="mandatory" src="'+elem+'">';
				console.log(elem);
			}
			
			
			// According to cost it will return the red_Max value
			function get_red_max(cost){
				var red = 0;
				var cost_table = [];				
				cost_table = strategyInfo["score_table"];
				var temp_difference, difference=10000000000;
				for(var i in cost_table) {					
					temp_difference = Math.abs(cost - cost_table[i]["Cost"]);
					if(temp_difference < difference){
						red = cost_table[i]["Red_Max"];
						difference = temp_difference;
					}
				}				
				return red;
			}
			
			// According to cost it will return the yellow_Max value
			function get_yellow_max(cost){
				var yellow = 0;
				var cost_table = [];			
				
				cost_table = strategyInfo["score_table"];
				var temp_difference, difference=10000000000;
				for(var i in cost_table) {					
					temp_difference = cost - cost_table[i]["Cost"];
					if(temp_difference < difference){
						yellow = cost_table[i]["Yellow_Max"];
						difference = temp_difference;
						console.log("difference = ", difference);
						console.log("yellow = ", yellow);
					}
				}		
				return yellow;
			}
			
			
			// Drawing the score Gauge
			function render_curve(displayScore,red, yellow,scoreCardFlag) {	
			
				$("#div1").empty();	
				if(scoreCardFlag){
					document.getElementById("div1").innerHTML += '<span class="sow-score-value"> </span>'+
									'<span class="sow-score-label"> </span>'+
									'<div class="footer-btn">'+
										'<a href="#" class="top" data-toggle="modal" data-target="#scoreModal">View Score-Card</a>'+
									'</div>	';
				}
				
				var path,text,svgContainer,svg_score;	
				var dataset = ["SOW Score"];		
				var red_max = 0;
				var yellow_max = 0;
				
				if(red == ""){
					red_max = parseInt(get_red_max(strategyCost));
				}else {
					red_max = parseInt(red);
				}
				
				if(yellow == ""){
					red_max = parseInt(get_yellow_max(strategyCost));
				}else {
					red_max = parseInt(yellow);
				}			
			
				console.log("Typ yellow", typeof(yellow_max));				
				var score = parseInt(displayScore);		
				
				svgContainer = d3_v4.select("#div1")
								.append("svg")
								.attr("width", 250)
								.attr("height", 250);
			
				path = svgContainer.append("path")
								.attr("d", "M 75,200 A 50,50 0 0,1 195,50");
				
				console.log(dataset[0]);				

				//console.log("text object is = ", text);			
				var mid = Math.floor((red_max + yellow_max)/2);		
												
				redraw(score,mid);
			}
			
			function redraw(score,mid) {				
				var index;	// Pointer to the color
				var color_code;	
				var score_data = [];
				score_data[0] = score.toString();
			
				var arc_width = 24;
				var radius_of_dot = 7.5;
			
				var pointer = 0;   // indicating number of points in the curve
				var pointer_1 = 0;
				
				var score_point;  // Point on the curve corresponding to the score

				var score1 = score;
				if(score >= 100){
					score = 99;
				}
				
				if(score == 0){
					score = 1;
				}
							
				var color = d3_v4.scaleLinear()
					.domain([0, mid, 100])
					.range(["red", "yellow", "green"]);
				
				//console.log("times = ", times);
				path = d3_v4.selectAll("path").remove();	
								
				// last argument in lineJoin() is the width of the arc/path				
				d3_v4.select("svg").selectAll("path")
					.data(quads(samples(path.node(), 2)))
					.enter().append("path")				
					.style("fill", function(d) { 								
									pointer += 1;
									index = Math.round(pointer/1.51);	// There are 134 points  on the curve. Calculated manually. Score range is 0-100. Ratio = 134/100 = 1.3		
									if(index == score) {
										score_point = d;  // Getting the score point on the curve
										//console.log("score_point found", score_point);
									}								
									return color(index);								
									})				
					.style("stroke", function(d) {
										pointer_1 += 1;
										index = Math.round(pointer_1/1.51);									
										return color(index);											
										})				
					.attr("d", function(d) { return lineJoin(d[0], d[1], d[2], d[3], arc_width); });

									
				console.log("text is = ", d3_v4.select("svg").selectAll("text"));
				
				//text = d3.select("svg").selectAll("text").remove();
				
				var text_data = [
							{"px":135, "py":150, "data":"SOW Score", "size": 14},//SOW Score
							{"px":135, "py":125, "data":score1.toString(), "size": 40}//score1.toString()
				];		
				
				console.log("Score data = ", score_data);
				d3_v4.select("svg").selectAll("text")
					.data(text_data)
					.enter()
					.append("text")					
					.attr("x", function(d) { return d.px; })
					.attr("y", function(d) { return d.py; })
					.text(function(d){
						console.log("Score is", d.data);											
						return d.data;
					})					
					.style("color","#656565")					
					.style("font-size",function(d) { 
						console.log("Size is", d.size);		
						console.log("size is", d.size);		
						return d.size;						
						})
					
					//.attr("font-family","roboto")
					.attr("text-anchor","middle");	
					
				
				
				var first_cor = [];
				var second_cor = [];
				var third_cor = [];
				var fourth_cor = [];		
				
				first_cor.push([score_point[0][0],score_point[0][1]]);	
				second_cor.push([score_point[1][0],score_point[1][1]]);	
				third_cor.push([score_point[2][0],score_point[2][1]]);	
				fourth_cor.push([score_point[3][0],score_point[3][1]]);	
				
				var mid_cor = [];
				var x_cor = (score_point[0][0] + score_point[1][0] + score_point[2][0] + score_point[3][0])/4;
				var y_cor = (score_point[0][1] + score_point[1][1] + score_point[2][1] + score_point[3][1])/4;
				mid_cor.push([x_cor,y_cor]);
				
				var start_cor = [];
				start_cor.push([75,200]);   // arc starts at this point
				
				var end_cor = [];
				end_cor.push([195,50]);    // arc moves to this point= (120+75, 200-150)	
				
				var center_cor = [];
				center_cor.push([135,125]);     // (195+75)/2 , (200+50)/2
				
				var score_circle_center = calculate_center(center_cor,mid_cor,arc_width,radius_of_dot);
				
			// Drawing the circle by putting one of the score points as center	
			
				d3_v4.select("body")
					.select("svg")				
					.append("circle")
					.data(score_circle_center)
					.attr("cx", function(d){
						//console.log(d);					
						return Math.floor(d[0]);
						})
					.attr("cy", function(d){
						//console.log(d);					
						return Math.floor(d[1]);
						})
					.attr("r", radius_of_dot)
					.style("fill", "blue");
					
			}
			
			
			// Sample the SVG path uniformly with the specified precision.
			function samples(path, precision) {
			  var n = path.getTotalLength(), t = [0], i = 0, dt = precision;
			  while ((i += dt) < n) t.push(i);
			  t.push(n);
			  return t.map(function(t) {
				var p = path.getPointAtLength(t), a = [p.x, p.y];
				a.t = t / n;
				return a;
			  });
			}
			
			// Compute quads of adjacent points [p0, p1, p2, p3].
				function quads(points) {			
				  return d3_v4.range(points.length - 1).map(function(i) {
					var a = [points[i - 1], points[i], points[i + 1], points[i + 2]];
					a.t = (points[i].t + points[i + 1].t) / 2;
					return a;
				  });
				}

			// Compute stroke outline for segment p12.
			function lineJoin(p0, p1, p2, p3, width) {
			  var u12 = perp(p1, p2),
				  r = width / 2,
				  a = [p1[0] + u12[0] * r, p1[1] + u12[1] * r],
				  b = [p2[0] + u12[0] * r, p2[1] + u12[1] * r],
				  c = [p2[0] - u12[0] * r, p2[1] - u12[1] * r],
				  d = [p1[0] - u12[0] * r, p1[1] - u12[1] * r];

			  if (p0) { // clip ad and dc using average of u01 and u12
				var u01 = perp(p0, p1), e = [p1[0] + u01[0] + u12[0], p1[1] + u01[1] + u12[1]];
				a = lineIntersect(p1, e, a, b);
				d = lineIntersect(p1, e, d, c);
			  }

			  if (p3) { // clip ab and dc using average of u12 and u23
				var u23 = perp(p2, p3), e = [p2[0] + u23[0] + u12[0], p2[1] + u23[1] + u12[1]];
				b = lineIntersect(p2, e, a, b);
				c = lineIntersect(p2, e, d, c);
			  }

			  return "M" + a + "L" + b + " " + c + " " + d + "Z";
			}
			
		// Compute intersection of two infinite lines ab and cd.
			function lineIntersect(a, b, c, d) {
			  var x1 = c[0], x3 = a[0], x21 = d[0] - x1, x43 = b[0] - x3,
				  y1 = c[1], y3 = a[1], y21 = d[1] - y1, y43 = b[1] - y3,
				  ua = (x43 * (y1 - y3) - y43 * (x1 - x3)) / (y43 * x21 - x43 * y21);
			  return [x1 + ua * x21, y1 + ua * y21];
			}

			// Compute unit vector perpendicular to p01.
			function perp(p0, p1) {
			  var u01x = p0[1] - p1[1], u01y = p1[0] - p0[0],
				  u01d = Math.sqrt(u01x * u01x + u01y * u01y);
			  return [u01x / u01d, u01y / u01d];
			}
			
			
			/* 
				The following function will take center of arc, score_point avg i.e mid_cor, radius of the circle
				and will calculate the center of the circle using (m.X2 + n.X1)/(m+n) formula.
			*/
			function calculate_center(center_cor,mid_cor,arc_width,radius_of_dot) {
				var dist_btw_center_to_arc = calculate_distance(center_cor,mid_cor);
				var n = (arc_width/2) + radius_of_dot;
				var m = dist_btw_center_to_arc - n;
				
				console.log("m = ", m);
				console.log("n = ", n);
				
				var h = (m * mid_cor[0][0] + n * center_cor[0][0])/(m + n);
				var k = (m * mid_cor[0][1] + n * center_cor[0][1])/(m + n);
				
				var center = [];
				center.push([h,k]);				
				
				//console.log("center = ", center);
				
				return center;				
			}
			
			// Calculates the distance between two points.
			function calculate_distance(center_cor,mid_cor) {
				var a = (center_cor[0][0] - mid_cor[0][0]) * (center_cor[0][0] - mid_cor[0][0]);
				var b = (center_cor[0][1] - mid_cor[0][1]) * (center_cor[0][1] - mid_cor[0][1]);
				//console.log("a = ", a);
				//console.log("b = ", b);
				var dist  = Math.sqrt(a+b);
				//console.log("Points are :: ", center_cor,mid_cor);
				//console.log("Distance is = ", dist);				
				return dist;			
			}
		