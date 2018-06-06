	// The following method will check the Cookie and will logout if not found
	function checkCookie(flag){
		var ck = document.cookie;
		console.log("Cokiee is :: ", ck);
		var session = readCookie("SESSION_ID")
		if(session == null){
			if(parseInt(flag) != 0) {
				window.location.replace("index.html");
			}			
		}else {		
			if(parseInt(flag) == 0){				
				if(readCookie("USER_ID") != null) {
					localStorage.setItem("userId",readCookie("USER_ID"));
					localStorage.setItem("userName",readCookie("FIRST_NAME")+" "+readCookie("LAST_NAME"));					
					if(localStorage.getItem("role") == "A"){
						window.location.replace("admin-home-page.html");
					}else {
						window.location.replace("upload.html");
					}				
				}			
			}else if(parseInt(flag) == 1){				
				if(localStorage.getItem("userId") != null) {
					console.log("Bug 1");				
				}else{
					if(readCookie("USER_ID") != null){
						localStorage.setItem("userId",readCookie("USER_ID"));
						localStorage.setItem("userName",readCookie("FIRST_NAME")+" "+readCookie("LAST_NAME"));
						console.log("Bug 2");	
						if(localStorage.getItem("role") == "A"){
							window.location.replace("admin-home-page.html");
						}else {
							window.location.replace("upload.html");
						}						
					}else{
						console.log("Bug 3");
						window.location.replace("index.html");
					}					
				}		
			}else {
				//return;
			}		
		}
	}

	// Checking already existing user
	function checkExistingUser(){
		var session = readCookie("SESSION_ID");
		if(session != null) {
			sessionStorage.setItem("userId",readCookie("USER_ID"));
			sessionStorage.setItem("userName",readCookie("FIRST_NAME")+" "+readCookie("LAST_NAME"));	
			sessionStorage.setItem("listMP",localStorage.getItem("listMP"));
			sessionStorage.setItem("listMS",localStorage.getItem("listMS"));
			window.location.replace("upload.html");
		}
	}


	// Will delete all cookies 
	function deleteAllCookies() {
		var cookies = document.cookie.split(";");

		for (var i = 0; i < cookies.length; i++) {
			var cookie = cookies[i];
			var eqPos = cookie.indexOf("=");
			var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
			document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
		}
	}

	function readCookie(name) {
	  var nameEQ = name + "=";
	  var ca = document.cookie.split(';');
	  for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	  }
	  return null;
	} 


	// The following function will call logout method of ligin servlet
	function logout(){
		document.cookie = null;
		deleteAllCookies();
		$.ajax({
			type: 'GET',
			url:'LoginServlet',
			data:{
				action:"Logout"
			},
			async: true,
			success:function(succ){
				console.log("Success Logout:: ", succ);
			},
			error:function(error){
				console.log("Error in Logout:: ", error);
			},
			cache: false,
			contentType: false,
			processData: false
		});
	}
	
	// The following function checks whether an object is empty or not
	function isEmpty(obj) {
		for(var key in obj) {
			if(obj.hasOwnProperty(key))
				return false;
		}
		return true;
	}
	
	

$(document).ready(function(){
   
    //Dropdown  code
    /* $(".dropdown-menu li a").click(function(){
        $(".dropdown-toggle:first-child").html($(this).text()+' <span class="arrow"></span>');
    }); */

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
   

    $('#buttons-middle li').click(function(){
        var currLiClass = $(this).attr('class');
        $('li.active').not( ".score" ).removeClass('active');
        $(this).addClass('active');
		console.log("Getting clicked now .......");
    });
	
	$('.class-tree').click(function(){
		$('#classTree').show();
		$('#wordCloud').hide();
		$('#scorePanel').hide();		
	});
	
	$('.word-cloud').click(function(){
		$('#classTree').hide();
		$('#wordCloud').show();
		$('#scorePanel').hide();		
	});
	
	$('.score').click(function(){
		$('#classTree').hide();
		$('#wordCloud').hide();
		$('#scorePanel').show();		
	});
	
	 // View strategy from score page
    $('.default-strategy-btns .view').click(function(){
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
		
    });

    // Edit strategy from score page
     $('.default-strategy-btns .edit').click(function(){
        $('.raw-score-val').hide();
        $('.weight-val').hide();
		$('.min-count-val').hide();
		$('.min-score-val').hide();
        $('.edit-strategy').hide();
		
		$('.raw-score-tbox input').show();
		$('.weight-tbox input').show();
		$('.min-count-tbox input').show();
		$('.min-score-tbox input').show();	
		
		// Enabling mandatory check box
		$('.check-mand').attr("disabled", false);		
    });

    // Edit strategy from view strategy overlay
     $('.edit-strategy').click(function(){
        $('.raw-score-val').hide();
        $('.weight-val').hide();
		$('.min-count-val').hide();
		$('.min-score-val').hide();

        $('.raw-score-tbox input').show();
		$('.weight-tbox input').show();
		$('.min-count-tbox input').show();
		$('.min-score-tbox input').show();	

		// Enabling mandatory check box
		$('.check-mand').attr("disabled", false);	
    });

    //Score page left nav
    $('li.score').addClass('active');
    $('#score-left-nav li').click(function(e){
        e.preventDefault();
		console.log("Getting clicked.......");
        $('li.active').removeClass('active');
        $(this).addClass('active');
    });

    //Score page mobile navigation
    $('li.score').addClass('active');
    $('#score-mobile-nav li').click(function(e){
        e.preventDefault();
        $('li.active').removeClass('active');
        $(this).addClass('active');
    });
	
	$('#strategyEditView').on('hidden.bs.modal', function () {			
		create_initial_view_table();
	})	
	
	//chat code
	$('#chat').hide();
	$('.chat-icon').click(function(e){
        e.preventDefault();
		start_conversation();
        $('#chat').show();
    });	


    /* upload click event for home page */
    // $('.homepage-steps').find(':nth-child(1)').addClass('step1');
    $('.drag-panel h2').find(':nth-child(2)').addClass('arrow-down');
    $('.sow-details h2').find(':nth-child(2)').addClass('arrow-right');
    $('.select-strategy h2').find(':nth-child(2)').addClass('arrow-right');
    $('.upload-content').show();
    $('.sow-content').hide();
    $('.strategy-content').hide();
    $('.drag-panel h2').click(function(e){
        e.preventDefault();
        $(this).find(':nth-child(2)').addClass('arrow-down');
        $('.upload-content').show();
        $('.sow-content').hide();
        $('.strategy-content').hide();
        $('.sow-details h2').find(':nth-child(2)').removeClass('arrow-down');
        $('.select-strategy h2').find(':nth-child(2)').removeClass('arrow-down');         
        $('.homepage-steps').find(':nth-child(1)').removeClass('complete');
        $('.homepage-steps').find(':nth-child(3)').removeClass('active');
        $('.homepage-steps').find(':nth-child(3)').removeClass('complete');
        $('.homepage-steps').find(':nth-child(5)').removeClass('active');        
    });

    $('.sow-details h2').click(function(e){
        e.preventDefault();
        $(this).find(':nth-child(2)').addClass('arrow-down');
        $('.sow-content').show();
        $('.upload-content').hide();
        $('.strategy-content').hide();
        $('.drag-panel h2').find(':nth-child(2)').removeClass('arrow-down');
        $('.select-strategy h2').find(':nth-child(2)').removeClass('arrow-down'); 
        $('.homepage-steps').find(':nth-child(1)').addClass('complete');
        $('.homepage-steps').find(':nth-child(3)').addClass('active');
        $('.homepage-steps').find(':nth-child(5)').removeClass('active');
    });

    $('.select-strategy h2').click(function(e){
        e.preventDefault();
        $(this).find(':nth-child(2)').addClass('arrow-down');
        $('.sow-content').hide();
        $('.upload-content').hide();
        $('.strategy-content').show();
        $('.drag-panel h2').find(':nth-child(2)').removeClass('arrow-down');
        $('.sow-details h2').find(':nth-child(2)').removeClass('arrow-down');
        $('.homepage-steps').find(':nth-child(1)').addClass('complete');
        $('.homepage-steps').find(':nth-child(3)').removeClass('active');
        $('.homepage-steps').find(':nth-child(3)').addClass('complete');
        $('.homepage-steps').find(':nth-child(5)').addClass('active');
    });
	
	// Calling Logout
	//$("#logout").click(function(){
	$(document).on("click","#logout", function(){
		console.log("Inside click");
		localStorage.clear();
		logout();
		window.location.replace("index.html");			
	});
	
	$('#backHome').click(function(){
		window.location.replace("upload.html");	
	});
	
	$('#backAdminHome').click(function(){
		window.location.replace("admin-home-page.html");	
	});

	// admin user create strategy and sow upload page
	$('#sowAnalyserTabContent').hide();
	$('.admin-tabs li .tab-strategy').click(function(){
		$('li.active').removeClass('active');
        $(this).closest('li').addClass('active');
		$('#createStrategyTabContent').show();
		$('#sowAnalyserTabContent').hide();
	});

	$('.admin-tabs li .tab-sow-analysis').click(function(){
		$('li.active').removeClass('active');
        $(this).closest('li').addClass('active');
		$('#sowAnalyserTabContent').show();
		$('#createStrategyTabContent').hide();
	});

	// admin user upload template and config files code
	$('#uploadMapConfigTabContent').hide();
	$('.upload-tabs li .tab-upload-template').click(function(){
		$('li.active').removeClass('active');
        $(this).closest('li').addClass('active');
		$('#uploadTabContent').show();
		$('#uploadMapConfigTabContent').hide();
	});

	$('.upload-tabs li .tab-mapping-config').click(function(){
		$('li.active').removeClass('active');
        $(this).closest('li').addClass('active');
		$('#uploadMapConfigTabContent').show();
		$('#uploadTabContent').hide();
	});

	$('#treeView').hide();

	$('#dynamicView').click(function(){
		$('#staticDynamicView').hide();
		$('#treeView').show();
		$('#staticClassTreeContent').hide();
		$('#dynamicClassTreeContent').show();		
	});

	$('#treeView .static-score h3').click(function(){
		$('#staticDynamicView').show();	
		$('#treeView').hide();
	});

	$('#treeView .dynamic-score h3').click(function(){
		$('#staticDynamicView').show();	
		$('#treeView').hide();		
	});

	/* $('#treeView .static-score .static-icon').click(function(){
		$('#dynamicClassTreeContent').hide();
		$('#staticClassTreeContent').show();				
	}); */

	/* $('#treeView .dynamic-score .dynamic-icon').click(function(){
		$('#staticClassTreeContent').hide();
		$('#dynamicClassTreeContent').show();				
	}); */	
	
	$('#treeView .static-score .static-icon').click(function(){
		$('#staticDynamicView').show();	
		$('#treeView').hide();					
	});
	
	$('#treeView .dynamic-score .dynamic-icon').click(function(){
		$('#staticDynamicView').show();	
		$('#treeView').hide();			
	});


    $(document).mouseup(function (e) {
        var popup = $("#chat");
        if (!$('.chat-icon').is(e.target) && !popup.is(e.target) && popup.has(e.target).length == 0) {
            popup.hide(500);
        }
    });
    
});	


