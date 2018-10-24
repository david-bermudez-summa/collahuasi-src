package mse389

import com.mincom.ellipse.attribute.Attribute
import com.mincom.ellipse.edoi.ejb.msf038.MSF038Key
import com.mincom.ellipse.edoi.ejb.msf052.MSF052Key
import com.mincom.ellipse.edoi.ejb.msf052.MSF052Rec
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Rec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf385.MSF385Key
import com.mincom.ellipse.edoi.ejb.msf385.MSF385Rec
import com.mincom.ellipse.edoi.ejb.msf387.MSF387Key
import com.mincom.ellipse.edoi.ejb.msf387.MSF387Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.edoi.ejb.msf38b.MSF38BKey
import com.mincom.ellipse.edoi.ejb.msf38b.MSF38BRec
import com.mincom.ellipse.edoi.ejb.msf38c.MSF38CKey
import com.mincom.ellipse.edoi.ejb.msf38c.MSF38CRec
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceModifyRequestDTO
import com.mincom.enterpriseservice.ellipse.valuations.ValuationsServiceModItemsReplyCollectionDTO
import com.mincom.enterpriseservice.ellipse.valuations.ValuationsServiceModItemsReplyDTO
import com.mincom.enterpriseservice.ellipse.valuations.ValuationsServiceModItemsRequestDTO
import com.mincom.enterpriseservice.ellipse.valuations.ValuationsServiceRetrieveItemsReplyCollectionDTO
import com.mincom.enterpriseservice.ellipse.valuations.ValuationsServiceRetrieveItemsReplyDTO
import com.mincom.enterpriseservice.ellipse.valuations.ValuationsServiceRetrieveItemsRequestDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import com.mincom.ria.action.impl.AbstractEDOIAction

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import java.text.DecimalFormat
import java.util.List
/**
* Creted by Summa Consulting 2018
* DBERMUDEZ  14/07/2018: Codigo inicial.v1
* .....................  Se crea el campo porcentaje de cumplimiento y se valida como mandatorio
* .....................  Se guarda el porcentaje en el campo PERF-GTEE-VAL, el monto calculado
* .....................  en campo PERF-GUARANTOR del MSF384
*/

public class ValuationsService_retrieveItems extends ServiceHook {

	public String Version = "v2";
	
	@Override
	public Object onPreExecute(Object input) {
		 
		return null;
	}
	
	@Override
	public Object onPostExecute(Object input, Object result) {
		
		log.info(Version);
		log.info("onPostExecute CLASS: " + result.getClass().toString());
		DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###.##");
		
		ValuationsServiceRetrieveItemsRequestDTO[] inputDTO = (ValuationsServiceRetrieveItemsRequestDTO[]) input;
		ValuationsServiceRetrieveItemsReplyDTO[] resultDTO; 
		
		if (result instanceof ValuationsServiceRetrieveItemsReplyDTO ){
			resultDTO = (ValuationsServiceRetrieveItemsReplyDTO[]) result;
		} else {
			ValuationsServiceRetrieveItemsReplyCollectionDTO res = (ValuationsServiceRetrieveItemsReplyCollectionDTO) result;
			resultDTO = res.getReplyElements();
		}
		
		for (ValuationsServiceRetrieveItemsReplyDTO itemDTO : resultDTO ){
			MSF387Key msf387key = new MSF387Key();
			msf387key.setContractNo(itemDTO.getContractNo())
			msf387key.setPortionNo(itemDTO.getPortionNo())
			msf387key.setElementNo(itemDTO.getElementNo())
			msf387key.setCategoryNo(itemDTO.getCategoryNo())
			
			MSF387Rec msf387rec =  tools.edoi.findByPrimaryKey(msf387key);
			
			BigDecimal categoryPaid = msf387rec.getActPaidVal();
			BigDecimal categoryTotal= msf387rec.getCategBasePrice();
			String balanceToPay = (decimalFormat.format(categoryTotal - categoryPaid) + ".00").padLeft(20);
			
			itemDTO.addCustomAttribute(setNewCustomValue("balanceToPay",balanceToPay));
		}
		
		return result;
	}
	
	private Attribute setNewCustomValue( String fieldName, Object value){
		Attribute att = new Attribute();
		att.setName(fieldName)
		att.setValue(value.toString());
		return att;
	}
		
}
