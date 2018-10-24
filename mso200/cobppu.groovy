package mso200

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
import com.mincom.ellipse.edoi.ejb.msf200.MSF200Key
import com.mincom.ellipse.edoi.ejb.msf200.MSF200Rec
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
class ParamsCobppu{
	
}

public class ProcessCobppu{
	
	private version = 1;
	private ParamsCobppu batchParams;
	
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
    public String Version = "Version 20180709 - v1"

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
		
		batchParams = params.fill(new ParamsCobppu())
		
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
		
		Constraint cSupType1 = MSF200Rec.supTypex1.equalTo("PP");
		Constraint cSupType2 = MSF200Rec.supTypex2.equalTo("PP");
		Constraint cSupType3 = MSF200Rec.supTypex3.equalTo("PP");
		Constraint cSupType4 = MSF200Rec.supTypex4.equalTo("PP");
		Constraint cSupType5 = MSF200Rec.supTypex5.equalTo("PP");
		Constraint cSupType6 = MSF200Rec.supTypex6.equalTo("PP");
		
		Constraint cSupStatus = MSF200Rec.supStatus.notEqualTo("7");
		
		QueryImpl query = new QueryImpl(MSF200Rec.class).or(cSupType1.or(cSupType2).or(cSupType3).or(cSupType4).or(cSupType5).or(cSupType6)).and(cSupStatus);
		edoi.search(query,{ MSF200Rec msf200rec ->
			info("SUPPLIER: " + msf200rec.getPrimaryKey().getSupplierNo())
			searchInvoices(msf200rec);
		})
		
	}	
	
	public void searchInvoices(MSF200Rec msf200rec){
		
		Constraint cSupplierNo = MSF260Key.supplierNo.equalTo(msf200rec.getPrimaryKey().getSupplierNo());
		//Constraint cPmtStatus = MSF260Rec.pmtStatus.greaterThanEqualTo("50");
		
		QueryImpl query = new QueryImpl(MSF260Rec.class).and(cSupplierNo)
		//.and(cPmtStatus);
		
		int totalInvoices = edoi.search(query).results.size();
		info("TOT:" + totalInvoices);
		if (totalInvoices > 0){
			info("totalInvoices" + totalInvoices)
			msf200rec.setSupStatus("7");
			edoi.update(msf200rec);
			
			Constraint cSupplierNo203 = MSF203Key.supplierNo.equalTo(msf200rec.getPrimaryKey().getSupplierNo());
			QueryImpl query203 = new QueryImpl(MSF203Rec.class).and(cSupplierNo203)
			
			edoi.search(query203,{ MSF203Rec msf203rec ->
				msf203rec.setSupStatus("7");
				edoi.update(msf203rec);
			})
			
			runMss080("COBEMA",
				"SP" + msf200rec.getPrimaryKey().getSupplierNo().padRight(12, " ") + " ".toString().padRight(10, " ") + " ".toString().padRight(12, " "))
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

ProcessCobppu process = new ProcessCobppu()
process.runBatch(binding)
