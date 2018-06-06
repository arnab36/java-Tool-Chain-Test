// This code is for placxe holder

// Get the modal
var modal = document.getElementById('myModal');
var span = document.getElementsByClassName("close")[0];	
console.log("==  >> ",span);

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
	console.log("Inside Close");
    modal.style.display = "none";
	document.getElementById("content").innerHTML = "";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
		document.getElementById("content").innerHTML = "";
    }
}



// This is the code for horinzontal tree
var w = 500,
    h = 500,
    i = 0,
    duration = 500,
    root;

var tree = d3.layout.tree()
    .size([h, w - 160]);

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

var vis = d3.select("#chart").append("svg:svg")
    .attr("width", w)
    .attr("height", h)
    .append("svg:g")
    .attr("transform", "translate(40,0)");


d3.json("./static/js/class_hierarchy_CISCO.json", function(json) {	
  //console.log(json);
  json.x0 = 800;
  json.y0 = 0;
  update(root = json);
});

var first_time = true;
console.log("Only once 0");

function update(source) {
   var nodes = tree.nodes(root).reverse();
  // Compute the new tree layout.
  
  // Update the nodes…
  	var node = vis.selectAll("g.node")
      .data(nodes, function(d) { return d.id || (d.id = ++i); });

	var nodeEnter = node.enter().append("svg:g")
    	.attr("class", "node")
    	.attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
		.attr("list",list_1);
    	 
  // Enter any new nodes at the parent's previous position.
 
  	nodeEnter.append("svg:circle")
      .attr("r", 4.5)
    //  .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; })
	  .style("fill", "lightsteelblue")  //added
      .on("click", click);
	
	console.log(nodeEnter);
	
	// This is the code for entering text.
	nodeEnter.append("svg:text")
      	.attr("x", function(d) {		
		return d._children ? -8 : 8; 
		})
		.attr("y", 2.5)
      	.text(function(d) { return d.name; })
		.on("click", popup_2);	

  // Transition nodes to their new position.
	nodeEnter.transition()
		.duration(duration)
		.attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
      	.style("opacity", 1)     
	    .select("text")
        .style("fill",color_assign);		
      
    node.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
      .style("opacity", 1);
    

	node.exit().transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
      .style("opacity", 1e-6)
      .remove();

    // Update the links…
    var link = vis.selectAll("path.link")
      .data(tree.links(nodes), function(d) { return d.target.id; });

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

  // Transition links to their new position.
    link.transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition exiting nodes to the parent's new position.
    link.exit().transition()
      .duration(duration)
      .attr("d", function(d) {
        var o = {x: source.x, y: source.y};
        return diagonal({source: o, target: o});
      })
      .remove();

  // Stash the old positions for transition.
    nodes.forEach(function(d) {
      d.x0 = d.x;
      d.y0 = d.y;	  
    });
	
  }  
  
console.log("Only once 2"); 


function color_assign(d) {	
	var layer = d.layer;	
	if(layer == 2) {
		for (var i = 0; i < list_1.length; i++) {
		if(list_1[i] == d.name)
			return "GREEN";
	   }
	    return "RED";	 
	}else if(layer == 1) {		
		var children = d.children;		
		for (var i = 0; i < list_1.length; i++) {
			for (var obj in children) {		
				if(children[obj].name == list_1[i]) {
					return "GREEN";					
			    }								
			}
        }	
		return "RED";
	}else {
		return "BLUE";
	}	     
}

// Toggle children on click.
function click(d) {
  if (d.children) {
    d._children = d.children;
    d.children = null;
  } else {
    d.children = d._children;
    d._children = null;
  }
  console.log("Only once 5");
  update(d);
}


function popup(d) {
	layer = d.layer;	
	
	var p1 = 'scrollbars=yes,resizable=yes,status=yes,location=no,toolbar=yes,menubar=yes';
	var p2 = 'width=500,height=500,left=200,top=200';
	if(layer == 2) {
		for (var i = 0; i < list_1.length; i++) {
			if(list_1[i] == d.name) {
				var data = JSON.stringify(json_data,null,2)
				window.open("",p1,p2).document.write("<html></body><pre>"+data+"</pre></body></html>");
			}
		}		
	}		
}

function popup_1(d) {
	layer = d.layer;	
	var p1 = 'scrollbars=yes,resizable=yes,status=yes,location=no,toolbar=yes,menubar=yes';
	var p2 = 'width=500,height=500,left=200,top=200';
	var list = [];
	
	if(layer == 2) {	
		for (var i = 0; i < list_1.length; i++) {
			if(list_1[i] == d.name) {
				for(var key in json_data) {	
					console.log(key);
					if(key == d.name) {
						var count = 0;
						console.log("Entering ==>  ",d.name);
						for(var tex in json_data[key]) {					
							list[count] = json_data[key][tex].text;								
							count += 1;
							console.log(json_data[key][tex].text);           
						}	
					}
				}
				
				console.log(list);
				
				var div = document.getElementById('content');
				content.innerHTML += "<ol>";
				for(var j = 0; j < list.length; j++) {
					content.innerHTML += "<br>" + list[j] + "</br>";					
				}
				content.innerHTML += "</ol>";
				
				modal.style.display = "block";		
							
			}
		}						
	}		
}

console.log("Only once 3");
d3.select(self.frameElement).style("height", "2000px");
console.log("Only once 4");



/*
//Extra code...

var w =  window.open("",p1,p2);
			    w.document.write("<html></body> <textarea rows='4' cols='50'> </textarea>");
				for(var i = 0; i < list.length; i++) {
					w.document.write("<br> <b>"+list[i]+" </b> </br>");
				}								
				w.document.write("</body></html>");
*/

/*
function popup_1(d) {
	layer = d.layer;	
	var list = [];

	if(layer == 2) {	
		for (var i = 0; i < list_1.length; i++) {
			if(list_1[i] == d.name) {
				for(var key in json_data) {	
					console.log(key);
					if(key == d.name) {
						var count = 0;
						console.log("Entering ==>  ",d.name);
						for(var tex in json_data[key]) {					
							list[count] = json_data[key][tex].text;								
							count += 1;
							console.log(json_data[key][tex].text);           
						}	
					}
				}



				//var div = document.getElementById('content');
				content.innerHTML ="";
				content.innerHTML += "<ul>";
				for(var j = 0; j < list.length; j++) {
					content.innerHTML += "<li>" + list[j] + "</li>";					
				}
				content.innerHTML += "</ul>";

				textmodal.style.display = "block";		

			}
		}						
	}		
}

*/

function popup_2(d) {
	layer = d.layer;	
	var p1 = 'scrollbars=yes,resizable=yes,status=yes,location=no,toolbar=yes,menubar=yes';
	var p2 = 'width=500,height=500,left=200,top=200';

	if(layer == 2) {	
		for (var i = 0; i < list_1.length; i++) {
			console.log("Searching = ", d.name);
			if(list_1[i] == d.name) {
				console.log("Found = ", d.name);
				for(var key in json_data) {
					//console.log(key);
					//console.log(json_data[key]);
					if(key == d.name) {
						var count = 0;
						var list = [];
						//textContent ="";
						textContent = "<ul>";

						for(var tex in json_data[key]) {
							//console.log(key);
							//console.log(json_data[key]);
							console.log(json_data[key][tex].text);
							//list[count] = json_data[key][tex].text;
							textContent += "<li>" + json_data[key][tex].text + "</li>" + "<br>";		
							count += 1;
						}	

		//				textContent ="";
		//				textContent += "<ul>";
		//				for(var j = 0; j < list.length; j++) {
		//					textContent += "<li>" + list[j] + "</li>" + "<br>";					
		//				}
						textContent += "</ul>";
						console.log(list);
						Title = "<title>" + d.name + "</title>";
						wstyle = "<style>" +  "body { font-family: Helvetica, Arial, sans-serif; font-size: 12px; font-weight: 400; line-height: 1.4; color: #121212; background-color: #F9F9F9;  }" + "</style>";

						window.open("",p1,p2).document.write("<html>" + "<head>" + Title + wstyle+ "</head>" + "<body>" + textContent + "</body>" + "</html>");
					}
				}
				break;
			}
		}
	}		
} 

