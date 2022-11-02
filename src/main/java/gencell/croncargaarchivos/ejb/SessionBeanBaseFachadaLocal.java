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
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author JOSEPH
 */
@Local
public interface SessionBeanBaseFachadaLocal {

    public <T> T BuscarPorID(Class<T> entityClass, Object primaryKey);

    public List BuscarTodos(Class entityClass);

    public List BuscarTodosPorId(Class entityClass, List ids);

    public void Crear(Object entity);

    public void Borrar(Object entity);

    public <T> T Editar(T entity);

    public void Refrescar(Object entity);

    public List<VWCronArchivosCarga> obtenerArchivosCarga();

    public void actualizarEstadoResultadosOrden(Integer idResultadoOrden, String estado);

    public void actualizarURLResultadosOrden(Integer idResultadoOrden, String url);

    public void actualizarEstadoPeticionBioLab(Integer id, String estado, String porcentaje);

    public void actualizarEstadoYDescPeticionBioLab(Integer id, String estado, String descripcion, String porcentaje);

    public void actualizarEstadoPeticionIdVarsomeBioLab(Integer id, String estado, String idVarsome, String porcentaje);

    public ProfilePersonaSelfdecode consultarProfile(Integer idPeticion);  
    
    //public List<VWCronSelfdecodeCargaArchivos> obtenerArchivosSelfdecode(Integer idPeticion);
    
    public List<VWCronSelfdecodeListos> obtenerArchivosListos(Integer idPeticion);
    
    public void actualizarEstadoBiolabSelfdecode(Integer idPeticion, String estado, String idVarsome, String porcentaje);
    
    public void actualizarEstadoYDescPeticionBioLabSelfdecode(Integer id, String estado, String descripcion, String porcentaje);
}
