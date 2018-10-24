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
import java.lang.reflect.Field
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
import com.mincom.ellipse.edoi.ejb.msf620.MSF620Key
import com.mincom.ellipse.edoi.ejb.msf620.MSF620Rec
import com.mincom.ellipse.edoi.ejb.msf623.MSF623Key
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Key
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Rec
import com.mincom.ellipse.edoi.ejb.outage.OUTAGEKey
import com.mincom.ellipse.edoi.ejb.outage.OUTAGERec
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
class ParamsCobkot{
	
}

class CobkotLine{
	String timeStamp = ''
	String action = ''
	String dstrctCode = ''
	String l1 = ''
	String l2 = ''
	String l3 = ''
	String l4 = ''
	String raisedDate = ''
	String planStrDate = ''
	String schedDate = ''
	String planFinDate = ''
	String closedDt = ''
	String completedCode = ''
	String ccDesc = ''
	String workOrder = ''
	String woDesc = ''
	String completedBy = ''
	String completedByDesc = ''
	String originatorId = ''
	String originatorIdDesc = ''
	String workGroup = ''
	String equipNo = ''
	String criticidad = ''
	String stdJobNo = ''
	String maintSchTask = ''
	String compCode = ''
	String compCodeDesc = ''
	String compModCode = ''
	String compModCodeDesc = ''
	String noOfTasks = ''
	String requestId = ''
	String woType = ''
	String woTypeDesc = ''
	String sdDate = ''
	String shutdownNo = ''
	String description = ''
	String table = '';
	String workOrderItm = '';
	
	String markedColumn = "";
}

class AUDIT_DATAOTComparator implements Comparator<CobkotLine> {
	
	@Override
	public int compare(CobkotLine o1, CobkotLine o2) {
		String line1 = o1.getTimeStamp();
		String line2 = o2.getTimeStamp();
			
		return line1.compareTo(line2);
	}
}

public class ProcessCobkot{
	
	private version = 1;
	private ParamsCobkot batchParams;
	
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
	ArrayList<CobkotLine> lines = new ArrayList<CobkotLine>();
	ArrayList<CobkotLine> allLines = new ArrayList<CobkotLine>();

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
		batchParams = params.fill(new ParamsCobkot())		
		Constraint cUuid = MSF080Rec.uuid.equalTo(request.getUUID());
		QueryImpl qMSF080 = new QueryImpl(MSF080Rec.class).and(cUuid);
		MSF080Rec msf080rec;
		
		edoi.search(qMSF080,{ MSF080Rec msf080recU ->
			msf080rec = msf080recU;
		});
	
		FILE_OUTPUT_PATH = env.getWorkDir().getCanonicalPath() + "/KPI_OTS/";
		FILE_OUTPUT = FILE_OUTPUT + "OTA_" + commarea.TodaysDate + commarea.Time + ".TXT";
	
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
		String timeStamp = getMSF010Rec('$620',"LAST");
				
		ArrayList<String> listColumns620 = new ArrayList<String>();
		listColumns620.add('WORK_ORDER');
		listColumns620.add('RAISED_DATE');
		listColumns620.add('PLAN_STR_DATE');
		listColumns620.add('PLAN_FIN_DATE');
		listColumns620.add('CLOSED_DT');
		listColumns620.add('COMPLETED_CODE');
		listColumns620.add('WORK_GROUP');
		listColumns620.add('EQUIP_NO');
		listColumns620.add('EQUIP_CLASSIFX6');
		listColumns620.add('STD_JOB_NO');
		listColumns620.add('MAINT_SCH_TASK');
		listColumns620.add('COMP_CODE');
		listColumns620.add('COMP_MOD_CODE');
		listColumns620.add('REQUEST_ID');
		listColumns620.add('WO_TYPE');
		listColumns620.add('SHUTDOWN_NO');
		ArrayList<String> listColumns623 = new ArrayList<String>();
		listColumns623.add("WO_TASK_NO")
		listColumns623.add("PLAN_START_DATE");
		
		Constraint cTableName623 = AUDIT_DATARec.tableName.equalTo("MSF623");
		Constraint cColumnName623 = AUDIT_DATARec.columnName.in(listColumns623);
		
		Constraint cColumnName620 = AUDIT_DATARec.columnName.in(listColumns620);
		Constraint cTableName620 = AUDIT_DATARec.tableName.equalTo("MSF620");
		
		Constraint cCreationTimeStamp = AUDIT_DATARec.creationTimestamp.greaterThan(timeStamp);
		
		QueryImpl query = new QueryImpl(AUDIT_DATARec.class).and(cTableName620).and(cColumnName620)
			.and(cCreationTimeStamp)
			.orderBy(AUDIT_DATARec.audit_dataKey)
		
		////////QUERY MSF620/////
		edoi.search(query, restart.each(1000, { AUDIT_DATARec auditrec ->
			
			//COLOCO LOS DATOS DE LA LINEA
			CobkotLine auditLine = new CobkotLine();
			auditLine.setTimeStamp(auditrec.getCreationTimestamp());
			auditLine.action = auditrec.getAction();
			String key = auditrec.getRecordKey().padRight(15);
			auditLine.setDstrctCode(key.substring(0,4));
			auditLine.setWorkOrder(key.substring(4,12));
			auditLine.setWorkOrderItm(key.substring(12,15));
			auditLine.setTable(auditrec.getTableName());
			
			auditLine = fillValues(auditLine , auditrec);
			auditLine = searchPendingValues(auditLine);
			lines.add(auditLine);
		}));
	
		info("TOTAL SELECCIONADOS: 620 " + lines.size())
		info("TOTAL SELECCIONADOS: 620 " )
		////////QUERY MSF623/////
		query = new QueryImpl(AUDIT_DATARec.class).and(cTableName623).and(cColumnName623)
			.and(cCreationTimeStamp)
			.orderBy(AUDIT_DATARec.audit_dataKey)
		
		edoi.search(query, restart.each(1000, { AUDIT_DATARec auditrec ->
		
			String key = auditrec.getRecordKey().padRight(15);
			
			CobkotLine auditLine = new CobkotLine();
			auditLine.setTimeStamp(auditrec.getCreationTimestamp());
			auditLine.action = auditrec.getAction();
			auditLine.setDstrctCode(key.substring(0,4));
			auditLine.setWorkOrder(key.substring(4,12));
			auditLine.setWorkOrderItm(key.substring(12,15));
			auditLine.setTable(auditrec.getTableName());
			
			auditLine = fillValues(auditLine , auditrec);
			auditLine = searchPendingValues(auditLine);
			lines.add(auditLine);
			
		}));
		
		info("TOTAL SELECCIONADOS: 623 " + lines.size())
				
		
		Collections.sort(lines, new AUDIT_DATAOTComparator())
		
		String previousKey = null;
		/*lines.each { CobkotLine auditLine1 ->
			info("AK" + auditLine1.getTimeStamp() + " PK:" + previousKey);
			if (!previousKey.equals(auditLine1.getTimeStamp()) && previousKey != null){				
				allLines.add(auditLine1);
			}
				
			previousKey = auditLine1.getTimeStamp();
		}*/
		
		CobkotLine auxLine = new CobkotLine();
		
		for (int i = 0; i < lines.size() - 1; i++ ){
			CobkotLine auditLineAct = lines.get(i);
			CobkotLine auditLineNxt = lines.get(i + 1);
			
			passMarkedValue(auditLineAct, auxLine);
			
			if (!auditLineAct.getTimeStamp().equals(auditLineNxt.getTimeStamp())){
				passAllMarkedValues(auditLineAct, auxLine);
				allLines.add(auditLineAct);
				auxLine = new Cobkm2Line();
			}
		}

		info("TOTAL AGRUPADO: " + allLines.size())
		lines = allLines;
		
		if (lines.size() > 0){
			updateControl(lines.get(lines.size() - 1));
		}
		
	}
	
	public void passAllMarkedValues(CobkotLine line, CobkotLine aux){
		Class cls = line.getClass()
		
		for (String keys : aux.getProperties().keySet()){
			String value = aux.getProperties().get(keys)
			
			if (value.trim().length() > 0 ){
				try{
					Field field = cls.getDeclaredField(keys);
					field.setAccessible(true);
					field.set(line, value);
				} catch (Exception ex ){ }
			}
		}
	}
	
	public void passMarkedValue(CobkotLine line, CobkotLine aux){
		info ("MC : " + line.markedColumn)
		String property = line.markedColumn;
		property = toTitleCase(property.replaceAll("_"," ")).replaceAll(" ","");
		info ("MC : " + property)
		property = property.substring(0,1).toLowerCase() + property.substring(1, property.length());
		
		String value = line.getProperties().get(property);
		Class cls = line.getClass();
		Field field = cls.getDeclaredField(property);
		field.setAccessible(true);
		field.set(aux, value);
		
	}
	
	public static String toTitleCase(String givenString) {
		givenString = givenString.toLowerCase()
		String[] arr = givenString.split(" ");
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < arr.length; i++) {
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
		}
		return sb.toString().trim();
	}

	
	private void updateControl(CobkotLine auditrec){
		MSF010Rec msf010rec = getMSF010Record('$620', "LAST");
		
		if (msf010rec != null){
			msf010rec.setTableDesc(auditrec.getTimeStamp())
			msf010rec.setAssocRec(commarea.TodaysDate + commarea.Time)
			edoi.update(msf010rec)
		} else {
			msf010rec = new MSF010Rec();
			MSF010Key msf010key = new MSF010Key();
			msf010key.setTableType('$620');
			msf010key.setTableCode('LAST');
			msf010rec.setPrimaryKey(msf010key)
			msf010rec.setTableDesc(auditrec.getTimeStamp())
			msf010rec.setAssocRec(commarea.TodaysDate + commarea.Time)
			msf010rec.setActiveFlag("Y");
			
			edoi.create(msf010rec);
		}
		
	}
	
	private CobkotLine fillValues(CobkotLine line, AUDIT_DATARec auditrec){
		
		line.markedColumn = auditrec.getColumnName()
		
		if (auditrec.getColumnName().trim().equals("DSTRCT_CODE")){
			line.setDstrctCode(auditrec.getAfter())
						
		} 
		
		if (auditrec.getColumnName().trim().equals("RAISED_DATE")){
			line.setRaisedDate(auditrec.getAfter())
			
		} 
		
		if (auditrec.getColumnName().trim().equals("PLAN_STR_DATE")){
			line.setPlanStrDate(auditrec.getAfter())
			
		} 
		
		if(auditrec.getColumnName().trim().equals("PLAN_FIN_DATE")){
			line.setPlanFinDate(auditrec.getAfter())
			
		} 
		
		if (auditrec.getColumnName().trim().equals("CLOSED_DT")){
			line.setClosedDt(auditrec.getAfter());
			
		} 
		
		if (auditrec.getColumnName().trim().equals("COMPLETED_CODE")){
			line.setCompletedCode(auditrec.getAfter());
			line.setCcDesc(getMSF010Rec( "SC", auditrec.getAfter()));
			
		} 
		
		if (auditrec.getColumnName().trim().equals("WO_DESC")){
			line.setWoDesc(auditrec.getAfter() );
			
		} 
		
		if (auditrec.getColumnName().trim().equals("COMPLETED_BY")){
			line.setCompletedBy(auditrec.getAfter() );
			line.setCompletedByDesc(getEmployeeName(auditrec.getAfter()));
						
		} 
		
		if (auditrec.getColumnName().trim().equals("ORIGINATOR_ID")){
			line.setOriginatorId(auditrec.getAfter() );
			line.setOriginatorIdDesc(getEmployeeName(auditrec.getAfter()));
						
		} 
		
		if (auditrec.getColumnName().trim().equals("WORK_GROUP")){
			line.setWorkGroup(auditrec.getAfter() );
						
		} 
		
		if (auditrec.getColumnName().trim().equals("EQUIP_NO")){
			line.setEquipNo(auditrec.getAfter() );
						
		} 
		
		if (auditrec.getColumnName().trim().equals("STD_JOB_NO")){
			line.setStdJobNo(auditrec.getAfter() );
						
		} 
		
		if (auditrec.getColumnName().trim().equals("MAINT_SCH_TASK")){
			line.setMaintSchTask(auditrec.getAfter() );
			
		} 
		
		if (auditrec.getColumnName().trim().equals("COMP_CODE")){
			line.setCompCode(auditrec.getAfter() );
			line.setCompCodeDesc(getMSF010Rec("CO" ,auditrec.getAfter()) );
			
		} 
		
		if (auditrec.getColumnName().trim().equals("COMP_MOD_CODE")){
			line.setCompModCode(auditrec.getAfter() );
			line.setCompCodeDesc(getMSF010Rec("MO" ,auditrec.getAfter()) );
			
		} 
		
		if (auditrec.getColumnName().trim().equals("MAINT_SCH_TASK")){
			line.setMaintSchTask(auditrec.getAfter() );		
				
		} 
		
		if (auditrec.getColumnName().trim().equals("NO_OF_TASKS")){
			line.setNoOfTasks(auditrec.getAfter() );
			
		} 
		
		if (auditrec.getColumnName().trim().equals("REQUEST_ID")){
			line.setRequestId(auditrec.getAfter() );
			
		} 
		
		if (auditrec.getColumnName().trim().equals("WO_TYPE")){
			line.setWoType(auditrec.getAfter() );
			line.setWoTypeDesc(getMSF010Rec("WO",auditrec.getAfter() ));
			
		} 
		
		if (auditrec.getColumnName().trim().equals("SHUTDOWN_NO")){
			line.setShutdownNo(auditrec.getAfter() );
			
		}

		return line;
	}
	
	private CobkotLine searchPendingValues(CobkotLine line){
		MSF620Key msf620key = new MSF620Key();
		msf620key.setDstrctCode(line.getDstrctCode())
		msf620key.setWorkOrder(line.getWorkOrder())
		
		try{
			MSF620Rec msf620rec = edoi.findByPrimaryKey(msf620key);
						
			///////////////MSF620/////////////
			if (line.getRaisedDate().trim().equals("")){
				line.setRaisedDate(msf620rec.getRaisedDate())
			} 
			
			if (line.getPlanStrDate().trim().equals("")){
				line.setPlanStrDate(msf620rec.getPlanStrDate())
				
			} 
			
			if (line.getPlanFinDate().trim().equals("")){
				line.setPlanFinDate(msf620rec.getPlanFinDate())
				
			} 
			
			if (line.getClosedDt().trim().equals("")){
				line.setClosedDt(msf620rec.getClosedDt());
				
			} 
			
			if (line.getCompletedCode().trim().equals("")){
				line.setCompletedCode(msf620rec.getCompletedCode());
				line.setCcDesc(getMSF010Rec( "SC", msf620rec.getCompletedCode()));
				
			} 
			
			if (line.getWoDesc().trim().equals("")){
				line.setWoDesc(msf620rec.getWoDesc() );
				
			} 
			
			if (line.getCompletedBy().trim().equals("")){
				line.setCompletedBy(msf620rec.getCompletedBy() );
				line.setCompletedByDesc(getEmployeeName(msf620rec.getCompletedBy()));
							
			} 
			
			if (line.getOriginatorId().trim().equals("")){
				line.setOriginatorId(msf620rec.getOriginatorId() );
				line.setOriginatorIdDesc(getEmployeeName(msf620rec.getOriginatorId()));
							
			} 
			
			if (line.getWorkGroup().trim().equals("")){
				line.setWorkGroup(msf620rec.getWorkGroup() );
							
			} 
			
			if (line.getEquipNo().trim().equals("")){
				line.setEquipNo(msf620rec.getEquipNo() );
							
			} 
			
			if (line.getStdJobNo().trim().equals("")){
				line.setStdJobNo(msf620rec.getStdJobNo() );
							
			} 
			
			if (line.getMaintSchTask().trim().equals("")){
				line.setMaintSchTask(msf620rec.getMaintSchTask() );
				
			} 
			
			if (line.getCompCode().trim().equals("")){
				line.setCompCode(msf620rec.getCompCode() );
				line.setCompCodeDesc(getMSF010Rec("CO" ,msf620rec.getCompCode()) );
				
			} 
			
			if (line.getCompModCode().trim().equals("")){
				line.setCompModCode(msf620rec.getCompModCode() );
				line.setCompModCodeDesc(getMSF010Rec("MO" ,msf620rec.getCompModCode()) );
				
			}
			
			if (line.getMaintSchTask().trim().equals("")){
				line.setMaintSchTask(msf620rec.getMaintSchTask());
					
			} 
			
			if (line.getNoOfTasks().trim().equals("")){
				line.setNoOfTasks(msf620rec.getNoOfTasks() );
				
			} 
			
			if (line.getRequestId().trim().equals("")){
				line.setRequestId(msf620rec.getRequestId() );
				
			} 
			
			if (line.getWoType().trim().equals("")){
				line.setWoType(msf620rec.getWoType() );
				line.setWoTypeDesc(getMSF010Rec("WO",msf620rec.getWoType() ));
				
			} 
			
			if (line.getShutdownNo().trim().equals("")){
				line.setShutdownNo(msf620rec.getShutdownNo() );
				
			}
					
			Constraint cRefrence = OUTAGERec.reference.equalTo(msf620rec.getShutdownNo());
			QueryImpl query = new QueryImpl(OUTAGERec.class).and(cRefrence);
			
			edoi.search(query,{OUTAGERec outagerec -> 
				line.setSdDate(outagerec.getActualStart())
				line.setDescription(outagerec.getDescription())
			});
					
			/////////////////SQL///////////////
			if (line != null){
				if (line.getWorkOrder().trim().size() > 0){
					line = getMaxTaskNo(line);
				}
				
				if (line.getEquipNo().trim().size() > 0){
					line = getLevelEquip(line);
				}
			}
			
			try{
				MSF600Key msf600key = new MSF600Key();
				msf600key.setEquipNo(line.getEquipNo());
				MSF600Rec msf600rec = edoi.findByPrimaryKey(msf600key);
				line.criticidad = getMSF010Rec("E5", msf600rec.getEquipClassifx6());
			} catch (Exception ex){ }
			
	
		} catch (Exception ex) {
		}
		
		return line;
		
	}
	
	private CobkotLine getMaxTaskNo(CobkotLine line){
		
		String query = 
			" SELECT MAX(PLAN_STR_DATE) MAX_PLAN_STR_DATE" + 
			" FROM MSF623 WHERE DSTRCT_CODE = '${line.getDstrctCode()}' "+  
			" AND WORK_ORDER = '${line.getWorkOrder()}' ";
		
		ArrayList<GroovyRowResult> list = sql.rows(query);
		
		if (list.size() > 0){
			line.setSchedDate(list.get(0).getAt("MAX_PLAN_STR_DATE"))
		}
		
		return line;
	} 

	private CobkotLine getLevelEquip(CobkotLine line){
		ArrayList<String> equips = new ArrayList<String>();
		String parentEquip
		boolean isLastEquip = false;
		parentEquip = line.getEquipNo(); 
		equips.add(parentEquip)
		
		while (!isLastEquip){
			parentEquip = upperEquip(parentEquip);
			
			if (parentEquip.trim().equals("")){
				isLastEquip = true;
				break;
			}
			equips.add(parentEquip)
		}
		
		line.setL1(equips.get(equips.size() - 1));
		line.setL2(equips.get(equips.size() - 2));
		line.setL3(equips.get(equips.size() - 3));
		line.setL4(equips.get(equips.size() - 4));
		
		return line;
	}
	
	private String upperEquip(String equipNo){
		//info("upperEquip"  + equipNo);
		MSF600Key msf600key = new MSF600Key();
		msf600key.setEquipNo(equipNo.trim());
		
		if (equipNo.trim().length() > 0 ){
			try {
				MSF600Rec msf600rec = edoi.findByPrimaryKey(msf600key);
				
				return msf600rec.getParentEquip();
			} catch (Exception ex) {
				return " ";
			}
		} else {
			return " ";
		}
	}
		
	private String getMSF010Rec(String tableType, String tableCode){
		MSF010Key msf010Key = new MSF010Key();
		MSF010Rec msf010rec = null;
		
		msf010Key.setTableType(tableType.trim().padRight(4));
		msf010Key.setTableCode(tableCode.trim().padRight(18));
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
		
		msf010Key.setTableType(tableType.trim().padRight(4));
		msf010Key.setTableCode(tableCode.trim().padRight(18));
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
		
		lines.each{CobkotLine line ->
			
			if (line != null){
				if (line.timeStamp == null) line.timeStamp = "";
				if (line.action == null) line.action = "";
				if (line.dstrctCode == null) line.dstrctCode= "";
				if (line.l1 == null) line.l1= "";
				if (line.l2 == null) line.l2= "";
				if (line.l3 == null) line.l3= "";
				if (line.l4 == null) line.l4= "";
				if (line.raisedDate == null) line.raisedDate= "";
				if (line.planStrDate == null) line.planStrDate= "";
				if (line.schedDate == null) line.schedDate= "";
				if (line.planFinDate == null) line.planFinDate= "";
				if (line.closedDt == null) line.closedDt= "";
				if (line.completedCode == null) line.completedCode= "";
				if (line.ccDesc == null) line.ccDesc= "";
				if (line.workOrder == null) line.workOrder= "";
				if (line.woDesc == null) line.woDesc= "";
				if (line.completedBy == null) line.completedBy= "";
				if (line.originatorId == null) line.originatorId= "";
				if (line.workGroup == null) line.workGroup= "";
				if (line.equipNo == null) line.equipNo= "";
				if (line.criticidad == null) line.criticidad= "";
				if (line.stdJobNo == null) line.stdJobNo= "";
				if (line.maintSchTask == null) line.maintSchTask= "";
				if (line.compCode == null) line.compCode= "";
				if (line.compModCode == null) line.compModCode= "";
				if (line.noOfTasks == null) line.noOfTasks= "";
				if (line.requestId == null) line.requestId= "";
				if (line.woType == null) line.woType= "";
				if (line.sdDate == null) line.sdDate= "";
				if (line.shutdownNo == null) line.shutdownNo= "";
				if (line.description== null) line.description= "";
				
				printWriter.write(
				
					line.timeStamp.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.action.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.dstrctCode.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.l1.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.l2.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.l3.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.l4.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.raisedDate.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.planStrDate.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.schedDate.replaceAll("\\r\\n|\\r|\\n", " ")  + SEPARATOR + 
					line.planFinDate.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.closedDt.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.completedCode.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.ccDesc.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.workOrder.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.woDesc.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.completedBy.replaceAll("\\r\\n|\\r|\\n", " ") + " - " + line.completedByDesc.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.originatorId.replaceAll("\\r\\n|\\r|\\n", " ") + " - " + line.originatorIdDesc.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.workGroup.replaceAll("\\r\\n|\\r|\\n", " ")  + SEPARATOR + 
					line.equipNo.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.criticidad.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.stdJobNo.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.maintSchTask.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.compCode.replaceAll("\\r\\n|\\r|\\n", " ") + " - " + line.getCompCodeDesc().replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.compModCode.replaceAll("\\r\\n|\\r|\\n", " ") + " - " + line.getCompModCodeDesc().replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.noOfTasks.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.requestId.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.woType.replaceAll("\\r\\n|\\r|\\n", " ") + " - " + line.woTypeDesc.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR + 
					line.sdDate.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.shutdownNo.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.description.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
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

ProcessCobkot process = new ProcessCobkot()
process.runBatch(binding)
