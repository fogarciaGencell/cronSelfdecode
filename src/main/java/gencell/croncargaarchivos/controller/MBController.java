/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gencell.croncargaarchivos.controller;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import gencell.croncargaarchivos.ejb.SessionBeanBaseFachadaLocal;
import gencell.croncargaarchivos.entities.Cron;
import gencell.croncargaarchivos.entities.VWCronVarsomeCargaArchivos;
import gencell.croncargaarchivos.entities.VWCronVarsomeEliminarArchivos;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

/**
 *
 * @author JOSEPH
 */
@Named(value = "MBController")
@SessionScoped
public class MBController implements Serializable {

    @EJB
    private SessionBeanBaseFachadaLocal sessionBeanBaseFachada;
    private Cron monitorCron;
    public static final String PATH_ARCHIVOS_DEV = "public/files/dev/laboratorio_gp/ngs";
    public static final String PATH_ARCHIVOS_VARSOME_DEV = "d:\\";
    public static final String PATH_ARCHIVOS_VARSOME = "/var/www/html/files_varsome/";
    public static final String HOST = "134.209.114.100";
    public static final String USUARIO = "gencell";
    public static final String CONTRASENA = "Gp.ftp123";

    /**
     * Creates a new instance of MBController
     */
    public MBController() {
        if (sessionBeanBaseFachada == null) {
            System.out.println("****************  fachada null Cron CARGA ARCHIVOS: ");
            sessionBeanBaseFachada = this.lookupSessionBeanBaseFachadaLocal();
            if (sessionBeanBaseFachada != null) {
                System.out.println("****************  fachada obtenida Cron CARGA ARCHIVOS: ");
            }
        }
    }

    /*public void ejecutarTareaCargaArchivos() {
        try {
            if (sessionBeanBaseFachada == null) {
                System.out.println("**************** fachada null en ejecutar tarea CARGA ARCHIVOS");
                return;
            }
            Date fechaInicio = new Date();
            monitorCron = sessionBeanBaseFachada.BuscarPorID(Cron.class, 1);
            List<VWCronArchivosCarga> listArchivosCarga;
            listArchivosCarga = sessionBeanBaseFachada.obtenerArchivosCarga();
            if (listArchivosCarga != null && !listArchivosCarga.isEmpty()) {
                System.out.println("**************** ENCUENTRA ARCHIVOS PARA CARGAR: " + listArchivosCarga.size());
                this.cargarArchivos(listArchivosCarga);
            }
            if (monitorCron != null) {
                monitorCron.setFechaInicio(fechaInicio);
                monitorCron.setFechaFinal(new Date());
                sessionBeanBaseFachada.Editar(monitorCron);
            }

        } catch (Exception e) {
            System.out.println("Excepcion ejecutarTareaCargaArchivos" + e.getMessage());
            e.printStackTrace();
        }
    }*/

    /*private void cargarArchivos(List<VWCronArchivosCarga> listArchivosCarga) {
        for (int i = 0; i < listArchivosCarga.size(); i++) {
            sessionBeanBaseFachada.actualizarEstadoResultadosOrden(listArchivosCarga.get(i).getIdResultadosOrden(), "CARGANDO");
        }
        for (int i = 0; i < listArchivosCarga.size(); i++) {
            VWCronArchivosCarga archivoCarga = listArchivosCarga.get(i);
            try {
                File archivo = new File("D:\\pruebaNGS\\" + archivoCarga.getUrlResultado());
                if (archivo.exists()) {
                    String numbreArchivo = this.saveFileFTP(PATH_ARCHIVOS_DEV, archivo);
                    if (archivo != null) {
                        sessionBeanBaseFachada.actualizarEstadoResultadosOrden(listArchivosCarga.get(i).getIdResultadosOrden(), "CARGADO");
                        sessionBeanBaseFachada.actualizarURLResultadosOrden(listArchivosCarga.get(i).getIdResultadosOrden(), numbreArchivo);
                        archivo.delete();
                    } else {
                        sessionBeanBaseFachada.actualizarEstadoResultadosOrden(listArchivosCarga.get(i).getIdResultadosOrden(), "ERROR");
                    }
                }
            } catch (Exception ex) {
                System.out.println("**************** ERROR AL CARGAR EL ARCHIVO DE CON IDORDENRESULTADO: " + archivoCarga.getIdResultadosOrden());
                ex.printStackTrace();
            }
        }
    }*/

   /* private String saveFileFTP(String ruta, File archivo) throws Exception {
        JSch jsch = new JSch();
        FileInputStream fis = null;
        ChannelSftp sftp = null;
        Session session = null;
        try {
            session = jsch.getSession(USUARIO, HOST, 22);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(CONTRASENA);
            session.connect();
            Channel channel = session.openChannel("sftp");
            sftp = (ChannelSftp) channel;
            sftp.connect();
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssS-");
            String nombreArchivoSubida = archivo.getName();
            String filename = sdf.format(new Date()) + nombreArchivoSubida;
            fis = new FileInputStream(archivo);
            sftp.cd(ruta);
            sftp.put(fis, filename);
            sftp.disconnect();
            return ruta + "/" + filename;
        } catch (JSchException e) {
            System.out.println("No se pudo realizar la conexión");
            return null;
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (sftp != null) {
                sftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }*/

   /* private void saveFileAWS(String ruta) throws Exception {
        try {
            String command = "curl --upload-file \"file.fq.gz\" \"https://sd-platform-dev-userdatas3bucket-1ptzap1zmfgnb.s3-accelerate.amazonaws.com/00000000-0000-0000-0000-000000000000/test/gencell-upload3?AWSAccessKeyId=ASIAZXBBM5C3QSJ5D2CM&Signature=4JvzoL0Fs4g4C8GAOo3MTM5GH9I%3D&x-amz-security-token=IQoJb3JpZ2luX2VjEK7%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJHMEUCIQCqlpjjEUHOzvtZuocSuzse8uy88unGPm1F5OoV%2FWIQQAIgZDtNWdAT0sxYof1ZjHFuXgb%2Bi87qK20SuWS%2FkXeMIK4qlAMIRxACGgw2Njc5Mzc0NjY1NTEiDIuOqC0%2B3m1qAGw%2FfCrxAl3RLytgo9OpnK5YhKR82tGSymgvOSc6PZeA5YQoTO8zvkNlBx3vL6RrBngspHkitMFKKDrJkHOkqhCH3c6h5gX3ugRIETTcdb3OeUTOOMOGkplvqxRYp%2BvPuBQvgNuMw2xU255EWT6uNXtCC3tEt9%2BZsvPRyS2J%2B25zJGzfhBUu2wio75p1ufWb4c6%2Fdnp%2BYIvCdzEoC%2F%2FPu14bWmRVyqbBrbolUk572VZTVnwQGmrLiTBQCJ6UbgY8lBmVPNEsWXFNLqxYneuw%2B96N96baPaOX2wVJ%2FjaT4pujIN5BIOJxGJHbI5wmRx5OQqAwdQKuLBSQk15g9Pt4uUDLfckMnpO%2FBKT2aNylPMeZ3V%2BYeLX8WhwKiOz8JLF48U2MsLWmSmUMlINiWMC9FIfVrP95SXuMYL%2FKFxD7qo%2BUBm%2Bwf6Zao7kCwOQGNRhMaHmFK99hoE0Rjm5zRImAfnkkPDsAXOqdpGEQRyRh%2FjvyMh%2FO5OlvcTDpiO2YBjqmAU1HD77giu9zx06UJ6o7tX47jluBuHUNOfdVy%2BE7E8bHMH9ogzRvniePxFpusa%2FswWecoiMzqh861IQuZ3Dh%2ByN80qXFIEY6EIPTbPN%2F1z4OC6JZ9jyS90yDAfrVy%2F7Mw9L6oTe1VFUCaVmmNqHH4FVfGXSHK83StJJwk1UAFUsACfUmTovtcalXT53zdX1%2Bb3yFyF%2FUWt0P4hNeFwQRkblyCjCySk0%3D&Expires=1662774658\"";
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                //output.append(line + "\n");
                System.out.println(line + "\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    public void ejecutarTareaCargaArchivosVarsome() {
        try {
            if (sessionBeanBaseFachada == null) {
                System.out.println("**************** fachada null en ejecutar tarea CARGA ARCHIVOS");
                return;
            }
            Date fechaInicio = new Date();
            monitorCron = sessionBeanBaseFachada.BuscarPorID(Cron.class, 5);
            List<VWCronVarsomeCargaArchivos> listArchivosCargaVarsome;
            listArchivosCargaVarsome = sessionBeanBaseFachada.BuscarTodos(VWCronVarsomeCargaArchivos.class);
            if (listArchivosCargaVarsome != null && !listArchivosCargaVarsome.isEmpty()) {
                System.out.println("**************** ENCUENTRA ARCHIVOS VARSOME PARA CARGAR: " + listArchivosCargaVarsome.size());
                this.cargarArchivosVarsome(listArchivosCargaVarsome);
            }
            this.ejecutarTareaEliminarArchivosVarsome();
            if (monitorCron != null) {
                monitorCron.setFechaInicio(fechaInicio);
                monitorCron.setFechaFinal(new Date());
                sessionBeanBaseFachada.Editar(monitorCron);
            }

        } catch (Exception e) {
            System.out.println("Excepcion ejecutarTareaCargaArchivos" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarArchivosVarsome(List<VWCronVarsomeCargaArchivos> listArchivosCargaVarsome) {
        for (int i = 0; i < listArchivosCargaVarsome.size(); i++) {
            sessionBeanBaseFachada.actualizarEstadoPeticionBioLab(listArchivosCargaVarsome.get(i).getId(), "CARGANDO-SERVER", "10");
        }
        for (int i = 0; i < listArchivosCargaVarsome.size(); i++) {
            VWCronVarsomeCargaArchivos archivoCarga = listArchivosCargaVarsome.get(i);
            try {
                Boolean flag = this.obtenerByteFile(archivoCarga);
                if (flag != null && flag) {
                    this.comprobarSize(archivoCarga);
                }
            } catch (Exception ex) {
                System.out.println("**************** ERROR AL CARGAR EL ARCHIVO DE CON ID: " + archivoCarga.getId());
                ex.printStackTrace();
                //sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(archivoCarga.getId(), "ERROR", ex.getMessage(), "12");
            }
        }
    }

    private Boolean obtenerByteFile(VWCronVarsomeCargaArchivos archivoCar) throws Exception {
        JSch jsch = new JSch();
        ChannelSftp sftp = null;
        Session session = null;
        try {
            session = jsch.getSession(archivoCar.getUsuarioFTP(), archivoCar.getIp(), 22);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(archivoCar.getPasswordFTP());
            session.connect();
            Channel channel = session.openChannel("sftp");
            sftp = (ChannelSftp) channel;
            sftp.connect();
            sftp.cd(archivoCar.getUrl());
            sftp.get(archivoCar.getNombreArchivo(), PATH_ARCHIVOS_VARSOME + archivoCar.getNombreArchivoRenombrado());
            sftp.disconnect();
            return true;
        } catch (Exception e) {
            System.out.println("No se pudo realizar la conexión  para subir al server");
            e.printStackTrace();
            sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(archivoCar.getId(), "ERROR SUBIDA A SERVER", e.getMessage(), "15");
            return false;
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private void comprobarSize(VWCronVarsomeCargaArchivos archivoCar) throws Exception {
        JSch jsch = new JSch();
        ChannelSftp sftp = null;
        Session session = null;
        try {
            File tmp = new File(PATH_ARCHIVOS_VARSOME + archivoCar.getNombreArchivoRenombrado());
            if (!tmp.exists()) {
                sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(archivoCar.getId(), "INICIAL", "EL ARCHIVO EN EL SERVIDOR NO EXISTE", "50");
                return;
            }

            session = jsch.getSession(archivoCar.getUsuarioFTP(), archivoCar.getIp(), 22);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(archivoCar.getPasswordFTP());
            session.connect();
            Channel channel = session.openChannel("sftp");
            sftp = (ChannelSftp) channel;
            sftp.connect();
            java.util.Vector<ChannelSftp.LsEntry> flLst = sftp.ls(archivoCar.getUrl());
            for (int j = 0; j < flLst.size(); j++) {
                ChannelSftp.LsEntry entry = flLst.get(j);
                SftpATTRS attr = entry.getAttrs();
                if (entry.getFilename().equals(archivoCar.getNombreArchivo())) {
                    if (tmp.length() == attr.getSize()) {
                        sessionBeanBaseFachada.actualizarEstadoPeticionBioLab(archivoCar.getId(), "CARGADO-SERVER", "50");
                        this.sendPost(archivoCar);
                        break;
                    } else {
                        sessionBeanBaseFachada.actualizarEstadoPeticionBioLab(archivoCar.getId(), "INICIAL", "10");
                        tmp.delete();
                        break;
                    }
                }
            }
            return;
        } catch (Exception e) {
            System.out.println("No se pudo realizar la conexión para comprobar archivo");
            e.printStackTrace();
            sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(archivoCar.getId(), "ERROR COMPROBACION ARCHIVO", e.getMessage(), "15");
            return;
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private void sendPost(VWCronVarsomeCargaArchivos archivoCar) {

        try {
            HttpPost post;

            DefaultHttpClient client;
            HttpResponse response;
            post = new HttpPost("https://us.clinical.varsome.com/api/v1/sample-files/");

            post.setHeader("Content-Type", "application/json; charset=utf8");
            post.setHeader("accept", "application/json");
            post.setHeader("accept-encoding", "gzip, deflate");
            post.setHeader("authorization", "Token 2r4YG0#0wuJaA9jympY$7vcpr?GhT!Pb@w4FeUgK");
            response = null;

            Gson gson = new Gson();
            RequestVarsome req = new RequestVarsome();

            req.setSample_file_name(archivoCar.getNombreArchivoRenombrado());
            req.setFile_url("http://gencellservicetwo.com/files_varsome/" + archivoCar.getNombreArchivoRenombrado());
            //req.setFile_url("http://204.199.115.147/files_varsome/" + nombreArchivo);
            String jsonData = gson.toJson(req);

            post.setEntity(new StringEntity(jsonData));
            String line = "";
            StringBuffer result = new StringBuffer();
            client = getDefaultHttpClient();
            response = client.execute(post);

            System.out.println("Post parameters : " + jsonData);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            while ((line = reader.readLine()) != null) {
                result.append(line); // OBTENER id varsome
            }
            System.out.println("Resultado obtenido:" + result.toString());
            String idVarsome = null;
            if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
                if (result != null && !result.toString().isEmpty()) {
                    JSONObject object = new JSONObject(result.toString());
                    System.out.println(object.get("id").toString());
                    //JSONObject respuestajson = new JSONObject(object.getJSONObject("docNumFact").toString());
                    idVarsome = object.get("id").toString();

                }
            }

            LogHttpVarsome log = new LogHttpVarsome();
            log.setFecha(new Date());
            log.setNombreArchivo(archivoCar.getNombreArchivoRenombrado());
            log.setResponseCode("" + response.getStatusLine().getStatusCode());
            log.setResponseMsg(result.toString());
            log.setSend(jsonData);
            log.setNombreUsuarioCreacion("CRON CARGA ARCHIVOS");
            log.setUsuarioCreacion("CRON CARGA ARCHIVOS");
            sessionBeanBaseFachada.Crear(log);
            sessionBeanBaseFachada.actualizarEstadoPeticionIdVarsomeBioLab(archivoCar.getId(), "ENVIADO-VARSOME", idVarsome, "80");

        } catch (Exception ex) {
            Logger.getLogger(MBController.class.getName()).log(Level.SEVERE, null, ex);
            LogHttpVarsome log = new LogHttpVarsome();
            log.setFecha(new Date());
            log.setNombreArchivo(archivoCar.getNombreArchivoRenombrado());
            log.setResponseCode("ERROR EN EL ENVIO DEL ARCHIVO");
            log.setResponseMsg(ex.getMessage());
            log.setNombreUsuarioCreacion("CRON CARGA ARCHIVOS");
            log.setUsuarioCreacion("CRON CARGA ARCHIVOS");
            sessionBeanBaseFachada.Crear(log);
            sessionBeanBaseFachada.actualizarEstadoPeticionBioLab(archivoCar.getId(), "ERROR-ENVIO-VARSOME", "60");
        }
    }

    private void ejecutarTareaEliminarArchivosVarsome() {
        try {
            if (sessionBeanBaseFachada == null) {
                System.out.println("**************** fachada null en ejecutar tarea ELIMINAR ARCHIVOS VARSOME");
                return;
            }

            List<VWCronVarsomeEliminarArchivos> listArchivosElimVarsome;
            listArchivosElimVarsome = sessionBeanBaseFachada.BuscarTodos(VWCronVarsomeEliminarArchivos.class);
            if (listArchivosElimVarsome != null && !listArchivosElimVarsome.isEmpty()) {
                System.out.println("**************** ENCUENTRA ARCHIVOS VARSOME PARA ELIMINAR: " + listArchivosElimVarsome.size());
                this.eliminarArchivosVarsome(listArchivosElimVarsome);
            }

        } catch (Exception e) {
            System.out.println("Excepcion ejecutarTareaEliminarArchivos" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void eliminarArchivosVarsome(List<VWCronVarsomeEliminarArchivos> listArchivosElimVarsome) {
        for (int i = 0; i < listArchivosElimVarsome.size(); i++) {
            sessionBeanBaseFachada.actualizarEstadoPeticionBioLab(listArchivosElimVarsome.get(i).getId(), "VALIDANDO-VARSOME", "90");
        }
        for (int i = 0; i < listArchivosElimVarsome.size(); i++) {
            VWCronVarsomeEliminarArchivos archivoEliminar = listArchivosElimVarsome.get(i);
            try {
                this.comprobarEstadoVarsome(archivoEliminar);

            } catch (Exception ex) {
                System.out.println("**************** ERROR AL INTENAR ELIMINAR EL ARCHIVO DE CON ID: " + archivoEliminar.getId());
                ex.printStackTrace();
                //sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(archivoCarga.getId(), "ERROR", ex.getMessage(), "12");
            }
        }
    }

    private void comprobarEstadoVarsome(VWCronVarsomeEliminarArchivos elimArchivos) {

        String estado = "";
        String isUsable = "";
        System.out.println("INICIA Obtener Estado");

        try {
            DefaultHttpClient client;
            HttpResponse response;
            HttpGet get;
            // Variables
            get = new HttpGet("https://us.clinical.varsome.com/api/v1/sample-files/" + elimArchivos.getIdVarsome() + "/");
            get.setHeader("Accept", "*/*");
            get.setHeader("Content-Type", "application/json;");
            get.setHeader("Authorization", "Token 2r4YG0#0wuJaA9jympY$7vcpr?GhT!Pb@w4FeUgK");
            response = null;

            String line = "";
            StringBuffer result = new StringBuffer();
            client = getDefaultHttpClient();
            response = client.execute(get);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            LogHttpVarsome log = new LogHttpVarsome();
            log.setFecha(new Date());
            log.setNombreArchivo(elimArchivos.getNombreArchivoRenombrado());
            log.setResponseCode("" + response.getStatusLine().getStatusCode());
            if (result != null) {
                log.setResponseMsg(result.toString());
            } else {
                log.setResponseMsg("RESPUESTA VACIA DE VARSOME");
            }
            log.setSend(elimArchivos.getIdVarsome());
            log.setNombreUsuarioCreacion("CRON ELIMINAR ARCHIVOS");
            log.setUsuarioCreacion("CRON ELIMINAR ARCHIVOS");
            sessionBeanBaseFachada.Crear(log);

            if (result != null && !result.toString().isEmpty()) {
                if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
                    JSONObject object = new JSONObject(result.toString());
                    estado = object.get("status").toString();
                    isUsable = object.get("is_usable").toString();
                    System.out.println("Result: " + result.toString());
                } else {
                    System.out.println("Result: " + result.toString());
                    sessionBeanBaseFachada.actualizarEstadoPeticionBioLab(elimArchivos.getId(), "ENVIADO-VARSOME", "80");
                    return;
                }

            } else {
                System.out.println("Result: " + result.toString());
                sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(elimArchivos.getId(), "ENVIADO-VARSOME", "ERROR VALIDANDO VARSOME", "90");
                return;
            }

            System.out.println("Estado: " + estado + " -----  " + "IsUsable: " + isUsable);

            if (estado != null && estado.equals("2")) {
                File fichero = new File(PATH_ARCHIVOS_VARSOME + elimArchivos.getNombreArchivoRenombrado());
                if (!fichero.exists()) {
                    sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(elimArchivos.getId(), "FINALIZADO", "", "100");
                    System.out.println("El fichero ya no existe");
                    return;
                }
                if (fichero.delete()) {
                    sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(elimArchivos.getId(), "", "FINALIZADO", "100");
                    System.out.println("El fichero ha sido borrado satisfactoriamente");
                } else {
                    System.out.println("El fichero no puede ser borrado");
                    sessionBeanBaseFachada.actualizarEstadoPeticionBioLab(elimArchivos.getId(), "ERROR BORRANDO ARCHIVO", "95");
                }
            }
            if (estado != null && estado.equals("3")) {
                sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(elimArchivos.getId(), "INICIAL", "ESTADO 3 EN VARSOME, NO ENCUENTRA EL ARCHIVO", "0");
                System.out.println("FINALIZA comprobarEstadoVarsome");
                return;
            }
            if (estado != null && (estado.equals("0") || estado.equals("1"))) {
                sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(elimArchivos.getId(), "ENVIADO-VARSOME", "ESTADO " + estado + " EN VARSOME", "80");
                System.out.println("FINALIZA comprobarEstadoVarsome");
                return;
            }

            sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(elimArchivos.getId(), "ENVIADO-VARSOME", "NO INGRESA A NINGUN ESTADO EN VARSOME", "90");
            System.out.println("FINALIZA comprobarEstadoVarsome");
            return;
        } catch (Exception ex) {
            System.out.println("**************** ERROR AL INTENAR ELIMINAR EL ARCHIVO DE CON ID: " + elimArchivos.getId());
            sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(elimArchivos.getId(), "ERROR ELIMINANDO-SERVER", ex.getMessage(), "90");
            ex.printStackTrace();
        }
    }

    private static DefaultHttpClient getDefaultHttpClient() throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext ssl_ctx = SSLContext.getInstance("TLS");
        TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String t) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String t) {
            }
        }};
        ssl_ctx.init(null, certs, new SecureRandom());
        SSLSocketFactory ssf = new SSLSocketFactory(ssl_ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = httpClient.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
        return new DefaultHttpClient(ccm, httpClient.getParams());
    }

    private SessionBeanBaseFachadaLocal lookupSessionBeanBaseFachadaLocal() {
        try {
            Context c = new InitialContext();
            return (SessionBeanBaseFachadaLocal) c.lookup("java:global/CronCargaArchivos/SessionBeanBaseFachada!gencell.croncargaarchivos.ejb.SessionBeanBaseFachadaLocal");
        } catch (Exception e) {
            System.out.println("ERROR AL OBTENER EL BEAN DE CRON CARGA ARCHIVOS: ");
            e.printStackTrace();
            return null;
        }
    }

}
