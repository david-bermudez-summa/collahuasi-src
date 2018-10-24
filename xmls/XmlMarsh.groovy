package xmls

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Carga_Contrat")
@XmlAccessorType (XmlAccessType.FIELD)
class XmlMarsh {
	
	String Resp_Contrat;
	String D_anual;
	String F_inicio_vig;
	String F_term_vig;
	String N_poliza;
	
	@XmlElement(name = "Contratista")
	private List<XmlMarshContractor> xmlMarshContractor = null;
 
	public List<XmlMarshContractor> getXmlMarshContractor() {
		return xmlMarshContractor;
	}
 
	public void setXmlMarshContractor(List<XmlMarshContractor> xmlMarshContractor) {
		this.xmlMarshContractor = xmlMarshContractor;
	}
		
	String Cod_contrat;
	String Mont_contrat;
	String Tip_contrat;
	String Descrip_Trab;
}
