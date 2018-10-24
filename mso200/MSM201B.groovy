package mso200;

import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Rec
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Key
import com.mincom.ellipse.edoi.ejb.msf810.MSF810Rec
import com.mincom.ellipse.ejra.mso.GenericMsoRecord
import com.mincom.ellipse.ejra.mso.MsoErrorMessage
import com.mincom.ellipse.hook.hooks.MSOHook
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.WarningMessageDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceException
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.slf4j.LoggerFactory

import java.text.DecimalFormat

/**
 *  Created by SUMMA CONSULTING S.A.S on 2018-07-01
 *  2018-07-31 DBERMUDEZ Codificacion Incial
 *  .......... Manejo de informacion de los representantes legales del proveedor
 */
class MSM201B extends MSOHook {

	String version = "20180731 - 1"
	@Override
	public GenericMsoRecord onDisplay(GenericMsoRecord screen) {
		log.info("onDisplay");
		log.info(version)
		displayLegalRepresentativeData(screen)
		return screen;
	}

	@Override
	public GenericMsoRecord onPreSubmit(GenericMsoRecord screen) {
		log.info("onPreSubmit");
		String supplierNo = screen.getField("SUPPLIER_NO2I").getValue();
		/*
		saveLegalrepresentativeData(supplierNo, "001", screen.getCustomFieldValue("Rut1"))
		saveLegalrepresentativeData(supplierNo, "002", screen.getCustomFieldValue("Nombre1"))
		saveLegalrepresentativeData(supplierNo, "003", screen.getCustomFieldValue("Rut2"))
		saveLegalrepresentativeData(supplierNo, "004", screen.getCustomFieldValue("Nombre2"))
		saveLegalrepresentativeData(supplierNo, "005", screen.getCustomFieldValue("Solicitante"))
		*/
		return null;
	}

	@Override
	public GenericMsoRecord onPostSubmit(GenericMsoRecord input, GenericMsoRecord result) {
		log.info("onPostSubmit");
		return null;
	}
	
	public void info(String value) {
		def logObject = LoggerFactory.getLogger(getClass());
		logObject.info("------------- " + value)
	}
	
	public displayLegalRepresentativeData(GenericMsoRecord screen){
		log.info("displayLegalRepresentativeData");
		String supplierNo = screen.getField("SUPPLIER_NO2I").getValue();
		//Inputs
		screen.setCustomFieldValue("Rut1",        getRefCode(supplierNo, "001"))
		screen.setCustomFieldValue("Nombre1",     getRefCode(supplierNo, "002"))
		screen.setCustomFieldValue("Rut2",        getRefCode(supplierNo, "003"))
		screen.setCustomFieldValue("Nombre2",     getRefCode(supplierNo, "004"))
		String employeeId = getRefCode(supplierNo, "005")
		screen.setCustomFieldValue("Solicitante", employeeId)
		
		screen.setCustomFieldValue("NombreSolicitante", getEmployeeName(employeeId))
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
		log.info("saveLegalrepresentativeData");
		if (refCode != null){
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
				info(ex.message);
				try{
					tools.edoi.update(msf071rec);
				} catch (Exception ex2){
					info(ex2.message);
				}
			}
		}
	}
}