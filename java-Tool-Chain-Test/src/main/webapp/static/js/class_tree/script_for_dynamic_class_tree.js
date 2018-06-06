	

	function call_dynamic_class_tree(flag) {	
		treeInitialization();		
		var firstObj=null;
		var position = null;
		treeFlag = flag;
		
		classHierarchy = JSON.parse(JSON.stringify(dynamicHierarchy));
		position = "dynamicClassTree";
		
		console.log("1st	",classHierarchy);				
		console.log("Position is :: ", position);		
		vis = d3.select("#"+position).append("svg:svg")
		.attr("width", w)
		.attr("height", h)
		.append("svg:g")
		.attr("transform", "translate(60,0)");		
		
		d3.json("ForIEFix",function(json) {		
		  json = JSON.parse(JSON.stringify(classHierarchy));		
		  json.x0 = 800;
		  json.y0 = 0;		  
		
		console.log("2nd	",classHierarchy);		
		console.log("3rd	",json);
			
		function toggle(d) {
			if (d.children) {
				d._children = d.children;
				d.children = null;
			}else{
				d.children = d._children;
				d._children = null;
			}
		} 
		
		tree.nodes(json).forEach(function(n) {
			if(firstObj==null && n.depth==1)
				firstObj=n;				
			n.depth==0? n:n; 
		});		
		dynamic_update(root = json);	
		dynamic_click(firstObj);
		dynamic_click(firstObj);
		});
		
		/* Flor field glass there is no "Cisco Pre-Existing Property" */
		setTimeout(function () { 
			if((contractType == "MP - FG") || (contractType == "MS - FG")){
				document.getElementById("footNotePopup").innerHTML += "Cisco Pre-Existing Property is not applicable to Field-Glass & hence not included in score calculation";
			}
		}, 1000);
		
	}
	
	
	function dynamic_update(source) {
		var  i =0;
	   var nodes = tree.nodes(root).reverse();
	  /* Compute the new tree layout. */
	  
	  /* Update the nodes…  */
		var node = vis.selectAll("g.node")
		  .data(nodes, function(d) { return d.id || (d.id = ++i); });

		var nodeEnter = node.enter().append("svg:g")
			.attr("class", "node")
			.attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
			.attr("list",KPIList);
			 
	  /* Enter any new nodes at the parent's previous position.  */
	 
		nodeEnter.append("svg:circle")
		  .attr("r", 7.5)
		  .style("fill", color_assign_dynamic)  //added
		  .style("stroke", color_assign_dynamic)
		  .on("click", dynamic_click);
		
			
		/* This is the code for entering text.  */
		nodeEnter.append("svg:text")
			.attr("font-size","13px")    
			.style("font-size", "13px") 			
			.attr("x", function(d) {
			return d._children ? 10 : (d.layer==0? -45:15); 
			})
			.attr("y",function(d) {
			return d._children ? 5: (d.layer==0? 5:5); 
			})
			.text(function(d) { return d.name; })
			.on("click", dynamic_class_info);			
		

	  /* Transition nodes to their new position.*/
		nodeEnter.transition()
			.duration(duration)
			.attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
			.style("opacity", 1)     
			.select("text")			
			.style("font-weight", function(d){
				if(d.layer==0) {
					return 700;
				}else {
					return 400;
				}
			})
			.style("fill","BLACK");		
		  
		node.transition()
		  .duration(duration)
		  .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
		  .style("opacity", 1);
		

		node.exit().transition()
		  .duration(duration)
		  .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
		  .style("opacity", 1e-6)
		  .remove();

		/* Update the links…  */
		var link = vis.selectAll("path.link")
		  .data(tree.links(nodes), function(d) { return d.target.id; });

	  /* Enter any new links at the parent's previous position.  */
		link.enter().insert("svg:path", "g")
		  .attr("class", "link")
		  .attr("d", function(d) {
			var o = {x: source.x0, y: source.y0};
			return diagonal({source: o, target: o});
		  })
		.transition()
		  .duration(duration)
		  .attr("d", diagonal);

	  /* Transition links to their new position. */
		link.transition()
		  .duration(duration)
		  .attr("d", diagonal);

	  /* Transition exiting nodes to the parent's new position. */
		link.exit().transition()
		  .duration(duration)
		  .attr("d", function(d) {
			var o = {x: source.x, y: source.y};
			return diagonal({source: o, target: o});
		  })
		  .remove();

	  /* Stash the old positions for transition. */
		nodes.forEach(function(d) {
		  d.x0 = d.x;
		  d.y0 = d.y;	  
		});
		
	} 
		  
	function color_assign_dynamic(d) {		
		var greenClassList = [];		
		for(i in dynamicResult){
			if(dynamicResult[i] == true){
				greenClassList.push(i);
			}
		}
		console.log("Inside dynamic :: color_assign_dynamic");
		var lay = d.layer;	
		if(lay == 1) {		
			var children = classHierarchy["children"];
			var count = 0;
			for(var i in children){
				if(children[i]["name"] == d.name){					
					if(dynamicResult[d.name] == true){						
						return "GREEN";
					}else{
						dynamicRedFlag = "RED";
						return "RED";
					}
				}
			}		
		}else {
			if(dynamicRedFlag == "RED"){
				return "RED";
			}else {
				return "GREEN";
			}
			
		} 
	} 		
		
	function dynamic_toggle_g(d) {
		if (d.children) {
			d._children = d.children;
			d.children = null;
		}
	}  
	
	function dynamic_click(d) {
		if (d.children) {
			d._children = d.children;
			d.children = null;
		}else {
			d.children = d._children;
			d._children = null;			
			dynamic_class_info(d);
		}	  
		d.parent.children.forEach(function(element) {
			console.log(element);
			if (d !== element) {
				dynamic_toggle_g(element);
			}
		});	
		dynamic_update(d);
	}  

	
	function dynamic_class_info(d){	
		console.log(d);
		var lay = d.layer;	
		var textContent = "";
		contentSpaceID = "dynamicClassContentPopup";
		jsonHelpData = dynamicHelpClasses;
		contentText = dynamicPopUpContent;		
		
		if(lay == 1) {			
			if(d.name == "Scope"){
				showContentForScope(d,contentSpaceID,jsonHelpData,contentText);
			}
			else if((d.name == "WorkProduct")&&((contractType =="MS - FG")||(contractType =="MP - FG"))){
				showContentForWorkProduct(d,contentSpaceID,jsonHelpData,contentText);
			}
			else if((d.name == "SupplierKeyPersonnel")&&((contractType =="MS - FG")||(contractType =="MP - FG"))){
				showContentForSupplierKeyPersonnel(d,contentSpaceID,jsonHelpData,contentText);
				}
			else { 
				var div = document.getElementById(contentSpaceID);			
				div.innerHTML = "";
				div.innerHTML += "<button type='button' class='close' aria-label='Close'> <span aria-hidden='true'>&times;</span></button>";
				div.innerHTML += "<strong>" + d.name + ":"+"</strong>" + "<br>";
				var temp_list = jsonHelpData[d.name];
				for(var i in temp_list) {
					div.innerHTML += temp_list[i]["text"] + "<br>";
					div.innerHTML += "<br>";
					div.innerHTML += "<strong>" + "Reference:" + "</strong>" + temp_list[i]["ref"] + " ";
					//div.innerHTML += "<a href=\"" + temp_list[i]["href"] + "\">" + "Link" + "</a>" + "<br>";
					div.innerHTML += "<a href='#' onClick='window.open(\"./static/help.html\", \"_blank\")'>" + "Link" + "</a>" + "<br>";
					//div.innerHTML += "<hr>" + "<br>";
					div.innerHTML += "<hr>";
				}	
				
				console.log("==========classesContent============\n",contentText);
				console.log("d.name = ",d.name);
				
				if (contentText[d.name].length > 0) {
					div.innerHTML += "<ol>";
					var list = contentText[d.name];
					console.log("========= list ======= \n ",list);				
					textContent += "<li>" + list + "</li>" + "<br>";					
					div.innerHTML += textContent;
					div.innerHTML += "</ol>";	
				}							
			}			
			$("#"+contentSpaceID).show();	
		}
	}

	
	function showContentForScope(d,contentSpaceID,jsonHelpData,contentText){
		var div = document.getElementById(contentSpaceID);			
		div.innerHTML = "";
		div.innerHTML += "<button type='button' class='close' aria-label='Close'> <span aria-hidden='true'>&times;</span></button>";
		div.innerHTML += "<strong>" + d.name + ":"+"</strong>" + "<br>";
		var temp_list = jsonHelpData[d.name];
		for(var i in temp_list) {
			div.innerHTML += temp_list[i]["text"] + "<br>";
			div.innerHTML += "<br>";
			div.innerHTML += "<strong>" + "Reference:" + "</strong>" + temp_list[i]["ref"] + " ";
			div.innerHTML += "<a href='#' onClick='window.open(\"./static/help.html\", \"_blank\")'>" + "Link" + "</a>" + "<br>";
			div.innerHTML += "<hr>";
		}
		
		var obj = JSON.parse(contentText[d.name]);
		for(var key in obj){
			div.innerHTML +="<hr>";
			div.innerHTML += "<ol>";
			if(key == "WHO"){
				div.innerHTML += "<strong>" + key + ": (Optional)"+"</strong>" + "<br>";
			}else {
				div.innerHTML += "<strong>" + key + ":"+"</strong>" + "<br>";
			}
			
			if(obj[key].length > 0){
				div.innerHTML +="<li>" +obj[key]+ "</li>";
			}
			
			div.innerHTML += "</ol>";	
			
		}		
	} 
	 
	function showContentForWorkProduct(d,contentSpaceID,jsonHelpData,contentText){
		var div = document.getElementById(contentSpaceID);			
		div.innerHTML = "";
		div.innerHTML += "<button type='button' class='close' aria-label='Close'> <span aria-hidden='true'>&times;</span></button>";
		div.innerHTML += "<strong>" + d.name + ":"+"</strong>" + "<br>";
		var temp_list = jsonHelpData[d.name];
		for(var i in temp_list) {
			div.innerHTML += temp_list[i]["text"] + "<br>";
			div.innerHTML += "<br>";
			div.innerHTML += "<strong>" + "Reference:" + "</strong>" + temp_list[i]["ref"] + " ";
			div.innerHTML += "<a href='#' onClick='window.open(\"./static/help.html\", \"_blank\")'>" + "Link" + "</a>" + "<br>";
			div.innerHTML += "<hr>";
		}
		
		var obj = JSON.parse(contentText[d.name]);
		//var obj = contentText[d.name]
		for(var key in obj){
			div.innerHTML +="<hr>";
			div.innerHTML += "<ol>";
			div.innerHTML += "<strong>" + key + ":"+"</strong>" + "<br>";
			
			if(obj[key].length > 0){
				div.innerHTML +="<li>" +obj[key]+ "</li>";
			}
			
			div.innerHTML += "</ol>";	
			
		}		
	}
	
	function showContentForSupplierKeyPersonnel(d,contentSpaceID,jsonHelpData,contentText){
		var div = document.getElementById(contentSpaceID);			
		div.innerHTML = "";
		div.innerHTML += "<button type='button' class='close' aria-label='Close'> <span aria-hidden='true'>&times;</span></button>";
		div.innerHTML += "<strong>" + d.name + ":"+"</strong>" + "<br>";
		var temp_list = jsonHelpData[d.name];
		for(var i in temp_list) {
			div.innerHTML += temp_list[i]["text"] + "<br>";
			div.innerHTML += "<br>";
			div.innerHTML += "<strong>" + "Reference:" + "</strong>" + temp_list[i]["ref"] + " ";
			div.innerHTML += "<a href='#' onClick='window.open(\"./static/help.html\", \"_blank\")'>" + "Link" + "</a>" + "<br>";
			div.innerHTML += "<hr>";
		}
		
		var obj = JSON.parse(contentText[d.name]);
		for(var key in obj){
			div.innerHTML +="<hr>";
			div.innerHTML += "<ol>";
			div.innerHTML += "<strong>" + key + ":"+"</strong>" + "<br>";
			
			if(obj[key].length > 0){
				div.innerHTML +="<li>" +obj[key]+ "</li>";
			}
			
			div.innerHTML += "</ol>";	
			
		}		
	}
	