
createStaticKPIList(staticResult,staticKPIList,staticGreenThreshold,staticRedThreshold);

function createStaticKPIList(staticResult,greenThreshold,staticRedThreshold){
	var answerUnit = staticResult["answer_units"];
	staticKPIList = [];
	var str,class_conf;
	for(var i  in answerUnit){
		if((answerUnit[i]["CLASS"] != "") && (answerUnit[i]["CLASS"] != null)){
			var className = answerUnit[i]["CLASS"];
			staticClassesContent[className] = [];
			if(parseInt(answerUnit[i]["Found"]) == 1){
				staticKPIList.push(className);
			}
			var content = answerUnit[i]["content"];			
			for(var j in content) {
				try {
					str = "";	
					class_conf = content[j]["HProb"].toString().bold();
					if(((parseFloat(content[j]["HProb"].toString()) >= staticRedThreshold) && (answerUnit[i]["Found"] == 0)) || ((parseFloat(content[j]["HProb"].toString()) >= greenThreshold) && (answerUnit[i]["Found"] == 1))){
						str = "( "+class_conf + ") " + content[j]["text"];
						staticClassesContent[className].push(str);	
					}						
				}catch(err){
					console.log(err);
				}	
			}								
		}
	}
}