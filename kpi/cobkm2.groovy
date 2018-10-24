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
import com.mincom.ellipse.edoi.ejb.msf700.MSF700Key
import com.mincom.ellipse.edoi.ejb.msf700.MSF700Rec
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
import com.mincom.ellipse.types.m0000.instances.EquipNo
import com.mincom.ellipse.types.m0000.instances.MaintSchTask
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
class ParamsCobkm2{
	
}

class Cobkm2Line {
	String timeStamp = ' '
	String action = ' '
	String rec700Type = ' '
	String equipNo = ' '
	String itemName1 = ' '
	String compCode= ' '
	String compCodeDesc= ' '
	String compModCode= ' '
	String compModCodeDesc= ' '
	String maintSchTask = ' '
	String schedDesc1 = ' '
	String workGroup = ' '
	String schedFreq1 = ' '
	String schedFreq2 = ' '
	String schedInd700 = ' '
	String lastSchDate = ' '
	String lastSchSt1 = ' '
	String lastSchSt2 = ' '
	String lastPerfDate = ' '
	String lastPerfSt1 = ' '
	String lastPerfSt2 = ' '
	String nextSchDate = ' '
	String nextSchStat = ' '
	String nextSchValue = ' '
	String statType1= ' '
	String statType1Desc= ' '
	String statType2= ' '
	String statType2Desc= ' '
	String stdJobNo = ' '
	String shutdownEquip = ' '
	String nextSchInd = ' '
	String dstrctCode = ' '
	String shutdownNo = ' '
	
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
	
	String markedColumn = "";
}

class AUDIT_DATAKm2Comparator implements Comparator<Cobkm2Line> {
	
	@Override
	public int compare(Cobkm2Line o1, Cobkm2Line o2) {
		String line1 = o1.getTimeStamp();
		String line2 = o2.getTimeStamp();
			
		return line1.compareTo(line2);
	}
}

public class ProcessCobkm2{
	
	private version = 1;
	private ParamsCobkm2 batchParams;
	
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
	ArrayList<Cobkm2Line> lines = new ArrayList<Cobkm2Line>();
	ArrayList<Cobkm2Line> allLines = new ArrayList<Cobkm2Line>();

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
		batchParams = params.fill(new ParamsCobkm2())		
		Constraint cUuid = MSF080Rec.uuid.equalTo(request.getUUID());
		QueryImpl qMSF080 = new QueryImpl(MSF080Rec.class).and(cUuid);
		MSF080Rec msf080rec;
		
		edoi.search(qMSF080,{ MSF080Rec msf080recU ->
			msf080rec = msf080recU;
		});
	
		FILE_OUTPUT_PATH = env.getWorkDir().getCanonicalPath() + "/KPI_MST/";
		FILE_OUTPUT = FILE_OUTPUT + "MST_" + commarea.TodaysDate + commarea.Time + ".TXT";
	
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
		String timeStamp = getMSF010Rec('$700',"LAST");
				
		ArrayList<String> listColumns700 = new ArrayList<String>();
		
		listColumns700.add('EQUIP_NO');
		listColumns700.add('COMP_CODE');
		listColumns700.add('COMP_MOD_CODE');
		listColumns700.add('MAINT_SCH_TASK');
		
		listColumns700.add('SCHED_DESC_1');
		listColumns700.add('WORK_GROUP');
		listColumns700.add('SCHED_FREQ_1');
		listColumns700.add('SCHED_FREQ_2');
		listColumns700.add('SCHED_IND_700');
		listColumns700.add('LAST_SCH_DATE');
		listColumns700.add('LAST_SCH_ST_1');
		
		listColumns700.add('LAST_SCH_ST_2');
		listColumns700.add('LAST_PERF_DATE');
		listColumns700.add('LAST_PERF_ST_1');
		listColumns700.add('LAST_PERF_ST_2');
		listColumns700.add('NEXT_SCH_DATE');
		listColumns700.add('NEXT_SCH_STAT');
		
		listColumns700.add('NEXT_SCH_VALUE');
		listColumns700.add('STAT_TYPE_1');
		listColumns700.add('STAT_TYPE_2');
		listColumns700.add('STD_JOB_NO');
		listColumns700.add('SHUTDOWN_EQUIP');
		listColumns700.add('NEXT_SCH_IND');
		
		listColumns700.add('DSTRCT_CODE');
		listColumns700.add('SHUTDOWN_NO');
		
		Constraint cColumnName700 = AUDIT_DATARec.columnName.in(listColumns700);
		Constraint cTableName700 = AUDIT_DATARec.tableName.equalTo("MSF700");
		
		Constraint cCreationTimeStamp = AUDIT_DATARec.creationTimestamp.greaterThan(timeStamp);
		
		QueryImpl query = new QueryImpl(AUDIT_DATARec.class).and(cTableName700).and(cColumnName700)
			.and(cCreationTimeStamp)
			.orderBy(AUDIT_DATARec.audit_dataKey)
		
		/////QUERY MSF700/////
		edoi.search(query, restart.each(1000, { AUDIT_DATARec auditrec ->
			
			//COLOCO LOS DATOS DE LA LINEA
			Cobkm2Line auditLine = new Cobkm2Line();
			auditLine.setTimeStamp(auditrec.getCreationTimestamp());
			auditLine.action = auditrec.getAction();
			String key = auditrec.getRecordKey().padRight(24);
			auditLine.setRec700Type(key.substring(0,2));
			auditLine.setEquipNo(key.substring(2,14));
			auditLine.setCompCode(key.substring(14,18));
			auditLine.setCompModCode(key.substring(18,20));
			auditLine.setMaintSchTask(key.substring(20,24));
			
			auditLine = fillValues(auditLine , auditrec);
			auditLine = searchPendingValues(auditLine);
			lines.add(auditLine);
		}));
	
		info("TOTAL SELECCIONADOS: 700 " + lines.size())
					
		Collections.sort(lines, new AUDIT_DATAKm2Comparator())
		
		String previousKey = null;
		Cobkm2Line auxLine = new Cobkm2Line();
		
		for (int i = 0; i < lines.size() - 1; i++ ){
			Cobkm2Line auditLineAct = lines.get(i);
			Cobkm2Line auditLineNxt = lines.get(i + 1);
			
			passMarkedValue(auditLineAct, auxLine);
			
			if (!auditLineAct.getTimeStamp().equals(auditLineNxt.getTimeStamp())){
				passAllMarkedValues(auditLineAct, auxLine);
				allLines.add(auditLineAct);
				auxLine = new Cobkm2Line();
			}
		}
		/*
		lines.each { Cobkm2Line auditLine1 ->
			info("AK" + auditLine1.getTimeStamp() + " PK:" + previousKey);
			if (!previousKey.equals(auditLine1.getTimeStamp()) && previousKey != null){			
				allLines.add(auditLine1);
			} else {
				String property = auditLine1.markedColumn;
				property = toTitleCase(property.replaceAll("_"," ")).replaceAll(" ","");
				property = property.substring(0,1).toLowerCase() + property.substring(1, property.length() - 1);
			}

			previousKey = auditLine1.getTimeStamp();
		}*/
		info("TOTAL AGRUPADO: " + allLines.size())
		lines = allLines;
		
		if (lines.size() > 0){
			updateControl(lines.get(lines.size() - 1));
		}
		
	}
	
	public void passAllMarkedValues(Cobkm2Line line, Cobkm2Line aux){
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
	
	public void passMarkedValue(Cobkm2Line line, Cobkm2Line aux){
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
	
	private void updateControl(Cobkm2Line auditrec){
		MSF010Rec msf010rec = getMSF010Record('$700', "LAST");
		
		if (msf010rec != null){
			msf010rec.setTableDesc(auditrec.getTimeStamp())
			msf010rec.setAssocRec(commarea.TodaysDate + commarea.Time)
			edoi.update(msf010rec)
		} else {
			msf010rec = new MSF010Rec();
			MSF010Key msf010key = new MSF010Key();
			msf010key.setTableType('$700');
			msf010key.setTableCode("LAST");
			msf010rec.setPrimaryKey(msf010key)
			msf010rec.setTableDesc(auditrec.getTimeStamp())
			msf010rec.setAssocRec(commarea.TodaysDate + commarea.Time)
			msf010rec.setActiveFlag("Y");
			
			edoi.create(msf010rec);
		}
		
	}
	
	private Cobkm2Line fillValues(Cobkm2Line line, AUDIT_DATARec auditrec){
		line.markedColumn = auditrec.getColumnName()
		
		if (auditrec.getColumnName().trim().equals("EQUIP_NO")){
			line.setEquipNo(auditrec.getAfter())
		} 
		
		if (auditrec.getColumnName().trim().equals("COMP_CODE")){
			line.setCompCode()
			line.setCompCodeDesc(getMSF010Rec( "CO", auditrec.getAfter()));
		} 
		
		if (auditrec.getColumnName().trim().equals("COMP_MOD_CODE")){
			line.setCompModCode(auditrec.getAfter())
			line.setCompModCodeDesc(getMSF010Rec( "MO", auditrec.getAfter()));
		} 
		
		if(auditrec.getColumnName().trim().equals("MAINT_SCH_TASK")){
			line.setMaintSchTask(auditrec.getAfter())
		} 
		
		if (auditrec.getColumnName().trim().equals("SCHED_DESC_1")){
			line.setSchedDesc1(auditrec.getAfter());
		} 
		
		if (auditrec.getColumnName().trim().equals("WORK_GROUP")){
			line.setWorkGroup(auditrec.getAfter());
		} 
		
		if (auditrec.getColumnName().trim().equals("SCHED_FREQ_1")){
			line.setSchedFreq1(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("SCHED_FREQ_2")){
			line.setSchedFreq2(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("SCHED_IND_700")){
			line.setSchedInd700(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("LAST_SCH_DATE")){
			line.setLastSchDate(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("LAST_SCH_ST_1")){
			line.setLastSchSt1(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("LAST_SCH_ST_2")){
			line.setLastSchSt2(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("LAST_PERF_DATE")){
			line.setLastPerfDate(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("LAST_PERF_ST_1")){
			line.setLastPerfSt1(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("LAST_PERF_ST_2")){
			line.setLastPerfSt2(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("NEXT_SCH_STAT")){
			line.setNextSchStat(auditrec.getAfter() );		
		} 
		
		if (auditrec.getColumnName().trim().equals("NEXT_SCH_VALUE")){
			line.setNextSchValue(auditrec.getAfter() );
		} 
		
		if (auditrec.getColumnName().trim().equals("STAT_TYPE_1")){
			line.setStatType1(auditrec.getAfter() );
			line.setStatType1Desc(getMSF010Rec("SS",auditrec.getAfter() ));			
		} 
		
		if (auditrec.getColumnName().trim().equals("STAT_TYPE_2")){
			line.setStatType2(auditrec.getAfter() );
			line.setStatType2Desc(getMSF010Rec("SS",auditrec.getAfter() ));			
		} 
		
		if (auditrec.getColumnName().trim().equals("STD_JOB_NO")){
			line.setStdJobNo(auditrec.getAfter() );
		}

		if (auditrec.getColumnName().trim().equals("SHUTDOWN_EQUIP")){
			line.setShutdownEquip(auditrec.getAfter() );
		}
		
		if (auditrec.getColumnName().trim().equals("NEXT_SCH_IND")){
			line.setNextSchInd(auditrec.getAfter() );
		}
		
		if (auditrec.getColumnName().trim().equals("DSTRCT_CODE")){
			line.setDstrctCode(auditrec.getAfter() );
			
		}
		
		if (auditrec.getColumnName().trim().equals("SHUTDOWN_NO")){
			line.setShutdownNo(auditrec.getAfter() );
			
		}
		 
		return line;
	}
	
	private Cobkm2Line searchPendingValues(Cobkm2Line line){
		MSF700Key msf700key = new MSF700Key();
		msf700key.setRec_700Type(line.rec700Type);
		msf700key.setEquipNo(line.equipNo);
		msf700key.setCompCode(line.compCode);
		msf700key.setCompModCode(line.compModCode);
		msf700key.setMaintSchTask(line.maintSchTask);
		
		try{
			MSF700Rec msf700rec = edoi.findByPrimaryKey(msf700key);
			
			if (line.getEquipNo().trim().equals("")){
				line.setEquipNo(msf700rec.getPrimaryKey().getEquipNo())
			}
			
			if (line.getCompCode().trim().equals("")){
				line.setCompCode(msf700rec.getPrimaryKey().getCompCode())
				line.setCompCodeDesc(getMSF010Rec( "CO", msf700rec.getPrimaryKey().getCompCode()));
			}
			
			if (line.getCompModCode().trim().equals("")){
				line.setCompModCode(msf700rec.getPrimaryKey().getCompModCode())
				line.setCompModCodeDesc(getMSF010Rec( "MO", msf700rec.getPrimaryKey().getCompModCode()));
			}
			
			if(line.getMaintSchTask().trim().equals("")){
				line.setMaintSchTask(msf700rec.getPrimaryKey().getMaintSchTask())
			}
			
			if (line.getSchedDesc1().trim().equals("")){
				line.setSchedDesc1(msf700rec.getSchedDesc_1());
			}
			
			if (line.getWorkGroup().trim().equals("")){
				line.setWorkGroup(msf700rec.getWorkGroup());
			}
			
			if (line.getSchedFreq1().trim().equals("")){
				line.setSchedFreq1(msf700rec.getSchedFreq_1().toString() );
			}
			
			if (line.getSchedFreq2().trim().equals("")){
				line.setSchedFreq2(msf700rec.getSchedFreq_2().toString() );
			}
			
			if (line.getSchedInd700().trim().equals("")){
				line.setSchedInd700(msf700rec.getSchedInd_700().toString() );
			}
			
			if (line.getLastSchDate().trim().equals("")){
				line.setLastSchDate(msf700rec.getLastSchDate() );
			}
			
			if (line.getLastSchSt1().trim().equals("")){
				line.setLastSchSt1(msf700rec.getLastSchSt_1().toString() );
			}
			
			if (line.getLastSchSt2().trim().equals("")){
				line.setLastSchSt2(msf700rec.getLastPerfSt_2().toString() );
			}
			
			if (line.getLastPerfDate().trim().equals("")){
				line.setLastPerfDate(msf700rec.getLastPerfDate());
			}
			
			if (line.getLastPerfSt1().trim().equals("")){
				line.setLastPerfSt1(msf700rec.getLastPerfSt_1().toString() );
			}
			
			if (line.getLastPerfSt2().trim().equals("")){
				line.setLastPerfSt2(msf700rec.getLastPerfSt_2().toString() );
			}
			
			if (line.getNextSchStat().trim().equals("")){
				line.setNextSchStat(msf700rec.getNextSchStat() );
			}
			
			if (line.getNextSchValue().trim().equals("")){
				line.setNextSchValue(msf700rec.getNextSchValue().toString() );
			}
			
			if (line.getStatType1().trim().equals("")){
				line.setStatType1(msf700rec.getStatType_1() );
				line.setStatType1Desc(getMSF010Rec("SS",msf700rec.getStatType_1()));
			}
			
			if (line.getStatType1().trim().equals("")){
				line.setStatType1(msf700rec.getStatType_2() );
				line.setStatType1Desc(getMSF010Rec("SS",msf700rec.getStatType_2() ));
			}
			
			if (line.getStdJobNo().trim().equals("")){
				line.setStdJobNo(msf700rec.getStdJobNo() );
			}
	
			if (line.getShutdownEquip().trim().equals("")){
				line.setShutdownEquip(msf700rec.getShutdownNo() );
			}
			
			if (line.getNextSchInd().trim().equals("")){
				line.setNextSchInd(msf700rec.getNextSchInd() );
			}
			
			if (line.getDstrctCode().trim().equals("")){
				line.setDstrctCode(msf700rec.getDstrctCode() );
			}
			
			if (line.getShutdownNo().trim().equals("")){
				line.setShutdownNo(msf700rec.getShutdownNo() );
			}
	
			MSF600Key msf600key = new MSF600Key();
			msf600key.setEquipNo(line.getEquipNo())
			
			MSF600Rec msf600rec = edoi.findByPrimaryKey(msf600key);
			line.setItemName1(msf600rec.getItemName_1())
			
		} catch (Exception ex) {
			info(ex.getMessage())
		}
		
		//searchForecast(line)
		
		return line;
		
	}
	
	private Cobkm2Line searchForecast(Cobkm2Line line){
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
		
		msf010Key.setTableType(tableType.trim().padRight(4));
		msf010Key.setTableCode(tableCode.trim().padRight(18));
		try{
			msf010rec = edoi.findByPrimaryKey(msf010Key);
			return msf010rec.getTableDesc();
		} catch (Exception ex){
			info(ex.getLocalizedMessage());
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
			return null;
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
		
		lines.each{Cobkm2Line line ->
			
			if (line != null){
				if (line.timeStamp == null ) line.timeStamp = ""
				if (line.action == null ) line.action = ""
				if (line.equipNo == null ) line.equipNo = ""
				if (line.itemName1 == null ) line.itemName1 = ""
				if (line.compCode == null ) line.compCode = ""
				if (line.compModCode  == null ) line.compModCode = ""
				if (line.maintSchTask == null ) line.maintSchTask = ""
				if (line.schedDesc1 == null ) line.schedDesc1 = ""
				if (line.workGroup == null ) line.workGroup = ""
				if (line.schedFreq1 == null ) line.schedFreq1 = ""
				if (line.schedFreq2 == null ) line.schedFreq2 = ""
				if (line.schedInd700 == null ) line.schedInd700 = ""
				if (line.lastSchDate == null ) line.lastSchDate = ""
				if (line.lastSchSt1 == null ) line.lastSchSt1 = ""
				if (line.lastSchSt2 == null ) line.lastSchSt2 = ""
				if (line.lastPerfDate == null ) line.lastPerfDate = ""
				if (line.lastPerfSt1 == null ) line.lastPerfSt1 = ""
				if (line.lastPerfSt2 == null ) line.lastPerfSt2 = ""
				if (line.nextSchDate == null ) line.nextSchDate = ""
				if (line.nextSchStat == null ) line.nextSchStat = ""
				if (line.nextSchValue == null ) line.nextSchValue = ""
				if (line.statType1 == null ) line.statType1 = ""
				if (line.statType2 == null ) line.statType2 = ""
				if (line.stdJobNo == null ) line.stdJobNo = ""
				if (line.shutdownEquip == null ) line.shutdownEquip = ""
				if (line.nextSchInd == null ) line.nextSchInd = ""
				if (line.dstrctCode == null ) line.dstrctCode = ""
				if (line.shutdownNo == null ) line.shutdownNo = ""

				
				printWriter.write(
				
					line.timeStamp.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.action.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.equipNo.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.itemName1.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.compCode.replaceAll("\\r\\n|\\r|\\n", " ") + " - "+ line.compCodeDesc.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.compModCode.replaceAll("\\r\\n|\\r|\\n", " ") + " - " + line.compModCodeDesc.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.maintSchTask.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.schedDesc1.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.workGroup.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.schedFreq1.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.schedFreq2.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.schedInd700.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.lastSchDate.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.lastSchSt1.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.lastSchSt2.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.lastPerfDate.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.lastPerfSt1.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.lastPerfSt2.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.nextSchDate.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.nextSchStat.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.nextSchValue.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.statType1.replaceAll("\\r\\n|\\r|\\n", " ") + " - " + line.statType1Desc.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.statType2.replaceAll("\\r\\n|\\r|\\n", " ") + " - " + line.statType2Desc.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.stdJobNo.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.shutdownEquip.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.nextSchInd.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.dstrctCode.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					line.shutdownNo.replaceAll("\\r\\n|\\r|\\n", " ") + SEPARATOR +
					/*line.scheduleDate1 + SEPARATOR +
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
					line.scheduleDate20 + SEPARATOR +*/
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

ProcessCobkm2 process = new ProcessCobkm2()
process.runBatch(binding)
