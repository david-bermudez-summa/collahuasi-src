package mseapm

import com.abb.ventyx.csb.directive.Directive
import com.abb.ventyx.csb.mse135.Mse135DetailScreenDefinition;
import com.abb.ventyx.csb.model.widget.Widget
import com.mincom.ellipse.app.security.SecurityToken
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
import com.mincom.enterpriseservice.ellipse.ConnectionId
import com.mincom.enterpriseservice.ellipse.EllipseSubroutineService
import com.mincom.enterpriseservice.ellipse.EllipseSubroutineServiceLocator
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
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

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.text.SimpleDateFormat

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
/**
 * Created by Summa Consulting on 10/05/2018.
 * 
 * DBERMUDEZ 30-08-2018 .... Se adiciona el tipo de transaccion PR para poder tener integracion
 * ......................... con el desarrollo entregado por HCyC. v2
 * DBERMUDEZ 15-06-2018 .... COEDCL - Initial Coding - DoCument Link
 * ......................... Abstract: Este programa maneja el despliegue de los 
 * ......................... documentos asociados a los siguientes tipos de transaccion 
 * ......................... en el MSEAPM NI, CO, VA, NI v1
 */
public class coedcl  extends GenericScriptPlugin implements GenericScriptExecuteForCollection
{
	private static String version = "2";

    public void info(String value) {
        def logObject = LoggerFactory.getLogger(getClass());
        logObject.info("------------- " + value)
    }

    /**
     * validateUser
     * @param security
     * @param results
     * @param result
     * @return
     */
    public String validateUser(SecurityToken security, GenericScriptResults results, GenericScriptResult result) {
        try {
            MSF020Rec msf020Rec = edoi.findByPrimaryKey(new MSF020Key(dstrctCode: security.getDistrict(), entity: security.getUserId(), entryType: "S"))

            return msf020Rec.getEmployeeId()

        } catch (Exception e2) {
            info("UserId No existe")
            result.addError(new UnlocalisedError("UserId No existe"));
            results.add(result)
        }
    }

    //<<<<<---------------------------------- BEGIN  : EXECUTE FOR COLLECTION ---------------------------------->>>>>
    public GenericScriptResults executeForCollection(SecurityToken securityToken, RequestAttributes requestAttributes,
                                                     Integer maxNumberOfObjects, RestartAttributes restartAttributes) throws FatalException
    {
		log.info("Version " + version);
		GenericScriptResult result;
        GenericScriptResults results = new GenericScriptResults()

        String district = securityToken.getDistrict()
        String userId = securityToken.getUserId()
        String employeeLogged = validateUser(securityToken, results, result);
        String positionId = securityToken.getRole()

        String tran877Type = requestAttributes.getAttributeStringValue("tran877Type")
        String transactionKey = requestAttributes.getAttributeStringValue("transactionKey")

		Constraint cDocRefType = MSF581Key.docRefType.equalTo(tran877Type);
		
		info("DOC_REF_TYPE###:" + tran877Type)
		info("TRANS_KEY###:" + transactionKey.trim())
		QueryImpl query 
		
		if (tran877Type.equals("PO") || tran877Type.equals("CO") ){
			Constraint cDocReference = MSF581Key.docReference.equalTo(transactionKey.trim());
			query = new QueryImpl(MSF581Rec.class).and(cDocRefType).and(cDocReference);
		} else if (tran877Type.equals("VA")){
			String contract = transactionKey.substring(0,8)
			String valuationNo = transactionKey.substring(8,12)
			
			Constraint cDocReference = MSF581Key.docReference.equalTo(contract);
			Constraint cDocRefOther = MSF581Key.docRefOther.equalTo(valuationNo)
			query = new QueryImpl(MSF581Rec.class).and(cDocRefType).and(cDocReference).and(cDocRefOther);
		} else if (tran877Type.equals("NI")){
			String dstrctCode = transactionKey.substring(0,4)
			String supplierNo = transactionKey.substring(4,10)
			String invNo = transactionKey.substring(10,20)
			String invNoItem = transactionKey.substring(20,23)
			
			Constraint cDocReference = MSF581Key.docReference.equalTo(invNo);
			Constraint cDocRefOther = MSF581Key.docRefOther.equalTo(supplierNo)
			query = new QueryImpl(MSF581Rec.class).and(cDocRefType).and(cDocReference).and(cDocRefOther);

		} else if (tran877Type.equals("PR")){
			String purchReq = transactionKey.trim()
			
			Constraint cDocReference = MSF581Key.docReference.equalTo(purchReq);
			Constraint cDocRefOther = MSF581Key.dstrctCode.equalTo(district)
			query = new QueryImpl(MSF581Rec.class).and(cDocRefType).and(cDocReference).and(cDocRefOther);
		}
		if (query != null){
			edoi.search(query, { MSF581Rec msf581rec ->  
			
				Constraint cDocNo = MSF586Key.documentNo.equalTo(msf581rec.getPrimaryKey().getDocumentNo());
				QueryImpl query2 = new QueryImpl(MSF586Rec.class).and(cDocNo);
				
				edoi.search(query2,{ MSF586Rec msf586rec ->
					result = new GenericScriptResult()
					result.addAttribute("documentNo",msf581rec.getPrimaryKey().getDocumentNo())
					result.addAttribute("elecRef",msf586rec.getElecRef())
					results.add(result)
				})
			})
		}
		
        return results;
    }
    //<<<<<---------------------------------- END  : EXECUTE FOR COLLECTION ---------------------------------->>>>>
}