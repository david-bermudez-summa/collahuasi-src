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
import com.mincom.ellipse.ejra.mso.MsoErrorMessage
import com.mincom.ellipse.hook.hooks.MSOHook
import com.mincom.enterpriseservice.ellipse.ConnectionId
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.WarningMessageDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceException
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import com.mincom.ellipse.client.connection.ConnectionHolder
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.slf4j.LoggerFactory

import java.text.DecimalFormat

/**
 * 2018-09-22 DBERMUDEZ Codificacion Incial 
 */


public class MSM200E extends MSOHook {
	
	String version = "1"
	
	@Override
	public GenericMsoRecord onDisplay(GenericMsoRecord screen) {
		log.info("onDisplay	");
		log.info("Version " + version);
	}
	
	@Override
	public GenericMsoRecord onPreSubmit(GenericMsoRecord screen) {
		log.info("onPreSubmit");
	}
	
	@Override
	public GenericMsoRecord onPostSubmit(GenericMsoRecord input, GenericMsoRecord result)
	{
		log.info("onPostSubmit");
		String prevMapName = input.getMapname();
		String nextMapName = result.getMapname();
		String prevSupplierNo = "";
		String nextSupplierNo = "";
		
		
		try{
			prevSupplierNo = input.getField("SUPPLIER_NO5I").getValue();
			nextSupplierNo = result.getField("SUPPLIER_CODE1I").getValue();
		} catch (Exception ex){ }
		
		log.info("onPostSubmit " + prevMapName + nextMapName + prevSupplierNo + nextSupplierNo);
		
		if (prevMapName.equals("MSM200E") && nextMapName.equals("MSM20DA")){
			if (prevSupplierNo.equals("******") && !nextSupplierNo.equals("******")){
				saveTempValues(nextSupplierNo);
			}
		}
		
		return null;
	}
	
	private void saveTempValues(String supplierNo){
		ConnectionId id = ConnectionHolder.getConnectionId();
		log.info("CONX: " + id.getId());
		Constraint cDataType = MSF012Key.dataType.equalTo("C");
		Constraint cKeyValue = MSF012Key.keyValue.like(id.getId() + "%");
		
		QueryImpl query = new QueryImpl(MSF012Rec.class).and(cDataType).and(cKeyValue);
		tools.edoi.search(query,{ MSF012Rec msf012rec ->
			log.info(msf012rec.getPrimaryKey().getKeyValue());
			saveLegalrepresentativeData(supplierNo, 
				msf012rec.getPrimaryKey().getKeyValue().substring(36,39),
				msf012rec.getDataArea().trim())
			tools.edoi.delete(msf012rec);
		})
	}
	
	public void saveLegalrepresentativeData(String supplierNo, String refNo, String refCode){
		log.info("saveTempLegalrepresentativeData");
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