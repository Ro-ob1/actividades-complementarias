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
@Table(name = "inscripcion")
public class Inscripcion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private LocalDate fechaSolicitud;
	private String estatusSolicitud;
	private String motivoRechazo;
	private String archivoHorario;

	@ManyToOne
	@JoinColumn(name = "id_alumno")
	private Alumno alumno;

	@ManyToOne
	@JoinColumn(name = "id_actividad")
	private Actividad actividad;

	@ManyToOne
	@JoinColumn(name = "id_encargado")
	private Encargado encargado;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getFechaSolicitud() {
		return fechaSolicitud;
	}

	public void setFechaSolicitud(LocalDate fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}

	public String getEstatusSolicitud() {
		return estatusSolicitud;
	}

	public void setEstatusSolicitud(String estatusSolicitud) {
		this.estatusSolicitud = estatusSolicitud;
	}

	public String getMotivoRechazo() {
		return motivoRechazo;
	}

	public void setMotivoRechazo(String motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}

	public String getArchivoHorario() {
		return archivoHorario;
	}

	public void setArchivoHorario(String archivoHorario) {
		this.archivoHorario = archivoHorario;
	}

	public Alumno getAlumno() {
		return alumno;
	}

	public void setAlumno(Alumno alumno) {
		this.alumno = alumno;
	}

	public Actividad getActividad() {
		return actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	public Encargado getEncargado() {
		return encargado;
	}

	public void setEncargado(Encargado encargado) {
		this.encargado = encargado;
	}
}