package mse384;

import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import com.mincom.batch.environment.BatchEnvironment;
import com.mincom.batch.script.Params;
import com.mincom.batch.script.Reports;
import com.mincom.batch.script.RequestInterface;
import com.mincom.batch.script.Restart;
import com.mincom.batch.script.Sort;

import java.io.File
import java.io.FileWriter;
import java.io.PrintWriter
import java.util.List;
import javax.sql.DataSource
import org.slf4j.LoggerFactory;

import com.mincom.ellipse.client.connection.Connection
import com.mincom.ellipse.client.connection.ConnectionHolder;

import com.mincom.ellipse.edoi.ejb.msf1hd.MSF1HDKey
import com.mincom.ellipse.edoi.ejb.msf1hd.MSF1HDRec
import com.mincom.ellipse.edoi.ejb.msf1hb.MSF1HBKey
import com.mincom.ellipse.edoi.ejb.msf1hb.MSF1HBRec
import com.mincom.ellipse.edoi.ejb.msf1cs.MSF1CSRec
import com.mincom.ellipse.edoi.ejb.msf1cs.MSF1CSKey
import com.mincom.ellipse.edoi.ejb.msf180.MSF180Key
import com.mincom.ellipse.edoi.ejb.msf180.MSF180Rec
import com.mincom.ellipse.edoi.ejb.msf170.MSF170Key
import com.mincom.ellipse.edoi.ejb.msf170.MSF170Rec
import com.mincom.ellipse.edoi.ejb.msf175.MSF175Key
import com.mincom.ellipse.edoi.ejb.msf175.MSF175Rec
import com.mincom.ellipse.edoi.ejb.msf260.MSF260Key
import com.mincom.ellipse.edoi.ejb.msf260.MSF260Rec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Key
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Rec
import com.mincom.ellipse.edoi.ejb.msf013.MSF013Key
import com.mincom.ellipse.edoi.ejb.msf013.MSF013Rec
import com.mincom.ellipse.edoi.ejb.msf063.MSF063Rec
import com.mincom.ellipse.edoi.ejb.msf063.MSF063Key
import com.mincom.ellipse.edoi.ejb.msf064.MSF064Rec
import com.mincom.ellipse.edoi.ejb.msf064.MSF064Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Rec
import com.mincom.ellipse.edoi.ejb.msf080.MSF080Rec
import com.mincom.ellipse.edoi.ejb.msf203.MSF203Rec
import com.mincom.ellipse.edoi.ejb.msf203.MSF203Key
import com.mincom.ellipse.edoi.ejb.msf20a.MSF20AKey
import com.mincom.ellipse.ejp.interaction.GenericMSOInteraction;
import com.mincom.ellipse.ejra.mso.GenericMsoRecord;
import com.mincom.ellipse.ejra.mso.MsoErrorMessage;
import com.mincom.ellipse.ejra.mso.MsoField
import com.mincom.ellipse.eroi.linkage.mss001.MSS001LINK;
import com.mincom.ellipse.eroi.linkage.mss080.MSS080LINK
import com.mincom.ellipse.eroi.linkage.mssdat.MSSDATLINK
import com.mincom.ellipse.eroi.linkage.mssdtm.MSSDTMLINK
import com.mincom.ellipse.eroi.linkage.mssfxc.MSSFXCLINK
import com.mincom.ellipse.script.util.BatchWrapper;
import com.mincom.ellipse.script.util.CommAreaScriptWrapper;
import com.mincom.ellipse.script.util.EDOIWrapper;
import com.mincom.ellipse.script.util.EROIWrapper;
import com.mincom.ellipse.script.util.ServiceWrapper;
import com.mincom.enterpriseservice.ellipse.ConnectionId;
import com.mincom.enterpriseservice.ellipse.EllipseScreenService
import com.mincom.enterpriseservice.ellipse.EllipseScreenServiceLocator;
import com.mincom.enterpriseservice.ellipse.EllipseSubroutineService
import com.mincom.enterpriseservice.ellipse.valuations.ValuationsServiceApproveRequestDTO
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import com.mincom.reporting.text.TextReportImpl


/**
 * Modified by DBERMUDEZ on 27/06/2018 Codigo Inicial
 * Ultima version 1.0
 */
class ParamsCobebg {
	
}

public class ProcessCobebg {
	
	private version = 1;
	private ParamsCobebg  batchParams;
	
	public Binding b;
	private EDOIWrapper edoi;
	private EROIWrapper eroi;
	private ServiceWrapper service;
	private BatchWrapper batch;
	private CommAreaScriptWrapper commarea;
	private BatchEnvironment env
	private Reports report;
	private Sort sort;
	private Params params;
	private RequestInterface request;
	private Restart restart;
	private Sql sql;
	private String mssdate
	private DataSource dataSource;
			
	public String mssdtmDate = "";
	public String mssdtmTime = ""
    public String Version = "Version 20180627 - v1"

	//Screen Variables
	//A000
	public void runBatch(Binding b){
		
		init(b);
		try {
            info(Version)
		    //B000
		    initialize()
		    //C000
		    processRequest();
		    //D000
		    printBatchReport();
		}
		finally {
				
		}
	}
	
	private void initialize(){
		boolean returnValue;	
		sql = new Sql(dataSource)
		
		batchParams = params.fill(new ParamsCobebg ())
		
		MSSDTMLINK mssdtmlink = eroi.execute("MSSDTM", { MSSDTMLINK mssdtmlink ->
			mssdtmlink.setDateFormat(commarea.DateFormat);
			mssdtmlink.setTestInd(commarea.TestInd);
		});
		
		mssdtmDate = mssdtmlink.getDate().substring(0,4) + mssdtmlink.getDate().substring(4,6) + mssdtmlink.getDate().substring(6,8)
		mssdtmTime = mssdtmlink.getTimeHh() + mssdtmlink.getTimeMm() + mssdtmlink.getTimeSs();
		
		Constraint cUuid = MSF080Rec.uuid.equalTo(request.getUUID());
		QueryImpl qMSF080 = new QueryImpl(MSF080Rec.class).and(cUuid);
		MSF080Rec msf080rec;
		
		edoi.search(qMSF080,{ MSF080Rec msf080recU ->
			msf080rec = msf080recU;
		});
			
	}
	
	private void processRequest(){
		info("processRequest");
		
		//Contratos que no esten cerrados
		ArrayList<String> statusList = new ArrayList<String>();
		statusList.add("05");
		statusList.add("20");
		
		Constraint cStatus = MSF384Rec.status_384.notIn(statusList);
		Constraint cPerfGuarantor = MSF384Rec.perfGuarantor.notEqualTo(" ");
		
		QueryImpl query = new QueryImpl(MSF384Rec.class).and(cStatus).and(cPerfGuarantor);
		edoi.search(query,{ MSF384Rec msf384rec ->
			checkSecurities(msf384rec);
		});
	}	
	
	public void checkSecurities(MSF384Rec msf384rec){
		info("checkSecurities");
		
		BigDecimal totalSecurLogAmt = 0;
		BigDecimal securLogAmt = 0;
		Constraint cContractNo = MSF38AKey.contractNo.equalTo(msf384rec.getPrimaryKey().getContractNo());
		
		QueryImpl query = new QueryImpl(MSF38ARec.class).and(cContractNo);
		edoi.search(query,{ MSF38ARec msf38arec ->
			//La validación se realiza sobre la sumatoria del campo MSF38A.SECUR_LOG_AMOUNT
			//de todas las boletas de garantía cuyo MSF38A.SECUR_DEP_TYPE = 01
			//y su estado MSF38A.SECUR_DEP_FRM in (I, A, P)
			String currencyGtee = msf38arec.getSecurName().toUpperCase();
			info("checkSecurities : " + currencyGtee);
			
			if (msf38arec.getSecurDepType().equals("01") &&
				(
					msf38arec.getSecurDepFrm().equals("I") ||
					msf38arec.getSecurDepFrm().equals("A") ||
					msf38arec.getSecurDepFrm().equals("P")
				)
			){
				if (!msf384rec.getCurrencyType().trim().equals(currencyGtee)){
					//Enviar correo de notificacion cuando la moneda de la boleta
					//es diferente a la del contrato.
					runMss080("COBEMA",
						"EG" +
						msf384rec.getPrimaryKey().getContractNo().padRight(12, " ") +
						msf38arec.getPrimaryKey().getSecurNo().padRight(20, " ") +
						"CR")
					securLogAmt = convertValue(msf38arec.getSecurLogAmount(), currencyGtee, msf384rec.getCurrencyType())
				} else {
					securLogAmt = msf38arec.getSecurLogAmount()
				}
				
				totalSecurLogAmt += securLogAmt;
			}
			info("checkSecurities F: " + currencyGtee);
		});
			
		info("AAAA");
		
		BigDecimal contractPrice = 0;
		double b = toDouble(msf384rec.getPerfGuarantor().trim());
		contractPrice = BigDecimal.valueOf(b);
		
		info("AAAA" + contractPrice	);
		
		//Se busca la tolerancia seteada
		MSF010Rec msf010TBG = getMSF010Rec("+TBG",commarea.District + "001");
		
		info("AAAA" + contractPrice	);
		
		if (msf010TBG != null){
			//try{
				BigDecimal tolerance = new BigDecimal(msf010TBG.getAssocRec().padRight(12).substring(0, 6).trim());
				
				info("PRICE" + contractPrice)
				info("DIFF" + totalSecurLogAmt + contractPrice)
				
				if (totalSecurLogAmt <= (1 + tolerance / 100) * contractPrice ){
					runMss080("COBEMA",
						"ET" +
						msf384rec.getPrimaryKey().getContractNo().padRight(12, " ") +
						"".padRight(20, " ") +
						"CR")
				}
			//} catch (Exception ex1){
				//info(ex1.getMessage());
				//info(ex1.getLocalizedMessage());
			//}
		}
	}
	
	public MSF010Rec getMSF010Rec (String tableType, String tableCode){
		try{
			MSF010Key msf010key = new MSF010Key();
			msf010key.setTableType(tableType)
			msf010key.setTableCode(tableCode)
			
			MSF010Rec msf010rec = edoi.findByPrimaryKey(msf010key);
			return msf010rec;
		} catch (Exception ex2){
			return null;
		}
	}
	
	public void runMss080(String progName, String requesParams) {
		info("runMss080")

		String Uuid = UUID.randomUUID().toString().substring(0, 32).toUpperCase();

		eroi.execute('MSS080', { MSS080LINK mss080linkrec ->
			mss080linkrec.uuid = Uuid
			mss080linkrec.requestParams = requesParams
			mss080linkrec.deferDate = commarea.TodaysDate
			mss080linkrec.deferTime = commarea.Time
			mss080linkrec.requestDate = commarea.TodaysDate
			mss080linkrec.requestTime = commarea.Time
			mss080linkrec.requestBy = commarea.UserId
			mss080linkrec.userId = commarea.UserId
			mss080linkrec.dstrctCode = commarea.District;
			mss080linkrec.requestDstrct = commarea.District;
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
	
	private MSF010Rec getMSF010Record(String tableType, String tableCode){
		MSF010Key msf010Key = new MSF010Key();
		MSF010Rec msf010rec = null;
		
		msf010Key.setTableType(tableType);
		msf010Key.setTableCode(tableCode);
		try{
			msf010rec = edoi.findByPrimaryKey(msf010Key);
			return msf010rec;
		} catch (Exception ex){
			return null;
		}
	}
	
	private BigDecimal convertValue(BigDecimal value, String fromCurrency, String toCurrency){
		BigDecimal valorLocal;
		BigDecimal valorCurrContrato;
		
		if (commarea.LocalCurrency.trim().equals(fromCurrency.trim())) {
			valorLocal = value;
		} else {
		
			MSSFXCLINK mssfxclink = eroi.execute("MSSFXC", { MSSFXCLINK mssfxclink ->
		
				mssfxclink.setOptionFxc("1")
				mssfxclink.setInputValue(value)
				mssfxclink.setConvTypeSw("F")
				mssfxclink.setRateTypeSw("B")
				mssfxclink.setLocalCurrency(commarea.LocalCurrency);
				mssfxclink.setForeignCurr(fromCurrency);
				mssfxclink.setTranDate(commarea.TodaysDate);
		
			});
		
			valorLocal = mssfxclink.getFlValueConv().setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		
		MSSFXCLINK mssfxclink = eroi.execute("MSSFXC", { MSSFXCLINK mssfxclink ->
		
			mssfxclink.setOptionFxc("1")
			mssfxclink.setInputValue(valorLocal)
			mssfxclink.setConvTypeSw("L")
			mssfxclink.setRateTypeSw("B")
			mssfxclink.setLocalCurrency(commarea.LocalCurrency);
			mssfxclink.setForeignCurr(toCurrency);
			mssfxclink.setTranDate(commarea.TodaysDate);
		
		});
			
		valorCurrContrato = mssfxclink.getFlValueConv().setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return 	valorCurrContrato;
	}
	
	private double toDouble(String value){
		String decimalSeparator = value.substring(value.length()- 3, value.length()- 2);
		
		if (decimalSeparator.equals(",")){
			value = value.replace(".", "").replace(",", ".");
		}
		
		try{
			return Double.valueOf(value.replace(",", ""));
		} catch (Exception ex) {
			return 0;
		}
	}

	private void processBatch(){
		info("processBatch")
	}
			
	private void printBatchReport(){
		info("printBatchReport");
	}
	
	public void init(Binding b) {
		info("init")
		env = b.getVariable("env");
		params = b.getVariable("params");
		request = b.getVariable("request");
		edoi = b.getVariable("edoi");
		eroi = b.getVariable("eroi");
		service = b.getVariable("service");
		batch = b.getVariable("batch");
		commarea = b.getVariable("commarea");
		env = b.getVariable("env");
		report = b.getVariable("report");
		sort = b.getVariable("sort");
		request = b.getVariable("request");
		restart = b.getVariable("restart");
		params = b.getVariable("params");
		dataSource = b.getVariable("dataSource");
		this.b = b;
	}
	
	public void info(String value){
		def logObject = LoggerFactory.getLogger(getClass());
		logObject.info("------------- " + value)
	}

}

ProcessCobebg  process = new ProcessCobebg()
process.runBatch(binding)
