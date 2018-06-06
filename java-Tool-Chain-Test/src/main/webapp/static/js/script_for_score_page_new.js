			
	$(document).ready(function() { 
		
		document.getElementById("user_id_place").innerHTML = userId;
		document.getElementById("u_name_place").innerHTML = userName;			
		document.getElementById("cost_position").innerHTML = strategyCost;
		document.getElementById("name_of_file").innerHTML = fileName; 
		
		document.getElementById("TM").innerHTML = "";
		document.getElementById("TM").innerHTML = docClassificationResult["priceClass"];
		
		document.getElementById("cost_position").innerHTML = "";
		document.getElementById("cost_position").innerHTML = docClassificationResult["highestVal"];
		
		document.getElementById("name_of_strategy").innerHTML = "";
		document.getElementById("name_of_strategy").innerHTML = strategyName;
			
		console.log("strategy_cost type = ",typeof(strategyCost));
		console.log("Strategy Name = ",strategyName);			
						
		console.log("sow_file_name = ",fileName);
		var modified_filename = fileName;
		/* if(fileName.length > 25) {				
			modified_filename = fileName.substring(0,20) +"~"+fileName.substring(fileName.lastIndexOf('.'),fileName.length);
		} */
		console.log("modified_filename Name = ",modified_filename);			
				
		// Inside score-card 
		document.getElementById("sow-name").innerHTML = modified_filename;
		
	// The following three functions are written in :: set_up_variables.js
		//setUpClassHierarchy(generalHierarchy);
		initilizeVariables(generalHierarchy);
		loadScoreCardTable(firstLevelClassCategory);
		loadStrategyViewTable(firstLevelClassCategory);		
		load_initial_page(docClassificationResult,strategyThreshold,scoreCardFlag);		
			
		create_initial_view_table(strategyInfo["class"]);	
		
		call_horizontal_tree("GENERAL");
		
		// For word cloud
		wordCloudData = KPIClassesBool;	
		modifyData(wordCloudData);
		findLH(modified_data);
		setMinSizeAndLower(width,height);
		// Used in word cloud
		x = calculateX(L,H,wordCloudData,modified_data,flag,min_size,text_area,lower);
		drawWordCloud(modified_data);
		
		$("#backToPrevPage").click(function(){				
			if(localStorage.getItem("role") == "U"){
				window.location.replace("score-static-dynamic.html");	
			}else if(localStorage.getItem("role") == "A"){
				window.location.replace("admin-score-static-dynamic.html");	
			}else{
				console.log("Role User/Admin is not defined.");
				logout();
			}	
		});		
			
	});			
			
				
			