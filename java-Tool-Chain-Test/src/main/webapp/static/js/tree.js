
var data = {"id": "1", "name": "SOW Root", "layer": "0", "current": "true", "children": [
        {"id": "2", "name": "Legal", "warning": "true", "layer": "1", "children": [
                {"id": "10", "name": "NDACompanyRestrict", "layer": "2"},
                {"id": "11", "name": "NDASupplierRestrict", "warning": "true","layer": "2"},
				 {"id": "12", "name": "AdherenceToLaws", "warning": "true","layer": "2"},
				  {"id": "13", "name": "IntellectualCapital", "warning": "true","layer": "2"},
				   {"id": "14", "name": "Compliance", "warning": "true","layer": "2"},
				     {"id": "15", "name": "GoverningContract", "warning": "true","layer": "2"},
					   {"id": "16", "name": "ChangeControl", "warning": "true","layer": "2"}				
            ]},
			
       {"id": "3", "name": "Payment", "warning": "true", "layer": "1", "children": [
		{"id": "17", "name": "TMTerms", "warning": "true", "layer": "2"},
		{"id": "18", "name": "FPTerms", "warning": "true", "layer": "2"},
		{"id": "19", "name": "PaymentTerms", "warning": "true", "layer": "2"}
	   ]},
	   
	   {"id": "4", "name": "Company", "warning": "true", "layer": "1", "children": [
		{"id": "20", "name": " CompanyActivities", "warning": "true", "layer": "2"},
		{"id": "21", "name": "CompanyRolesAndResponsibilities", "warning": "true", "layer": "2"},		
	   ]},
	   
	   {"id": "5", "name": "Supplier", "warning": "true", "layer": "1", "children": [
		{"id": "22", "name": "SupplierActivities", "warning": "true", "layer": "2"},
		{"id": "23", "name": "SupplierRolesAndResponsibilities", "warning": "true", "layer": "2"},		
	   ]},
	   
	   {"id": "6", "name": "AdditionalTerms", "warning": "true", "layer": "1", "children": [
		{"id": "24", "name": "SpecialTerms", "warning": "true", "layer": "2"},
		{"id": "25", "name": "TravelTerms", "warning": "true", "layer": "2"},		
	   ]},
	   
	   {"id": "7", "name": "Informational", "warning": "true", "layer": "1", "children": [
		{"id": "26", "name": "TableOfContents", "warning": "true", "layer": "2"}			
	   ]},
	   
	   {"id": "8", "name": "WorkDescription", "warning": "true", "layer": "1", "children": [
		{"id": "27", "name": "ExecutiveSummary", "warning": "true", "layer": "2"},
		{"id": "28", "name": "WorkingConstraint", "warning": "true", "layer": "2"},
		{"id": "29", "name": "WorkLocation", "warning": "true", "layer": "2"},
		{"id": "30", "name": "Assumptions", "warning": "true", "layer": "2"}		
	   ]},
	   
	   {"id": "9", "name": "Deliverable", "warning": "true", "layer": "1", "children": [
		{"id": "31", "name": "Deliverable", "warning": "true", "layer": "2"},
		{"id": "32", "name": "AcceptanceCriteria", "warning": "true", "layer": "2"},		
	   ]},
	   
	   {"id": "33", "name": "New", "warning": "true", "layer": "1", "children":[]}	   
        
    ]};

	
var customNodes = new Array(),
        tmpNodes,
        label_w = 75,
        branch_w = 75,	
        layer_wider_label = new Array(),
        depencencyChart;

		
function initDependencyChart() {
        tmpNodes = d3.layout.tree().size([500, 400]).nodes(data);
// Create a svg canvas
    depencencyChart = d3.select("#depencency-chart").append("svg:svg")
            .attr("width",500)
            .attr("height", 600)
            .append("svg:g")
            .attr("transform", "translate(20, 15)"); // shift everything to the right
    var fakeTxtBox = depencencyChart.append("svg:text")
            .attr("id", "fakeTXT")
            .attr("text-anchor", "right")
            .text(data.name);
			
    layer_wider_label[0] = fakeTxtBox.node().getComputedTextLength();
    depencencyChart.select("#fakeTXT").remove();
    data.y = getNodeY(data.id);
    data.x = 0;
    data.depth = parseInt(data.layer);
    customNodes.push(data);
    prepareNodes(data.children);
    //align nodes.
    updateNodesXOffset()
    drawChart();
}


function updateNodesXOffset(){
    var x_offsets = new Array();
    x_offsets[0] = 0;
    customNodes.forEach(function(node) {
        node.x = 0;
        if (node.layer > 0) {
            node.x = x_offsets[node.layer - 1] + layer_wider_label[node.layer - 1] + branch_w;
			console.log(layer_wider_label[node.layer - 1]);
			console.log(x_offsets[node.layer - 1]);
			console.log(node.x);
			console.log("=================================");
            x_offsets[node.layer] = node.x;
        }
    });
}


function getNodeY(id) {
    var ret = 0;
    tmpNodes.some(function(node) {
        if (node.id === id) {
            //return x:d3.tree has a vertical layout by default.
            ret = node.x;
            return;
        }
    })
    return ret;
}


function prepareNodes(nodes) {
    nodes.forEach(function(node) {
        prepareNode(node);
        if (node.children) {
            prepareNodes(node.children);
        }
    });
}


function prepareNode(node) {
    node.y = getNodeY(node.id);
    //fake element to calculate labels area width.
    var fakeTxtBox = depencencyChart.append("svg:text")
            .attr("id", "fakeTXT")
            .attr("text-anchor", "right")
            .text(node.name);
    var this_label_w = fakeTxtBox.node().getComputedTextLength();
    depencencyChart.select("#fakeTXT").remove();
    if (layer_wider_label[node.layer] == null) {
        layer_wider_label[node.layer] = this_label_w;
    } else {
        if (this_label_w > layer_wider_label[node.layer]) {
            layer_wider_label[node.layer] = this_label_w;
        }
    }
//                node.x = nodex;
    //x will be set
    node.depth = parseInt(node.layer);
    customNodes.push(node);
}


function customSpline(d) {
    var p = new Array();
    p[0] = d.source.x + "," + d.source.y;
    p[3] = d.target.x + "," + d.target.y;
    var m = (d.source.x + d.target.x) / 2
    p[1] = m + "," + d.source.y;
    p[2] = m + "," + d.target.y;
    //This is to change the points where the spline is anchored
    //from [source.right,target.left] to [source.top,target.bottom]
//                var m = (d.source.y + d.target.y)/2
//                p[1] = d.source.x + "," + m;
//                p[2] = d.target.x + "," + m;
    return "M" + p[0] + "C" + p[1] + " " + p[2] + " " + p[3];
}


function drawChart() {
    customNodes.forEach(function(node) {
        var nodeSVG = depencencyChart.append("svg:g")
                .attr("transform", "translate(" + node.x + "," + node.y + ")")
        if (node.depth > 0) {
            nodeSVG.append("svg:circle")
                    .attr("stroke", node.children ? "VIOLET" : "PINK")
                    .attr("fill", "YELLOW")
                    .attr("r", 10.5)		
									
        }
		
        var txtBox = nodeSVG.append("svg:text")
                .attr("dx", 8)
                .attr("dy", 4)
                .attr("fill", node.current ? "ORANGE" : node.children ? "MAJENTA" : "GRAY")
                .text(node.name)
				.on("click", click);	
				
        var txtW = txtBox.node().getComputedTextLength();
        if (node.current) {
            nodeSVG.insert("rect", "text")
                   // .attr("fill", node.children ? "#3191c1" : "#269926")
				    .attr("fill", node.children ? "GREEN" : "BLACK")
                    .attr("width", txtW + 8)
                    .attr("height", "20")
                    .attr("y", "-12")
                    .attr("x", "5")
                    .attr("rx", 4)
                    .attr("ry", 4);					
        }
		
        if (node.children) {
            node.x = node.x + txtW + 20;
            //prepare links;
            var links = new Array();
            node.children.forEach(function(child) {
                var st = new Object();
                st.source = node;
//                        st.parent = node;
                st.target = child;
                st.warning = child.warning;
                links.push(st);
            });

            //draw links (under nodes)
            depencencyChart.selectAll("pathlink")
                    .data(links)
                    .enter().insert("svg:path", "g")
                    .attr("class", function(d) {
                return d.warning === "true" ? "link warning" : "link"
            })
                    .attr("d", customSpline)
            //draw a node at the end of the link
            nodeSVG.append("svg:circle")
                    .attr("stroke", "#3191c1")
                    .attr("fill", "RED")
                    .attr("r", 10.5)
                    .attr("transform", "translate(" + (txtW + 20) + ",0)")
					//.attr('class','addButton')
					//.on("click", click);
        }
    });
	
	// Toggle children on click.
	function click(node) {
	  alert("hi ::");	
	  alert(node);	  
	}
}

