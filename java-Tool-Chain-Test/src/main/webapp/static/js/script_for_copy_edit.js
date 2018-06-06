	
	
	setUpClassHierarchy(classHierarchy);
	loadStrategyViewTable(firstLevelClassCategory);
	
	/* 
		The following class is called cost_table_row as it would store the three values i.e 
		cost, red and green values.
	*/
	
	class cost_table_row {
		constructor(cost, red, yellow) {
			this.cost = cost;
			this.red = red;
			this.yellow = yellow;
		}
		 
		getCost(){
			return this.cost; 
		}
		
		getRed(){
			return this.red; 
		}
		
		getYellow(){
			return this.yellow; 
		}
	}
	
	
	// This function will sort the array of cost_table_row objects
	function sort_array(array){
		var length = array.length;
		var temp;
		for(var i=0; i < length - 1; i++){
			for (var j = 0; j < length-i-1; j++) {
				 if (array[j].getCost() > array[j+1].getCost()){
					temp =  array[j+1];
					array[j+1] = array[j];
					array[j] = temp;
				 }
			}
		}
		return array;
	}
	
	
	/*
		This function will get all the values from cost table and will
		create an array of objects out of it. It will add the passed arguments
		also in the array and finally return an array of objects.
	*/
	function get_cost_table_values(cost,red,yellow){
		var array = [];		
		var temp_cost, temp_red, temp_yellow,obj,counter = 0;
		for(var i = 1; i <= final_row_id; i++) {
			try {
				temp_cost = parseFloat(document.getElementById("row"+i.toString()+"_cost").value);
				temp_red = parseFloat(document.getElementById("row"+i.toString()+"_red").value);
				temp_yellow = parseFloat(document.getElementById("row"+i.toString()+"_yellow").value);
				obj = new cost_table_row(temp_cost,temp_red,temp_yellow);
				array[counter] = obj;
				counter += 1;
			}catch(e) {
				
			}			
		}
		obj = new cost_table_row(cost,red,yellow);
		array[counter] = obj;		
		return array;
	}	
	
	
		
	/*
		The following function will take the json file(basically the class heirarchy)
		and strategy_name and startegy data json object	as input and will create the
		table for startegy edit or copy.
	*/
	function create_class_table(class_hierarchy_json,strategy_name,user_obj) {	
		class_hierarchy = class_hierarchy_json;	
		var chld = class_hierarchy_json["children"];
		console.log(chld);	
		
		// This variable holds all the strategies created by this user
		//var user_obj = strategy_json_object[jsonKey_userId];
		
		// This var holds only the startegy we are considering here
		var strategy_info;
		
		for(var key in user_obj){
			if(user_obj[key]["name"] == strategy_name) {
				strategy_info = user_obj[key];
				if(strategy_info["access"] == "Public"){
					document.getElementById("access_type_id").checked = true;
				}
			}
		}
		console.log("strategy_info = \n ",strategy_info);
		
		var raw_score,min_count,min_score,weight;		
		
		var first_level_class_name,table_id,second_level_class_list,flag,man_flag;
		for(var i = 0; i < chld.length; i++) {			
			first_level_class_name = chld[i]["name"];	
			table_id = first_level_class_name + "_Table"; 	
			for(var j = 0; j < chld[i]["children"].length; j++) {	
				var class_name = chld[i]["children"][j]["name"];
				var check_box_id = "check."+class_name;
				var class_name_id = "class_name."+class_name;
				var raw_score_id = "raw_score."+class_name;
				var weight_id = "weight."+class_name;
				var min_count_d = "min_count."+class_name;
				var mandatory_class_id = "mandatory_class."+class_name;
				var min_score_id = "min_score."+class_name;	
				
				flag = false;
				man_flag = false;
				if(strategy_info["class"].hasOwnProperty(first_level_class_name)){
					second_level_class_list = strategy_info["class"][first_level_class_name];
					//console.log("First level found ");
					for(var k in second_level_class_list) {
						if(second_level_class_list[k]["class_name"] == class_name) {
							//console.log("Second level found ");
							flag = true;							
							raw_score = second_level_class_list[k]["raw_score"];		
							min_count = second_level_class_list[k]["min_count"];		
							min_score = second_level_class_list[k]["min_score"];	
							weight = second_level_class_list[k]["weight"];	
							man_flag = second_level_class_list[k]["mandatory"];
						}
					}
				}
				
				if(!flag){
					raw_score = 100;		
					min_count = 1;		
					min_score = 0;	
					weight = 5;						
				}
				
				var _tr = 	'<tr>' + 
								'<td> <div class="checbox"> <input type="checkbox"  class="check_box_1" id = '+check_box_id+' >  </div> </td>' +
								'<td class="class-name">'+class_name+'</td>' +
								'<td> <div class="tbox"> <input type="number" value='+raw_score+' id='+raw_score_id+' min="0" max="100"  onchange="checkDecimal(this.id)"/> </div> </td>' +
								'<td> <div class="tbox"> <input type="number" id='+weight_id+' value='+weight+' min="0" max="100"  onchange="checkDecimal(this.id)"/> </div> </td>' +
								'<td> <div class="tbox"> <input type="number" value='+min_count+' id='+min_count_d+' min="1" max="100" onchange="checkDecimal(this.id)"/> </div> </td>' +
								//'<td> <div class="checbox"> <input type="checkbox" class="check_box_2" id ='+mandatory_class_id+' value="" >  </div></td>' +
								'<td>  <span> No </span> <label class="switch">  <input type="checkbox" class="check_box_2 mandate" id ='+mandatory_class_id+'> <div class="slider round"></div> </label> <span> Yes </span></td>' + 
								'<td> <div class="tbox"> <input type="number" value='+min_score+' id='+min_score_id+' min="0" max="100" onchange="checkDecimal(this.id)"/> </div> </td>' +								
							'</tr>';					
					
				$('#'+table_id+' > tbody:last-child').append(_tr);
				
				if(flag) {
					document.getElementById(check_box_id).checked = true;
				}else {
					document.getElementById(check_box_id).checked = false;
				}
					
				var elem = document.getElementById(mandatory_class_id);
				console.log("Elenm = ", elem);
				
				if(man_flag) {
					document.getElementById(mandatory_class_id).checked = true;
				}else {
					document.getElementById(mandatory_class_id).checked = false;
				}	
			}
		}
		create_cost_table(strategy_info);
	}	
	
	// This function will read the user_strategy info and will create a sorted cost table.
	function create_cost_table(strategy_info){		
		var cost,red,yellow,obj,array = [];
		var cost_table = strategy_info["score_table"];		
		for(var k in cost_table) {
			cost = parseFloat(cost_table[k]["Cost"]);
			red = parseFloat(cost_table[k]["Red_Max"]);
			yellow = parseFloat(cost_table[k]["Yellow_Max"]);
			obj = new cost_table_row(cost,red,yellow);
			array[k] = obj;
		}
		
		// Sort the array
		array = sort_array(array);
		
		//  As we are creating a new array row id will be newly assigned
		final_row_id  = array.length;	
		var _tr,counter = 0;;
		for(var i = 1; i <= final_row_id; i++){
			cost = array[counter].getCost();
			red = array[counter].getRed();
			yellow = array[counter].getYellow();
			_tr =   '<tr id="row'+i.toString()+'">'+
						'<td> <span class="edit-input cost"> <input type="number" id="row'+i.toString()+'_cost" value="'+cost+'" min="0" onchange="check_for_duplicate_cost(this.id)" /> </span> <span class="val" id="row'+i.toString()+'_span_cost">'+cost+'</span></td>' +
						'<td> <span class="edit-input"> <input type="number"  id="row'+i.toString()+'_red" value="'+red+'" min="0" max="100" onchange="check_condition(this.id)"  /> </span> <span class="val" id="row'+i.toString()+'_span_red"> '+red+'</span></td> ' +
						' <td> <span class="edit-input"> <input type="number" id="row'+i.toString()+'_yellow" value="'+yellow+'" min="0" max="100" onchange="check_condition(this.id)" /> </span> <span class="val" id="row'+i.toString()+'_span_yellow">'+yellow+'</span></td>' +
						'<td>' +
							'<div class="edit-delete">' +
								'<a href="#" title="Edit"> <i class="edit" id="row'+i.toString()+'_edit" ></i> </a>'+
									'<span class="devider"> </span>'+
								'<a href="#" title="Delete"> <i class="delete" id="row'+i.toString()+'_delete"></i></a>'+
							'</div>'+
							'<div class="save-cancel">'+
									'<a href="#" title="Save" > <i class="save" id="row'+i.toString()+'_save"></i> </a>'+
									' <span class="devider"> </span>'+
									'<a href="#" title="Cancel"> <i class="cancel" id="row'+i.toString()+'_cancel" ></i> </a>'+
							'</div>'+
						'</td>'+
					'</tr>';
			// Adding the row into the table 
			$('#cost_table > tbody:last-child').append(_tr);
			counter += 1;
		}
			
	}
	
	// This function will create a cost table withy one default row
	function add_row_to_cost_table() {		
		var cost = parseFloat(document.getElementById("score_table_cost").value);
		var red = parseFloat(document.getElementById("score_table_red").value);
		var yellow = parseFloat(document.getElementById("score_table_yellow").value);			
	
		if(cost == "" || red == "" || yellow == "") {
			//alert("Please enter all the values .....");
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgModal').show();
			$('#errormsgModal').text("Please enter all the values .....");  
			return;
		}
		
		if(isNaN(cost)){  
			//alert("Row can not be added as you have not enetered the cost value.....");			
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgModal').show();
			$('#errormsgModal').text("Row can not be added as you have not enetered the cost value.....");  
			return;
		}
		
		if(isNaN(red)){  
			//alert("Row can not be added as you have not enetered the red value.....");
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgModal').show();
			$('#errormsgModal').text("Row can not be added as you have not enetered the red value.....");  			
			return;
		}
		
		if(isNaN(yellow)){  
			//alert("Row can not be added as you have not enetered the yellow value.....");		
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgModal').show();
			$('#errormsgModal').text("Row can not be added as you have not enetered the yellow value.....");  			
			return;
		}
		
		if(yellow<=red){  
			//alert("red maximum cannot be greater that Yellow.....");	
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgModal').show();
			$('#errormsgModal').text("red maximum cannot be greater that Yellow.....");  			
			return;
		}
		
		 if(0>yellow || yellow>100){  
            //alert("yellow should be within 0 to 100.....");
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgModal').show();
			$('#errormsgModal').text("yellow should be within 0 to 100.....");  			
            return;
        }
        
        if(0>red || red>100){  
           // alert("Red should be within 0 to 100.....");      
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgModal').show();
			$('#errormsgModal').text("Red should be within 0 to 100.....");  					   
            return;
        }		

			
		var id;		
		for(var i = 1; i <= final_row_id; i++) {
			//console.log("================================");
			id = "row"+i.toString()+"_cost";			
			try {
				if(document.getElementById(id).value==cost){
					//alert("Cost already exits. Try with different cost.....");	
				//	document.getElementById("errormsgModal").removeAttribute("hidden");
					$('#errormsgModal').show();
					$('#errormsgModal').text("Cost already exits. Try with different cost.....");  	
					return;	
				}
			}
			catch(e){				
			}			
		}		 
		$( "#myModal" ).modal("hide");	
		// Now we restructure the table
		restructure_cost_table(cost,red,yellow);
		// Resetting the inputs
		$('#score_table_cost').val(null);
		$('#score_table_red').val(null);	
		$('#score_table_yellow').val(null);	
		
	}
	
	// This function will remove the previous table and will create a new one.
	function restructure_cost_table(cost,red,yellow){
		// Create the array
		var array = get_cost_table_values(cost,red,yellow);
		
		// Sort the array
		array = sort_array(array);
		
		//  As we are creating a new array row id will be newly assigned
		final_row_id  = array.length;		
		
		// Remove the previous table	
		$('#cost_table').find("tr:gt(0)").remove();
		
		console.log("array \n ",array);
		
		/*
			Now the new table is formed using all the values in the sorted array.
			Here i starts from two as first row is unchangable and default.
		*/
		var _tr;
		var counter = 0;
		for(var i = 1; i <= final_row_id; i++) {	
		
			cost = array[counter].getCost();
			red = array[counter].getRed();
			yellow = array[counter].getYellow();			
					
			console.log("cost = ",cost);
			//console.log("red = ",red);
			//console.log("yellow = ",yellow);
			
			_tr =   '<tr id="row'+i.toString()+'">'+
						'<td> <span class="edit-input cost"> <input type="number" id="row'+i.toString()+'_cost" value="'+cost+'" onchange="check_for_duplicate_cost(this.id)" /> </span> <span class="val" id="row'+i.toString()+'_span_cost">'+cost+'</span></td>' +
						'<td> <span class="edit-input"> <input type="number"  id="row'+i.toString()+'_red" value="'+red+'" onchange="check_condition(this.id)"  /> </span> <span class="val" id="row'+i.toString()+'_span_red"> '+red+'</span></td> ' +
						' <td> <span class="edit-input"> <input type="number" id="row'+i.toString()+'_yellow" value="'+yellow+'" onchange="check_condition(this.id)" /> </span> <span class="val" id="row'+i.toString()+'_span_yellow">'+yellow+'</span></td>' +
						'<td>' +
							'<div class="edit-delete">' +
								'<a href="#"> <i class="edit" id="row'+i.toString()+'_edit" ></i> </a>'+
									'<span class="devider"> </span>'+
								'<a href="#"> <i class="delete" id="row'+i.toString()+'_delete"></i></a>'+
							'</div>'+
							'<div class="save-cancel">'+
									'<a href="#"> <i class="save" id="row'+i.toString()+'_save"></i> </a>'+
									' <span class="devider"> </span>'+
									'<a href="#"> <i class="cancel" id="row'+i.toString()+'_cancel" ></i> </a>'+
							'</div>'+
						'</td>'+
					'</tr>';						
						
			// Adding the row into the table 
			$('#cost_table > tbody:last-child').append(_tr);
			counter += 1;
		}
		
	}
	
	
	// It will create a temporary json object 
	function save_strategy(){	
		var strategy_name = $("#new_strategy_name").val();			
		if(strategy_name == null || strategy_name == "") {
			//alert("Please enter a new strategy name ....");	
			//document.getElementById("errormsgBody").removeAttribute("hidden");
			$('#errormsgBody').show();
			$('#errormsgBody').text("Please enter a new strategy name ....");			
			
			$('#new_strategy_name').focus();
			return;
		}
		
		var check_flag = check_checkbox();
		if(check_flag) {
			//alert("Please select at least one class to include in the strategy...");
			//document.getElementById("errormsgBody").removeAttribute("hidden");
			$('#errormsgBody').show();
			$('#errormsgBody').text("Please select at least one class to include in the strategy...");
			return;
		}
		
		var found = special_char_check(strategy_name);
		if(found){
			alert("Strategy name can not contain dot(.)");
			$('#new_strategy_name').focus();
			return;
		}		
			
		CreateNewStrategy();
		update_json(temporary_obj);
		save_to_local(strategyInfo);
		temporary_obj = {};			
	}	
	
	// After creating a strategy it will be saved to local project directory
	function save_to_local(obj){
		var json_data = JSON.stringify(obj);
		
		$("#wrapper *").prop("disabled", true);
		$.ajax({
				type: "POST",
				url: "CreateEditServlet",
				async: false,
				data: { 
						param_1: json_data,
						strategyName: $("#new_strategy_name").val(),
						stratType: contractType,
						accessType: accessType,
						modifyFlag: modFlag
				},
				success: function () {
					console.log("message success is = ");												
					localStorage.removeItem("strategyInfo");
					localStorage.setItem("strategyInfo",JSON.stringify(strategyInfo));	

					changeStrategyInfoHierarchy(strategyInfo);					
					$("#wrapper *").prop("disabled", false);					
				},
				error: function(xhr, textStatus, errorThrown){								
					//console.log(xhr);								
					//console.log(errorThrown);
					$("#wrapper *").prop("disabled", false);
					alert(errorThrown);
					alert(xhr.responseText);
					window.location.reload();
				}					
		});
	}
	
	
	function changeStrategyInfoHierarchy(strategyInfo){
		var temp = strategyInfo[contractType];
		for(var i in temp){
			if(temp[i]["name"] == strategy_name){
				var acc = temp[i]["access"];
				var strategyInfoHierarchy = JSON.parse(localStorage.getItem("strategyInfoHierarchy"));
				var temp1 = strategyInfoHierarchy[userId][contractType];
				for(var j in temp1){
					if(temp1[j]["StrategyName"] == strategy_name){
						temp1[j]["accessType"] = acc.substring(0,3);;
					}
				}
				
			}
		}
		localStorage.removeItem("strategyInfoHierarchy");
		localStorage.setItem("strategyInfoHierarchy",JSON.stringify(strategyInfoHierarchy));
		window.location.replace("admin-strategy-main-page.html");
	}
	
			
	// The following function will add a new strategy in the existing list
	function CreateNewStrategy() {		
		var strategy_name = $("#new_strategy_name").val().trim();		
		var number_row = document.getElementById("cost_table").rows.length;	
		temporary_obj = write_to_json_object();						 
	}
		
	// The following function will write the strategy data to a json object
	function write_to_json_object() {	
		//console.log("Bug 7");
		// First of all create the meta-data			
		var dictionary = {};
		var classes = {};
		var tables;
		var index = 0;
		
		var chld = class_hierarchy["children"];		
		for(var i = 0; i < chld.length; i++) {			
			var table_id = chld[i]["name"]+"_Table";	
			//console.log("table_id = ",table_id);	
			tables = [];
			index = 0;			
			for(var j = 0; j < chld[i]["children"].length; j++) {	
				var class_name = chld[i]["children"][j]["name"];
				var check_box_id = "check."+class_name;					
				
				if(document.getElementById(check_box_id).checked) {
					//console.log("True");
					var temp_obj={};
					var raw_score = document.getElementById("raw_score."+class_name.toString()).value;
					var weight = document.getElementById("weight."+class_name.toString()).value;
					var min_count = document.getElementById("min_count."+class_name.toString()).value;
					var min_score = document.getElementById("min_score."+class_name.toString()).value;	
										
					temp_obj["class_name"] = class_name;
					temp_obj["raw_score"] = (parseFloat(raw_score)).toFixed(2);
					temp_obj["weight"] = (parseFloat(weight)).toFixed(2);	
					temp_obj["min_count"] = parseInt(min_count);
					
					//var elem = document.getElementById("mandatory_class."+class_name.toString());
					var mandatory_class_id = "mandatory_class."+class_name;
					var elem = document.getElementById(mandatory_class_id);					
					
					if($(elem).is(':checked')) {
						temp_obj["mandatory"] = true;	
						//console.log(elem);						
					}else {
						temp_obj["mandatory"] = false;	
						//console.log(elem);							
					}		
					
					temp_obj["min_score"] = (parseFloat(min_score)).toFixed(2);										
					tables[index] = temp_obj;
					index += 1;		
					//console.log("temp obj = ", temp_obj);				
				}
			}
			
			if(tables != ""){
				classes[chld[i]["name"]] = tables;
				//console.log("tables  = ", tables);
				//console.log("class  = ", chld[i]["name"]);
			}
				
			//console.log("classes  = ", classes);
		}
		dictionary["name"] = $("#new_strategy_name").val();
		
		// Assigning date
		var dateString = "";
		var dNow = new Date();		
		dateString += (dNow.getMonth() + 1) + "/";  
		dateString += dNow.getDate() + "/";  
		dateString += dNow.getFullYear();  
		dictionary["Date"] = dateString;
		
		// Assigning public/private
		var acc = document.getElementById("access_type_id");		
		if($(acc).is(':checked')){
			dictionary["access"] = "Public";	
			accessType = "Public";
		}else {
			dictionary["access"] = "Private";
			accessType = "Private";
		}
		console.log(" acc is = ",acc);
		
		
		dictionary["class"] = classes;		
		return 	dictionary;		
	}
	
	
		
	/* 
		This function will create an object for score_table and will append it with 
		a strategy_object and finally it will write the object to the JSON.
	*/
	
	function update_json(dictionary) {
		//console.log("Bug 8");
		var score_obj = [];
		var _cost,_red_max,_yellow_max;
		var counter = 0;
		
		for(var i = 1; i <= final_row_id; i++) {
			try {
				var temp_obj={};
				_cost = $('#row'+i.toString()+"_cost").val();				
				 _red_max = $('#row'+i.toString()+"_red").val();
				 _yellow_max = $('#row'+i.toString()+"_yellow").val();
				 
				 if(typeof(_cost) == "undefined")
					continue;
									 
				 
				 temp_obj["Cost"] = _cost;
				 temp_obj["Red_Max"] = _red_max;
				 temp_obj["Yellow_Max"] = _yellow_max;
				 //score_obj.push(temp_obj);
				 score_obj[counter] = temp_obj;	
				counter += 1;				 
			}catch(e){
				//console.log(e);
			}				
		}
		dictionary["score_table"] = score_obj;
		
		// Delete the previous strategy
		for(var i in strategyInfo[contractType]){ 
			if(strategyInfo[contractType][i]["name"] == strategy_name) {
				console.log(strategyInfo[contractType][i]["name"]);
				strategyInfo[contractType].splice(i,1);
			}
		}		
		// Push/add the edited version of strategy 
		strategyInfo[contractType].push(dictionary);			
	}	

	
	// Checking whether the changed cost already exists in the table
	function check_for_duplicate_cost(id){
		console.log("Function called");
		//var number_row = document.getElementById("cost_table").rows.length;
		var _cost = $('#'+id).val();
		//console.log("Cost = ",_cost);
		var temp_cost, temp_id;
		for(var i = 1; i <= final_row_id; i++) {
			temp_id = 'row'+i.toString()+"_cost";
			//console.log("temp_id = ",temp_id);
			if(id != temp_id) {
				temp_cost = $('#'+temp_id).val();
				//console.log("Temp Cost = ",temp_cost);
				if(_cost == temp_cost){
					//alert("Cost already exists in the table.");
					//document.getElementById("errormsgModal").removeAttribute("hidden");
					$('#errormsgBody').show();
					$('#errormsgBody').text("Cost already exists in the table.");
					$('#'+id).val(prev_cost);
					$('#'+id).focus();					
					return false;
				}
			}			
		}
		return true;
	}
	
	// It will check red and yellow values condition
	function check_condition(id) {		
		var row_number = parseInt(id.substring(3,id.indexOf('_')));
       // console.log("Row id is = ",row_number);   
        
        var temp_id_yel = "row"+row_number.toString()+"_yellow"
        var temp_id_red = "row"+row_number.toString()+"_red"		
		
		var yellow = parseFloat(document.getElementById(temp_id_yel).value);
		var red = parseFloat(document.getElementById(temp_id_red).value); 
		
		// Checking whether it is red/yellow
		var row_category = id.substring(id.indexOf('_'), id.length);
		//console.log("row_category is = ",row_category);   
		
		if(red > 100) {
			//alert("red value can not be more than 100.");		
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgBody').show();
			$('#errormsgBody').text("red value can not be more than 100.");
			document.getElementById(id).value =  parseInt(document.getElementById("row"+row_number.toString()+"_span"+row_category).innerHTML);
			$('#'+id).focus();			
		}
		if(yellow > 100) {
			//alert("yellow value can not be more than 100.");
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgBody').show();
			$('#errormsgBody').text("yellow value can not be more than 100.");
			document.getElementById(id).value =  parseInt(document.getElementById("row"+row_number.toString()+"_span"+row_category).innerHTML);
			$('#'+id).focus();			
		}
		if(red >= yellow){
			//alert("yellow value should always be greater than red value.");
			//document.getElementById("errormsgModal").removeAttribute("hidden");
			$('#errormsgBody').show();
			$('#errormsgBody').text("yellow value should always be greater than red value.");
			document.getElementById(id).value =  parseInt(document.getElementById("row"+row_number.toString()+"_span"+row_category).innerHTML);
			$('#'+id).focus();			
		}	
		
	}	
	





	
	
	// Editing the cost table
	function commit_edit(id){		 
        var row_number = parseInt(id.substring('3',id.indexOf('_')));
        console.log("Row id is = ",id);   
        var temp_id = "row"+row_number.toString()+"_cost";
        var temp_id_yel = "row"+row_number.toString()+"_yellow"
        var temp_id_red = "row"+row_number.toString()+"_red"
		
		var cost = document.getElementById(temp_id).value;
		var yellow = document.getElementById(temp_id_yel).value;
		var red = document.getElementById(temp_id_red).value; 
		
		if(prev_cost != cost) {
			$("#row"+row_number.toString()).remove();
			restructure_cost_table(cost,red,yellow);
		}else {
			document.getElementById("row"+row_number.toString()+"_span_cost").innerHTML = cost;
			document.getElementById("row"+row_number.toString()+"_span_yellow").innerHTML =  yellow;
			document.getElementById("row"+row_number.toString()+"_span_red").innerHTML = red;    
		}        
    }
	
	var prev_cost,prev_yel,prev_red;
		$("#tt").bind('keyup mouseup', function () {
	   // prev_cost= document.getElementById("row3_cost").value;           
	});	
	
	$(document).ready(function() {
		
		// $('#user_name_palce').append('<span>'+user_name+'</span>');	
		 //console.log("user_name is = ",userId);
		document.getElementById("user_id_place").innerHTML = userId;
		document.getElementById("u_name_place").innerHTML = userName;
		document.getElementById("new_strategy_name").value = strategy_name;
		
		$('#contractTypeView').val(contractType);
		
		var row_to_delete;
		
		// Clear Modal on close
		$('#myModal').on('hidden.bs.modal', function(e)
		{ 
			$('#score_table_cost').val("");
			$('#score_table_red').val("");
			$('#score_table_yellow').val("");			
			$('#errormsgModal').hide();
		}) ;
		
		// Clear Body error on modal open
		$('#myModal').on('shown.bs.modal', function(e)
		{ 
			$('#errormsgBody').hide();
		}) ;
		 
		
		//final_row_id = document.getElementById("cost_table").rows.length - 1;			
		
		//upload or file code
		$("#upload_link").on('click', function(e){
			e.preventDefault();
			$("#browse:hidden").trigger('click');
		});

		//Dropdown  code
		$(".dropdown-menu li a").click(function(){
			$(".dropdown-toggle:first-child").html($(this).text()+' <span class="arrow"></span>');
		});

		$('.btn-cost-table').click(function(){
			$('#costTable').show();
			$('.btn-create-strategy').show();   
			$('.btn-cost-table').hide();     
			$('#defineClass').hide();
		});

		$('.btn-create-strategy').click(function(){
			$('#defineClass').show();
			$('.btn-cost-table').show(); 
			$('.btn-create-strategy').hide();           
			$('#costTable').hide();
		});
		
		create_class_table(classHierarchy,strategy_name,strategyInfo[contractType]);
		
		$(document).on("click", '.edit-delete .edit', function(e){ 
			if(other_editable>0){
               // alert("save or cancel the edited data in other row first!");
				//document.getElementById("errormsgBody").removeAttribute("hidden");
				$('#errormsgBody').show();
				$('#errormsgBody').text("save or cancel the edited data in other row first!");
                return;
            }			
			e.preventDefault();
			$(this).closest('tr').find('.edit-input input').show();
			$(this).closest('tr').find('.val').hide();    
			$(this).closest('tr').find('.edit-delete').hide();   
			$(this).closest('tr').find('.save-cancel').show();  		
            prev_cost=document.getElementById(e.target.id.substring(0, 4)+"_cost").value;     
            prev_yel=document.getElementById(e.target.id.substring(0, 4)+"_yellow").value;    
            prev_red=document.getElementById(e.target.id.substring(0, 4)+"_red").value; 			
            other_editable=other_editable+1;              
		}); 
		
		
		
		$(document).on("click", '.edit-delete .delete', function(e){
			var row_remain = document.getElementById("cost_table").rows.length - 1;
			if(row_remain == 1) {
				$('#errormsgBody').show();
				$('#errormsgBody').text("Sorry You need to have atleast one row in the cost table.");
				return;
			}
			console.log("Showing the tab");
			$('#rowDelete').modal('show');
			console.log("Showing the tab again");			
			row_to_delete = document.getElementById(e.target.id.substring(0, 4));			
		}); 
		
		$('#delete_row').click(function(){
			row_to_delete.parentNode.removeChild(row_to_delete); 
			row_to_delete = null;
		});
		
		$('#dont_delete_row').click(function(){
			row_to_delete = null;
		});
		
		$(document).on("click", '.save-cancel .save', function(e){				
			e.preventDefault();			
			$(this).closest('tr').find('.edit-input input').hide();
			$(this).closest('tr').find('.val').show();    
			$(this).closest('tr').find('.edit-delete').show();   
			$(this).closest('tr').find('.save-cancel').hide();  
			commit_edit(e.target.id);			
			other_editable=other_editable-1;			
		});
		
		$(document).on("click", '.save-cancel .cancel', function(e){              
            e.preventDefault();
            $(this).closest('tr').find('.edit-input input').hide();
            $(this).closest('tr').find('.val').show();    
            $(this).closest('tr').find('.edit-delete').show();   
            $(this).closest('tr').find('.save-cancel').hide(); 
			//alert("prev cost is "+prev_cost); 			
			document.getElementById(e.target.id.substring(0, 4)+"_cost").value=prev_cost;     
			document.getElementById(e.target.id.substring(0, 4)+"_yellow").value=prev_yel;    
			document.getElementById(e.target.id.substring(0, 4)+"_red").value=prev_red;       
			other_editable=other_editable-1;            
        });
		
		$("#access_type_id").click(function(e) {
			modFlag = "U";
		});

	});
	
	
		
	
	