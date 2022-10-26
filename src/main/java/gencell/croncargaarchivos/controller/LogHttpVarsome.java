/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gencell.croncargaarchivos.controller;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author JOSEPH
 */
@Entity
@Table(name = "LogHttpVarsome")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LogHttpVarsome.findAll", query = "SELECT l FROM LogHttpVarsome l")
    , @NamedQuery(name = "LogHttpVarsome.findById", query = "SELECT l FROM LogHttpVarsome l WHERE l.id = :id")
    , @NamedQuery(name = "LogHttpVarsome.findByFecha", query = "SELECT l FROM LogHttpVarsome l WHERE l.fecha = :fecha")
    , @NamedQuery(name = "LogHttpVarsome.findBySend", query = "SELECT l FROM LogHttpVarsome l WHERE l.send = :send")
    , @NamedQuery(name = "LogHttpVarsome.findByResponseService", query = "SELECT l FROM LogHttpVarsome l WHERE l.responseService = :responseService")
    , @NamedQuery(name = "LogHttpVarsome.findByResponseCode", query = "SELECT l FROM LogHttpVarsome l WHERE l.responseCode = :responseCode")
    , @NamedQuery(name = "LogHttpVarsome.findByResponseMsg", query = "SELECT l FROM LogHttpVarsome l WHERE l.responseMsg = :responseMsg")})
public class LogHttpVarsome implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;    
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;    
    @Column(name = "send")
    private String send;    
    @Column(name = "responseService")
    private String responseService;    
    @Column(name = "responseCode")
    private String responseCode;    
    @Column(name = "responseMsg")
    private String responseMsg;
    @Column(name = "nombreArchivo")
    private String nombreArchivo;
    @Column(name = "nombreUsuarioCreacion")
    private String nombreUsuarioCreacion;
    @Column(name = "usuarioCreacion")
    private String usuarioCreacion;

    public LogHttpVarsome() {
    }

    public LogHttpVarsome(Integer id) {
        this.id = id;
    }

    public LogHttpVarsome(Integer id, Date fecha) {
        this.id = id;
        this.fecha = fecha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getResponseService() {
        return responseService;
    }

    public void setResponseService(String responseService) {
        this.responseService = responseService;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }        

    public String getNombreUsuarioCreacion() {
        return nombreUsuarioCreacion;
    }

    public void setNombreUsuarioCreacion(String nombreUsuarioCreacion) {
        this.nombreUsuarioCreacion = nombreUsuarioCreacion;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
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
        if (!(object instanceof LogHttpVarsome)) {
            return false;
        }
        LogHttpVarsome other = (LogHttpVarsome) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gencell.laboratoriogp.entities.varsome.LogHttpVarsome[ id=" + id + " ]";
    }
    
}
