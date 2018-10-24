package coerit

import java.net.URI;
import com.google.gson.Gson;
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive

import java.beans.beancontext.BeanContextServiceProviderBeanInfo
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

import org.apache.commons.vfs.FileSystemManager
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
import com.mincom.ellipse.m1000.file.orchestrator.FileUpdateOrchestrator
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
import com.mincom.ellipse.staging.FileStagingService
import com.mincom.ellipse.types.m1000.validators.FileUpdateValidator
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
import com.ventyx.ellipse.context.EllipseSystemContext

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
import com.mincom.ellipse.efs.EFSHelper
import com.mincom.ellipse.efs.EllipseFileSystem
import com.mincom.ellipse.efs.impl.FileHandlerImpl

public class coe1cv  extends GenericScriptPlugin implements GenericScriptExecute
{
	private WebServiceRequest restCall;
	private static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0";
	private File tempFile;
	private String id; 
	
	//ellipse-elldeve.elldeve.collahuasi.cl
	@Override
	public GenericScriptResults execute(SecurityToken arg0, List<RequestAttributes> arg1, Boolean arg2)
			throws FatalException {
		String resultOCR = "";
		String subscriptionKey = "   ";
		String xmlFile;
		String fileExtention = ""
		String fileSize = ""
		byte[] imageBytes = null;
		FileStagingService fss = new FileStagingService();

		for (RequestAttributes arg : arg1 ){
			xmlFile = arg.getAttributeStringValue("recognizeFile").toString();
			
			id = getAttributeFile("id",xmlFile);			
			tempFile = fss.getStagedFile(id);
			fss.copyStagedFileToEFS(id);
		}
		
		if (tempFile != null){
			imageBytes = convertFileToBytes(tempFile);
		
			MSF010Rec msf010rec = getMSF010Rec("+AZU","01");
			
			if (msf010rec != null ){
				subscriptionKey =  msf010rec.getTableDesc();
			}	
			
			//resultOCR = recognizeText(subscriptionKey, imageBytes, "en", false);
			//resultOCR = formatOCR(resultOCR );
			
			resultOCR = recognizeHandwriting(subscriptionKey, imageBytes);
			resultOCR = formatHandwriting(resultOCR );
			
			tempFile.delete();
		}
		
		GenericScriptResults results = new GenericScriptResults()
		GenericScriptResult result = new GenericScriptResult()
		result.addAttribute("ActividadVerificadaExt",resultOCR);
		result.addAttribute("recognizeFile","");
		results.add(result)
		
		
		return results;
	}
	
	public String getAttributeFile(String attribute, String xmlFile){
		
		String[] xmlFileParts = "";
		
		if (xmlFile != null && xmlFile.size() > 0){
			xmlFileParts = xmlFile.split(attribute + ">")
			
			if (xmlFileParts.size() > 0){
				return xmlFileParts[1].replace("<","").replace("/","");
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public byte[] convertFileToBytes(File file){
		
		byte[] getBytes;
		try {
			getBytes = new byte[(int) file.length()];
			InputStream is = new FileInputStream(file);
			is.read(getBytes);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getBytes;
	}
	
	public String formatOCR(String json){
		JsonParser parser = new JsonParser();
		String text = " ";
		// Obtain Array
		JsonObject jsonObject = parser.parse(json).getAsJsonObject();
		JsonArray regions = jsonObject.getAsJsonArray("regions");
		for (JsonElement region : regions) {
			for (JsonElement line : region.getAsJsonArray("lines")){
				for (JsonElement word : line.getAsJsonArray("words")){
					JsonObject element = word.getAsJsonObject();
					text = text + element.get("text").getAsString() + " ";
				}
				text = text + "\n";
			}
		}
		
		return text;
				
	}
	
	public String formatHandwriting(String json){
		JsonParser parser = new JsonParser();
		String text = " ";
		// Obtain Array
		JsonObject jsonObject = parser.parse(json).getAsJsonObject();
		JsonObject recognitionResult = jsonObject.getAsJsonObject("recognitionResult");
		for (JsonElement line : recognitionResult.getAsJsonArray("lines")){
			JsonObject element = line.getAsJsonObject();
			text = text + element.get("text").getAsString() + " ";
			text = text + "\n";
		}
		
		
		return text;
				
	}
	
	public String recognizeText(String subscriptionKey, byte[] data, String languageCode, boolean detectOrientation) throws VisionServiceException, IOException {
		
		restCall = new WebServiceRequest(subscriptionKey);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("language", languageCode);
		params.put("detectOrientation", detectOrientation);
		String path = uriBase + "/ocr";
		String uri = WebServiceRequest.getUrl(path, params);

		params.put("data", data);
		String json = (String) this.restCall.request(uri, "POST", params, "application/octet-stream", false);

		return json;
	}

	public String  recognizeHandwriting(String subscriptionKey , byte[] data) throws VisionServiceException {
		log.info("recognizeHandwriting");
		restCall = new WebServiceRequest(subscriptionKey);
		WebServiceRequest restCall2 = new WebServiceRequest(subscriptionKey);
		
		Map<String, Object> params = new HashMap<String, Object>();
		String path = uriBase + "/RecognizeText?handwriting=true";
		
		String uri = WebServiceRequest.getUrl(path, params);
		params.put("data", data);
		String operationUrl = (String) this.restCall.request(uri, "POST", params, "application/octet-stream", false);
		String json;
		
		JsonParser parser = new JsonParser();
		int maxAttemps = 20
		int attemps = 0;
		boolean keepGoing = true
		while( keepGoing && attemps < maxAttemps ){
			Thread.sleep(1000);
			json = (String) restCall2.request(operationUrl, "GET", null, null, false);
			JsonObject jsonObject = parser.parse(json).getAsJsonObject();
			
			String status = jsonObject.get("status").toString().replace("\"","");
			
			if (status.equals("NotStarted") || status.equals("Running")){
				keepGoing = true;
			} else {
				keepGoing = false;
			}
			attemps++;
		}

		return json;
	}
	
	private byte[] getImageFromEllipse(String uuid){
		Constraint primaryKey = MSFIMGKey.primaryKey.like("%" + uuid + "%");
		Constraint isDefault = MSFIMGRec.isDefault.equalTo("Y");
		Constraint serviceName = MSFIMGKey.serviceName.equalTo("com.mincom.enterpriseservice.ellipse.employee.EmployeeService");
		
		QueryImpl query = new QueryImpl(MSFIMGRec.class).and(primaryKey).and(serviceName).and(isDefault);
		byte[] imageBytes;
		
		edoi.search(query, { MSFIMGRec msfimgrec ->
			
			MSF053Key msf053key = new MSF053Key();
			msf053key.setMediaUuid(msfimgrec.getPrimaryKey().getMediaId());
			
			try{
				MSF053Rec msf053rec = edoi.findByPrimaryKey(msf053key);
				
				MSF055Key msf055key = new MSF055Key();
				msf055key.setImageClassId("FULL")
				msf055key.setMediaUuid(msfimgrec.getPrimaryKey().getMediaId())
				
				MSF055Rec msf055rec = edoi.findByPrimaryKey(msf055key);
				
				imageBytes = getBlobData(msf055rec.getBlobUuid());
				
			} catch (Exception ex) {
				
			}
		});

	}
	
	private byte[] getBlobData(String id){
		String sql = "SELECT blob_data_054 FROM MSF054 WHERE blob_uuid = '" + id + "'";
		
		byte[] returnBytes;
		Connection conn = dataSource.getConnection();
		Statement stmt = conn.prepareStatement(sql)
		try{
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			Blob blob = rs.getBlob("blob_data_054");
			
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			
			blob.free();
			
			returnBytes = blobAsBytes
			
			
		} catch (Exception ex){
			
		} finally {
			try{ stmt.close(); } catch (Exception ex){}
			try{ conn.close(); } catch (Exception ex){}
		}
		
		return returnBytes;
		
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

public class WebServiceRequest {
	private static final String headerKey = "ocp-apim-subscription-key";
	private HttpClient client = new DefaultHttpClient();
	private String subscriptionKey;
	private Gson gson = new Gson();

	public WebServiceRequest(String key) {
		this.subscriptionKey = key;
	}

	public Object request(String url, String method, Map<String, Object> data, String contentType, boolean responseInputStream) throws VisionServiceException {
		if (method.matches("GET")) {
			return get(url);
		} else if (method.matches("POST")) {
			return post(url, data, contentType, responseInputStream);
		} else if (method.matches("PUT")) {
			return put(url, data);
		} else if (method.matches("DELETE")) {
			return delete(url);
		} else if (method.matches("PATCH")) {
			return patch(url, data, contentType, false);
		}

		throw new VisionServiceException("Error! Incorrect method provided: " + method);
	}

	private Object get(String url) throws VisionServiceException {
		HttpGet request = new HttpGet(url);
		request.setHeader(headerKey, this.subscriptionKey);

		try {
			
			HttpResponse response = this.client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return readInput(response.getEntity().getContent());
			} else {
				throw new Exception("Error executing GET request! Received error code: " + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new VisionServiceException(e.getMessage());
		}
	}

	private Object post(String url, Map<String, Object> data, String contentType, boolean responseInputStream) throws VisionServiceException {
		return webInvoke("POST", url, data, contentType, responseInputStream);
	}

	private Object patch(String url, Map<String, Object> data, String contentType, boolean responseInputStream) throws VisionServiceException {
		return webInvoke("PATCH", url, data, contentType, responseInputStream);
	}

	private Object webInvoke(String method, String url, Map<String, Object> data, String contentType, boolean responseInputStream) throws VisionServiceException {
		HttpPost request = null;

		if (method.matches("POST")) {
			request = new HttpPost(url);
		} else if (method.matches("PATCH")) {
			//request = new HttpPatch(url);
		}

		boolean isStream = false;

		/*Set header*/
		if (contentType != null && !contentType.isEmpty()) {
			request.setHeader("Content-Type", contentType);
			if (contentType.toLowerCase().contains("octet-stream")) {
				isStream = true;
			}
		} else {
			request.setHeader("Content-Type", "application/json");
		}

		request.setHeader(headerKey, this.subscriptionKey);

		try {
			if (!isStream) {
				String json = this.gson.toJson(data).toString();
				StringEntity entity = new StringEntity(json);
				request.setEntity(entity);
			} else {
				request.setEntity(new ByteArrayEntity((byte[]) data.get("data")));
			}

			HttpResponse response = this.client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				if(!responseInputStream) {
					return readInput(response.getEntity().getContent());
				}else {
					return response.getEntity().getContent();
				}
			}else if(statusCode==202)
			{
				return response.getFirstHeader("Operation-Location").getValue();
			}
			else {
				throw new Exception("Error executing POST request! Received error code: " + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new VisionServiceException(e.getMessage());
		}
	}

	private Object put(String url, Map<String, Object> data) throws VisionServiceException {
		HttpPut request = new HttpPut(url);
		request.setHeader(headerKey, this.subscriptionKey);

		try {
			String json = this.gson.toJson(data).toString();
			StringEntity entity = new StringEntity(json);
			request.setEntity(entity);
			request.setHeader("Content-Type", "application/json");
			HttpResponse response = this.client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200 || statusCode == 201) {
				return readInput(response.getEntity().getContent());
			} else {
				throw new Exception("Error executing PUT request! Received error code: " + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new VisionServiceException(e.getMessage());
		}
	}

	private Object delete(String url) throws VisionServiceException {
		HttpDelete request = new HttpDelete(url);
		request.setHeader(headerKey, this.subscriptionKey);

		try {
			HttpResponse response = this.client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new Exception("Error executing DELETE request! Received error code: " + response.getStatusLine().getStatusCode());
			}

			return readInput(response.getEntity().getContent());
		} catch (Exception e) {
			throw new VisionServiceException(e.getMessage());
		}
	}

	public static String getUrl(String path, Map<String, Object> params) {
		StringBuffer url = new StringBuffer(path);

		boolean start = true;
		for (Map.Entry<String, Object> param : params.entrySet()) {
			if (start) {
				url.append("?");
				start = false;
			} else {
				url.append("&");
			}

			try {
				url.append(param.getKey() + "=" + URLEncoder.encode(param.getValue().toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return url.toString();
	}

	private String readInput(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer json = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			json.append(line);
		}

		return json.toString();
	}
}

public class VisionServiceException extends Exception {
	
		public VisionServiceException(String message) {
			super(message);
		}
	
		public VisionServiceException(Gson errorObject) {
			super(errorObject.toString());
		}
	}