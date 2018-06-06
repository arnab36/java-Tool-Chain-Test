
setUpClassHierarchy(classHierarchy);
loadStrategyViewTable(firstLevelClassCategory);

$(document).ready(function() {
	document.getElementById("user_id_place").innerHTML = userId;
	document.getElementById("u_name_place").innerHTML = userName;
		
	$('#contractTypeEdit').val(contractType);
	
	$(document).on("click", '.delete', function(e){				
		e.preventDefault();			
		console.log(e.target.id);
		var elem_id= e.target.id;		
		last_clicked_strategy_name = elem_id.substring(elem_id.indexOf('#')+1, elem_id.lastIndexOf('.'));	
		//last_clicked_strategy_name = elem_id.substring(firstIndex+1,elem_id.lastIndexOf('.'));
		console.log("strategyName = ",last_clicked_strategy_name);
	});
	
	
	$('#delete_strategy').click(function(){		
		console.log("Startegy to be deleted is = ",last_clicked_strategy_name);
		var index,flag = false;					
		
		var temp = strategyInfoHierarchy[userId][contractType];		
		for(var i in temp){ 
			if(temp[i]["StrategyName"] == last_clicked_strategy_name){
				temp.splice(i,1);
				break;
			} 
		} 
		
		temp1 = allStrategyInfo[contractType];
		for(var i in temp1){ 
			if(temp1[i]["name"] == last_clicked_strategy_name){
				temp1.splice(i,1);
				break;
			} 
		} 		
		
		var deleteFlag = deleteStrategyAjax(allStrategyInfo);
		console.log(deleteFlag);
		if(deleteFlag){					
			localStorage.removeItem("strategyInfo");
			localStorage.setItem("strategyInfo",JSON.stringify(allStrategyInfo)); 
			localStorage.removeItem("strategyInfoHierarchy");
			localStorage.setItem("strategyInfoHierarchy",JSON.stringify(strategyInfoHierarchy)); 			
			last_clicked_strategy_name = "";
			
			window.location.reload();
		}else {
			window.location.reload();
		}
		
	});
	
	
	$('#dont_delete_strategy').click(function(){		
		console.log("Startegy to be deleted is = ",last_clicked_strategy_name);
		last_clicked_strategy_name = "";
	});
	
	$('#dont_copy_strategy').click(function(){		
		console.log("Startegy to be deleted is = ",last_clicked_strategy_name);
		last_clicked_strategy_name = "";
		last_clicked_user_key = "";
	});
	
	// Edit strategy
	$(document).on("click", '.edit', function(e){	
		var elem_id= e.target.id;	
		var firstIndex = elem_id.indexOf('#');	
		var strategy_name = elem_id.substring(firstIndex+1,elem_id.lastIndexOf('.'));					
		sessionStorage.setItem("strategyName",strategy_name);	
		window.location.replace("strategy-copy-edit.html");
	});
	
	
	$(document).on("click", '.copy', function(e){		
		var elem_id= e.target.id;		
		var strategy_name = elem_id.substring(elem_id.indexOf('#')+1,elem_id.lastIndexOf('.'));		
		var json_key = elem_id.substring(0,elem_id.indexOf('#'));
		var accessType = elem_id.substring(elem_id.lastIndexOf('#')+1,elem_id.length ); 
		last_clicked_strategy_name = strategy_name;
		last_clicked_user_key = json_key;	
		document.getElementById('prev_strategy_name').innerHTML  = "";
		document.getElementById('prev_strategy_name').innerHTML += last_clicked_strategy_name;
		console.log("json_key :: ",json_key);
		console.log("strategy_name :: ",strategy_name);
		console.log("accessType :: ",accessType);
	});
	
	// Copying strategy
	$("#copy_strategy").click(function(e){	
		var new_strategy_name = $('#new_strategy_name').val();
		if(new_strategy_name == ""){
			alert("Please enter a strategy name.....");
			$('#new_strategy_name').focus();	
			return;			
		}		
				
		//var list = strategyList["own"];
		var flag = check_unique(new_strategy_name);
		if(flag){
			alert("Strategy name already exist. Please choose a different name");
				$('#new_strategy_name').val("");	
				$('#new_strategy_name').focus();	
				return;
		}
		copy_strategy(last_clicked_user_key,last_clicked_strategy_name,new_strategy_name);			
	});	
	
	if(localStorage.getItem("role") == "A"){
		load_strategies_admin(userId)
	}else {
		load_strategies_user(userId);
	}	
});


/*
	The following function will call CreateEditServlet and update obj storage 
	and dashDB accordingly.
	Take the value from strategyInfo and delete the strategy first and then 
	send that object to server.
*/
	function deleteStrategyAjax(obj){
		var json_data = JSON.stringify(obj);
		var success = false;
		$("#wrapper *").prop("disabled", true);
		$.ajax({
				type: "POST",
				url: "CreateEditServlet",
				async: false,
				data: { 
						param_1: json_data,
						strategyName: last_clicked_strategy_name,
						stratType: contractType,
						accessType: "Null",
						modifyFlag: "D"
				},
				success: function () {
					$("#wrapper *").prop("disabled", false);
					console.log("message success is = ");	
					success = true;					
				},
				error: function(xhr, textStatus, errorThrown){	
					$("#wrapper *").prop("disabled", false);
					console.log(xhr);								
					console.log(errorThrown);
					alert(errorThrown);
					alert(xhr.responseText);
					return false;
				}					
		});
		return success;
	}
	
	
	function load_strategies_user(userId){
		$("#view_user_strategies").find("tr:gt(0)").remove();			
		var strategy_name, strategy_list,access_type,date, u_id,isAdmin = false;	
		var strategyInfoHierarchy = JSON.parse(localStorage.getItem("strategyInfoHierarchy"));	
		for(var eachUserNameKey in strategyInfoHierarchy){
			var eachUser = strategyInfoHierarchy[eachUserNameKey];
			var listStrategy = eachUser[contractType];
			for(var j in listStrategy){
				if(listStrategy[j]["accessType"] == "Pub"){
					strategy_name = listStrategy[j]["StrategyName"];
					access_type = "Pub";
					var _tr =   '<tr>' +
					'<td>'+'<img src="./static/img/user-pic.png" border=2 height=35 width=35 >'+'</td>'+
					'<td>'+eachUserNameKey+'</td>'+
					'<td>'+strategy_name+'</td>'+
					'<td>'+access_type+'</td>'+
					'<td>'+
				    '<div class="buttons">'+
					'<a title="View" href="#strategyView" data-toggle="modal" data-target="#strategyView">' +
					'<i class="view" id="'+eachUserNameKey+"#"+strategy_name+'.view#'+access_type+'" onclick="create_initial_view_table(this.id);"> </i>' +
					'</a>'+'</div>'+'</td>'+'</tr>';
					$('#view_user_strategies > tbody:last-child').append(_tr);
				}				
			}
		}
	}
	
	
	function load_strategies_admin(userId){
		$("#view_user_strategies").find("tr:gt(0)").remove();			
		var strategy_name, strategy_list,access_type,date, u_id,isAdmin = false;	
		var strategyInfoHierarchy = JSON.parse(localStorage.getItem("strategyInfoHierarchy"));	
		for(var eachUserNameKey in strategyInfoHierarchy){
			var eachUser = strategyInfoHierarchy[eachUserNameKey];
			var listStrategy = eachUser[contractType];
			if(eachUserNameKey == userId){
				for(var j in listStrategy){ 
					var strategy_name = listStrategy[j]["StrategyName"];
					var access_type = listStrategy[j]["accessType"];
					var _tr =   '<tr>' +
						'<td>'+'<img src="./static/img/user-pic.png" border=2 height=35 width=35 >'+'</td>'+
						'<td>'+"You"+'</td>'+
						'<td>'+strategy_name+'</td>'+
						'<td>'+access_type+'</td>'+
						'<td>'+
					   '<div class="buttons">'+
						'<a title="View" href="#strategyView" data-toggle="modal" data-target="#strategyView">' +
						'<i class="view" id="'+eachUserNameKey+"#"+strategy_name+'.view#'+access_type+'" onclick="create_initial_view_table(this.id);"> </i>' +
						'</a>'+
						'<span class="devider"> </span>'+
						'<a title="Copy" href="#" data-toggle="modal" data-target="#strategyCopy">'+
							'<i class="copy" id="'+eachUserNameKey+"#"+strategy_name+'.copy#'+access_type+'" ></i>'+
						'</a>'+
						'<span class="devider"> </span>'+
						'<a title="Edit" href="#" data-toggle="modal" data-target="#strategyEdit">' +
						'<i class="edit" id="'+eachUserNameKey+"#"+strategy_name+'.edit" > </i>' +
							'</a>'+									
								'<span class="devider"> </span>'+
							'<a title="Delete" href="#" data-toggle="modal" data-target="#strategyDelete"> <i class="delete" id="'+eachUserNameKey+"#"+strategy_name+'.delete"></i></a>'+
						'</div>'+
						'</td>'+
					'</tr>';
					$('#view_user_strategies > tbody:last-child').append(_tr);
				}
			}else {
				for(var j in listStrategy){ 
					if(listStrategy[j]["accessType"] == "Pub"){
						var strategy_name = listStrategy[j]["StrategyName"];
						var access_type = "Pub";
						var _tr =   '<tr>' +
						'<td>'+'<img src="./static/img/user-pic.png" border=2 height=35 width=35 >'+'</td>'+
						'<td>'+eachUserNameKey+'</td>'+
						'<td>'+strategy_name+'</td>'+
						'<td>'+access_type+'</td>'+
						'<td>'+
					   '<div class="buttons">'+
						'<a title="View" href="#strategyView" data-toggle="modal" data-target="#strategyView">' +
						'<i class="view" id="'+eachUserNameKey+"#"+strategy_name+'.view#'+access_type+'" onclick="create_initial_view_table(this.id);"> </i>' +
						'</a>'+
						'<span class="devider"> </span>'+
						'<a title="Copy" href="#" data-toggle="modal" data-target="#strategyCopy">'+
							'<i class="copy" id="'+eachUserNameKey+"#"+strategy_name+'.copy#'+access_type+'" ></i>'+
						'</a>'+
						'</div>'+
						'</td>'+
					'</tr>';
					$('#view_user_strategies > tbody:last-child').append(_tr);
					}
				}
			}
		}			
	}

	
/*
	The following function will load the list of strategies available for the current user.
*/	
	function load_strategies(userId){		
	
		$("#view_user_strategies").find("tr:gt(0)").remove();			
		var strategy_name, strategy_list,access_type,date, u_id,isAdmin = false;	
		var strategyInfoHierarchy = JSON.parse(localStorage.getItem("strategyInfoHierarchy"));		
		for(var eachUserNameKey in strategyInfoHierarchy){
			var b = strategyInfoHierarchy[eachUserNameKey];
			if((localStorage.getItem("role") == "A") && (userId == eachUserNameKey)){
				u_id = "you";
				isAdmin = true;
			}else {
				u_id = eachUserNameKey;
				isAdmin = false;
			}
			var listStrategy = b[contractType];
			for(var j in listStrategy){
				if(!isAdmin){
					if(listStrategy[j]["accessType"] == "Pub"){
						strategy_name = listStrategy[j]["StrategyName"];
						access_type = "Pub";
					}
				}else {
					strategy_name = listStrategy[j]["StrategyName"];
					access_type = listStrategy[j]["accessType"];
				}
				date = dateString;	
				var _tr =   '<tr>' +
						'<td>'+'<img src="./static/img/user-pic.png" border=2 height=35 width=35 >'+'</td>'+
						'<td>'+u_id+'</td>'+
						'<td>'+strategy_name+'</td>'+
						'<td>'+access_type+'</td>'+
						'<td>'+date+'</td>'+
						'<td>'+
						   '<div class="buttons">'+
								'<a title="View" href="#strategyView" data-toggle="modal" data-target="#strategyView">' +
									'<i class="view" id="'+eachUserNameKey+"#"+strategy_name+'.view#'+access_type+'" onclick="create_initial_view_table(this.id);"> </i>' +
								'</a>';
								
				if(isAdmin){
					_tr += '<span class="devider"> </span>'+
								'<a title="Copy" href="#" data-toggle="modal" data-target="#strategyCopy">'+
									'<i class="copy" id="'+eachUserNameKey+"#"+strategy_name+'.copy#'+access_type+'" ></i>'+
								'</a>'+
									'<span class="devider"> </span>'+
							   '<a title="Edit" href="#" data-toggle="modal" data-target="#strategyEdit">' +
									'<i class="edit" id="'+eachUserNameKey+"#"+strategy_name+'.edit" > </i>' +
								'</a>'+									
									'<span class="devider"> </span>'+
								'<a title="Delete" href="#" data-toggle="modal" data-target="#strategyDelete"> <i class="delete" id="'+eachUserNameKey+"#"+strategy_name+'.delete"></i></a>'+
							'</div>'+
						'</td>'+
					'</tr>';
				}
									
			$('#view_user_strategies > tbody:last-child').append(_tr);
			}
		}
	}
	
	
		
	// This function is for opening startegy information
	function create_initial_view_table(id){	
		
		console.log("ID received = ",id);
		var json_key = id.substring(0,id.indexOf('#'));			
		var strategy_name = id.substring(id.indexOf('#')+1, id.lastIndexOf('.'));
		var accessType = id.substring(id.lastIndexOf('#')+1,id.length);

		console.log("json_key is = ",json_key);
		console.log("strategy_name is = ",strategy_name);
		console.log("accessType :: ",accessType);
		$("#wrapper *").prop("disabled", true);
				
		$.ajax({
			type: 'POST',
			url: 'GetStrategyInfoFromObjectStorage',
			async: false,
			data: { 
					userId : json_key,
					stratType: contractType,					
					strategyName: strategy_name,
					allInfo : "No"
			},
			success: function (strategyContent) {
				console.log("Strategy info is :: ", strategyContent);
				user_strategy_data = (JSON.parse(strategyContent["singleStrategyInfo"]))["class"];
				console.log("========= Strategy Received ============");
				console.log(user_strategy_data);
				$("#wrapper *").prop("disabled", false);
			},
			error: function(xhr, textStatus, errorThrown){								
				console.log(xhr);								
				console.log(errorThrown);
				alert(errorThrown);
				alert(xhr.responseText);
				$("#wrapper *").prop("disabled", false);
			}				
		});			
		
				
		// user_strategy_data holds the class information
		document.getElementById("strategy_name_space").innerHTML = strategy_name;
		
		// Remove the previous tables
		for(var key in user_strategy_data){
			table_id = key.toString()+"_Table";				
			$("#"+table_id).find("tr").remove();			
		}
		
		// Create the new table again
		$('#strategy_name').val(strategy_name);
		var _tr,_tr1,_tr2,_tr3,table_id,class_name,count,mandatory,weighted_score,color_indicator,check_flag;
		var class_list = [];
		var row_info = {};
		
		// Formula for weighted score will be implemented later
		for(var key in user_strategy_data){
			table_id = key.toString()+"_Table";			
			class_list = user_strategy_data[key];
			for(var i in class_list) {
				row_info = class_list[i];				
				_tr1 = '<tr>' +
						'<td>' +
							'<div class="checbox"> <input type="checkbox" value="" checked disabled>  </div> ' +                          
						'</td>'+
						'<td class="class-name">'+row_info["class_name"]+'</td>'+
						'<td> <div class="raw-score-tbox"> ' +
								'<input type="number" id="raw_score_'+row_info["class_name"]+'" value="'+row_info["raw_score"]+'" min="0" max="100" onchange="checkDecimal(this.id)" />'  +
								'<span class="raw-score-val" id="raw_score_span_'+row_info["class_name"]+'">'+row_info["raw_score"]+'</span> ' +
							'</div> ' +
						'</td>' +
						'<td> <div class="weight-tbox">'+
								'<input type="number" id="weight_'+row_info["class_name"]+'" value="'+row_info["weight"]+'" min="0" max="100" onchange="checkDecimal(this.id)" />'  +
								'<span class="weight-val" id="weight__span_'+row_info["class_name"]+'">'+row_info["weight"]+'</span> ' + 
							'</div> </td>' +
						'<td>'+
							'<div class="min-count-tbox"> '+
								'<input type="number" id="min_count_'+row_info["class_name"]+'" value="'+row_info["min_count"]+'" min="0" max="100" onchange="checkDecimal(this.id)" />'  +
								'<span class="min-count-val" id="min_count_span_'+row_info["class_name"]+'">'+row_info["min_count"]+'</span> ' + 
							'</div>'+
						'</td>';
				
				// Checking for mandatory class
				if(row_info["mandatory"]){
					_tr2 = 	'<td> ' +
								//'<div class="checbox"> <input class="check-mand" type="checkbox" id="mandatory_'+row_info["class_name"]+'" value="" checked>  </div> ' +     
								'<span> No </span> <label class="switch">  <input type="checkbox" class="check-mand mandate" id="mandatory_'+row_info["class_name"]+'" checked> <div class="slider round"></div> </label> <span> Yes </span>' +
							'</td>' ;
				}else {
					_tr2 = 	'<td> ' +
								//'<div class="checbox"> <input class="check-mand" type="checkbox" id="mandatory_'+row_info["class_name"]+'" value="">  </div> ' +           
								'<span> No </span> <label class="switch">  <input type="checkbox" class="check-mand mandate" id="mandatory_'+row_info["class_name"]+'" > <div class="slider round"></div> </label> <span> Yes </span>' +
							'</td>' ;
				}			
				
						
				_tr3 = 	'<td>'+
							'<div class="min-score-tbox"> '+
								'<input type="number" id="min_score_'+row_info["class_name"]+'" value="'+row_info["min_score"]+'" min="0" max="100" onchange="checkDecimal(this.id)" />'  +
								'<span class="min-score-val" id="min_score_span_'+row_info["class_name"]+'">'+row_info["min_score"]+'</span> ' + 
							'</div>'+
						'</td>' +						
					'</tr>';
				
				_tr = _tr1 + _tr2 + _tr3;
				$('#'+table_id +'> tbody:last-child').append(_tr);			
			}
		}	
		
		/* 
			Incase after edit we will be able to view the entire table again with text
			boxes hidden and spans shown.
		*/
		$('.raw-score-tbox input').hide();
		$('.weight-tbox input').hide();
		$('.min-count-tbox input').hide();
		$('.min-score-tbox input').hide();
		
		$('.raw-score-val').show();
        $('.weight-val').show();
		$('.min-count-val').show();
		$('.min-score-val').show();
		
		// disabling mandatory check box
		$('.check-mand').attr("disabled", true);
	}

	// This following function will copy a strategy
	function copy_strategy(json_key,strategy_name,new_strategy_name){
		console.log("json_key = ", json_key);
		console.log("strategy_name = ", strategy_name);
		console.log("new_strategy_name = ", new_strategy_name);
		
		var copyFlag = false;
				
		// Assigning date
		var dateString = "";
		var dNow = new Date();		
		dateString += (dNow.getMonth() + 1) + "/";  
		dateString += dNow.getDate() + "/";  
		dateString += dNow.getFullYear();  
		
		var access, errorFlag = true;
		var stratData = null;
		
		$("#wrapper *").prop("disabled", true);		
		$.ajax({
			type: 'POST',
			url: 'CopyStrategyServlet',
			async: false,
			data: { 
					userId : json_key,
					stratType: contractType,
					new_strategy_name: new_strategy_name,
					strategyName: strategy_name,
					date: dateString
			},
			success: function (message) {				
				allStrategyInfo = JSON.parse(message["data"]);				
				$("#wrapper *").prop("disabled", false);
				copyFlag = true;
			},
			error: function(xhr, textStatus, errorThrown){								
				console.log(xhr);								
				console.log(errorThrown);
				//alert(errorThrown);
				//alert(xhr.responseText);
				access = xhr.responseText; 
				errorFlag = false;
				$("#wrapper *").prop("disabled", false);
			},
				
		});	
		
		console.log(copyFlag);
		if(copyFlag){
			var newObj = {};			
			newObj["accessType"] = "Pub";
			newObj["StrategyName"] = new_strategy_name;			
			strategyInfoHierarchy[userId][contractType].push(newObj);
			changeStrategyInfoHierarchy(allStrategyInfo,new_strategy_name);
			
			localStorage.removeItem("strategyInfo");
			localStorage.setItem("strategyInfo",JSON.stringify(allStrategyInfo)); 
			localStorage.removeItem("strategyInfoHierarchy");
			localStorage.setItem("strategyInfoHierarchy",JSON.stringify(strategyInfoHierarchy)); 			
			last_clicked_strategy_name = "";
		}
		window.location.reload();		
	}	
	
	
	function changeStrategyInfoHierarchy(strategyInfo,strategy_name){
		var temp = strategyInfo[contractType];
		for(var i in temp){
			if(temp[i]["name"] == strategy_name){
				var acc = temp[i]["access"];
				var temp1 = strategyInfoHierarchy[userId][contractType];
				for(var j in temp1){
					if(temp1[j]["StrategyName"] == strategy_name){
						temp1[j]["accessType"] = acc.substring(0,3);
					}
				}
				
			}
		}
	}
	

