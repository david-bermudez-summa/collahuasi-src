package mso266

import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Key
import com.mincom.ellipse.edoi.ejb.msf071.MSF071Rec
import com.mincom.ellipse.edoi.ejb.msf200.MSF200Key
import com.mincom.ellipse.edoi.ejb.msf200.MSF200Rec
import com.mincom.ellipse.edoi.ejb.msf260.MSF260Key
import com.mincom.ellipse.edoi.ejb.msf260.MSF260Rec
import com.mincom.ellipse.edoi.ejb.msf26a.MSF26AKey
import com.mincom.ellipse.edoi.ejb.msf26a.MSF26ARec
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Key
import com.mincom.ellipse.edoi.ejb.msf384.MSF384Rec
import com.mincom.ellipse.edoi.ejb.msf660.MSF660Key
import com.mincom.ellipse.edoi.ejb.msf660.MSF660Rec
import com.mincom.ellipse.ejra.mso.GenericMsoRecord
import com.mincom.ellipse.ejra.mso.MsoErrorMessage
import com.mincom.ellipse.eroi.linkage.mssfxc.MSSFXCLINK
import com.mincom.ellipse.hook.hooks.MSOHook
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.Constraint
import com.mincom.eql.impl.QueryImpl
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
/*
 * Creted by Summa Consulting 2018
 * DBERMUDEZ 02/10/2018: Se adiciona validacion para mostra el proveedor suspendido.(v3)
 * DBERMUDEZ 09/07/2018: Se adiciona validacion para no permitir el ingreso
 * .................... de una segunda factura para un proveedor tipo 'PP'. (v2)
 * JMORALES Codigo inicial. 5/14/2016(v1).
 *
 * */

public class MSM266A extends MSOHook {
	public Sql sql;
	String Version = "Version 3.0"
	@Override
	public GenericMsoRecord onPreSubmit(GenericMsoRecord screen) {
		log.info (Version);
		log.info("nextAction###: " + screen.nextAction)
		if (!screen.nextAction.equals(GenericMsoRecord.F3_KEY)) {
			BigDecimal valor_sobrante
			BigDecimal total_budget
			BigDecimal total_actual
			BigDecimal total_trans
			BigDecimal total_compromisos
			BigDecimal total_pantalla
			BigDecimal valor_tolerancia
			BigDecimal valor_max
			int porcentaje_tolerancia

			String ProjectNumber = " "
			String Distrito = " "
			String ExtInvNo = " "
			String Supplier = " "
			String InvDate = " "
			String CurrencyInd = " "

			ExtInvNo = screen.getField("INV_NO1I").getValue().toString();
			Supplier = screen.getField("SUPPLIER_NO1I").getValue().toString();
			Distrito = screen.getField("DSTRCT_CODE1I").getValue().toString();
			CurrencyInd = screen.getField("CURRENCY_TYPE1I").getValue().toString();
			InvDate = screen.getField("INV_DATE1I").getValue().toString();

			log.info("ExtInvNo###: " + ExtInvNo)
			log.info("Supplier###: " + Supplier)
			log.info("Distrito###: " + Distrito)
			log.info("CurrencyInd###: " + CurrencyInd)
			log.info("InvDate###: " + InvDate)
			for (int i = 1; i < 4; i++) {

				String PwInd = screen.getField("PROJECT_IND1I" + i).getValue().toString();
				ProjectNumber = screen.getField("WORK_ORDER1I" + i).getValue().toString();
				log.info("ProjectNumber${i}###: " + ProjectNumber)
				log.info("PwInd${i}###: " + PwInd)
				String ParenProject = ProjectNumber.padRight(8," ").substring(0, 6) + "00";
				log.info("ParenProject###: " + ParenProject)
				if (PwInd.equals("P") && ProjectNumber.length() > 0 && ProjectNumber != null) {

					MSF660Rec msf660Rec
					try {
						msf660Rec = tools.edoi.findByPrimaryKey(new MSF660Key(dstrctCode: "${Distrito}", projectNo: "${ParenProject}"))
					} catch (Exception e) {
						log.info("ProjectNumber###: " + ProjectNumber + " No encontrado")
					}
					if (msf660Rec != null) {
						log.info("CapitalSw###: " + msf660Rec.getCapitalSw())

						if (msf660Rec.getCapitalSw().equals("Y")) {

							MSF071Rec msf071Rec

							try {
								msf071Rec = tools.edoi.findByPrimaryKey(new MSF071Key(entityType: "PRJ", entityValue: "${msf660Rec.getPrimaryKey().getDstrctCode() + msf660Rec.getPrimaryKey().getProjectNo()}", refNo: "012", seqNum: "001"))
							} catch (Exception e) {
								log.info("msf071Rec###: " + ProjectNumber + " No encontrado")
							}

							if (msf071Rec != null) {
								log.info("msf071Rec.getRefCode().substring(0, 1)###: " + msf071Rec.getRefCode().substring(0, 1))

								if (msf071Rec.getRefCode().substring(0, 1).equals("Y")) {

									MSF071Rec msf071Rec1

									try {
										msf071Rec1 = tools.edoi.findByPrimaryKey(new MSF071Key(entityType: "PRJ", entityValue: "${msf660Rec.getPrimaryKey().getDstrctCode() + msf660Rec.getPrimaryKey().getProjectNo()}", refNo: "013", seqNum: "001"))
									} catch (Exception e) {
										log.info("msf071Rec1###: " + ProjectNumber + " No encontrado")
									}
									if (msf071Rec1 != null) {
										porcentaje_tolerancia = Integer.parseInt(msf071Rec1.getRefCode().substring(0, 2));
										log.info("porcentaje_tolerancia###: " + porcentaje_tolerancia)
										MSF071Rec msf071Rec2
										try {
											msf071Rec2 = tools.edoi.findByPrimaryKey(new MSF071Key(entityType: "PRJ", entityValue: "${msf660Rec.getPrimaryKey().getDstrctCode() + msf660Rec.getPrimaryKey().getProjectNo()}", refNo: "014", seqNum: "001"))
										} catch (Exception e) {
											log.info("msf071Rec1###: " + ProjectNumber + " No encontrado")
										}
										if (msf071Rec2 != null) {
											valor_tolerancia = new BigDecimal(msf071Rec2.getRefCode().substring(0, 5));
											log.info("valor_tolerancia###: " + valor_tolerancia)

											ArrayList<GroovyRowResult> resultTotalTrans = TotalTrans(ProjectNumber, msf660Rec.getPrimaryKey().getDstrctCode())
											ArrayList<GroovyRowResult> resultTotalbudget = Totalbudget(msf660Rec.getPrimaryKey().getProjectNo(), msf660Rec.getPrimaryKey().getDstrctCode())
											ArrayList<GroovyRowResult> resultTotalactual = Totalactual(msf660Rec.getPrimaryKey().getProjectNo(), msf660Rec.getPrimaryKey().getDstrctCode())
											ArrayList<GroovyRowResult> resultTotalCompromisos = TotalCompromisos(msf660Rec.getPrimaryKey().getProjectNo(), msf660Rec.getPrimaryKey().getDstrctCode())


											log.info("resultTotalTrans###: " + resultTotalTrans.size())
											log.info("resultTotalbudget###: " + resultTotalbudget.size())
											log.info("resultTotalactual###: " + resultTotalactual.size())
											log.info("resultTotalCompromisos###: " + resultTotalCompromisos.size())

											if (resultTotalTrans.size() > 0) {
												total_trans = new BigDecimal(resultTotalTrans.get(0).get("total_trans").toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
											} else {
												total_trans = new BigDecimal(BigInteger.ZERO)
											}
											if (resultTotalbudget.size() > 0) {
												total_budget = new BigDecimal(resultTotalbudget.get(0).get("total_budget").toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
											} else {
												total_budget = new BigDecimal(BigInteger.ZERO)
											}
											if (resultTotalactual.size() > 0) {
												total_actual = new BigDecimal(resultTotalactual.get(0).get("total_actual").toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
											} else {
												total_actual = new BigDecimal(BigInteger.ZERO)
											}
											if (resultTotalCompromisos.size() > 0) {
												total_compromisos = new BigDecimal(resultTotalCompromisos.get(0).get("total_compromisos").toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
											} else {
												total_compromisos = new BigDecimal(BigInteger.ZERO)
											}


											log.info("total_trans###: " + total_trans)
											log.info("total_actual###: " + total_actual)
											log.info("total_budget###: " + total_budget)
											log.info("total_compromisos###: " + total_compromisos)

											String InvItemValue = screen.getField("INV_ITEM_VALUE1I" + i).getValue().toString();

											if (CurrencyInd.equals(tools.commarea.LocalCurrency)) {

												if (!InvItemValue.isNumber()) {
													total_pantalla = 0;
												} else {
													total_pantalla = new BigDecimal(InvItemValue.trim())
												}

											} else {
												MSSFXCLINK mssfxclink = tools.eroi.execute("MSSFXC", { MSSFXCLINK mssfxclink ->

													mssfxclink.setOptionFxc("1")

													if (!InvItemValue.isNumber()) {
														mssfxclink.setInputValue(0)
													} else {
														mssfxclink.setInputValue(new BigDecimal(InvItemValue.trim()))
													}
													mssfxclink.setConvTypeSw("F")
													mssfxclink.setRateTypeSw("B")
													mssfxclink.setLocalCurrency(tools.commarea.LocalCurrency);
													mssfxclink.setForeignCurr(CurrencyInd);
													mssfxclink.setTranDate(InvDate);

												});
												total_pantalla = mssfxclink.getFlValueConv().setScale(2, BigDecimal.ROUND_HALF_UP);

											}

											valor_sobrante = (total_budget - total_actual - total_trans - total_compromisos - total_pantalla).setScale(2, BigDecimal.ROUND_HALF_UP);

											log.info("total_pantalla###: " + total_pantalla)
											log.info("valor_sobrante###: " + valor_sobrante)

											if (valor_sobrante < 0) {

												valor_max = ((total_budget.setScale(2, BigDecimal.ROUND_HALF_UP) * porcentaje_tolerancia).setScale(2, BigDecimal.ROUND_HALF_UP) / 100).setScale(2, BigDecimal.ROUND_HALF_UP)
												BigDecimal limite = 0;

												if (valor_max < valor_tolerancia) {
													limite = valor_max;
												} else {
													limite = valor_tolerancia
												}

												valor_sobrante = valor_sobrante.multiply(-1);

												log.info("valor_sobrante###: " + valor_sobrante)
												log.info("valor_max###: " + valor_max)
												log.info("limite###: " + limite)


												if (valor_sobrante > limite) {

													throw new EnterpriseServiceOperationException(
															new ErrorMessageDTO("C953", " EL PROYECTO HA EXCEDIDO EL PRESUPUESTO (" + ProjectNumber + ")", " ", 0, 0));

												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

		}
		
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
	
	}

	@Override
	public GenericMsoRecord onPostSubmit(GenericMsoRecord input, GenericMsoRecord result) {
		return result;
	}
	public ArrayList<GroovyRowResult> TotalTrans(String project_no, String dstrct_code) {
		sql = new Sql(tools.dataSource);
		String querySaf3a0 = "select nvl(sum(tran_amount),0)\n" +
				"     total_trans\n" +
				"     from MSF900 where\n" +
				"     dstrct_code = '${dstrct_code}'\n" +
				"     and process_date >= to_char(sysdate-1, 'YYYYMMDD')\n" +
				"     and rec900_type in ('C','H','I','K','M','P','Q','S')\n" +
				"     and posted_status in ('N','O','U','V')\n" +
				"     and project_no = '${project_no}'";
		log.info("total_trans: " + querySaf3a0)
		ArrayList<GroovyRowResult> result = sql.rows(querySaf3a0).toList();
		return result;
	}

	public ArrayList<GroovyRowResult> Totalbudget(String project_no, String dstrct_code) {
		sql = new Sql(tools.dataSource);
		String querySaf3a0 = "select  nvl(sum(tot_est_cost),0)\n" +
				"       total_budget\n" +
				"       from MSF667\n" +
				"       where dstrct_code = '${dstrct_code}'\n" +
				"       and project_no = '${project_no}'\n" +
				"       and budget_code in ('01  ','SFC1','SFC2','SFC3')\n" +
				"       and revsd_period = '000000'\n" +
				"       and category_code = ' '\n" +
				"       and exp_rev_ind = 'E'";
		log.info("total_budget: " + querySaf3a0)
		ArrayList<GroovyRowResult> result = sql.rows(querySaf3a0).toList();
		return result;
	}

	public ArrayList<GroovyRowResult> Totalactual(String project_no, String dstrct_code) {
		sql = new Sql(tools.dataSource);
		String querySaf3a0 = "select nvl(tot_act_cost,0)" +
				" total_actual" +
				" from MSF668" +
				" where dstrct_code = '${dstrct_code}'" +
				" and project_no = '${project_no}'" +
				" and revsd_period = '000000'" +
				" and wo_proj_ind = ' '" +
				" and work_order = ' '" +
				" and category_code = ' '" +
				" and exp_rev_ind = 'E'"
		log.info("total_actual: " + querySaf3a0)
		ArrayList<GroovyRowResult> result = sql.rows(querySaf3a0).toList();
		return result;
	}

	public ArrayList<GroovyRowResult> TotalCompromisos(String project_no, String dstrct_code) {
		sql = new Sql(tools.dataSource);
		String querySaf3a0 = "select nvl(sum(serv_ord_comms + iss_req_comms  + preq_comms + contract_comms),0) total_compromisos" +
				" from MSF665_DATA_665_C" +
				" where level_type_665 = 'TL'" +
				" and dstrct_code = '${dstrct_code}'" +
				" and project_no ='${project_no}'" +
				" and revsd_period = '000000'" +
				" and summary_code  = ' '" +
				" and rec665_type   = 'C'";
		log.info("total_compromisos:  " + querySaf3a0)
		ArrayList<GroovyRowResult> result = sql.rows(querySaf3a0).toList();
		return result;
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