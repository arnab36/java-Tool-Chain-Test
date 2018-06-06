			
		/*
			Code for login verification.
		*/
		function verify_login(){			
			var user_name = $('#email').val();
			var password = $('#password').val();
			console.log("Function called");
			
			if(user_name == "" || user_name == null) {				
				//alert("Please enter the User Name..");
				$( '#password' ).removeClass( "login-error-focus" );
				$( '#email' ).addClass( "login-error-focus" );
				$( '#err' ).text("Please enter user name");
				$( '#err' ).show();
				//$('#email').focus();	
				return false;
			}
			
			if(password == "" || password == null) {				
				//alert("Please enter the Password..");
				$( '#email' ).removeClass( "login-error-focus" );
				$( '#password' ).addClass( "login-error-focus" );
				$( '#err' ).text("Please enter password");
				$( '#err' ).show();
				//$('#password').focus();	
				return false;
			}
			call_ajax();		
			
		};
		
			
		function call_ajax(){
			var userId = $('#email').val();
	        var frm = $("#myForm");
			checkExistingUser();
			console.log("Form submitted 2");
			$.ajax({
				type: frm.attr('method'),
				url: frm.attr('action'),
				data: frm.serialize(),
				async: false,
				success: function(successResponse){
					var data  = JSON.parse(successResponse["jsonObject"]);
					console.log("Success :: ", data);
					if( data["status"] == 200){				
						
						localStorage.setItem("userId",userId);
						localStorage.setItem("userName",data["firstName"]+" "+data["lastName"]);							
						//localStorage.setItem("strategyInfoHierarchy",JSON.stringify(JSON.parse(successResponse["strategyInfoHierarchy"])));
						localStorage.setItem("role",data["role"]);						
						console.log("Done...");
						if(data["role"] == "U")
							window.location.replace("upload.html");
						else
							window.location.replace("admin-home-page.html");
						}else{
							document.getElementById('err').innerHTML = "Wrong User Id or Password";
							$( '#err' ).show();
							document.getElementById("login").disabled = false;
					}
				},
				error: function(error) {
					console.log("error :: ", error["status"]);						
					document.getElementById("login").disabled = false;
					document.getElementById('err').innerHTML = "Wrong credentials.";
					$( '#err' ).show();
					/* // Error logic has to be implemented
					if( error["status"] == 200){
						sessionStorage.setItem("userId",userId);
						sessionStorage.setItem("userName",error["firstName"]+" "+error["lastName"]);	
						sessionStorage.setItem("listMP",error["list_MP"]);
						sessionStorage.setItem("listMS",error["list_MS"]);
						
						localStorage.setItem("userId",userId);
						localStorage.setItem("userName",error["firstName"]+" "+error["lastName"]);	
						localStorage.setItem("listMP",error["list_MP"]);
						localStorage.setItem("listMS",error["list_MS"]);
						
						console.log("Done...");
						if(data["role"] == "U")
							window.location.replace("upload.html");
						else
							window.location.replace("admin-home-page.html");						
					}else{
							document.getElementById('err').innerHTML = "Wrong User Id or Password";
							$( '#err' ).show();
							document.getElementById("login").disabled = false;
					} */
				}
			});
		}
		
		$('#myForm').keydown(function(e) {
			console.log("Hi");
			if (e.keyCode == 13) {
			// As ASCII code for ENTER key is "13"
				console.log("Hi there");
				document.getElementById("login").disabled = true;
				verify_login();
			}
		}); 
			
		
		$('#login').click(function(){
			document.getElementById("login").disabled = true;
			verify_login();
		});
		
		//Added by Akanksha
		
		$('#email').focus(function(){
			document.getElementById("login").disabled = false;
			$( '#email' ).removeClass( "login-error-focus" );
			$( '#err' ).text("");
			$( '#err' ).hide();
			return false;
		});
		$('#password').focus(function(){
			document.getElementById("login").disabled = false;
			$( '#password' ).removeClass( "login-error-focus" );
			$( '#err' ).text("");
			$('#err').hide();
			return false;
		});
		
		
		