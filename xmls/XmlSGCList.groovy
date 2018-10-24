package xmls;

import java.util.List;
 
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
 
@XmlRootElement(name = "SGCS")
@XmlAccessorType (XmlAccessType.FIELD)
public class XmlSGCList 
{
    @XmlElement(name = "sgc")
    private List<XmlSGC> sgcs = null;
 
    public List<XmlSGC> getEmployees() {
        return sgcs;
    }
 
    public void setEmployees(List<XmlSGC> sgcs) {
        this.sgcs = sgcs;
    }
}