/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gencell.croncargaarchivos.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author USUARIO
 */
@Entity
@Table(name = "CargueArchivosSelf")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CargueArchivosSelf.findAll", query = "SELECT c FROM CargueArchivosSelf c")
    , @NamedQuery(name = "CargueArchivosSelf.findById", query = "SELECT c FROM CargueArchivosSelf c WHERE c.id = :id")
    , @NamedQuery(name = "CargueArchivosSelf.findByIdPersonas", query = "SELECT c FROM CargueArchivosSelf c WHERE c.idPersonas = :idPersonas")
    , @NamedQuery(name = "CargueArchivosSelf.findByIdProfileSelfdecode", query = "SELECT c FROM CargueArchivosSelf c WHERE c.idProfileSelfdecode = :idProfileSelfdecode")
    , @NamedQuery(name = "CargueArchivosSelf.findByIdGenomeFile", query = "SELECT c FROM CargueArchivosSelf c WHERE c.idGenomeFile = :idGenomeFile")
    , @NamedQuery(name = "CargueArchivosSelf.findByEstado", query = "SELECT c FROM CargueArchivosSelf c WHERE c.estado = :estado")})
public class CargueArchivosSelf implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "idPersonas")
    private Integer idPersonas;
    @Size(max = 200)
    @Column(name = "idProfileSelfdecode")
    private String idProfileSelfdecode;
    @Size(max = 200)
    @Column(name = "idGenomeFile")
    private String idGenomeFile;
    @Size(max = 20)
    @Column(name = "estado")
    private String estado;

    public CargueArchivosSelf() {
    }

    public CargueArchivosSelf(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdPersonas() {
        return idPersonas;
    }

    public void setIdPersonas(Integer idPersonas) {
        this.idPersonas = idPersonas;
    }

    public String getIdProfileSelfdecode() {
        return idProfileSelfdecode;
    }

    public void setIdProfileSelfdecode(String idProfileSelfdecode) {
        this.idProfileSelfdecode = idProfileSelfdecode;
    }

    public String getIdGenomeFile() {
        return idGenomeFile;
    }

    public void setIdGenomeFile(String idGenomeFile) {
        this.idGenomeFile = idGenomeFile;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CargueArchivosSelf)) {
            return false;
        }
        CargueArchivosSelf other = (CargueArchivosSelf) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gencell.croncargaarchivos.entities.CargueArchivosSelf[ id=" + id + " ]";
    }
    
}
