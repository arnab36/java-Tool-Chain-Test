  
  /*
	The following code uses 'data' variable that has already been set in the previous html page
	from where the .js file is called.
  */
	
	// Words will be scaled within this size
	var min_size,lower;	
	var percent = 1;
	var text_area = Math.ceil(width * height * percent);
	 
	var flag = 0;
	
	// First we find the highest and lowest of data values.
	var L = 1000;
	var H = 0;
	
	
	//console.log("data \n ",data);
	
	function modifyData(wordCloudData){
		for (k in wordCloudData) {			
			if(wordCloudData[k] > 0) {
				modified_data[k] = wordCloudData[k];				
			}
		}	
	}
	
	
	function findLH(modified_data){
		for (k in modified_data) {
			if(modified_data[k] > H) {
				H = modified_data[k];
			}
			if(modified_data[k] < L) {
				L = modified_data[k];
			}
		}	
	}	

	function setMinSizeAndLower(width,height){
		if(width > height) {
			lower = height * .02;
			min_size  = height;		
		}else {
			lower = width * .02;
			min_size = width;
		}	
	
	}
	
	function calculateX(L,H,wordCloudData,modified_data,flag,min_size,text_area,lower){
		var result = 0;
		if(L == H) {
			result = Method_1(L,H,wordCloudData,text_area);
			flag = 1;
		}
		else {		
			result = Method_2(L,H,wordCloudData,text_area);
		}	
		
		result = manually_check(result,lower,H,L,min_size,modified_data,flag);		
		return result;
	}
	

	/*
	The following code will solve an equation for x(upper size scaling limit).
	We are considering,
	 lower font size = 2% of the div size.
	 upper font size = x% of the div size.
	 percent = the % area of the total div that will be used for writing. We have to 
	 guess it because a lot of space will be available after writing and we dont know how 
	 the words will be written.
	
	*/
	
	
	
	/*
	  The following function will manually check whether size of any text word is so bigger 
	  after scaling that it would be clipped out or not. The highest value i.e x will be decreased 
	  if it is found that some text/texts are bigger that the length/breadth(which one is smaller).
	*/
	function manually_check(x,lower,H,L,min_size,modified_data,flag) {		
		var signal,temp;
		if(flag == 0) {
			for(k in modified_data) {
				signal = false;
				temp = 0;
				while(signal == false && x > lower) {
					temp = lower + ((x-lower)* (modified_data[k] - L))/(H-L);
					//console.log("Temp is = "+temp);
					temp = temp * 0.4 * k.length;
					//console.log(temp);
					if(temp >= min_size) {
						//alert("happened for = " + temp + "  " + k + " " + x);
						x = x - 0.2;
					}else {
						signal = true;
					}
				}			
		 }
		}else {
			for(k in modified_data) {
				signal = false;
				temp = 0;
				while(signal == false && x > lower) {
					temp = x * 0.4 * k.length;
					//console.log("Temp is "+temp);
					if(temp >= min_size) {						
						//alert("happened for = " + temp + "  " + k + " " + x);
						x = x - 0.2;
					}else {
						signal = true;
					}
				}
			}
		}
		
		return x;
	}
	
	
	function Method_1(L, H, data, text_area) {
		var Fraction = algebra.Fraction;
		var Expression = algebra.Expression;
		var Equation = algebra.Equation;	
		
		var eq1 = new Expression();
		var expr1  = new Expression();
		
		for (k in data) {	
			if(data[k] > 0) {
			var expr = new Expression("x");		
			expr = expr.multiply(expr);				
			expr = expr.multiply(2);	
			expr = expr.divide(5);	
			expr = expr.multiply(k.length);				
			expr1 = expr1.add(expr);		
			}
		}
		//alert(expr1);
		var eq2 = new Equation(expr1,text_area);
		console.log("Equation is => "+eq2);
		var answer = eq2.solveFor("x");
		
		console.log("x = " + answer);
		console.log(typeof(answer));
		
		var x = answer[1];
		console.log(x);
		return x;		
	}
		
	
	function Method_2_previous(L, H, data, text_area) {
		var Fraction = algebra.Fraction;
		var Expression = algebra.Expression;
		var Equation = algebra.Equation;	
		
		var eq1 = new Expression();
		var expr1  = new Expression();
		for (k in data) {	
			if(data[k] > 0) {
			var expr = new Expression("x");			
			expr = expr.subtract(lower);	
			
			var temp = Math.ceil((data[k] - L) /(H - L));			
			expr = expr.multiply(temp);			
			expr = expr.add(lower);
			expr = expr.multiply(expr);	
			expr = expr.multiply(2);	
			expr = expr.divide(5);		
			expr = expr.multiply(k.length);						
			expr1 = expr1.add(expr);				
			}
		}
	
		var eq2 = new Equation(expr1,text_area);
		console.log("Equation is => "+eq2);
		var answer = eq2.solveFor("x");
		var x = answer[1];
		console.log(x);
		return x;
	}
	
	/*
		Additional try-catch added for handling IE as
		it does not support some of the library functions.
		Returning 80% by default.
	*/
	function Method_2(L, H, data, text_area) {
		var Fraction = algebra.Fraction;
		var Expression = algebra.Expression;
		var Equation = algebra.Equation;			
		
		var eq1 = new Expression();
		var expr1  = new Expression();
		
		for (k in data) {	
			if(data[k] > 0) {
				try {
					var expr = new Expression("x");			
					expr = expr.subtract(lower);	
					
					var temp = Math.ceil((data[k] - L) /(H - L));
					
					expr = expr.multiply(temp);		
					
					expr = expr.add(lower);
					expr = expr.multiply(expr);	
					expr = expr.multiply(2);	
					expr = expr.divide(5);		
					expr = expr.multiply(k.length);
					
					expr1 = expr1.add(expr);
				}catch(err){
					console.log(err);
				}					
			}
		}
		console.log("Expression is => "+expr1);
		var eq2,answer;
		try {
			eq2 = new Equation(expr1,text_area);
			console.log("Equation is => "+eq2);
			answer = eq2.solveFor("x");
			//console.log("x = " + answer);
			//console.log(typeof(answer));
			
			var x = answer[1];
			console.log("x is :: "+x);
			return x;
		}catch(err){
			console.log(err);
		}
		return 80;	
	}
	
   // drawWordCloud(test_string);
	//drawWordCloud();
	

      function drawWordCloud(modified_data){
		  
        var word_count = {};  
		word_count = modified_data;		
		$("#wordCloud").empty();		
        var svg_location = "#wordCloud";      		
        var fill = d3.scale.category20();
        var word_entries = d3.entries(word_count);
		
        var xScale = d3.scale.linear()
           .domain([0, d3.max(word_entries, function(d) {
			//  alert("Second Alert "+d.value);
              return d.value;
            })
           ])
           .range([lower,x]);

        d3.layout.cloud().size([width, height])
          .timeInterval(20)
          .words(word_entries)
          .fontSize(function(d) { 
		 // alert("Third Alert "+ xScale(+d.value));
		  return xScale(+d.value); 
		  })
          .text(function(d) { return d.key; })
          //.rotate(function() { return (~~(Math.random() * 6) - 3) * 30;})
		  .rotate(function() { return 0;})
          .font("Impact")
		 // .spiral("archimedean")
          .on("end", draw)
          .start();

        function draw(words) {
          d3.select(svg_location).append("svg")
              .attr("width", width)
              .attr("height", height)
            .append("g")
              .attr("transform", "translate(" + [width >> 1, height >> 1] + ")")
            .selectAll("text")
              .data(words)
            .enter().append("text")
              .style("font-size", function(d) {
				 // alert("Fourth Alert "+xScale(d.value) + "px");
				  return xScale(d.value) + "px";
				  })
              .style("font-family", "Impact")
              .style("fill", function(d, i) { return fill(i); })
              .attr("text-anchor", "middle")
              .attr("transform", function(d) {
                return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
              })
              .text(function(d) { 
					//alert(d.key); 
					return d.key; });
        }
        d3.layout.cloud().stop();
      }
   