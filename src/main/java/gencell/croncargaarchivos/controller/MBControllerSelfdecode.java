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
import gencell.croncargaarchivos.entities.VWCronSelfdecodeCargaArchivos;
import gencell.croncargaarchivos.selfdecode.ProfilePersonaSelfdecode;
import gencell.croncargaarchivos.selfdecode.SelfdecodeServiceProcess;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
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



@Named(value = "MBControllerSelf")
@SessionScoped
public class MBControllerSelfdecode implements Serializable {

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
    public MBControllerSelfdecode() {
        if (sessionBeanBaseFachada == null) {
            System.out.println("****************  fachada null Cron CARGA ARCHIVOS: ");
            //sessionBeanBaseFachada = this.lookupSessionBeanBaseFachadaLocal();
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

    public void ejecutarTareaCargaArchivosSelfdecode() {
        try {
            if (sessionBeanBaseFachada == null) {
                System.out.println("**************** fachada null en ejecutar tarea CARGA ARCHIVOS");
                return;
            }
            Date fechaInicio = new Date();
            monitorCron = sessionBeanBaseFachada.BuscarPorID(Cron.class, 5);
            List<VWCronSelfdecodeCargaArchivos> listArchivosCargaSelfdecode;
            listArchivosCargaSelfdecode = sessionBeanBaseFachada.BuscarTodos(VWCronSelfdecodeCargaArchivos.class);
            if (listArchivosCargaSelfdecode != null && !listArchivosCargaSelfdecode.isEmpty()) {
                System.out.println("**************** ENCUENTRA ARCHIVOS SELFDECODE PARA CARGAR: " + listArchivosCargaSelfdecode.size());
                this.cargarArchivosSelfdecode(listArchivosCargaSelfdecode);
            }
            //this.ejecutarTareaEliminarArchivosSelfdecode();
            if (monitorCron != null) {
                monitorCron.setFechaInicio(fechaInicio);
                monitorCron.setFechaFinal(new Date());
                sessionBeanBaseFachada.Editar(monitorCron);
            }

        } catch (Exception e) {
            System.out.println("Excepcion ejecutarTareaCargaArchivosSelfdecode" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarArchivosSelfdecode(List<VWCronSelfdecodeCargaArchivos> listArchivosCargaSelfdecode) {
        for (int i = 0; i < listArchivosCargaSelfdecode.size(); i++) {
            sessionBeanBaseFachada.actualizarEstadoPeticionBioLab(listArchivosCargaSelfdecode.get(i).getId(), "CARGANDO-SERVER", "10");
        }
        for (int i = 0; i < listArchivosCargaSelfdecode.size(); i++) {
            VWCronSelfdecodeCargaArchivos archivoCargaSelf = listArchivosCargaSelfdecode.get(i);
            try {
                Boolean flag = this.obtenerByteFile(archivoCargaSelf);
                if (flag != null && flag) {
                    this.comprobarSize(archivoCargaSelf);
                }
            } catch (Exception ex) {
                System.out.println("**************** ERROR AL CARGAR EL ARCHIVO DE CON ID: " + archivoCargaSelf.getId());
                ex.printStackTrace();
                //sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(archivoCarga.getId(), "ERROR", ex.getMessage(), "12");
            }
        }
    }

    private Boolean obtenerByteFile(VWCronSelfdecodeCargaArchivos archivoCar) throws Exception {
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
            sftp.get(archivoCar.getNombreArchivo(), PATH_ARCHIVOS_VARSOME + archivoCar.getNombreArchivo());
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

    private void comprobarSize(VWCronSelfdecodeCargaArchivos archivoCar) throws Exception {
        JSch jsch = new JSch();
        ChannelSftp sftp = null;
        Session session = null;
        try {
            File tmp = new File(PATH_ARCHIVOS_VARSOME + archivoCar.getNombreArchivo());
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

    private void sendPost(VWCronSelfdecodeCargaArchivos archivoCar) throws InterruptedException {
        
        RequestVarsome req = new RequestVarsome();
        String forward_reads = "";
        String reverse_reads = "";
        Integer idPeticion ;
        String profile = "" ;
        Integer idPaciente;
        ProfilePersonaSelfdecode profileSelfdecode;         
        SelfdecodeServiceProcess selfdecodeserviceprocess = new SelfdecodeServiceProcess();
        
        forward_reads = "http://gencellservicetwo.com/files_varsome/" + archivoCar.getNombreArchivo();
        //reverse_reads = "http://gencellservicetwo.com/files_varsome/" +archivoCar.getNombreArchivo();
        reverse_reads = "http://gencellservicetwo.com/files_varsome/" + archivoCar.getNombreArchivo().replaceAll("1.", "2.");
        
        idPeticion = archivoCar.getIdPeticion();
        profileSelfdecode  = sessionBeanBaseFachada.consultarProfile(idPeticion); // Id Profile SelfDecode
        
        profile = profileSelfdecode.getIdProfileSelfdecode();
        idPaciente = profileSelfdecode.getIdPaciente();
        
        if ((profile != null) && (forward_reads != null) && (reverse_reads != null)&&(idPaciente != null)){
            
             selfdecodeserviceprocess.iniciarEnvioArchivoSelfdecode(profile, forward_reads, reverse_reads, idPaciente);
        }else{
            System.out.println("Verifique que no tenga datos nulos");
        }
    }

   
/*    private void ejecutarTareaEliminarArchivosSelfdecode() {
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
                this.comprobarEstadoSelfdecode(archivoEliminar);

            } catch (Exception ex) {
                System.out.println("**************** ERROR AL INTENAR ELIMINAR EL ARCHIVO DE CON ID: " + archivoEliminar.getId());
                ex.printStackTrace();
                //sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLab(archivoCarga.getId(), "ERROR", ex.getMessage(), "12");
            }
        }
    }

    private void comprobarEstadoSelfdecode(VWCronVarsomeEliminarArchivos elimArchivos) {

        
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
    }*/

}

