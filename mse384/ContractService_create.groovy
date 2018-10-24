package mse384

import com.mincom.ellipse.attribute.Attribute
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.eroi.linkage.mssfxc.MSSFXCLINK
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.WarningMessageDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceCreateReplyDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceCreateRequestDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceModifyReplyDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceModifyRequestDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceReadReplyDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl

import java.text.DecimalFormat
/**
* Creted by Summa Consulting 2018
* DBERMUDEZ  14/07/2018: Codigo inicial.v1
* .....................  Se crea el campo porcentaje de cumplimiento y se valida como mandatorio
* .....................  Se guarda el porcentaje en el campo PERF-GTEE-VAL, el monto calculado
* .....................  en campo PERF-GUARANTOR del MSF384
*/

public class ContractService_create extends ServiceHook {
	public static WarningMessageDTO[] prevWarnings;

	public String Version = "v1 - 20180714";
	@Override
	public Object onPreExecute(Object input) {
		log.info(Version);
		log.info("onPreExecute");	
	}
	
    @Override
    public Object onPostExecute(Object input, Object result) {
		log.info(Version);
		ContractServiceCreateRequestDTO inputDTO = (ContractServiceCreateRequestDTO) input;
		ContractServiceCreateReplyDTO resultDTO = (ContractServiceCreateReplyDTO) result;
		
		MSF010Rec msf010TBG = getMSF010Rec("+TBG",tools.commarea.District + "001");
		
		if (msf010TBG != null){
			try{
				BigDecimal initialTol = new BigDecimal(msf010TBG.getAssocRec().padRight(12).substring(6, 12).trim());
				MSF384Key msf384key = new MSF384Key();
				msf384key.setContractNo(resultDTO.getContractNo());
				
				MSF384Rec msf384rec = tools.edoi.findByPrimaryKey(msf384key);
				msf384rec.setPerfGteeVal(initialTol);
				
				tools.edoi.update(msf384rec);
			} catch (Exception ex) {}
		}
		
		return null;
    }
	
	public MSF010Rec getMSF010Rec (String tableType, String tableCode){
		try{
			MSF010Key msf010key = new MSF010Key();
			msf010key.setTableType(tableType)
			msf010key.setTableCode(tableCode)
			
			MSF010Rec msf010rec = tools.edoi.findByPrimaryKey(msf010key);
			return msf010rec;
		} catch (Exception ex){
			return null;
		}
	}
}
