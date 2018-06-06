			
	$("#downloadFile").on("click", function(){		
		var summaryFileName = fileName+"_Results.xlsx";
		if(staticScore >= 90){
			generalScore = "NA";
		}else{
			generalScore = SOW_SCORE;
		}		
		
		var data1 = [{Static_Score:staticScore,Dynamic_Score:dynamicSCore,General_Score:generalScore }];
	    var data2 = calculateSummary("STATIC");
		
	    var data3 = "NA";
		if(contractType == "MP - Non FG" || contractType == "MS - Non FG"){
			data3 = calculateSummary("GENERAL");
		}
		
	    var data4 = calculateDynamicSummary();	
	    var data5 = getAlerts();
	    
	    var opts = [{sheetid:'Summary',header:true},{sheetid:'Static_Score',header:false},{sheetid:'General_Score',header:false},{sheetid:'Dynamic_Score',header:false},{sheetid:'Alerts',header:false}];
	    var res = alasql('SELECT INTO XLSX("'+summaryFileName+'",?) FROM ?',
	                     [opts,[data1,data2,data3,data4,data5]]);
	});
	
	
	function calculateSummary(type){
		var data = [];
		var category = [];
		var classes = [];
		var definition=[];
		var classColor = [];
		var classHierarchy = {};
		var helpClasses = {};
		var greenList = [];
		var contentText = [];
		var fileContent = {};
		
		if(type == "GENERAL"){
			classHierarchy = generalHierarchy;
			helpClasses = generalHelpClasses;
			greenList = KPIList;
			fileContent = classesContent;
		}else if(type == "STATIC"){
			classHierarchy = staticHierarchy;
			helpClasses = staticHelpClasses;
			greenList = staticKPIList;
			fileContent = staticClassesContent;
		}
		
		//Start with static
		var children = classHierarchy["children"];	
		
		for(var ix in children){
			var chld = children[ix]["children"];
			for(jx in chld){
				var className = chld[jx]["name"];
				try{
					definition.push(helpClasses[className][0]["text"]);
				}catch(err){
					console.log(err);
				}
			
				classes.push(chld[jx]["name"]);
				category.push(children[ix]["name"]);
				var flag = false;
				for(var i in greenList){
					if(className == greenList[i]){
						flag = true;	
						break;
					}
				}
				
				var tempText = "";
				for(var j in fileContent[className]) {
					tempText +=  fileContent[className][j] + " ";
				}
				contentText.push(tempText);
				
				if(flag){
					classColor.push("GREEN");
				}
				else{
					classColor.push("RED");
				}
			}
		}
		
		for(var i=0;i< classes.length; i++){
	    	var row;
	    	row = {Category:category[i],Class:classes[i],Definition:definition[i],Result:classColor[i],
						FileContent:contentText[i]};
	    	data.push(row);
	    }	
		
		return data;
	}	
	
	
	function calculateDynamicSummary(){	
		var data = [];	
		var dynamic_def = [];
		var dynamic_className = [];
		var dynamicColor = [];
		var contentText = [];
		
		var dynamic_Classes = dynamicHierarchy["children"];	
		
		for(var ixx in dynamic_Classes){
			var className  = dynamic_Classes[ixx]["name"];
			console.log("Dynamic className :: ",className);
			dynamic_def.push(dynamicHelpClasses[className][0]["text"]);
			dynamic_className.push(dynamic_Classes[ixx]["name"]);
			if(dynamicResult[className]) {
				dynamicColor.push("GREEN");
			}else {
				dynamicColor.push("RED");
			}
			var tempText = "";
			for(var j in dynamicPopUpContent[className]) {
				tempText +=  dynamicPopUpContent[className][j] + " ";
			}
			contentText.push(tempText);
		}	
		
		for(var j=0;j<dynamic_className.length;j++){
	    	var row;
	    	row = {Class:dynamic_className[j],Definition:dynamic_def[j], Result:dynamicColor[j],
					FileContent:contentText[j]};
	    	data.push(row);
	    }
		
	    return data;		
	}
	
	function getAlerts(){
		var alertsData = [];
		if ($.isEmptyObject(dynamicAlert)){
			var text = "NA";
			var data;
			data = {Definition: text};
			alertsData.push(data);
		}else{
				for (var key in dynamicAlert) {
					var alerts;
					alerts = {Definition: dynamicAlert[key]["output"]};
					alertsData.push(alerts);
				}
			}
		
		return alertsData;
	}
	
	
	function createMultitab() {
		var data1 = [{ScoreType:"Static_Score", Score:staticScore},{ScoreType:"Dynamic_Score", Score:staticScore}];
	    var data2 = [{a:100,b:10},{a:200,b:20}];	  
	    var opts = [{sheetid:'One',header:true},{sheetid:'Two',header:false},{sheetid:'Three',header:false},{sheetid:'Four',header:false}];
		var opts = [{sheetid:'One',header:true},{sheetid:'Two',header:false}];
	    var res = alasql('SELECT INTO XLSX("restest344b.xlsx",?) FROM ?',
	                     [opts,[data1,data2]]);
	}
	
	
	
	function download_csv(data) {
		var csv = 'Class Name,Result,Definition,Text from SoW,Opportunity Owner Comments\n';
		data.forEach(function(row) {
				csv += row.join(',');
				csv += "\n";
		});	 
		
		var hiddenElement = document.createElement('a');
		document.body.appendChild(hiddenElement);
		hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(csv);
		hiddenElement.target = '_blank';
		hiddenElement.download = fileName + '_result.csv';
		hiddenElement.click();
	}
	
	$(document).on('click', '#download', function(){
		var data = CreateCSVData();
		download_csv(data);
	});
	
		
	function CreateCSVData(classHierarchy){ 
		var data = [];
		//var jsonData = JSON.parse(sessionStorage.getItem("classHierarchy"));
		var jsonData = classHierarchy;
		var chld = jsonData["children"];
		var  rColor, rDef, rText;
		for(var i in chld){
			var children = chld[i]["children"];
			for(var j in children){
				var row =[];	
				var temp = children[j];
				if(KPIClassesBool[temp["name"]] > 0 ){
					rColor = "Green";
				}else {
					rColor = "Red";
				}
				row.push(temp["name"]);
				row.push(rColor);				
				
				rDef = "";
				var list = jsonHelpData[temp["name"]];
				for(var k in list){
					rDef += list[k]["text"].replace(/,/g , '');
				}
				row.push(rDef);	
				
				
				rText = "";
				list = classesContent[temp["name"]];
				for(var k in list){
					rText +=  list[k].replace(/,/g , '').replace(/<b>/g ,"").replace(/<\/b>/,"") + ".";					
				}
				row.push(rText); 				
				data.push(row);	
				console.log(rDef);
			}
		}
		console.log("data is :: ", data);
		return data;
	}
	
	
	
	
	
	
	
	
	
	
	




