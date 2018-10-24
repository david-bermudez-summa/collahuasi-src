package me1tr

import com.mincom.ellipse.edoi.ejb.msf010.MSF010Key
import com.mincom.ellipse.edoi.ejb.msf010.MSF010Rec
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Key
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Rec
import com.mincom.ellipse.edoi.ejb.msf870.MSF870Key
import com.mincom.ellipse.edoi.ejb.msf870.MSF870Rec
import com.mincom.ellipse.eroi.linkage.mss080.MSS080LINK
import com.mincom.ellipse.hook.hooks.HookTools
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.ellipse.script.util.EDOIWrapper
import com.mincom.ellipse.types.m3140.instances.ReceiptTaskCreateDocumentReceiveDTO
import com.mincom.ellipse.types.m3140.instances.ReceiptTaskCreateDocumentReceiveServiceResult
import com.mincom.enterpriseservice.ellipse.ErrorMessageDTO
import com.mincom.enterpriseservice.exception.EnterpriseServiceOperationException
import com.mincom.eql.impl.QueryImpl
import java.text.DecimalFormat
import org.slf4j.LoggerFactory

/**
 *  Created by SUMMA CONSULTING S.A.S on 21/07/2017.
 *  26-Jun-2018 DBERMUDEZ Ahora el envio del correo no es inmediatamente se haga la recepcion ONSITE,
 *                        se guardan los datos en el MSF012 para que luego el cobepo lo envie.
 *  21-Jul-2017 JAFETH  Ellipse 8
 */
class ReceiptTaskService_receiveDocument extends ServiceHook {
    String Version = "Version 20180903 - v5"

    @Override
    public Object onPreExecute(Object input) {
        info(Version)

        ReceiptTaskCreateDocumentReceiveDTO dto = (ReceiptTaskCreateDocumentReceiveDTO) input
        log.info("##########################################################****NUEVO****#################################################################")

        boolean isPanolero = false;
        boolean isPanolAsigs = false;
        boolean isAdmin = false;
        boolean isNotPanolero = false;

        log.info("WareHouseId: " + dto.getWarehouseId().getValue())
        log.info("PositionId: " + tools.commarea.PositionId.toString().trim())

        if (dto.getWarehouseId().getValue() != null && dto.getDistrictCode().getValue() != null) {

            try {

                WriteLog("MSE1RC", "onPreExecute", "ReceiveDocument WareHouseId: ${dto.getWarehouseId().getValue()} District: ${dto.getDistrictCode().getValue()}", tools)
                isAdmin = isUserAdminPanol(tools.commarea.PositionId.toString().trim(), dto.getWarehouseId().getValue(), dto.getDistrictCode().getValue(), tools.edoi)

                log.info("isAdmin: " + isAdmin)
                WriteLog("MSE1RC", "onPreExecute", "isAdmin: " + isAdmin, tools)

                if (isAdmin == false) {

                    isPanolero = isUserPanol(tools.commarea.PositionId.toString().trim(), dto.getWarehouseId().getValue(), dto.getDistrictCode().getValue(), tools.edoi)
                    log.info("isPanolero: " + isPanolero)

                    WriteLog("MSE1RC", "onPreExecute", "isPanolero: " + isPanolero, tools)

                    if (isPanolero == false) {

                        isNotPanolero = isNotUserPanol(dto.getWarehouseId().getValue(), dto.getDistrictCode().getValue(), tools.edoi)
                        log.info("isNotPanolero: " + isNotPanolero)
                        WriteLog("MSE1RC", "onPreExecute", "isNotPanolero: " + isNotPanolero, tools)

                        if (isNotPanolero == true) {
                            MSF010Rec msf010Rec = tools.edoi.findByPrimaryKey(new MSF010Key(tableType: 'ER', tableCode: 'P003'))
                            WriteLog("MSE1RC", "onPreExecute", "+WH: " + msf010Rec.getTableDesc(), tools)
                            throw new EnterpriseServiceOperationException(
                                    new ErrorMessageDTO("P003", msf010Rec.getTableDesc(), "WareHouseId", 0, 0))
                        }
                    } else {

                        isPanolAsigs = isPanolAsig(tools.commarea.PositionId.toString().trim(), dto.getWarehouseId().getValue(), dto.getDistrictCode().getValue(), tools.edoi)

                        WriteLog("MSE1RC", "onPreExecute", "isPanolAsigs: " + isPanolAsigs, tools)
                        if (isPanolAsigs == false) {

                            MSF010Rec msf010Rec = tools.edoi.findByPrimaryKey(new MSF010Key(tableType: 'ER', tableCode: 'P002'))

                            WriteLog("MSE1RC", "onPreExecute", "+PNL: " + msf010Rec.getTableDesc(), tools)

                            throw new EnterpriseServiceOperationException(
                                    new ErrorMessageDTO("P002", msf010Rec.getTableDesc(), "WareHouseId", 0, 0))
                        }
                    }
                }
                log.info("isPanolero: " + isPanolero)
                log.info("isAdmin: " + isAdmin)
                log.info("isNotPanolero: " + isNotPanolero)

            } catch (Exception e) {
                log.info("Exception: " + e.getMessage())

                WriteLog("MSE1RC", "onPreExecute", "X2: " + e.getMessage(), tools)

                throw new EnterpriseServiceOperationException(
                        new ErrorMessageDTO("X2", e.getMessage(), "WareHouseId", 0, 0))
            }
        }
        return null;

    }

    public boolean isUserPanol(String PositionId, String WareHouseId, String DstrctCode, EDOIWrapper edoi) {
        info("Class Panoles - isUserPanol")
        boolean PNLencontrado = false;
        String pnl = " ";
        String Panoles = ""
        boolean UserPanol = false;
        ArrayList<String> vPanoles = new ArrayList<String>()
        String cPanoles = DstrctCode + WareHouseId
        try {

            MSF870Rec msf870Rec = edoi.findByPrimaryKey(new MSF870Key(positionId: PositionId.toString().trim()))
            pnl = PositionId + msf870Rec.getGlobalProfile()
            def cTableType = MSF010Key.tableType.equalTo("+PNL")
            def cTableCode = MSF010Key.tableCode.like(PositionId.toString().trim() + "%")

            def Query010 = new QueryImpl(MSF010Rec.class).and(cTableType).and(cTableCode)

            MSF010Rec msf010Rec = edoi.firstRow(Query010)
            if (msf010Rec != null) {
                info("TableCode: " + msf010Rec.getPrimaryKey().getTableCode())
                info("pnl: " + pnl)
                String TableDesc = msf010Rec.getTableDesc()
                TableDesc = TableDesc.trim().replace(" ", "")
                info("getTableDesc: " + TableDesc.trim())
                if (TableDesc.trim().equals(pnl.trim())) {
                    Panoles += msf010Rec.getAssocRec()
                    info("Panoles: " + Panoles)
                    UserPanol = true;
                }

            }
            info("UserPanol: " + UserPanol)
        } catch (Exception e) {
            info("Error en la validacion: " + e.getMessage())
            throw new EnterpriseServiceOperationException(
                    new ErrorMessageDTO("+PNL", e.getMessage(), "WareHouseId", 0, 0))
        }
        return UserPanol
    }

    public boolean isPanolAsig(String PositionId, String WareHouseId, String DstrctCode, EDOIWrapper edoi) {
        info("Class Panoles - isPanolAsig")
        boolean PNLencontrado = false;
        String pnl = " ";
        String Panoles = ""
        boolean UserPanol = false;
        ArrayList<String> vPanoles = new ArrayList<String>()
        String cPanoles = DstrctCode + WareHouseId
        try {

            MSF870Rec msf870Rec = edoi.findByPrimaryKey(new MSF870Key(positionId: PositionId.toString().trim()))
            pnl = PositionId + msf870Rec.getGlobalProfile()
            def cTableType = MSF010Key.tableType.equalTo("+PNL")
            def cTableCode = MSF010Key.tableCode.like(PositionId.toString().trim() + "%")

            def Query010 = new QueryImpl(MSF010Rec.class).and(cTableType).and(cTableCode)

            edoi.search(Query010, { MSF010Rec msf010Rec ->
                info("TableCode: " + msf010Rec.getPrimaryKey().getTableCode())
                info("pnl: " + pnl)
                String TableDesc = msf010Rec.getTableDesc()
                TableDesc = TableDesc.trim().replace(" ", "")
                info("getTableDesc: " + TableDesc.trim())
                if (TableDesc.trim().equals(pnl.trim())) {
                    Panoles += msf010Rec.getAssocRec()
                    info("Panoles: " + Panoles)
                    UserPanol = true;
                }
            })
            info("UserPanol: " + UserPanol)
            if (UserPanol) {
                int cant = (Panoles.trim().length() / 8);
                int fin = 8;
                int ini = 0;
                for (int i = 0; i < cant; i++) {
                    vPanoles.add(Panoles.substring(ini, fin));
                    ini += 8;
                    fin += 8;
                }

                info("vPanoles: " + vPanoles)
                info("vPanoles.size: " + vPanoles.size())

                for (int i = 0; i < vPanoles.size(); i++) {

                    if (cPanoles != null) {

                        info("cPanoles: " + cPanoles)
                        info("vPanoles${i}: " + vPanoles.get(i))

                        if (cPanoles.trim().equals(vPanoles.get(i).trim())) {
                            PNLencontrado = true;
                            break;
                        }

                    }
                }
                info("PNLencontrado: " + PNLencontrado)

            } else {
                info("USER_ID no se encuentra en la tabla +PNL")
            }
        } catch (Exception e) {
            info("Error en la validacion: " + e.getMessage())
            throw new EnterpriseServiceOperationException(
                    new ErrorMessageDTO("+PNL", e.getMessage(), "WareHouseId", 0, 0))
        }
        return PNLencontrado
    }

    public boolean isUserAdminPanol(String PositionId, String WareHouseId, String DstrctCode, EDOIWrapper edoi) {
        info("Class Panoles - isUserAdminPanol")
        boolean PNLencontrado = false;
        String pnl = " ";
        String Panoles = ""
        boolean UserPanol = false;
        String cPanoles = ""
        try {
            MSF870Rec msf870Rec = edoi.findByPrimaryKey(new MSF870Key(positionId: PositionId.toString().trim()))
            pnl = PositionId + msf870Rec.getGlobalProfile()
            def cTableType = MSF010Key.tableType.equalTo("+PNL")
            def cTableCode = MSF010Key.tableCode.like(PositionId.toString().trim() + "%")

            def Query010 = new QueryImpl(MSF010Rec.class).and(cTableType).and(cTableCode)

            MSF010Rec msf010Rec = edoi.firstRow(Query010)
            if (msf010Rec != null) {

                info("TableCode: " + msf010Rec.getPrimaryKey().getTableCode())
                info("pnl: " + pnl)
                String TableDesc = msf010Rec.getTableDesc()
                TableDesc = TableDesc.trim().replace(" ", "")
                info("getTableDesc: " + TableDesc.trim())
                if (TableDesc.trim().equals(pnl.trim())) {
                    Panoles += msf010Rec.getAssocRec()
                    info("Panoles: " + Panoles)
                    UserPanol = true;
                }

            }
            info("UserPanol: " + UserPanol)
            cPanoles = Panoles;
            if (UserPanol) {

                if (cPanoles != null) {

                    info("DstrctCode: " + DstrctCode)
                    info("cPanoles: " + cPanoles)

                    if (cPanoles.trim().equals(DstrctCode.trim())) {
                        PNLencontrado = true;

                    }
                }
                info("PNLencontrado: " + PNLencontrado)

            } else {
                info("USER_ID no se encuentra en la tabla +PNL")
            }
        } catch (Exception e) {
            info("Error en la validacion: " + e.getMessage())
            throw new EnterpriseServiceOperationException(
                    new ErrorMessageDTO("+PNL", e.getMessage(), "WareHouseId", 0, 0))
        }
        return PNLencontrado
    }

    public boolean isNotUserPanol(String WareHouseId, String DstrctCode, EDOIWrapper edoi) {
        info("Class Panoles - isNotUserPanol")
        boolean PNLencontrado = false;
        String cPanoles = DstrctCode + WareHouseId
        try {
            info("cPanoles: " + cPanoles.trim())
            MSF010Rec msf010Rec = edoi.findByPrimaryKey(new MSF010Key(tableType: '+WH', tableCode: cPanoles.trim()))
            PNLencontrado = true;
        } catch (Exception e) {
            info("WareHouseId no es Panol")
        }
        return PNLencontrado
    }

    void info(String value) {
        def logObject = LoggerFactory.getLogger(getClass());
        logObject.info("------------- " + value)
    }

    public void WriteLog(String pantalla, String evento, String operacion, HookTools tools) {
        info("Class Panoles - WriteLog");

        String dirWork = "/appliance/data/efs/ellpry/log/hooks/"
        // String dirWork = "/appliance/data/efs/ellprod/log/hooks/"
        String sFichero = dirWork + tools.commarea.UserId.toString().trim() + "_${pantalla}_hooksPanol_${tools.commarea.TodaysDate}.log";
        File ficheros = new File(sFichero);
        String linea = "";

        if (ficheros.exists()) {
            info("Archivo ya creado");

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(sFichero, true));
                linea = tools.commarea.TodaysDate.toString().substring(0, 4) + "-" + tools.commarea.TodaysDate.toString().substring(4, 6) + "-" + tools.commarea.TodaysDate.toString().substring(6, 8) + " " +
                        tools.commarea.Time.toString().substring(0, 2) + ":" + tools.commarea.Time.toString().substring(2, 4) + ":" + tools.commarea.Time.toString().substring(4, 6) + " " +
                        "UserId: " + tools.commarea.UserId + " [app: " + pantalla + " ,Event: " + evento + " ,Desc: " + operacion + "]\n"
                bw.write(linea)
                info("Se crea esta linea: " + linea);
                bw.close()
            } catch (Exception e) {
                throw new EnterpriseServiceOperationException(
                        new ErrorMessageDTO("9995", e.getMessage(), " ", 0, 0))

            }
        } else {
            info("Archivo no creado");

            FileWriter fichero = null;
            BufferedWriter bw = null;
            try {
                fichero = new FileWriter(sFichero);
                bw = new BufferedWriter(fichero);
                info("se crea el archivo");
                linea = tools.commarea.TodaysDate.toString().substring(0, 4) + "-" + tools.commarea.TodaysDate.toString().substring(4, 6) + "-" + tools.commarea.TodaysDate.toString().substring(6, 8) + " " +
                        tools.commarea.Time.toString().substring(0, 2) + ":" + tools.commarea.Time.toString().substring(2, 4) + ":" + tools.commarea.Time.toString().substring(4, 6) + " " +
                        "UserId: " + tools.commarea.UserId + " [app: " + pantalla + " ,Event: " + evento + " ,Desc: " + operacion + "]\n"

                bw.write(linea)
                bw.close()
                info("Se crea esta linea: " + linea);
            } catch (Exception e) {
                info(e.printStackTrace())
                throw new EnterpriseServiceOperationException(
                        new ErrorMessageDTO("9995", e.getMessage(), " ", 0, 0))

            } finally {
                try {
                    if (null != fichero) {
                        fichero.close();
                    }
                } catch (Exception e2) {

                    info(e2.printStackTrace());
                    throw new EnterpriseServiceOperationException(
                            new ErrorMessageDTO("9995", e2.getMessage(), " ", 0, 0))

                }
            }
        }
    }


    @Override
    public Object onPostExecute(Object input, Object result) {
		DecimalFormat formatQty = new DecimalFormat("0000");
		
		ReceiptTaskCreateDocumentReceiveDTO dtoIn = (ReceiptTaskCreateDocumentReceiveDTO) input
        ReceiptTaskCreateDocumentReceiveServiceResult dtoOut = (ReceiptTaskCreateDocumentReceiveServiceResult) result

        log.info("##Document Key: " + dtoIn.getDocumentKey().getValue())
        log.info("##Document Type: " + dtoIn.getDocumentType().getValue())
        log.info("##Document Number: " + dtoIn.getDocumentNumber().getValue())
        log.info("##Document DistrictCode: " + dtoIn.getDocumentDistrictCode().getValue())
        log.info("##Document Item: " + dtoIn.getDocumentItem().getValue())

		String qty = "";
		try {
			qty = formatQty.format(dtoIn.getTaskQuantityUOM().getValue()).padLeft(15, "0")
		} catch (Exception ex ){
			qty = formatQty.format(dtoIn.getTaskQuantityUOM().getValue()).padLeft(15, "0")
		}

		String keyValue = "RECEIPT" + dtoIn.getDocumentNumber().getValue().padLeft(6) + dtoIn.getDocumentItem().getValue().padLeft(3);
		String dataValue = "N" + qty + tools.commarea.TodaysDate + "ON";
		
		createMSF012Rec(keyValue, dataValue);
        return null;
    }
	
	public void createMSF012Rec(String keyValue, String dataValue){
		MSF012Key msf012key = new MSF012Key();
		msf012key.setDataType("C");
		msf012key.setKeyValue(keyValue);
		
		MSF012Rec msf012rec = new MSF012Rec();
		msf012rec.setPrimaryKey(msf012key)
		msf012rec.setDataArea(dataValue);
		
		try{
			tools.edoi.findByPrimaryKey(msf012key);
			String newItemS = msf012key.getKeyValue().padRight(60).substring(19, 22);
			int newItemI = 0;
			if (!newItemS.trim().equals("")){
				newItemI = newItemI + 1;
			}
			
			newItemI.toString().padLeft(3,"0");
			
			tools.edoi.create(msf012rec);
		} catch (Exception ex ){
			tools.edoi.update(msf012rec);
		}
	}
}