package com.ibm.contracts.advisor.FieldGlass.Alert;

import java.util.ArrayList;
import java.util.List;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.vo.FieldGlassOPOBJ;

public class FGAlertHandler implements Constants {

	private  List<FieldGlassOPOBJ> alertList = new ArrayList<FieldGlassOPOBJ>();
	
	public  List<FieldGlassOPOBJ> ProcessAlert(
			String dynamicProcessingInput, String contractType) {
		// TODO Auto-generated method stub
		
		FieldGlassOPOBJ object1  = ClassificationAlert.CheckClassificationAlert(dynamicProcessingInput,contractType);
		alertList.add(object1);
		if (MPFG.equalsIgnoreCase(contractType)){
			FieldGlassOPOBJ object4  = FGDynamicAlert.PaymentMethodAlertFG(dynamicProcessingInput);
			FieldGlassOPOBJ object5  = FGDynamicAlert.SpecialTermAlertFG(dynamicProcessingInput);
			alertList.add(object4);
			alertList.add(object5);			
		}
		
		if(MSFG.equalsIgnoreCase(contractType)){
			//FieldGlassOPOBJ object2  = FGDynamicAlert.SLAAlertFG(dynamicProcessingInput);
			FieldGlassOPOBJ object3  = FGDynamicAlert.SLACreditTermAlertFG(dynamicProcessingInput);
			FieldGlassOPOBJ object4  = FGDynamicAlert.PaymentMethodAlertFG(dynamicProcessingInput);
			FieldGlassOPOBJ object5  = FGDynamicAlert.SpecialTermAlertFG(dynamicProcessingInput);
			//alertList.add(object2);
			alertList.add(object3);
			alertList.add(object4);
			alertList.add(object5);
		}
		return alertList;
	}

}
