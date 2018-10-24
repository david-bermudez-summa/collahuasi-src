package mail

import com.mincom.batch.environment.BatchEnvironment
import com.mincom.batch.script.*
import com.mincom.ellipse.common.unix.UnixTools
import com.mincom.ellipse.edoi.common.exception.EDOIObjectNotFoundException
import com.mincom.ellipse.edoi.ejb.msf100.MSF100Key
import com.mincom.ellipse.edoi.ejb.msf100.MSF100Rec
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Key
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Rec
import com.mincom.ellipse.edoi.ejb.msf020.MSF020Key
import com.mincom.ellipse.edoi.ejb.msf020.MSF020Rec
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Rec
import com.mincom.ellipse.edoi.ejb.msf096.MSF096Key
import com.mincom.ellipse.edoi.ejb.msf096.MSF096Rec
import com.mincom.ellipse.edoi.ejb.msf096.MSF096_STD_VOLATKey
import com.mincom.ellipse.edoi.ejb.msf096.MSF096_STD_VOLATRec
import com.mincom.ellipse.edoi.ejb.msf200.MSF200Key
import com.mincom.ellipse.edoi.ejb.msf200.MSF200Rec
import com.mincom.ellipse.edoi.ejb.msf203.MSF203Key
import com.mincom.ellipse.edoi.ejb.msf203.MSF203Rec
import com.mincom.ellipse.edoi.ejb.msf20a.MSF20AKey
import com.mincom.ellipse.edoi.ejb.msf20a.MSF20ARec
import com.mincom.ellipse.edoi.ejb.msf220.MSF220Key
import com.mincom.ellipse.edoi.ejb.msf220.MSF220Rec
import com.mincom.ellipse.edoi.ejb.msf221.MSF221Key
import com.mincom.ellipse.edoi.ejb.msf221.MSF221Rec
import com.mincom.ellipse.edoi.ejb.msf230.MSF230Key
import com.mincom.ellipse.edoi.ejb.msf230.MSF230Rec
import com.mincom.ellipse.edoi.ejb.msf231.MSF231Key
import com.mincom.ellipse.edoi.ejb.msf231.MSF231Rec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.edoi.ejb.msf510.MSF510Key
import com.mincom.ellipse.edoi.ejb.msf510.MSF510Rec
import com.mincom.ellipse.edoi.ejb.msf511.MSF511Key
import com.mincom.ellipse.edoi.ejb.msf511.MSF511Rec
import com.mincom.ellipse.edoi.ejb.msf541.MSF541Key
import com.mincom.ellipse.edoi.ejb.msf541.MSF541Rec
import com.mincom.ellipse.edoi.ejb.msf543.MSF543Key
import com.mincom.ellipse.edoi.ejb.msf543.MSF543Rec
import com.mincom.ellipse.edoi.ejb.msf550.MSF550Key
import com.mincom.ellipse.edoi.ejb.msf550.MSF550Rec
import com.mincom.ellipse.edoi.ejb.msf620.MSF620Key
import com.mincom.ellipse.edoi.ejb.msf620.MSF620Rec
import com.mincom.ellipse.edoi.ejb.msf623.MSF623Key
import com.mincom.ellipse.edoi.ejb.msf623.MSF623Rec
import com.mincom.ellipse.edoi.ejb.msf625.MSF625Key
import com.mincom.ellipse.edoi.ejb.msf625.MSF625Rec
import com.mincom.ellipse.edoi.ejb.msf655.MSF655Key
import com.mincom.ellipse.edoi.ejb.msf655.MSF655Rec
import com.mincom.ellipse.edoi.ejb.msf660.MSF660Key
import com.mincom.ellipse.edoi.ejb.msf660.MSF660Rec
import com.mincom.ellipse.edoi.ejb.msf720.MSF720Key
import com.mincom.ellipse.edoi.ejb.msf720.MSF720Rec
import com.mincom.ellipse.edoi.ejb.msf723.MSF723Key
import com.mincom.ellipse.edoi.ejb.msf723.MSF723Rec
import com.mincom.ellipse.edoi.ejb.msf808.MSF808Key
import com.mincom.ellipse.edoi.ejb.msf808.MSF808Rec
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Key
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Rec
import com.mincom.ellipse.edoi.ejb.msf811.MSF811Key
import com.mincom.ellipse.edoi.ejb.msf811.MSF811Rec
import com.mincom.ellipse.edoi.ejb.msfx51.MSFX51Key
import com.mincom.ellipse.edoi.ejb.msfx51.MSFX51Rec
import com.mincom.ellipse.eroi.linkage.mssfxc.MSSFXCLINK
import com.mincom.ellipse.script.util.*
import com.mincom.ellipse.types.m0000.instances.ApDstrctCode
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory

import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.persistence.criteria.CriteriaBuilder.Trimspec
import javax.sql.DataSource

import java.awt.Component.BaselineResizeBehavior
import java.text.DecimalFormat
import java.text.Normalizer

/**
 * Modified by DBERMUDEZ on 27/09/2018 Correciones en el format de la SP - Suspension Proveedores (v29)
 * Modified by DBERMUDEZ on 27/09/2018 Correciones en el format de la BG (v28)
 * Modified by DBERMUDEZ on 20/09/2018 Correciones en el format de la PO (v27)
 * Modified by DBERMUDEZ on 10/09/2018 Correciones en el format de la PO (v26)
 * Modified by DBERMUDEZ on 09/09/2018 Se elimina la instruccion de reemplazar "&nbsp" por " " ya que modificaba otras instrucciones HTML(v25)
 * Modified by DBERMUDEZ on 24/08/2018 Se modifica formato para Proveedor pago unico (v24)
 * Modified by DBERMUDEZ on 17/08/2018 Se modifica formato para boletas de garantia (v23)
 * Modified by DBERMUDEZ on 09/08/2018 Se coloca formato justificado para el caso PO (v22)
 * Modified by DBERMUDEZ on 08/08/2018 por instrucciones de Jafeth Morales se elimina el charset Cp1252 (v21)
 * Modified by DBERMUDEZ on 31/07/2018 se corrige el query del MSF012 cuando es tipo 'PO' (v20)
 * Modified by DBERMUDEZ on 22/07/2018 se creo un nuevo metodo para cuando es tipo 'PO' (v19)
 * Modified by JAFETH on 21/07/2018 se le agrego las seccion del VAT aprobado (v18)
 * Modified by DBERMUDEZ on 21/07/2018 Se le agrego lineas para el envio de copias cuando es tipo 'PO' (v17)
 * Modified by JAFETH on 21/7/2018 Se modifico el metodo sendmail para enviar copias y se le agrego las seccion del VAT (v16)
 * Modified by DBERMUDEZ on 12/07/2018 Se le agrego lineas para el envio de correo cuando es tipo 'ET' y 'EG' (v15)
 * Modified by DBERMUDEZ on 27/06/2018 Se le agrego lineas para el envio de correo cuando es tipo 'PO' (v14)
 * Modified by DBERMUDEZ on 25/06/2018 Se le agrego lineas para el envio de correo cuando es tipo 'SP' (v13)
 * Modified by JAFETH on 03/6/2018 Se le agrego el lider responsable al correo de incidente.
 * Modified by JAFETH on 03/6/2018 Se le agrego la descripcion extendida a los correos de incidentes y OT .
 * Modified by JAFETH on 02/17/2018 Se modifico la visualizacion de los lideres de GRT.
 * Modified by JAFETH on 01/23/2018 Se soluciono el problema objeto nulo
 * Modified by JAFETH on 01/13/2018 Se soluciono el problema de la descripcion del Event Sub Type
 * Modified by JAFETH on 12/23/2017 Se agrego el envio de correo para los GRT cerrados
 * Modified by JAFETH on 12/18/2017 Se agrego el envio de correo para los GRT
 * Modified by JAFETH on 8/22/2017 Se le coloco el host de correo parametrizable leyendo desde una tabla msf010
 * Modified by JAFETH on 8/8/2017 Se incluye el equipo en el correo de cierre de WR.
 * Modified by JAFETH on 6/28/2017 se le agrego un try en la parte de la busqueda MSF541 para mirar que problema puede estar presentando.
 * Modified by JAFETH on 4/17/2017 modifico el host de correos y se agrego la funcionalidad de incidente y acciones preventivas(Crear y cerrar)
 * Created by JAFETH on 12/5/2016.
 */
class ParamsCobema {

	String Option
	String WR_WO
	String Employee_id
	String DS_OT
}

public class ProcessCobema {
	public ParamsCobema batchParams;
	public EDOIWrapper edoi;
	public EROIWrapper eroi;
	public ServiceWrapper service;
	public BatchWrapper batch;
	public CommAreaScriptWrapper commarea;
	public BatchEnvironment env;
	public UnixTools tools;
	public Reports report;
	public Sort sort;
	public Params params;
	public RequestInterface request;
	public Restart restart;
	public Binding b;
	public String ellipseVersion;
	public DataSource dataSource;
	public String Dir
	public String mailMessage
	public String pathName
	public String emailCC = "";
	public String copyCC = "N"
	public String subject
	public Sql sql;

	boolean enviarCorreo = false;
	String MailTo;
	String emailCC;
	boolean IsCopy = false;
	boolean isHtml = false;

	String Version = "v29"

	public void info(String value) {
		def logObject = LoggerFactory.getLogger(getClass());
		logObject.info("------------- " + value)
	}


	public void init(Binding b) {

		info("Init");
		this.b = b;

		edoi = b.getVariable("edoi");
		eroi = b.getVariable("eroi");
		service = b.getVariable("service");
		batch = b.getVariable("batch");
		commarea = b.getVariable("commarea");
		env = b.getVariable("env");
		tools = b.getVariable("tools");
		report = b.getVariable("report");
		sort = b.getVariable("sort");
		request = b.getVariable("request");
		restart = b.getVariable("restart");
		dataSource = b.getVariable("dataSource");
		params = b.getVariable("params");

	}


	void runBatch(Binding b) {
		info("Runbatch");
		info(Version)
		init(b);

		try {
			batchParams = params.fill(new ParamsCobema());
			info("Option: " + batchParams.getOption());
			info("WR_WO: " + batchParams.getWR_WO());
			info("Employee_id: " + batchParams.getEmployee_id());
			info("DS_OT: " + batchParams.getDS_OT());

			enviarCorreo();
		} catch (Exception e) {

			info("Process Batch failed. Exception occurs: ${e.getMessage()}")
			int abort = Integer.parseInt(" ");

		} finally {

			info("Finish ...");
		}

	}

	public void enviarCorreo() {
		boolean enviarCorreo = false;
		switch (batchParams.getOption().trim()) {
		case 'AV':
			info("Entro a enviar case VV...");
			MSF810Rec msf810Rec
			String Name = "";
			String Name2 = "";
			try {
				msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: batchParams.getEmployee_id()))
				MailTo = msf810Rec.getEmailAddress();
				Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
				enviarCorreo = true;

				if (enviarCorreo) {
					MSF510Rec msf510Rec = edoi.findByPrimaryKey(new MSF510Key(dstrctCode: commarea.District.toString(), incidentNo: batchParams.getWR_WO().trim()))

					def employee_id = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
					def dstrct_code = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
					def personInd = MSF511Key.personInd.equalTo("E")
					def PrimaryRol = MSF511Rec.primaryRole.between("00", "01");

					def query = new QueryImpl(MSF511Rec.class).and(employee_id).and(dstrct_code).and(personInd).and(PrimaryRol)

					MSF511Rec msf511Rec = edoi.firstRow(query);

					//String sqls = "select EMPLOYEE_ID from ellipse.msf511 where INCIDENT_NO='0000018016' and DSTRCT_CODE='COLL' and PERSON_IND='E' and PRIMARY_ROLE BETWEEN('00') and( '01')";
					MSF071Rec msf071rec = getMSF071Rec('003', 'VAT', msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), '001');
					String employeeEjecutor
					if (msf071rec != null) {
						employeeEjecutor = msf071rec.getRefCode()
					} else {
						employeeEjecutor = msf511Rec.getPrimaryKey().getEmployeeId()
					}
					info("employeeEjecutor: " + employeeEjecutor)

					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: employeeEjecutor))
					Name2 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
					String IncidentType = ""
					String EventSubType = ""
					String LocatType = ""
					String description = ""

					try {
						MSF096_STD_VOLATRec msf096_std_volatRec = edoi.findByPrimaryKey(
								new MSF096_STD_VOLATKey(stdKey: commarea.District.toString() + batchParams.getWR_WO().trim(), stdLineNo: "0000", stdTextCode: "IT"))
						description = msf096_std_volatRec.getStdVolat_1() + msf096_std_volatRec.getStdVolat_2() + msf096_std_volatRec.getStdVolat_3() +
								msf096_std_volatRec.getStdVolat_4() + msf096_std_volatRec.getStdVolat_5()
					} catch (Exception e) {
						description = "N/A."
					}
					sql = new Sql(dataSource);
					description = getStdText(commarea.District.toString() + msf510Rec.getPrimaryKey().getIncidentNo(), "IN")


					try {

						MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHIT', tableCode: msf510Rec.getIncidentType()))
						IncidentType = msf010Rec.getTableDesc().toUpperCase()

					} catch (Exception e1) {
						IncidentType = "N/A."
					}
					try {
						if (msf510Rec.getIncidentType().trim().equalsIgnoreCase("GRT")) {
							MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'GRTD', tableCode: msf510Rec.getEventSubType()))
							EventSubType = msf010R.getTableDesc().toUpperCase()
						} else {
							MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: msf510Rec.getIncidentType().padRight(4, " ").substring(0, 4), tableCode: msf510Rec.getEventSubType()))
							EventSubType = msf010R.getTableDesc().toUpperCase()
						}

					} catch (Exception e1) {
						EventSubType = "N/A."
					}

					try {

						switch (msf510Rec.getLocatType().trim()) {
							case 'P':
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'PHYL', tableCode: msf510Rec.getLocatCode()))
								LocatType = msf010R.getTableDesc().toUpperCase()
								break
							default:
								LocatType = "N/A."
								break
						}

					} catch (Exception e1) {
						LocatType = "N/A."
					}

					MSF808Rec msf808Rec = edoi.findByPrimaryKey(new MSF808Key(primRptCodes: msf510Rec.getPrimRptCd()))

					String fecha = (99999999 - Integer.parseInt(msf510Rec.getRevOccDate()));
					String hora = msf510Rec.getTimeOccurred()

					subject = "Aprobado VATS ${msf510Rec.getPrimaryKey().getIncidentNo()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}";

					mailMessage = "Estimado ${Name},\n" +
							"Se informa que el VATS No ${msf510Rec.getPrimaryKey().getIncidentNo()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}\n ha sido aprobado" +
							"Periodo           :     ${fecha.substring(6, 8)}/${fecha.substring(4, 6)}/${fecha.substring(0, 4)} ${hora.substring(0, 2)}:${hora.substring(2, 4)}:${hora.substring(4, 6)} hasta ${msf510Rec.getDateReported().padRight(8, " ").substring(6, 8)}/${msf510Rec.getDateReported().padRight(8, " ").substring(4, 6)}/${msf510Rec.getDateReported().padRight(8, " ").substring(0, 4)} ${msf510Rec.getTimeReported().padRight(6, " ").substring(0, 2)}:${msf510Rec.getTimeReported().padRight(6, " ").substring(2, 4)}:${msf510Rec.getTimeReported().padRight(6, " ").substring(4, 6)}\n" +
							"PRC               :     ${msf808Rec.getPrcName().toUpperCase()}\n" +
							"Localizacion      :     ${msf510Rec.getLocatType()} ${LocatType} ${msf510Rec.getFreeFormAddr()}\n" +
							"Ejecutor          :     ${employeeEjecutor} ${Name2}\n" +
							"Descripcion Ext:  :     ${description}" +
							" \n" +
							"Atte,\n" +
							"\n" +
							"Compañia Minera Doña Ines de Collahuasi SCM.\n\n\n" +
							"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";

				}
			} catch (Exception e) {

				info("ERROR: " + e.getMessage())


				enviarCorreo = false;
			}

			break
		case 'VV':
				info("Entro a enviar case VV...");
				MSF810Rec msf810Rec
				String Name = "";
				String Name2 = "";
				try {
					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: batchParams.getEmployee_id()))
					MailTo = msf810Rec.getEmailAddress();
					Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
					enviarCorreo = true;

					if (enviarCorreo) {
						MSF510Rec msf510Rec = edoi.findByPrimaryKey(new MSF510Key(dstrctCode: commarea.District.toString(), incidentNo: batchParams.getWR_WO().trim()))

						def employee_id = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
						def dstrct_code = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
						def personInd = MSF511Key.personInd.equalTo("E")
						def PrimaryRol = MSF511Rec.primaryRole.between("00", "01");

						def query = new QueryImpl(MSF511Rec.class).and(employee_id).and(dstrct_code).and(personInd).and(PrimaryRol)

						MSF511Rec msf511Rec = edoi.firstRow(query);

						//String sqls = "select EMPLOYEE_ID from ellipse.msf511 where INCIDENT_NO='0000018016' and DSTRCT_CODE='COLL' and PERSON_IND='E' and PRIMARY_ROLE BETWEEN('00') and( '01')";
						MSF071Rec msf071rec = getMSF071Rec('003', 'VAT', msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), '001');
						String employeeEjecutor
						if (msf071rec != null) {
							employeeEjecutor = msf071rec.getRefCode()
						} else {
							employeeEjecutor = msf511Rec.getPrimaryKey().getEmployeeId()
						}
						info("employeeEjecutor: " + employeeEjecutor)

						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: employeeEjecutor))
						Name2 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
						String IncidentType = ""
						String EventSubType = ""
						String LocatType = ""
						String description = ""

						try {
							MSF096_STD_VOLATRec msf096_std_volatRec = edoi.findByPrimaryKey(
									new MSF096_STD_VOLATKey(stdKey: commarea.District.toString() + batchParams.getWR_WO().trim(), stdLineNo: "0000", stdTextCode: "IT"))
							description = msf096_std_volatRec.getStdVolat_1() + msf096_std_volatRec.getStdVolat_2() + msf096_std_volatRec.getStdVolat_3() +
									msf096_std_volatRec.getStdVolat_4() + msf096_std_volatRec.getStdVolat_5()
						} catch (Exception e) {
							description = "N/A."
						}
						sql = new Sql(dataSource);
						description = getStdText(commarea.District.toString() + msf510Rec.getPrimaryKey().getIncidentNo(), "IN")


						try {

							MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHIT', tableCode: msf510Rec.getIncidentType()))
							IncidentType = msf010Rec.getTableDesc().toUpperCase()

						} catch (Exception e1) {
							IncidentType = "N/A."
						}
						try {
							if (msf510Rec.getIncidentType().trim().equalsIgnoreCase("GRT")) {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'GRTD', tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							} else {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: msf510Rec.getIncidentType().padRight(4, " ").substring(0, 4), tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							}

						} catch (Exception e1) {
							EventSubType = "N/A."
						}

						try {

							switch (msf510Rec.getLocatType().trim()) {
								case 'P':
									MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'PHYL', tableCode: msf510Rec.getLocatCode()))
									LocatType = msf010R.getTableDesc().toUpperCase()
									break
								default:
									LocatType = "N/A."
									break
							}

						} catch (Exception e1) {
							LocatType = "N/A."
						}

						MSF808Rec msf808Rec = edoi.findByPrimaryKey(new MSF808Key(primRptCodes: msf510Rec.getPrimRptCd()))

						String fecha = (99999999 - Integer.parseInt(msf510Rec.getRevOccDate()));
						String hora = msf510Rec.getTimeOccurred()

						subject = "Validar VATS ${msf510Rec.getPrimaryKey().getIncidentNo()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}";

						mailMessage = "Estimado ${Name},\n" +
								"Se requiere que Valide el VATS No. ${msf510Rec.getPrimaryKey().getIncidentNo()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}\n" +
								"Periodo           :     ${fecha.substring(6, 8)}/${fecha.substring(4, 6)}/${fecha.substring(0, 4)} ${hora.substring(0, 2)}:${hora.substring(2, 4)}:${hora.substring(4, 6)} hasta ${msf510Rec.getDateReported().padRight(8, " ").substring(6, 8)}/${msf510Rec.getDateReported().padRight(8, " ").substring(4, 6)}/${msf510Rec.getDateReported().padRight(8, " ").substring(0, 4)} ${msf510Rec.getTimeReported().padRight(6, " ").substring(0, 2)}:${msf510Rec.getTimeReported().padRight(6, " ").substring(2, 4)}:${msf510Rec.getTimeReported().padRight(6, " ").substring(4, 6)}\n" +
								"PRC               :     ${msf808Rec.getPrcName().toUpperCase()}\n" +
								"Localizacion      :     ${msf510Rec.getLocatType()} ${LocatType} ${msf510Rec.getFreeFormAddr()}\n" +
								"Ejecutor          :     ${employeeEjecutor} ${Name2}\n" +
								"Descripcion Ext:  :     ${description}" +
								" \n" +
								"VATS solo se podra Validar antes de su periodo de validez" +
								"Atte,\n" +
								"\n" +
								"Compana Minera Dona Ines de Collahuasi SCM.\n\n\n" +
								"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";

					}
				} catch (Exception e) {

					info("ERROR: " + e.getMessage())


					enviarCorreo = false;
				}

				break
			case 'VA':
				info("Entro a enviar case VA...");
				MSF810Rec msf810Rec
				String Name = "";
				String Name2 = "";
				try {
					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: batchParams.getEmployee_id()))
					MailTo = msf810Rec.getEmailAddress();
					Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
					enviarCorreo = true;

					if (enviarCorreo) {
						MSF510Rec msf510Rec = edoi.findByPrimaryKey(new MSF510Key(dstrctCode: commarea.District.toString(), incidentNo: batchParams.getWR_WO().trim()))

						def employee_id = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
						def dstrct_code = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
						def personInd = MSF511Key.personInd.equalTo("E")
						def PrimaryRol = MSF511Rec.primaryRole.between("00", "01");

						def query = new QueryImpl(MSF511Rec.class).and(employee_id).and(dstrct_code).and(personInd).and(PrimaryRol)

						MSF511Rec msf511Rec = edoi.firstRow(query);

						//String sqls = "select EMPLOYEE_ID from ellipse.msf511 where INCIDENT_NO='0000018016' and DSTRCT_CODE='COLL' and PERSON_IND='E' and PRIMARY_ROLE BETWEEN('00') and( '01')";
						MSF071Rec msf071rec = getMSF071Rec('003', 'VAT', msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), '001');
						String employeeEjecutor
						if (msf071rec != null) {
							employeeEjecutor = msf071rec.getRefCode()
						} else {
							employeeEjecutor = msf511Rec.getPrimaryKey().getEmployeeId()
						}
						info("employeeEjecutor: " + employeeEjecutor)

						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: employeeEjecutor))
						Name2 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
						String IncidentType = ""
						String EventSubType = ""
						String LocatType = ""
						String description = ""

						try {
							MSF096_STD_VOLATRec msf096_std_volatRec = edoi.findByPrimaryKey(
									new MSF096_STD_VOLATKey(stdKey: commarea.District.toString() + batchParams.getWR_WO().trim(), stdLineNo: "0000", stdTextCode: "IT"))
							description = msf096_std_volatRec.getStdVolat_1() + msf096_std_volatRec.getStdVolat_2() + msf096_std_volatRec.getStdVolat_3() +
									msf096_std_volatRec.getStdVolat_4() + msf096_std_volatRec.getStdVolat_5()
						} catch (Exception e) {
							description = "N/A."
						}
						sql = new Sql(dataSource);
						description = getStdText(commarea.District.toString() + msf510Rec.getPrimaryKey().getIncidentNo(), "IN")


						try {

							MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHIT', tableCode: msf510Rec.getIncidentType()))
							IncidentType = msf010Rec.getTableDesc().toUpperCase()

						} catch (Exception e1) {
							IncidentType = "N/A."
						}
						try {
							if (msf510Rec.getIncidentType().trim().equalsIgnoreCase("GRT")) {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'GRTD', tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							} else {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: msf510Rec.getIncidentType().padRight(4, " ").substring(0, 4), tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							}

						} catch (Exception e1) {
							EventSubType = "N/A."
						}

						try {

							switch (msf510Rec.getLocatType().trim()) {
								case 'P':
									MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'PHYL', tableCode: msf510Rec.getLocatCode()))
									LocatType = msf010R.getTableDesc().toUpperCase()
									break
								default:
									LocatType = "N/A."
									break
							}

						} catch (Exception e1) {
							LocatType = "N/A."
						}

						MSF808Rec msf808Rec = edoi.findByPrimaryKey(new MSF808Key(primRptCodes: msf510Rec.getPrimRptCd()))

						String fecha = (99999999 - Integer.parseInt(msf510Rec.getRevOccDate()));
						String hora = msf510Rec.getTimeOccurred()

						subject = "Corregir VATS ${msf510Rec.getPrimaryKey().getIncidentNo()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}";

						mailMessage = "Estimado ${Name},\n" +
								"Se requiere que Corrija el VATS No. ${msf510Rec.getPrimaryKey().getIncidentNo()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}\n" +
								"Periodo           :     ${fecha.substring(6, 8)}/${fecha.substring(4, 6)}/${fecha.substring(0, 4)} ${hora.substring(0, 2)}:${hora.substring(2, 4)}:${hora.substring(4, 6)} hasta ${msf510Rec.getDateReported().padRight(8, " ").substring(6, 8)}/${msf510Rec.getDateReported().padRight(8, " ").substring(4, 6)}/${msf510Rec.getDateReported().padRight(8, " ").substring(0, 4)} ${msf510Rec.getTimeReported().padRight(6, " ").substring(0, 2)}:${msf510Rec.getTimeReported().padRight(6, " ").substring(2, 4)}:${msf510Rec.getTimeReported().padRight(6, " ").substring(4, 6)}\n" +
								"PRC               :     ${msf808Rec.getPrcName().toUpperCase()}\n" +
								"Localizacion      :     ${msf510Rec.getLocatType()} ${LocatType} ${msf510Rec.getFreeFormAddr()}\n" +
								"Ejecutor          :     ${employeeEjecutor} ${Name2}\n" +
								"Descripcion Ext:  :     ${description}" +
								" \n" +
								"VATS solo se podra Corregir antes de su periodo de validez" +
								"Atte,\n" +
								"\n" +
								"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
								"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";

					}
				} catch (Exception e) {

					info("ERROR: " + e.getMessage())


					enviarCorreo = false;
				}

				break
			case 'BG':
				info("Entro a enviar case BG...");
				try {

					info("contractNo: " + batchParams.getWR_WO().trim())

					MSF384Rec msf384Rec = edoi.findByPrimaryKey(new MSF384Key(contractNo: batchParams.getWR_WO().trim()))
					String boletaGaran = (batchParams.getEmployee_id() + batchParams.getDS_OT()).padRight(22, " ").substring(0, 20)
					String TipoMsj = (batchParams.getEmployee_id() + batchParams.getDS_OT()).padRight(22, " ").substring(20, 22)
					info("boletaGaran: " + boletaGaran)
					info("TipoMsj: " + TipoMsj)
					def cContractNo = MSF38AKey.contractNo.equalTo(msf384Rec.getPrimaryKey().getContractNo())
					def cSecurrLogRef = MSF38ARec.securLogRef.equalTo(boletaGaran.trim())
					def QueryMSF38A = new QueryImpl(MSF38ARec.class).and(cContractNo).and(cSecurrLogRef)

					MSF38ARec msf38ARec = edoi.firstRow(QueryMSF38A)

					if (msf38ARec != null) {
						info("ContractPrice: " + msf384Rec.getContractPrice())
						info("SecurLogAmount: " + msf38ARec.getSecurLogAmount())
						MailTo = 'NEW'

						try {
							MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key
									(employeeId: msf384Rec.getResponsCode_2()))
							if (msf810Rec.getEmailAddress().equals(' ')) {
								info('Administrador de Contrato sin email : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_2())
							} else {
								info('Administrador de Contrato');
								MailTo = msf810Rec.getEmailAddress().trim()
							}
						} catch (Exception e) {
							info('Administrador de Contrato inexistente : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_2())
						}
						try {
							MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key
									(employeeId: msf384Rec.getResponsCode_3()))
							if (msf810Rec.getEmailAddress().equals(' ')) {
								info('Ingeniero de Contrato sin email : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_3())
							} else {
								info('Ingeniero de Contrato');
								if (MailTo.equals('NEW')) {
									MailTo = msf810Rec.getEmailAddress().trim()
								} else {
									MailTo += ';' + msf810Rec.getEmailAddress().trim()
								}
							}
						} catch (Exception e) {
							info('Ingeniero de Contrato inexistente : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_3())
						}


						def cTableType = MSF010Key.tableType.equalTo("+VNC")
						def QueryMSF010 = new QueryImpl(MSF010Rec.class).and(cTableType)

						edoi.search(QueryMSF010, { MSF010Rec msf010Rec ->
							info('Soporte de Contrato');
							if (MailTo.equals('NEW')) {
								MailTo = msf010Rec.getAssocRec()
							} else {
								MailTo += ';' + msf010Rec.getAssocRec()
							}
						})

						if (TipoMsj.equals("CR")) {

							subject = "Cambio status en FIN24 ${msf38ARec.getSecurLogRef()}"

							mailMessage = "Estimados,\n" +
									"Se ha creado la boleta de garantia ${msf38ARec} con monto ${msf38ARec.getSecurLogAmount()} para el contrato: ${msf38ARec.getPrimaryKey().getContractNo()}\n "
							if (msf384Rec.getContractPrice() < msf38ARec.getSecurLogAmount()) {
								mailMessage += "Nota: el monto de la boleta de la garantia es menor al monto del contrato."
							}
							mailMessage += " \n" +
									" \n" +
									"Atte,\n" +
									"\n" +
									"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
									"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";

						} else {

							MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: 'CASF', tableCode: msf38ARec.getSecurDepFrm().trim()))
							String StatusName = msf010Rec.getTableDesc()
							subject = "Cambio de estado boleta garantia ${msf38ARec.getSecurLogRef()}"

							mailMessage = "Estimados,\n" +
									"La boleta de garantia ${msf38ARec.getSecurLogRef().trim()} del contrato: ${msf38ARec.getPrimaryKey().getContractNo()} ha cambiado al estado ${msf38ARec.getSecurDepFrm()} (${StatusName})\n " +
									" \n" +
									" \n" +
									"Atte,\n" +
									"\n" +
									"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
									"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";

						}
						enviarCorreo = true;
					} else {
						enviarCorreo = false;
					}
				} catch (Exception e) {
					info("Exception: " + e.getMessage())
					enviarCorreo = false;
				}

				break
			case 'EG':
				info("Entro a enviar case EG...");
				try {

					info("contractNo: " + batchParams.getWR_WO().trim())

					MSF384Rec msf384Rec = edoi.findByPrimaryKey(new MSF384Key(contractNo: batchParams.getWR_WO().trim()))
					String boletaGaran = (batchParams.getEmployee_id())

					def cContractNo = MSF38AKey.contractNo.equalTo(msf384Rec.getPrimaryKey().getContractNo())
					def cSecurrLogRef = MSF38AKey.securNo.equalTo(boletaGaran.trim())
					def QueryMSF38A = new QueryImpl(MSF38ARec.class).and(cContractNo).and(cSecurrLogRef)

					MSF38ARec msf38ARec = edoi.firstRow(QueryMSF38A)

					if (msf38ARec != null) {
						try {
							MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key
									(employeeId: msf384Rec.getResponsCode_2()))
							if (msf810Rec.getEmailAddress().equals(' ')) {
								info('Administrador de Contrato sin email : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_2())
							} else {
								info('Administrador de Contrato');
								MailTo = msf810Rec.getEmailAddress().trim()
							}
						} catch (Exception e) {
							info('Administrador de Contrato inexistente : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_2())
						}
						try {
							MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key
									(employeeId: msf384Rec.getResponsCode_3()))
							if (!msf810Rec.getEmailAddress().trim().equals(' ')) {
								info('Ingeniero de Contrato');
								MailTo += ';' + msf810Rec.getEmailAddress().trim()
							}
						} catch (Exception e) {
							info('Ingeniero de Contrato inexistente : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_3())
						}

						def cTableType = MSF010Key.tableType.equalTo("+EBG")
						def QueryMSF010 = new QueryImpl(MSF010Rec.class).and(cTableType)

						edoi.search(QueryMSF010, { MSF010Rec msf010Rec ->
							MailTo += ';' + msf010Rec.getAssocRec().trim();
						})

						subject = "Alerta. Moneda del contrato ${msf384Rec.getPrimaryKey().getContractNo()} es diferente a la boleta de garantia : ${msf38ARec.getSecurLogRef()}"

						mailMessage =
							"<p style=\"font-family:Courier New; font-size:14px;text-align:justify\">";
						mailMessage +=
								"<br> La garantia ${msf38ARec.getSecurLogRef()} tiene una monenda diferente a la del contrato:<br><br><br>" +
										"Moneda de la Garantia:<b> ${msf38ARec.getSecurName().trim()} </b><br>" +
										"Moneda del contrato:<b> ${msf384Rec.getCurrencyType()} </b><br>" +
										"Atte,<br><br>Compania Minera Dona Ines de Collahuasi SCM.<br><br><br>".replaceAll(" ", "&nbsp;");
						mailMessage +=
							"<img src=\"http://www.collahuasi.cl/wp-content/themes/collahuasi-01/img/collahuasi.png\"><br><br><br>" +
							"Nota: los acentos fueron eliminados por temas de configuracion del sistema.</p>";

						enviarCorreo = true;
					} else {
						enviarCorreo = false;
					}
				} catch (Exception e) {
					info("Exception: " + e.getMessage())
					enviarCorreo = false;
				}

				break

			case 'ET':
				info("Entro a enviar case EG...");
				try {

					info("contractNo: " + batchParams.getWR_WO().trim())

					MSF384Rec msf384Rec = edoi.findByPrimaryKey(new MSF384Key(contractNo: batchParams.getWR_WO().trim()))

					try {
						MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key
								(employeeId: msf384Rec.getResponsCode_2()))
						if (msf810Rec.getEmailAddress().equals(' ')) {
							info('Administrador de Contrato sin email : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_2())
						} else {
							info('Administrador de Contrato');
							MailTo = msf810Rec.getEmailAddress().trim()
						}
					} catch (Exception e) {
						info('Administrador de Contrato inexistente : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_2())
					}
					try {
						MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key
								(employeeId: msf384Rec.getResponsCode_3()))
						if (msf810Rec.getEmailAddress().equals(' ')) {
							info('Ingeniero de Contrato sin email : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_3())
						} else {
							info('Ingeniero de Contrato');
							if (MailTo.equals('NEW')) {
								MailTo = msf810Rec.getEmailAddress().trim()
							} else {
								MailTo += ';' + msf810Rec.getEmailAddress().trim()
							}
						}
					} catch (Exception e) {
						info('Ingeniero de Contrato inexistente : ' + msf384Rec.getPrimaryKey().getContractNo() + '/' + msf384Rec.getResponsCode_3())
					}


					def cTableType = MSF010Key.tableType.equalTo("+EBG")
					def cActiveStatus = MSF010Rec.activeFlag.equalTo("Y")
					def QueryMSF010 = new QueryImpl(MSF010Rec.class).and(cTableType).and(cActiveStatus);

					edoi.search(QueryMSF010, { MSF010Rec msf010Rec ->
						MailTo += ';' + msf010Rec.getAssocRec().trim();
					})

					subject = "Ingreso de Boleta de Garantia insuficiente para el contrato No ${msf384Rec.getPrimaryKey().getContractNo()}";

					mailMessage =
						"<p style=\"font-family:Courier New; font-size:14px;text-align:justify\">";
					mailMessage +="Estimado, <br><br> Se informa el ingreso de  la siguiente Boleta de Garantia, la cual se encuentra fuera de la politica establecida " +
						"por la Compania:<br><br>".replaceAll(" ", "&nbsp;");
					mailMessage += "<table style='font-family:Courier New; font-size:14px;width:50%;border=0'>";
				   
					ArrayList<String> securList = new ArrayList<String>();
					securList.add("I")
					securList.add("A")
					securList.add("P")

					def cContractNo = MSF38AKey.contractNo.equalTo(msf384Rec.getPrimaryKey().getContractNo())
					def cSecurDepForm = MSF38ARec.securDepFrm.in(securList);
					def cSecurDepType = MSF38ARec.securDepType.equalTo("01");

					def QueryMSF38A = new QueryImpl(MSF38ARec.class).and(cContractNo).and(cSecurDepForm).and(cSecurDepType);
					BigDecimal totalAmt = 0;
					DecimalFormat df = new DecimalFormat("#,###.00");
					edoi.search(QueryMSF38A, { MSF38ARec msf38ARec ->

						MSF010Rec msf010_CAGU = getMSF010Rec("CAGU", msf38ARec.getSecurGtor());
						String securBank = "";
						if (msf010_CAGU != null) {
							securBank = msf010_CAGU.getTableDesc();
						}

						BigDecimal securLogAmt = convertValue(msf38ARec.getSecurLogAmount(), msf38ARec.getSecurName(), msf384Rec.getCurrencyType())
						
						String securLogAmtS = '$' + df.format(securLogAmt);
						String securLogAmt2S = '$' + df.format(msf38ARec.getSecurLogAmount())
						totalAmt = totalAmt + securLogAmt;
						String date = msf38ARec.getReleaseDueDate();
						
						date = date.substring(6, 8) + "/" + date.substring(4, 6) + "/" + date.substring(0, 4);
						
						mailMessage +=
							"<tr><td>Numero de la Boleta de Garantia: </td><td><b>${msf38ARec.getSecurLogRef()} </b></td></tr>" +
							"<tr><td>Moneda de la Boleta de Garantia: </td><td><b>${msf38ARec.getSecurName()} </b></td></tr>" +
							"<tr><td>Fecha de vencimiento de la garantia: </td><td><b>${date} </b></td></tr>" +
							"<tr><td>Banco Emisor: <td><b>${msf38ARec.getSecurGtor()} - ${securBank}</b></td></tr>" +
							"<tr><td>Monto de la garantia: </td><td><b>${securLogAmt2S} - ${msf38ARec.getSecurName()}</b></td></tr>" +
							"<tr><td>Motivo del aviso : </td><td><b>Monto insuficiente<br><br></b></tr>".replaceAll(" ", "&nbsp;");
					});
					String securTotalS = '$' + df.format(totalAmt);
					mailMessage +=
						'<tr><td>Monto requerido en Boleta segun contrato: </td><td><b>$' + msf384Rec.getPerfGuarantor() + "- ${msf384Rec.getCurrencyType()}.</b></td></tr>"+
						"<tr><td>Monto total de Garantias que aplican: </td><td><b> ${securTotalS} - ${msf384Rec.getCurrencyType()}.</b></td></tr>" +
						"</table> <br>" +
						"Atte,<br>" +
						"<br>" +
						"Compania Minera Dona Ines de Collahuasi SCM.<br><br><br>".replaceAll(" ", "&nbsp;");
					mailMessage +=
						"<img src=\"http://www.collahuasi.cl/wp-content/themes/collahuasi-01/img/collahuasi.png\"><br><br><br>" +
						"Nota: los acentos fueron eliminados por temas de configuracion del sistema.</p>";
					enviarCorreo = true;
					isHtml = true;

				} catch (Exception e) {
					info("Exception: " + e.getMessage())
					enviarCorreo = false;
				}

				break
			case 'PO':
				info("Entro a enviar case PO...");
				
				try{
					processPO();
					enviarCorreo = true;
					IsCopy = true;
					isHtml = true;
					
				} catch (Exception e) {
					info("Exception: " + e.getMessage())
					enviarCorreo = false;
				}
	
				break;

			case 'SP':
				info("Entro a enviar case SP...");
				try {
					MSF200Rec msf200Rec = edoi.findByPrimaryKey(new MSF200Key(supplierNo: batchParams.getWR_WO().trim()))

					def cTableType = MSF010Key.tableType.equalTo("+ISP")
					def cActiveStatus = MSF010Rec.activeFlag.equalTo("Y")
					def QueryMSF010 = new QueryImpl(MSF010Rec.class).and(cTableType).and(cActiveStatus);
					MailTo = "";
					
					edoi.search(QueryMSF010, { MSF010Rec msf010Rec ->
						MailTo = MailTo + msf010Rec.getAssocRec().trim() + ";";
					})

					if (MailTo.length() > 0){
						MailTo = MailTo.substring(0, MailTo.length() - 1);
					}
					
					info("supplierNo: " + msf200Rec.getPrimaryKey().getSupplierNo())
					info("SupplierName: " + msf200Rec.getSupplierName())
					info("MailTo: " + MailTo)

					String govtId = "";
					
					MSF203Key msf203key = new MSF203Key();
					msf203key.setDstrctCode(commarea.District);
					msf203key.setSupplierNo(batchParams.getWR_WO().trim());
					MSF203Rec msf203rec
					
					try{
						msf203rec = edoi.findByPrimaryKey(msf203key);
						govtId = msf203rec.getTaxFileNo()
					} catch (Exception ex){
						try{
							msf203key.setDstrctCode("    ");
							msf203key.setSupplierNo(batchParams.getWR_WO().trim());
							msf203rec = edoi.findByPrimaryKey(msf203key);
							govtId = msf203rec.getTaxFileNo()
						} catch (Exception ex2) { }
					}
					
					subject = "Informa Suspension de Proveedor creado como Pago Unico: ${batchParams.getWR_WO().trim()} - ${msf200Rec.getSupplierName()}";
					
					mailMessage = "<p style=\"font-family:Courier New; font-size:14px;text-align:justify\"> ";
					mailMessage += "Estimados Usuarios<br>" +
						"Se informa que el siguiente proveedor ha sido suspendido en el maestro de proveedores, debido a que se encontraba creado " + 
						"como Pago Unico y ya se efectu&oacute; el pago. <br>";
					mailMessage += "<table style='font-family:Courier New; font-size:14px;width:50%;border=0'>";
					mailMessage += "<tr><td>RUT </td><td><b>${govtId} </b></td></tr>" +
						"<tr><td>Suppplier </td><td><b>${batchParams.getWR_WO().trim()} </b></td></tr>" +
						"<tr><td>Nombre del proveedor</td><td><b>${msf200Rec.getSupplierName()} </b></td></tr></table>";
					
					mailMessage += "Para activarlo nuevamente debe solicitar al proveedor la informacion faltante, segun el procedimiento " +
						"PF-04 Registro de Proveedores, completando el formulario de creacion o modificacion de proveedores normales.<br><br>" + 
						"Saluda Atentamente <br>"+
						"Compania Minera Dona Ines de Collahuasi SCM.<br></p>" +
						"<img src=\"http://www.collahuasi.cl/wp-content/themes/collahuasi-01/img/collahuasi.png\"><br><br><br>";

					enviarCorreo = true;
					isHtml = true;

				} catch (Exception e) {
					info("Exception: " + e.getMessage())
					enviarCorreo = false;
				}

				break;
			case 'CI':
				info("Entro a enviar incidente cierre case CI...");
				try {
					enviarCorreo = true;
					MSF810Rec msf810Rec;
					MSF510Rec msf510Rec = edoi.findByPrimaryKey(new MSF510Key(dstrctCode: commarea.District.toString(), incidentNo: batchParams.getWR_WO().trim()))
					def employee_id = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
					def dstrct_code = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
					def personInd = MSF511Key.personInd.equalTo("E")
					def PrimaryRol = MSF511Rec.primaryRole.between("00", "01");

					def query = new QueryImpl(MSF511Rec.class).and(employee_id).and(dstrct_code).and(personInd).and(PrimaryRol)

					MSF511Rec msf511Rec = edoi.firstRow(query);
					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec.getPrimaryKey().getEmployeeId()))
					String Name2 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: batchParams.getEmployee_id()))
					String Name3 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
					String IncidentType = ""
					String EventSubType = ""
					String description = ""
					String LocatType = ""
					String Responsables = "";
					String AccionesPreventivas = "";
					String Documentos = "*Doc adjunto";
					String correos = "";

					sql = new Sql(dataSource);
					String sqlResponsables = "Select EMPLOYEE_ID from ellipse.msf511 where  INCIDENT_NO='${msf510Rec.getPrimaryKey().getIncidentNo()}' and DSTRCT_CODE='${msf510Rec.getPrimaryKey().getDstrctCode()}' and PRIMARY_ROLE in ('00', '05','09')";
					ArrayList<GroovyRowResult> aResponsables = sql.rows(sqlResponsables).toList();
					description = getStdText(commarea.District.toString() + msf510Rec.getPrimaryKey().getIncidentNo(), "IN")

					for (int i = 0; i < aResponsables.size(); i++) {
						info("employeeId: " + aResponsables.get(i).get("EMPLOYEE_ID").toString().trim())
						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: aResponsables.get(i).get("EMPLOYEE_ID").toString().trim()))
						Responsables += aResponsables.get(i).get("EMPLOYEE_ID").toString().trim() + " " + msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName() + "\n"

						info("EmailAddress: " + msf810Rec.getEmailAddress())
						correos = correos + msf810Rec.getEmailAddress() + ";"

					}
					info("correos: " + correos)
					MailTo = correos;


					try {

						MSF010Rec msf010Rec1 = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHIT', tableCode: msf510Rec.getIncidentType()))
						IncidentType = msf010Rec1.getTableDesc().toUpperCase()

						try {

							if (msf510Rec.getIncidentType().trim().equalsIgnoreCase("GRT")) {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'GRTD', tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							} else {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: msf510Rec.getIncidentType().padRight(4, " ").substring(0, 4), tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							}


						} catch (Exception ex) {
							EventSubType = "N/A."
						}

					} catch (Exception e1) {
						IncidentType = "N/A."
						EventSubType = "N/A."
					}

					try {

						switch (msf510Rec.getLocatType().trim()) {
							case 'P':
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'PHYL', tableCode: msf510Rec.getLocatCode()))
								LocatType = msf010R.getTableDesc().toUpperCase()
								break
							default:
								LocatType = "N/A."
								break
						}

					} catch (Exception e1) {
						LocatType = "N/A."
					}
					String fecha = (99999999 - Integer.parseInt(msf510Rec.getRevOccDate()));
					String hora = msf510Rec.getTimeOccurred()
					MSF808Rec msf808Rec = edoi.findByPrimaryKey(new MSF808Key(primRptCodes: msf510Rec.getPrimRptCd()))

					def cIncidente = MSF550Key.eventNumber.equalTo(batchParams.getWR_WO().trim())

					def Query550 = new QueryImpl(MSF550Rec.class).and(cIncidente)

					edoi.search(Query550, { MSF550Rec msf550Rec ->
						sql = new Sql(dataSource);
						String queryDoc = "select c.document_no,c.document_name1 \n" +
								"from ellipse.msf581 b, ellipse.msf580 c\n" +
								"where b.doc_ref_type = 'CA' \n" +
								"and b.dstrct_code = '${commarea.District.toString()}'\n" +
								"and b.doc_reference = '${msf550Rec.getPrimaryKey().getEventNumber()}'\n" +
								"and b.doc_ref_other = '${msf550Rec.getPrimaryKey().getEventItemNo()}'\n" +
								"and c.dstrct_code = b.dstrct_code\n" +
								"and c.document_no = b.document_no"
						info(queryDoc)

						ArrayList<GroovyRowResult> result = sql.rows(queryDoc).toList();
						for (int i = 0; i < result.size(); i++) {
							Documentos += "\n" + result.get(i).get("document_no") + result.get(i).get("document_name1")
						}
						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf550Rec.getResponsible().trim()))

						AccionesPreventivas += msf550Rec.getPrimaryKey().getEventItemNo().trim().padRight(5, " ") + msf550Rec.getCorrectActDesc().padRight(60, " ").substring(0, 40) +
								(msf550Rec.getResponsible().trim() + " " + msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()).padRight(40, " ") +
								msf550Rec.getCaStatus().trim().padRight(7, " ") + "\n" + Documentos + "\n\n"
						Documentos = "*Doc adjunto";
					});

					subject = "Incidente ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()} FINALIZADO (CLOSED) o ANULADO (NULO) el dia " +
							"${msf510Rec.getLastModDate().substring(6, 8)}/${msf510Rec.getLastModDate().substring(4, 6)}/${msf510Rec.getLastModDate().substring(0, 4)} a las " +
							"${msf510Rec.getLastModTime().substring(0, 2)}:${msf510Rec.getLastModTime().substring(2, 4)} hrs por ${batchParams.getEmployee_id()}";

					mailMessage = "Estimados:\n" +
							"Se informa que el incidente ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()} ha  sido FINALIZADO (CLOSED) o ANULADO (NULO) el dia " +
							"${msf510Rec.getLastModDate().substring(6, 8)}/${msf510Rec.getLastModDate().substring(4, 6)}/${msf510Rec.getLastModDate().substring(0, 4)} a las " +
							"${msf510Rec.getLastModTime().substring(0, 2)}:${msf510Rec.getLastModTime().substring(2, 4)} hrs por ${batchParams.getEmployee_id()}\n" +
							" \n" +
							"Resumen del evento\n" +
							"No                :     ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}  \n" +
							"Fecha ocurrencia  :     ${fecha.substring(6, 8)}/${fecha.substring(4, 6)}/${fecha.substring(0, 4)} ${hora.substring(0, 2)}:${hora.substring(2, 4)}:${hora.substring(4, 6)}\n" +
							"Tipo              :     ${IncidentType} \n" +
							"Subtipo           :     ${EventSubType}\n" +
							"PRC               :     ${msf808Rec.getPrcName().toUpperCase()}\n" +
							"Localizacion      :     ${msf510Rec.getLocatType()} ${LocatType} ${msf510Rec.getFreeFormAddr()}\n" +
							"Creador en Ellipse:     ${msf511Rec.getPrimaryKey().getEmployeeId()} ${Name2} \n" +
							"Responsables      :     " +
							"\n${Responsables}" +
							"\n" +
							"Cierre            : FINALIZADO o ANULADO por Usuario ${batchParams.getEmployee_id()} ${Name3}\n" +
							//"Comentarios Cierre: XXXXXXXXX\n" +
							" \n" +
							"Plan(es) de accion asociado\n" +
							"Plan " + "Descripcion".padRight(40, " ") + " Responsable".padRight(40, " ") + "Status\n" +
							AccionesPreventivas +
							" \n" +
							"Descripcion Ext:  :     ${description}" +
							" \n" +
							"Atte,\n" +
							"\n" +
							"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
							"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";

					enviarCorreo = true;
				} catch (Exception e) {
					info("Error: " + e.getMessage())
					enviarCorreo = false;
				}

				break;
			case 'GR':
				info("Entro a enviar incidente case GR...");
				try {
					MSF810Rec msf810Rec
					enviarCorreo = true;
					MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: "+OHT", tableCode: batchParams.getEmployee_id().trim()))
					info("Tipo: " + msf010Rec.getAssocRec())
					int pos1 = Integer.parseInt(msf010Rec.getAssocRec().trim()) - 1
					int pos2 = pos1 + 1
					String correos = ""
					info("pos1: " + pos1)
					info("pos2: " + pos2)
					def tabletype = MSF010Key.tableType.equalTo("+E51")
					def Query = new QueryImpl(MSF010Rec.class).and(tabletype)

					edoi.search(Query, { MSF010Rec msf010Rec1 ->
						info("TableCode: " + msf010Rec1.getPrimaryKey().getTableCode())
						info("AssocRec: " + msf010Rec1.getAssocRec().trim())
						info("AssocRec(${pos1},${pos2}): " + msf010Rec1.getAssocRec().trim().substring(pos1, pos2))
						if (msf010Rec1.getAssocRec().trim().substring(pos1, pos2).equals("Y")) {

							msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf010Rec1.getPrimaryKey().getTableCode().trim()))
							info("EmailAddress: " + msf810Rec.getEmailAddress())
							correos = correos + msf810Rec.getEmailAddress() + ";"
							info("correos: " + correos)
						}
					})
					MailTo = correos;
					MSF510Rec msf510Rec = edoi.findByPrimaryKey(new MSF510Key(dstrctCode: commarea.District.toString(), incidentNo: batchParams.getWR_WO().trim()))

					def employee_id = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
					def dstrct_code = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
					def personInd = MSF511Key.personInd.equalTo("E")
					def PrimaryRol = MSF511Rec.primaryRole.between("00", "01");

					def query = new QueryImpl(MSF511Rec.class).and(employee_id).and(dstrct_code).and(personInd).and(PrimaryRol)

					MSF511Rec msf511Rec = edoi.firstRow(query);
					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec.getPrimaryKey().getEmployeeId()))
					String Name2 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
					String IncidentType = ""
					String EventSubType = ""
					String description = ""
					String LocatType = ""
					String Responsables = "";

					PrimaryRol = MSF511Rec.primaryRole.between("00", "05");

					query = new QueryImpl(MSF511Rec.class).and(employee_id).and(dstrct_code).and(personInd).and(PrimaryRol)

					sql = new Sql(dataSource);
					String sqlResponsables = "Select EMPLOYEE_ID from ellipse.msf511 where  INCIDENT_NO='${msf510Rec.getPrimaryKey().getIncidentNo()}' and DSTRCT_CODE='${msf510Rec.getPrimaryKey().getDstrctCode()}' and PRIMARY_ROLE in ('00', '05','09')";
					ArrayList<GroovyRowResult> aResponsables = sql.rows(sqlResponsables).toList();



					for (int i = 0; i < aResponsables.size(); i++) {
						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: aResponsables.get(i).get("EMPLOYEE_ID").toString().trim()))
						Responsables += aResponsables.get(i).get("EMPLOYEE_ID").toString().trim() + " " + msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName() + "\n"

					}
					/*     edoi.search(query, { MSF511Rec msf511R ->

						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511R.getPrimaryKey().getEmployeeId()))
						Responsables += msf511R.getPrimaryKey().getEmployeeId() + " " + msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName() + "\n"

					});
					*/
					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec.getPrimaryKey().getEmployeeId()))


					description = getStdText(commarea.District.toString() + msf510Rec.getPrimaryKey().getIncidentNo(), "IN")
					try {

						MSF010Rec msf010Rec1 = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHIT', tableCode: msf510Rec.getIncidentType()))
						IncidentType = msf010Rec1.getTableDesc().toUpperCase()

						try {

							if (msf510Rec.getIncidentType().trim().equalsIgnoreCase("GRT")) {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'GRTD', tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							} else {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: msf510Rec.getIncidentType().padRight(4, " ").substring(0, 4), tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							}

						} catch (Exception ex) {
							EventSubType = "N/A."
						}

					} catch (Exception e1) {
						IncidentType = "N/A."
						EventSubType = "N/A."
					}
/*
					try {
						MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: msf510Rec.getIncidentType().substring(0, 4), tableCode: msf510Rec.getEventSubType()))
						EventSubType = msf010R.getTableDesc().toUpperCase()
					} catch (Exception e1) {
						EventSubType = "N/A."
					}
*/
					try {

						switch (msf510Rec.getLocatType().trim()) {
							case 'P':
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'PHYL', tableCode: msf510Rec.getLocatCode()))
								LocatType = msf010R.getTableDesc().toUpperCase()
								break
							default:
								LocatType = "N/A."
								break
						}

					} catch (Exception e1) {
						LocatType = "N/A."
					}

					String fecha = (99999999 - Integer.parseInt(msf510Rec.getRevOccDate()));
					String hora = msf510Rec.getTimeOccurred()
					MSF808Rec msf808Rec = edoi.findByPrimaryKey(new MSF808Key(primRptCodes: msf510Rec.getPrimRptCd()))
					subject = "Nuevo ${IncidentType} creado en Ellipse: ${batchParams.getWR_WO().trim()}  ${(msf510Rec.getIncidentDesc()).toUpperCase()}";

					mailMessage = "Estimados:\n" +
							"Se ha creado ${IncidentType} \n\n" +
							"No                :     ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}  \n" +
							"Fecha ocurrencia  :     ${fecha.substring(6, 8)}/${fecha.substring(4, 6)}/${fecha.substring(0, 4)} ${hora.substring(0, 2)}:${hora.substring(2, 4)}:${hora.substring(4, 6)}\n" +
							"Tipo              :     ${IncidentType} \n" +
							"Subtipo           :     ${EventSubType}\n" +
							"PRC               :     ${msf808Rec.getPrcName().toUpperCase()}\n" +
							"Localizacion      :     ${msf510Rec.getLocatType()} ${LocatType} ${msf510Rec.getFreeFormAddr()}\n" +
							"Creador en Ellipse:     ${msf511Rec.getPrimaryKey().getEmployeeId()} ${Name2}\n" +
							"Responsables      :     \n" +
							"${Responsables}" +
							" \n" +
							"Descripcion Ext:  :     ${description}" +
							" \n" +
							"Atte,\n" +
							"\n" +
							"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
							"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";


				} catch (Exception e) {
					info("Error: " + e.getMessage())
					enviarCorreo = false;
				}


				break;
			case 'PP':
				try {
					MSF810Rec msf810Rec
					String correos = ""
					def tabletype = MSF010Key.tableType.equalTo("+PPU")
					def Query = new QueryImpl(MSF010Rec.class).and(tabletype)
	
					edoi.search(Query, { MSF010Rec msf010Rec1 ->
						info("TableCode: " + msf010Rec1.getPrimaryKey().getTableCode())
						info("AssocRec: " + msf010Rec1.getAssocRec().trim())
	
						if (msf010Rec1.getActiveFlag().equals("Y")) {
	
							msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf010Rec1.getTableDesc().trim()))
							info("EmailAddress: " + msf810Rec.getEmailAddress())
							correos = correos + msf810Rec.getEmailAddress() + ";"
							info("correos: " + correos)
						}
					})
					MailTo = correos;
					subject = "Proveedor dado de baja en Ellipse: ${batchParams.getWR_WO().trim()} ";
	
					mailMessage = "Estimados:\n" +
							"Se ha deshabilitado el siguiente proveedor ${batchParams.getWR_WO().trim()} \n\n" +
							"Atte,\n" +
							"\n" +
							"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
							"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";
	
	
				} catch (Exception e) {
					info("Error: " + e.getMessage())
					enviarCorreo = false;
				}
	
	
				break;
			case 'IN':
				info("Entro a enviar incidente case IN...");
				MSF810Rec msf810Rec
				String Name = "";
				String Name2 = "";
				try {
					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: batchParams.getEmployee_id()))
					MailTo = msf810Rec.getEmailAddress();
					Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
					enviarCorreo = true;

					if (enviarCorreo) {
						MSF510Rec msf510Rec = edoi.findByPrimaryKey(new MSF510Key(dstrctCode: commarea.District.toString(), incidentNo: batchParams.getWR_WO().trim()))

						def employee_id = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
						def dstrct_code = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
						def personInd = MSF511Key.personInd.equalTo("E")
						def PrimaryRol = MSF511Rec.primaryRole.between("00", "01");

						def query = new QueryImpl(MSF511Rec.class).and(employee_id).and(dstrct_code).and(personInd).and(PrimaryRol)

						MSF511Rec msf511Rec = edoi.firstRow(query);

						//String sqls = "select EMPLOYEE_ID from ellipse.msf511 where INCIDENT_NO='0000018016' and DSTRCT_CODE='COLL' and PERSON_IND='E' and PRIMARY_ROLE BETWEEN('00') and( '01')";

						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec.getPrimaryKey().getEmployeeId()))
						Name2 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
						String IncidentType = ""
						String EventSubType = ""
						String LocatType = ""
						String description = ""

						try {
							MSF096_STD_VOLATRec msf096_std_volatRec = edoi.findByPrimaryKey(
									new MSF096_STD_VOLATKey(stdKey: commarea.District.toString() + batchParams.getWR_WO().trim(), stdLineNo: "0000", stdTextCode: "IT"))
							description = msf096_std_volatRec.getStdVolat_1() + msf096_std_volatRec.getStdVolat_2() + msf096_std_volatRec.getStdVolat_3() +
									msf096_std_volatRec.getStdVolat_4() + msf096_std_volatRec.getStdVolat_5()

						} catch (Exception e) {
							description = "N/A."
						}
						sql = new Sql(dataSource);
						description = getStdText(commarea.District.toString() + msf510Rec.getPrimaryKey().getIncidentNo(), "IN")

						try {

							MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHIT', tableCode: msf510Rec.getIncidentType()))
							IncidentType = msf010Rec.getTableDesc().toUpperCase()

						} catch (Exception e1) {
							IncidentType = "N/A."
						}
						try {
							if (msf510Rec.getIncidentType().trim().equalsIgnoreCase("GRT")) {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'GRTD', tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							} else {
								MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: msf510Rec.getIncidentType().padRight(4, " ").substring(0, 4), tableCode: msf510Rec.getEventSubType()))
								EventSubType = msf010R.getTableDesc().toUpperCase()
							}

						} catch (Exception e1) {
							EventSubType = "N/A."
						}

						try {

							switch (msf510Rec.getLocatType().trim()) {
								case 'P':
									MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'PHYL', tableCode: msf510Rec.getLocatCode()))
									LocatType = msf010R.getTableDesc().toUpperCase()
									break
								default:
									LocatType = "N/A."
									break
							}

						} catch (Exception e1) {
							LocatType = "N/A."
						}

						MSF808Rec msf808Rec = edoi.findByPrimaryKey(new MSF808Key(primRptCodes: msf510Rec.getPrimRptCd()))

						String fecha = (99999999 - Integer.parseInt(msf510Rec.getRevOccDate()));
						String hora = msf510Rec.getTimeOccurred()

						subject = "Nuevo ${IncidentType} en su responsabilidad:  ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}";

						mailMessage = "Estimado ${Name},\n" +
								"Se ha creado ${IncidentType} bajo su responsabilidad:\n" +
								"No                :     ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}  \n" +
								"Fecha ocurrencia  :     ${fecha.substring(6, 8)}/${fecha.substring(4, 6)}/${fecha.substring(0, 4)} ${hora.substring(0, 2)}:${hora.substring(2, 4)}:${hora.substring(4, 6)}\n" +
								"Tipo              :     ${IncidentType} \n" +
								"Subtipo           :     ${EventSubType}\n" +
								"PRC               :     ${msf808Rec.getPrcName().toUpperCase()}\n" +
								"Localizacion      :     ${msf510Rec.getLocatType()} ${LocatType} ${msf510Rec.getFreeFormAddr()}\n" +
								"Creador en Ellipse:     ${msf511Rec.getPrimaryKey().getEmployeeId()} ${Name2}\n" +
								" \n" +
								"Descripcion Ext:  :     ${description}" +
								" \n" +
								"Atte,\n" +
								"\n" +
								"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
								"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";


					}
				} catch (Exception e) {

					info("ERROR: " + e.getMessage())


					enviarCorreo = false;
				}

				break;
			case 'GT':
				info("Entro a enviar incidente case GT...");
				MSF810Rec msf810Rec
				String Name = "";
				String Name2 = "";
				String Name3 = "";
				try {
					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: batchParams.getEmployee_id().trim()))
					MailTo = msf810Rec.getEmailAddress();
					info("MailTo: " + MailTo)
					Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
					enviarCorreo = true;

					if (enviarCorreo) {
						MSF510Rec msf510Rec = edoi.findByPrimaryKey(new MSF510Key(dstrctCode: commarea.District.toString(), incidentNo: batchParams.getWR_WO().trim()))
						info("IncidentNo: " + msf510Rec.getPrimaryKey().getIncidentNo())
						def employee_id = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
						def dstrct_code = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
						def personInd = MSF511Key.personInd.equalTo("E")

						ArrayList<String> AprimaryRole = new ArrayList<String>()
						AprimaryRole.add("00")
						AprimaryRole.add("01")
						AprimaryRole.add("08")
						def PrimaryRol = MSF511Rec.primaryRole.in(AprimaryRole);

						def query = new QueryImpl(MSF511Rec.class).and(employee_id).and(dstrct_code).and(personInd).and(PrimaryRol)

						MSF511Rec msf511Rec = edoi.firstRow(query);
						info("EmployeeId: " + msf511Rec.getPrimaryKey().getEmployeeId())
						//String sqls = "select EMPLOYEE_ID from ellipse.msf511 where INCIDENT_NO='0000018016' and DSTRCT_CODE='COLL' and PERSON_IND='E' and PRIMARY_ROLE BETWEEN('00') and( '01')";

						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec.getPrimaryKey().getEmployeeId()))
						Name2 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
						String IncidentType = ""
						String EventSubType = ""
						String LocatType = ""
						String description = ""
						String rol = ""
						def employee_id_lider = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
						def dstrct_code_lider = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
						def PrimaryRol_lider = MSF511Rec.primaryRole.between("07", "09");

						def queryLider = new QueryImpl(MSF511Rec.class).and(employee_id_lider).and(dstrct_code_lider).and(PrimaryRol_lider)

						edoi.search(queryLider, { MSF511Rec msf511Rec_lideres ->
							Name3 += msf511Rec_lideres.getPrimaryKey().getEmployeeId() + " "

							try {
								MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHRC', tableCode: msf511Rec_lideres.getPrimaryRole()))
								rol = msf010Rec.getTableDesc()
							} catch (Exception e2) {
								rol = ' '
							}

							if (msf511Rec_lideres.getPrimaryKey().getPersonInd().equals("E")) {
								msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec_lideres.getPrimaryKey().getEmployeeId()))
								Name3 += msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName() + " ${rol}" + " | "
							} else {
								try {
									MSF811Rec msf811Rec = edoi.findByPrimaryKey(new MSF811Key(nonEmplId: msf511Rec_lideres.getPrimaryKey().getEmployeeId()))
									Name3 += msf811Rec.getSurname() + ", " + msf811Rec.getFirstName() + " ${rol}" + " | "
								} catch (Exception e1) {
									msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec_lideres.getPrimaryKey().getEmployeeId()))
									Name3 += msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName() + " ${rol}" + " | "
								}
							}
						})

						/*  MSF511Rec msf511Rec_lider = edoi.firstRow(queryLider);
						  if (msf511Rec_lider.getPrimaryKey().getPersonInd().equals("E")) {
							  msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec_lideres.getPrimaryKey().getEmployeeId()))
							  Name3 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
						  } else {
							  try {
								  MSF811Rec msf811Rec = edoi.findByPrimaryKey(new MSF811Key(nonEmplId: msf511Rec_lideres.getPrimaryKey().getEmployeeId()))
								  Name3 = msf811Rec.getSurname() + ", " + msf811Rec.getFirstName()
							  } catch (Exception e1) {
								  msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec_lideres.getPrimaryKey().getEmployeeId()))
								  Name3 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
							  }
						  }*/

						try {
							MSF096_STD_VOLATRec msf096_std_volatRec = edoi.findByPrimaryKey(
									new MSF096_STD_VOLATKey(stdKey: commarea.District.toString() + batchParams.getWR_WO().trim(), stdLineNo: "0000", stdTextCode: "IT"))
							description = msf096_std_volatRec.getStdVolat_1() + msf096_std_volatRec.getStdVolat_2() + msf096_std_volatRec.getStdVolat_3() +
									msf096_std_volatRec.getStdVolat_4() + msf096_std_volatRec.getStdVolat_5()
						} catch (Exception e) {
							description = "N/A."
						}
						sql = new Sql(dataSource);
						description = getStdText(commarea.District.toString() + msf510Rec.getPrimaryKey().getIncidentNo(), "IN")

						try {

							MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHIT', tableCode: msf510Rec.getIncidentType()))
							IncidentType = msf010Rec.getTableDesc().toUpperCase()

						} catch (Exception e1) {
							IncidentType = "N/A."
						}
						try {
							MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'GRTD', tableCode: msf510Rec.getEventSubType()))
							EventSubType = msf010R.getTableDesc().toUpperCase()
						} catch (Exception e1) {
							EventSubType = "N/A."
						}

						try {

							switch (msf510Rec.getLocatType().trim()) {
								case 'P':
									MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'PHYL', tableCode: msf510Rec.getLocatCode()))
									LocatType = msf010R.getTableDesc().toUpperCase()
									break
								default:
									LocatType = "N/A."
									break
							}

						} catch (Exception e1) {
							LocatType = "N/A."
						}

						MSF808Rec msf808Rec = edoi.findByPrimaryKey(new MSF808Key(primRptCodes: msf510Rec.getPrimRptCd()))
						info("primRptCodes: " + msf808Rec.getPrimaryKey().getPrimRptCodes())
						String fecha = (99999999 - Integer.parseInt(msf510Rec.getRevOccDate()));
						String hora = msf510Rec.getTimeOccurred()
						String IncidentesRelacionados = " "

						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '022', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))

							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '024', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))

							IncidentesRelacionados += msf071Rec.getRefCode() + " "
						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '025', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))
							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '026', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))
							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '027', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))
							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '028', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))
							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}


						subject = "Nuevo ${IncidentType} ${EventSubType} en su responsabilidad:  ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()} ";

						mailMessage = "Estimado ${Name},\n" +
								"Se ha creado ${IncidentType} y se le ha nombrado como Lider:\n" +
								"No                     :     ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}  \n" +
								"Fecha ocurrencia       :     ${fecha.substring(6, 8)}/${fecha.substring(4, 6)}/${fecha.substring(0, 4)} ${hora.substring(0, 2)}:${hora.substring(2, 4)}:${hora.substring(4, 6)}\n" +
								"Tipo                   :     ${IncidentType} \n" +
								"Subtipo                :     ${EventSubType}\n" +
								"Estado                 :     ${msf510Rec.getEvntStatus()}\n" +
								"PRC                    :     ${msf808Rec.getPrcName().toUpperCase()}\n" +
								"Localizacion           :     ${msf510Rec.getLocatType()} ${LocatType} ${msf510Rec.getFreeFormAddr()}\n" +
								"Creador en Ellipse     :     ${msf511Rec.getPrimaryKey().getEmployeeId()} ${Name2}\n" +
								"Lider(es) en Ellipse   :     ${Name3}\n" +
								"Incidentes Relac.      :     ${IncidentesRelacionados}\n" +
								" \n" +
								"Descripcion Ext        :     ${description}" +
								" \n" +
								"Atte,\n" +
								"\n" +
								"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
								"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";


					}
				} catch (Exception e) {

					info("ERROR: " + e.getMessage())


					enviarCorreo = false;
				}
				info("enviarCorreo: " + enviarCorreo)


			break; case 'GC':
				info("Entro a enviar incidente case GC...");
				MSF810Rec msf810Rec
				String Name = "";
				String Name2 = "";
				String Name3 = "";
				try {
					try {
						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: batchParams.getEmployee_id()))
						MailTo = msf810Rec.getEmailAddress();
						Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
						enviarCorreo = true;
						info("Empleado")
					} catch (Exception e2) {
						try {
							MSF811Rec msf811Rec = edoi.findByPrimaryKey(new MSF811Key(nonEmplId: batchParams.getEmployee_id()))
							MailTo = msf811Rec.getEmailAddress();
							Name = msf811Rec.getSurname() + " " + msf811Rec.getFirstName() + ", " + msf811Rec.getSecondName()
							enviarCorreo = true;
							info("No empleado")

						} catch (Exception e1) {
							info("No se encontro el empleado")
							enviarCorreo = false;
						}
					}

					if (enviarCorreo) {
						MSF510Rec msf510Rec = edoi.findByPrimaryKey(new MSF510Key(dstrctCode: commarea.District.toString(), incidentNo: batchParams.getWR_WO().trim()))

						def employee_id = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
						def dstrct_code = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
						def personInd = MSF511Key.personInd.equalTo("E")
						def PrimaryRol = MSF511Rec.primaryRole.between("00", "01");

						def query = new QueryImpl(MSF511Rec.class).and(employee_id).and(dstrct_code).and(personInd).and(PrimaryRol)

						MSF511Rec msf511Rec = edoi.firstRow(query);

						//String sqls = "select EMPLOYEE_ID from ellipse.msf511 where INCIDENT_NO='0000018016' and DSTRCT_CODE='COLL' and PERSON_IND='E' and PRIMARY_ROLE BETWEEN('00') and( '01')";

						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec.getPrimaryKey().getEmployeeId()))
						Name2 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
						String IncidentType = ""
						String EventSubType = ""
						String LocatType = ""
						String description = ""
						String rol = ''
						def employee_id_lider = MSF511Key.incidentNo.equalTo(batchParams.getWR_WO().trim())
						def dstrct_code_lider = MSF511Key.dstrctCode.equalTo(commarea.District.toString())
						def PrimaryRol_lider = MSF511Rec.primaryRole.equalTo("07");

						def queryLider = new QueryImpl(MSF511Rec.class).and(employee_id_lider).and(dstrct_code_lider).and(PrimaryRol_lider)

						MSF511Rec msf511Rec_lider = edoi.firstRow(queryLider);

						try {
							MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHRC', tableCode: msf511Rec_lider.getPrimaryRole()))
							rol = msf010Rec.getTableDesc()
						} catch (Exception e2) {
							rol = ' '
						}


						if (msf511Rec_lider.getPrimaryKey().getPersonInd().equals("E")) {
							msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec_lider.getPrimaryKey().getEmployeeId()))
							Name3 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName() + " - " + rol
						} else {
							try {
								MSF811Rec msf811Rec = edoi.findByPrimaryKey(new MSF811Key(nonEmplId: msf511Rec_lider.getPrimaryKey().getEmployeeId()))
								Name3 = msf811Rec.getSurname() + ", " + msf811Rec.getFirstName() + " - " + rol
							} catch (Exception e1) {
								msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf511Rec_lider.getPrimaryKey().getEmployeeId()))
								Name3 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName() + " - " + rol
							}
						}
						sql = new Sql(dataSource);
						description = getStdText(commarea.District.toString() + msf510Rec.getPrimaryKey().getIncidentNo(), "IN")

						try {

							MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: 'OHIT', tableCode: msf510Rec.getIncidentType()))
							IncidentType = msf010Rec.getTableDesc().toUpperCase()

						} catch (Exception e1) {
							IncidentType = "N/A."
						}
						try {
							MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'GRTD', tableCode: msf510Rec.getEventSubType()))
							EventSubType = msf010R.getTableDesc().toUpperCase()
						} catch (Exception e1) {
							EventSubType = "N/A."
						}

						try {

							switch (msf510Rec.getLocatType().trim()) {
								case 'P':
									MSF010Rec msf010R = edoi.findByPrimaryKey(new MSF010Key(tableType: 'PHYL', tableCode: msf510Rec.getLocatCode()))
									LocatType = msf010R.getTableDesc().toUpperCase()
									break
								default:
									LocatType = "N/A."
									break
							}

						} catch (Exception e1) {
							LocatType = "N/A."
						}

						MSF808Rec msf808Rec = edoi.findByPrimaryKey(new MSF808Key(primRptCodes: msf510Rec.getPrimRptCd()))

						String fecha = (99999999 - Integer.parseInt(msf510Rec.getRevOccDate()));
						String hora = msf510Rec.getTimeOccurred()
						String IncidentesRelacionados = " "

						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '022', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))

							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '024', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))

							IncidentesRelacionados += msf071Rec.getRefCode() + " "
						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '025', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))
							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '026', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))
							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '027', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))
							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}
						try {
							MSF071Rec msf071Rec = edoi.findByPrimaryKey(new MSF071Key(refNo: '028', entityType: 'INC', entityValue: msf510Rec.getPrimaryKey().getDstrctCode() + msf510Rec.getPrimaryKey().getIncidentNo(), seqNum: '001'))
							IncidentesRelacionados += msf071Rec.getRefCode() + " "

						} catch (Exception e) {

						}


						subject = "Cerrado ${IncidentType} ${EventSubType} en su responsabilidad:  ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()} ";

						mailMessage = "Estimado ${Name},\n" +
								"Se ha cerrado ${IncidentType} bajo su responsabilidad:\n" +
								"No                :     ${batchParams.getWR_WO().trim()} ${(msf510Rec.getIncidentDesc()).toUpperCase()}  \n" +
								"Fecha ocurrencia  :     ${fecha.substring(6, 8)}/${fecha.substring(4, 6)}/${fecha.substring(0, 4)} ${hora.substring(0, 2)}:${hora.substring(2, 4)}:${hora.substring(4, 6)}\n" +
								"Tipo              :     ${IncidentType} \n" +
								"Subtipo           :     ${EventSubType}\n" +
								"Estado            :     ${msf510Rec.getEvntStatus()}\n" +
								"PRC               :     ${msf808Rec.getPrcName().toUpperCase()}\n" +
								"Localizacion      :     ${msf510Rec.getLocatType()} ${LocatType} ${msf510Rec.getFreeFormAddr()}\n" +
								"Creador en Ellipse:     ${msf511Rec.getPrimaryKey().getEmployeeId()} ${Name2}\n" +
								"Lider en Ellipse  :     ${msf511Rec_lider.getPrimaryKey().getEmployeeId()} ${Name3}\n" +
								"Incidentes Relac. :     ${IncidentesRelacionados}\n" +
								" \n" +
								"Descripcion Ext:  :     ${description}" +
								" \n" +
								"Atte,\n" +
								"\n" +
								"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
								"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";


					}
				} catch (Exception e) {

					info("ERROR: " + e.getMessage())


					enviarCorreo = false;
				}

				break;
			case 'WT':
				info("Entro a enviar Work Request case WT...");
				MSF810Rec msf810Rec
				String Name = "";
				String Name2 = "";
				String OtDesc = "";
				boolean swMSEWOT = false;

				if (batchParams.getDS_OT().trim().length() > 0) {
					swMSEWOT = true;
					MSF620Rec msf620Rec = edoi.findByPrimaryKey(new MSF620Key(dstrctCode: batchParams.getDS_OT().substring(0, 4).trim(), workOrder: batchParams.getDS_OT().substring(4).trim()))
					OtDesc = msf620Rec.getWoDesc();
				}

				MSF541Rec msf541Rec = edoi.findByPrimaryKey(new MSF541Key
						(requestId: batchParams.WR_WO));



				try {

					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf541Rec.getEmployeeId()))
					MailTo = msf810Rec.getEmailAddress();
					Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
					enviarCorreo = true;

					msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: batchParams.getEmployee_id()))
					Name2 = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName();
				} catch (Exception e) {

					try {

						MSF543Rec msf543Rec = edoi.findByPrimaryKey(new MSF543Key(requestorId: msf541Rec.getRequestorId()))
						MailTo = msf543Rec.getEmailAddress();
						Name = msf543Rec.getSurname() + " " + msf543Rec.getFirstName() + ", ";
						enviarCorreo = true;

						if (MailTo.trim().length() == 0) {

							msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf541Rec.getCreationUser()))
							MailTo = msf810Rec.getEmailAddress();
							Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
							enviarCorreo = true;
						} else {
							msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf541Rec.getCreationUser()))
							MailTo = MailTo + ";" + msf810Rec.getEmailAddress();
							enviarCorreo = true;
						}

						if (MailTo.trim().length() == 0) {

							enviarCorreo = false;

						}


					} catch (Exception ex) {
						info("ERROR: " + ex.getMessage())
						enviarCorreo = false;

					}


				}
				if (enviarCorreo) {

					if (swMSEWOT) {
						subject = "Work Request: ${batchParams.getWR_WO()} ${(msf541Rec.getShortDesc_1() + msf541Rec.getShortDesc_2()).toUpperCase()} ha sido reabierta por asignacion de WO.";
						mailMessage = "Estimado ${Name},\n" +
								"Se informa que la Work Request " +
								"\nWR: ${batchParams.WR_WO} ${(msf541Rec.getShortDesc_1() + msf541Rec.getShortDesc_2()).toUpperCase()}  \n" +
								"Fecha Creacion: ${msf541Rec.getCreationDate().substring(6, 8)}/${msf541Rec.getCreationDate().substring(4, 6)}/${msf541Rec.getCreationDate().substring(0, 4)}\n" +
								"ha sido reabierta por el usuario ${batchParams.getEmployee_id()} ${Name2}\n" +
								"debido a que asigno la Oorden de trabajo \n" +
								"WO: ${batchParams.getDS_OT().substring(4).trim()} ${OtDesc}." +
								" \n" +
								" \n" +
								"Atte,\n" +
								"\n" +
								"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
								"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";

					} else {
						subject = "Work Request: ${batchParams.getWR_WO()} ${(msf541Rec.getShortDesc_1() + msf541Rec.getShortDesc_2()).toUpperCase()} ha sido reabierta desde mse541.";
						mailMessage = "Estimado ${Name},\n" +
								"Se informa reapertura de Work Request desde el modulo MSE541." +
								"\nWR: ${batchParams.WR_WO} ${(msf541Rec.getShortDesc_1() + msf541Rec.getShortDesc_2()).toUpperCase()}  " +
								"\nFecha Creacion: ${msf541Rec.getCreationDate().substring(6, 8)}/${msf541Rec.getCreationDate().substring(4, 6)}/${msf541Rec.getCreationDate().substring(0, 4)}\n" +
								"Cambio realizado por el usuario  ${batchParams.getEmployee_id()} ${Name2}." +
								" \n" +
								" \n" +
								"Atte,\n" +
								"\n" +
								"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
								"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";

					}
				}

				break;

			case 'WR':
				info("Entro a enviar Work Request case WR...");
				MSF810Rec msf810Rec
				String Name = "";
				try {
					info("requestId: " + batchParams.WR_WO.trim());

					MSF541Rec msf541Rec = edoi.findByPrimaryKey(new MSF541Key
							(requestId: batchParams.WR_WO.trim()));
					info("RequestStat: " + msf541Rec.getRequestStat());

					subject = "Work Request: ${batchParams.getWR_WO()} ${(msf541Rec.getShortDesc_1() + msf541Rec.getShortDesc_2()).toUpperCase()} ${msf541Rec.getEquipNo()} fue cerrada automáticamente.";

					info("EmployeeId: " + msf541Rec.getEmployeeId());

					try {

						msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf541Rec.getEmployeeId().padRight(10, " ")))
						MailTo = msf810Rec.getEmailAddress();
						Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
						enviarCorreo = true;

					} catch (Exception e) {
						try {

							MSF543Rec msf543Rec = edoi.findByPrimaryKey(new MSF543Key(requestorId: msf541Rec.getRequestorId()))
							MailTo = msf543Rec.getEmailAddress();
							Name = msf543Rec.getSurname() + " " + msf543Rec.getFirstName() + ", ";
							enviarCorreo = true;

							if (MailTo.trim().length() == 0) {

								msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf541Rec.getCreationUser()))
								MailTo = msf810Rec.getEmailAddress();
								Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
								enviarCorreo = true;
							} else {
								msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf541Rec.getCreationUser()))
								MailTo = MailTo + ";" + msf810Rec.getEmailAddress();
								enviarCorreo = true;
							}

							if (MailTo.trim().length() == 0) {

								enviarCorreo = false;

							}


						} catch (Exception ex) {
							info("ERROR: " + ex.getMessage())
							enviarCorreo = false;

						}


					}
					info("Name: " + Name);
					info("enviarCorreo: " + enviarCorreo);
					if (enviarCorreo) {

						String text = "";
						String linea = "";
						List<String> extText = [];
						boolean entityFound = false;

						Constraint reqId = MSFX51Key.requestId.equalTo(batchParams.WR_WO.trim())
						def qMSFx51 = new QueryImpl(MSFX51Rec.class).and(reqId);

						//Verificamos si la entidad asociada se encuentra cerrada
						edoi.search(qMSFx51, { MSFX51Rec msfx51rec ->

							String descripcion = " ";
							String EquipNo = " ";
							String CloseDate = " ";
							String textSC = " ";

							switch (msfx51rec.getPrimaryKey().getReqXrefType().toString().trim()) {
								case 'ES':
									try {
										MSF655Rec msf655Rec = edoi.findByPrimaryKey(new MSF655Key
												(estimateNo: msfx51rec.getPrimaryKey().crossReference.substring(0, 12), versionNo: msfx51rec.getPrimaryKey().crossReference.substring(12, 15)));
										msf655Rec.getEquipNo()
										if (!msf655Rec.getQuoteStatus().equals('A')) {
											entityFound = true;

										} else {
											EquipNo = msf655Rec.getEquipNo()
											descripcion = msf655Rec.getDescLine_1().toUpperCase();
											CloseDate = msf655Rec.getPlanFinishDate().substring(6, 8) + "/" + msf655Rec.getPlanFinishDate().substring(4, 6) + "/" + msf655Rec.getPlanFinishDate().substring(0, 4);

										}


									} catch (EDOIObjectNotFoundException e) {
										info("Msf655 no existe");
									}

									break
								case 'WO':
									try {

										MSF620Rec msf620Rec = edoi.findByPrimaryKey(new MSF620Key
												(dstrctCode: msfx51rec.getPrimaryKey().getCrossReference().substring(0, 4), workOrder: msfx51rec.getPrimaryKey().getCrossReference().substring(4)));
										msf620Rec.getEquipNo()
										if (msf620Rec.getWoStatusM().equals('C')) {
											try {

												MSF010Rec msf010rec = edoi.findByPrimaryKey(new MSF010Key(tableType: "SC", tableCode: msf620Rec.getCompletedCode()))

												textSC = msf620Rec.getCompletedCode() + " " + msf010rec.getTableDesc().toUpperCase();

											} catch (Exception e2) {

												textSC = msf620Rec.getCompletedCode() + " NO EXISTE DESCRIPCION";

											}
											EquipNo = msf620Rec.getEquipNo()
											descripcion = msf620Rec.getWoDesc().toUpperCase();
											CloseDate = msf620Rec.getClosedDt().substring(6, 8) + "/" + msf620Rec.getClosedDt().substring(4, 6) + "/" + msf620Rec.getClosedDt().substring(0, 4);
											try {

												MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: "+SC", tableCode: msf620Rec.getCompletedCode()))

												if (msf010Rec.getAssocRec().padRight(50, ' ').substring(0, 1).equals('Y')) {
													entityFound = true;
												} else {
													textSC = msf620Rec.getCompletedCode() + " " + msf010Rec.getTableDesc().toUpperCase();
												}
											} catch (Exception e1) {

												info("msf010Rec no existe");
											}

										} else {
											entityFound = true;
										}
									} catch (Exception e) {
										descripcion = "NINGUNA."
									}

									break
								case 'PJ':
									try {
										MSF660Rec msf660Rec = edoi.findByPrimaryKey(new MSF660Key
												(dstrctCode: msfx51rec.getPrimaryKey().getCrossReference().substring(0, 4), projectNo: msfx51rec.getPrimaryKey().getCrossReference().substring(4, 12)));
										msf660Rec.getEquipNo()
										if (!msf660Rec.getStatus_660().equals('C')) {
											entityFound = true;
										} else {
											EquipNo = msf660Rec.getEquipNo()
											descripcion = msf660Rec.getProjDesc().toUpperCase();
											CloseDate = msf660Rec.getPlanFinDate().substring(6, 8) + "/" + msf660Rec.getPlanFinDate().substring(4, 6) + "/" + msf660Rec.getPlanFinDate().substring(0, 4);

										}

									} catch (EDOIObjectNotFoundException e) {
										info("Msf660 no existe");
									}

									break
								case 'PW':
									try {

										MSF625Rec msf625Rec = edoi.findByPrimaryKey(new MSF625Key
												(dstrctCode: msfx51rec.getPrimaryKey().getCrossReference().substring(0, 4), parentWo: msfx51rec.getPrimaryKey().getCrossReference().substring(4, 12)));

										if (!msf625Rec.getStatus_625().equals('C')) {
											entityFound = true
										} else {
											EquipNo = msf625Rec.getEquipNo()
											descripcion = msf625Rec.getParentWoDesc().toUpperCase();
											CloseDate = msf625Rec.getPlanFinDate().substring(6, 8) + "/" + msf625Rec.getPlanFinDate().substring(4, 6) + "/" + msf625Rec.getPlanFinDate().substring(0, 4);
										}

									} catch (EDOIObjectNotFoundException e) {
										info("Msf625 no existe");
									}

									break
								default: break
							}

							info("batchParams.getDS_OT(): " + batchParams.getDS_OT());
							info("CrossReference: " + msfx51rec.getPrimaryKey().getCrossReference());

							if (batchParams.getDS_OT().trim().equals(msfx51rec.getPrimaryKey().getCrossReference())) {

								linea = "** " + msfx51rec.getPrimaryKey().getReqXrefType().padRight(4, " ") + msfx51rec.getPrimaryKey().getCrossReference().padRight(15, " ").substring(4) + descripcion.padRight(44, " ").toUpperCase() +
										CloseDate.padRight(14, " ") + textSC + " ${EquipNo} \n";

							} else {

								linea = "   " + msfx51rec.getPrimaryKey().getReqXrefType().padRight(4, " ") + msfx51rec.getPrimaryKey().getCrossReference().padRight(15, " ").substring(4) + descripcion.padRight(44, " ").toUpperCase() +
										CloseDate.padRight(14, " ") + textSC + " ${EquipNo} \n";

							}
							text = text + linea;
							extText.add(linea.padRight(120, ' '))

						});
						info("entityFound: " + entityFound);

						if (!entityFound) {

							/* Cerramos automaticamente la WR */
							MSF620Rec msf620R = edoi.findByPrimaryKey(new MSF620Key(dstrctCode: batchParams.getDS_OT().substring(0, 4), workOrder: batchParams.getDS_OT().substring(4)));

							msf541Rec.setCompletedBy(msf620R.getCompletedBy())
							msf541Rec.setClosedDate(commarea.TodaysDate.toString())
							msf541Rec.setClosedTime(commarea.Time.toString())
							msf541Rec.setRequestStat('C')
							edoi.update(msf541Rec)

							msf541Rec = edoi.findByPrimaryKey(new MSF541Key(requestId: batchParams.WR_WO));
							info("RequestStat cierre: " + msf541Rec.getRequestStat());

							/* Se crea la informacion de cierre de WR en texto extendido */

							MSF096Key msf096Key = new MSF096Key()
							MSF096Rec msf096Rec = new MSF096Rec()
							MSF096_STD_VOLATKey msf096_std_volatKey = new MSF096_STD_VOLATKey()
							MSF096_STD_VOLATRec msf096_std_volatRec = new MSF096_STD_VOLATRec()
							int lineNo
							String lastLineNo = getLastLineNo(msf541Rec.getPrimaryKey().getRequestId())
							info("lastLineNo... " + lastLineNo);
							try {

								msf096Key.setStdTextCode('WQ')
								msf096Key.setStdKey(msf541Rec.getPrimaryKey().getRequestId())

								msf096_std_volatKey.setStdTextCode('WQ')
								msf096_std_volatKey.setStdKey(msf541Rec.getPrimaryKey().getRequestId())

								if (lastLineNo.equals('XXXXX')) {
									lineNo = 0;
									msf096Key.setStdLineNo(lineNo.toString().padLeft(4, '0'))
									msf096_std_volatKey.setStdLineNo(lineNo.toString().padLeft(4, '0'))
									msf096_std_volatRec.setStdVolat_1('.HEADING CIERRE AUTOMATICO DE WORK REQUEST')

								} else {

									lineNo = lastLineNo.toInteger() + 1
									msf096Key.setStdLineNo(lineNo.toString().padLeft(4, '0'))
									msf096_std_volatKey.setStdLineNo(lineNo.toString().padLeft(4, '0'))
									msf096_std_volatRec.setStdVolat_1('------------------------------------\nCIERRE AUTOMATICO DE WORK REQUEST')

								}
								msf096Rec.setPrimaryKey(msf096Key)
								edoi.create(msf096Rec)

								msf096_std_volatRec.setPrimaryKey(msf096_std_volatKey)
								msf096_std_volatRec.setStdVolat_2('Fecha:' + msf541Rec.getClosedDate().substring(6, 8) + "/" + msf541Rec.getClosedDate().substring(4, 6) + "/" + msf541Rec.getClosedDate().substring(0, 4) +
										" ${commarea.Time.toString().substring(0, 2)}:${commarea.Time.toString().substring(2, 4)}")
								MSF810Rec msf810R = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf541Rec.getCompletedBy()))
								msf096_std_volatRec.setStdVolat_3('Gatillado por:' + msf541Rec.getCompletedBy() + " ${msf810R.getSurname() + " " + msf810R.getFirstName() + ", " + msf810R.getSecondName()}")
								msf096_std_volatRec.setStdVolat_4(' ')
								msf096_std_volatRec.setStdVolat_5(' ')
								msf096_std_volatRec.setStdVolLength(60)
								edoi.create(msf096_std_volatRec)
							} catch (Exception w) {
								info("Exception... " + w.getMessage());
							}
							int volatNo = 1
							boolean recWritten = true
							for (int i = 0; i < extText.size(); i++) {

								switch (volatNo) {

									case 1: recWritten = false
										msf096Key = new MSF096Key()
										msf096Rec = new MSF096Rec()
										msf096_std_volatKey = new MSF096_STD_VOLATKey()
										msf096_std_volatRec = new MSF096_STD_VOLATRec()
										msf096_std_volatRec.setStdVolat_1(extText.get(i).substring(0, 60))
										msf096_std_volatRec.setStdVolat_2(extText.get(i).substring(60))
										break
									default: lineNo = lineNo + 1
										msf096Key.setStdTextCode('WQ')
										msf096Key.setStdKey(msf541Rec.getPrimaryKey().getRequestId())
										msf096Key.setStdLineNo(lineNo.toString().padLeft(4, '0'))
										msf096Rec.setPrimaryKey(msf096Key)
										edoi.create(msf096Rec)

										msf096_std_volatKey.setStdTextCode('WQ')
										msf096_std_volatKey.setStdKey(msf541Rec.getPrimaryKey().getRequestId())
										msf096_std_volatKey.setStdLineNo(lineNo.toString().padLeft(4, '0'))
										msf096_std_volatRec.setPrimaryKey(msf096_std_volatKey)
										msf096_std_volatRec.setStdVolat_3(extText.get(i).substring(0, 60))
										msf096_std_volatRec.setStdVolat_4(extText.get(i).substring(60))
										msf096_std_volatRec.setStdVolat_5('(**)Gatilla Cierre')
										edoi.create(msf096_std_volatRec)
										recWritten = true
										volatNo = 0
										break
								}

								volatNo++

							}

							if (!recWritten) {
								lineNo = lineNo + 1
								try {
									msf096Key.setStdTextCode('WQ')
									msf096Key.setStdKey(msf541Rec.getPrimaryKey().getRequestId())
									msf096Key.setStdLineNo(lineNo.toString().padLeft(4, '0'))
									msf096Rec.setPrimaryKey(msf096Key)
									edoi.create(msf096Rec)

									msf096_std_volatKey.setStdTextCode('WQ')
									msf096_std_volatKey.setStdKey(msf541Rec.getPrimaryKey().getRequestId())
									msf096_std_volatKey.setStdLineNo(lineNo.toString().padLeft(4, '0'))
									msf096_std_volatRec.setPrimaryKey(msf096_std_volatKey)
									msf096_std_volatRec.setStdVolLength(60)
									edoi.create(msf096_std_volatRec)
								} catch (Exception w) {
									info("Exception... " + w.getMessage());
								}
							}

							msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf541Rec.getCompletedBy()))
							mailMessage = "Estimado ${Name},\n" +
									"Se informa que ${msf541Rec.getCompletedBy()} ${msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()} gatilla cierre automatico de la Work Request  " +
									"\nNro: ${batchParams.WR_WO} Descripcion: ${(msf541Rec.getShortDesc_1() + msf541Rec.getShortDesc_2()).toUpperCase()}  ${msf541Rec.getEquipNo()}" +
									"Fecha Creacion: ${msf541Rec.getCreationDate().substring(6, 8)}/${msf541Rec.getCreationDate().substring(4, 6)}/${msf541Rec.getCreationDate().substring(0, 4)}.\n" +
									" \n" +
									"Las actividades asociadas a la Work Request y su fecha de cierre son los siguientes:\n" +
									"\n" +
									text +
									" \n" +
									"\n(**) OT que cerro el Work Request." +
									" \n" +
									" \n" +
									"Atte,\n" +
									"\n" +
									"Compania Minera Dona Ines de Collahuasi SCM.\n\n\n" +
									"Nota: los acentos fueron eliminados por temas de configuracion del sistema.";

						}
					}
				} catch (Exception e) {
					info("No se encontro Work Request ${batchParams.WR_WO}");
					info("Exception: " + e.getMessage());
					int abort = Integer.parseInt(" ");
				}
				break

			default:

				info("Entro a enviar Work Order ...");

				info("batchParams.WR_WO.trim().length() > 0: " + (batchParams.WR_WO.trim().length() > 0));

				if (batchParams.Option.padRight(2, " ").substring(0, 1).equals("Y") ||
						batchParams.Option.padRight(2, " ").substring(0, 1).equals("N") ||
						batchParams.Option.padRight(2, " ").substring(0, 1).equals(" ") ||
						(batchParams.Option.trim().equals("WO") && batchParams.WR_WO.padRight(12, " ").equals("            "))) {
					info("Envio correo OTs Masivo...");
					/* Armado del Texto del mensaje */
					String mTexto = " ".trim();

					String recipientes = "";
					ArrayList<GroovyRowResult> aMSF012 = sMSF012()

					if (aMSF012.size() > 0) {

						for (int i = 0; i < aMSF012.size(); i++) {

							Constraint dataType = MSF012Key.dataType.equalTo('C')
							Constraint keyValue = MSF012Key.keyValue.like('MAIL620%')
							Constraint dataArea = MSF012Rec.dataArea.equalTo(aMSF012.get(i).get("DATA_AREA").toString())
							def qMSF012 = new QueryImpl(MSF012Rec.class).and(dataType).and(keyValue).and(dataArea);
							edoi.search(qMSF012, { MSF012Rec msf012rec ->

								recipientes = msf012rec.getDataArea().padRight(934, " ").substring(0, 7)
								try {

									info("dstrctCode: " + msf012rec.getPrimaryKey().getKeyValue().padRight(63, " "));

									MSF620Rec msf620Rec = edoi.findByPrimaryKey(new MSF620Key
											(dstrctCode: msf012rec.getPrimaryKey().getKeyValue().substring(7, 11), workOrder: msf012rec.getPrimaryKey().keyValue.padRight(63, " ").substring(11, 19)));

									String starDate = msf620Rec.getPlanStrDate().padRight(8, " ");
									String endDate = msf620Rec.getPlanFinDate().padRight(8, " ");


									if (endDate.trim().length() > 0) {

										mTexto = mTexto + (msf620Rec.getPrimaryKey().getWorkOrder()).padRight(12, " ") +
												msf620Rec.getWoDesc().padRight(45, " ").toUpperCase() +
												(starDate.substring(6, 8) + "/" + starDate.substring(4, 6) + "/" + starDate.substring(0, 4)).padRight(26, " ") +
												(endDate.substring(6, 8) + "/" + endDate.substring(4, 6) + "/" + endDate.substring(0, 4)) + "\n"

									} else {

										mTexto = mTexto + (msf620Rec.getPrimaryKey().getWorkOrder()).padRight(12, " ") +
												msf620Rec.getWoDesc().padRight(45, " ").toUpperCase() +
												(starDate.substring(6, 8) + "/" + starDate.substring(4, 6) + "/" + starDate.substring(0, 4)).padRight(26, " ") + "\n"

									}


								} catch (EDOIObjectNotFoundException e) {
									info("Msf620 no existe");
								}
								edoi.delete(msf012rec)
							});

							/* enviar correo a los email de cada empleado registrado en recipientes */
							subject = "Generacion de Ordenes de Trabajo a su Grupo:  " + recipientes;
							String descWg = " ";
							try {

								MSF720Rec msf720Rec = edoi.findByPrimaryKey(new MSF720Key(workGroup: recipientes))
								descWg = msf720Rec.getWorkGrpDesc();
							} catch (Exception ex) {

							}
							String Texto1 = "Estimado (a)(s)\n" +
									" \n" +
									"Se han creado las siguientes OTs asociadas a su grupo de trabajo ${recipientes} ${descWg.toUpperCase()}.\n" +
									"Recordar que estas OTs  debe ser ejecutadas en los plazos definidos.\n" +
									" \n" +
									"Nro OT      Descripcion                                 PSD                       PFD\n" +
									"-------------------------------------------------------------------------------------------------\n"


							String Texto2 = "\n" +
									"\nNota: los acentos fueron eliminados por temas de configuracion del sistema.\n\n" +
									"Atte,\n" +
									"\n" +
									"Compania Minera Dona Ines de Collahuasi SCM"
							MailTo = " ";


							mailMessage = Texto1 + mTexto + " \n" + " \n" + " \n" + Texto2
							Constraint workGroup = MSF723Key.workGroup.equalTo(recipientes)
							Constraint rec723Type = MSF723Key.rec_723Type.equalTo("S")
							def qMSF723 = new QueryImpl(MSF723Rec.class).and(workGroup).and(rec723Type);
							edoi.search(qMSF723, { MSF723Rec msf723rec ->
								try {

									MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf723rec.getPrimaryKey().getEmployeeId()));
									MailTo += msf810Rec.getEmailAddress() + ";";
									/* envair correo
											   para   : eMailTo
											   asunto : Asignacion de OT
											   Texto  : mTexto
											   */
								} catch (Exception e) {
									info("Msf810 no existe");
								}
							});


						}
						enviarCorreo = true;
					}

				} else {

					info("Envio correo OTs individual...");

					String mTexto = ""
					String recipientes = "";

					try {
						MSF620Rec msf620Rec = edoi.findByPrimaryKey(new MSF620Key
								(dstrctCode: batchParams.getWR_WO().substring(0, 4), workOrder: batchParams.getWR_WO().substring(4, 12)));

						String starDate = msf620Rec.getPlanStrDate().padRight(8, " ");
						String endDate = msf620Rec.getPlanFinDate().padRight(8, " ");


						if (endDate.trim().length() > 0) {

							mTexto = mTexto + (msf620Rec.getPrimaryKey().getWorkOrder()).padRight(12, " ") +
									msf620Rec.getWoDesc().padRight(44, " ").toUpperCase() +
									"PSD: " + (starDate.substring(6, 8) + "/" + starDate.substring(4, 6) + "/" + starDate.substring(0, 4)).padRight(15, " ") +
									"PFD: " + (endDate.substring(6, 8) + "/" + endDate.substring(4, 6) + "/" + endDate.substring(0, 4)) + "\n"

						} else {

							mTexto = mTexto + (msf620Rec.getPrimaryKey().getWorkOrder()).padRight(12, " ") +
									msf620Rec.getWoDesc().padRight(44, " ").toUpperCase() +
									"PSD: " + (starDate.substring(6, 8) + "/" + starDate.substring(4, 6) + "/" + starDate.substring(0, 4)).padRight(15, " ") +
									"PFD: N/A.\n"

						}


						mTexto = mTexto + "\nTareas asociadas :\n"
						Constraint dstrctCode = MSF623Key.dstrctCode.equalTo(msf620Rec.getPrimaryKey().getDstrctCode())
						Constraint workOrder = MSF623Key.workOrder.equalTo(msf620Rec.getPrimaryKey().getWorkOrder())

						def qMSF623 = new QueryImpl(MSF623Rec.class).and(dstrctCode).and(workOrder);
						edoi.search(qMSF623, { MSF623Rec msf623rec ->

							String pfd = msf623rec.getPlanFinDate().padRight(8, " ")

							if (pfd.trim().length() > 0) {
								mTexto = mTexto + "     " + msf623rec.getPrimaryKey().getWoTaskNo() + "    " +
										msf623rec.getWoTaskDesc().padRight(43, " ").toUpperCase() + " " +
										"PSD: " + (msf623rec.getPlanStrDate().substring(6, 8) + "/" + msf623rec.getPlanStrDate().substring(4, 6) + "/" + msf623rec.getPlanStrDate().substring(0, 4)).padRight(10, " ") + "     " +
										"PSD: " + (msf623rec.getPlanFinDate().substring(6, 8) + "/" + msf623rec.getPlanFinDate().substring(4, 6) + "/" + msf623rec.getPlanFinDate().substring(0, 4)).padRight(10, " ") + "\n"
							} else {
								mTexto = mTexto + "     " + msf623rec.getPrimaryKey().getWoTaskNo() + "    " +
										msf623rec.getWoTaskDesc().padRight(43, " ").toUpperCase() + " " +
										"PSD: " + (msf623rec.getPlanStrDate().substring(6, 8) + "/" + msf623rec.getPlanStrDate().substring(4, 6) + "/" + msf623rec.getPlanStrDate().substring(0, 4)).padRight(10, " ") + "     " +
										"PSD: N/A" + "\n"
							}

						});
						sql = new Sql(dataSource);
						String description = getStdTextOT(commarea.District.toString() + msf620Rec.getPrimaryKey().getWorkOrder(), "WO")

						info(description)
						info(description.replaceAll("&", "\n"))
						String TextoStd = "Informacion adicional: \n" + description.replaceAll("&", "\n")
						subject = "Asignacion de Orden de Trabajo ";

						try {
							info("Employee_id: " + msf620Rec.getAssignPerson());

							MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key(employeeId: msf620Rec.getAssignPerson()));

							MailTo = msf810Rec.getEmailAddress();
							String Name = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
							String Texto1 = "Estimado (a) ${Name}\n" +
									" \n" +
									"Se ha asignado la siguiente orden de Trabajo, que debe ser ejecutada en los plazos definidos.\n" + " \n"


							String Texto2 = "\nNota: los acentos fueron eliminados por temas de configuracion del sistema.\n\n" +
									"Atte,\n" +
									"\n" +
									"Compania Minera Dona Ines de Collahuasi SCM"


							if (description.isEmpty()) {
								mailMessage = Texto1 + " \n" + mTexto + "\n" + "\n" + "\n" + "\n" + "\n" + Texto2
							} else {
								mailMessage = Texto1 + " \n" + mTexto + "\n" + "\n" + TextoStd + "\n" + "\n" + "\n" + Texto2
							}

							/* envair correo
						   para   : eMailTo
						   asunto : Asignacion de OT
						   Texto  : mTexto
						   */
							enviarCorreo = true;
						} catch (Exception e) {
							MailTo = "NO MAIL"
							info("Msf810 no existe");
						}
					} catch (EDOIObjectNotFoundException e) {
						info("Msf620 no existe");
					}

				}
				break
		}

		try {
			List<String> logMessage = new ArrayList<String>();
			logMessage.add(cleanString(mailMessage));
			info("Email Enviado a: " + MailTo);
			info("Email Copiado a: " + emailCC);
			info("Subjet :  ${subject}");
			info("\n" + logMessage.get(0));

			MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: "+HST", tableCode: 'HOST_EMAIL'))

			String host = msf010Rec.getAssocRec()

			SendEmail myEmail;

			if (IsCopy) {
				myEmail = new SendEmail(host, cleanString(subject), MailTo, logMessage, emailCC, IsCopy)

			} else {
				myEmail = new SendEmail(host, cleanString(subject), MailTo, logMessage)
			}
			
			if (isHtml){
				myEmail.setIsHtml(isHtml);
			}

			if (enviarCorreo) {
				myEmail.sendMail()
				postSendEmail();
			}

			info("myEmail enviado: " + enviarCorreo);

		} catch (Exception e) {

			info("myEmail no enviado: " + e.getMessage());
			info("myEmail no enviado: " + e.getLocalizedMessage());

		}
	}
	
	private processPO(){
		BigDecimal qty = 0
		BigDecimal amount = 0
		String poNoItems = "";
		String poNoItemDesc = "";
		String poNoItemDescQty = "";
		String poNo = batchParams.getWR_WO().substring(0, 6)
		String date = batchParams.getEmployee_id().trim();
		DecimalFormat df = new DecimalFormat( "#,###,###,##0" );
		
		emailCC = "";
		sql = new Sql(dataSource);
		
		MSF220Rec msf220Rec = edoi.findByPrimaryKey(new MSF220Key(poNo: batchParams.getWR_WO().substring(0, 6)))
		MSF200Rec msf200Rec = edoi.findByPrimaryKey(new MSF200Key(supplierNo: msf220Rec.getSupplierNo()))
		MSF20ARec msf20ARec = edoi.findByPrimaryKey(new MSF20AKey(supplierNo: msf220Rec.getSupplierNo()))
		
		MailTo = msf20ARec.getOrderEmailAddr();
		String purchOfficer = "";
		String emailCCOS = "";
		String emailCCOC = "";
		try {
			MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key
					(employeeId: msf220Rec.getPurchOfficer()))
			if (!msf810Rec.getEmailAddress().trim().equals('')) {
				info('Purchaser Officer' + msf810Rec.getEmailAddress().trim());
				emailCC += ';' + msf810Rec.getEmailAddress().trim()
			}

			purchOfficer = msf810Rec.getSurname() + " " + msf810Rec.getFirstName() + ", " + msf810Rec.getSecondName()
		} catch (Exception e) {	}
		
		Constraint cDstrctCode231 = MSF231Key.dstrctCode.equalTo(msf220Rec.getDstrctCode());
		Constraint cPreqNo231 = MSF231Key.preqNo.equalTo(msf220Rec.getPrimaryKey().getPoNo());
		QueryImpl query231 = new QueryImpl(MSF231Rec.class).and(cDstrctCode231).and(cPreqNo231);
		
		MSF231Rec msf231recf = edoi.firstRow(query231);
		if (msf231recf != null){
			MSF230Key msf230key = new MSF230Key();
			msf230key.setDstrctCode(msf231recf.getPrimaryKey().getDstrctCode())
			msf230key.setPreqNo(msf231recf.getPrimaryKey().getPreqNo())
			
			try {
				MSF230Rec msf230rec = edoi.findByPrimaryKey(msf230key);
				
				MSF810Rec msf810Rec = edoi.findByPrimaryKey(new MSF810Key
						(employeeId: msf230rec.getRequestedBy()))
				if (!msf810Rec.getEmailAddress().trim().equals('')) {
					info('PR Creador' + msf810Rec.getEmailAddress().trim());
					emailCC += ';' + msf810Rec.getEmailAddress().trim()
				}
			} catch (Exception e) {	}
		}
		
		def cTableType = MSF010Key.tableType.equalTo("+RCP")
		def cActiveStatus = MSF010Rec.activeFlag.equalTo("Y")
		def QueryMSF010 = new QueryImpl(MSF010Rec.class).and(cTableType).and(cActiveStatus);

		edoi.search(QueryMSF010, { MSF010Rec msf010Rec ->
			info('+RCP Creador' + msf010Rec.getAssocRec().trim());
			emailCCOS += ';' + msf010Rec.getAssocRec().trim();
		})
		
		if (emailCCOS.length() >  0)
			emailCCOS = emailCCOS.substring(1, emailCCOS.length());
		
		cTableType = MSF010Key.tableType.equalTo("+RCO")
		cActiveStatus = MSF010Rec.activeFlag.equalTo("Y")
		QueryMSF010 = new QueryImpl(MSF010Rec.class).and(cTableType).and(cActiveStatus);

		edoi.search(QueryMSF010, { MSF010Rec msf010Rec2 ->
			info('+RCO Creador' + msf010Rec2.getAssocRec().trim());
			emailCCOC += ';' + msf010Rec2.getAssocRec().trim();
		})
		
		if (emailCCOC.length() > 0)
			emailCCOC = emailCCOC.substring(1, emailCCOC.length());
		
		String SupplierName = msf200Rec.getSupplierName()
		
		info("poNo: " + poNo)
		info("supplierNo: " + msf220Rec.getSupplierNo())
		info("SupplierName: " + SupplierName)
		info("MailTo: " + MailTo)
		
		String query1 =
			" SELECT SUBSTR(replace(KEY_VALUE,' ',''), 14,3) ITEM " +
			" FROM MSF012 " +
			" WHERE DATA_TYPE = 'C' AND " +
			"	KEY_VALUE LIKE 'RECEIPT${batchParams.getWR_WO().trim()}%' AND SUBSTR(DATA_AREA,1,1) = 'N'";

		info(query1)
	
		ArrayList<MSF012Rec> list = new ArrayList<MSF012Rec>();
		ArrayList<GroovyRowResult> rows = sql.rows(query1)
		ArrayList<String> poNoItem = new ArrayList<String>();
		
		rows.each { GroovyRowResult row ->
			poNoItem.add(row.get("ITEM"))
		};
	
		Constraint cPoNo = MSF221Key.poNo.equalTo(batchParams.getWR_WO().trim());
		Constraint cPoNoItem = MSF221Key.poItemNo.in(poNoItem);
		
		QueryImpl query
		
		if (poNoItem.size() > 0){
			query = new QueryImpl(MSF221Rec.class).and(cPoNo).and(cPoNoItem);
		} else {
			query = new QueryImpl(MSF221Rec.class).and(cPoNo)
		}
		
		String poType = "";
		String currency = msf220Rec.getCurrencyType();
		edoi.search(query,{ MSF221Rec msf221Rec ->
			
			info("PoItemType: " + msf221Rec.getPoItemType())
			info("Cantidad: " + qty)
			poType = msf221Rec.getPoItemType().trim();
			BigDecimal value = 0;
			
			MSF012Key msf012key = new MSF012Key();
			msf012key.setDataType("C");
			msf012key.setKeyValue("RECEIPT" + msf221Rec.getPrimaryKey().getPoNo() + msf221Rec.getPrimaryKey().getPoItemNo());
			MSF012Rec msf012rec = edoi.findByPrimaryKey(msf012key);
			try{
				value = new BigDecimal(msf012rec.getDataArea().substring(1,16));
			} catch (Exception ex){}
			
			if (msf221Rec.getPoItemType().trim().equals("S")) {
				amount += value;
			} else {
				amount += msf221Rec.getCurrNetPrI() * value;
			}
			
			String itemDesc = "";
			if (msf220Rec.getOwnedStkInd().equals("O")){
				try{
					MSF100Key msf100key = new MSF100Key();
					msf100key.setStockCode(msf221Rec.getPreqStkCode().trim())
					MSF100Rec msf100rec = edoi.findByPrimaryKey(msf100key);
					itemDesc = msf100rec.getStkDesc().trim()
				} catch (Exception ex) {}

			} else {
				try{
					MSF231Key msf231key = new MSF231Key();
					msf231key.setDstrctCode(msf221Rec.getDstrctCode())
					msf231key.setPreqNo(msf221Rec.getPreqStkCode().substring(0,6));
					msf231key.setPreqItemNo(msf221Rec.getPreqStkCode().substring(6,9));
					MSF231Rec msf231rec = edoi.findByPrimaryKey(msf231key);
					itemDesc =
						msf231rec.getItemDescx1().trim() + " " +
						msf231rec.getItemDescx2().trim() + " " +
						msf231rec.getItemDescx3().trim() + " " +
						msf231rec.getItemDescx4().trim()
				} catch (Exception ex) {}
			}

			
			qty += msf221Rec.getCurrQtyI();
			poNoItems += msf221Rec.getPrimaryKey().getPoItemNo() + ",";
			poNoItemDesc += "<br>" + msf221Rec.getPrimaryKey().getPoItemNo() + " - " + itemDesc + ".<br>";
			poNoItemDescQty += " " + value + " Unidad(es) de " + itemDesc + " correspondiente a la entrega parcial o total del item No " + msf221Rec.getPrimaryKey().getPoItemNo() + "<br><br>";
			
		})

		/*if (poNoItemDescQty.length() > 0){
			poNoItemDescQty = poNoItemDescQty.substring(0, poNoItemDescQty.length() - 1);
		}*/

		if (poNoItems.length() > 0){
			poNoItems = poNoItems.substring(0, poNoItems.length() - 1);
		}

		info("Monto: " + amount)
		String sAmount = '$' + df.format(amount);
		
		if (poType.equals("S")){
			emailCC = emailCCOS;
			subject = "Autorizacion de Facturar Orden de Servicio ${poNo} Items ${poNoItems}";
			
			mailMessage = "<p style=\"font-family:Courier New; font-size:14px;text-align:justify\"> ";
			mailMessage +=
				"Estimado Proveedor: <br>" +
				" <br>" +
				"A continuacion, por medio de la presente se autoriza a facturar la entrega parcial o total de(los) item(s) <b>${poNoItems}</b> de la Orden de Servicio No <b>${poNo}</b> por un monto neto de <b>${sAmount} - ${currency}</b> correspondiente a los servicios: <br> ${poNoItemDesc}<br>" +
				"<br>" +
				"Facturacion:<br>"+ 
				"De acuerdo a la recepcion de los Servicios detallados, la factura electronica a emitir debe incluir la siguiente informacion obligatoria, para evitar que su factura sea rechazada comercialmente y reclamada ante el Servicio de Impuestos Internos:<br>" +
				"<br>" +
				"* Numero de la Orden de Servicio (Orden de Compra) sin puntos, espacios o caracteres adicionales, en la seccion referencias de la factura. No en la Glosa.<br>" +
				"* En la descripcion de la factura debe indicar el servicio prestado, el numero de estado de pago y el mes de servicio.<br>" +
				"<br>" +
				"De no indicar la informacion solicitada en la factura, Collahuasi procedera a reclamarla dentro del plazo legal de ocho dias que establece la Ley 19.983, en cuyo caso debera emitir una nota de credito de " +
				"anulacion, y si corresponde, debera emitir una nueva factura con los requerimientos que se les solicita.<br>" +
				"Tenga presente que no debe incluir dos o mas ordenes de Servicios en la misma factura electronica, y los montos correspondientes autorizados entre si con la factura deben ser iguales, de lo contrario la " +
				"factura sera reclamada por contenido ante el SII. Tambien, no debe indicar en las facturas la formas de pago <b>CONTADO</b> cuando las condiciones de pago establecidas contractualmente con Collahuasi <br>" +
				"correspondan a <b>CREDITO</b> a 30 dias de recibida su factura.<br>" +
				"<br>" +
				"Las consultas sobre facturacion y pago, debe realizarlas en nuestro Portal de Consulta de pago a Proveedores, https://proveedores.collahuasi.cl, donde puede revisar en linea y descargar toda la "+
				"informacion correspondiente al estado de sus facturas. En la seccion Preguntas Frecuentes del Portal, se encuentra publicado nuestros requerimientos de facturacion y los pasos a seguir para obtener su "+
				"clave de acceso al portal.<br>" +
				"Recuerde que el pago de su factura se realizara el viernes mas cercano a los 30 dias de recibida, siempre que cumpla con la recepcion conforme y requisitos de facturacion mencionados.<br>" +
				"Saluda Atentamente,<br>" +
				" <br>".replaceAll(" ", "&nbsp;");
			mailMessage +="<b>Compania Minera Dona Ines de Collahuasi SCM.</b><br>" +
				"<img src=\"http://www.collahuasi.cl/wp-content/themes/collahuasi-01/img/collahuasi.png\"><br><br><br>" +
				"Nota: los acentos fueron eliminados por temas de configuracion del sistema." +
				"</p>";
		} else {
			emailCC = emailCCOC;
			subject = "Informa Recepcion Orden de Compra ${poNo} Items ${poNoItems}";
			
			mailMessage = "<p style=\"font-family:Courier New; font-size:14px;text-align:justify\"> ";
			mailMessage +=
				"Estimado Proveedor: <br>" +
				" <br>" +
				"A continuacion, por medio de la presente se informa la recepcion de: <br><b> ${poNoItemDescQty}</b>de la Orden de Compra No <b>${poNo}</b> por un monto neto de <b>${sAmount} - ${currency}</b> para que proceda a su facturacion segun se indica en "+
				"el presente correo electronico. De existir discrepancias de recepcion de bienes, discrepancias de precio o discrepancias de cantidad, debera comunicarse con el comprador <b>${purchOfficer}</b> para su regularizacion. <br><br><br>" +
				"Facturacion: <br>" +
				"De acuerdo a la recepcion de los bienes detallados, la factura electronica a emitir debe incluir la siguiente informacion obligatoria, para evitar que su factura sea rechazada comercialmente y reclamada ante el Servicio de Impuestos Internos: <br>" +
				"	 <br>" +
				"* Numero de la Orden de Compra sin puntos, espacios o caracteres adicionales, en la seccion referencias de la factura. No en la Glosa. <br>" +
				"* Numero de la Guia de Despacho con que entrego los productos, en la seccion referencias de la factura. <br>" +
				"* Numero de item de la Orden de Compra facturado. <br>" +
				" <br>" +
				"De no indicar la informacion solicitada en la factura, Collahuasi procedera a reclamarla dentro del plazo legal de ocho dias que establece la Ley 19.983, en cuyo caso debera emitir una nota de credito de " +
				"anulacion, y si corresponde, debera emitir una nueva factura con los requerimientos que se les solicita. <br>" +
				"Tenga presente que no debe incluir dos o mas ordenes de Compras en la misma factura electronica, y los montos correspondientes autorizados entre si con la factura deben ser iguales, salvo que su Orden de Compra "+
				"incluya reajustabilidad de precio a la paridad establecida, de lo contrario la factura sera reclamada por contenido ante el SII. Tambien, no debe indicar en las facturas la formas de pago <b>CONTADO</b> cuando " +
				"las condiciones de pago establecidas contractualmente con Collahuasi correspondan a <b>CREDITO</b> a 30 dias de recibida su factura. Por ultimo, toda discrepancias de recepcion de bienes, discrepancias de precio o discrepancias "+
				"de cantidad, debe ser regularizada directamente con el comprador indicado en la Orden de Compra. <br>" +
				"Las consultas sobre facturacion y pago, debe realizarlas en nuestro Portal de Consulta de pago a Proveedores, https://proveedores.collahuasi.cl, donde puede revisar en linea y descargar toda la informacion correspondiente al estado de sus facturas. <br>"+
				"En la seccion Preguntas Frecuentes del Portal, se encuentra publicado nuestros requerimientos de facturacion y los pasos a seguir para obtener su clave de acceso al portal. <br>" +
				"Recuerde que el pago de su factura se realizara el viernes mas cercano a los 30 dias de recibida, siempre que cumpla con la recepcion conforme y requisitos de facturacion mencionados. <br>" +
				"Saluda Atentamente, <br>" +
				" <br>" +
				"<b>Compania Minera Dona Ines de Collahuasi SCM.</b><br>".replaceAll(" ", "&nbsp;");
			mailMessage +=
				" <img src=\"http://www.collahuasi.cl/wp-content/themes/collahuasi-01/img/collahuasi.png\"><br><br><br>" +
				"Nota: los acentos fueron eliminados por temas de configuracion del sistema." +
				"</p>";
		}

	}
	
	/**
	 * getMSF071Rec
	 * @param refNo
	 * @param entityType
	 * @param entityValue
	 * @param seqNum
	 * @return
	 */
	private MSF071Rec getMSF071Rec(String refNo, String entityType, String entityValue, String seqNum) {
		info("getMSF071Rec")
		try {
			MSF071Rec msf071Rec = edoi.findByPrimaryKey(
					new MSF071Key(refNo: refNo, entityType: entityType, entityValue: entityValue, seqNum: seqNum)
			);
			info("msf071Rec encontrado: " + msf071Rec.getRefCode())
			return msf071Rec;
		} catch (Exception ex) {
			info("No encontrado: " + ex.getMessage())
			return null;
		}
	}
	
	private postSendEmail(){
		info("postSendEmail : " + batchParams.getOption())
		sql = new Sql(dataSource);
		
		switch (batchParams.getOption().trim()) {
			case 'PO':
					String query012 =
					" UPDATE MSF012 SET DATA_AREA = 'S' || SUBSTR(DATA_AREA,2,LENGTH(DATA_AREA)) " +
					" WHERE DATA_TYPE = 'C' AND KEY_VALUE LIKE 'RECEIPT${batchParams.getWR_WO().trim()}%'";
				sql.executeUpdate(query012);
				break;
		}
	}

	private ArrayList<GroovyRowResult> sMSF012() {
		sql = new Sql(dataSource);
		String QueryMSF012 = "SELECT SUBSTR(DATA_AREA,1,7) DATA_AREA FROM ELLIPSE.MSF012 WHERE DATA_TYPE = 'C' AND KEY_VALUE LIKE 'MAIL620%' GROUP BY SUBSTR(DATA_AREA,1,7)";
		ArrayList<GroovyRowResult> aMSF012 = sql.rows(QueryMSF012).toList();

		return aMSF012;
	}

	private String getLastLineNo(String workRequest) {
		info("getLastLineNo")

		sql = new Sql(dataSource);
		String query096 = "SELECT NVL(MAX(STD_LINE_NO),'XXXXX') lastLineNo FROM MSF096 WHERE STD_TEXT_CODE = 'WQ' AND STD_KEY = '${workRequest}'";
		def result = sql.firstRow(query096);
		return result.lastLineNo;
	}

	public static String cleanString(String texto) {
		texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
		texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return texto;
	}

	private String getStdText(String stdTxtKey, String TextCode) {
		info("getStdText")
		try {
			String execQuery = " select dbms_lob.substr(rtrim(STD_VOLAT_1)  ||' ' || \n" +
					"                   rtrim(STD_VOLAT_2) || ' ' || \n" +
					"                   rtrim(STD_VOLAT_3) || ' ' || \n" +
					"                   rtrim(STD_VOLAT_4) || ' ' || \n" +
					"                   rtrim(STD_VOLAT_5),2000,1) NOTA\n" +
					"     from ellipse.MSF096_STD_VOLAT\n" +
					"    where Std_Text_Code = '${TextCode}'\n" +
					"      and Std_Key = '${stdTxtKey}'" +
					"       ORDER BY STD_LINE_NO";
			info(execQuery)
			int offset = 1;
			ArrayList<GroovyRowResult> aMSF096 = sql.rows(execQuery).toList();
			if (aMSF096 != null) {
				String texto = ""
				for (int i = 0; i < aMSF096.size(); i++) {
					texto += aMSF096.get(0).get("NOTA").toString();
				}
				return texto
			} else {
				return " "
			}


		} catch (Exception e) {
			info("Error: " + e.getMessage())
			return " ";
		}
		return " ";
	}

	public MSF010Rec getMSF010Rec(String tableType, String tableCode) {
		try {
			MSF010Key msf010key = new MSF010Key();
			msf010key.setTableType(tableType)
			msf010key.setTableCode(tableCode)

			MSF010Rec msf010rec = edoi.findByPrimaryKey(msf010key);
			return msf010rec;
		} catch (Exception ex) {
			return null;
		}
	}


	private String getStdTextOT(String stdTxtKey, String TextCode) {
		info("getStdText")
		try {
			String execQuery = " select dbms_lob.substr(\n" +
					"      rtrim(STD_VOLAT_1)||'&'||\n" +
					"      rtrim(STD_VOLAT_2)||'&'||\n" +
					"      rtrim(STD_VOLAT_3)||'&'||\n" +
					"      rtrim(STD_VOLAT_4)||'&'||\n" +
					"      rtrim(STD_VOLAT_5)||'&'\n" +
					"      ,2000,1) NOTA\n" +
					"     from ellipse.MSF096_STD_VOLAT\n" +
					"    where Std_Text_Code = '${TextCode}'\n" +
					"      and Std_Key = '${stdTxtKey}'" +
					"       ORDER BY STD_LINE_NO";
			info(execQuery)
			int offset = 1;
			ArrayList<GroovyRowResult> aMSF096 = sql.rows(execQuery).toList();
			String nota = "";
			for (int i = 0; i < aMSF096.size(); i++) {
				nota += aMSF096.get(i).get("NOTA").toString().replace(".HEADING", "");
			}
			return nota
		} catch (Exception e) {
			info("Error: " + e.getMessage())
			return " ";
		}
		return " ";
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
}

class SendEmail {
	/*
	 * IMPORTANT!
	 * Update this Version number EVERY push to GIT
	 */
	private version = 4
	private String subject;
	private String emailTo;
	private String emailCC;
	private ArrayList mailMessage;
	private ArrayList pathNameList = new ArrayList();
	private String emailFrom;
	private String host;
	private boolean isHtml;
	private boolean copyCC

	public SendEmail(String host, String subject, String emailTo, ArrayList mailMessage) {
		this.subject = subject
		this.emailTo = emailTo
		this.mailMessage = mailMessage
		this.pathNameList.add("")
		this.emailFrom = ""
		this.host = host
		this.isHtml = false
		this.copyCC = false
	}

	public SendEmail(String host, String subject, String emailTo, ArrayList mailMessage, String pathName) {
		this(host, subject, emailTo, mailMessage)
		this.pathNameList.add(pathName)
	}

	public SendEmail(String host, String subject, String emailTo, ArrayList mailMessage, String pathName, String emailFrom) {
		this(host, subject, emailTo, mailMessage)
		this.emailFrom = emailFrom
		this.pathNameList.add(pathName)

	}

	public SendEmail(String host,String subject, String emailTo, ArrayList mailMessage, String emailCC, boolean copyCC) {
		this(host, subject, emailTo, mailMessage)
		this.emailCC = emailCC
		this.copyCC = copyCC
		this.pathNameList.add("")
		this.emailFrom = ""
		this.host = host
		this.isHtml = false
	}

	public SendEmail(String subject, String emailTo, ArrayList mailMessage, String pathName, String emailFrom, String host) {
		this(host, subject, emailTo, mailMessage, emailFrom)
		this.host = host
		this.pathNameList.add(pathName)
	}

	public SendEmail(String subject, String emailTo, ArrayList mailMessage, String pathName, String emailFrom, String host, boolean isHtml) {
		this(host, subject, emailTo, mailMessage, emailFrom, host)
		this.isHtml = isHtml
		this.pathNameList.add(pathName)
	}

	public SendEmail(String host, String subject, String emailTo, ArrayList mailMessage, ArrayList pathName) {
		this(host, subject, emailTo, mailMessage)
		this.pathNameList = pathName
	}

	public SendEmail(String host, String subject, String emailTo, ArrayList mailMessage, ArrayList pathName, String emailFrom) {
		this(host, subject, emailTo, mailMessage)
		this.emailFrom = emailFrom
		this.pathNameList = pathName
	}

	public SendEmail(String subject, String emailTo, ArrayList mailMessage, ArrayList pathName, String emailFrom, String host) {
		this(host, subject, emailTo, mailMessage, emailFrom)
		this.host = host
		this.pathNameList = pathName
	}

	public SendEmail(String subject, String emailTo, ArrayList mailMessage, ArrayList pathName, String emailFrom, String host, boolean isHtml) {
		this(subject, emailTo, mailMessage, emailFrom, host)
		this.isHtml = isHtml
		this.pathNameList = pathName
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public boolean isCopyCC() {
		return copyCC;
	}

	public void setCopyCC(boolean copyCC) {
		this.copyCC = copyCC;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public void setIsHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}
	
	public void sendMail() {
		info("sendMail")

		info("Get system properties")

		Properties properties = System.getProperties();

		info("Setup mail server if it pass as an input parameter")

		//host = "10.100.63.129";

		info("host: " + host);

		if (!host.trim().equals("")) {
			properties.setProperty("mail.transport.protocol", "smtp");
			properties.setProperty("mail.host", host);
		}

		info("Get the default Session object.")
		Session session = Session.getDefaultInstance(properties, null)
		
		info("Create a default MimeMessage object.")
		
		MimeMessage message = new MimeMessage(session);

		info("Set Email From if it is pass as an input parameter.")

		if (!emailFrom.trim().equals("")) {
				message.setFrom(new InternetAddress(emailFrom));
		}
		info("Set To: header field of the header.")
		List<String> emailToList = Arrays.asList(emailTo.split(";"))
		for (String singleEmailTo : emailToList) {
				info("emailTo: " + emailTo)
				message.addRecipient(Message.RecipientType.TO,
								new InternetAddress(singleEmailTo))
		}
		//modificado
		info("CopyCC?: " + copyCC)
		if (copyCC) {

				List<String> emailCCList = Arrays.asList(emailCC.split(";"))
				for (String singleEmailTo : emailCCList) {
						info("emailCC: " + emailCC)
						
						if (singleEmailTo != null && singleEmailTo.size() > 0){
								message.addRecipient(Message.RecipientType.CC,
												new InternetAddress(singleEmailTo))
						}
				}

		}
		//modificado
		info(" Set Subject: header field")
		message.setSubject(subject);

		info("Create the message part")
		MimeBodyPart messagePart = new MimeBodyPart()
		String tempString = ""

		info("Fill the message")
		String newLineChar = "\n"
		if (this.isHtml) {
				newLineChar = "<br>"
		}
		tempString = StringUtils.join(mailMessage, newLineChar)
		if (this.isHtml) {
				tempString = wrapTextToHTML(tempString)
		}
		messagePart.setContent(tempString, "text/html")
		info("Create a multipar message")
		Multipart multipart = new MimeMultipart();
		info("Set text message part")
		multipart.addBodyPart(messagePart);

		info("Part two is attachment")
		if (pathNameList.size() > 0) {
				info("pathNameList: " + pathNameList.toArray())
				pathNameList.each { field ->
						info("field: " + field)
						if (!field.toString().trim().equals("")) {
								MimeBodyPart attachmentPart = new MimeBodyPart();
								FileDataSource fds = new FileDataSource(field.toString());
								attachmentPart.setDataHandler(new DataHandler(fds));
								attachmentPart.setFileName(fds.getName());
								multipart.addBodyPart(attachmentPart);
						}
				}
				info("Send the complete message parts")
				message.setContent(multipart);
		}
		info("Send message")
		Transport.send(message);
		info("Sended message")
	}

	private String wrapTextToHTML(String text) {
		info("wrapTextToHTML")
		StringBuffer sb = new StringBuffer();
		text = text.replaceAll("\r", "<br>");
		text = text.replaceAll("\n", "<br>");
		//text = text.replaceAll(" ", "&nbsp;");
		sb.append("<html>");
		sb.append("<body style=\"font-family:Courier New; font-size:14px; \">");
		sb.append(text)
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	public void info(String value) {
		def logObject = LoggerFactory.getLogger(getClass());
		logObject.info("------------- " + value);
		logObject.info("#######################################################################################");
	}

}

ProcessCobema process = new ProcessCobema();
process.runBatch(binding);