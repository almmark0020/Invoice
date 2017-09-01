package mx.com.amis.sipac.invoice.persistence.domain;

public enum OrderTypeEnum {
  DANOS_MATERIALES("D"),
  GASTOS_MEDICOS("G");
  
  private String typeId; 
 
  private OrderTypeEnum(String typeId) {
    this.typeId = typeId;
  }

  public String getTypeId() {
    return this.typeId;
  }
}
