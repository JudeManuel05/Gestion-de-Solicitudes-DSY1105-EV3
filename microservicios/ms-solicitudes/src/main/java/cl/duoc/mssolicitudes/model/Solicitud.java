package cl.duoc.mssolicitudes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA: tabla "solicitudes".
 * El código de seguimiento (SOL-XXXXXXXX) es único.
 * Las fechas se guardan en milisegundos (igual que en la app Android).
 */
@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false, length = 60)
    private String categoria;

    @Column(length = 120)
    private String servicio;

    @Column(length = 500)
    private String evidenciaUri;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(length = 120)
    private String correoCliente;

    @Column(nullable = false)
    private Long fechaCreacion;

    @Column(nullable = false)
    private Long fechaActualizacion;

    public Solicitud() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }
    public String getEvidenciaUri() { return evidenciaUri; }
    public void setEvidenciaUri(String evidenciaUri) { this.evidenciaUri = evidenciaUri; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCorreoCliente() { return correoCliente; }
    public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }
    public Long getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Long fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public Long getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Long fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
