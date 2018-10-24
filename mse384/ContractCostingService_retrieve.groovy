package mse384

import com.mincom.ellipse.attribute.Attribute
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceReadReplyDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceReadRequestDTO
import com.mincom.enterpriseservice.ellipse.contractcosting.ContractCostingServiceRetrieveRequestDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl

import java.text.DecimalFormat

import org.springframework.context.i18n.LocaleContextHolder

/**
 * DBERMUDEZ 14/08/2018 .......... Validacion para que solo los responsables de los contratos deben poder ingresar a
 * ............................... revisar la información de los contratos asociados a ello. (v1) 
 * DBERMUDEZ 13/08/2018........... Codigo Inicial. (v0)
 */
public class ContractCostingService_retrieve extends ServiceHook {

	public String Version = "v1";
	@Override
	public Object onPreExecute(Object input) {
		log.info(Version);
		log.info("onPreExecute")
		
		ContractCostingServiceRetrieveRequestDTO inputDTO = (ContractCostingServiceRetrieveRequestDTO) input
		
		if (inputDTO.getContractNo().trim().equals("::ERROR:")){
			log.info("onPreExecute1")
			throw new EnterpriseServiceOperationException(
				new ErrorMessageDTO("9999", "USTED NO ESTA AUTORIZADO PARA ABRIR ESTE CONTRATO", "ContractNo", 0, 0));
		}
		log.info("onPreExecute2")
		return null;
	}
	
}
