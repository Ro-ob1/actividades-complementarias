package itch.ac.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nivel_desempenio")
public class NivelDesempenio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nombre;
	private Double rangoMin;
	private Double rangoMax;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getRangoMin() {
		return rangoMin;
	}

	public void setRangoMin(Double rangoMin) {
		this.rangoMin = rangoMin;
	}

	public Double getRangoMax() {
		return rangoMax;
	}

	public void setRangoMax(Double rangoMax) {
		this.rangoMax = rangoMax;
	}
}