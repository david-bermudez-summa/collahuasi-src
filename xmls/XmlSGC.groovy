package xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SGC")
@XmlAccessorType (XmlAccessType.FIELD)
public class XmlSGC {

/*  '<xml version="1.0"?>                    '.                
    '<SGC>                                   '.
    '<tipo>              </tipo>             '.
    '<contrato>          </contrato>         '.
    '<oca>               </oca>              '.
    '<tipoOca>           </tipoOca>          '.
    '<empresa>           </empresa>          '.
    '<descripcion>       </descripcion>      '.
    '<inicio>            </inicio>           '.
    '<termino>           </termino>          '.
    '<admin>             </admin>            '.
    '<mailAdm>           </mailAdm>          '.
    '<fonoAdm>           </fonoAdm>          '.
    '<codArea>           </codArea>          '.
    '<area>              </area>             '.
    '<dotacion>          </dotacion>         '.
    '<admCont>           </admCont>          '.
    '<mailACont>         </mailACont>        '.
    '<fonoACont>         </fonoACont>        '.
    '<garantia>          </garantia>         '.
    '<monto>             </monto>            '.
    '<vigGart>           </vigGart>          '.
    '<bcoGart>           </bcoGart>          '.
    '<comm>              </comm>             '.
    '<razonSocial>       </razonSocial>      '.
    '<repLegal>          </repLegal>         '.
    '<fonoRLegal>        </fonoRLegal>       '.
    '<faxRLegal>         </faxRLegal>        '.
    '<direccRLegal>      </direccRLegal>     '.
    '<contacto>          </contacto>         '.
    '<fonoContacto>      </fonoContacto>     '.
    '<faxContacto>       </faxContacto>      '.
    '<direccContacto>    </direccContacto>   '.
    '<mailEmpresa>       </mailEmpresa>      '.
    '<origen>            </origen>           '.
    '</SGC>                                  '.*/
	
	private String tipo;
	private String contrato
	private String oca;
	private String tipoOca
	private String empresa
	private String descripcion
	private String inicio
	private String termino
	private String admin
	private String mailAdm
	private String fonoAdm
	private String codArea
	private String area
	private String dotacion
	private String admCont
	private String mailACont
	private String fonoACont
	private String garantia
	private String monto
	private String vigGart
	private String bcoGart
	private String comm
	private String site
	private String razonSocial
	private String repLegal
	private String fonoRLegal
	private String faxRLegal
	private String direccRLegal
	private String contacto
	private String fonoContacto
	private String faxContacto
	private String direccContacto
	private String mailEmpresa
	private String origen

}