package kpi

import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import com.mincom.batch.environment.BatchEnvironment;
import com.mincom.batch.script.Params;
import com.mincom.batch.script.Reports;
import com.mincom.batch.script.RequestInterface;
import com.mincom.batch.script.Restart;
import com.mincom.batch.script.Sort;

import java.awt.Component.BaselineResizeBehavior
import java.beans.beancontext.BeanContextServiceProviderBeanInfo
import java.io.File
import java.io.FileWriter;
import java.io.PrintWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Comparator
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
import com.mincom.ellipse.edoi.ejb.msf510.MSF510Key
import com.mincom.ellipse.edoi.ejb.msf510.MSF510Rec
import com.mincom.ellipse.edoi.ejb.msf550.MSF550Key
import com.mincom.ellipse.edoi.ejb.msf550.MSF550Rec
import com.mincom.ellipse.edoi.ejb.msf600.MSF600Key
import com.mincom.ellipse.edoi.ejb.msf600.MSF600Rec
import com.mincom.ellipse.edoi.ejb.msf690.MSF690Key
import com.mincom.ellipse.edoi.ejb.msf690.MSF690Rec
import com.mincom.ellipse.edoi.ejb.msf700.MSF700Key
import com.mincom.ellipse.edoi.ejb.msf700.MSF700Rec
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Key
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Rec
import com.mincom.ellipse.edoi.ejb.audit_data.AUDIT_DATAKey
import com.mincom.ellipse.edoi.ejb.audit_data.AUDIT_DATARec
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
import com.mincom.ellipse.types.m0000.instances.EquipNo;
import com.mincom.ellipse.types.m0000.instances.MaintSchTask;
import com.mincom.ellipse.types.m0000.instances.Rec_700Type
import com.mincom.ellipse.types.m8mwp.instances.BooleanFlag
import com.mincom.ellipse.types.m8mwp.instances.MSTForecastDTO
import com.mincom.ellipse.types.m8mwp.instances.MSTiInstances
import com.mincom.ellipse.types.m8mwp.instances.MSTiMWPDTO
import com.mincom.ellipse.types.m8mwp.instances.MSTiMWPServiceResult
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
class ParamsCobkm1{
	
}

class Cobkm1Line{
	String timeStamp = ' '
	String maintSchTask = ' '
	String equipNo = ' '
	String equipDesc = ' '
	String compCode = ' '
	String compModCode = ' '
	String schedDesc = ' '
	String workGroup = ' '
	String schedInd700 = ' '
	String stdJobNo = ' '
	String stdJobDesc = ' '
	String dstrctCode = ' '
	String scheduleDate1 = ' '
	String scheduleDate2 = ' '
	String scheduleDate3 = ' '
	String scheduleDate4 = ' '
	String scheduleDate5 = ' '
	String scheduleDate6 = ' '
	String scheduleDate7 = ' '
	String scheduleDate8 = ' '
	String scheduleDate9 = ' '
	String scheduleDate10 = ' '
	String scheduleDate11 = ' '
	String scheduleDate12 = ' '
	String scheduleDate13 = ' '
	String scheduleDate14 = ' '
	String scheduleDate15 = ' '
	String scheduleDate16 = ' '
	String scheduleDate17 = ' '
	String scheduleDate18 = ' '
	String scheduleDate19 = ' '
	String scheduleDate20 = ' '
}

class COBKM1Comparator implements Comparator<Cobkm1Line> {
	
	@Override
	public int compare(Cobkm1Line o1, Cobkm1Line o2) {
		String line1 = o1.getTimeStamp()
		String line2 = o2.getTimeStamp()
			
		return line1.compareTo(line2);
	}
}

public class ProcessCobkm1{
	
	private version = 1;
	private ParamsCobkm1 batchParams;
	
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
	public String Version = "v1"
	
	public String FILE_OUTPUT_PATH = "";
	public String FILE_OUTPUT = "";
	
	public TextReportImpl reportWriter;
	public FileWriter fileWriter;
	public PrintWriter printWriter;
	public static String SEPARATOR = "|";
	ArrayList<Cobkm1Line> lines = new ArrayList<Cobkm1Line>();

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
		sql = new Sql(dataSource)
		batchParams = params.fill(new ParamsCobkm1())		
		Constraint cUuid = MSF080Rec.uuid.equalTo(request.getUUID());
		QueryImpl qMSF080 = new QueryImpl(MSF080Rec.class).and(cUuid);
		MSF080Rec msf080rec;
		
		edoi.search(qMSF080,{ MSF080Rec msf080recU ->
			msf080rec = msf080recU;
		});
	
		FILE_OUTPUT_PATH = env.getWorkDir().getCanonicalPath() + "/KPI_MST/";
		FILE_OUTPUT = FILE_OUTPUT + "PROYECCION_" + commarea.TodaysDate + commarea.Time + ".TXT";
	
		File dirPath = new File(FILE_OUTPUT_PATH);
		if(dirPath.exists()){
		   info(FILE_OUTPUT_PATH + " already created...");
		}else{
		   dirPath.mkdirs();
		}
		
		File file = new File(FILE_OUTPUT_PATH + FILE_OUTPUT);
		try {
			if (file.exists())
				file.delete();
			
			if (file.createNewFile()){
				info("file created succesfully");
			}
			else{
				info("could not create the file");
			}
			fileWriter = new FileWriter(file,false);
		   
			OutputStream outputStream = new FileOutputStream(file);
			Writer writer = new OutputStreamWriter(outputStream,"8859_1");
		   
			printWriter = new PrintWriter(writer);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		report.outputHandler.addFile(file);

			
	}
	
	private void processRequest(){
		info("processRequest");
		
		Constraint cRec700Type = MSF700Key.rec_700Type.equalTo("ES");
		Constraint cSchedInd700 = MSF700Rec.schedInd_700.notEqualTo("9");
		Constraint cEquipNO = MSF700Key.equipNo.equalTo("140CV007");
		
		
		QueryImpl query = new QueryImpl(MSF700Rec.class).and(cRec700Type).
			and(cSchedInd700)
			//.orderBy(MSF700Rec.msf700Key)
			//.and(cEquipNO)
			
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date = new Date();
		
		edoi.search(query, restart.each(1000, { MSF700Rec msf700rec ->
			Cobkm1Line line = new Cobkm1Line();
			
			line.timeStamp = dateFormat.format(date);
				
			line.maintSchTask = msf700rec.getPrimaryKey().getMaintSchTask();
			line.equipNo = msf700rec.getPrimaryKey().getEquipNo();
			line.compCode = msf700rec.getPrimaryKey().getCompCode() + " - " + 
				getMSF010Rec("CO",msf700rec.getPrimaryKey().getCompCode());
			line.compModCode = msf700rec.getPrimaryKey().getCompModCode() + " - " + 
				getMSF010Rec("MO",msf700rec.getPrimaryKey().getCompModCode());
			line.schedDesc = msf700rec.getSchedDesc_1()  + msf700rec.getSchedDesc_2()
			line.workGroup = msf700rec.getWorkGroup()
			line.schedInd700= msf700rec.getSchedInd_700()
			line.stdJobNo = msf700rec.getStdJobNo()
			line.dstrctCode = msf700rec.getDstrctCode()
			
			line = searchPendingValues(line);
			lines.add(line);
			line = searchForecast(line);
		}));
		info("TOTAL LINES: " + lines.size());
		
		Collections.sort(lines, new COBKM1Comparator())
		
	}
	

	private Cobkm1Line searchPendingValues(Cobkm1Line line){
		//EQUIP_DESC	MSF600
		//STD_JOB_DESC	MSF690
		
		MSF600Key msf600key = new MSF600Key();
		msf600key.setEquipNo(line.equipNo)
		
		try{
			MSF600Rec msf600rec = edoi.findByPrimaryKey(msf600key);
			line.equipDesc = msf600rec.getItemName_1().trim() + msf600rec.getItemName_2().trim();
			
			MSF690Key msf690key = new MSF690Key();
			msf690key.setDstrctCode(line.dstrctCode)
			msf690key.setStdJobNo(line.stdJobNo);
			
			MSF690Rec msf690rec = edoi.findByPrimaryKey(msf690key);
			line.stdJobDesc = msf690rec.getStdJobDesc();
			
			return line
	
		} catch (Exception ex) {
			info(ex.getMessage());
			
			return line;
		}	
	}
	
	private Cobkm1Line searchForecast(Cobkm1Line line){
		info("FORCAST FOR : " + line.maintSchTask + line.equipNo);
		
		try{
			MSTiMWPServiceResult[] reply = service.get('MST').forecast(
				{MSTForecastDTO request ->
					MaintSchTask mst = new MaintSchTask(line.maintSchTask);
					EquipNo eq = new EquipNo(line.equipNo);
					MSTiInstances inst = new MSTiInstances(20)
					BooleanFlag showRel = new BooleanFlag("N");
					BooleanFlag hideSupp = new BooleanFlag("Y");
					Rec_700Type recType = new Rec_700Type("ES");
					
					request.maintSchTask = mst
					request.equipNo = eq
					request.nInstances = inst
					request.showRelated = showRel
					request.hideSuppressed = hideSupp
					request.rec700Type = recType
				}
			);
			
			if (reply != null ){
				info("SIZE: " + reply.size());
				for (int i = 0; i < reply.size(); i++){
					MSTiMWPDTO item = (MSTiMWPDTO) reply[i].getServiceDto();
					//info(item.toString())
					
					switch (i){
						case 0: line.scheduleDate1 = item.getPlannedStartDate().getValue(); break;
						case 1: line.scheduleDate2 = item.getPlannedStartDate().getValue(); break;
						case 2: line.scheduleDate3 = item.getPlannedStartDate().getValue(); break;
						case 3: line.scheduleDate4 = item.getPlannedStartDate().getValue(); break;
						case 4: line.scheduleDate5 = item.getPlannedStartDate().getValue(); break;
						case 5: line.scheduleDate6 = item.getPlannedStartDate().getValue(); break;
						case 6: line.scheduleDate7 = item.getPlannedStartDate().getValue(); break;
						case 7: line.scheduleDate8 = item.getPlannedStartDate().getValue(); break;
						case 8: line.scheduleDate9 = item.getPlannedStartDate().getValue(); break;
						case 9: line.scheduleDate10 = item.getPlannedStartDate().getValue(); break;
						case 10: line.scheduleDate11 = item.getPlannedStartDate().getValue(); break;
						case 11: line.scheduleDate12 = item.getPlannedStartDate().getValue(); break;
						case 12: line.scheduleDate13 = item.getPlannedStartDate().getValue(); break;
						case 13: line.scheduleDate14 = item.getPlannedStartDate().getValue(); break;
						case 14: line.scheduleDate15 = item.getPlannedStartDate().getValue(); break;
						case 15: line.scheduleDate16 = item.getPlannedStartDate().getValue(); break;
						case 16: line.scheduleDate17 = item.getPlannedStartDate().getValue(); break;
						case 17: line.scheduleDate18 = item.getPlannedStartDate().getValue(); break;
						case 18: line.scheduleDate19 = item.getPlannedStartDate().getValue(); break;
						case 19: line.scheduleDate20 = item.getPlannedStartDate().getValue(); break;
					}
				}
			}
		} catch (Exception ex) {
			info(ex.getMessage());
		}
		return line;
	}
		
	private String getMSF010Rec(String tableType, String tableCode){
		MSF010Key msf010Key = new MSF010Key();
		MSF010Rec msf010rec = null;
		
		msf010Key.setTableType(tableType);
		msf010Key.setTableCode(tableCode);
		try{
			msf010rec = edoi.findByPrimaryKey(msf010Key);
			return msf010rec.getTableDesc();
		} catch (Exception ex){
			return " ";
		}
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
			return null;;
		}
	}
	
	private String getEmployeeName(String employeeId){
		MSF810Key msf810Key = new MSF810Key();
		MSF810Rec msf810rec = null;
		
		msf810Key.setEmployeeId(employeeId);
		try{
			msf810rec = edoi.findByPrimaryKey(msf810Key);
			return msf810rec.getSurname() + " " + msf810rec.getFirstName() + " " +msf810rec.getSecondName();
		} catch (Exception ex){
			return " ";
		}
	}
			
	private void printBatchReport(){
		info("printBatchReport");
		
		lines.each{Cobkm1Line line ->
			
			if (line != null){
				printWriter.write(
				
					line.timeStamp + SEPARATOR +
					line.maintSchTask + SEPARATOR +
					line.equipNo + SEPARATOR +
					line.equipDesc + SEPARATOR +
					line.compCode + SEPARATOR +
					line.compModCode + SEPARATOR +
					line.schedDesc + SEPARATOR +
					line.workGroup + SEPARATOR +
					line.schedInd700 + SEPARATOR +
					line.stdJobNo + SEPARATOR +
					line.stdJobDesc + SEPARATOR +
					line.dstrctCode + SEPARATOR +
					line.scheduleDate1 + SEPARATOR +
					line.scheduleDate2 + SEPARATOR +
					line.scheduleDate3 + SEPARATOR +
					line.scheduleDate4 + SEPARATOR +
					line.scheduleDate5 + SEPARATOR +
					line.scheduleDate6 + SEPARATOR +
					line.scheduleDate7 + SEPARATOR +
					line.scheduleDate8 + SEPARATOR +
					line.scheduleDate9 + SEPARATOR +
					line.scheduleDate10 + SEPARATOR +
					line.scheduleDate11 + SEPARATOR +
					line.scheduleDate12 + SEPARATOR +
					line.scheduleDate13 + SEPARATOR +
					line.scheduleDate14 + SEPARATOR +
					line.scheduleDate15 + SEPARATOR +
					line.scheduleDate16 + SEPARATOR +
					line.scheduleDate17 + SEPARATOR +
					line.scheduleDate18 + SEPARATOR +
					line.scheduleDate19 + SEPARATOR +
					line.scheduleDate20 + SEPARATOR +
					"\n"
				)
			}
		}
		
		printWriter.close();
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

ProcessCobkm1 process = new ProcessCobkm1()
process.runBatch(binding)
