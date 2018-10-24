package coerit

import java.net.URI;
import com.google.gson.Gson;
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.ws.rs.core.UriBuilder

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import javax.xml.bind.DatatypeConverter;
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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
import com.mincom.ellipse.types.m3001.instances.AuditColumnsDTO
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
import java.sql.Blob
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.List

import com.mincom.ellipse.edoi.common.logger.EDOILogger
import com.mincom.ellipse.edoi.ejb.msf002.MSF002_SC3801Rec
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf020.MSF020Key
import com.mincom.ellipse.edoi.ejb.msf020.MSF020Rec
import com.mincom.ellipse.edoi.ejb.msf053.MSF053Key
import com.mincom.ellipse.edoi.ejb.msf053.MSF053Rec
import com.mincom.ellipse.edoi.ejb.msf054.MSF054Key
import com.mincom.ellipse.edoi.ejb.msf054.MSF054Rec
import com.mincom.ellipse.edoi.ejb.msf055.MSF055Key
import com.mincom.ellipse.edoi.ejb.msf055.MSF055Rec
import com.mincom.ellipse.edoi.ejb.msf581.MSF581Key
import com.mincom.ellipse.edoi.ejb.msf581.MSF581Rec
import com.mincom.ellipse.edoi.ejb.msf586.MSF586Key
import com.mincom.ellipse.edoi.ejb.msf586.MSF586Rec
import com.mincom.ellipse.edoi.ejb.msfimg.MSFIMGKey
import com.mincom.ellipse.edoi.ejb.msfimg.MSFIMGRec

public class coerit  extends GenericScriptPlugin implements GenericScriptExecute, GenericScriptCreate, GenericScriptUpdate
{

	@Override
	public GenericScriptResults execute(SecurityToken arg0, List<RequestAttributes> arg1, Boolean arg2)
			throws FatalException {
		
		log.info("======RITUS=====");
		log.info("DISTRITO:" + arg0.getDistrict())
		String riskWorkNo = "0000033251";
		
		GenericScriptResults results = new GenericScriptResults()
		GenericScriptResult result = new GenericScriptResult()
		result.addAttribute("riskWorkNo",riskWorkNo);
		results.add(result)
		
		return results;
	}
	
	@Override
	public GenericScriptResults update(SecurityToken arg0, List<RequestAttributes> arg1, Boolean arg2)
			throws FatalException {
		String riskWorkNo = "0000033251";
		
		GenericScriptResults results = new GenericScriptResults()
		GenericScriptResult result = new GenericScriptResult()
		result.addAttribute("riskWorkNo",riskWorkNo);
		results.add(result)
		
		return results;
	}
	
	@Override
	public GenericScriptResults create(SecurityToken arg0, List<RequestAttributes> arg1, Boolean arg2)
			throws FatalException {
		String riskWorkNo = "0000033251";
		
		GenericScriptResults results = new GenericScriptResults()
		GenericScriptResult result = new GenericScriptResult()
		result.addAttribute("riskWorkNo",riskWorkNo);
		results.add(result)
		
		return results;
	}

	private MSF010Rec getMSF010Rec(String tableType, String tableCode){
		try{
			MSF010Key msf010key = new MSF010Key();
			msf010key.setTableType(tableType);
			msf010key.setTableCode(tableCode);
			MSF010Rec msf010rec = edoi.findByPrimaryKey(msf010key);
			
			return msf010rec;
		} catch (Exception ex){
			return null;
		}
		
	}

}