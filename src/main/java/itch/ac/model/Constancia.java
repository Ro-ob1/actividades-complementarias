package itch.ac.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "constancia")
public class Constancia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private LocalDate fechaGeneracion;
	private Double valorNumerico;
	private Integer valorCurricular;
	private String archivoPdf;
	private String estatus;

	@ManyToOne
	@JoinColumn(name = "id_inscripcion")
	private Inscripcion inscripcion;

	@ManyToOne
	@JoinColumn(name = "id_semestre")
	private Semestre semestre;

	@ManyToOne
	@JoinColumn(name = "id_nivel")
	private NivelDesempenio nivel;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getFechaGeneracion() {
		return fechaGeneracion;
	}

	public void setFechaGeneracion(LocalDate fechaGeneracion) {
		this.fechaGeneracion = fechaGeneracion;
	}

	public Double getValorNumerico() {
		return valorNumerico;
	}

	public void setValorNumerico(Double valorNumerico) {
		this.valorNumerico = valorNumerico;
	}

	public Integer getValorCurricular() {
		return valorCurricular;
	}

	public void setValorCurricular(Integer valorCurricular) {
		this.valorCurricular = valorCurricular;
	}

	public String getArchivoPdf() {
		return archivoPdf;
	}

	public void setArchivoPdf(String archivoPdf) {
		this.archivoPdf = archivoPdf;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public Inscripcion getInscripcion() {
		return inscripcion;
	}

	public void setInscripcion(Inscripcion inscripcion) {
		this.inscripcion = inscripcion;
	}

	public Semestre getSemestre() {
		return semestre;
	}

	public void setSemestre(Semestre semestre) {
		this.semestre = semestre;
	}

	public NivelDesempenio getNivel() {
		return nivel;
	}

	public void setNivel(NivelDesempenio nivel) {
		this.nivel = nivel;
	}
}