	$(document).ready(function() {
		$(".upload-template").hide();
		$(".upload-config").hide();
		document.getElementById("u_name_place").innerHTML = userName;
		document.getElementById("user_id_place").innerHTML = userId;
		
		$("#uploadButton").click(function(){
			if(input_file_name != ""){
				var temp1 = $('#templateContractType').text().trim();
				$("#contractType1").val(temp1);
		
				var temp2 = $('#MapTemplateDocumentType').text().trim();
				$("#documentType1").val(temp2);
				$("#templateType1").val("tab1");
				setTimeout(function(){ callUploadTemplate();  }, 2000);
					
			}else {
				document.getElementById("errormsg").removeAttribute("hidden");
				$('#errormsg1').text("Please Select a File First.");  
			}	
		});
		
		$("#uploadMapping").click(function(){
			if(config_file_name != ""){
				$("#contractType2").val($('#mappingContracts').text());
				$("#scoreType2").val($('#mapConfigScoreType').text());
				$("#documentType2").val($('#mapConfigDocumentType').text());
				$("#templateType2").val("tab2");
				setTimeout(function(){ callUploadMapping();	  }, 2000);				
			}else {
				document.getElementById("errormsg").removeAttribute("hidden");
				$('#errormsg1').text("Please Select a File First.");  
			}	
		});
		
		$(".upload").click(function(e){
			e.preventDefault();
			document.getElementById('browse').click();
		});
		
		$(".config").click(function(e){
			e.preventDefault();
			document.getElementById('browse_configFile').click();
		});
		
		loadContractType();
		loadTemplateDocumentType();
		
		loadMappingContracts();
		loadMappingScoreType();
		loadMappingDocumentType();	
		
	});

	$('.file-close').click(function(){
		document.getElementById("doc-name").innerHTML  = "";
		document.getElementById("input_file_name").value  = "";
		input_file_name = "";
		modified_filename  = "";
		analyzeFlag = false;
		flag = 0;
		$('.upload-template').hide();
	});
	
	$('.ConfigFile-close').click(function(){
		document.getElementById("config-name").innerHTML  = "";
		document.getElementById("config_file_name").value  = "";
		config_file_name = "";
		modified_filename1  = "";
		mappingFlag = false;
		flag1 = 0;
		$('.upload-config').hide();
	});
	
	$('input[name="file1"]').change(function(){	
			document.getElementById("errormsg").setAttribute("hidden","true");		
			input_file_name = $('input[name=file1]').val().split('\\').pop();
			console.log(input_file_name);
			var ext = input_file_name.substring(input_file_name.lastIndexOf('.'),input_file_name.length);	
			var modified_filename = input_file_name;

			if(ext == ".pdf" || ext == ".doc" || ext == ".docx"){	
				flag = 2;
				//$("#hidden_flag").val(2);			
				if(input_file_name.length > 25) {				
					modified_filename = input_file_name.substring(0,20) +"~"+input_file_name.substring(input_file_name.lastIndexOf('.'),input_file_name.length);
				}			
				$("#input_file_name").val(input_file_name);	
				console.log($("#input_file_name").val());
				document.getElementById("doc-name").innerHTML  = "";
				document.getElementById("doc-name").innerHTML  += modified_filename;
				$('.upload-template').show();
			}	
			else {
				input_file_name ="";
				//files = "";
				document.getElementById("errormsg").removeAttribute("hidden");
				$('#errormsg1').text("Only Pdf, Doc, Docx are allowed....");  
				return;
			}		   
	});	
	
	$('input[name="config_file"]').change(function(){	
		document.getElementById("errormsg").setAttribute("hidden","true");		
		config_file_name = $('input[name=config_file]').val().split('\\').pop();
		console.log(config_file_name);
		var ext1 = config_file_name.substring(config_file_name.lastIndexOf('.'),config_file_name.length);	
		var modified_filename1 = config_file_name;

		if(ext1 == ".xls" || ext1 == ".xlsx"){	
			flag1 = 2;
			//$("#hidden_flag").val(2);			
			if(config_file_name.length > 25) {				
				modified_filename1 = config_file_name.substring(0,20) +"~"+config_file_name.substring(config_file_name.lastIndexOf('.'),config_file_name.length);
			}			
			$("#config_file_name").val(config_file_name);	
			console.log($("#config_file_name").val());
			document.getElementById("config-name").innerHTML  = "";
			document.getElementById("config-name").innerHTML  += modified_filename1;
			$('.upload-config').show();
		}	
		else {
			config_file_name ="";
			//files = "";
			document.getElementById("errormsg").removeAttribute("hidden");
			$('#errormsg1').text("Only Xls, Xlsx files are allowed....");  
			return;
		}		   
	});	
	
	/*
		Template upload
	*/
	function callUploadTemplate(){	
		var templateType = "tab1";				
		var formData = new FormData($("#upload_template_form")[0]);	
		
		/* var temp1 = $('#templateContractType').text().trim();
		$("#contractType1").val(temp1);
		
		var temp2 = $('#MapTemplateDocumentType').text().trim();
		$("#documentType1").val(temp2); */
		
		$("#input_file_name").val(input_file_name);
		$("#templateType1").val(templateType);		
						
		// ajax call for file upload
		$('#loadingDiv').show();
		
		$.ajax({
			url: 'UploadTemplateServlet',
			method: 'post',
			data:formData,						
			async: true,
			success: function(successResponse) {
			//	text_log(successResponse["data"]);
				console.log("successResponse :: ",successResponse);
				if(successResponse["error"] == null || successResponse["error"] =="" ||successResponse["error"] =="null"){						
					console.log("successResponse is :: \n", successResponse);					
					$('#input_file_name').val(successResponse["fileName"]);
					//console.log("No Error :: Doc convert is disabled.");
					$('#loadingDiv').hide();
					//alert(successResponse);
				}else {
					$('#errormsg1').text(successResponse["error"]);
					document.getElementById("errormsg").removeAttribute("hidden");
					console.log("With Error :: ",successResponse["error"]);
					$('#loadingDiv').hide();	
					//alert(successResponse["error"]);
				}	 					
			},
			error: function(errorResponse){	
				$('#loadingDiv').hide();	
				console.log(errorResponse);
				$('#errormsg1').text("Error :: Some problems occured during document upload. Please try again.");
				document.getElementById("errormsg").removeAttribute("hidden");
			},
			cache: false,
			contentType: false,
			processData: false
		});	
				
	}
	
	/*
		Hierarchy Upload
	*/
	function callUploadMapping(){	
		var templateType = "tab2";		
		var formData = new FormData($("#upload_mapping_config_form")[0]);				
				
			
		// ajax call for file upload
		$('#loadingDiv').show();
		
		$.ajax({
			url: 'UploadMappingConfigServlet',
			method: 'post',
			data:formData,						
			async: true,
			success: function(successResponse) {
				console.log("successResponse :: ",successResponse);
				if(successResponse["error"] == null || successResponse["error"] =="" ||successResponse["error"] =="null"){						
					console.log("successResponse is :: \n", successResponse);				
					$('#input_file_name').val(successResponse["fileName"]);
					//console.log("No Error :: Doc convert is disabled.");
					$('#loadingDiv').hide();
					//alert(successResponse);
				}else {
					$('#errormsg1').text(successResponse["error"]);
					document.getElementById("errormsg").removeAttribute("hidden");
					console.log("With Error :: ",successResponse["error"]);
					$('#loadingDiv').hide();	
					//alert(successResponse["error"]);
				}	 					
			},
			error: function(errorResponse){	
				$('#loadingDiv').hide();	
				console.log(errorResponse);
				$('#errormsg1').text("Error :: Some problems occured during document upload. Please try again.");
				document.getElementById("errormsg").removeAttribute("hidden");
			},
			cache: false,
			contentType: false,
			processData: false
		});	
				
	}
	
		
	function loadContractType(){
		var list = ["Managed Projects" , "Manages Services"];
		document.getElementById("templateContractType").innerHTML = "";
		document.getElementById("templateContractType").innerHTML = list[0];
		document.getElementById("menu2").innerHTML = "";		
		for (var i in list) { 
			var clientName="<li id="+i+" role=\"presentation\" onclick=\"select_contract_name('"+list[i]+"')\"><a role=\"menuitem\" tabindex=\"-1\" href=\"#\">"+list[i]+"</a></li>";			
			document.getElementById("menu2").innerHTML += clientName;
		}		
	}
	
	function select_contract_name(name){
		document.getElementById("templateContractType").innerHTML = "";
		document.getElementById("templateContractType").innerHTML =name;	
	}
	
	
	function loadTemplateDocumentType(){
		var list = ["FG","Non FG"];
		document.getElementById("MapTemplateDocumentType").innerHTML = "";
		document.getElementById("MapTemplateDocumentType").innerHTML = list[0];
		document.getElementById("documentType").innerHTML = "";		
		for (var i in list) { 
			var clientName="<li id="+i+" role=\"presentation\" onclick=\"selectTemplateDocumentType('"+list[i]+"')\"><a role=\"menuitem\" tabindex=\"-1\" href=\"#\">"+list[i]+"</a></li>";			
			document.getElementById("documentType").innerHTML += clientName;
		}		
	}
	
	function selectTemplateDocumentType(name){
		document.getElementById("MapTemplateDocumentType").innerHTML = "";
		document.getElementById("MapTemplateDocumentType").innerHTML =name;	
	}
	
		
	
	/*
	 ================ Mapping Configuration Starts ===================
	*/
	function loadMappingContracts(){
		var list = ["Managed Projects" , "Manages Services"];
		document.getElementById("mappingContracts").innerHTML = "";
		document.getElementById("mappingContracts").innerHTML = list[0];
		document.getElementById("contracts").innerHTML = "";		
		for (var i in list) { 
			var clientName="<li id="+i+" role=\"presentation\" onclick=\"select_mappingContract('"+list[i]+"')\"><a role=\"menuitem\" tabindex=\"-1\" href=\"#\">"+list[i]+"</a></li>";			
			document.getElementById("contracts").innerHTML += clientName;
		}		
	}
	
	function select_mappingContract(name){
		document.getElementById("mappingContracts").innerHTML = "";
		document.getElementById("mappingContracts").innerHTML =name;	
	}
	
	function loadMappingScoreType(){
		var list = ["Static","Dynamic","General"];
		document.getElementById("mapConfigScoreType").innerHTML = "";
		document.getElementById("mapConfigScoreType").innerHTML = list[0];
		document.getElementById("scoreMenu2").innerHTML = "";		
		for (var i in list) { 
			var clientName="<li id="+i+" role=\"presentation\" onclick=\"select_mappingScoreType('"+list[i]+"')\"><a role=\"menuitem\" tabindex=\"-1\" href=\"#\">"+list[i]+"</a></li>";			
			document.getElementById("scoreMenu2").innerHTML += clientName;
		}		
	}
	
	function select_mappingScoreType(name){
		document.getElementById("mapConfigScoreType").innerHTML = "";
		document.getElementById("mapConfigScoreType").innerHTML =name;	
	}
	
	function loadMappingDocumentType(){
		var list = ["FG","Non FG"];
		document.getElementById("mapConfigDocumentType").innerHTML = "";
		document.getElementById("mapConfigDocumentType").innerHTML = list[0];
		document.getElementById("documentType3").innerHTML = "";		
		for (var i in list) { 
			var clientName="<li id="+i+" role=\"presentation\" onclick=\"select_mappingDocumentType('"+list[i]+"')\"><a role=\"menuitem\" tabindex=\"-1\" href=\"#\">"+list[i]+"</a></li>";			
			document.getElementById("documentType3").innerHTML += clientName;
		}		
	}
	
	function select_mappingDocumentType(name){
		document.getElementById("mapConfigDocumentType").innerHTML = "";
		document.getElementById("mapConfigDocumentType").innerHTML =name;	
	}
	
	/*
	  ============================================================================
	*/
	
	