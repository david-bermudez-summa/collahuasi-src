package mse384;

import com.mincom.ellipse.edoi.ejb.msf000.MSF000_DC0012Key
import com.mincom.ellipse.edoi.ejb.msf000.MSF000_DC0012Rec
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.eroi.linkage.mss080.MSS080LINK
import com.mincom.ellipse.eroi.linkage.mssfxc.MSSFXCLINK
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.WarningMessageDTO
import com.mincom.enterpriseservice.ellipse.securities.SecuritiesServiceModifyReplyCollectionDTO
import com.mincom.enterpriseservice.ellipse.securities.SecuritiesServiceModifyRequestDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import groovy.sql.Sql

import java.text.DecimalFormat

/*
 * Creted by Summa Consulting 2018
 * DBERMUDEZ 09/07/2018: Se adiciona validacion en el campo comentario para que se digite correctamente la 
 * ..................... moneda de la boleta de garantia.(v3)
 * ..................... de una segunda factura para un proveedor tipo 'PP'. (v2)  
 * JMORALES 23/05/2016:  Codigo inicial. (v1).
 * 
 * */
public class SecuritiesService_multipleModify extends ServiceHook {
	public Sql sql;
	public static WarningMessageDTO[] prevWarnings;

	public String Version = "Version 20180709 - v1"

	@Override
	public Object onPreExecute(Object input) {
	 // metodo();
		SecuritiesServiceModifyRequestDTO[] requestDTO = (SecuritiesServiceModifyRequestDTO[]) input
		MSF010Rec msf010R

		try {
			log.info("positionId####:" + tools.commarea.positionId);
			msf010R = tools.edoi.findByPrimaryKey(new MSF010Key(tableType: "+38A", tableCode: tools.commarea.positionId))
		} catch (Exception e1) {

		}
		if (msf010R != null) {
			for (int i = 0; i < requestDTO.length; i++) {

				if (msf010R.getAssocRec().trim().equals("C")) {

					if (!requestDTO[i].getSecurityDepForm().equals("I") && !requestDTO[i].getSecurityDepForm().equals("S") && !requestDTO[i].getSecurityDepForm().equals("P")) {

						MSF010Rec msf010Rec = tools.edoi.findByPrimaryKey(new MSF010Key(tableType: "ER", tableCode: "5670"))

						throw new EnterpriseServiceOperationException(
								new ErrorMessageDTO("5670", " ${msf010Rec.getTableDesc()}", "SecurityDepForm", i, i));
					}
				} else if (msf010R.getAssocRec().trim().equals("T")) {

					if (requestDTO[i].getSecurityDepForm().equals("R") || requestDTO[i].getSecurityDepForm().equals("A") || requestDTO[i].getSecurityDepForm().equals("D")) {

						log.info("ContractNumber####:" + requestDTO[i].getContractNumber());
						log.info("SecurityNo####:" + requestDTO[i].getSecurityNo().toString().padLeft(2, "0"));
						String PortionNumber = requestDTO[i].getPortionNumber();
						if (PortionNumber == null) {
							PortionNumber = "  ";
						}
						log.info("PortionNumber####:" + PortionNumber);
						MSF38ARec msf38ARec
						try {
							msf38ARec = tools.edoi.findByPrimaryKey(new MSF38AKey(contractNo: "${requestDTO[i].getContractNumber()}", portionNo: "${PortionNumber}", securNo: "${requestDTO[i].getSecurityNo().toString().padLeft(2, "0")}"))
						} catch (Exception e) {
							log.info("Registro no encontrado####:")
						}
						if (msf38ARec != null) {
							log.info("SecurDepFrm viejo####:" + msf38ARec.getSecurDepFrm());
							log.info("SecurDepFrm nuevo####:" + requestDTO[i].getSecurityDepForm());

							if (msf38ARec.getSecurDepFrm().equals("D") && !requestDTO[i].getSecurityDepForm().equals("D")) {

								MSF010Rec msf010Rec = tools.edoi.findByPrimaryKey(new MSF010Key(tableType: "ER", tableCode: "5670"))
								throw new EnterpriseServiceOperationException(
										new ErrorMessageDTO("5670", " ${msf010Rec.getTableDesc()}", "SecurityDepForm", i, i));

							} else {

								if (msf38ARec.getSecurDepFrm().equals("A") &&
										(!requestDTO[i].getSecurityDepForm().equals("D") && !requestDTO[i].getSecurityDepForm().equals("A"))) {

									MSF010Rec msf010Rec = tools.edoi.findByPrimaryKey(new MSF010Key(tableType: "ER", tableCode: "5670"))

									throw new EnterpriseServiceOperationException(
											new ErrorMessageDTO("5670", " ${msf010Rec.getTableDesc()}", "SecurityDepForm", i, i));

								}
							}
						}
						log.info("SecurityDepForm####:" + requestDTO[i].getSecurityDepForm());

					} else {

						MSF010Rec msf010Rec = tools.edoi.findByPrimaryKey(new MSF010Key(tableType: "ER", tableCode: "5670"))

						throw new EnterpriseServiceOperationException(
								new ErrorMessageDTO("5670", " ${msf010Rec.getTableDesc()}", "SecurityDepForm", i, i));

					}
				}
			}

			//Valida el campo comentario. Este ahora se utilizara para registrar la boleta de garantia
			//FIN 23
			for (int i = 0; i < requestDTO.size(); i++) {
				log.info(i + " ");
				String currencyGtee = requestDTO[i].getSecurityName().toUpperCase();
				String error = validateCurrency(currencyGtee);
				if (error != null){
					throw new EnterpriseServiceOperationException(
						new ErrorMessageDTO("9999", error, "SecurityName", i, i));
				}
			}
			
		} else {
			log.info("Not found positionId####:" + tools.commarea.positionId);

			MSF010Rec msf010Rec = tools.edoi.findByPrimaryKey(new MSF010Key(tableType: "ER", tableCode: "5670"))

			throw new EnterpriseServiceOperationException(
					new ErrorMessageDTO("5670", " ${msf010Rec.getTableDesc()}", "SecurityDepForm", 0, 0));


		}
		

		return null;
	}
	
	public Object onPostExecute(Object input, Object result) {
		log.info(Version)
		log.info("onPostExecute")

		SecuritiesServiceModifyReplyCollectionDTO replyDTO = (SecuritiesServiceModifyReplyCollectionDTO) result
		
		//FIN 23
		validateSecurities(replyDTO)
		return null;

	}
	
	private void validateSecurities(SecuritiesServiceModifyReplyCollectionDTO replyDTO){
		BigDecimal totalSecurLogAmt = 0;
		BigDecimal securLogAmt = 0;
		ArrayList<WarningMessageDTO> warnings = new ArrayList<WarningMessageDTO>();
		
		MSF384Key msf384key = new MSF384Key();
		msf384key.setContractNo(replyDTO.replyElements[0].getContractNumber());
		MSF384Rec msf384rec = tools.edoi.findByPrimaryKey(msf384key);
		
		Constraint cContractNo = MSF38AKey.contractNo.equalTo(replyDTO.replyElements[0].getContractNumber())
		QueryImpl query = new QueryImpl(MSF38ARec.class).and(cContractNo)
		
		tools.edoi.search(query,{ MSF38ARec msf38arec ->
			log.info("LOGNAME" + msf38arec.getSecurName());
			
			//La validación se realiza sobre la sumatoria del campo MSF38A.SECUR_LOG_AMOUNT
			//de todas las boletas de garantía cuyo MSF38A.SECUR_DEP_TYPE = 01
			//y su estado MSF38A.SECUR_DEP_FRM in (I, A, P)
			String currencyGtee = msf38arec.getSecurName().toUpperCase();
			
			log.info("DUE DATE:::" + msf38arec.getReleaseDueDate());
			
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
		double b = toDouble(msf384rec.getPerfGuarantor().trim());
		contractPrice = BigDecimal.valueOf(b);
		
		log.info("VALOR " + contractPrice)
		
		//Se busca la tolerancia seteada
		MSF010Rec msf010TBG = getMSF010Rec("+TBG",tools.commarea.District + "001");
		if (msf010TBG != null){
			//try{
				BigDecimal tolerance = new BigDecimal(msf010TBG.getAssocRec().padRight(12).substring(0, 6).trim());
				
				log.info("TOLERANCIA" +  (1 + tolerance / 100));
				log.info("C " + contractPrice);
				log.info("T" + totalSecurLogAmt);
				
				BigDecimal tolValue = contractPrice / (1 + tolerance / 100);
				
				log.info("QQQQQ TOL VALUE: " + tolValue + " totalSecurLogAmt " + totalSecurLogAmt);
				log.info("warningDisplayed2" + " CMP " + totalSecurLogAmt.compareTo(tolValue));
				
				if (totalSecurLogAmt < tolValue){
					
					DecimalFormat myFormatter = new DecimalFormat('###,###.###');
					String totalS = myFormatter.format(totalSecurLogAmt);
					String tolValueS = myFormatter.format(contractPrice);
					
					warnings.add(new WarningMessageDTO("W2:0099", "ADVERTENCIA - Monto de boletas de garantias " + totalS +
						" es inferior al minimo exigido " + tolValueS + " [Valores en moneda del contrato "+ msf384rec.getCurrencyType() + "]","SecurName", 0, 0));
					replyDTO.setWarningsAndInformation((WarningMessageDTO[])warnings.toArray());
					
				}
			/*} catch (Exception ex){
				log.info(ex.getMessage())
				log.info(ex.getLocalizedMessage())
			}*/
		}

		if (replyDTO.getWarningsAndInformation() != null){
			
			if (prevWarnings != null){
				if (prevWarnings.size() == replyDTO.getWarningsAndInformation().size()){
					boolean equalsTo=false;
					for (int i = 0; i < prevWarnings.size(); i++){
						if (replyDTO.getWarningsAndInformation()[i].getMessage().equals(prevWarnings[i].getMessage())){
							equalsTo =  true;
						} else {
							equalsTo =  false;
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

	}

	
	public void runMss080(String progName, String requesParams) {

		log.info("runMss080")

		String Uuid = UUID.randomUUID().toString().substring(0, 32).toUpperCase();

		tools.eroi.execute('MSS080', { MSS080LINK mss080linkrec ->
			mss080linkrec.uuid = Uuid
			mss080linkrec.requestParams = requesParams
			mss080linkrec.deferDate = tools.commarea.TodaysDate
			mss080linkrec.deferTime = tools.commarea.Time
			mss080linkrec.requestDate = tools.commarea.TodaysDate
			mss080linkrec.requestTime = tools.commarea.Time
			mss080linkrec.requestBy = tools.commarea.UserId
			mss080linkrec.userId = tools.commarea.UserId
			mss080linkrec.dstrctCode = tools.commarea.District;
			mss080linkrec.requestDstrct = tools.commarea.District;
			mss080linkrec.startOption = "Y";
			mss080linkrec.requestRecNo = '01'
			mss080linkrec.requestNo = '01'
			mss080linkrec.pubType = "TXT"
			mss080linkrec.noOfCopies1 = '01'
			mss080linkrec.progReportId = 'A'
			mss080linkrec.medium = 'R'
			mss080linkrec.startOption = "Y"
			mss080linkrec.traceFlg = "N"
			mss080linkrec.progName = progName
			mss080linkrec.taskUuid = Uuid
		})

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
	
	private double toDouble(String value){
		try{
			String decimalSeparator = value.substring(value.length()- 3, value.length()- 2);
			
			if (decimalSeparator.equals(",")){
				value = value.replace(".", "").replace(",", ".");
			}
				
			return Double.valueOf(value.replace(",", ""));
		} catch (Exception ex) {
			return 0;
		}
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
