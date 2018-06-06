	/*
		This page is for setting up the variables for result display.
		The following lines will set the values of global variables that will be needed in future.
	*/
	
			
	function initilizeVariables(classHierarchy){
		console.log("=========== Inside initilizeVariables =========");		
		console.log("class_hierarchy = ",classHierarchy);
		setInitialValues(classHierarchy);	
	}
	
	
	/*
		This function is called from script_for_create_strategy.js
		which is an utility script for create_strategy.html
	*/
	function setUpClassHierarchy(classHierarchy){
		var children = classHierarchy["children"];
		firstLevelClassCategory = [];
		for(var i in children){
			firstLevelClassCategory.push(children[i]["name"]);
		}
	} 
	
		 
	/*
		The following function will read the class hierarcy json and then will initialize the following variables, - 
			- classesContent			
			- firstLevelClassCategory
	*/
	function setInitialValues(classHierarchy){
		console.log("=========== Inside setInitialValues =========");
		var children = classHierarchy["children"];
		for(var i in children){
			firstLevelClassCategory.push(children[i]["name"]);
			var secondLevelChld = children[i]["children"];
			for(var j in secondLevelChld) {
				classesContent[secondLevelChld[j]["name"]] = [];
				KPIClassesBool[secondLevelChld[j]["name"]] = 0;
				KPIClassesConfidence[secondLevelChld[j]["name"]] = 0;
			}
		}		
	}
	 
	 
	 
	 // This function will load the score card table
	function loadScoreCardTable(firstLevelClassCategory){
		var li;
		console.log("=========== Inside loadScoreCardTable =========");
		for(var i in firstLevelClassCategory) {
			li = "";
			li += '<li class="" id="'+firstLevelClassCategory[i]+'" onclick="open_table(this.id)">'+
						'<a href="#">'+firstLevelClassCategory[i]+'</a>'+
						'<span class="devider">'+
						'</span> <span id="total_weighted_score_'+firstLevelClassCategory[i]+'"> </span>'+
					'</li>';
			//document.getElementById("buttons-middle").innerHTML += li;
			$('#buttons-middle').append(li);
	 
		}
	}
	 
	
	// Loading the view and edit strategy table
	function loadStrategyViewTable(firstLevelClassCategory){
		console.log("=========== Inside loadStrategyViewTable =========");
		var li;
		var collapsedClass = "";
		for(var i in firstLevelClassCategory) {
			li = "";
			li += '<div class="panel panel-default">'+
						'<div class="panel-heading '+collapsedClass+'" data-toggle="collapse" data-parent="#accordion" data-target="#view'+firstLevelClassCategory[i]+'">'+
							'<i class="toggle-arrow"></i>'+
							'<h4 class="panel-title" >'+
								'<a href="#view'+firstLevelClassCategory[i]+'">'+firstLevelClassCategory[i]+'</a>'+
							'</h4>'+
						'</div>'+						
						'<div id="view'+firstLevelClassCategory[i]+'" class="panel-collapse collapse in">'+
							'<div class="panel-body">'+
								'<div class="table-responsive">'+
									'<div class="table-responsive accordion-header">'+         
										'<table class="table">'+
											'<thead>'+
											'<tr>'+
												'<th></th>'+
												'<th>Class Name</th>'+
												'<th>Raw Score</th>'+
												'<th> Weight</th>'+
												'<th> Min Count</th>'+
												'<th>Mandatory Class</th>'+
												'<th>Min Score</th>'+
											'</tr>'+
											'</thead>'+
										'</table>'+
									'</div>'+            
									'<table class="table" id="'+firstLevelClassCategory[i]+'_Table">'+
										'<tbody>'+																		
										'</tbody>'+
									'</table>'+
								'</div>'+
							'</div>'+
						'</div>'+
					'</div>';
					
			document.getElementById("accordion").innerHTML += li;
			collapsedClass = "collapsed";
			if(i != 0){
				$('#view'+firstLevelClassCategory[i]).collapse();
			}
			//$('#accordion').append(li);	 
		}
	}
	