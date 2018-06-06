	
	$(document).ready(function() {
		
		document.getElementById("user_id_place").innerHTML = userId;
		document.getElementById("u_name_place").innerHTML = userName;			
		document.getElementById("staticScorePosition").innerHTML = staticScore+" "; 
			
		document.getElementById("fileNameSpace").innerHTML = fileName;	
		
		document.getElementById("name_of_strategy").innerHTML = "";
		document.getElementById("name_of_strategy").innerHTML = contractType;	
		
		if(dynamicAlert == null || dynamicAlert == ""){
			$("#dynamicShow").prop('disabled',true);	
			$("#scoreAlert").hide();
		}
		
		/* $("#dynamicShow").on("click", function(){
			var keyValue = "";
			document.getElementById("dynamicUL").innerHTML = "";
			for(var key in dynamicAlert){								
				document.getElementById("dynamicUL").innerHTML += "<li>"+ key + " :: " + dynamicAlert[key] +" </li>";
			}
		}); */
		
		$("#dynamicShow").on("click", function(){
			var keyValue = "";
			document.getElementById("dynamicUL").innerHTML = "";
			for(var key in dynamicAlert){
				var color = "green";
				if(dynamicAlert[key]["flag"] == true){
					color = "orange";
				}			
				document.getElementById("dynamicUL").innerHTML += "<li style='color:"+color+"'>"+ key + " :: " + dynamicAlert[key]["output"] +" </li>";
			}
		});
		
		// For putting tick/cross
		if((parseFloat(staticScore)).toFixed(2) < staticThreshold){
			document.getElementById("staticScoreImage").innerHTML = "<img src='./static/img/score-icon-bad.png' alt='' />"
		}else {
			document.getElementById("staticScoreImage").innerHTML = "<img src='./static/img/score-icon-good.png' alt='' />"
		}
		
		$("#staticView").on("click",function(e){
			$('#staticDynamicView').hide();
			$('#treeView').show();
			$('#dynamicClassTreeContent').hide();
			$('#staticClassTreeContent').show();
			jsonHelpData  = staticHelpData;
			$("#staticClassTree").empty();	
			document.getElementById("staticClassTree").innerHTML += '<div id="staticClassContentPopup" style="position:absolute; right:30px; width:350px; height:400px; border:1px solid #333; overflow-y: scroll; padding: 10px; display:None" ></div>';
			call_horizontal_tree("STATIC");
		});
		
		$("#dynamicView").on("click",function(e){
			$('#staticDynamicView').hide();
			$('#treeView').show();
			$('#staticClassTreeContent').hide();
			$('#dynamicClassTreeContent').show();
			jsonHelpData  = dynamicHelpData;
			$("#dynamicClassTree").empty();
			document.getElementById("dynamicClassTree").innerHTML += '<div id="dynamicClassContentPopup" style="position:absolute; right:30px; width:350px; height:400px; border:1px solid #333; overflow-y: scroll; padding: 10px; display:None" ></div>';
			
			// Here we have to ad condition as the following is only applicable to // field glass
			if((contractType == "MP - FG") || (contractType == "MS - FG")){
				document.getElementById("dynamicClassTree").innerHTML += '<div id="footNotePopup" style="position: absolute; right: 30px; width: 350px; height: 70px; top: 580px; border: 1px solid rgb(51, 51, 51); overflow-y: scroll; padding: 10px;"><button type="button" class="close" aria-label="Close"><span aria-hidden="true">×</span></button></div>';
				console.log("Comes inside...");
			}		
			
			call_dynamic_class_tree("DYNAMIC");
		});
		
		$("#linkToGenScore").on("click",function(e){
			if(localStorage.getItem("role") == "U"){
				window.location.replace("score-page_new.html");	
			}else if(localStorage.getItem("role") == "A"){
				window.location.replace("admin-score-page_new.html");	
			}else{
				console.log("Role User/Admin is not defined.");
				logout();
			}			
		});	

		calculateDynamicSCore(dynamicResult);	
		
		document.getElementById("user_id_place").innerHTML = userId;
		document.getElementById("u_name_place").innerHTML = userName;
		
		console.log("sow_file_name = ",fileName);
		var modified_filename = fileName;
		/* if(fileName.length > 25) {				
			modified_filename = fileName.substring(0,20) +"~"+fileName.substring(fileName.lastIndexOf('.'),fileName.length);
		} */
		console.log("modified_filename Name = ",modified_filename);			
				
		if(parseFloat(staticScore) > 90) { 
			//$("#linkToGenScore").hide();
			document.getElementById("linkToGenScore").style.visibility='hidden';
			$("#top-score-icon").removeClass("score-icon-cross");		
			$("#top-score-icon").addClass("score-icon-tick");				
		} else {
			$("#top-score-icon").removeClass("score-icon-tick");	
			$("#top-score-icon").addClass("score-icon-cross");		
		}
		
		if(Object.keys(dynamicAlert).length == 0){
			var d = document.getElementById("dynamicShow");
			d.className += " disable";
			document.getElementById("dynamicShow").disabled = true;
		}	
		calculateGeneralScoreForDownload(docClassificationResult,strategyThreshold);		
	});
	
	function calculateDynamicSCore(dynamicResult){
		var positive = 0;
		var total = 0;
		for(var i in dynamicResult) {	
			total += 1;		
			if(dynamicResult[i]){
				positive += 1;
			}
		}

		// Excluding the class CiscoPreExistingProperty for Field Glass
		if(contractType == "MP - FG" || contractType == "MS - FG"){
			total = total - 1;
		}
		
		dynamicSCore = (positive/ total) * 100;
		console.log(total);
		console.log(positive);
		console.log(dynamicSCore);
		// Rendering the dynamic score gauge
		render_curve(Math.round(dynamicSCore),redMaxDynamic,yellowMaxDynamic,scoreCardFlag);	
		
		// Putting the Dynamic score in position
		document.getElementById("dynamicScorePlace").innerHTML = "";
		document.getElementById("dynamicScorePlace").innerHTML = Math.round(dynamicSCore);
		document.getElementById("dynamicScoreTopPosition").innerHTML = Math.round(dynamicSCore);
	}
	
	
	function calculateGeneralScoreForDownload(classificationResult,strategyThreshold){
		
		setInitialValues(generalHierarchy);
		
		calculate_KPI_List(classificationResult,strategyThreshold);
		// This function will calculate the KPIClassesBool & KPIClassesConfidence.		
		calculate_KPI_List(classificationResult,strategyThreshold);
		
		// list_1 will be needed for class Tree
		list_1 = KPIClassesBool;
		
		// This function will set the KPIList & classesContent
		initialize_lists(classificationResult);		
				
		// This function will extract the strategy info for the current strategy and will call another function that will create the initial scoren table.
		create_user_object(strategyInfo);	
		
		// SOW score will be calculated here
		calculate_SOW_score(false);
	}	
	
	
	
	