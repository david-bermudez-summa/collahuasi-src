package mseapm;

import com.mincom.ellipse.attribute.Attribute
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.eroi.linkage.mss080.MSS080LINK
import com.mincom.ellipse.eroi.linkage.mssfxc.MSSFXCLINK
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.ellipse.types.m0000.instances.AuthAmount
import com.mincom.ellipse.types.m3875.instances.TransactionServiceResult
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.WarningMessageDTO
import com.mincom.enterpriseservice.ellipse.securities.SecuritiesServiceCreateReplyCollectionDTO
import com.mincom.enterpriseservice.ellipse.securities.SecuritiesServiceCreateReplyDTO
import com.mincom.enterpriseservice.ellipse.securities.SecuritiesServiceCreateRequestDTO
import com.mincom.enterpriseservice.ellipse.securities.SecuritiesServiceModifyReplyCollectionDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import groovy.sql.Sql

import java.text.DecimalFormat

/*
 * Creted by Summa Consulting 2018
 * DBERMUDEZ 09/07/2018: Se adiciona validacion en el campo comentario para que se digite correctamente la 
 * ..................... moneda de la boleta de garantia.
 * ..................... de una segunda factura para un proveedor tipo 'PP'. (v2)  
 * JMORALES 23/05/2016:  Codigo inicial. (v1).
 * 
 * */

public class ApprovalsManagerService_retrieveApprovals extends ServiceHook {
    public Sql sql;

	public String Version = "Version 20180709 - v1"
	
    @Override
    public Object onPreExecute(Object input) {
		log.info(Version)
        return null;
    }
	
	@Override
	public Object onPostExecute(Object input, Object result) {
		log.info(Version)
		log.info("onPostExecute")
		
		DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###.##");
		
		TransactionServiceResult[] resultDTOs = (TransactionServiceResult[]) result;
		for (TransactionServiceResult resultDTO : resultDTOs){
			BigDecimal value  = ((AuthAmount) resultDTO.getServiceDto().getValueByName("amountToAuthorise")).getValue();
			String secondAmount =  decimalFormat.format(convertValue(value, tools.commarea.LocalCurrency, tools.commarea.SecondCurr))
			resultDTO.getServiceDto().addCustomAttribute(setNewCustomValue("secAmount",secondAmount));
		}

		return resultDTOs;
	}
	
	private Attribute setNewCustomValue( String fieldName, Object value){
		Attribute att = new Attribute();
		att.setName(fieldName)
		att.setValue(value.toString());
		return att;
	}
	
	
	private BigDecimal convertValue(BigDecimal value, String fromCurrency, String toCurrency){
		BigDecimal valorLocal;
		BigDecimal valorCurrContrato;
		
		if (tools.commarea.LocalCurrency.trim().equals(fromCurrency.trim())) {
			valorLocal = value;
		} else {
		
			MSSFXCLINK mssfxclink = tools.eroi.execute("MSSFXC", { MSSFXCLINK mssfxclink ->
		
				mssfxclink.setOptionFxc("1")
				mssfxclink.setInputValue(value)
				mssfxclink.setConvTypeSw("F")
				mssfxclink.setRateTypeSw("B")
				mssfxclink.setLocalCurrency(tools.commarea.LocalCurrency);
				mssfxclink.setForeignCurr(fromCurrency);
				mssfxclink.setTranDate(tools.commarea.TodaysDate);
		
			});
		
			valorLocal = mssfxclink.getFlValueConv().setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		
		MSSFXCLINK mssfxclink = tools.eroi.execute("MSSFXC", { MSSFXCLINK mssfxclink ->
		
			mssfxclink.setOptionFxc("1")
			mssfxclink.setInputValue(valorLocal)
			mssfxclink.setConvTypeSw("L")
			mssfxclink.setRateTypeSw("B")
			mssfxclink.setLocalCurrency(tools.commarea.LocalCurrency);
			mssfxclink.setForeignCurr(toCurrency);
			mssfxclink.setTranDate(tools.commarea.TodaysDate);
		
		});
			
		valorCurrContrato = mssfxclink.getFlValueConv().setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return 	valorCurrContrato;
	}
	

	
}
