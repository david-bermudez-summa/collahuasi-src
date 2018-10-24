package mse384

import com.mincom.ellipse.attribute.Attribute
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Rec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceModifyReplyDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceModifyRequestDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceReadReplyDTO
import com.mincom.enterpriseservice.ellipse.contractitem.ContractItemServiceModifyPortMileReplyDTO
import com.mincom.enterpriseservice.ellipse.contractitem.ContractItemServiceModifyPortMileRequestDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
/**
* Creted by Summa Consulting 2018
* DBERMUDEZ  14/07/2018: Codigo inicial.v1
* .....................  Se crea el campo porcentaje de cumplimiento y se valida como mandatorio
* .....................  Se guarda el porcentaje en el campo PERF-GTEE-VAL, el monto calculado
* .....................  en campo PERF-GUARANTOR del MSF384
*/

public class ContractItemService_modifyPortMile extends ServiceHook {
	private String entityType = "+AP";
	public String Version = "v1 - 20180714";
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	@Override
	public Object onPreExecute(Object input) {
	}
	
    @Override
    public Object onPostExecute(Object input, Object result) {
		log.info(Version);
		log.info("onPostExecute");

		ContractItemServiceModifyPortMileReplyDTO resultDTO = (ContractItemServiceModifyPortMileReplyDTO) result
		ContractItemServiceModifyPortMileRequestDTO inputDTO = (ContractItemServiceModifyPortMileRequestDTO) input;		
		String applyAdjustment = getCustomValue(inputDTO.getCustomAttributes(),"applyAdjustment");
		String contractNo = inputDTO.getContractNo();
		String portionNo = inputDTO.getPortion().padLeft(3,"0");
		
		if (applyAdjustment.equals("Y"))
			saveRefCodeValue(contractNo, portionNo, "001", "1", tools.commarea.UserId)
		else
			saveRefCodeValue(contractNo, portionNo, "001", "0", tools.commarea.UserId)
			
		resultDTO.addCustomAttribute(setNewCustomValue("applyAdjustment",applyAdjustment));
			
        return null;
    }
	
	private Attribute setNewCustomValue( String fieldName, Object value){
		Attribute att = new Attribute();
		att.setName(fieldName)
		att.setValue(value.toString());
		return att;
	}
	
	private void setCustomValue(Attribute[] atts, String fieldName, Object value){
		for (Attribute att : atts){
			if(att.getName().equals(fieldName)){
				if (value instanceof BigDecimal){
					att.setValue((BigDecimal)value);
				} else if (value instanceof String){
					att.setValue((String)value);
				}
				
				log.info(att.getName() + " : " + att.getValue())
			}
		}
	}
	
	public void saveRefCodeValue(String entityValue, String refNo, String seqNum, String refCode, String userId){
		
		MSF071Rec msf071rec = new MSF071Rec();
		
		DateFormat timeFormat = new SimpleDateFormat("HHmmss");
		
		Date actualDate = new Date();
		
		try{
			MSF071Key msf071key = new MSF071Key();
			msf071key.setEntityType(entityType);
			msf071key.setEntityValue(entityValue);
			msf071key.setRefNo(refNo);
			msf071key.setSeqNum(seqNum);
			
			msf071rec.setPrimaryKey(msf071key)
			msf071rec.setLastModDate(dateFormat.format(actualDate));
			msf071rec.setLastModTime(timeFormat.format(actualDate));
			msf071rec.setLastModUser(userId);
			
			msf071rec.setRefCode(refCode);
			
			tools.edoi.create(msf071rec);
			
		} catch (Exception ex) {
			tools.edoi.update(msf071rec);
			
		}
	}
	
	private Object getCustomValue(Attribute[] atts, String fieldName){
		for (Attribute att : atts){
			if(att.getName().equals(fieldName)){
				return att.getValue();
			}
		}
		
		return null;
	}
}
