package mx.com.amis.sipac.invoice.persistence.domain;

public enum EstatusFacturacionEnum {
  EN_PROCESO(1),
  FACTURA(2),
  NOTA_CREDITO(3),
  COMPLEMENTO(4),
  CANCELACION(5);
  
  private Integer estatusId; 
 
  private EstatusFacturacionEnum(Integer estatusId) {
    this.estatusId = estatusId;
  }

  public Integer getEstatusId() {
    return this.estatusId;
  }
}
