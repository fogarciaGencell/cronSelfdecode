/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gencell.croncargaarchivos.controller;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import gencell.croncargaarchivos.ejb.SessionBeanBaseFachadaLocal;
import gencell.croncargaarchivos.entities.Cron;
import gencell.croncargaarchivos.entities.VWCronSelfFileId;
import gencell.croncargaarchivos.entities.VWCronSelfdecodeCargaArchivos;
import gencell.croncargaarchivos.entities.VWCronSelfdecodeListos;
import gencell.croncargaarchivos.selfdecode.ProfilePersonaSelfdecode;
import gencell.croncargaarchivos.selfdecode.SelfdecodeServiceProcess;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "MBControllerSelfdecode")
@SessionScoped
public class MBControllerSelfdecode implements Serializable {

    @EJB
    private SessionBeanBaseFachadaLocal sessionBeanBaseFachada;
    private Cron monitorCron;
    public static final String PATH_ARCHIVOS_DEV = "public/files/dev/laboratorio_gp/ngs";
    public static final String PATH_ARCHIVOS_SELFDECODE_DEV = "d:\\";
    public static final String PATH_ARCHIVOS_SELFDECODE = "/var/www/html/files_varsome/";
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
            this.sendPost(); // Ejecutara el envio a Selfdecode 
            this.eliminarArchivosSelfdecode();// Elimina los archivos que estan en estado Complete SELFDECODE
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
            sftp.get(archivoCar.getNombreArchivo(), PATH_ARCHIVOS_SELFDECODE + archivoCar.getNombreArchivo());
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
            File tmp = new File(PATH_ARCHIVOS_SELFDECODE + archivoCar.getNombreArchivo());
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
                        //this.sendPost(archivoCar);
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

    private void sendPost() {

        //Variables
        String forward_reads = "";
        String reverse_reads = "";
        Integer idPeticion;
        String profile;
        Integer idPaciente;
        ProfilePersonaSelfdecode profileSelfdecode;
        SelfdecodeServiceProcess selfdecodeserviceprocess = new SelfdecodeServiceProcess();
        //List<VWCronSelfdecodeCargaArchivos> listArchivosCargaSelfdecode;
        List<VWCronSelfdecodeListos> listArchivosListosSelf;
        List<VWCronSelfdecodeListos> archivosOkForwardAndReverse;

        listArchivosListosSelf = sessionBeanBaseFachada.BuscarTodos(VWCronSelfdecodeListos.class);// Crea una lista con los objetos que estan listos 6 por consulta

        if (!listArchivosListosSelf.isEmpty() || listArchivosListosSelf != null) { // Verifica si se obtiene resultados listos para enviar 

            for (VWCronSelfdecodeListos archivo : listArchivosListosSelf) {

                idPeticion = archivo.getIdPeticion(); // Obtiene la peticion 
                // PENDIENTE CAMBIAR EL ESTADO CUANDO SE ENVIA A SELFDECODE PARA QUE NO VULEVA A CONSULTAR
                archivosOkForwardAndReverse = sessionBeanBaseFachada.obtenerArchivosListos(idPeticion); // consulta los que estan estado cargado-server, trae 2 registros o 1 

                // si aca despues de hacer la consulta no vienen los 2 no se ejecuta 
                if (archivosOkForwardAndReverse.size() == 2) {

                    if (archivosOkForwardAndReverse.get(0).getIdentificador().equals("1")) { 
                        //listArchivosCargaSelfdecode = sessionBeanBaseFachada.obtenerArchivosSelfdecode(idPeticion); // deberia obtener solo 2 objetos
                        forward_reads = "http://gencellservicetwo.com/files_varsome/" + archivosOkForwardAndReverse.get(0).getNombreArchivo();
                        reverse_reads = "http://gencellservicetwo.com/files_varsome/" + archivosOkForwardAndReverse.get(1).getNombreArchivo();
                        break;
                    } else if (archivosOkForwardAndReverse.get(1).getIdentificador().equals("1")) {
                        forward_reads = "http://gencellservicetwo.com/files_varsome/" + archivosOkForwardAndReverse.get(1).getNombreArchivo();
                        reverse_reads = "http://gencellservicetwo.com/files_varsome/" + archivosOkForwardAndReverse.get(0).getNombreArchivo();
                        break;
                    }

                    profileSelfdecode = sessionBeanBaseFachada.consultarProfile(idPeticion); // Consulta el idPaciente y el Profile

                    profile = profileSelfdecode.getIdProfileSelfdecode();
                    idPaciente = profileSelfdecode.getIdPaciente();

                    // Validación para que no este ningun parametro en nulo
                    if ((profile != null) && (!forward_reads.equals("")) && (reverse_reads.equals("")) && (idPaciente != null) && (idPeticion != null)) {

                        selfdecodeserviceprocess.iniciarEnvioArchivoSelfdecode(profile, forward_reads, reverse_reads, idPaciente, idPeticion);
                    } else {
                        System.out.println("Verifique que no tenga datos nulos");
                    }
                } else {
                    System.out.println("LOS ARCHIVOS NO ESTAN LISTOS O YA FUERON ENVIADOS");
                }
            }
        } else {
            System.out.println("Los Archivos no estan cargados");
        }
    }

    public void eliminarArchivosSelfdecode() {

        List<VWCronSelfFileId> listaFileId;
        List<VWCronSelfdecodeListos> archivosBorrar; // Para obtener el nombre del archivo a eliminar 
        SelfdecodeServiceProcess consultarEstado = new SelfdecodeServiceProcess();
        String estado;
        String file;
        Integer idPeticion;

        listaFileId = sessionBeanBaseFachada.BuscarTodos(VWCronSelfFileId.class); // Crea una lista con los objetos maximo 6

        for (VWCronSelfFileId fileIdSelfdecode : listaFileId) {

            idPeticion = fileIdSelfdecode.getIdPeticion();
            file = fileIdSelfdecode.getIdGenomeFile();
            estado = consultarEstado.obtenerEstadoFile(file);
            archivosBorrar = sessionBeanBaseFachada.obtenerArchivosListos(idPeticion); // trae 2 resultados

            if (archivosBorrar.size() == 2) {// valida si vienen los dos archivos para eliminar forward y reverse
                if (estado != null && estado.equals("COMPLETE")) {

                    File fichero1 = new File(PATH_ARCHIVOS_SELFDECODE + archivosBorrar.get(0).getNombreArchivo());
                    File fichero2 = new File(PATH_ARCHIVOS_SELFDECODE + archivosBorrar.get(1).getNombreArchivo());

                    // Valida existencia de fichero 1
                    if (!fichero1.exists()) {
                        sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLabSelfdecode(archivosBorrar.get(0).getId(), "FINALIZADO", "", "100");
                        System.out.println("El fichero " + archivosBorrar.get(0).getNombreArchivo() + " ya no existe");
                        return;
                    }
                    
                    // valida existencia de fichero 2
                    if (!fichero2.exists()) {
                        sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLabSelfdecode(archivosBorrar.get(1).getId(), "FINALIZADO", "", "100");
                        System.out.println("El fichero " + archivosBorrar.get(1).getNombreArchivo() + " ya no existe");
                        return;
                    }
                    
                    // Elimina Fichero 1
                    if (fichero1.delete()) {
                        sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLabSelfdecode(archivosBorrar.get(0).getId(), "", "FINALIZADO", "100");
                        System.out.println("El fichero "+ archivosBorrar.get(0).getNombreArchivo() + " ha sido borrados satisfactoriamente");
                    } else {
                        System.out.println("El fichero " + archivosBorrar.get(0).getNombreArchivo() + " no puede ser borrado");
                        sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLabSelfdecode(idPeticion, "", "ERROR BORRANDO ARCHIVO", "95");
                    }
                    
                    // Elimina Fichero 2 
                    if (fichero2.delete()) {
                        sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLabSelfdecode(archivosBorrar.get(1).getId(), "", "FINALIZADO", "100");
                        System.out.println("El fichero "+ archivosBorrar.get(1).getNombreArchivo() + " ha sido borrados satisfactoriamente");
                    } else {
                        System.out.println("El fichero " + archivosBorrar.get(1).getNombreArchivo() + " no puede ser borrado");
                        sessionBeanBaseFachada.actualizarEstadoYDescPeticionBioLabSelfdecode(idPeticion, "", "ERROR BORRANDO ARCHIVO", "95");
                    }
                }
            }else{
                System.out.println("ERROR OBTENIENDO LA INFORMACIÓN PARA ELIMINAR, tienen que ser 2 archivos por petición");
        }

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
