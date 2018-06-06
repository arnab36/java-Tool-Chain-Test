// This javascript page contains the view and edit strategy from score page. Here new class can not be added to strategy 
	
	
	$('#just_view').click(function() {
		document.getElementById('modal_title_view_edit').innerHTML = "";
		document.getElementById('modal_title_view_edit').innerHTML = "View Strategy";
	});
	
	$('#just_edit').click(function() {
		document.getElementById('modal_title_view_edit').innerHTML = "";
		document.getElementById('modal_title_view_edit').innerHTML = "Edit Strategy";
	});
	
	$('#just_view_mobile').click(function() {
		document.getElementById('modal_title_view_edit').innerHTML = "";
		document.getElementById('modal_title_view_edit').innerHTML = "View Strategy";
	});
	
	$('#just_edit_mobile').click(function() {
		document.getElementById('modal_title_view_edit').innerHTML = "";
		document.getElementById('modal_title_view_edit').innerHTML = "Edit Strategy";
	});
	
	
	
	function create_initial_view_table(userJsonData){	
		
		var date = strategyInfo["Date"];
		var access =  strategyInfo["access"];
		document.getElementById('created-date').innerHTML = "";
		document.getElementById('created-date').innerHTML = date;
		document.getElementById('access-value').innerHTML = "";
		document.getElementById('access-value').innerHTML = access;
		
			
		// Remove the previous tables
		for(var key in userJsonData){
			table_id = key.toString()+"_Table";			
			//$("#"+table_id+" > tbody").html("");
			$("#"+table_id).find("tr").remove();			
		}		
		
		// Create the new table again
		$('#strategy_name').val(strategyName);
		var _tr,_tr1,_tr2,_tr3,table_id,class_name,count,mandatory,weighted_score,color_indicator,check_flag;
		var class_list = [];
		var row_info = {};
		
		// Formula for weighted score will be implemented later
		for(var key in userJsonData){
			table_id = key.toString()+"_Table";			
			class_list = userJsonData[key];
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
								'<span> No </span> <label class="switch">  <input type="checkbox" class="check-mand mandate" id="mandatory_'+row_info["class_name"]+'"  checked> <div class="slider round"></div> </label> <span> Yes </span>' +  
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
	
	
	
	/*
		The following function will make changes in the strategy info and save it to 
		Json object and also to the local.
	*/
	
	/* function edit_strategy(){
		class_list = [];
		var row_info = {};		
		for(var key in userJsonData){
			class_list = userJsonData[key];
			for(var i in class_list) {
				row_info = class_list[i];			
				row_info["raw_score"] = $('#'+'raw_score_'+row_info["class_name"]).val();
				row_info["weight"] = $('#'+'weight_'+row_info["class_name"]).val();
				
				if($('#'+'mandatory_'+row_info["class_name"]).is(':checked')) {
					row_info["mandatory"] = true;
				}else {
					row_info["mandatory"] = false;
				}
				
				row_info["min_count"] = $('#'+'min_count_'+row_info["class_name"]).val();
				row_info["min_score"] = $('#'+'min_score_'+row_info["class_name"]).val();
				class_list[i] = row_info;
			}
			userJsonData[key] = class_list;
		}	
		strategyInfo["class"] = userJsonData;
		
		var dateString = "";
		var dNow = new Date();		
		dateString += (dNow.getMonth() + 1) + "/";  
		dateString += dNow.getDate() + "/";  
		dateString += dNow.getFullYear();  
		strategyInfo["Date"] = dateString;
		
		var strategyList = allStrategyInfo[contractType];		
		for(var i in strategyList){
			if(strategyList[i]["name"] == strategyName){
				strategyList[i] = strategyInfo;
				break;
			}
		}		
		
		// User's strategy info is inserted into the main strategy json data
		//strategy_json_data[jsonKey_userId] = user_json_data;			
		
		// Now ajax call to save the strategy 
		var json_data_1 = JSON.stringify(allStrategyInfo);				
		$.ajax({
			type: "POST",
			url: "saveStrategyServlet",
			async: false,
			data: { 
				param_1: json_data_1					
			}					
		});	
		
		call_horizontal_tree();			
		$('#strategyEditView').modal('hide');
		// Creating the table again
		create_initial_view_table();		
		load_initial_page();	
		render_curve();
		set_mandatory_score();	
		
	} */

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	