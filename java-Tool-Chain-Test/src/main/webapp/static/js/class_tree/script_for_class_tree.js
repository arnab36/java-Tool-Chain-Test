	

			
	//call_horizontal_tree("STATIC");		
		
	$(document).on("click",'#classContentPopup .close',function(){
		//console.log("Hiding");
		$('#classContentPopup').hide();	
	});
	
	$(document).on("click",'#dynamicClassContentPopup .close',function(){
		//console.log("Hiding");
		$('#dynamicClassContentPopup').hide();	
	});
	
	$(document).on("click",'#staticClassContentPopup .close',function(){
		//console.log("Hiding");
		$('#staticClassContentPopup').hide();	
	});	
	
	var jwheight = 390;
	
    /* $(window).resize(function(){
		console.log("width is :: ", $(window).width());
		console.log("height is :: ", $(window).height());
        /* if ($(window).width() < 992) {
            jwheight = 39;
			console.log("jwheight is :: ", $(window).width());
        }else {
			jwheight += 1;
			console.log("jwheight is :: ", $(window).width());
		} */
  //  }) */
		
	
	
	function treeInitialization(){
		root = null;
		vis = null;
		tree = null;
		tree = d3.layout.tree()
			.size([h, w - 250]);	
	
		// New added code
		diagonal = null;
		diagonal = d3.svg.diagonal()
			.source(function (d) {						
				if (d.source.layer == "0" )
				{					
					return {x: d.source.x, y: d.source.y - 130};
				} 				
				if (d.source.layer == "1" )
				{	
					console.log("Name :: ",d.source.name);
					console.log("Length :: ",d.source.name.length);				
					return {x: d.source.x, y: d.source.y - 100 +  (6 * d.source.name.length)};				
				} 				
				return {x: d.source.x, y: d.source.y};
			})
			.target(function (d) {
				 return {x: d.target.x, y: d.target.y -130};
			})
			.projection(function (d) {
				 return [d.y+ 130 , d.x];
			}); 
		}
	
	
	function toggle(d) {
		if (d.children) {
			d._children = d.children;
			d.children = null;
		}else{
			d.children = d._children;
			d._children = null;
		}
	} 

	
	function call_horizontal_tree(flag) {	
		treeInitialization();		
		var firstObj = null;
		var position = null;
		treeFlag = flag;
		
		if(flag == "STATIC"){
			classHierarchy = JSON.parse(JSON.stringify(staticHierarchy));
			contentText = staticClassesContent;
			position = "staticClassTree";
		}else if(flag == "DYNAMIC"){
			classHierarchy = JSON.parse(JSON.stringify(dynamicHierarchy));		
			position = "dynamicClassTree";
		}else if(flag == "GENERAL"){
			classHierarchy = JSON.parse(JSON.stringify(generalHierarchy));
			contentText = classesContent;
			position = "classTree";
		}else{
			console.log("No Heirarchy is defined in script for_class_tree....");
		}
		
		console.log("Position is :: ", position);
		
		vis = d3.select("#"+position).append("svg:svg")
		.attr("width", w)
		.attr("height", h)
		.append("svg:g")
		.attr("transform", "translate(60,0)");		
		
		d3.json("ForIEFix",function(json){		
		  json = JSON.parse(JSON.stringify(classHierarchy));
		  json.x0 = 800;
		  json.y0 = 0;
						
		tree.nodes(json).forEach(function(n) {
			if(firstObj==null && n.depth==1){
				firstObj=n;		
			}						
			n.depth==0? n:n; 
		});
		
		update(root = json);
		click(firstObj);
		click(firstObj);
		});
	}


	function update(source) {
		var i = 0;
		var greenClassList = [];
		if(treeFlag == "STATIC"){
			greenClassList = staticKPIList;			
		}else {
			greenClassList = KPIList;
		}
		
		console.log("Inside static static_update");
		
		
	   var nodes = tree.nodes(root).reverse();
	  // Compute the new tree layout.
	  console.log("Inside static static_update 1");
	  
	  console.log("vis is ",vis);
	  console.log("node is ",node);
	  
	  // Update the nodes…
		var node = vis.selectAll("g.node")
		  .data(nodes, function(d) { return d.id || (d.id = ++i); });
		  console.log("Inside static static_update 2");
		  
		console.log("node is ",node);
		  

		var nodeEnter = node.enter().append("svg:g")
			.attr("class", "node")
			.attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
			.attr("list",greenClassList);
		
		console.log("Inside static static_update 3");
			 
	  // Enter any new nodes at the parent's previous position.
	 
		nodeEnter.append("svg:circle")
		  .attr("r", 7.5)
		  .style("fill", color_assign)  //added
		  .style("stroke", color_assign)
		  .on("click", click);
		
		console.log("Inside static static_update 4");
		//console.log(nodeEnter);		
		
		// This is the code for entering text.
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
			.on("click", class_info);			
		
		console.log("Inside static static_update 5");
	  // Transition nodes to their new position.	
		nodeEnter.transition()
			.duration(duration)
			.attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
			.style("opacity", 1)     
			.select("text")
			//.style("font-weight", 700)
			.style("font-weight", function(d){
				if(d.layer==0) {
					return 700;
				}else {
					return 400;
				}
			})
			.style("fill","BLACK");	

		console.log("Inside static static_update 6");
		  
		node.transition()
		  .duration(duration)
		  .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
		  .style("opacity", 1);
		  
		console.log("Inside static static_update 7");
		

		node.exit().transition()
		  .duration(duration)
		  .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
		  .style("opacity", 1e-6)
		  .remove();
		  
		console.log("Inside static static_update 8");

		// Update the links…
		var link = vis.selectAll("path.link")
		  .data(tree.links(nodes), function(d) { return d.target.id; });
		  
		console.log("Inside static static_update 9");
		console.log(link);

	  // Enter any new links at the parent's previous position.
		link.enter().insert("svg:path", "g")
		  .attr("class", "link")
		  .attr("d", function(d) {
			var o = {x: source.x0, y: source.y0};
			return diagonal({source: o, target: o});
		  })
		.transition()
		.duration(duration)
		.attr("d", diagonal);
		  
		console.log("Inside static static_update 10");

	  // Transition links to their new position.
		link.transition()
		  .duration(duration)
		  .attr("d", diagonal);
		  
		console.log("Inside static static_update 11");

	  // Transition exiting nodes to the parent's new position.
		link.exit().transition()
		  .duration(duration)
		  .attr("d", function(d) {
			var o = {x: source.x, y: source.y};
			return diagonal({source: o, target: o});
		  })
		  .remove();
		  
		console.log("Inside static static_update 12");

	  // Stash the old positions for transition.
		nodes.forEach(function(d) {
		  d.x0 = d.x;
		  d.y0 = d.y;	  
		});
		
		console.log("Inside static static_update 13");
		
	  }  
	  
		
	// Alternate code for color assignment
	function color_assign(d) {
		var greenClassList = [];
		
		if(treeFlag == "STATIC"){
			greenClassList = staticKPIList;			
		}else {
			greenClassList = KPIList;
		}
		
		var lay = d.layer;	
		if(lay == 2) {
			for (var i = 0; i < greenClassList.length; i++) {
			if(greenClassList[i] == d.name)
				return "GREEN";
		   }
			return "RED";	 
		}else if(lay == 1) {		
			var children = classHierarchy["children"];
			
			//console.log("1st Level child type "+ typeof(children));
			//console.log("1st Level child  "+ children);
			
			var count = 0;
			for(var i in children){
				//console.log("d.name = "+ d.name);
				//console.log("children[i]['name'] = "+ children[i]["name"]);
				if(children[i]["name"] == d.name){
					var chld = children[i]["children"];
					//console.log("Typeof child : "+ typeof(chld));
					//console.log("chld = ", chld);
					for(var j in chld){
						for (var i = 0; i < greenClassList.length; i++) {
							if(greenClassList[i] == chld[j]["name"]){
								count += 1;
								break;
						   }
						}
					}
					if(chld.length == count){
						first_level_class_color.push("GREEN");
						return "GREEN";
					}else{
						first_level_class_color.push("RED");
						return "RED";
					}
				}
			}		
		}else {
			for(var i in first_level_class_color) {
				if(first_level_class_color[i] == "RED") {
					//console.log("first_level_class_color = ", first_level_class_color[i]);
					return "RED"
				}
			}
			return "GREEN";
		}     
	}
	
	

	// Toggle children on click.
	/* function click(d) {
	  if (d.children) {
		d._children = d.children;
		d.children = null;
	  } else {
		d.children = d._children;
		d._children = null;
		//call function
		class_info(d);
	  }
	 // console.log("Only once 5");
	  update(d);
	} */
	
	function toggle_g(d) {
		if (d.children) {
			d._children = d.children;
			d.children = null;
		}
	}  
	
	function click(d) {
		if (d.children) {
			d._children = d.children;
			d.children = null;
		}else {
			d.children = d._children;
			d._children = null;			
			class_info(d);
		}	  
		d.parent.children.forEach(function(element) {
			console.log(element);
			if (d !== element) {
				toggle_g(element);
			}
		});	
	  update(d);
	} 
	
	
	function class_info(d){	
		var lay = d.layer;	
		var textContent = "";
		
		var contentSpaceID = null;
		if(treeFlag == "STATIC"){
			contentSpaceID = "staticClassContentPopup";
			jsonHelpData = staticHelpClasses;
		}else if(treeFlag == "DYNAMIC"){
			contentSpaceID = "dynamicClassContentPopup";
			jsonHelpData = dynamicHelpClasses;
		}else if(treeFlag == "GENERAL"){
			contentSpaceID = "classContentPopup";
			jsonHelpData  = generalHelpData;
		}else {
			treeFlag = "";
			console.log("Error in tree flag");
		}
		console.log("Content pop up ID :: "+ contentSpaceID);
		
		if(lay == 2) {			
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
			
			//if (contentText[d.name].length > 0) {
			if(contentText.hasOwnProperty(d.name)){
				div.innerHTML += "<ol>";
				list = contentText[d.name];
				console.log("========= list ======= \n ",list);
				for(var j = 0; j < list.length; j++) {
					textContent += "<li>" + list[j] + "</li>" + "<br>";
					//div.innerHTML += "<br>" + list[j] + "</br>";					
				}
				div.innerHTML += textContent;
				div.innerHTML += "</ol>";	
			}
			
			if(treeFlag == "STATIC"){
				var redFlag = true;
				for(var i in staticKPIList) {
					if(staticKPIList[i] == d.name){
						redFlag = false;						
						break;
					}
				}
				
				if(redFlag){ 
					var au = missingStatic["answer_units"];
					for(var i in au) {
						console.log("Color is :: ", d.color);					
						if(d.name == au[i]["CLASS"]){
							div.innerHTML += "<hr>" +"Missing Text <br>";
							div.innerHTML += "<ol>";
							textContent = "";
							var content = au[i]["content"];
							for(var j in content){
								if(content[j]["Found"] == 0 && content[j]["text"] != ""){
									textContent += "<li>" + content[j]["text"] + "</li>" + "<br>";
								}
							}
							div.innerHTML += textContent;
							div.innerHTML += "</ol>";
						}							
					}	
				}		
			}
			
			
					
			$("#"+contentSpaceID).show();	
		}		
	}



	function class_info_p(d){	
		var lay = d.layer;	
		var textContent = "";
		
		var contentSpaceID = null;
		if(treeFlag == "STATIC"){
			contentSpaceID = "staticClassContentPopup";
			jsonHelpData = staticHelpClasses;
		}else if(treeFlag == "DYNAMIC"){
			contentSpaceID = "dynamicClassContentPopup";
			jsonHelpData = dynamicHelpClasses;
		}else if(treeFlag == "GENERAL"){
			contentSpaceID = "classContentPopup";
			jsonHelpData  = generalHelpData;
		}else {
			treeFlag = "";
			console.log("Error in tree flag");
		}
		console.log("Content pop up ID :: "+ contentSpaceID);
		
		if(lay == 2) {			
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
			
			//if (contentText[d.name].length > 0) {
			if(contentText.hasOwnProperty(d.name)){
				div.innerHTML += "<ol>";
				list = contentText[d.name];
				console.log("========= list ======= \n ",list);
				for(var j = 0; j < list.length; j++) {
					textContent += "<li>" + list[j] + "</li>" + "<br>";
					//div.innerHTML += "<br>" + list[j] + "</br>";					
				}
				div.innerHTML += textContent;
				div.innerHTML += "</ol>";	
			}			
			
			
			if(treeFlag == "STATIC"){				
				var redFlag = true;
				for(var i in staticKPIList) {
					if(staticKPIList[i] == d.name){
						redFlag = false;						
						break;
					}
				}
				if(redFlag){
					showMissingText(div);
				}
						
			}
		}		
		$("#"+contentSpaceID).show();	
	}		
	
	
	
	function showMissingText(d,div){
		var au = missingStatic["answer_units"];
		for(var i in au) {
			if(d.color == "RED"){
				div.innerHTML += "<hr>" +"Missing Text <br>";
				div.innerHTML += "<ol>";
				var textContent = "";
				if(d.name == au[i]["CLASS"]){
					var content = au[i]["content"];
					for(var j in content){
						if(content[j]["Found"] == 0 && content[j]["text"] != ""){
							textContent += "<li>" + content[j]["text"] + "</li>" + "<br>";
						}
					}
				}
				div.innerHTML += textContent;
				div.innerHTML += "</ol>";
			}				
		}
	}

	