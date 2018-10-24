package mse17d

import com.mincom.ellipse.service.m3140.stockcodeholdings.*;
import com.abb.ventyx.csb.directive.Directive
import com.abb.ventyx.csb.mse135.Mse135DetailScreenDefinition;
import com.abb.ventyx.csb.model.widget.Widget
import com.mincom.ellipse.app.security.SecurityToken
import com.mincom.ellipse.types.m0000.instances.DstrctCode;
import com.mincom.ellipse.client.connection.ConnectionHolder
import com.mincom.ellipse.errors.exceptions.FatalException
import com.mincom.ellipse.script.plugin.GenericScriptCreate
import com.mincom.ellipse.script.plugin.GenericScriptExecute
import com.mincom.ellipse.script.plugin.GenericScriptExecuteForCollection
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
import com.mincom.ellipse.edoi.ejb.msf581.MSF581Key
import com.mincom.ellipse.edoi.ejb.msf581.MSF581Rec
import com.mincom.ellipse.edoi.ejb.msf586.MSF586Key
import com.mincom.ellipse.edoi.ejb.msf586.MSF586Rec

public class coe17d  extends GenericScriptPlugin implements GenericScriptExecute
{
	private String initialPeriod = "";
	
    public void info(String value) {
        def logObject = LoggerFactory.getLogger(getClass());
        logObject.info("------------- " + value)
    }

	@Override
	public GenericScriptResults execute(SecurityToken arg0, List<RequestAttributes> arg1, Boolean arg2)
			throws FatalException {

		String dstrctCode = arg1.get(0).getAttributeStringValue("districtCode")
		String stockCode = arg1.get(0).getAttributeStringValue("stockCode")
		
		info("DBS:" + dstrctCode)
		info("DBS:" + stockCode)
		
		GenericScriptResults results = new GenericScriptResults()
		GenericScriptResult result = new GenericScriptResult()
	
		WarehouseHoldingsServiceResult[] reply = service.get('StockCodeHoldings').searchWarehouseCounts(
			{StockCodeDistrictSearchParam request ->
				
				request.setDistrictCode(new DstrctCode(dstrctCode));
				request.setStockCode(new StockCode(stockCode));
				request.setShowZeroHoldings(new com.mincom.ellipse.types.m0000.instances.Boolean(true));
			}
		);
		
		int indexAtributte = 0;
		
		double[] totals = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
		
		result.addAttribute("title0"," ____ ")
		result.addAttribute("title1","Available")
		result.addAttribute("title2","Consigned SOH")
		result.addAttribute("title3","In Transit")
		result.addAttribute("title4","Waiting Receipt")
		result.addAttribute("title5","Dues Out")
		result.addAttribute("title6","Reserved")
		result.addAttribute("title7","Dues In")
		result.addAttribute("title8","ROQ")
		result.addAttribute("title9","Owned SOH")
		result.addAttribute("title10","Total SOH")
		result.addAttribute("title11","Total Assets")
		result.addAttribute("title12","Being Picked")
		result.addAttribute("title13","Discrepant")
		result.addAttribute("title14","Loan Dues Out")
		result.addAttribute("title15","Loan Dues In")
		
		indexAtributte++;
		
		for (WarehouseHoldingsServiceResult warehouse :  reply){
			
			WarehouseHoldingsDTO dto = warehouse.getWarehouseHoldingsDTO();
			
			result.addAttribute("col00_" + String.valueOf(indexAtributte),dto.getWarehouseName().getAsEllipseValue())
			
			result.addAttribute("col01_" + String.valueOf(indexAtributte),dto.getAvailableForIssue().getDoubleValue())
			totals[0] += dto.getAvailableForIssue().getDoubleValue();
			result.addAttribute("col02_" + String.valueOf(indexAtributte),dto.getConsignSOH().getDoubleValue())
			totals[1] += dto.getConsignSOH().getDoubleValue();
			result.addAttribute("col03_" + String.valueOf(indexAtributte),dto.getInTransit().getDoubleValue())
			totals[2] += dto.getInTransit().getDoubleValue();
			result.addAttribute("col04_" + String.valueOf(indexAtributte),dto.getWaitingReceipt().getDoubleValue())
			totals[3] += dto.getWaitingReceipt().getDoubleValue();
			result.addAttribute("col05_" + String.valueOf(indexAtributte),dto.getDuesOut().getDoubleValue())
			totals[4] += dto.getDuesOut().getDoubleValue();
			result.addAttribute("col06_" + String.valueOf(indexAtributte),dto.getReserved().getDoubleValue())
			totals[5] += dto.getReserved().getDoubleValue();
			result.addAttribute("col07_" + String.valueOf(indexAtributte),dto.getDuesIn().getDoubleValue())
			totals[6] += dto.getDuesIn().getDoubleValue();
			result.addAttribute("col08_" + String.valueOf(indexAtributte),dto.getRecommendedOrderQuantity().getDoubleValue())
			totals[7] += dto.getRecommendedOrderQuantity().getDoubleValue();
			result.addAttribute("col09_" + String.valueOf(indexAtributte),dto.getOwnedSOH().getDoubleValue())
			totals[8] += dto.getOwnedSOH().getDoubleValue();
			result.addAttribute("col10_" + String.valueOf(indexAtributte),dto.getTotalOnHand().getDoubleValue())
			totals[9] += dto.getTotalOnHand().getDoubleValue();
			result.addAttribute("col11_" + String.valueOf(indexAtributte),dto.getTotalAssets().getDoubleValue())
			totals[10] += dto.getTotalAssets().getDoubleValue();
			result.addAttribute("col12_" + String.valueOf(indexAtributte),dto.getWarehouseService().getDoubleValue())
			totals[11] += dto.getWarehouseService().getDoubleValue();
			result.addAttribute("col13_" + String.valueOf(indexAtributte),dto.getDiscrepantQuantity().getDoubleValue())
			totals[12] += dto.getDiscrepantQuantity().getDoubleValue();
			result.addAttribute("col14_" + String.valueOf(indexAtributte),dto.getLoansDueOut().getDoubleValue())
			totals[13] += dto.getLoansDueOut().getDoubleValue();
			result.addAttribute("col15_" + String.valueOf(indexAtributte),dto.getLoansDueIn().getDoubleValue())
			totals[14] += dto.getLoansDueIn().getDoubleValue();
			
			//log.info(totals);
			indexAtributte++;
		}

		result.addAttribute("total1",totals[0])
		result.addAttribute("total2",totals[1])
		result.addAttribute("total3",totals[2])
		result.addAttribute("total4",totals[3])
		result.addAttribute("total5",totals[4])
		result.addAttribute("total6",totals[5])
		result.addAttribute("total7",totals[6])
		result.addAttribute("total8",totals[7])
		result.addAttribute("total9",totals[8])
		result.addAttribute("total10",totals[9])
		result.addAttribute("total11",totals[10])
		result.addAttribute("total12",totals[11])
		result.addAttribute("total13",totals[12])
		result.addAttribute("total14",totals[13])
		result.addAttribute("total15",totals[14])
		
		results.add(result)
		
		return results;
	}	
}