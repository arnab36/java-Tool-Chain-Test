
	function loadStrategyServlet(){			
		var frm = $("#loadStrategyForm");	
		$.ajax({
			type: 'GET',
			url:'UploadServlet',
			data: frm.serialize(),
			async: true,
			success:function(strategy){
				console.log("Success load Initial Strategy:: ", strategy);
				
				try {
					localStorage.setItem("strategyInfoHierarchy",JSON.stringify(JSON.parse(strategy["strategyHierarchyInfo"])));
					localStorage.setItem("strategyInfo",JSON.stringify(JSON.parse(strategy["strategyInfo"])));
					$("#loadingDiv").hide();
				}catch(err){
					localStorage.setItem("strategyInfoHierarchy","");
					localStorage.setItem("strategyInfo","");
					console.log(err);
					$("#loadingDiv").hide();
				}
				
			},
			error:function(error){
				console.log("Error in load Initial Strategy:: ", error);
				$("#loadingDiv").hide();
			},
			cache: false,
			contentType: false,
			processData: false
		});
		
		setTimeout(function(){ 			
			$("#loadingDiv").hide();
		}, 3000);
	}
		
		
	function loadStrategyHierarchy(){
		var frm = $("#loadHierarchyForm");	
		$.ajax({
			type: 'POST',
			url:'GetStrategyHierarchy',			
			async: true,
			success:function(strategy){
				console.log("Success Logout:: ", strategy);
				localStorage.setItem("classHierarchyCISCO_GEN_MP",JSON.stringify(JSON.parse(strategy["classHierarchyCISCO_GEN_MP"])));
				localStorage.setItem("classHierarchyCISCO_GEN_MS",JSON.stringify(JSON.parse(strategy["classHierarchyCISCO_GEN_MS"])));
				
				/* localStorage.setItem("classHierarchyCISCO_STATIC_MP",JSON.stringify(JSON.parse(strategy["classHierarchyCISCO_STATIC_MP"])));
				localStorage.setItem("classHierarchyCISCO_STATIC_MS",JSON.stringify(JSON.parse(strategy["classHierarchyCISCO_STATIC_MS"]))); */
				
				window.location.replace("strategy-sow-analysis.html");
			},
			error:function(error){
				console.log("Error in Logout:: ", error);
			},
			cache: false,
			contentType: false,
			processData: false
		});
	}
	
	$(document).ready(function() {		
		
		$("#userIdStrategy").val(userId);
		$("#userRole").val(localStorage.getItem("role"));		
		
		$("#loadingDiv").show();
		setTimeout(function(){ 
			loadStrategyServlet();			
		}, 1000);
		
		// Setting the user name		
		document.getElementById("user_id_place").innerHTML = userId;
		document.getElementById("u_name_place").innerHTML = userName;			
		
		// All strategy information for current user
		strategyInfo = JSON.parse(sessionStorage.getItem("strategyInfo"));	 	
				
		$("#ButtonSOW").on('click', function () {
			$("#loadingDiv").show();
			setTimeout(function(){ 
			loadStrategyHierarchy();	
			}, 1000);
		});
				
		$("#ButtonUpload").on('click', function () {
		  window.location.replace("template-mapping-config.html");
		});			
		
		
	});