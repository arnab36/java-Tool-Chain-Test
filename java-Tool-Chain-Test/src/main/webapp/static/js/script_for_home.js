	
	/* The following GET method will get strategy information from object storage */
	function loadStrategyServlet(){	
		var frm = $("#loadStrategyForm");	
		$.ajax({
			type: 'GET',
			url:'UploadServlet',
			data: frm.serialize(),
			async: true,
			success:function(strategy){
				console.log("Success Logout:: ", strategy);
				try {
					localStorage.setItem("strategyInfoHierarchy",JSON.stringify(JSON.parse(strategy["jsonObject"])));
				}catch(err){
					localStorage.setItem("strategyInfoHierarchy","");
					console.log(err);
				}				
				ProcessStrategyList();				
			},
			error:function(error){
				console.log("Error in getting strategy:: ", error);
			},
			cache: false,
			contentType: false,
			processData: false
		});
	}
	
	/* Process the strategy information to fill strtegy list */	
	function ProcessStrategyList(){	
	
		var strategyHierarchy = {};
		/* Error handling */
		try{
			strategyHierarchy = JSON.parse(localStorage.getItem("strategyInfoHierarchy"));
		}catch(err){
		  console.log(err);
		}
		
		/* Checking if we have any strategy at all */
		if(isEmpty(strategyHierarchy)) { 
			return;
		}
		
		for(var i in strategyHierarchy){
			var mpList = strategyHierarchy[i]["MP"];
			for(var j in mpList){
				if(mpList[j]["accessType"] == "Pub"){
					strategy_list_MP[mpList[j]["StrategyName"]] = i;
				}
			}
			
			var msList = strategyHierarchy[i]["MS"];
			for(var k in msList){
				if(msList[k]["accessType"] == "Pub"){
					strategy_list_MS[msList[k]["StrategyName"]] = i;
				}
			}
		}		
		changeStrategyList(strategy_list_MP);
	}
	
	
	/* Done button functionalities */
	$('#done').click(function(){
		if(input_file_name != ""){		
			$('.sow-details-row *').prop('disabled',false);
			$('.create-strategy-row *').prop('disabled',false);
			$('.sow-details h2').click();
		}else {
			document.getElementById("errormsg").removeAttribute("hidden");
			$('#errormsg1').text("Please Select a File First.");  
		}
	});
	
	/*
		When a strategy is selected/changed this function is called
	*/
	function select_strategy_name(id){
		console.log("called  = ",id);
		strategyName = id;
		$('#strategyName').val(id);
		document.getElementById("ChooseStrategy").innerHTML = "";
		document.getElementById("ChooseStrategy").innerHTML =id + '  <span class="arrow"></span>';		
		document.getElementById("errormsg").setAttribute("hidden","true");
		if($("#browse").val().length > 0){		
			$('#upload-button').attr('disabled',false);	
		}		
		console.log("setStrategyInfo has to be called");		
	}
	
	
	/*
		File is selected, then this function is called.
	*/	
	$('input[name="file1"]').change(function(){
		document.getElementById("errormsg").setAttribute("hidden","true");		
		input_file_name = $('input[type=file]').val().split('\\').pop();			
		var ext = input_file_name.substring(input_file_name.lastIndexOf('.'),input_file_name.length);	
		var modified_filename = input_file_name;
		
		console.log("Coming inside browse...");
		console.log("input_file_name :: ", input_file_name);

		if(ext == ".pdf" || ext == ".doc" || ext == ".docx"){	
			flag = 2;
			console.log("flag changed to :: ", flag);
			$("#hidden_flag").val(2);			
			if(input_file_name.length > 25) {				
				modified_filename = input_file_name.substring(0,20) +"~"+input_file_name.substring(input_file_name.lastIndexOf('.'),input_file_name.length);
			}			
			$("#input_file_name").val(input_file_name);
			$("#input_file_name_doc").val(input_file_name);
			$("#doc_name").text("");
			$("#doc_name").text(modified_filename); 
			$(".browse-file").show();
			console.log("Modified File Name :: " , modified_filename);
		}	
		else {
			input_file_name ="";
			files = "";
			document.getElementById("errormsg").removeAttribute("hidden");
			$('#errormsg1').text("Only Pdf, Doc, Docx are allowed.");  
			return;
		}		   
	});	
	
	$('.browse-file .file-close').click(function(e){		
		$(".browse-file").hide();
		e.stopPropagation();
		flag = 0;	
		$(".browse-file").hide();
		console.log("flag changed to :: ", flag);		
	}); 	
	
	/* When Contract Type is changed this function is called to change the strategy list value  */
	function changeStrategyList(list){
		$('#strategyName').val("");
		//document.getElementById("strategyid").innerHTML +="";
		document.getElementById("ChooseStrategy").innerHTML = "";
		document.getElementById("ChooseStrategy").innerHTML = 'Choose	<span class="arrow"></span>';
		document.getElementById("strategyid").innerHTML = "";		
		for (var i in list) { 
			var tempStrategyName="<li id="+i+" role=\"presentation\" onclick=\"select_strategy_name('"+i+"')\"><a role=\"menuitem\" tabindex=\"-1\" href=\"#\">"+i+"</a></li>";			
			document.getElementById("strategyid").innerHTML += tempStrategyName;
		}		
	}
	
	
	/* 
		The following function will call document conversion. After successfully converting
		it will call document classification.
	*/
	function callDocumentConversion(){			
		$("#user_id").val(userId);		
		var formData = new FormData($("#file_upload_form")[0]);				
						
		$.ajax({
			//url: '/ContractsAdvisor/UploadServlet',
			url: 'UploadServlet',
			method: 'post',
			data:formData,						
			async: true,
			success: function(successResponse) {
			//	text_log(successResponse["data"]);
				console.log("successResponse :: ",successResponse);
				if(successResponse["error"] == null || successResponse["error"] =="" ||successResponse["error"] =="null"){						
					console.log("successResponse is :: \n", successResponse);					
					$('#input_file_name').val(successResponse["fileName"]);
					console.log("No Error :: Doc convert is disabled.");
					CallAnalyze();
				}else {
					$('#errormsg1').text(successResponse["error"]);
					document.getElementById("errormsg").removeAttribute("hidden");
					console.log("With Error :: ",successResponse["error"]);
					$('#loadingDiv').hide();
					document.getElementById("process-button").disabled = false;
				}										
			},
			error: function(errorResponse){	
				$('#loadingDiv').hide();	
				console.log(errorResponse);
				//text_log("Error :: Some problems occured. Please try again.");
				$('#errormsg1').text("Error :: Some problems occured during document conversion. Please try again.");
				document.getElementById("errormsg").removeAttribute("hidden");
				document.getElementById("process-button").disabled = false;
			},
			cache: false,
			contentType: false,
			processData: false
		});	
				
	}
	
	/*
		This function calls analyze servlet
	*/
	function CallAnalyze(){		
		var infoSec = false;	
		
		if($('#infoSecurity').is(":checked")){
			infoSec = true;
		}

		$('#loadingDiv').show();
		var dtext ="Std language classification is started";
		//text_log(dtext);
		console.log("Analyze button clicked");
		// Dummy variables are created and sent to next page
		var threshold = 0.7;	
		
		$('#analyzeUserId').val(userId);
		
		var frm = $("#analyze_form");
		$.ajax({
			type: frm.attr('method'),
			url: frm.attr('action'),
			data: frm.serialize(),
			async: true,
			success: function(successResponse){
				console.log("Inside success ## Result is :: \n", successResponse);	
				
				sessionStorage.setItem("fileName",input_file_name);							
				sessionStorage.setItem("contractType",selectedProject);				
				sessionStorage.setItem("strategyName",strategyName);
				
				try {
					sessionStorage.setItem("staticResult",JSON.stringify(JSON.parse(successResponse["staticResult"])));	
					sessionStorage.setItem("staticHierarchy",JSON.stringify(JSON.parse(successResponse["staticHierarchy"])));					
					sessionStorage.setItem("staticScore",successResponse["staticScore"]);
				}catch(err){
					console.log(err);
				}
				
				
				try{
				sessionStorage.setItem("generalHierarchy",JSON.stringify(JSON.parse(successResponse["generalHierarchy"])));
				sessionStorage.setItem("generalResult",JSON.stringify(JSON.parse(successResponse["generalResult"])));
				sessionStorage.setItem("generalHelpClasses",JSON.stringify(JSON.parse(successResponse["generalHelpClasses"])));
				sessionStorage.setItem("staticHelpClasses",JSON.stringify(JSON.parse(successResponse["staticHelpClasses"])));
				sessionStorage.setItem("missingStatic",JSON.stringify(JSON.parse(successResponse["missingStatic"])));
				}catch(err){
					console.log(err);
				}
				
				
				try {
				sessionStorage.setItem("dynamicAlert",JSON.stringify(JSON.parse(successResponse["dynamicAlert"])));
				sessionStorage.setItem("dynamicResult",JSON.stringify(JSON.parse(successResponse["dynamicResult"])));
				sessionStorage.setItem("dynamicHierarchy",JSON.stringify(JSON.parse(successResponse["dynamicHierarchy"])));
				sessionStorage.setItem("dynamicHelpClasses",JSON.stringify(JSON.parse(successResponse["dynamicHelpClases"])));
				sessionStorage.setItem("dynamicPopUpContent",JSON.stringify(JSON.parse(successResponse["dynamicPopUpContent"])));
				}catch(err){
					console.log(err);
				}
								
				if(successResponse["error"] == null || successResponse["error"] =="" || successResponse["error"] =="null"){
					window.location.replace("score-static-dynamic.html");	
				}
				else{
					$('#errormsg1').text(successResponse["error"]);
					document.getElementById("errormsg").removeAttribute("hidden");
					$('#loadingDiv').hide();
				} 								
			},
			error: function(errorResponse) {
				sessionStorage.setItem("threshold",threshold);
				sessionStorage.setItem("classHierarchy",JSON.stringify(classHierarchy));
				sessionStorage.setItem("helpClasses",JSON.stringify(helpClasses));
				sessionStorage.setItem("fileName",input_file_name);
							
				$('#loadingDiv').hide();
				$('#errormsg1').text("Error :: Some problems occured. Please try again.");
				document.getElementById("errormsg").removeAttribute("hidden");
				document.getElementById("process-button").disabled = false;
			}
		});
	}
	
	
	/* Going to view all startegies page. Required for Admin page */
	$(document).on("click","#view_all_strategies", function(){
		sessionStorage.setItem("classHierarchy",JSON.stringify(classHierarchy));
		sessionStorage.setItem("contractType",contractType);
		console.log("StrategyInfoServlet is called :: ",selectedProject);
		$.ajax({
			type: 'POST',
			url: 'StrategyInfoServlet',
			async: false,
			data: { 
					stratType: selectedProject,	
					role: role,
					userId : userId
			},
			success: function (message) {
				console.log("message success is = ", message);					
				sessionStorage.setItem("classHierarchy",JSON.stringify(JSON.parse(message["generalHierarchy"])));
				window.location.replace("strategy-main-page.html");
			},
			error: function(xhr, textStatus, errorThrown){								
				console.log(xhr);								
				console.log(errorThrown);
				alert(errorThrown);
				alert(xhr.responseText);
			},			
		});		
	});  
		
	
	/* Currently we are working under the assumption that there is only one admin */
	function loadStrategy(strategyName){
		var strategyCreator = null;
		if(flag == 0){
			document.getElementById("errormsg").removeAttribute("hidden");
			$('#errormsg1').text("Please Select a file first.");  
			console.log("Flag is :: ", flag);
			document.getElementById("process-button").disabled = false;
			return false;
		}
		
		var chosen = $("#strategyName").val();
		
		if((contractType == "MP")||(contractType == "MS")){ 
			if(chosen=="Default" || chosen=="" ){  			
				document.getElementById("errormsg").removeAttribute("hidden");
				$('#errormsg1').text("Please choose a strategy to proceed "); 
				document.getElementById("process-button").disabled = false;
				return false;
			} 
		}
		
		$('#loadingDiv').show();
		
		if($('#contractType').val()== "MP - Non FG"){
			contractType = "MP";
			strategyCreator = strategy_list_MP[strategyName];
			selectedProject = "MP - Non FG";
		}else if($('#contractType').val()== "MS - Non FG"){
			contractType = "MS";
			strategyCreator = strategy_list_MS[strategyName];
			selectedProject = "MS - Non FG";
		}else if($('#contractType').val()== "MP - FG"){
			contractType = "MPFG";
			strategyCreator = strategy_list_MP[strategyName];
			selectedProject = "MP - FG";
		}else {
			contractType = "MSFG";
			strategyCreator = strategy_list_MS[strategyName];
			selectedProject = "MS - FG";
		}
		
		// For Filed Glass we need not to get Strategy Information
		if((contractType == "MP - FG")||(contractType == "MS - FG")){
			return true;
		}
		
		$.ajax({
			type: 'POST',
			url: 'GetStrategyInfoFromObjectStorage',
			async: false,
			data: { 
					userId : strategyCreator,
					stratType: contractType,
					strategyName: strategyName,
					allInfo : "Yes"
			},
			success: function (message) {	
				selectedStrategyInfo = message;			
				sessionStorage.setItem("selectedStrategyInfo", JSON.stringify(JSON.parse(message["singleStrategyInfo"])));
				
			},
			error: function(xhr, textStatus, errorThrown){							
				console.log(xhr);								
				console.log(errorThrown);				
			}				
		});	
		return true;		
	}
		
	
	

	$(document).ready(function() {
		
		$(".browse-file").hide();			
		loadStrategyServlet();
		
		$('#contractTypeDocConvert').val("MP - Non FG");
		$('#contractType').val("MP - Non FG");
		$('#versionNumber').val("5");
		contractType = "MP";
		
		$('.sow-details-row *').prop('disabled',true);
        $('.create-strategy-row *').prop('disabled',true);		
		
		var dropzone = document.getElementById("upload_link");				
					
		/* Setting the user name	*/	
		document.getElementById("user_id_place").innerHTML = userId;
		document.getElementById("u_name_place").innerHTML = userName;

		$('.contract-type-menu a').click(function(){
			$('#contractTypeSelected').text($(this).text());
			$('#contractTypeDocConvert').val($(this).text());
			$('#contractType').val($(this).text());
			document.getElementById("strategyid").innerHTML = ""
			if($(this).text() == "MP - Non FG"){
				changeStrategyList(strategy_list_MP);
				contractType = "MP";
			}else if($(this).text() == "MS - Non FG"){
				changeStrategyList(strategy_list_MS);
				contractType = "MS";
			}else if($(this).text() == "MP - FG"){
				changeStrategyList(strategy_list_MP);
				contractType = "MP - FG";
			}else {
				changeStrategyList(strategy_list_MS);
				contractType = "MS - FG";
			}
			/* enableDisableStrategySelection(contractType); */
		});
	  
		$('.contract-version-menu a').click(function(){
			$('#contractVersionSelected').text($(this).text());
			var temp = $(this).text();
			var ver = temp.substring(3,4);
			$('#versionNumber').val(ver);
		});
				
		$(".choose li a").click(function(){
			$(".show-strategy-here:first-child").html($(this).text()+' <span class="arrow"></span>');
			console.log("Clicked in strategy choose");
		});
		
				
		/*
			This button is a single button. On press it will call  the document conversion 
			or after that document conversion will call analyze.
		*/
		$("#process-button").click(function(){
			var conditionFlag = true;
			document.getElementById("process-button").disabled = true;
			setTimeout(function(){ 			
				conditionFlag = loadStrategy(strategyName);	
				if(conditionFlag){
					callDocumentConversion();
				}else {
					document.getElementById("process-button").disabled = false;
				}				
				
			}, 1000);				
		});
		
				
		/* Code for drag and drop */
		dropzone.ondrop = function(e){
			e.preventDefault();	
			e.stopImmediatePropagation();
			this.className = 'drag-panel';
			console.log(typeof(e.dataTransfer.files[0]));	
			console.log(e.dataTransfer.files[0]);	
			
			console.log(typeof(e.dataTransfer.files[0].name));	
			console.log(e.dataTransfer.files[0].name);
			input_file_name = e.dataTransfer.files[0].name;
			files = e.dataTransfer.files[0];
			
			/* Checking the extension  */
			var ext = input_file_name.substring(input_file_name.lastIndexOf('.'),input_file_name.length);			
			if(ext == ".pdf" || ext == ".doc" || ext == ".docx"){									
				if(input_file_name!=""){
					flag = 1;
					console.log("flag changed to :: ", flag);
					$("#hidden_flag").val(1);		
					var modified_filename = input_file_name;					
					if(input_file_name.length > 25) {				
						modified_filename = input_file_name.substring(0,20) +"~"+input_file_name.substring(input_file_name.lastIndexOf('.'),input_file_name.length);
					}
					$('.file-name label').text(modified_filename);
					$('.file-name').show(); 						 			
				}				
			}else {
				input_file_name ="";
				files = "";
				//alert("Only Pdf, Doc, Docx are allowed....");
				document.getElementById("errormsg").removeAttribute("hidden");
				$('#errormsg1').text("Only Pdf, Doc, Docx are allowed.");  
				return;
			}											
		}
		
		dropzone.ondragover = function(e) {						
			this.className = 'drag-panel';		
			return false;
		} 
		
		dropzone.ondragleave = function() {
			this.className = 'drag-panel';
			return false;
		}
		
		$(".browse").click(function(e){
			e.preventDefault();
			document.getElementById('browse').click();
			console.log("Getting clicked here....");
		});
		
		$(".drag-panel").mouseover(function() {
			$('.drag-panel').css('cursor', 'pointer');
		});	
				
	});	
	
	/* On selection of FG category the strategy portion will be disabled */
	function enableDisableStrategySelection(contractType){
		var k = $("#contractTypeSelected").text();
		if ((k=="MP - FG")||(k=="MS - FG")){
			console.log(k);
			$("#ChooseStrategy").attr('disabled','disabled');			
		}
		else{
			$("#ChooseStrategy").removeAttr('disabled');
		}
	}
	
	