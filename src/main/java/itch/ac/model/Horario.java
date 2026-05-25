package itch.ac.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "horario")
public class Horario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String diaSemana;
	private java.time.LocalTime horaInicio;
	private java.time.LocalTime horaFin;

	@ManyToOne
	@JoinColumn(name = "id_actividad")
	private Actividad actividad;

	@ManyToOne
	@JoinColumn(name = "id_espacio")
	private Espacio espacio;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(String diaSemana) {
		this.diaSemana = diaSemana;
	}

	public java.time.LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(java.time.LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public java.time.LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(java.time.LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	public Actividad getActividad() {
		return actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	public Espacio getEspacio() {
		return espacio;
	}

	public void setEspacio(Espacio espacio) {
		this.espacio = espacio;
	}
}