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

public class ValuationsService_multipleModItems extends ServiceHook {

	public String Version = "v2";
	private String currency = "CNTR";
	private MSF071Rec msf071recPR;
	
	@Override
	public Object onPreExecute(Object input) {
		 
		return null;
	}
	
	@Override
	public Object onPostExecute(Object input, Object result) {
		
		log.info(Version);
		log.info("onPostExecute");
		
		ValuationsServiceModItemsRequestDTO[] inputDTO = (ValuationsServiceModItemsRequestDTO[]) input;
		ValuationsServiceModItemsReplyDTO[] resultDTO; 
		
		if (result instanceof ValuationsServiceModItemsReplyDTO ){
			resultDTO = (ValuationsServiceModItemsReplyDTO[]) result;
		} else {
			ValuationsServiceModItemsReplyCollectionDTO res = (ValuationsServiceModItemsReplyCollectionDTO) result;
			resultDTO = res.getReplyElements();
		}
		
		String value = getCustomValue(inputDTO[0].getCustomAttributes(),"sendedReadjustPortion"); 
		if (value != null){
			return null;
		}
		
		BigDecimal factorAdjusment = 0;
		BigDecimal total = 0;
		String contrctNo = inputDTO[0].getContractNo();
		String valnNo = inputDTO[0].getValuationNo();
		
		msf071recPR = getMSF071Rec("+IR", contrctNo, "001", "002");

		//Clear all the values associated with the Readjust Portion
		if (msf071recPR == null){
			return null;
		}
		
		BigDecimal totalPI = 0;
		
		Constraint cEntityType = MSF071Key.entityType.equalTo("+IR");
		Constraint cEntityValue= MSF071Key.entityValue.equalTo(contrctNo);
		Constraint cRefNo      = MSF071Key.refNo.equalTo("001");
		Constraint cSeqNum     = MSF071Key.seqNum.greaterThan("002");
		
		QueryImpl query = new QueryImpl(MSF071Rec.class).and(cEntityType).and(cEntityValue).and(cRefNo).and(cSeqNum).orderBy(MSF071Rec.msf071Key);
		ArrayList<MSF071Rec> listIR = tools.edoi.search(query).getResults();
		
		if (listIR.size() == 0){
			return null;
		}

		Constraint cContractNo = MSF38CKey.contractNo.equalTo(contrctNo);
		Constraint cValnNo = MSF38CKey.valnNo.equalTo(valnNo);
		Constraint cPortionNo = MSF38CKey.portionNo.notEqualTo(msf071recPR.getRefCode().trim())
		
		QueryImpl query2 = new QueryImpl(MSF38CRec.class).and(cContractNo).and(cValnNo).and(cPortionNo);
		tools.edoi.search(query2,{ MSF38CRec element->
		//for (ValuationsServiceModItemsReplyDTO element : resultDTO){		
			
			String valuationDate = getValuationDate(contrctNo, valnNo);
			String baseDate = getBaseDate(contrctNo,element.getPrimaryKey().getPortionNo())
			
			if (baseDate != null && needAdjustment(contrctNo,element.getPrimaryKey().getPortionNo())){
				factorAdjusment = 0;
				
				listIR.each{ MSF071Rec msf071rec ->
					
					String code = msf071rec.getRefCode().padRight(20)

					String indexValue = code.substring(10, 20).trim();
					String indexCode = code.substring(0, 4);
										
					BigDecimal IFV = getIndexValue(currency,indexCode, valuationDate)
					BigDecimal IB = getIndexValue(currency,indexCode, baseDate)
					BigDecimal PI = 0;
					
					log.info(":::IFV" + IFV)
					log.info(":::IB" + IB)
					
					try{
						PI = new BigDecimal(indexValue.trim());
					} catch (Exception ex ){
						throw new EnterpriseServiceOperationException(
							new ErrorMessageDTO("9999", "FORMATO DEL VALOR DE PORCENTAJE DE REAJUSTE NO VALIDO" + indexValue.trim(), "ERROR", 0, 0));
					}
					totalPI = totalPI + PI;
					log.info(":::PI" + PI)
					log.info(":::TOTAL PI" + totalPI)
					if (IB == 0){
						throw new EnterpriseServiceOperationException(
							new ErrorMessageDTO("9999", "VALOR DEL INDICE ${indexCode} PARA LA FECHA ${baseDate} ES IGUAL A CERO", "ERROR", 0, 0));
					}
					
					if (IB == null){
						throw new EnterpriseServiceOperationException(
							new ErrorMessageDTO("9999", "VALORES DEL INDICE ${indexCode} PARA LA MONEDA ${currency} EN LA FECHA ${baseDate} NO ESTAN DEFINIDOS", "ERROR", 0, 0));
					}
					
					if (IFV == null){
						throw new EnterpriseServiceOperationException(
							new ErrorMessageDTO("9999", "VALORES DEL INDICE ${indexCode} PARA LA MONEDA ${currency} EN LA FECHA ${valuationDate} NO ESTAN DEFINIDOS", "ERROR", 0, 0));
					}
					
					factorAdjusment += ((IFV - IB) / IB)  * (PI / 100);
				}
				
				log.info(":::TOTAL PI fin" + totalPI)
				if (totalPI < 100){
					throw new EnterpriseServiceOperationException(
						new ErrorMessageDTO("9999", "LOS INDICES CONFIGURADOS EN EL POLINOMIO DEBEN SER IGUALES A 100", "ERROR", 0, 0));
				}
				
				BigDecimal elementValue = 0;
				
				elementValue = element.getActVal();

				log.info("FACTOR " + factorAdjusment);
				log.info("ELEMENTO VALUE " + elementValue);
				total += factorAdjusment * elementValue
			}
			
		})
		
		log.info("TOTAL DE REAJUSTE" + total);
		BigDecimal portionValue = getPortionAdjustmentValue(contrctNo)
		if (total > portionValue){
			
			DecimalFormat myFormatter = new DecimalFormat('###,###.###');
			String totalS = myFormatter.format(total);
			String portionValueS = myFormatter.format(portionValue);
			
			throw new EnterpriseServiceOperationException(
				new ErrorMessageDTO("9999", "VALOR DEL PRECIO DE AJUSTE ${totalS} ES MAYOR A LA PORCION DE PROVISION ${portionValueS}", "ERROR", 0, 0));
		} else {
			ValuationsServiceModItemsRequestDTO readjustItem = new ValuationsServiceModItemsRequestDTO();
			readjustItem.setContractNo(contrctNo)
			readjustItem.setValuationNo(valnNo)
			readjustItem.setPortionNo(msf071recPR.getRefCode().trim())
			readjustItem.setElementNo("01");
			readjustItem.setCategoryNo("01")
			readjustItem.setCalculatedMethod("V");
			readjustItem.setCalculatedType("A");
			readjustItem.setActualValue(total);
			
			modifyReadjustPortion(readjustItem);
			
		}
		
		return null;
	}
	
	private Object getCustomValue(Attribute[] atts, String fieldName){
		for (Attribute att : atts){
			if(att.getName().equals(fieldName)){
				return att.getValue();
			}
		}
		
		return null;
	}
	
	private ValuationsServiceModItemsReplyDTO[] modifyReadjustPortion(ValuationsServiceModItemsRequestDTO requestInput){
		
		requestInput.addCustomAttribute(setNewCustomValue("sendedReadjustPortion","Y"));
		Object values  = tools.service.get('Valuations').modItems({ ValuationsServiceModItemsRequestDTO request ->
			
			request.setContractNo(requestInput.getContractNo())
			request.setValuationNo(requestInput.getValuationNo())
			request.setPortionNo(requestInput.getPortionNo())
			request.setElementNo("01");
			request.setCategoryNo("01")
			request.setCalculatedMethod("V");
			request.setCalculatedType("A");
			request.setActualValue(requestInput.getActualValue());
		});
	
		if (values instanceof ValuationsServiceModItemsReplyDTO ){
			ValuationsServiceModItemsReplyDTO[] reply = (ValuationsServiceModItemsReplyDTO[]) values;
			return reply;
		} else {
			ValuationsServiceModItemsReplyCollectionDTO reply = (ValuationsServiceModItemsReplyCollectionDTO) values;
			return reply.replyElements;
		}
	}
	
	private Attribute setNewCustomValue( String fieldName, Object value){
		Attribute att = new Attribute();
		att.setName(fieldName)
		att.setValue(value.toString());
		return att;
	}
	
	private BigDecimal getPortionAdjustmentValue(String contrctNo){
		MSF071Rec msf071rec = getMSF071Rec("+IR", contrctNo, "001", "002")

		MSF385Key msf385key = new MSF385Key();
		msf385key.setContractNo(contrctNo)
		msf385key.setPortionNo(msf071rec.getRefCode().trim())
		
		try{
			MSF385Rec msf385rec = tools.edoi.findByPrimaryKey(msf385key);
			return msf385rec.getPortionVal();
		} catch (Exception ex){
			return 0;
		}
			
	}
	
	private BigDecimal getIndexValue(String currencyType, String indexType, String date){
		
		date = String.valueOf(99999999 - Integer.parseInt(date))
		
		Constraint cCurrencyType = MSF052Key.currencyType.equalTo(currencyType);
		Constraint cIndexType = MSF052Key.indexType.equalTo(indexType);
		Constraint cRevsdEffDate = MSF052Key.revsdEffDate.greaterThanEqualTo(date);
		
		QueryImpl query = new QueryImpl(MSF052Rec.class).and(cCurrencyType).and(cIndexType).and(cRevsdEffDate);
		MSF052Rec msf052rec = tools.edoi.firstRow(query);
		
		if (msf052rec != null)
			return msf052rec.getExtRelatRate();
		else
			return null;
		
	}
	
	private String getBaseDate(String contractNo, String portionNo){
		MSF071Rec msf071rec = getMSF071Rec("+IR", contractNo, "001", "001")
		
		if (msf071rec == null){
			return null;
		} else {
			if (msf071rec.getRefCode().trim().equals("")){
				return null;
			} else {
				return msf071rec.getRefCode().trim();
			}
		}
	}
	
	private String getValuationDate(String contractNo, String valuationNo){
		MSF38BKey msf38bkey = new MSF38BKey()
		msf38bkey.setContractNo(contractNo)
		msf38bkey.setValnNo(valuationNo)
		
		MSF38BRec msf38brec = tools.edoi.findByPrimaryKey(msf38bkey);
		
		return msf38brec.getValnDate();
	}
	
	private boolean needAdjustment(String contractNo, String portionNo){
		MSF071Rec msf071rec = getMSF071Rec("+AP", contractNo, portionNo.padLeft(3,"0"), "001")
		
		if (msf071rec == null){
			return false;
		} else {
			if (msf071rec.getRefCode().trim().equals("0")){
				return false;
			} else {
				return true;
			}
		}
		
	}
		
	private MSF071Rec getMSF071Rec(String entityType, String entityValue, String refNo, String seqNum){
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
	
}
