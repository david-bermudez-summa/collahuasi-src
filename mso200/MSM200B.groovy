package mso200;

import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Key
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Rec
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Rec
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Key
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Rec
import com.mincom.ellipse.ejra.mso.GenericMsoRecord
import com.mincom.ellipse.ejra.mso.InitialMsoRecord
import com.mincom.ellipse.ejra.mso.MsoErrorMessage
import com.mincom.ellipse.hook.hooks.MSOHook
import com.mincom.enterpriseservice.ellipse.ConnectionId
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.WarningMessageDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceException
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import groovy.sql.GroovyRowResult
import com.mincom.ellipse.client.connection.ConnectionHolder
import groovy.sql.Sql
import org.slf4j.LoggerFactory

import java.text.DecimalFormat

/**
 *  2018-09-22 DBERMUDEZ Si quita los Warnings y se guardan los datos en la MSF012 con el SCROLL-KEY
 *  .................... Para luego guardarlo en la pantalla MSM200E
 *  2018-09-05 DBERMUDEZ Despliegue de warnings v3
 *  2018-07-31 DBERMUDEZ Despliegue en pantalla del nombre del solitante
 *  2018-07-01 DBERMUDEZ Codificacion Incial
 *  .......... Manejo de informacion de los representantes legales del proveedor
 */
class MSM200B extends MSOHook {

	String version = "4"
	@Override
	public GenericMsoRecord onDisplay(GenericMsoRecord screen) {
		log.info("onDisplay");
		log.info(version)
		
		displayLegalRepresentativeData(screen)
		
		if (screen.getCurrentCursorField().getName().equals("DELETE_CONF2I")){
			screen.getField("DELETE_CONF2I").setValue("Y");
			screen.getField("ORDER_EMAIL_L12I").setIsProtected(false);
			screen.getField("ORDER_EMAIL_L12I").setValue("...");
			
		} else {
			screen.getField("DELETE_CONF2I").setIsProtected(true);
			screen.getField("CONFIRM_LIT2I").setValue("...");
		}
		
		return screen;
	}

	@Override
	public GenericMsoRecord onPreSubmit(GenericMsoRecord screen) {
		String supplierNo = screen.getField("SUPPLIER_NO2I").getValue();
		
		if (screen.getNextAction() != GenericMsoRecord.F3_KEY){
			saveLegalrepresentativeData(supplierNo, "001", screen.getCustomFieldValue("Rut1"))
			saveLegalrepresentativeData(supplierNo, "002", screen.getCustomFieldValue("Nombre1"))
			saveLegalrepresentativeData(supplierNo, "003", screen.getCustomFieldValue("Rut2"))
			saveLegalrepresentativeData(supplierNo, "004", screen.getCustomFieldValue("Nombre2"))
			saveLegalrepresentativeData(supplierNo, "005", screen.getCustomFieldValue("Solicitante"))
		
			
		}
		screen = null;
		
		return screen;
	}

	@Override
	public GenericMsoRecord onPostSubmit(GenericMsoRecord input, GenericMsoRecord result) {
		log.info("onPostSubmit");
		
		return null;
	}
	
	public displayLegalRepresentativeData(GenericMsoRecord screen){
		log.info("displayLegalRepresentativeData");
		String supplierNo = screen.getField("SUPPLIER_NO2I").getValue();
		//Inputs
		if (supplierNo.trim().length() > 0 && !supplierNo.equals("******")){
			screen.setCustomFieldValue("Rut1",        getRefCode(supplierNo, "001"))
			screen.setCustomFieldValue("Nombre1",     getRefCode(supplierNo, "002"))
			screen.setCustomFieldValue("Rut2",        getRefCode(supplierNo, "003"))
			screen.setCustomFieldValue("Nombre2",     getRefCode(supplierNo, "004"))
			String employeeId = getRefCode(supplierNo, "005")
			screen.setCustomFieldValue("Solicitante", employeeId)
			
			screen.setCustomFieldValue("NombreSolicitante", getEmployeeName(employeeId))
		}
	}
	
	public String getEmployeeName(String employeeId){
		try{
			MSF810Key msf810key = new MSF810Key();
			msf810key.setEmployeeId(employeeId);
			
			MSF810Rec msf810rec = tools.edoi.findByPrimaryKey(msf810key);
			String name;
			
			if (msf810rec.getFirstName() != null){
				name = msf810rec.getFirstName()
			} 
			
			if (msf810rec.getSecondName() != null){
				name += " " + msf810rec.getSecondName()
			}
			
			if (msf810rec.getSurname() != null){
				name += " " + msf810rec.getSurname()
			}
			
			if (msf810rec.getThirdName() != null){
				name += " " + msf810rec.getThirdName()
			}
			
			return name
		} catch (Exception ex){
			return " ";
		}
			
	}
	
	public String getRefCode(String supplierNo, String refNo){
		log.info("getRefCode");
		try{
			MSF071Key msf071key = new MSF071Key();
			msf071key.setEntityType("+RL")
			msf071key.setEntityValue(supplierNo)
			msf071key.setRefNo(refNo)
			msf071key.setSeqNum("001");
			
			MSF071Rec msf071rec = tools.edoi.findByPrimaryKey(msf071key);
			
			return msf071rec.getRefCode().trim();
		} catch (Exception ex){
			return "";
		}
		
	}
	
	public void saveLegalrepresentativeData(String supplierNo, String refNo, String refCode){
		log.info("saveTempLegalrepresentativeData " + supplierNo);
		if (supplierNo.trim().equals("")){
			ConnectionId id = ConnectionHolder.getConnectionId();
			log.info("CONX: " + id.getId())
			MSF012Key msf012key = new MSF012Key();
			msf012key.setDataType("C")
			msf012key.setKeyValue(id.getId()+refNo);
			MSF012Rec msf012rec = new MSF012Rec();
			msf012rec.setPrimaryKey(msf012key)
			msf012rec.setDataArea(refCode);
				
			try{
				tools.edoi.create(msf012rec)
			} catch (Exception ex){
				log.info(ex.getMessage())
				try{
					tools.edoi.update(msf012rec);
				} catch (Exception ex2){
					log.info(ex2.getMessage())
				}
			}
		} else {		
			if (refCode != null && supplierNo.trim().length() > 0){
				MSF071Key msf071key = new MSF071Key();
				msf071key.setEntityType("+RL")
				msf071key.setEntityValue(supplierNo)
				msf071key.setRefNo(refNo)
				msf071key.setSeqNum("001");
				
				MSF071Rec msf071rec = new MSF071Rec();
				msf071rec.setPrimaryKey(msf071key);
				msf071rec.setLastModDate(tools.commarea.TodaysDate)
				msf071rec.setLastModTime(tools.commarea.Time)
				msf071rec.setLastModUser(tools.commarea.UserId)
				msf071rec.setRefCode(refCode)
				
				try{
					tools.edoi.create(msf071rec)
				} catch (Exception ex){
					try{
						tools.edoi.update(msf071rec);
					} catch (Exception ex2){
					}
				}
			}
		}
	}
}