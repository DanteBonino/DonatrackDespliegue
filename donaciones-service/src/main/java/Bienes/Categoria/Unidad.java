package Bienes.Categoria;

public enum Unidad {
  KILOGRAMOS {
    public String getAbreviatura() {
      return "kg";
    }
  },
  UNIDADES {
    public String getAbreviatura() {
      return "uds";
    }
  },
  LITROS {
    public String getAbreviatura() {
      return "l";
    }
  };

  abstract public String getAbreviatura();
}
