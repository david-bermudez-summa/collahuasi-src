package coeire;

import com.mincom.ellipse.service.m3140.stockcodeholdings.*;
import com.abb.ventyx.csb.directive.Directive
import com.abb.ventyx.csb.mse135.Mse135DetailScreenDefinition;
import com.abb.ventyx.csb.model.widget.Widget
import com.mincom.ellipse.app.security.SecurityToken
import com.mincom.ellipse.types.m0000.instances.DstrctCode;
import com.mincom.ellipse.client.connection.ConnectionHolder
import com.mincom.ellipse.errors.exceptions.FatalException
import com.mincom.ellipse.script.plugin.GenericScriptCreate
import com.mincom.ellipse.script.plugin.GenericScriptDelete
import com.mincom.ellipse.script.plugin.GenericScriptExecute
import com.mincom.ellipse.script.plugin.GenericScriptExecuteForCollection
import com.mincom.ellipse.script.plugin.GenericScriptMethodType
import com.mincom.ellipse.script.plugin.GenericScriptOnScreenLoadCsbDirective
import com.mincom.ellipse.script.plugin.GenericScriptPlugin
import com.mincom.ellipse.script.plugin.GenericScriptResult
import com.mincom.ellipse.script.plugin.GenericScriptResults
import com.mincom.ellipse.script.plugin.GenericScriptUpdate
import com.mincom.ellipse.script.plugin.RequestAttributes
import com.mincom.ellipse.script.plugin.RestartAttributes
import com.mincom.ellipse.service.m1000.customisation.CustomisationService
import com.mincom.ellipse.service.m3140.stockcodeholdings.StockCodeHoldingsService
import com.mincom.ellipse.types.m3140.instances.StockCodeDistrictSearchParam
import com.mincom.ellipse.types.m3140.instances.WarehouseHoldingsDTO
import com.mincom.ellipse.types.m3140.instances.WarehouseHoldingsServiceResult
import com.mincom.enterpriseservice.ellipse.ConnectionId
import com.mincom.enterpriseservice.ellipse.EllipseSubroutineService
import com.mincom.enterpriseservice.ellipse.EllipseSubroutineServiceLocator
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.stockusage.StockUsageServiceRetrieveWhouseUsageReplyCollectionDTO
import com.mincom.enterpriseservice.ellipse.stockusage.StockUsageServiceRetrieveWhouseUsageReplyDTO
import com.mincom.enterpriseservice.ellipse.stockusage.StockUsageServiceRetrieveWhouseUsageRequestDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import com.mincom.ews.service.transaction.TransactionService
import com.mincom.ria.action.csb.CSBDirectiveAction
import com.mincom.ria.action.csb.CSBDirectiveGridAction
import com.mincom.ria.action.csb.CSBDirectiveScriptCaller
import com.mincom.ria.debug.DataBindingServiceXMLLogging
import com.mincom.ria.util.OpenAMUtil
import groovy.sql.GroovyRowResult
import org.codehaus.groovy.runtime.SqlGroovyMethods
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.transaction.interceptor.TransactionInterceptor
import org.springframework.transaction.jta.JtaTransactionManager
import com.mincom.ellipse.errors.Message
import com.mincom.ellipse.errors.UnlocalisedError
import com.mincom.ellipse.errors.UnlocalisedMessage
import com.mincom.ellipse.m1000.customisation.orchestrator.CustomisationRetrieveCustomisationOrchestrator
import com.mincom.ellipse.m1000.genericscript.orchestrator.GenericScriptDTOUtil
import com.mincom.ellipse.types.m0000.instances.StockCode;

import java.awt.Component.BaselineResizeBehavior
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.List

import javax.persistence.criteria.CriteriaBuilder.Trimspec
import com.mincom.ellipse.edoi.common.logger.EDOILogger
import com.mincom.ellipse.edoi.ejb.msf002.MSF002_SC3801Rec
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf020.MSF020Key
import com.mincom.ellipse.edoi.ejb.msf020.MSF020Rec
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Rec
import com.mincom.ellipse.edoi.ejb.msf385.MSF385Key
import com.mincom.ellipse.edoi.ejb.msf385.MSF385Rec
import com.mincom.ellipse.edoi.ejb.msf581.MSF581Key
import com.mincom.ellipse.edoi.ejb.msf581.MSF581Rec
import com.mincom.ellipse.edoi.ejb.msf586.MSF586Key
import com.mincom.ellipse.edoi.ejb.msf586.MSF586Rec

public class coeire  extends GenericScriptPlugin implements GenericScriptExecute, GenericScriptCreate, GenericScriptUpdate, GenericScriptDelete, GenericScriptExecuteForCollection {
	private String entityType = "+IR";
	private String currencyIndex = "CNTR"
	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
    public void info(String value) {
        def logObject = LoggerFactory.getLogger(getClass());
        logObject.info("------------- " + value)
    }
	
	/*
	 ***************************
	 * LOGIC FOR HEADER ********
	 ***************************
	 */
	@Override
	public GenericScriptResults execute(SecurityToken arg0, List<RequestAttributes> arg1, Boolean arg2)
			throws FatalException {
		
		GenericScriptResults results = new GenericScriptResults()
		String action = arg1.get(0).getAttributeStringValue("action")
		
		info(" ::: ACTION:" + action)
		
		if (action.equals("READ")){
			results = read(results, arg1);
		} else if (action.equals("UPDATE")){
			results = createOrUpdate(results, arg0, arg1);
		} else if (action.equals("DELETE")){
			deleteHeader(results, arg1)
		}
		
		return results;
	}	
	
	private boolean isValidPortionNo(String contractNo, String portionNo){
		
		MSF385Key msf385key = new MSF385Key();
		msf385key.setContractNo(contractNo)
		msf385key.setPortionNo(portionNo)
		
		try{
			if (portionNo != null){
				edoi.findByPrimaryKey(msf385key)
				return true;
			} else {
				return true;
			}
		} catch (Exception ex){
			return false;
		}
		
	}
	
	private GenericScriptResults read(GenericScriptResults results, List<RequestAttributes> arg1){
		GenericScriptResult result = new GenericScriptResult()
		
		String contractNo = arg1.get(0).getAttributeStringValue("contractNo")
		String contractDesc = arg1.get(0).getAttributeStringValue("contractDesc")
		info(" ::: CONTRACT NO:" + contractNo)
		
		Constraint cEntityType = MSF071Key.entityType.equalTo(entityType);
		Constraint cEntityValue= MSF071Key.entityValue.equalTo(contractNo);
		Constraint cRefNo      = MSF071Key.refNo.equalTo("001");
		Constraint cSeqNums    = MSF071Key.seqNum.lessThanEqualTo("002");
		
		QueryImpl query = new QueryImpl(MSF071Rec.class).and(cEntityType).and(cEntityValue).and(cRefNo).and(cSeqNums).orderBy(MSF071Rec.msf071Key);
		edoi.search(query,{ MSF071Rec msf071rec ->
			if (msf071rec.getPrimaryKey().getSeqNum().equals("001")){
				result.addAttribute("baseDate", convertToDate(msf071rec.getRefCode()));
			}
			
			if (msf071rec.getPrimaryKey().getSeqNum().equals("002")){
				result.addAttribute("portionNo", msf071rec.getRefCode());
			}
		})
		
		result.addAttribute("contractNo", contractNo)
		result.addAttribute("contractDesc", contractDesc)
		
		results.add(result);
		
		return results;
	}
	
	private GenericScriptResults createOrUpdate(GenericScriptResults results, SecurityToken arg0,  List<RequestAttributes> arg1){
		GenericScriptResult result = new GenericScriptResult()
		
		String contractNo = arg1.get(0).getAttributeStringValue("contractNo")
		Date   baseDate   = arg1.get(0).getAttributeDateValue("baseDate")
		String portionNo  = arg1.get(0).getAttributeStringValue("portionNo")
		
		boolean isValid = isValidPortionNo(contractNo, portionNo)

		if (isValid){
			if (baseDate != null){
				saveRefCodeValue(contractNo, "001", "001", dateFormat.format(baseDate), arg0.userId )
			}
			if (portionNo != null){
				saveRefCodeValue(contractNo, "001", "002", portionNo, arg0.userId )
			}
		} else {
			result.addError(new UnlocalisedError("PORCION ${portionNo} NO ES VALIDA PARA EL CONTRATO ${contractNo}"));
			results.add(result);
		}
		
		String error = validateItemExists(arg1);
		if (error != null){
			result.addInformationalMessage(new UnlocalisedMessage(error));
			results.add(result);
		}
		
		return results;
	}
	
	private void deleteHeader(GenericScriptResults results, List<RequestAttributes> arg1){
		GenericScriptResult result = new GenericScriptResult()
		
		for (int i = 0; i < arg1.size(); i++){
			String contractNo = arg1.get(i).getAttributeStringValue("contractNo")
			
			Constraint cEntityType = MSF071Key.entityType.equalTo(entityType);
			Constraint cEntityValue= MSF071Key.entityValue.equalTo(contractNo);
			
			QueryImpl query = new QueryImpl(MSF071Rec.class).and(cEntityType).and(cEntityValue);
			
			edoi.deleteAll(query);
		}
	}
	
	public void saveRefCodeValue(String entityValue, String refNo, String seqNum, String refCode, String userId){
		info("::: SAVE REF CODE");
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
			
			edoi.create(msf071rec);
			info("::: SAVE REF CODE CREATE");
		} catch (Exception ex) {
			edoi.update(msf071rec);
			info("::: SAVE REF CODE UPDATE");
		}
	}
	
	public Date convertToDate(String valueToDate){
		try {
			Date date1 = dateFormat.parse(valueToDate);
			return date1;
		} catch (Exception ex ){
			return null;
		}
	}

	/*
	 ***************************
	 * LOGIC FOR ITEMS *********
	 ***************************
	 */
	///////////////////////////SEARCH/////////////////////////////////
	@Override
	public GenericScriptResults executeForCollection(SecurityToken paramSecurityToken,
			RequestAttributes paramRequestAttributes, Integer paramInteger, RestartAttributes paramRestartAttributes)
			throws FatalException {
		info("executeForCollection")
		GenericScriptResults results = new GenericScriptResults()
		String action = paramRequestAttributes.getAttributeStringValue("action")
		
		if (action.equals("indicatorList")){
			results = getIndicatorList(paramRequestAttributes);
		} else if (action.equals("searchItems")){
			results = searchItems(paramRequestAttributes);
		}
		
		return results;
	}
	
	private GenericScriptResults searchItems(RequestAttributes paramRequestAttributes){
		GenericScriptResults results = new GenericScriptResults();
		
		String contractNo = paramRequestAttributes.getAttributeStringValue("contractNo")
		info(" ::: CONTRACT NO:" + contractNo)
		
		Constraint cEntityType = MSF071Key.entityType.equalTo(entityType);
		Constraint cEntityValue= MSF071Key.entityValue.equalTo(contractNo);
		Constraint cRefNo      = MSF071Key.refNo.equalTo("001");
		Constraint cSeqNum     = MSF071Key.seqNum.greaterThan("002");
		
		QueryImpl query = new QueryImpl(MSF071Rec.class).and(cEntityType).and(cEntityValue).and(cRefNo).and(cSeqNum).orderBy(MSF071Rec.msf071Key);
		
		GenericScriptResult result = new GenericScriptResult()
		edoi.search(query,{ MSF071Rec msf071rec ->
			
			String code = msf071rec.getRefCode().padRight(20)
			result = new GenericScriptResult()
			
			result.addAttribute("indicator", code.substring(0, 4));
			result.addAttribute("value", code.substring(10, 20).trim());
			result.addAttribute("seqNum", msf071rec.getPrimaryKey().getSeqNum());
			
			results.add(result);
		})

		return results;
	}
	
	private GenericScriptResults getIndicatorList(RequestAttributes paramRequestAttributes){
		
		GenericScriptResults results = new GenericScriptResults();
		
		Constraint cTableType = MSF010Key.tableType.equalTo("IX");
		Constraint cTableCode = MSF010Key.tableCode.like(currencyIndex + "%");
		
		QueryImpl query = new QueryImpl(MSF010Rec.class).and(cTableType).and(cTableCode);
		
		edoi.search(query,{ MSF010Rec msf010rec ->
			GenericScriptResult result = new GenericScriptResult();
			String code = msf010rec.getPrimaryKey().getTableCode().padRight(20)
			result.addAttribute("key",code.substring(4,8)) 
			result.addAttribute("desc", msf010rec.getTableDesc())
			
			results.add(result);
			
		})
		
		return results;
	}
	
	///////////////////////////CREATE/////////////////////////////////
	@Override
	public GenericScriptResults create(SecurityToken token, List<RequestAttributes> paramList,
			Boolean paramBoolean) throws FatalException {

		info("::: CREATE ITEMS")
		GenericScriptResults results = new GenericScriptResults();
		
		results = validate(results, paramList, "CREATE")
		
		if (results.getServiceResults().size() > 0)
			return results;

		
		for (RequestAttributes param : paramList ){
			String contractNo = param.getAttributeStringValue("contractNo");
			String seqNum = getLastSeqNum(contractNo)
			String indicator = param.getAttributeStringValue("indicator");
			String value = param.getAttributeStringValue("value");
			
			info(":::::" + contractNo);
			info(":::::" + indicator);
			info(":::::" + value);
			
			saveRefCodeValue(contractNo, "001", seqNum, indicator.padRight(10) + value.padRight(10), token.userId)
		}
		
		return results;
	}
	
	public String getLastSeqNum(String contractNo){
		Constraint cEntityType = MSF071Key.entityType.equalTo(entityType);
		Constraint cEntityValue= MSF071Key.entityValue.equalTo(contractNo);
		Constraint cRefNo = MSF071Key.refNo.equalTo("001");
				
		QueryImpl query = new QueryImpl(MSF071Rec.class).max(MSF071Key.seqNum).and(cEntityType).and(cEntityValue).and(cRefNo);

		String lastSeqNum = "003";
		edoi.search(query, { it ->
			if (it != null){
				lastSeqNum = Integer.parseInt(it) + 1;
			}
		})
		
		return lastSeqNum.padLeft(3, "0");
	}
	
	///////////////////////////UPDATE/////////////////////////////////
	@Override
	public GenericScriptResults update(SecurityToken token, List<RequestAttributes> paramList,
			Boolean paramBoolean) throws FatalException {		
		info("::: UPDATE ITEMS")
		GenericScriptResults results = new GenericScriptResults();
		
		boolean errorFound = validate(results, paramList, "UPDATE")
		
		if (results.getServiceResults().size() > 0)
			return results;
		
		for (RequestAttributes param : paramList ){
			String contractNo = param.getAttributeStringValue("contractNo");
			String seqNum = param.getAttributeStringValue("seqNum");
			String indicator = param.getAttributeStringValue("indicator");
			String value = param.getAttributeStringValue("value");
			
			info(":::::" + contractNo);
			info(":::::" + indicator);
			info(":::::" + value);
			
			saveRefCodeValue(contractNo, "001", seqNum, indicator.padRight(10) + value.padRight(10), token.userId)
		}
		
		return results;
	}
	/////////////////////////DELETE/////////////////////////////////
	@Override
	public GenericScriptResults delete(SecurityToken paramSecurityToken, List<RequestAttributes> paramList,
			Boolean paramBoolean) throws FatalException {
			
		info("::: DELETE ITEMS")
		GenericScriptResults results = new GenericScriptResults();
		info("delete")
		results = deleteItem(results, paramList);
		
		return results;
	}
	
	private GenericScriptResults deleteItem(GenericScriptResults results, List<RequestAttributes> arg1){
		GenericScriptResult result = new GenericScriptResult()
		results = validate(results, arg1, "DELETE");
		
		if (results.getServiceResults().size() == 0){
		
			for (int i = 0; i < arg1.size(); i++){
				String contractNo = arg1.get(i).getAttributeStringValue("contractNo")
				String seqNum = arg1.get(i).getAttributeStringValue("seqNum")
				
				Constraint cEntityType = MSF071Key.entityType.equalTo(entityType);
				Constraint cEntityValue= MSF071Key.entityValue.equalTo(contractNo);
				Constraint cRefNo = MSF071Key.refNo.equalTo("001");
				Constraint cSeqNo = MSF071Key.seqNum.equalTo(seqNum);
				
				QueryImpl query = new QueryImpl(MSF071Rec.class).and(cEntityType).and(cEntityValue).and(cRefNo).and(cSeqNo);
				
				edoi.deleteAll(query);
			}
		}
		
		return results
	}
	
	private String validateItemExists(List<RequestAttributes> arg1){
		info("VAL::POR");
		String error = null;
		
		String contractNo = arg1.get(0).getAttributeStringValue("contractNo")
		
		Constraint cEntityType = MSF071Key.entityType.equalTo(entityType);
		Constraint cEntityValue= MSF071Key.entityValue.equalTo(contractNo);
		Constraint cRefNo = MSF071Key.refNo.equalTo("001");
		Constraint cSeqNo = MSF071Key.seqNum.greaterThan("002");
		
		QueryImpl query = new QueryImpl(MSF071Rec.class).and(cEntityType).and(cEntityValue).and(cRefNo).and(cSeqNo);
		boolean isValid = false;
		edoi.search(query, { MSF071Rec msf071rec ->
			
			if (!msf071rec.getRefCode().trim().equals("")){
				isValid = true;
			}
		})
		
		if (!isValid){
			error = "ES NECESARIO INGRESAR LOS PESOS PARA QUE EL POLINOMIO PUEDA SER APLICADO";
		} 
		
		return error;

	}
	
	private GenericScriptResults validate(GenericScriptResults results,  List<RequestAttributes> arg1, String action ){
		info("validate")
		GenericScriptResult result = new GenericScriptResult();
		String contractNo = arg1.get(0).getAttributeStringValue("contractNo")
		BigDecimal total = 0;
		BigDecimal totalParam = 0;
		String error = null;
		
		ArrayList<String> seqNums = new ArrayList<String>();
		seqNums.add("001")
		seqNums.add("002")
		HashMap<String, String> map = new HashMap<String, String>();
				
		for (int i = 0; i < arg1.size(); i++){
			//try {
				info("::: VALUE" + i + " :: " + arg1.get(i).getAttributeStringValue("value"))
				info("::: REFNO" + i + " :: " + arg1.get(i).getAttributeStringValue("seqNum"))
				info("::: INDICATOR PARAM" + i + " :: " + arg1.get(i).getAttributeStringValue("indicator"))
				
				String seqNum = arg1.get(i).getAttributeStringValue("seqNum");
				String indicator = arg1.get(i).getAttributeStringValue("indicator");
				
				totalParam += new BigDecimal(arg1.get(i).getAttributeStringValue("value"));
			
				if (map.containsKey(indicator.trim())){
					error = "Indicador duplicado [" + indicator + "]" ;
				} else {
					map.put(indicator.trim(),indicator.trim());
				}
				
				if (seqNum != null){
					seqNums.add(seqNum)
				}
			//} catch (Exception ex) {
			//	ex.getMessage()
			//}
		}
		
		info(seqNums.toString())
		Constraint cEntityType  = MSF071Key.entityType.equalTo(entityType);
		Constraint cEntityValue = MSF071Key.entityValue.equalTo(contractNo);
		Constraint cRefNo       = MSF071Key.refNo.equalTo("001");
		Constraint cSeqNum      = MSF071Key.seqNum.notIn(seqNums);
		
		boolean itemFound= false;
		
		QueryImpl query = new QueryImpl(MSF071Rec.class).and(cEntityType).and(cEntityValue).and(cSeqNum).and(cRefNo);
		edoi.search(query,{ MSF071Rec msf071rec ->
			itemFound = true;
			
			String code = msf071rec.getRefCode().padRight(20);
			info("BD::: " + code.substring(0, 10).trim());
			if (map.containsKey(code.substring(0, 10).trim())){
				error = "Indicador duplicado [" + code.substring(0, 10)+ "]" ;
				log.info("EXISTE");
			} else {
				map.put(code.substring(0, 10).trim(), code.substring(0, 10).trim());
				log.info("NO EXISTE");
			}
		
			try{
				BigDecimal value =new BigDecimal(code.substring(10,20).trim())
				info("valor"+ value);
				total += value;
			} catch (Exception ex){
				info(ex.getMessage())
			}
			
		})
	
		if (action.equals("CREATE") && !itemFound){
			total = 0;
		}
		
		if (action.equals("CREATE") && itemFound){
			total = 100;
			totalParam = 0;
		}
		
		if (action.equals("DELETE") && itemFound){
			totalParam = 0;
		}
		
		info("::: total" + total)
		info("::: totalParam" + totalParam)
		
		if ((total + totalParam) != 100){
			error = "LOS VALORES DE LOS INDICES DEBEN SER IGUALES A 100"
		}
		
		if (error != null){
			/*cSeqNum = MSF071Key.seqNum.equalTo("003");
			query = new QueryImpl(MSF071Rec.class).and(cEntityType).and(cEntityValue).and(cSeqNum).and(cRefNo);
			edoi.search(query,{ MSF071Rec msf071rec ->
				msf071rec.setRefCode("0");
				edoi.update(msf071rec);
			});*/
		
			result.addError(new UnlocalisedError(error));
			results.add(result);
		}
		
		return results;
	}

	
}