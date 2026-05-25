package itch.ac.controller;

import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itch.ac.model.Alumno;
import itch.ac.model.Constancia;
import itch.ac.model.Evaluacion;
import itch.ac.model.Inscripcion;
import itch.ac.service.IAlumnoService;
import itch.ac.service.IConstanciaService;
import itch.ac.service.IEvaluacionService;
import itch.ac.service.IInscripcionService;
import itch.ac.service.ISemestreService;

@Controller
@RequestMapping("/constancia")
public class ConstanciaController {

	@Autowired private IConstanciaService  constanciaService;
	@Autowired private IInscripcionService inscripcionService;
	@Autowired private IEvaluacionService  evaluacionService;
	@Autowired private ISemestreService    semestreService;
	@Autowired private IAlumnoService      alumnoService;

	@GetMapping("/constancias")
	public String listar(
			@RequestParam(required = false) String alumno,
			@RequestParam(required = false) Integer idSemestre,
			@RequestParam(required = false) String estatus,
			Authentication auth,
			Model model) {

		boolean esAlumno = auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ALUMNO"));

		List<Constancia> constancias;

		if (esAlumno) {
			Alumno alumnoObj = alumnoService.buscarPorNumControl(auth.getName());
			if (alumnoObj != null) {
				final Integer alumnoId = alumnoObj.getId();
				constancias = constanciaService.buscarTodasConstancias().stream()
						.filter(c -> c.getInscripcion().getAlumno().getId().equals(alumnoId))
						.collect(Collectors.toList());
			} else {
				constancias = List.of();
			}
		} else {
			constancias = constanciaService.buscarTodasConstancias();

			if (alumno != null && !alumno.isBlank()) {
				String q = alumno.toLowerCase();
				constancias = constancias.stream()
						.filter(c -> {
							String nombre = c.getInscripcion().getAlumno().getPersona().getNombre()
									+ " " + c.getInscripcion().getAlumno().getPersona().getApellido();
							return nombre.toLowerCase().contains(q);
						})
						.collect(Collectors.toList());
			}
			if (idSemestre != null) {
				constancias = constancias.stream()
						.filter(c -> c.getSemestre() != null && c.getSemestre().getId().equals(idSemestre))
						.collect(Collectors.toList());
			}
			if (estatus != null && !estatus.isBlank()) {
				constancias = constancias.stream()
						.filter(c -> estatus.equals(c.getEstatus()))
						.collect(Collectors.toList());
			}
		}

		model.addAttribute("constancias", constancias);
		model.addAttribute("semestres", semestreService.buscarTodosSemestres());
		model.addAttribute("alumno", alumno);
		model.addAttribute("idSemestre", idSemestre);
		model.addAttribute("estatus", estatus);
		model.addAttribute("esAlumno", esAlumno);
		return "constancia/listaConstancias";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		List<Inscripcion> aprobadas = inscripcionService.buscarInscripcionesPorEstatus("APROBADA");
		List<InscripcionEvalDto> datos = new ArrayList<>();
		for (Inscripcion i : aprobadas) {
			if (!evaluacionService.existeEvaluacionPorInscripcionId(i.getId())) continue;
			if (constanciaService.existeConstanciaPorInscripcionId(i.getId())) continue;
			Evaluacion eval = evaluacionService.buscarPorInscripcionId(i.getId());
			datos.add(new InscripcionEvalDto(i, eval));
		}
		model.addAttribute("constancia", new Constancia());
		model.addAttribute("inscripcionesData", datos);
		return "constancia/formularioConstancia";
	}

	@PostMapping("/guardar")
	public String guardar(
			Constancia constancia,
			RedirectAttributes attributes) {

		if (constancia.getInscripcion() != null && constancia.getInscripcion().getId() != null) {
			constancia.setInscripcion(inscripcionService.buscarPorId(constancia.getInscripcion().getId()));
		}

		Inscripcion inscripcion = constancia.getInscripcion();
		if (inscripcion == null || inscripcion.getId() == null) {
			attributes.addFlashAttribute("msg", "⚠ Selecciona una inscripción válida.");
			return "redirect:/constancia/nuevo";
		}

		if (constancia.getId() == null) {
			if (!evaluacionService.existeEvaluacionPorInscripcionId(inscripcion.getId())) {
				attributes.addFlashAttribute("msg",
						"⚠ No se puede generar la constancia: el alumno no tiene evaluación completada.");
				return "redirect:/constancia/nuevo";
			}
			if (constanciaService.existeConstanciaPorInscripcionId(inscripcion.getId())) {
				attributes.addFlashAttribute("msg",
						"⚠ Ya existe una constancia para esta inscripción.");
				return "redirect:/constancia/nuevo";
			}
			Evaluacion evaluacion = evaluacionService.buscarPorInscripcionId(inscripcion.getId());
			constancia.setValorNumerico(evaluacion.getValorNumerico());
			constancia.setNivel(evaluacion.getNivel());
			constancia.setSemestre(inscripcion.getActividad().getSemestre());
			constancia.setFechaGeneracion(LocalDate.now());
			constancia.setEstatus("GENERADA");
			// valorCurricular derivado de la disciplina
			if (inscripcion.getActividad().getDisciplina() != null) {
				constancia.setValorCurricular(inscripcion.getActividad().getDisciplina().getCreditos());
			}
		}

		constanciaService.guardarConstancia(constancia);
		attributes.addFlashAttribute("msg", "Constancia guardada correctamente.");
		return "redirect:/constancia/constancias";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		model.addAttribute("constancia", constanciaService.buscarPorId(id));
		return "constancia/detalleConstancia";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		model.addAttribute("constancia", constanciaService.buscarPorId(id));
		model.addAttribute("inscripcionesData", new ArrayList<>());
		return "constancia/formularioConstancia";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Constancia constancia = constanciaService.eliminarPorId(id);
		if (constancia == null) {
			attributes.addFlashAttribute("msg", "⚠ No se pudo eliminar la constancia.");
		} else {
			attributes.addFlashAttribute("msg", "Constancia eliminada correctamente.");
		}
		return "redirect:/constancia/constancias";
	}

	@GetMapping("/pdf/{id}")
	public void generarPdf(@PathVariable Integer id, HttpServletResponse response) throws Exception {
		Constancia c = constanciaService.buscarPorId(id);
		if (c == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

		String alumnoNombre = c.getInscripcion().getAlumno().getPersona().getNombre()
				+ " " + c.getInscripcion().getAlumno().getPersona().getApellido();
		String numControl = c.getInscripcion().getAlumno().getNumControl();
		String actividadNombre = c.getInscripcion().getActividad().getNombre();

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition",
				"inline; filename=\"constancia_" + numControl + ".pdf\"");

		Document doc = new Document(PageSize.LETTER, 72, 72, 72, 72);
		PdfWriter.getInstance(doc, response.getOutputStream());
		doc.open();

		Color verde = new Color(0x00, 0x5C, 0x2E);
		Color gris  = new Color(0x55, 0x55, 0x55);

		Font fInst    = new Font(Font.HELVETICA, 13, Font.BOLD,   verde);
		Font fSub     = new Font(Font.HELVETICA, 10, Font.NORMAL, gris);
		Font fTitulo  = new Font(Font.HELVETICA, 20, Font.BOLD,   verde);
		Font fNormal  = new Font(Font.HELVETICA, 12, Font.NORMAL);
		Font fNombre  = new Font(Font.HELVETICA, 17, Font.BOLD);
		Font fDato    = new Font(Font.HELVETICA, 11, Font.NORMAL, gris);
		Font fPie     = new Font(Font.HELVETICA,  9, Font.ITALIC, gris);

		// Institución
		Paragraph pInst = new Paragraph("Instituto Tecnológico de Chilpancingo — TecNM", fInst);
		pInst.setAlignment(Element.ALIGN_CENTER);
		doc.add(pInst);

		Paragraph pSub = new Paragraph("Sistema de Actividades Complementarias", fSub);
		pSub.setAlignment(Element.ALIGN_CENTER);
		doc.add(pSub);

		doc.add(new Paragraph(" "));
		LineSeparator ls = new LineSeparator(1.5f, 100, verde, Element.ALIGN_CENTER, 0);
		doc.add(new Chunk(ls));
		doc.add(new Paragraph(" "));

		// Título
		Paragraph pTitulo = new Paragraph("CONSTANCIA DE ACREDITACIÓN", fTitulo);
		pTitulo.setAlignment(Element.ALIGN_CENTER);
		pTitulo.setSpacingBefore(12);
		pTitulo.setSpacingAfter(18);
		doc.add(pTitulo);

		// Introducción
		Paragraph pIntro = new Paragraph("Se hace constar que el(la) alumno(a):", fNormal);
		pIntro.setAlignment(Element.ALIGN_CENTER);
		doc.add(pIntro);
		doc.add(new Paragraph(" "));

		// Nombre alumno
		Paragraph pNombre = new Paragraph(alumnoNombre.toUpperCase(), fNombre);
		pNombre.setAlignment(Element.ALIGN_CENTER);
		doc.add(pNombre);

		Paragraph pCtrl = new Paragraph("No. Control: " + numControl, fDato);
		pCtrl.setAlignment(Element.ALIGN_CENTER);
		doc.add(pCtrl);
		doc.add(new Paragraph(" "));

		Paragraph pAcred = new Paragraph("Acreditó satisfactoriamente la Actividad Complementaria:", fNormal);
		pAcred.setAlignment(Element.ALIGN_CENTER);
		doc.add(pAcred);
		doc.add(new Paragraph(" "));

		// Actividad
		Font fActividad = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph pAct = new Paragraph(actividadNombre.toUpperCase(), fActividad);
		pAct.setAlignment(Element.ALIGN_CENTER);
		doc.add(pAct);

		// Disciplina
		if (c.getInscripcion().getActividad().getDisciplina() != null) {
			Paragraph pDisc = new Paragraph(
					"Disciplina: " + c.getInscripcion().getActividad().getDisciplina().getNombre(), fDato);
			pDisc.setAlignment(Element.ALIGN_CENTER);
			doc.add(pDisc);
		}

		// Semestre
		if (c.getSemestre() != null) {
			Paragraph pSem = new Paragraph("Semestre: " + c.getSemestre().getNombre(), fDato);
			pSem.setAlignment(Element.ALIGN_CENTER);
			doc.add(pSem);
		}

		doc.add(new Paragraph(" "));

		// Evaluación y créditos
		String nivelNombre = c.getNivel() != null ? " — Nivel: " + c.getNivel().getNombre() : "";
		Paragraph pGrade = new Paragraph(
				"Evaluación obtenida: " + c.getValorNumerico() + "/100" + nivelNombre, fNormal);
		pGrade.setAlignment(Element.ALIGN_CENTER);
		doc.add(pGrade);

		if (c.getValorCurricular() != null) {
			Paragraph pCred = new Paragraph("Valor curricular: " + c.getValorCurricular() + " crédito(s)", fNormal);
			pCred.setAlignment(Element.ALIGN_CENTER);
			doc.add(pCred);
		}

		doc.add(new Paragraph(" "));
		doc.add(new Paragraph(" "));

		// Fecha
		Paragraph pFecha = new Paragraph("Fecha de emisión: " + c.getFechaGeneracion(), fDato);
		pFecha.setAlignment(Element.ALIGN_CENTER);
		doc.add(pFecha);

		doc.add(new Paragraph(" "));
		doc.add(new Paragraph(" "));
		doc.add(new Paragraph(" "));

		// Línea de firma
		Paragraph pLinea = new Paragraph("_______________________________", fNormal);
		pLinea.setAlignment(Element.ALIGN_CENTER);
		doc.add(pLinea);

		Paragraph pFirma = new Paragraph("Firma y sello del encargado", fPie);
		pFirma.setAlignment(Element.ALIGN_CENTER);
		doc.add(pFirma);

		doc.add(new Paragraph(" "));
		doc.add(new Chunk(ls));
		doc.add(new Paragraph(" "));

		Paragraph pPie = new Paragraph("IT Chilpancingo — TecNM · Actividades Complementarias", fPie);
		pPie.setAlignment(Element.ALIGN_CENTER);
		doc.add(pPie);

		doc.close();
	}

	public static class InscripcionEvalDto {
		private final Inscripcion inscripcion;
		private final Evaluacion  evaluacion;

		public InscripcionEvalDto(Inscripcion inscripcion, Evaluacion evaluacion) {
			this.inscripcion = inscripcion;
			this.evaluacion  = evaluacion;
		}

		public Inscripcion getInscripcion() { return inscripcion; }
		public Evaluacion  getEvaluacion()  { return evaluacion; }
	}
}
