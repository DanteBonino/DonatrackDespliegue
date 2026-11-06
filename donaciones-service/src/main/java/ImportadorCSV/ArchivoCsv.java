package ImportadorCSV;

import DTOs.DonanteDto;
import ExceptionHandler.NoSePudoLeerElArchivo;
import ImportadorDesdeArchivo.Archivos.Archivo;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileReader;
import java.util.List;

//public class ImportadorCSV.ImportadorCsv {
//  public List<String> importarLineasDesde(String unaRuta) {
//    // Al declararlo acá, Java lo cierra solo al terminar el bloque try
//    try (var buffer = new BufferedReader(new FileReader(unaRuta))) {
//      List<String> lineas = new ArrayList<>();
//
//      //Saltear encabezado
//      buffer.readLine();
//
//      String linea;
//      while ((linea = buffer.readLine()) != null) {
//        lineas.add(linea);
//      }
//      return lineas;
//    } catch (IOException e) {
//      throw new RuntimeException("No se pudo leer el archivo CSV solicitado");
//    }
//  }
//}


public class ArchivoCsv extends Archivo {

  public ArchivoCsv(String path) {
    super(path);
  }

  @Override
  public List<DonanteDto> importarInstancias() {
    try (FileReader reader = new FileReader(this.path)) {
      return new CsvToBeanBuilder<DonanteDto>(reader)
          .withType(DonanteDto.class)
          .build()
          .parse();
    } catch (Exception e) {
      throw new NoSePudoLeerElArchivo(this.path);
    }
  }

}

//class CreadorDeDonantesDesdeArchivo{
//  private AdaptadorImportador importador;
//
//  public CreadorDeDonantesDesdeArchivo(AdaptadorImportador importador){
//    this.importador = importador;
//  }
//
//  public List <DonanteBasico> instanciasDesde(String rutaDeArchivo){
//    List <DonanteBasico> lineas = this.importador.importarLineasDesde(rutaDeArchivo);
//    return lineas.stream().map(this::crearDonante).toList();
//  }
//
//  private DonanteBasico crearDonante(DonanteBasico unDonante){
//    var nombre    = unDonante.nombre();
//    var documento = unDonante.documento();
//    var email     = unDonante.email();
//    var telefono  = unDonante.telefono();
//    var tipo      = unDonante.tipoPersona();
//
////    if(Objects.equals(unDonante.tipoPersona(), "HUMANA")){
////      return new DonanteSimple(nombre, documento, email, telefono);
////    }
////    else{
////      return new DonanteJuridico(nombre, documento, email, telefono);
////    }
//    return unDonante;
//  }
//
//}

//class ImportadorCsvAdapterDonantes{
//  private Importador importador;
//
//  public ImportadorCsvAdapterDonantes (Importador importador){
//    this.importador = importador;
//  }
//
//  public List<DonanteBasico> importarLineasDesde(String unaRuta){
//    List <String> lineas = this.importador.importarLineasDesde(unaRuta);
//    return lineas.stream().map(this::crearDonanteBasico).toList();
//  }
//
//  private DonanteBasico crearDonanteBasico(String linea){
//    String[] campos = linea.split(",");
//    return new DonanteBasico(campos[0], campos[2], campos[3], campos[4], campos[5]);
//  }
//}

//interface AdaptadorImportador {
//  List<DonanteBasico>importarLineasDesde(String ruta);
//}
//
//interface Importador {
//  List<String>importarLineasDesde(String ruta);
//}
//


