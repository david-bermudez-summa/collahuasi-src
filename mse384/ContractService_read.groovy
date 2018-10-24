package mse384

import com.mincom.ellipse.attribute.Attribute
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf020.MSF020Key
import com.mincom.ellipse.edoi.ejb.msf020.MSF020Rec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38AKey
import com.mincom.ellipse.edoi.ejb.msf38a.MSF38ARec
import com.mincom.ellipse.edoi.ejb.msf870.MSF870Key
import com.mincom.ellipse.edoi.ejb.msf870.MSF870Rec
import com.mincom.ellipse.edoi.ejb.msf875.MSF875Key
import com.mincom.ellipse.edoi.ejb.msf875.MSF875Rec
import com.mincom.ellipse.edoi.ejb.msf878.MSF878Key
import com.mincom.ellipse.edoi.ejb.msf878.MSF878Rec
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceReadReplyDTO
import com.mincom.enterpriseservice.ellipse.contract.ContractServiceReadRequestDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import java.beans.beancontext.BeanContextServiceProviderBeanInfo
import java.text.DecimalFormat

import org.apache.xalan.templates.ElemValueOf
import org.springframework.context.i18n.LocaleContextHolder

/**
 * DBERMUDEZ 14/08/2018 .......... Validacion para que solo los responsables de los contratos deben poder ingresar a 
 * ............................... revisar la información de los contratos asociados a ello. (v4)
 * DBERMUDEZ 14/07/2018 .......... Despliegue de los porcentajes y monto de las obligaciones de cumpliemiento.
 * ............................... de las boletas de garantia. (v3)
 * Modified by JAFETH on 7/23/2016 Modificacion Totales de las boletas de garantias.(v2)
 * Created by JAFETH on 5/27/2016 Visualizacion de monto a aprobar de Variacion contrato MSEAPM (v1).
 */
public class ContractService_read extends ServiceHook {

	public String Version = "v4";
	
    @Override
    public Object onPostExecute(Object input, Object result) {
		log.info(Version);
		log.info("onPostExecute")
		
		ContractServiceReadReplyDTO resultDTO = (ContractServiceReadReplyDTO) result
		
		ArrayList<Attribute> attList = new ArrayList<Attribute>();

        Constraint c1 = MSF38AKey.contractNo.equalTo(resultDTO.getContractNo())
        def query = new QueryImpl(MSF38ARec.class).and(c1)
        BigDecimal TotalValue = new BigDecimal(BigDecimal.ZERO)
        BigDecimal TotalRelease = new BigDecimal(BigDecimal.ZERO)
        BigDecimal BalanceRemaining = new BigDecimal(BigDecimal.ZERO)
        tools.edoi.search(query, { MSF38ARec msf38ARec ->
			log.info("SecurDepFrm###: " + msf38ARec.getSecurDepFrm())
			log.info("SecurLogAmount###: " + msf38ARec.getSecurLogAmount())
            if (msf38ARec.getSecurDepFrm().trim().equals("A") || msf38ARec.getSecurDepFrm().trim().equals("S") || msf38ARec.getSecurDepFrm().trim().equals("D")) {
                TotalValue = TotalValue.add(msf38ARec.getSecurLogAmount())
            }

            if (msf38ARec.getSecurDepFrm().trim().equals("D")) {
                TotalRelease = TotalRelease.add(msf38ARec.getSecurLogAmount())
            }

            if (msf38ARec.getSecurDepFrm().trim().equals("A") || msf38ARec.getSecurDepFrm().trim().equals("S")) {
                BalanceRemaining = BalanceRemaining.add(msf38ARec.getSecurLogAmount())
            }

        });

        resultDTO.setSecurityTotalRelease(TotalRelease)
        resultDTO.setSecurityTotalBalance(BalanceRemaining)
        resultDTO.setSecurityTotalValue(TotalValue)
		//Condicion
		if (!canUserViewContract(resultDTO)){
			resultDTO = new ContractServiceReadReplyDTO();
			resultDTO.setContractNo("::ERROR:")
			return resultDTO;
		}
		
        DecimalFormat formateador = new DecimalFormat("###,###.00");
        BigDecimal Total = resultDTO.getOriginalContractPrice() + resultDTO.getUnApprAdjustmentPrice() + resultDTO.getApprovedAdjustmentsPrice();
        resultDTO.setDistrictCodeDescription(formateador.format(Total.doubleValue()))

		//Procentaje de cumplimiento
		log.info("onPostExecute - 1")
		MSF384Key msf384key = new MSF384Key();
		msf384key.setContractNo(resultDTO.getContractNo());
		MSF384Rec msf384rec = tools.edoi.findByPrimaryKey(msf384key);
		
		attList.add(setNewCustomValue("fulfillmentPrc", msf384rec.getPerfGteeVal()));
		attList.add(setNewCustomValue("fulfillmentAmt", msf384rec.getPerfGuarantor()));
		
		log.info("LOCALIZACION NAVEGADOR: "  + LocaleContextHolder.getLocale().toString())
		
		if (LocaleContextHolder.getLocale().toString().substring(0,2).equals("en")){
			resultDTO.addCustomAttribute(setNewCustomValue("fulfillmentPrc", msf384rec.getPerfGteeVal()));
			resultDTO.addCustomAttribute(setNewCustomValue("fulfillmentAmt", msf384rec.getPerfGuarantor()));
		} else{
			resultDTO.addCustomAttribute(setNewCustomValue("fulfillmentPrc", msf384rec.getPerfGteeVal().toString().replace(",", "").replace(".", ",")));
			resultDTO.addCustomAttribute(setNewCustomValue("fulfillmentAmt", msf384rec.getPerfGuarantor()));
		}
					
        return null;
    }
	
	public MSF010Rec getMSF010Rec (String tableType, String tableCode){
		try{
			MSF010Key msf010key = new MSF010Key();
			msf010key.setTableType(tableType)
			msf010key.setTableCode(tableCode)
			
			MSF010Rec msf010rec = tools.edoi.findByPrimaryKey(msf010key);
			return msf010rec;
		} catch (Exception ex){
			return null;
		}
	}
	
	private boolean canUserViewContract(ContractServiceReadReplyDTO resultDTO){
		
		String employeeId = tools.commarea.EmployeeId.trim()
		log.info("LOGEED USER:" + employeeId)
		log.info("getRespCode1 USER:" + resultDTO.getRespEmployee1())
		log.info("getRespCode2 USER:" + resultDTO.getRespEmployee2())
		log.info("getRespCode3 USER:" + resultDTO.getRespEmployee3())
		log.info("getRespCode4 USER:" + resultDTO.getRespEmployee4())
		
		if (
			resultDTO.getRespEmployee1().equals(employeeId) ||
			resultDTO.getRespEmployee2().equals(employeeId) ||
			resultDTO.getRespEmployee3().equals(employeeId) ||
			resultDTO.getRespEmployee4().equals(employeeId) ||
			isUserInTableDefined() ||
			isSupPositionInEllipseHierarchy(resultDTO)
		){
			return true;
		} else {
			return false;
		}
		
	}
	
	private boolean isUserInTableDefined(){
		
		MSF010Rec msf010rec = getMSF010Record("+CUV", tools.commarea.PositionId);
		if (msf010rec == null){
			return false;
		} else {
			return true;
		}
		
	}
	
	private boolean isSupPositionInEllipseHierarchy(ContractServiceReadReplyDTO resultDTO){
		
		/*SELECT POSITION_ID,
		POS_STOP_DATE,
		99999999 - INV_STR_DATE
	  FROM MSF878
	  WHERE EMPLOYEE_ID = '13037554-5' AND (POS_STOP_DATE = '00000000' OR POS_STOP_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD'));
	  */
		ArrayList<String> employeeList = new ArrayList<String>();
		employeeList.add(resultDTO.getRespEmployee1())
		employeeList.add(resultDTO.getRespEmployee2())
		employeeList.add(resultDTO.getRespEmployee3())
		employeeList.add(resultDTO.getRespEmployee4())
		
		Constraint cEmployeeId = MSF878Key.employeeId.in(employeeList);
		Constraint cPosStopDateZero = MSF878Key.posStopDate.equalTo("00000000");
		Constraint cPosStopDateGTDate = MSF878Key.posStopDate.greaterThanEqualTo(tools.commarea.TodaysDate);
		Constraint cPosStartDateGTDate = MSF878Key.invStrDate.greaterThan(String.valueOf(99999999 - Integer.parseInt(tools.commarea.TodaysDate)));
		
		QueryImpl query = new QueryImpl(MSF878Rec.class).and(cPosStopDateZero.and(cPosStartDateGTDate).or(cPosStopDateGTDate.and(cPosStartDateGTDate))).and(cEmployeeId);
		boolean found = false;
		tools.edoi.search(query,{MSF878Rec msf878rec ->
			found = found || checkUserHierarchy(msf878rec.getPrimaryKey().getPositionId())
		})
		
		return found;
	}
	
	private boolean checkUserHierarchy(String positionId){
		Sql sql = new Sql(tools.dataSource)
		String query = 
			"SELECT DISTINCT NIVEL, IS_LEAF, REPLACE(POSTITIONS,'/${positionId}','') FROM ("+
			"SELECT "+
			"  LEVEL AS NIVEL, "+
			"  CONNECT_BY_ISLEAF IS_LEAF, "+
			"  SYS_CONNECT_BY_PATH(POSITION_ID, '/') AS POSTITIONS "+
			"FROM MSF875 "+
			"  START WITH POSITION_ID       = '${positionId}'"+
			"  CONNECT BY PRIOR SUPERIOR_ID = POSITION_ID)"+
			"WHERE IS_LEAF = 1";
		boolean found = false;
		sql.rows(query).each{ GroovyRowResult item ->
			String hierPositions = item.getAt(2).toString();
			
			if (hierPositions.contains(tools.commarea.PositionId)){
				found = found || true;
			}
		};
		
		return found;
	}
	
	private MSF010Rec getMSF010Record(String tableType, String tableCode){
		MSF010Key msf010Key = new MSF010Key();
		MSF010Rec msf010rec = null;
		
		msf010Key.setTableType(tableType.trim().padRight(4));
		msf010Key.setTableCode(tableCode.trim().padRight(18));
		try{
			msf010rec = tools.edoi.findByPrimaryKey(msf010Key);
			return msf010rec;
		} catch (Exception ex){
			return null;
		}
	}
	
	private Attribute setNewCustomValue( String fieldName, Object value){
		Attribute att = new Attribute();
		att.setName(fieldName)
		att.setValue(value.toString());
		return att;
	}
	
	private void setCustomValue(Attribute[] atts, String fieldName, Object value){
		for (Attribute att : atts){
			if(att.getName().equals(fieldName)){
				if (value instanceof BigDecimal){
					att.setValue((BigDecimal)value);
				} else if (value instanceof String){
					att.setValue((String)value);
				}
				
				log.info(att.getName() + " : " + att.getValue())
			}
		}
	}
	
	private Object getCustomValue(Attribute[] atts, String fieldName){
		for (Attribute att : atts){
			if(att.getName().equals(fieldName)){
				return att.getValue();
			}
		}
		
		return null;
	}
}
