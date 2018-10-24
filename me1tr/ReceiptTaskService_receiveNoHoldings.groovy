package me1tr

import com.mincom.ellipse.edoi.ejb.msf012.MSF012Key
import com.mincom.ellipse.edoi.ejb.msf012.MSF012Rec
import com.mincom.ellipse.edoi.ejb.msf151.MSF151Key
import com.mincom.ellipse.edoi.ejb.msf151.MSF151Rec
import com.mincom.ellipse.eroi.linkage.mss080.MSS080LINK
import com.mincom.ellipse.hook.hooks.ServiceHook
import com.mincom.ellipse.types.m3140.instances.ReceiptTaskReceiveDTO
import java.text.DecimalFormat

/**
 *  Created by SUMMA CONSULTING S.A.S on 17/05/2018.
 *  26-Jun-2018 DBERMUDEZ Ahora el envio del correo no es inmediatamente se haga la recepcion ONSITE,
 *                        se guardan los datos en el MSF012 para que luego el cobepo lo envie.
 *  17-May-2018 JAFETH  Ellipse 8
 */
class ReceiptTaskService_receiveNoHoldings extends ServiceHook {

    String Version = "Version 20180903 - v5"

    @Override
    public Object onPostExecute(Object input, Object result) {
        log.info("onPostExecute")
        log.info(Version)

		DecimalFormat formatQty = new DecimalFormat("000");

        ReceiptTaskReceiveDTO dto = (ReceiptTaskReceiveDTO) input

        log.info("##Document Key: " + dto.getDocumentKey().getValue())
        log.info("##Document Type: " + dto.getDocumentType().getValue())
        BigDecimal totalQty = 0;
		BigDecimal toatlVal = 0;

        String waybillNo = dto.getDocumentKey().getValue().substring(0, 8)
        String WaybillItemNo = dto.getDocumentKey().getValue().substring(8, 11)

        for (int i = 0; i < dto.getDirectPurchaseReceiptItems().size(); i++) {
            log.info("##Receipt Quantity: " + dto.getDirectPurchaseReceiptItems()[i].getReceiptQuantity().getValue())
            totalQty += dto.getDirectPurchaseReceiptItems()[i].getReceiptQuantity().getValue()
        }

        MSF151Rec msf151Rec = tools.edoi.findByPrimaryKey(new MSF151Key(waybillNo: waybillNo, waybillItmNo: WaybillItemNo))

        if (msf151Rec.getWbillItemTy().equals('OI')) {

            String poNoItemNo = msf151Rec.getWbillItemDet().padRight(40, " ").substring(0, 9)
			String qty = formatQty.format(totalQty).padLeft(15, "0")
	
			String keyValue = "RECEIPT" + (poNoItemNo).trim();
			String dataValue = "N" + qty + tools.commarea.TodaysDate + "ON";
			
			createMSF012Rec(keyValue, dataValue);
	
        }


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

    public void runMss080(String progName, String requesParams) {

        log.info("runMss080")

        String Uuid = UUID.randomUUID().toString().substring(0, 32).toUpperCase();

        tools.eroi.execute('MSS080', { MSS080LINK mss080linkrec ->
            mss080linkrec.uuid = Uuid
            mss080linkrec.requestParams = requesParams
            mss080linkrec.deferDate = tools.commarea.TodaysDate
            mss080linkrec.deferTime = tools.commarea.Time
            mss080linkrec.requestDate = tools.commarea.TodaysDate
            mss080linkrec.requestTime = tools.commarea.Time
            mss080linkrec.requestBy = tools.commarea.UserId
            mss080linkrec.userId = tools.commarea.UserId
            mss080linkrec.dstrctCode = tools.commarea.District;
            mss080linkrec.requestDstrct = tools.commarea.District;
            mss080linkrec.startOption = "Y";
            mss080linkrec.requestRecNo = '01'
            mss080linkrec.requestNo = '01'
            mss080linkrec.pubType = "TXT"
            mss080linkrec.noOfCopies1 = '01'
            mss080linkrec.progReportId = 'A'
            mss080linkrec.medium = 'R'
            mss080linkrec.startOption = "Y"
            mss080linkrec.traceFlg = "N"
            mss080linkrec.progName = progName
            mss080linkrec.taskUuid = Uuid
        })

    }

    @Override
    public Object onPreExecute(Object input) {
        log.info("onPreExecute")
        return null;
    }
}