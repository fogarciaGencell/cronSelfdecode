/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gencell.croncargaarchivos;

import gencell.croncargaarchivos.controller.MBController;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author JOSEPH
 */
public class JobCargaArchivos implements Job {

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        System.out.println("**************************************************** INICIA EJECUCION *************************************************************************************************************" + new Date());
        System.out.println("---------INICIO CRON CARGA ARCHIVOS VARSOME---------");        
        /*MBController controller = new MBController();
        controller.ejecutarTareaCargaArchivosSelfdecode();*/
        System.out.println("---------FINALIZA CRON CARGA VARSOME---------");    
        
        
    }
}
    