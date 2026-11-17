package Bienes.Categoria;

public enum Unidad {
  KILOGRAMOS {
    public String getAbreviatura() {
      return "kg";
    }
    public Boolean admiteDecimales() {
      return true;
    }
  },
  UNIDADES {
    public String getAbreviatura() {
      return "uds";
    }
    public Boolean admiteDecimales() {
      return false;
    }
  },
  LITROS {
    public String getAbreviatura() {
      return "l";
    }
    public Boolean admiteDecimales() {
      return true;
    }
  };

  abstract public String getAbreviatura();
  abstract public Boolean admiteDecimales();
}
