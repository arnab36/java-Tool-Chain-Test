		
	function load_initial_page(classificationResult,strategyThreshold,scoreCardFlag){
	
		// This function will calculate the KPIClassesBool & KPIClassesConfidence.		
		calculate_KPI_List(classificationResult,strategyThreshold);
		
		// list_1 will be needed for class Tree
		list_1 = KPIClassesBool;
		
		// This function will set the KPIList & classesContent
		initialize_lists(classificationResult);		
				
		// This function will extract the strategy info for the current strategy and will call another function that will create the initial scoren table.
		create_user_object(strategyInfo,scoreCardFlag);	
		
		// SOW score will be calculated here
		calculate_SOW_score(scoreCardFlag);
		
	}
	

	// The following function will set the total weighted score of first level class categories
	function calculate_initial_weighted_scores(tempdata,flag){
		var first_level_class,total_weighted_score;
		for(var i in firstLevelClassCategory) {
			first_level_class = firstLevelClassCategory[i];			
			if(tempdata.hasOwnProperty(first_level_class)){
				var temp_list = tempdata[first_level_class];		
				var _tr,class_name,count,mandatory,weighted_score,color_indicator,children = 0,counter = 0;
				total_weighted_score = 0;
				// Formula for weighted score will be implemented later
				for(var key in temp_list) {
					class_name = temp_list[key]["class_name"];
					count = temp_list[key]["min_count"];			
					check_flag = checkINKPIList(class_name);
					children = temp_list.length;
					if(check_flag) {
						weighted_score = calculate_weighted_score(class_name,temp_list[key]["raw_score"],temp_list[key]["weight"]);			
					}else {
						weighted_score = 0;
					}
					total_weighted_score += parseFloat(weighted_score);
				}
				if(flag){
					document.getElementById("total_weighted_score_"+first_level_class).innerHTML = total_weighted_score.toFixed(2);
				}				
			}else {
				if(flag){
					document.getElementById("total_weighted_score_"+first_level_class).innerHTML = 0.0;
				}				
			}
		}		
	}
		
 
/*
	The following code is for creating score card modal.
	Whenever a first level link is clicked it will add the corresponding classes to its second level table.
	
*/
	
	function create_initial_score_card_table(userJsonData){			
	// If there is anything exists in the table it would be removed
		$("#scoreCardTable").find("tr:gt(0)").remove();	
		
	// Find and display the first available class category
		var cur_act_li;	
		
		var flagForActiveTab = false;
		for(var i in firstLevelClassCategory){
			if(userJsonData.hasOwnProperty(firstLevelClassCategory[i])){
				cur_act_li= $("#"+firstLevelClassCategory[i]);
				$(cur_act_li).addClass('active');
				break;
			}			
		}
		
		var current_active_id = cur_act_li.attr('id');		
		var temp_list = userJsonData[current_active_id];
		
		var _tr,class_name,count,mandatory,weighted_score,color_indicator,check_flag,counter = 0,children = 0;
		
		// Formula for weighted score will be implemented later
		for(var key in temp_list) {
			class_name = temp_list[key]["class_name"];
			count = temp_list[key]["min_count"];
			check_flag = checkINKPIList(class_name);
			children = temp_list.length;
			if(check_flag) {
				weighted_score = calculate_weighted_score(class_name,temp_list[key]["raw_score"],temp_list[key]["weight"]);			
			}else {
				weighted_score = 0;
			}
			
			
			if(temp_list[key]["mandatory"]) {
				mandatory = "yes";
			}else {
				mandatory = "No";
			}
			
			color_indicator = "critical";
			for(i in KPIList) {
				if(KPIList[i] == class_name) {
					color_indicator = "good";	
					counter += 1;					
				}
			}
			if(color_indicator == "critical"){
				set_flag = true;
			}
			
			_tr  = '<tr>' +
						'<td>' +
							'<span class="'+color_indicator+'"> </span>' +  
							'<span class="class-text">'+class_name+'</span>' +
						'</td>' +
						'<td>'+count+'</td>' +
						'<td>'+mandatory+'</td>' +
						'<td>'+weighted_score+'</td>' +
					'</tr>';					
			$('#scoreCardTable > tbody:last-child').append(_tr);						
		}
		
		if(counter == children) {
			$('#tablePanel').removeClass("critical");
			$('#tablePanel').addClass("good");	
		}
		
	
	
		// Add active/inactive/ red/ green colors to first level categories
		var cur_elem,first_level_class_name;
		counter = 0;
		children = 0;
		class_name = "";
		temp_list = [];		
		for(var key in firstLevelClassCategory) {
			cur_elem = $("#"+firstLevelClassCategory[key].toString());			
			
			if(userJsonData.hasOwnProperty(firstLevelClassCategory[key])){
				first_level_class_name = firstLevelClassCategory[key];
				temp_list = userJsonData[first_level_class_name];
				$(cur_elem).addClass('red');				
				counter = 0;	
				children = 	temp_list.length;		
				for(var i in temp_list) {
					class_name = temp_list[i]["class_name"];
					
					for(var j in KPIList) {
						//console.log(KPIList[j]);
						//console.log(class_name);
						if(class_name == KPIList[j]) {
							//console.log("Found green class");
							counter += 1;						
							break;
						}
					}				
				}
				if(children == counter) {
					$(cur_elem).removeClass('red');
					$(cur_elem).addClass('green');								
				}
			}else {				
				$(cur_elem).addClass('disable');
				$(cur_elem).off("click");				
			}
		}	
	}
	

	// It will destroy the old score_card table and will create a new one
	function open_table(id){
		
		var elem = $('#'+id.toString());
		if($(elem).hasClass('disable')) {
			return;
		}
			
		$("#scoreCardTable").find("tr:gt(0)").remove();	
		var temp_list = strategyInfo["class"][id];
		
		var _tr,class_name,count,mandatory,weighted_score,color_indicator,children = 0,counter = 0;
		
		// Formula for weighted score will be implemented later
		for(var key in temp_list) {
			class_name = temp_list[key]["class_name"];
			count = temp_list[key]["min_count"];			
			check_flag = checkINKPIList(class_name);
			children = temp_list.length;
			if(check_flag) {
				weighted_score = calculate_weighted_score(class_name,temp_list[key]["raw_score"],temp_list[key]["weight"]);		
			}else {
				weighted_score = 0;
			}	
			
			if(temp_list[key]["mandatory"]) {
				mandatory = "yes";
			}else {
				mandatory = "No";
			}			
			color_indicator = "critical";			
			$('#tablePanel').removeClass("good");
			$('#tablePanel').addClass("critical");			
			for(i in KPIList) {
				if(KPIList[i] == class_name) {
					counter += 1;
					color_indicator = "good";
					break;					
				}
			}
			
			_tr  = '<tr>' +
						'<td>' +
							'<span class="'+color_indicator+'"> </span>' +  
							'<span class="class-text">'+class_name+'</span>' +
						'</td>' +
						'<td>'+count+'</td>' +
						'<td>'+mandatory+'</td>' +
						'<td>'+weighted_score+'</td>' +
					'</tr>';					
			$('#scoreCardTable > tbody:last-child').append(_tr);						
		}
		if(counter == children){
			$('#tablePanel').removeClass("critical");
			$('#tablePanel').addClass("good");
		}
	}
	
	
	// Using jsonKey_userId and strategy_name we are extracting the particular strategy info
	function create_user_object(strategyInfo,flag){		
		var userJsonData = strategyInfo["class"];
		//console.log("userJsonData => \n",userJsonData);	
		calculate_initial_weighted_scores(userJsonData,flag);
		if(flag){
			create_initial_score_card_table(userJsonData);
		}				
	}
	
	
	// The following function will take the classification result and threshold value and will calculate the 
	function calculate_KPI_List(json_data,threshold) {
		reset_lists();
		var list = {};
		//console.log("calculate_KPIList")
		var answer_units = json_data['answer_units'];
		for(var x in answer_units) {
			//console.log(answer_units[x]["content"]);
			for(var y in  answer_units[x]["content"]) {
				class_name = answer_units[x]["content"][y]["CLASS"];
				class_conf = parseFloat(answer_units[x]["content"][y]["confidence"]);				
				class_quality = answer_units[x]["content"][y]["Quality"] ;				
				if(class_conf > threshold) {
					console.log("class Conf :: ", class_conf);
					console.log("class Conf Type :: ", typeof(class_conf));
					KPIClassesBool[class_name] += 1;		
					KPIClassesConfidence[class_name] += (class_quality * class_conf);
				}
			}			 
		}		
	}	
	
	
	/*
		The following function will initialize the lists.
	*/
	function initialize_lists(json_data) {		
		//console.log("KPIClassesBool =>",KPIClassesBool);
		//console.log("initialize_lists");
        for(var idx in KPIClassesBool) {
			if(KPIClassesBool[idx] > 0){
				 KPIList.push(idx);		// Green_List
			}                     
		}		
		var gclass,class_conf,str;		
		var answer_units = json_data['answer_units'];
		for(var x in answer_units) {
			for(var y in answer_units[x]['content']) {
				gclass = answer_units[x]['content'][y]['CLASS'];
                if(gclass != 'BLANK') {
					//if(checkINKPIList(gclass)) {										
						class_conf = (parseFloat(answer_units[x]["content"][y]["confidence"])).toFixed(2);
						if((KPIClassesBool[gclass] > 0 && class_conf >= strategyThreshold)
						  ||(KPIClassesBool[gclass] == 0 && class_conf >= redThreshold)){
							str = "";	
							class_conf = class_conf.toString().bold();
							str = "( "+class_conf + ") " + answer_units[x]['content'][y]["text"];
							console.log("Pushing for Class :: "+ gclass);
							try {
								classesContent[gclass].push(str);	
							}catch(err){
								console.log(err);
							}
							
						}										
					//}					
				}                    
			}                
		}		
	}
	
	function checkINKPIList(className){
		var flag = false;
		for(var i in KPIList) {
			if(KPIList[i] == className){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
		
	
	// This function will reset the values in the list.
	function reset_lists() {
		KPIList = [];
		for (var key in KPIClassesBool){
			KPIClassesBool[key] = 0;
		}
		for (var key in classesContent){
			classesContent[key] = [];
		}
		for (var key in KPIClassesConfidence){
			KPIClassesConfidence[key] = 0;
		}
	}	
	
	
	// This function will calculate the weighted score for each class
	function calculate_weighted_score(class_name,raw_score,weight){
		var weighted_score = 0;	
		raw_score = parseFloat(raw_score);
		weight = parseFloat(weight);
		//console.log(typeof(raw_score));
		//console.log(typeof(weight));
		/*if(class_name == "Deliverable") {
			weighted_score = (KPIClassesConfidence[class_name] * raw_score * weight)/100;
		}else {
			weighted_score = (KPIClassesConfidence[class_name] * weight * raw_score)/(KPIClassesBool[class_name] * 100);
		}*/
		weighted_score = (KPIClassesConfidence[class_name] * weight * raw_score)/(KPIClassesBool[class_name] * 100);
		//console.log(typeof(weighted_score));
		return weighted_score.toFixed(2);
	}
	
	
	// This function will calculate the final SOW_score
	function calculate_SOW_score(scoreCardFlag){		
		var temp_list = [];
		var classInfo = strategyInfo["class"];;
		var class_name,weighted_score,temp_score = 0;
		for(var key in classInfo) {
			temp_list = classInfo[key];
			for(var i in temp_list) {
				class_name = temp_list[i]["class_name"];				
				check_flag = checkINKPIList(class_name);
				if(check_flag) {
					weighted_score = calculate_weighted_score(class_name,temp_list[i]["raw_score"],temp_list[i]["weight"]);
				}else {
					weighted_score = 0;
				}	
				console.log("weighted_score = " + weighted_score);
				temp_score += parseFloat(weighted_score);
				console.log(typeof(temp_score));
			}
		}
		console.log("Type score = ", typeof(temp_score));
		console.log("score = ", temp_score);
		//SOW_SCORE = temp_score.toFixed(2);
		SOW_SCORE = Math.round(temp_score);
		
		console.log("Score calculated  = ",SOW_SCORE);
		
		// If the score goes beyond 100 we make it 100
		if(SOW_SCORE > 100) {
			SOW_SCORE = 100;
		}
	
		if(scoreCardFlag){
			render_curve(SOW_SCORE,"","",scoreCardFlag);	
			set_mandatory_score(strategyInfo["class"]);		
		}
		sessionStorage.setItem("SOW_SCORE",SOW_SCORE);
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
	
	