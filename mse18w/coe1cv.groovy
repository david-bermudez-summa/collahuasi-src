package mse18w

import java.net.URI;

import javax.ws.rs.core.UriBuilder

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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

public class coe1cv  extends GenericScriptPlugin implements GenericScriptExecute {
	private static final String subscriptionKey = "322ca047df604165bbd7ce7a0907342c";
	private static final String uriBase =
	"https://westcentralus.api.cognitive.microsoft.com/vision/v2.0/analyze";

	private static final String imageToAnalyze =
	"https://upload.wikimedia.org/wikipedia/commons/" +
			"1/12/Broadway_and_Times_Square_by_night.jpg";

	private String initialPeriod = "";
	
    public void info(String value) {
        def logObject = LoggerFactory.getLogger(getClass());
        logObject.info("------------- " + value)
    }

	@Override
	public GenericScriptResults execute(SecurityToken arg0, List<RequestAttributes> arg1, Boolean arg2)
			throws FatalException {
		
		String responseTxt = "";	
		//CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpClient client = new DefaultHttpClient();
		try {
			// Request parameters. All of them are optional.
			// Request parameters and other properties.
			List<NameValuePair> postParams = new ArrayList<NameValuePair>(2);
			postParams.add(new BasicNameValuePair("visualFeatures", "Categories,Description,Color"));
			postParams.add(new BasicNameValuePair("language", "es"));

			// Prepare the URI for the REST API call.
			HttpPost request = new HttpPost(uriBase);
			request.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
			
			// Request headers.
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
			
			// Request body.
			StringEntity requestEntity =
					new StringEntity("{\"url\":\"" + imageToAnalyze + "\"}");
			request.setEntity(requestEntity);

			// Make the REST API call and get the response entity.
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Format and display the JSON response.
				String jsonString = EntityUtils.toString(entity);
				JSONObject json = new JSONObject(jsonString);
				
				responseTxt = json.toString(2);
			}
		} catch (Exception e) {
			// Display error message.
			System.out.println(e.getMessage());
		}
		
		GenericScriptResults results = new GenericScriptResults();
		GenericScriptResult result = new GenericScriptResult();
		result.addAttribute("ActividadVerificadaExt", responseTxt)
		
		results.add(result)
				
		return results;
	}
	
}