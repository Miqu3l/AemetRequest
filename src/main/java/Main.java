import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Main {

    private static final String AEMET_ERROR = "Error en la resposta de la Aemet";

    public static void main(String[] args){
        try {
            //Declaración de variables con los datos de la petición a la Aemet.
            String codiMunicipi = "43028";  //Código de la población de la Aemet
            String dataBuscada = "2024-11-26T00:00:00"; //Fecha. (Previsión de 40 horas desde la fecha)
            String horaBuscada = "02";      //Hora del dia. (Desde las 00 hasta las 23)
            String periodeBuscat ="0107";   //Franja horario. (Entre las 0107, las 0713, las 1319 y las 1901)

            //Si no encuentra la fecha
            boolean dataTrobada = false;

            //Genera la petición a la Aemet i recibe la respuesta.
            AemetRequest request = new AemetRequest();
            String response = request.aemetForecastRequest(codiMunicipi);

            //Comprueba si la respuesta es correcta.
            if (!AEMET_ERROR.equals(response)) {
                //Crea un objeto para convertir la respuesta en un objeto AemetResponse.
                //En este caso, en una lista de objetos, ya que así es el formato de la respuesta de la Aemet
                ObjectMapper objectMapper = new ObjectMapper();
                List<AemetResponse> aemetResponses = objectMapper.readValue(response,
                        new TypeReference<List<AemetResponse>>() {});

                //Extrae los datos de la fecha, la hora y la franja horaria introducida en las variable del inicio.
                AemetResponse aemetResponse = aemetResponses.get(0);

                for (AemetResponse.Prediccion.Dia dia : aemetResponse.getPrediccion().getDia()) {
                    if (dia.getFecha().equals(dataBuscada)) {
                        dataTrobada= true;
                        System.out.println("Dades per la data: " + dataBuscada);

                        //Buscar velocitat de vent mitja i ratxa màxima.
                        if (dia.getVientoAndRachaMax() != null) {
                            for (AemetResponse.Prediccion.Dia.Viento vent : dia.getVientoAndRachaMax()) {
                                if (horaBuscada.equals(vent.getPeriodo())) {
                                    if (vent.getDireccion() != null && vent.getVelocidad() != null) {
                                        System.out.println("Velocitat del vent: " + vent.getVelocidad());
                                    } else if (vent.getValue() != null) {
                                        System.out.println("Ratxa màxima: " + vent.getValue());
                                    }
                                }
                            }
                        }

                        //Buscar probabilitat de pluja.
                        if (dia.getProbPrecipitacion() != null) {
                            for (AemetResponse.Prediccion.Dia.Probabilidad probabilitat : dia.getProbPrecipitacion()) {
                                if (periodeBuscat.equals(probabilitat.getPeriodo())) {
                                    System.out.println("Probabilitat de pluja: " + probabilitat.getValue());
                                }
                            }
                        }

                        //Buscar precipitació
                        if (dia.getPrecipitacion() != null) {
                            for (AemetResponse.Prediccion.Dia.Precipitacion precipitacio : dia.getPrecipitacion()) {
                                if(horaBuscada.equals(precipitacio.getPeriodo())){
                                    System.out.println("Precipitació: " + precipitacio.getValue());
                                }
                            }
                        }

                        //Buscar probabilitat de tempesta
                        if (dia.getProbTormenta() != null) {
                            for (AemetResponse.Prediccion.Dia.ProbTormenta tempesta : dia.getProbTormenta()) {
                                if (periodeBuscat.equals(tempesta.getPeriodo())){
                                    System.out.println("Probabilitat de tempesta: " + tempesta.getValue());
                                }
                            }
                        }

                        //Buscar neu
                        if (dia.getNieve() != null) {
                            for (AemetResponse.Prediccion.Dia.Nieve neu : dia.getNieve()) {
                                if (horaBuscada.equals(neu.getPeriodo())){
                                    System.out.println("Neu: " + neu.getValue());
                                }
                            }
                        }

                        //Buscar probabilitat de neu
                        if (dia.getProbNieve() != null) {
                            for (AemetResponse.Prediccion.Dia.probNieve probNeu : dia.getProbNieve()) {
                                if (periodeBuscat.equals(probNeu.getPeriodo())){
                                    System.out.println("Probabilitat de nevada: " + probNeu.getValue());
                                }
                            }
                        }

                        //Buscar temperatura
                        if (dia.getTemperatura() != null) {
                            for (AemetResponse.Prediccion.Dia.Temperatura temperatura : dia.getTemperatura()) {
                                if (horaBuscada.equals(temperatura.getPeriodo())){
                                    System.out.println("Temperatura: " + temperatura.getValue());
                                }
                            }
                        }

                        //Buscar sensació tèrmica
                        if (dia.getSensTermica() != null) {
                            for (AemetResponse.Prediccion.Dia.sensTermica sensTermica : dia.getSensTermica()) {
                                if (horaBuscada.equals(sensTermica.getPeriodo())){
                                    System.out.println("Sensació tèrmica: " + sensTermica.getValue());
                                }
                            }
                        }

                        //Buscar humitat relativa
                        if (dia.getHumedadRelativa() != null) {
                            for (AemetResponse.Prediccion.Dia.humedadRelativa humitatRelativa : dia.getHumedadRelativa()) {
                                if (horaBuscada.equals(humitatRelativa.getPeriodo())){
                                    System.out.println("Humitat relativa: " + humitatRelativa.getValue());
                                }
                            }
                        }
                    }
                }
                if (!dataTrobada) {
                    System.out.println("La previsió per aquesta data no està disponible");
                }
            } else {
                System.out.println(response);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}