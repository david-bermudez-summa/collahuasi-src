package mso156

import com.mincom.ellipse.edoi.ejb.msf012.MSF012Key
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Rec
import com.mincom.ellipse.ejra.mso.GenericMsoRecord
import com.mincom.ellipse.eroi.linkage.mss080.MSS080LINK
import com.mincom.ellipse.hook.hooks.MSOHook
import java.text.DecimalFormat
import org.slf4j.LoggerFactory

/**
 *  Created by SUMMA CONSULTING S.A.S on 17/05/2018.
 *  26-Jun-2018 DBERMUDEZ Ahora el envio del correo no es inmediatamente se haga la recepcion OFFSITE,
 *                        se guardan los datos en el MSF012 para que luego el cobepo lo envie.
 *  17-May-2018 JAFETH  Ellipse 8
 */
class MSM156B extends MSOHook {
	String poNo = "";
	HashMap<String,MSF012Rec> msf012recg = new HashMap<String,MSF012Rec>();
	String Version = "Version 20180903 - v5"

	@Override
	public GenericMsoRecord onDisplay(GenericMsoRecord screen) {
		info("onDisplay MSM156B");
		info(Version)
		return screen;
	}

	@Override
	public GenericMsoRecord onPreSubmit(GenericMsoRecord screen) {

		info("onPreSubmit MSM156B");
		poNo = screen.getField("PO_NO2I").getValue().trim();
		
		saveCacheMSF012(screen);
		info("ok")
		return null;
	}
	
	@Override
	public GenericMsoRecord onPostSubmit(GenericMsoRecord input, GenericMsoRecord result) {
		info("OnPostSubmit MSM156B");
		
		String errInput;
		String errResult;
		
		try{
			errInput = input.getField("ERRMESS2I").getValue().trim()
		} catch (Exception ex ){
			errInput = input.getField("ERRMESS1I").getValue().trim()
		}
		
		try{
			errResult = input.getField("ERRMESS2I").getValue().trim()
		} catch (Exception ex ){
			errResult = input.getField("ERRMESS1I").getValue().trim()
		}
		
		info("JM:" + errInput)
		info("JM:" + errResult)
		//Si no hay error en el antes y despuess
		if (
			(errInput.equals("") && errResult.equals("")) ||
			(!errInput.equals("") && errResult.equals("")) ||
			(result.getMapname().equals("MSM23GD") )
			){
				info("JM:" + msf012recg.toString())

				for (Map.Entry map : msf012recg.entrySet()){
					MSF012Rec msf012rec = map.getValue();
					info(msf012rec.toString());
					createMSF012Rec(msf012rec.getPrimaryKey().getKeyValue(), msf012rec.getDataArea())
				}
				
		}
		return null;
	}
	
	public void createMSF012Rec(String keyValue, String dataValue){
		MSF012Key msf012key = new MSF012Key();
		msf012key.setDataType("C");
		msf012key.setKeyValue(keyValue);
		
		MSF012Rec msf012rec = new MSF012Rec();
		msf012rec.setPrimaryKey(msf012key)
		msf012rec.setDataArea(dataValue);
		
		try{
			tools.edoi.findByPrimaryKey(msf012key);
			String newItemS = msf012key.getKeyValue().padRight(60).substring(19, 22);
			int newItemI = 0;
			if (!newItemS.trim().equals("")){
				newItemI = newItemI + 1;
			}
			
			newItemI.toString().padLeft(3,"0");
			
			tools.edoi.create(msf012rec);
		} catch (Exception ex ){
			tools.edoi.update(msf012rec);
		}
	}
	
	public void saveCacheMSF012(GenericMsoRecord result){
		DecimalFormat formatVal = new DecimalFormat("0000000.00");
		
		for (int i = 1; i < 8; i++) {
	
			if (!result.getField("ITEM_NO2I${i}").getValue().trim().equals("") ){
				log.info("PROTEGIDO?:" + i + " -> " +result.getField("COMPLETE_IND2I${i}").isProtected());
				
				if (result.getField("COMPLETE_IND2I${i}").isProtected()){
					String qty = result.getField("VAL_RECEIVED2I${i}").getValue().padLeft(15, "0")
					
					String keyValue = "RECEIPT" + poNo.trim().padLeft(6) + result.getField("ITEM_NO2I${i}").getValue().trim().padLeft(3);
					String dataValue = "N" + qty + tools.commarea.TodaysDate + "OF";
				
					saveCachedData(keyValue, dataValue);
				} else {
					if (!result.getField("COMPLETE_IND2I${i}").getValue().toString().trim().equals("")){
						String qty = result.getField("VAL_RECEIVED2I${i}").getValue().padLeft(15, "0")
						
						String keyValue = "RECEIPT" + poNo.trim().padLeft(6) + result.getField("ITEM_NO2I${i}").getValue().trim().padLeft(3);
						String dataValue = "N" + qty + tools.commarea.TodaysDate + "OF";
					
						saveCachedData(keyValue, dataValue);
					}
				}
			}		
		}
	}

	public void saveCachedData(String keyValue, String dataValue){
		MSF012Key msf012key = new MSF012Key();
		msf012key.setDataType("C");
		msf012key.setKeyValue(keyValue);
		
		MSF012Rec msf012rec = new MSF012Rec();
		msf012rec.setPrimaryKey(msf012key)
		msf012rec.setDataArea(dataValue);
		
		if (!msf012recg.containsKey(keyValue))
			msf012recg.put(keyValue, msf012rec);
	}

	public void info(String value) {
		def logObject = LoggerFactory.getLogger(getClass());
		logObject.info("------------- " + value)
	}
}