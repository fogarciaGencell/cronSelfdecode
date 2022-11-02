/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gencell.croncargaarchivos.ejb;

import gencell.croncargaarchivos.entities.VWCronArchivosCarga;
import gencell.croncargaarchivos.entities.VWCronSelfdecodeCargaArchivos;
import gencell.croncargaarchivos.entities.VWCronSelfdecodeListos;
import gencell.croncargaarchivos.selfdecode.ProfilePersonaSelfdecode;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author JOSEPH
 */
@Stateless
public class SessionBeanBaseFachada implements SessionBeanBaseFachadaLocal {

    @PersistenceContext(unitName = "CronCargaArchivos-PU")
    private EntityManager em;

    // Generales
    public <T> T BuscarPorID(Class<T> entityClass, Object primaryKey) {
        em.getEntityManagerFactory().getCache().evictAll();
        return em.find(entityClass, primaryKey);
    }

    public List BuscarTodos(Class entityClass) {
        em.getEntityManagerFactory().getCache().evictAll();
        return em.createQuery("select o "
                + "from " + entityClass.getSimpleName() + " o").getResultList();
    }

    public List BuscarTodosPorId(Class entityClass, List ids) {
        em.getEntityManagerFactory().getCache().evictAll();
        List entitys = new ArrayList();
        for (Object id : ids) {
            entitys.add(BuscarPorID(entityClass, id));
        }
        return entitys;
    }

    public void Crear(Object entity) {
        em.persist(entity);
        em.flush();
    }

    public void Borrar(Object entity) {
        em.remove(em.merge(entity));
        em.flush();
    }

    public <T> T Editar(T entity) {
        try {
            return em.merge(entity);
        } catch (Exception err) {
            return null;
        }
    }

    public void Refrescar(Object entity) {
        em.refresh(entity);
    }

    public List<VWCronArchivosCarga> obtenerArchivosCarga() {
        em.getEntityManagerFactory().getCache().evictAll();
        String query = "select * from VWCronArchivosCarga;";
        Query q = em.createNativeQuery(query, VWCronArchivosCarga.class);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return null;
        } else {
            return (List<VWCronArchivosCarga>) q.getResultList();
        }
    }

    public void actualizarEstadoResultadosOrden(Integer idResultadoOrden, String estado) {
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            String query = "UPDATE ResultadosOrden SET estadoCarga = '" + estado + "' WHERE idResultadosOrden = '" + idResultadoOrden + "';";
            Query q = em.createNativeQuery(query);
            Integer retorno = q.executeUpdate();
            System.out.print("Resultado actualizarResultadosOrden " + retorno);
            //return retorno.toString();
        } catch (Exception e) {
            System.out.print("Error en: actualizarResultadosOrden " + e.getMessage());
            e.printStackTrace();
            //return "Error: " + e.getMessage();
        }
    }

    public void actualizarURLResultadosOrden(Integer idResultadoOrden, String url) {
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            String query = "UPDATE ResultadosOrden SET urlResultado = '" + url + "' WHERE idResultadosOrden = '" + idResultadoOrden + "';";
            Query q = em.createNativeQuery(query);
            Integer retorno = q.executeUpdate();
            System.out.print("Resultado actualizarURLResultadosOrden " + retorno);
            //return retorno.toString();
        } catch (Exception e) {
            System.out.print("Error en: actualizarURLResultadosOrden " + e.getMessage());
            e.printStackTrace();
            //return "Error: " + e.getMessage();
        }
    }

    public void actualizarEstadoPeticionBioLab(Integer id, String estado, String porcentaje) {
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            String query = "UPDATE PeticionBioLab SET estado = '" + estado + "' , porcentaje='" + porcentaje + "' WHERE id = '" + id + "';";
            Query q = em.createNativeQuery(query);
            Integer retorno = q.executeUpdate();
            System.out.print("Resultado actualizarEstadoPeticionBioLab " + retorno);
            //return retorno.toString();
        } catch (Exception e) {
            System.out.print("Error en: actualizarEstadoPeticionBioLab " + e.getMessage());
            e.printStackTrace();
            //return "Error: " + e.getMessage();
        }
    }

    public void actualizarEstadoYDescPeticionBioLab(Integer id, String estado, String descripcion, String porcentaje) {
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            String query = "UPDATE PeticionBioLab SET estado = '" + estado + "' , descripcion = '" + descripcion + "' , porcentaje='" + porcentaje + "' WHERE id = '" + id + "';";
            Query q = em.createNativeQuery(query);
            Integer retorno = q.executeUpdate();
            System.out.print("Resultado actualizarEstadoPeticionBioLab " + retorno);
            //return retorno.toString();
        } catch (Exception e) {
            System.out.print("Error en: actualizarEstadoPeticionBioLab " + e.getMessage());
            e.printStackTrace();
            //return "Error: " + e.getMessage();
        }
    }

    public void actualizarEstadoPeticionIdVarsomeBioLab(Integer id, String estado, String idVarsome, String porcentaje) {
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            String query = "UPDATE PeticionBioLab SET estado = '" + estado + "', idVarsome = '" + idVarsome + "' , porcentaje='" + porcentaje + "' WHERE id = '" + id + "';";
            Query q = em.createNativeQuery(query);
            Integer retorno = q.executeUpdate();
            System.out.print("Resultado actualizarEstadoPeticionBioLab " + retorno);
            //return retorno.toString();
        } catch (Exception e) {
            System.out.print("Error en: actualizarEstadoPeticionBioLab " + e.getMessage());
            e.printStackTrace();
            //return "Error: " + e.getMessage();
        }
    }

    @Override
    public ProfilePersonaSelfdecode consultarProfile(Integer idPeticion) {
       
        em.getEntityManagerFactory().getCache().evictAll();
        String query = "SELECT     pps.idPaciente,\n"
                    + "pps.idProfileSelfdecode \n"
                     + "FROM       PeticionBioLab pbl \n"
                     + "JOIN       Peticion pet \n"
                     + "ON         pbl.idPeticion=pet.idPeticion \n"
                     + "JOIN       ProfilePersonaSelfdecode pps \n"
                     + "ON         pps.idPaciente=pet.idPaciente \n"
                     + "WHERE      pbl.idPeticion=" + idPeticion + ";";
        Query q = em.createNativeQuery(query, ProfilePersonaSelfdecode.class);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return null;
        } else {
            return (ProfilePersonaSelfdecode) q.getResultList().get(0);
        }
       
    }

//    @Override
//    public List<VWCronSelfdecodeCargaArchivos> obtenerArchivosSelfdecode(Integer idPeticion) {
//        em.getEntityManagerFactory().getCache().evictAll();
//        String query = "select * from VWCronSelfdecodeCargaArchivos where idPeticion = "+ idPeticion+ " limit 2;";
//        Query q = em.createNativeQuery(query, VWCronSelfdecodeCargaArchivos.class);
//        if (q.getResultList() == null || q.getResultList().isEmpty()) {
//            return null;
//        } else {
//            return (List<VWCronSelfdecodeCargaArchivos>) q.getResultList();
//        }
//        
//    }

    @Override
    public List<VWCronSelfdecodeListos> obtenerArchivosListos(Integer idPeticion) {
        em.getEntityManagerFactory().getCache().evictAll();
        String query = "select * from VWCronSelfdecodeListos where idPeticion = "+ idPeticion+ " limit 2;";
        Query q = em.createNativeQuery(query, VWCronSelfdecodeListos.class);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return null;
        } else {
            return (List<VWCronSelfdecodeListos>) q.getResultList();
        }
    }

}
