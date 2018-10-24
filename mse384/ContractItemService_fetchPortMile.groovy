package mse384

import com.mincom.ellipse.attribute.Attribute
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Rec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf385.MSF385Key
import com.mincom.ellipse.edoi.ejb.msf385.MSF385Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceReadReplyDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceReadRequestDTO
import com.mincom.enterpriseservice.ellipse.contractitem.ContractItemServiceFetchPortMileReplyDTO
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl

import java.text.DecimalFormat

/**
 * DBERMUDEZ 12/10/2018 .......... Se corrige el despliegue de la informacion de cumplimiento.
 * DBERMUDEZ 14/07/2018 .......... Despliegue de los porcentajes y monto de las obligaciones de cumpliemiento.
 * ............................... de las boletas de garantia. v3
 * Modified by JAFETH on 7/23/2016 Modificacion Totales de las boletas de garantias.
 * Created by JAFETH on 5/27/2016 Visualizacion de monto a aprobar de Variacion contrato MSEAPM.
 */
public class ContractItemService_fetchPortMile extends ServiceHook {

	public String Version = "v4 - 20181012";
	private String entityType = "+AP";
	
	@Override
	public Object onPreExecute(Object input){
		
	}
	
    @Override
    public Object onPostExecute(Object input, Object result) {
		log.info(Version);
		log.info("onPostExecute")
		
		DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###.##");
		
        ContractItemServiceFetchPortMileReplyDTO resultDTO = (ContractItemServiceFetchPortMileReplyDTO) result
		String contractNo = resultDTO.getContractNo();
		String portionNo = resultDTO.getPortion().padLeft(3,"0");
		
		MSF385Key msf385key = new MSF385Key();
		msf385key.setContractNo(resultDTO.getContractNo())
		msf385key.setPortionNo(resultDTO.getPortion())

		MSF385Rec msf385rec = tools.edoi.findByPrimaryKey(msf385key);
		BigDecimal categoryPaid = msf385rec.getActPaidVal();
		BigDecimal categoryTotal= msf385rec.getPortionVal();
		String balanceToPay = (decimalFormat.format(categoryTotal - categoryPaid) + ".00").padLeft(30);
		
		resultDTO.addCustomAttribute(setNewCustomValue("approvedValuation",(decimalFormat.format(categoryPaid) + ".00").padLeft(30)));
		resultDTO.addCustomAttribute(setNewCustomValue("balanceToPay",balanceToPay));
		
		MSF071Rec msf071rec = getMSF071Rec(contractNo, portionNo, "001");

		if (msf071rec != null){
			
			String value = 'Y';
			if (msf071rec.getRefCode().trim().equals("1")){
				value = "Y";
			} else {
				value = "N";
			}
			
			resultDTO.addCustomAttribute(setNewCustomValue("applyAdjustment",value));
			
		}
			
        return null;
    }
	
	private MSF071Rec getMSF071Rec(String entityValue, String refNo, String seqNum){
		MSF071Key msf071key = new MSF071Key();
		msf071key.setEntityType(entityType);
		msf071key.setEntityValue(entityValue);
		msf071key.setRefNo(refNo);
		msf071key.setSeqNum(seqNum);
		
		try{
			MSF071Rec msf071rec = tools.edoi.findByPrimaryKey(msf071key);
		} catch (Exception ex){
			return null;
		}

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
	
	private Object getCustomValue(Attribute[] atts, String fieldName){
		for (Attribute att : atts){
			if(att.getName().equals(fieldName)){
				return att.getValue();
			}
		}
		
		return null;
	}
}
