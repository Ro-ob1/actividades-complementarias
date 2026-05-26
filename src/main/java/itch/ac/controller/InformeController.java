package itch.ac.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import itch.ac.model.Constancia;
import itch.ac.model.Inscripcion;
import itch.ac.model.Semestre;
import itch.ac.service.IConstanciaService;
import itch.ac.service.IInscripcionService;
import itch.ac.service.ISemestreService;

@Controller
@RequestMapping("/informe")
public class InformeController {

	@Autowired private ISemestreService semestreService;
	@Autowired private IInscripcionService inscripcionService;
	@Autowired private IConstanciaService constanciaService;

	@GetMapping
	public String informe(@RequestParam(required = false) Integer idSemestre, Model model) {

		List<Semestre> semestres = semestreService.buscarTodosSemestres();
		model.addAttribute("semestres", semestres);

		Semestre semestre = idSemestre != null
				? semestreService.buscarPorId(idSemestre)
				: semestreService.buscarSemestreActivo();

		if (semestre == null && !semestres.isEmpty()) {
			semestre = semestres.get(0);
		}

		if (semestre != null) {
			model.addAttribute("idSemestre", semestre.getId());
			model.addAttribute("semestre", semestre);

			List<Inscripcion> inscripciones = inscripcionService.buscarInscripcionesPorSemestre(semestre);
			inscripciones.sort((a, b) -> a.getActividad().getNombre().compareTo(b.getActividad().getNombre()));

			final Integer semId = semestre.getId();
			List<Constancia> constancias = constanciaService.buscarTodasConstancias().stream()
					.filter(c -> c.getSemestre() != null && semId.equals(c.getSemestre().getId()))
					.collect(Collectors.toList());

			Map<Integer, Constancia> constByInscId = constancias.stream()
					.collect(Collectors.toMap(c -> c.getInscripcion().getId(), c -> c, (a, b) -> a));

			// Build flat list of report rows
			List<Map<String, Object>> filas = new ArrayList<>();
			for (Inscripcion ins : inscripciones) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("actividadNombre", ins.getActividad().getNombre());
				row.put("alumnoNombre", ins.getAlumno().getPersona().getNombre()
						+ " " + ins.getAlumno().getPersona().getApellido());
				row.put("numControl", ins.getAlumno().getNumControl());
				row.put("carrera", ins.getAlumno().getCarrera() != null
						? ins.getAlumno().getCarrera().getNombre() : "—");
				row.put("estatus", ins.getEstatusSolicitud());
				Constancia c = constByInscId.get(ins.getId());
				row.put("valorNumerico", c != null && c.getValorNumerico() != null ? c.getValorNumerico() : null);
				row.put("nivel", c != null && c.getNivel() != null ? c.getNivel().getNombre() : null);
				row.put("creditos", c != null && c.getValorCurricular() != null ? c.getValorCurricular() : null);
				filas.add(row);
			}

			// Summary stats
			long totalInscripciones = inscripciones.size();
			long aprobadas = inscripciones.stream().filter(i -> "APROBADA".equals(i.getEstatusSolicitud())).count();
			long rechazadas = inscripciones.stream().filter(i -> "RECHAZADA".equals(i.getEstatusSolicitud())).count();
			long pendientes = inscripciones.stream().filter(i -> "PENDIENTE".equals(i.getEstatusSolicitud())).count();
			long constanciasEmitidas = constancias.size();
			int totalCreditos = constancias.stream()
					.filter(c2 -> "ENTREGADA".equals(c2.getEstatus()))
					.mapToInt(c2 -> c2.getValorCurricular() != null ? c2.getValorCurricular() : 0)
					.sum();

			// Chart: bar — activities with enrollment counts
			List<String> actLabels = inscripciones.stream()
					.map(i -> i.getActividad().getNombre())
					.distinct()
					.collect(Collectors.toList());
			List<Long> actCounts = actLabels.stream()
					.map(nombre -> inscripciones.stream()
							.filter(i -> nombre.equals(i.getActividad().getNombre()))
							.count())
					.collect(Collectors.toList());

			model.addAttribute("filas", filas);
			model.addAttribute("totalInscripciones", totalInscripciones);
			model.addAttribute("aprobadas", aprobadas);
			model.addAttribute("rechazadas", rechazadas);
			model.addAttribute("pendientes", pendientes);
			model.addAttribute("constanciasEmitidas", constanciasEmitidas);
			model.addAttribute("totalCreditos", totalCreditos);
			model.addAttribute("actLabels", actLabels);
			model.addAttribute("actCounts", actCounts);
		}

		return "informe/informe";
	}
}
