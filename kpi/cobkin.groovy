package kpi

import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import com.mincom.batch.environment.BatchEnvironment;
import com.mincom.batch.script.Params;
import com.mincom.batch.script.Reports;
import com.mincom.batch.script.RequestInterface;
import com.mincom.batch.script.Restart;
import com.mincom.batch.script.Sort;

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
class ParamsCobkin{
	
}

class CobkinLine{
	String timeStamp = '';
	String action = '';
	String eventNumber = '';
	String descEvento = '';
	String statusEvento = '';
	String eventItemNo = '';
	String correctActDesc = '';
	String correctActCode = '';
	String correctActCodeDesc = '';
	String creationDate = '';
	String turnoCreationPrimRptCd = '';
	String turnoCreationPc03 = '';
	String turnoCreationPc04 = '';
	String turnoCreationPc05 = '';
	String origDueDate = '';
	String dueDate = '';
	String completeDate = '';
	String originatorId = '';
	String originadorDesc = '';
	String origPosition = '';
	String origCargo = '';
	String origRefer = '';
	String origLevel1 = '';
	String origLevel2 = '';
	String origLevel3 = '';
	String origLevel4 = '';
	String origLevel1D = '';
	String origLevel2D = '';
	String origLevel3D = '';
	String origLevel4D = '';
	String responsible = '';
	String responsibleDesc = '';
	String respPosition = '';
	String respCargo = '';
	String respRefer = '';
	String respLevel1 = '';
	String respLevel2 = '';
	String respLevel3 = '';
	String respLevel4 = '';
	String respLevel1D = '';
	String respLevel2D = '';
	String respLevel3D = '';
	String respLevel4D = '';
	String caStatus = '';
	String caStatusDesc = '';
	String caPriority = '';
	String caPriorityDesc = '';
	
	String markedColumn = "";
	
}

class CobkinLineComparator implements Comparator<CobkinLine> {
	
	@Override
	public int compare(CobkinLine o1, CobkinLine o2) {
		String line1 = o1.getTimeStamp();
		String line2 = o2.getTimeStamp();
			
		return line1.compareTo(line2);
	}
}

public class ProcessCobkin{
	
	private version = 1;
	private ParamsCobkin batchParams;
	
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
	ArrayList<CobkinLine> lines = new ArrayList<CobkinLine>();
	ArrayList<CobkinLine> allLines = new ArrayList<CobkinLine>();

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
		batchParams = params.fill(new ParamsCobkin())		
		Constraint cUuid = MSF080Rec.uuid.equalTo(request.getUUID());
		QueryImpl qMSF080 = new QueryImpl(MSF080Rec.class).and(cUuid);
		MSF080Rec msf080rec;
		
		edoi.search(qMSF080,{ MSF080Rec msf080recU ->
			msf080rec = msf080recU;
		});
	
		FILE_OUTPUT_PATH = env.getWorkDir().getCanonicalPath() + "/KPI_ACCIONES/";
		FILE_OUTPUT = FILE_OUTPUT + "ACCIONES_" + commarea.TodaysDate + commarea.Time + ".TXT";
	
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
		String timeStamp = getMSF010Rec('$550',"LAST");
				
		ArrayList<String> list = new ArrayList<String>();
		list.add("EVENT_ITEM_NO")
		list.add("CORRECT_ACT_DESC")
		list.add("CORRECT_ACT_CODE")
		list.add("ORIG_DUE_DATE")
		list.add("DUE_DATE")
		list.add("COMPLETE_DATE")
		list.add("ORIGINATOR_ID")
		list.add("RESPONSIBLE")
		list.add("CA_STATUS")
		list.add("CA_PRIORITY");
		
		Constraint cTableName = AUDIT_DATARec.tableName.equalTo("MSF550");
		Constraint cColumnName = AUDIT_DATARec.columnName.in(list);
		Constraint cCreationTimeStamp = AUDIT_DATARec.creationTimestamp.greaterThan(timeStamp);
		
		QueryImpl query = new QueryImpl(AUDIT_DATARec.class).and(cCreationTimeStamp).
			and(cTableName).and(cColumnName).orderBy(AUDIT_DATARec.audit_dataKey)
			
		edoi.search(query, restart.each(1000, { AUDIT_DATARec auditrec ->
			//COLOCO LOS DATOS DE LA LINEA
			CobkinLine auditLine = new CobkinLine();
		
			auditLine.setTimeStamp(auditrec.getCreationTimestamp());
			auditLine.action = auditrec.getAction();
			String key = auditrec.getRecordKey().padRight(14);
			auditLine.setEventNumber(key.substring(0,10));
			auditLine.setEventItemNo(key.substring(10,14));
			auditLine = fillValues(auditLine , auditrec);
			auditLine = searchPendingValues(auditLine);
			lines.add(auditLine);
		}));
		info("TOTAL LINES: " + lines.size());
		
		Collections.sort(lines, new CobkinLineComparator())
		
		String previousKey = null;
		CobkinLine auxLine = new CobkinLine();
		
		for (int i = 0; i < lines.size() - 1; i++ ){
			CobkinLine auditLineAct = lines.get(i);
			CobkinLine auditLineNxt = lines.get(i + 1);
			
			passMarkedValue(auditLineAct, auxLine);
			
			if (!auditLineAct.getTimeStamp().equals(auditLineNxt.getTimeStamp())){
				passAllMarkedValues(auditLineAct, auxLine);
				allLines.add(auditLineAct);
				auxLine = new CobkinLine();
			}
		}
		/*lines.each { AuditLineOT auditLine1 ->
			info("AK" + auditLine1.getTimeStamp() + " PK:" + previousKey);
			if (!previousKey.equals(auditLine1.getTimeStamp()) && previousKey != null){
				allLines.add(auditLine1);
			}
			previousKey = auditLine1.getTimeStamp();
		}*/
		lines = allLines;
		
		info("TOTAL SELECCIONADOS: " + lines.size())
		
		if (lines.size() > 0){
			CobkinLine line = lines.get(lines.size() - 1)
			updateControl(line);
		}
		
	}
	
	public void passAllMarkedValues(CobkinLine line, CobkinLine aux){
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
	
	public void passMarkedValue(CobkinLine line, CobkinLine aux){
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

	
	private void updateControl(CobkinLine auditrec){
		MSF010Rec msf010rec = getMSF010Record('$550', "LAST");
		
		if (msf010rec != null){
			msf010rec.setTableDesc(auditrec.getTimeStamp())
			msf010rec.setAssocRec(commarea.TodaysDate + commarea.Time)
			edoi.update(msf010rec)
		} else {
			MSF010Key msf010key = new MSF010Key();
			msf010key.setTableType('$550');
			msf010key.setTableCode('LAST');
			msf010rec.setPrimaryKey(msf010key)
			msf010rec.setTableDesc(auditrec.getTimeStamp())
			msf010rec.setAssocRec(commarea.TodaysDate + commarea.Time)
			msf010rec.setActiveFlag("Y");
			
			edoi.create(msf010rec);
		}
		
	}
	
	private CobkinLine fillValues(CobkinLine line, AUDIT_DATARec auditrec){
		line.markedColumn = auditrec.getColumnName()
		
		if (auditrec.getColumnName().trim().equals("CORRECT_ACT_DESC")){
			line.setCorrectActDesc(auditrec.getAfter())
			
		} 
		
		if (auditrec.getColumnName().trim().equals("CORRECT_ACT_CODE")){
			line.setCorrectActCode(auditrec.getAfter())
			line.setCorrectActCodeDesc(getMSF010Rec("OHCA", auditrec.getAfter()))
						
		} 
		
		if (auditrec.getColumnName().trim().equals("ORIG_DUE_DATE")){
			line.setOrigDueDate(auditrec.getAfter())
			
		} 
		
		if (auditrec.getColumnName().trim().equals("DUE_DATE")){
			line.setDueDate(auditrec.getAfter())
			
		} 
		
		if (auditrec.getColumnName().trim().equals("COMPLETE_DATE")){
			line.setCompleteDate(auditrec.getAfter())
			
		} 
		
		if (auditrec.getColumnName().trim().equals("ORIGINATOR_ID")){
			line.setOriginatorId(auditrec.getAfter());
			line.setOriginadorDesc(getEmployeeName(auditrec.getAfter()))
			
		} 
		
		if (auditrec.getColumnName().trim().equals("RESPONSIBLE")){
			line.setResponsible(auditrec.getAfter());
			line.setResponsibleDesc(getEmployeeName(auditrec.getAfter()));
			
		} 
		
		if (auditrec.getColumnName().trim().equals("CA_STATUS")){
			line.setCaStatus(auditrec.getAfter() );
			line.setCaStatusDesc(getMSF010Rec("CRST",auditrec.getAfter() ));
			
		} 
		
		if (auditrec.getColumnName().trim().equals("CA_PRIORITY")){
			line.setCaPriority(auditrec.getAfter() );
			line.setCaPriorityDesc(getMSF010Rec("CRPR",auditrec.getAfter() ));
						
		}

		return line;
	}
	
	private CobkinLine searchPendingValues(CobkinLine line){
		MSF550Key msf550key = new MSF550Key();
		msf550key.setEventNumber(line.getEventNumber());
		msf550key.setEventItemNo(line.getEventItemNo());
		
		try{
			MSF550Rec msf550rec = edoi.findByPrimaryKey(msf550key);
						
			///////////////MSF550/////////////
			if (line.getCorrectActDesc().trim().equals("")){
				line.setCorrectActDesc(msf550rec.getCorrectActDesc())
				
			}
			
			if (line.getCorrectActCode().trim().equals("")){
				line.setCorrectActCode(msf550rec.getCorrectActCode())
				line.setCorrectActCodeDesc(getMSF010Rec("OHCA", msf550rec.getCorrectActCode()))
				
			}
			
			if (line.getOrigDueDate().trim().equals("")){
				line.setOrigDueDate(msf550rec.getOrigDueDate())
				
			}
			
			if (line.getDueDate().trim().equals("")){
				line.setDueDate(msf550rec.getDueDate())
				
			}
			
			if (line.getCreationDate().trim().equals("")){
				line.setCreationDate(msf550rec.getCreationDate())
			}
				
			if (line.getCompleteDate().trim().equals("")){
				line.setCompleteDate(msf550rec.getCompleteDate())
				
			} 
			
			if (line.getOriginatorId().trim().equals("")){
				line.setOriginatorId(msf550rec.getOriginatorId())
				
			}
			
			if (line.getResponsible().trim().equals("")){
				line.setResponsible(msf550rec.getResponsible())
				
			}
			
			if (line.getCaStatus().trim().equals("")){
				line.setCaStatus(msf550rec.getCaStatus());
				line.setCaStatusDesc(getMSF010Rec("CRST",msf550rec.getCaStatus()));
				
			} 
			
			if (line.getCaPriority().trim().equals("")){
				line.setCaPriority(msf550rec.getCaPriority());
				line.setCaPriorityDesc(getMSF010Rec("CRPR",msf550rec.getCaPriority()));
			}
			
			line.setOriginadorDesc(getEmployeeName(msf550rec.getOriginatorId()));
			line.setResponsibleDesc(getEmployeeName(msf550rec.getResponsible()))
			///////////////MSF510/////////////
			MSF510Key msf510key = new MSF510Key();
			msf510key.setIncidentNo(line.getEventNumber());
			msf510key.setDstrctCode(commarea.District);
			
			MSF510Rec msf510rec = edoi.findByPrimaryKey(msf510key);
			
			line.setDescEvento(msf510rec.getIncidentDesc())
			line.setStatusEvento(msf510rec.getEvntStatus())
			line.setTurnoCreationPrimRptCd(msf510rec.getPrimRptCd())
			
			info("PRC 1" + msf510rec.getPrimRptCd().substring(12, 16));
			info("PRC 2" + msf510rec.getPrimRptCd().substring(16, 20));
			info("PRC 3" + msf510rec.getPrimRptCd().substring(20, 24));
			
			line.setTurnoCreationPc03(getMSF010Rec("PC03",msf510rec.getPrimRptCd().substring( 8, 12).padRight(18)));
			line.setTurnoCreationPc04(getMSF010Rec("PC04",msf510rec.getPrimRptCd().substring(12, 16).padRight(18)));
			line.setTurnoCreationPc05(getMSF010Rec("PC05",msf510rec.getPrimRptCd().substring(16, 20).padRight(18)));
			
			/////////////////SQL///////////////
			line = getHierarchyData("ORIGINATOR", line);
			line = getHierarchyData("RESPONSIBLE", line);
			
			return line
	
		} catch (Exception ex) {
			info(ex.getMessage());
			
			return line;
		}
		
	}
	
	private CobkinLine getHierarchyData(String employeeType, CobkinLine line){
		String employeeId;
		
		if (employeeType.equals("ORIGINATOR")){
			employeeId = line.getOriginatorId();
		} else {
			employeeId = line.getResponsible();
		}
		
		String query = 
			"SELECT m878.EMPLOYEE_ID, "+
				"Jqia.L1,"+
				"Jqia.l2,"+
				"Jqia.l3,"+
				"Jqia.l4,"+
				"Jqia.l5,"+
				"Jqia.l6,"+
				"Jqia.L1_d,"+
				"Jqia.l2_d,"+
				"Jqia.l3_d,"+
				"Jqia.l4_d,"+
				"Jqia.l5_d,"+
				"Jqia.l6_d,"+
				"m878.INV_STR_DATE,"+
				"m878.POS_STOP_DATE,"+
				"m878.POSITION_ID,"+
				"JQIA.POS_TITLE,"+
				"JQIA.reference reference_desc, "+
				"JQIA.global_profile "+
			"FROM msf878 m878  "+
			"INNER JOIN "+
				"(SELECT employee_id, "+
					"PRIMARY_POS, "+
					"MIN( POS_STOP_DATE) POSTOPDATE, "+
					"MAX(DECODE(POS_STOP_DATE,'00000000','99999999',POS_STOP_DATE)) POS_STOP_DATE "+
				"FROM msf878 "+
				"WHERE PRIMARY_POS='0' AND POSITION_ID <>'TOP20000' "+
				"GROUP BY employee_id, PRIMARY_POS "+
			") m878max ON ( m878.EMPLOYEE_ID = m878max.EMPLOYEE_ID AND m878.PRIMARY_POS = m878max.PRIMARY_POS AND DECODE(M878.POS_STOP_DATE,'00000000','99999999',M878.POS_STOP_DATE)=m878max.POS_STOP_DATE) "+
			"LEFT JOIN "+
			"(SELECT a.position_id, "+
				"a.SUPERIOR_ID, "+
				"LEVEL AS Lvl, "+
				"CONNECT_BY_ISLEAF IsLeaf, "+
				"SYS_CONNECT_BY_PATH(trim(a.POSITION_ID),'/') AS PAT, "+
				"( CASE WHEN LEVEL>=1 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(b.POS_TITLE,'>'),2,40))  END) L1_d, "+
				"( CASE WHEN LEVEL>=2 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(b.POS_TITLE,'>'),43,40)) END) L2_d, "+
				"( CASE WHEN LEVEL>=3 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(b.POS_TITLE,'>'),84,40)) END) L3_d, "+
				"( CASE WHEN LEVEL>=4 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(b.POS_TITLE,'>'),125,40)) END) L4_d, "+
				"( CASE WHEN LEVEL>=5 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(b.POS_TITLE,'>'),166,40)) END) L5_d, "+
				"( CASE WHEN LEVEL>=5 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(b.POS_TITLE,'>'),207,40))  END) L6_d, "+
				
				"( CASE WHEN LEVEL>=1 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(a.position_id,'>'),2,8))  END) L1, "+
				"( CASE WHEN LEVEL>=2 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(a.position_id,'>'),13,8)) END) L2, "+
				"( CASE WHEN LEVEL>=3 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(a.position_id,'>'),24,8)) END) L3, "+
				"( CASE WHEN LEVEL>=4 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(a.position_id,'>'),35,8)) END) L4, "+
				"( CASE WHEN LEVEL>=5 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(a.position_id,'>'),46,8)) END) L5, "+
				"( CASE WHEN LEVEL>=5 THEN trim(SUBSTR(SYS_CONNECT_BY_PATH(a.position_id,'>'),57,8))  END) L6, "+				
				"B.REFERENCE, " + 
				"B.global_profile, "+
				"B.POS_TITLE "+
			"FROM msf875 a "+
				"LEFT JOIN msf870 b ON (A.POSITION_ID               =b.POSITION_ID) "+
				"START WITH a.position_id      ='CEO00000' CONNECT BY PRIOR a.POSITION_ID=a.SUPERIOR_ID "+
			") Jqia ON (m878.POSITION_ID     =jqia.POSITION_ID) "+
			" WHERE m878.EMPLOYEE_ID  = '${employeeId}'";
		
		ArrayList<GroovyRowResult> list = sql.rows(query);
		
		if (employeeType.equals("ORIGINATOR")){
			
			line.setOrigPosition(list.get(0).getAt("POSITION_ID"))
			line.setOrigCargo(list.get(0).getAt("POS_TITLE"))
			line.setOrigRefer(list.get(0).getAt("reference_desc"))
			
			line.setOrigLevel1(list.get(0).getAt("l1"))
			line.setOrigLevel1D(list.get(0).getAt("l1_d"))
			line.setOrigLevel2(list.get(0).getAt("l2"))
			line.setOrigLevel2D(list.get(0).getAt("l2_d"))
			line.setOrigLevel3(list.get(0).getAt("l3"))
			line.setOrigLevel3D(list.get(0).getAt("l3_d"))
			line.setOrigLevel4(list.get(0).getAt("l4"))
			line.setOrigLevel4D(list.get(0).getAt("l4_d"))
		} else {
			line.setRespPosition(list.get(0).getAt("POSITION_ID"))
			line.setRespCargo(list.get(0).getAt("POS_TITLE"))
			line.setRespRefer(list.get(0).getAt("reference_desc"))
			
			line.setRespLevel1(list.get(0).getAt("l1"))
			line.setRespLevel1D(list.get(0).getAt("l1_d"))
			line.setRespLevel2(list.get(0).getAt("l2"))
			line.setRespLevel2D(list.get(0).getAt("l2_d"))
			line.setRespLevel3(list.get(0).getAt("l3"))
			line.setRespLevel3D(list.get(0).getAt("l3_d"))
			line.setRespLevel4(list.get(0).getAt("l4"))
			line.setRespLevel4D(list.get(0).getAt("l4_d"))
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
		
		lines.each{CobkinLine line ->
			
			if (line != null){
				printWriter.write(
				
					line.timeStamp + SEPARATOR + 
					line.action + SEPARATOR + 
					line.eventNumber + SEPARATOR + 
					line.descEvento + SEPARATOR + 
					line.statusEvento + SEPARATOR + 
					line.eventItemNo + SEPARATOR + 
					line.correctActDesc + SEPARATOR + 
					line.correctActCode + " - " + line.correctActCodeDesc + SEPARATOR +
					line.creationDate + SEPARATOR + 
					line.turnoCreationPrimRptCd + SEPARATOR + 
					line.turnoCreationPc03 + SEPARATOR + 
					line.turnoCreationPc04 + SEPARATOR + 
					line.turnoCreationPc05 + SEPARATOR + 
					line.origDueDate + SEPARATOR + 
					line.dueDate + SEPARATOR + 
					line.completeDate + SEPARATOR + 
					line.originatorId + " - " + line.originadorDesc + SEPARATOR + 
					line.origPosition + SEPARATOR + 
					line.origCargo  + SEPARATOR + 
					line.origRefer + SEPARATOR + 
					line.origLevel1 + " - " + line.origLevel1D + SEPARATOR +
					line.origLevel2 + " - " + line.origLevel2D + SEPARATOR +
					line.origLevel3 + " - " + line.origLevel3D + SEPARATOR +
					line.origLevel4 + " - " + line.origLevel4D + SEPARATOR +
					line.responsible + " - " + line.responsibleDesc + SEPARATOR + 
					line.respPosition + SEPARATOR + 
					line.respCargo + SEPARATOR + 
					line.respRefer + SEPARATOR + 
					line.respLevel1 + " - " + line.respLevel1D + SEPARATOR +
					line.respLevel2 + " - " + line.respLevel2D + SEPARATOR +
					line.respLevel3 + " - " + line.respLevel3D + SEPARATOR +
					line.respLevel4 + " - " + line.respLevel4D + SEPARATOR +
					line.caStatus   + " - " + line.caStatusDesc + SEPARATOR +
					line.caPriority + " - " + line.caPriorityDesc + SEPARATOR +
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

ProcessCobkin process = new ProcessCobkin()
process.runBatch(binding)
