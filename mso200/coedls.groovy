package mso200;

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

public class coedls  extends GenericScriptPlugin implements GenericScriptOnScreenLoadCsbDirective{
	private String entityType = "+IR";
	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
    public void info(String value) {
        def logObject = LoggerFactory.getLogger(getClass());
        logObject.info("------------- " + value)
    }

	@Override
	public Directive getOnScreenLoadDirective(RequestAttributes arg0, Widget<?> arg1) {
		// TODO Auto-generated method stub
		log.info(arg0.getClass().toString());
		log.info(arg1.getClass().toString());
		return null;
	}
	
}