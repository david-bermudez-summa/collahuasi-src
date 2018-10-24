package mso260;

import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf200.MSF200Key
import com.mincom.ellipse.edoi.ejb.msf200.MSF200Rec
import com.mincom.ellipse.edoi.ejb.msf203.MSF203Key
import com.mincom.ellipse.edoi.ejb.msf203.MSF203Rec
import com.mincom.ellipse.edoi.ejb.msf260.MSF260Key
import com.mincom.ellipse.edoi.ejb.msf260.MSF260Rec
import com.mincom.ellipse.ejra.mso.GenericMsoRecord
import com.mincom.ellipse.ejra.mso.MsoErrorMessage
import com.mincom.ellipse.ejra.mso.MsoField
import com.mincom.ellipse.eroi.linkage.mssbnk.MSSBNKLINK
import com.mincom.ellipse.hook.hooks.MSOHook
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import groovy.sql.Sql

/*
 * Creted by Summa Consulting 2018
 * DBERMUDEZ 02/10/2018: Se adiciona validacion para mostra el proveedor suspendido. (v3)
 * DBERMUDEZ 09/07/2018: Se adiciona validacion para no permitir el ingreso
 * .................... de una segunda factura para un proveedor tipo 'PP'. (v2)
 * JMORALES Codigo inicial. (v1).
 *
 * */
public class MSM260A extends MSOHook {

	String Version = "Version 3.0"
	public Sql sql;
	String MNEMONIC1I = " ";

	public GenericMsoRecord onPreSubmit(GenericMsoRecord screen) {
		String MsjPantalla = screen.getMessage();
		log.info("nextAction###: " + screen.nextAction)
		MsoField field = screen.getField("MNEMONIC1I");
		MNEMONIC1I = field.getValue()
		field.setIsProtected(false);
		log.info("GenericMsoRecord.TRANSMIT_KEY###: " + GenericMsoRecord.TRANSMIT_KEY)

		screen.getField("MNEMONIC1I").setIsProtected(false);
		if (screen.nextAction.equals(GenericMsoRecord.TRANSMIT_KEY)||screen.nextAction.equals(0)) {

			log.info("MsjPantalla###: " + MsjPantalla)
			// if (MsjPantalla.contains("4493")) {

			String CurrencyInd = " "
			String DstrctCode = " ";
			String BranchCode = " ";
			String BankAcctNo = " ";
			String Supplier = " ";
			String Mnemonic = " ";

			CurrencyInd = screen.getField("CURRENCY_TYPE1I").getValue().toString().trim();
			DstrctCode = screen.getField("DSTRCT_CODE1I").getValue().toString();
			BranchCode = screen.getField("BRANCH_CODE1I").getValue().toString();
			BankAcctNo = screen.getField("BANK_ACCT_NO1I").getValue().toString();

			String PoNo = screen.getField("PO_NO1I").getValue().toString();
			Supplier = screen.getField("SUPPLIER_NO1I").getValue().toString();
			if (Supplier.trim().isNumber()) {
				Supplier = Supplier.padLeft(6, "0");
			}

			Mnemonic = screen.getField("MNEMONIC1I").getValue().toString();

			log.info("Supplier###: " + Supplier)
			log.info("Mnemonic###: " + Mnemonic)
			log.info("DstrctCode###: " + DstrctCode)
			MSF203Rec msf203Rec
			try {
				msf203Rec = tools.edoi.findByPrimaryKey(new MSF203Key(dstrctCode: DstrctCode, supplierNo: Supplier))
			} catch (Exception) {

			}
			if (msf203Rec != null) {
				MSF200Rec msf200Rec
				try {
					msf200Rec = tools.edoi.findByPrimaryKey(new MSF200Key(supplierNo: msf203Rec.getPrimaryKey().getSupplierNo()))
				} catch (Exception) {

				}

				if (!PoNo.isEmpty() && tools.commarea.CountryCode.toString().equals("CL")
						&& msf200Rec.getCountryCode().equals("CL")
						&& !msf203Rec.getTaxFileNo().equals(Mnemonic)) {

					MSF010Rec msf010Rec = tools.edoi.findByPrimaryKey(new MSF010Key(tableType: "ER", tableCode: "1644"))
					throw new EnterpriseServiceOperationException(
							new ErrorMessageDTO("9877", msf010Rec.getTableDesc(), " ", 0, 0));


				} else {
					log.info("CurrencyInd###: " + CurrencyInd)
					log.info("BranchCode###: " + BranchCode)
					log.info("BankAcctNo###: " + BankAcctNo)
					if (!BranchCode.isEmpty() && !BankAcctNo.isEmpty()) {

						if (!BranchCode.trim().equals("?") && !BankAcctNo.trim().equals("?")) {

							MSSBNKLINK mssbnklink = tools.eroi.execute("MSSBNK", { MSSBNKLINK mssbnklink ->
								mssbnklink.setOption("V")
								mssbnklink.setInpDstrctCode(DstrctCode)
								mssbnklink.setInpBranchCode(BranchCode)
								mssbnklink.setInpBankAcctNo(BankAcctNo)
							})
							log.info("CurrencyInd2###: " + mssbnklink.getCurrencyType())
							if (!mssbnklink.getCurrencyType().trim().equals(CurrencyInd.trim())) {
								MsjPantalla = " ";

								throw new EnterpriseServiceOperationException(
										new ErrorMessageDTO("7408", " TRANSACCION DEBE SER PAGADA EN " + CurrencyInd + "", " ", 0, 0));

							}
						}
					}
				}

			}
		}
		field.setValue(" ")
		field.setIsProtected(true);
		// }
		
		//Validacion Provedor Suspendido
		String errorCode = validateSuspendedSupp(screen);
		log.info("AAA" + errorCode)
		if (!screen.nextAction.equals(GenericMsoRecord.F3_KEY)) {
			if (errorCode != null) {
				screen.setErrorMessage(new MsoErrorMessage(getMSF010Rec("ER", errorCode), errorCode,
					getMSF010Rec("ER", errorCode),
					MsoErrorMessage.ERR_TYPE_ERROR,
					MsoErrorMessage.ERR_SEVERITY_UNSPECIFIED))
				return screen;
			}
		}
		
		//Validacion Provedor Pago Unico
		errorCode = validateSinglePmtSupp(screen);
		log.info("AAA" + errorCode)
		if (!screen.nextAction.equals(GenericMsoRecord.F3_KEY)) {
			if (errorCode != null) {
				screen.setErrorMessage(new MsoErrorMessage(getMSF010Rec("ER", errorCode), errorCode,
					getMSF010Rec("ER", errorCode),
					MsoErrorMessage.ERR_TYPE_ERROR,
					MsoErrorMessage.ERR_SEVERITY_UNSPECIFIED))
				return screen;
			}
		}
		
		return null;
	}
	
	@Override
	public GenericMsoRecord onDisplay(GenericMsoRecord screen) {
		MsoField field = screen.getField("MNEMONIC1I");
		log.info("nextAction###: " + screen.getNextAction())
		if (screen.nextAction.equals(GenericMsoRecord.TRANSMIT_KEY)||screen.nextAction.equals(0)) {
			field.setIsProtected(false);
			field.setValue(MNEMONIC1I)
		}

		if (screen.getNextAction().equals(3) || screen.getNextAction().equals(4)) {
			field.setValue(" ")
			MNEMONIC1I = " ";
		}
		return screen;
	}

	@Override
	public GenericMsoRecord onPostSubmit(GenericMsoRecord input, GenericMsoRecord result) {

		return null;
	}
	
	public String getMSF010Rec(String tableType, String tableCode){
		try{
			MSF010Key msf010key = new MSF010Key();
			msf010key.setTableType(tableType)
			msf010key.setTableCode(tableCode)
			
			MSF010Rec msf010rec = tools.edoi.findByPrimaryKey(msf010key);
			return msf010rec.getTableDesc();
		} catch (Exception ex){
			return "";
		}
	}
	
	public String validateSuspendedSupp(GenericMsoRecord screen){
		String supplierNo = screen.getField("SUPPLIER_NO1I").getValue();
		
		if (supplierNo != null && supplierNo.trim().length() > 0){
			MSF200Key msf200key = new MSF200Key();
			msf200key.setSupplierNo(supplierNo.padLeft(6, "0"));
			try{
				MSF200Rec msf200rec = tools.edoi.findByPrimaryKey(msf200key);
				if (msf200rec.getSupStatus().equals("7")){
					return "S002";
				} else {
					return null;
				}
			} catch (Exception ex){
				return null;
			}
		}
	}
	
	public String validateSinglePmtSupp(GenericMsoRecord screen){
		String supplierNo = screen.getField("SUPPLIER_NO1I").getValue();
		log.info(supplierNo);
		if (supplierNo != null && supplierNo.trim().length() > 0){
			log.info("1");
			MSF200Key msf200key = new MSF200Key();
			msf200key.setSupplierNo(supplierNo.padLeft(6, "0"));
			
			try{
				MSF200Rec msf200rec = tools.edoi.findByPrimaryKey(msf200key);
				log.info("2");
				if (
					msf200rec.getSupTypex1().equals("PP") ||
					msf200rec.getSupTypex2().equals("PP") ||
					msf200rec.getSupTypex3().equals("PP") ||
					msf200rec.getSupTypex4().equals("PP") ||
					msf200rec.getSupTypex5().equals("PP") ||
					msf200rec.getSupTypex6().equals("PP") ){
				
					log.info("3");
					
					if (hasInvoices(supplierNo.padLeft(6, "0"))){
						return "S001"
					}
				}
			} catch (Exception ex ){
				
			}
		}
		
		return null;
		
	}
	
	public boolean hasInvoices(supplierNo){

		Constraint cSupplierNo = MSF260Key.supplierNo.equalTo(supplierNo);
		QueryImpl query = new QueryImpl(MSF260Rec.class).and(cSupplierNo);
		
		int size = tools.edoi.search(query).results.size();
		log.info("size" + size)
		if (size > 0){
			return true;
		} else {
			return false;
		}
		
	}

}
