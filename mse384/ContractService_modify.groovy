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
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceModifyReplyDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceModifyRequestDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceReadReplyDTO
import com.mincom.enterpriseservice.ellipse.securities.SecuritiesServiceCreateReplyCollectionDTO
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

public class ContractService_modify extends ServiceHook {

	public String Version = "v1 - 20180714";
	public static WarningMessageDTO[] prevWarnings;

	@Override
	public Object onPreExecute(Object input) {
		log.info(Version);
		log.info("onPreExecute");
	}
	
    @Override
    public Object onPostExecute(Object input, Object result) {
		log.info(Version);
		log.info("onPostExecute");

		ContractServiceModifyReplyDTO resultDTO = (ContractServiceModifyReplyDTO) result
		ContractServiceModifyRequestDTO inputDTO = (ContractServiceModifyRequestDTO) input;
		
		Constraint cTableType = MSF010Key.tableType.equalTo("+PBG");
		Constraint cActiveFlag= MSF010Rec.activeFlag.equalTo("Y");
		Constraint cAssocRec = MSF010Rec.assocRec.equalTo(tools.commarea.PositionId.trim());
		QueryImpl query = new QueryImpl(MSF010Rec.class).and(cTableType).and(cActiveFlag).and(cAssocRec);
		
		String fulfillmentPrc = getCustomValue(inputDTO.getCustomAttributes(),"fulfillmentPrc");
		DecimalFormat df = new DecimalFormat("###,##0.00");
		
		MSF384Key msf384key = new MSF384Key();
		msf384key.setContractNo(inputDTO.getContractNo())
		
		MSF384Rec msf384rec = (MSF384Rec)tools.edoi.findByPrimaryKey(msf384key);
		BigDecimal bgValue = toBigDecimal(fulfillmentPrc);
		
		log.info("DBS:::"  + bgValue)
		log.info("DBS:::"  + msf384rec.getPerfGteeVal())
		if (bgValue != msf384rec.getPerfGteeVal()){
			MSF010Rec msf010recPBG = tools.edoi.firstRow(query)
			if (msf010recPBG == null) {				
				throw new EnterpriseServiceOperationException(
					new ErrorMessageDTO("9999", "USTED NO ESTA AUTORIZADO PARA CAMBIAR EL PORCENTAJE DE GARANTIA", "ContractNo", 0, 0));
			}
		}
		
		msf384rec.setPerfGteeVal(bgValue);
		msf384rec.setPerfGuarantor(df.format(bgValue * msf384rec.getContractPrice() / 100));

		tools.edoi.update(msf384rec);
		
		
		validateSecurities(resultDTO)
		
        return null;
    }
	

	private void validateSecurities(ContractServiceModifyReplyDTO replyDTO){
		ArrayList<WarningMessageDTO> warnings = new ArrayList<WarningMessageDTO>();
		
		BigDecimal totalSecurLogAmt = 0;
		BigDecimal securLogAmt = 0;
		
		MSF384Key msf384key = new MSF384Key();
		msf384key.setContractNo(replyDTO.getContractNo());
		MSF384Rec msf384rec = tools.edoi.findByPrimaryKey(msf384key);
		
		Constraint cContractNo = MSF38AKey.contractNo.equalTo(replyDTO.getContractNo())
		QueryImpl query = new QueryImpl(MSF38ARec.class).and(cContractNo)
		String error;
		boolean foundSecurities =false;
		tools.edoi.search(query,{ MSF38ARec msf38arec ->
			log.info("LOGNAME" + msf38arec.getSecurName());
			foundSecurities =true;
			//La validación se realiza sobre la sumatoria del campo MSF38A.SECUR_LOG_AMOUNT
			//de todas las boletas de garantía cuyo MSF38A.SECUR_DEP_TYPE = 01
			//y su estado MSF38A.SECUR_DEP_FRM in (I, A, P)
			String currencyGtee = msf38arec.getSecurName().toUpperCase();
			
			error = validateCurrency(currencyGtee);
			/*if (error != null){
				throw new EnterpriseServiceOperationException(
					new ErrorMessageDTO("9999", error, "SecurityName", 1, 1));
			}*/
			
			if (msf38arec.getSecurDepType().equals("01") && msf38arec.getReleaseDueDate() >= tools.commarea.TodaysDate && 
				(
					msf38arec.getSecurDepFrm().equals("I") ||
					msf38arec.getSecurDepFrm().equals("A") ||
					msf38arec.getSecurDepFrm().equals("P")
				)
			){
				log.info(msf384rec.getCurrencyType().trim() + currencyGtee);
				if (!msf384rec.getCurrencyType().trim().equals(currencyGtee)){
					//Enviar correo de notificacion cuando la moneda de la boleta
					//es diferente a la del contrato.
					
					warnings.add(new WarningMessageDTO("W2:0099", "ADVERTENCIA - Moneda de la garantia difiere a la Moneda del Contrato", "SecurName", 0, 0));
					replyDTO.setWarningsAndInformation((WarningMessageDTO[])warnings.toArray());
					securLogAmt = convertValue(msf38arec.getSecurLogAmount(), currencyGtee, msf384rec.getCurrencyType())
				} else {
					securLogAmt = msf38arec.getSecurLogAmount()
				}
				
				log.info("Subtotal" + securLogAmt.toString());
				totalSecurLogAmt += securLogAmt;
			}
		});
		log.info("Total DDDD:" + totalSecurLogAmt.toString()+ " -- " +msf384rec.getPerfGuarantor().trim());
		
		BigDecimal contractPrice = 0;
		try{
			double b = Double.valueOf(msf384rec.getPerfGuarantor().trim().replace(",", ""));
			contractPrice = BigDecimal.valueOf(b);
		} catch (Exception ex) { }
		
		log.info("VALOR " + contractPrice)
		if (contractPrice > 0 && foundSecurities){
			//Se busca la tolerancia seteada
			MSF010Rec msf010TBG = getMSF010Rec("+TBG",tools.commarea.District + "001");
			if (msf010TBG != null){
				try{
					BigDecimal tolerance = new BigDecimal(msf010TBG.getAssocRec().padRight(12).substring(0, 6).trim());		
					BigDecimal tolValue = contractPrice / (1 + tolerance / 100);
					
					log.info("DIFERENCIA");
					log.info("totalSecurLogAmt:" + totalSecurLogAmt);
					log.info("totalSecurLogAmt:" + tolValue);
					
					if (totalSecurLogAmt < tolValue){
						
						DecimalFormat myFormatter = new DecimalFormat('###,###.###');
						String totalS = myFormatter.format(totalSecurLogAmt);
						String tolValueS = myFormatter.format(contractPrice);
						
						warnings.add(new WarningMessageDTO("W2:0099", "ADVERTENCIA - Monto de boletas de garantias " + totalS +
							" es inferior al minimo exigido " + tolValueS + " [Valores en moneda " + msf384rec.getCurrencyType() + "]","SecurName", 0, 0));
						replyDTO.setWarningsAndInformation((WarningMessageDTO[])warnings.toArray());						
					}
				} catch (Exception ex){
					log.info(ex.getMessage())
					log.info(ex.getLocalizedMessage())
				}
			}
		}
		
		if (replyDTO.getWarningsAndInformation() != null){
			
			if (prevWarnings != null){
				if (prevWarnings.size() == replyDTO.getWarningsAndInformation().size()){
					boolean equalsTo=false;
					for (int i = 0; i < prevWarnings.size(); i++){
						if (replyDTO.getWarningsAndInformation()[i].getMessage().equals(prevWarnings[i].getMessage())){
							equalsTo =  true;
						} else {
							equalsTo = false;
							break;
						}
					}
					
					if (equalsTo){
						replyDTO.setWarningsAndInformation(null);
					}
				}
			}
			
			prevWarnings = replyDTO.getWarningsAndInformation();
		}
		
		if (error != null){
			prevWarnings == null;
		}
	}
	
	public String validateCurrency(String currency){
		if (currency.trim().size() > 4){
			return "La moneda de la boleta de garantia no es valida: " + currency;
		}
		
		MSF010Rec msf010rec = getMSF010Rec("CU", currency);
		
		if (msf010rec == null){
			return "La moneda no esta definida en la tabla [CU] - " + currency;
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
	
	private Object getCustomValue(Attribute[] atts, String fieldName){
		for (Attribute att : atts){
			if(att.getName().equals(fieldName)){
				return att.getValue();
			}
		}
		
		return null;
	}
	
	private BigDecimal toBigDecimal(String value){
		try{
			String decimalSeparator = value.substring(value.length()- 3, value.length()- 2);
		
			if (decimalSeparator.equals(",")){
				value = value.replace(".", "").replace(",", ".");
			}
				
			return new BigDecimal(value);
		} catch (Exception ex) {
			return 0;
		}
	}
	
	private BigDecimal convertValue(BigDecimal value, String fromCurrency, String toCurrency){
		BigDecimal valorLocal;
		BigDecimal valorCurrContrato;
		try{
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
		} catch (Exception ex){
			return 0;
		}
	}
	
}
