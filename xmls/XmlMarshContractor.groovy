package xmls

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Contratista")
@XmlAccessorType (XmlAccessType.FIELD)
public class XmlMarshContractor {
	String Rut_cont;
	String Dig_Rut_cont
	String R_soc_cont
	String Dir_cont
	String Ciud_Cont
	String Nomb_Contact
}
