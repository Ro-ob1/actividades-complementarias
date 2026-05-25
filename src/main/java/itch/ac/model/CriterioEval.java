package itch.ac.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "criterio_eval")
public class CriterioEval {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Double valor;

	@ManyToOne
	@JoinColumn(name = "id_evaluacion")
	private Evaluacion evaluacion;

	@ManyToOne
	@JoinColumn(name = "id_criterio")
	private CriterioBase criterio;

	@ManyToOne
	@JoinColumn(name = "id_nivel")
	private NivelDesempenio nivel;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Evaluacion getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(Evaluacion evaluacion) {
		this.evaluacion = evaluacion;
	}

	public CriterioBase getCriterio() {
		return criterio;
	}

	public void setCriterio(CriterioBase criterio) {
		this.criterio = criterio;
	}

	public NivelDesempenio getNivel() {
		return nivel;
	}

	public void setNivel(NivelDesempenio nivel) {
		this.nivel = nivel;
	}
}