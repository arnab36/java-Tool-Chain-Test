		/*
			dot('.') s are not allowed in strategy_name.
			It will check whether strategy_name contains '.' or not. 
		*/
		function special_char_check(strategy_name) {	
			var flag  = false;
			if(strategy_name.includes("."))
				flag = true;
			return flag;
		}
		
		// Checking whether strategy name is unique or not
		function check_unique(strategyName) {
			strategyName=strategyName.trim();
			var strategyHierarchy = {};
			try {
				JSON.parse(localStorage.getItem("strategyInfoHierarchy"));
			}catch(err){
				console.log(err);
				return false;				
			}
			for(var i in strategyHierarchy){
				var mpList = strategyHierarchy[i]["MP"];
				for(var j in mpList){		
                    console.log();		
					if(mpList[j]["StrategyName"] == strategyName){
						return true;
					}
				}
				
				var msList = strategyHierarchy[i]["MS"];
				for(var k in msList){
					console.log();	
					if(mpList[k]["StrategyName"] == strategyName){
						return true;
					}
				}
			}	
			return false;
		}
		
		/*
		  The following function won't allow you to enter any things inside a text box except real numbers
		*/
		function isNumber(evt,id) {
		consol.log("is bumber calling");
			// When enter is pressed the value is enterd in the text box
			if(evt.keyCode === 13) {
				//console.log("Enter key is pressed...");
				evt.preventDefault();
				var elem = document.getElementById(id);
				
				$(elem).blur();
				return true;
			}

			evt = (evt) ? evt : window.event;
			var charCode = (evt.which) ? evt.which : evt.keyCode;

			if (charCode > 31 && (charCode < 48 || charCode > 57) && (charCode != 46 && charCode != 110 && charCode != 190)) {
				return false;
			}else {			
				var str = document.getElementById(id).value;
				var flag = true;
				var found = false;			
				for(var i = 0; i < str.length; i++) {
					var temp_code = str.charCodeAt(i);	
					if((temp_code == 46 || temp_code == 110 || temp_code == 190) && found == true) {
						flag = false;
					}else if((temp_code == 46 || temp_code == 110 || temp_code == 190) && found == false){
						found = true;
					}else {
						continue;
					}
				}

				if(flag == false) {
					var new_val = str.substring(0,str.length-1);
					document.getElementById(id).value = new_val;	
					return false;				
				}else {
					return true;
				}			
			}
		} 
	
		
		/*
			The following function will check whether the entered value contains two decimal points or not.
			After that it will calculate the percent value and make changes there too.
		*/
		function checkDecimal(id) {
			var str = document.getElementById(id).value;
			var flag = true;
			var found = false;			
			for(var i = 0; i < str.length; i++) {
				var temp_code = str.charCodeAt(i);	
						
				if((temp_code == 46 || temp_code == 110 || temp_code == 190) && found == true) {
					flag = false;
				}else if((temp_code == 46 || temp_code == 110 || temp_code == 190) && found == false){
					found = true;
				}else {
					continue;
				}
			}
			
			if(flag == false) {
				var new_val = str.substring(0,str.length-1);
				document.getElementById(id).value = new_val;	
			}
			
			var value = parseFloat(str);
			console.log("Value  = ", value);
			if(value < 0) {
				//alert("You can not put value less than 0");
				$('#errormsgBody').show();
				$('#errormsgBody').text("You can not put value less than 0");  
				document.getElementById(id).value = 0;				
			}else if(value > 100) {
				//alert("You can not put value greater than 100");
				$('#errormsgBody').show();
				$('#errormsgBody').text("You can not put value greater than 100");  
				document.getElementById(id).value = 100;				
				
			}else if(isNaN(value)) {			
				//alert("Please enter a value");
				$('#errormsgBody').show();
				$('#errormsgBody').text("Please enter a value");  
				document.getElementById(id).value = 0;
				var elem = document.getElementById(id);
				$(elem).focus();				
			}else {
				return;
			}
		}		
		
		
		/*
			The following function will make sure that the cost,red_max, yellow_max values 
			of the score table are entered properly.
			It will also check whether the property Red_Max < Yellow_Max is satisfied or not.
			The only difference between this function and the function above is that this function 
			will not call the "ReCalculate" method.
		*/
		/* function check_score_table_value(id) {
			var str = document.getElementById(id).value;
			var flag = true;
			var found = false;			
			for(var i = 0; i < str.length; i++) {
				var temp_code = str.charCodeAt(i);	
						
				if((temp_code == 46 || temp_code == 110 || temp_code == 190) && found == true) {
					flag = false;
				}else if((temp_code == 46 || temp_code == 110 || temp_code == 190) && found == false){
					found = true;
				}else {
					continue;
				}
			}
			
			if(flag == false) {
				var new_val = str.substring(0,str.length-1);
				document.getElementById(id).value = new_val;	
			}
			
			var value = parseFloat(str);
			if(value < 0) {
				//alert("You can not put value less than 0");
				$('#errormsgBody').show();
				$('#errormsgBody').text("Please enter a value");  
				document.getElementById(id).value = 0;				
			}else if(isNaN(value)) {			
				alert("Please enter a value");
				document.getElementById(id).value = null;
				var elem = document.getElementById(id);
				$(elem).focus();
			}else {
				var red_max = parseFloat(document.getElementById("score_table_red").value);
				var yellow_max = parseFloat(document.getElementById("score_table_yellow").value);				
				
				try {
					if(red_max != "" && yellow_max != "") {
						if(red_max >= yellow_max) {
						alert("The value of Red_Max can not be greater than Yellow_Max....");
						document.getElementById("score_table_red").value = 0;
						}
					}					
				}catch(e) {
					//console.log(e);
					return;
				}				
			}
		} */
		
		
		
		// Checking whether at least on class name is selected
		function check_checkbox() {
			var flag = true;		
			$(".check_box_1").each(function(i) {	
				//console.log($(this).is(':checked'));				
				if($(this).is(':checked')){
					flag = false;
				}					
			});
			return flag;
		}
		
		// From the check box id get the class name.
		// Length("check.") = 6
		function get_class_name(current_obj) {
			var id = current_obj.attr('id');
			var name = id.substring(6,id.length);
			return name;
		}
		
		
		