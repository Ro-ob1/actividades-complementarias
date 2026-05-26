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
			// Alumno solo ve sus constancias ya ENTREGADAS (documento físico recibido)
			Alumno alumnoObj = alumnoService.buscarPorNumControl(auth.getName());
			if (alumnoObj != null) {
				final Integer alumnoId = alumnoObj.getId();
				constancias = constanciaService.buscarTodasConstancias().stream()
						.filter(c -> c.getInscripcion().getAlumno().getId().equals(alumnoId)
								  && "ENTREGADA".equals(c.getEstatus()))
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

	@GetMapping("/entregar/{id}")
	public String marcarEntregada(@PathVariable Integer id, RedirectAttributes attributes) {
		Constancia constancia = constanciaService.buscarPorId(id);
		if (constancia == null) {
			attributes.addFlashAttribute("msg", "⚠ No se encontró la constancia.");
		} else if ("ENTREGADA".equals(constancia.getEstatus())) {
			attributes.addFlashAttribute("msg", "⚠ La constancia ya fue marcada como entregada.");
		} else {
			constancia.setEstatus("ENTREGADA");
			constanciaService.guardarConstancia(constancia);
			attributes.addFlashAttribute("msg", "Constancia marcada como entregada correctamente.");
		}
		return "redirect:/constancia/constancias";
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

		String alumnoNombre    = c.getInscripcion().getAlumno().getPersona().getNombre()
				+ " " + c.getInscripcion().getAlumno().getPersona().getApellido();
		String numControl      = c.getInscripcion().getAlumno().getNumControl();
		String actividadNombre = c.getInscripcion().getActividad().getNombre();
		String carrera         = c.getInscripcion().getAlumno().getCarrera() != null
				? c.getInscripcion().getAlumno().getCarrera().getNombre() : "";

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition",
				"inline; filename=\"constancia_" + numControl + ".pdf\"");

		Document doc = new Document(PageSize.LETTER, 50, 50, 30, 40);
		PdfWriter.getInstance(doc, response.getOutputStream());
		doc.open();

		// ── Colores TecNM ───────────────────────────────────────────────────
		Color azulTecnm  = new Color(0x00, 0x47, 0x9D);
		Color azulOscuro = new Color(0x00, 0x2A, 0x6E);
		Color azulClaro  = new Color(0xE6, 0xEF, 0xFA);
		Color azulBorde  = new Color(0xAA, 0xC4, 0xE8);
		Color grisOsc    = new Color(0x1A, 0x1A, 0x1A);
		Color grisClr    = new Color(0x55, 0x55, 0x55);

		// ── Fuentes ─────────────────────────────────────────────────────────
		Font fInstNm  = new Font(Font.HELVETICA, 11, Font.BOLD,   azulOscuro);
		Font fInstSub = new Font(Font.HELVETICA,  9, Font.NORMAL, grisClr);
		Font fFolio   = new Font(Font.HELVETICA,  7, Font.ITALIC, grisClr);
		Font fTitulo  = new Font(Font.HELVETICA, 18, Font.BOLD,   azulTecnm);
		Font fSubTit  = new Font(Font.HELVETICA,  9, Font.NORMAL, grisClr);
		Font fNormal  = new Font(Font.HELVETICA, 10, Font.NORMAL, grisOsc);
		Font fNombre  = new Font(Font.HELVETICA, 16, Font.BOLD,   grisOsc);
		Font fSubNom  = new Font(Font.HELVETICA,  8, Font.NORMAL, grisClr);
		Font fAct     = new Font(Font.HELVETICA, 13, Font.BOLD,   azulTecnm);
		Font fLabel   = new Font(Font.HELVETICA,  8, Font.BOLD,   azulOscuro);
		Font fValue   = new Font(Font.HELVETICA,  9, Font.NORMAL, grisOsc);
		Font fPie     = new Font(Font.HELVETICA,  7, Font.ITALIC, grisClr);

		// ── 1. BANNER: logo TecNM | institución | logo ITCH + folio ─────────
		PdfPTable banner = new PdfPTable(3);
		banner.setWidthPercentage(100);
		banner.setWidths(new float[]{1.5f, 7f, 1.5f});
		banner.setSpacingAfter(0);

		banner.addCell(buildLogoConstancia("static/images/logoTecnm.png", 90, 62));

		PdfPCell celdaTit = new PdfPCell();
		celdaTit.setBackgroundColor(Color.WHITE);
		celdaTit.setBorder(0);
		celdaTit.setHorizontalAlignment(Element.ALIGN_CENTER);
		celdaTit.setVerticalAlignment(Element.ALIGN_MIDDLE);
		celdaTit.setPadding(8);
		Paragraph pNm = new Paragraph("TECNOLÓGICO NACIONAL DE MÉXICO", fInstNm);
		pNm.setAlignment(Element.ALIGN_CENTER);
		celdaTit.addElement(pNm);
		Paragraph pSb = new Paragraph("Instituto Tecnológico de Chilpancingo", fInstSub);
		pSb.setAlignment(Element.ALIGN_CENTER);
		celdaTit.addElement(pSb);
		banner.addCell(celdaTit);

		banner.addCell(buildLogoConstancia("static/images/itch.jpg", 58, 40));

		doc.add(banner);

		// ── 2. BARRAS SEPARADORAS AZULES ────────────────────────────────────
		PdfPTable sep1 = new PdfPTable(1);
		sep1.setWidthPercentage(100);
		sep1.setSpacingAfter(0);
		PdfPCell lc1 = new PdfPCell(new Phrase(" "));
		lc1.setBackgroundColor(azulTecnm);
		lc1.setFixedHeight(4);
		lc1.setBorder(0);
		sep1.addCell(lc1);
		doc.add(sep1);

		PdfPTable sep2 = new PdfPTable(1);
		sep2.setWidthPercentage(100);
		sep2.setSpacingAfter(20);
		PdfPCell lc2 = new PdfPCell(new Phrase(" "));
		lc2.setBackgroundColor(azulOscuro);
		lc2.setFixedHeight(2);
		lc2.setBorder(0);
		sep2.addCell(lc2);
		doc.add(sep2);

		// ── 3. TÍTULO ────────────────────────────────────────────────────────
		Paragraph pTitulo = new Paragraph("CONSTANCIA DE ACREDITACIÓN", fTitulo);
		pTitulo.setAlignment(Element.ALIGN_CENTER);
		pTitulo.setSpacingAfter(6);
		doc.add(pTitulo);

		Paragraph pSubTit = new Paragraph("Actividades Complementarias", fSubTit);
		pSubTit.setAlignment(Element.ALIGN_CENTER);
		pSubTit.setSpacingAfter(2);
		doc.add(pSubTit);

		String semNom = c.getSemestre() != null ? c.getSemestre().getNombre() : "";
		if (!semNom.isEmpty()) {
			Paragraph pSem = new Paragraph("Período Escolar: " + semNom, fSubTit);
			pSem.setAlignment(Element.ALIGN_CENTER);
			pSem.setSpacingAfter(6);
			doc.add(pSem);
		}

		Paragraph pFolio = new Paragraph("Folio: AC-" + String.format("%05d", c.getId()), fFolio);
		pFolio.setAlignment(Element.ALIGN_RIGHT);
		pFolio.setSpacingAfter(6);
		doc.add(pFolio);

		doc.add(new Chunk(new LineSeparator(1.5f, 50, azulTecnm, Element.ALIGN_CENTER, 0)));

		// ── 4. CUERPO ────────────────────────────────────────────────────────
		Paragraph pIntro = new Paragraph(
				"La Coordinación de Actividades Complementarias hace constar que el(la) alumno(a):",
				fNormal);
		pIntro.setAlignment(Element.ALIGN_CENTER);
		pIntro.setSpacingBefore(30);
		pIntro.setSpacingAfter(18);
		doc.add(pIntro);

		Paragraph pNombre = new Paragraph(alumnoNombre.toUpperCase(), fNombre);
		pNombre.setAlignment(Element.ALIGN_CENTER);
		pNombre.setSpacingAfter(8);
		doc.add(pNombre);

		String ctrlLine = "No. de Control: " + numControl
				+ (carrera.isEmpty() ? "" : "     |     " + carrera);
		Paragraph pCtrl = new Paragraph(ctrlLine, fSubNom);
		pCtrl.setAlignment(Element.ALIGN_CENTER);
		pCtrl.setSpacingAfter(30);
		doc.add(pCtrl);

		Paragraph pAcred = new Paragraph(
				"Acreditó satisfactoriamente la Actividad Complementaria:", fNormal);
		pAcred.setAlignment(Element.ALIGN_CENTER);
		pAcred.setSpacingAfter(8);
		doc.add(pAcred);

		Paragraph pAct = new Paragraph(actividadNombre.toUpperCase(), fAct);
		pAct.setAlignment(Element.ALIGN_CENTER);
		pAct.setSpacingAfter(4);
		doc.add(pAct);

		if (c.getInscripcion().getActividad().getDisciplina() != null) {
			Paragraph pDisc = new Paragraph(
					"Disciplina: " + c.getInscripcion().getActividad().getDisciplina().getNombre(),
					fSubNom);
			pDisc.setAlignment(Element.ALIGN_CENTER);
			pDisc.setSpacingAfter(24);
			doc.add(pDisc);
		}

		// ── 5. CUADRO DE DATOS ───────────────────────────────────────────────
		PdfPTable tDatos = new PdfPTable(4);
		tDatos.setWidthPercentage(76);
		tDatos.setSpacingBefore(6);
		tDatos.setSpacingAfter(60);
		tDatos.setWidths(new float[]{1.4f, 1.6f, 1.5f, 1.5f});

		String[][] datos = {
			{"EVALUACIÓN",       c.getValorNumerico() != null ? c.getValorNumerico() + " / 100" : "—"},
			{"NIVEL",            c.getNivel() != null ? c.getNivel().getNombre() : "—"},
			{"VAL. CURRICULAR",  c.getValorCurricular() != null ? c.getValorCurricular() + " crédito(s)" : "—"},
			{"FECHA DE EMISIÓN", c.getFechaGeneracion() != null ? c.getFechaGeneracion().toString() : "—"}
		};
		for (String[] par : datos) {
			PdfPCell cLbl = new PdfPCell(new Phrase(par[0], fLabel));
			cLbl.setBackgroundColor(azulClaro);
			cLbl.setBorderColor(azulBorde);
			cLbl.setBorderWidth(0.4f);
			cLbl.setPadding(8);
			cLbl.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cLbl.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell cVal = new PdfPCell(new Phrase(par[1], fValue));
			cVal.setBackgroundColor(Color.WHITE);
			cVal.setBorderColor(azulBorde);
			cVal.setBorderWidth(0.4f);
			cVal.setPadding(8);
			cVal.setHorizontalAlignment(Element.ALIGN_LEFT);
			cVal.setVerticalAlignment(Element.ALIGN_MIDDLE);

			tDatos.addCell(cLbl);
			tDatos.addCell(cVal);
		}
		doc.add(tDatos);

		// ── 6. FIRMAS ────────────────────────────────────────────────────────
		PdfPTable tFirma = new PdfPTable(2);
		tFirma.setWidthPercentage(80);
		tFirma.setSpacingAfter(22);

		String[] textosFirma = {
			"Encargado(a) de Actividades Complementarias",
			"Director(a) del Instituto"
		};
		for (String txt : textosFirma) {
			PdfPCell cel = new PdfPCell();
			cel.setBorder(PdfPCell.NO_BORDER);
			cel.setBorderWidthTop(0.8f);
			cel.setBorderColorTop(azulTecnm);
			cel.setPaddingTop(50);
			cel.setPaddingBottom(6);
			cel.setPaddingLeft(12);
			cel.setPaddingRight(12);
			cel.setHorizontalAlignment(Element.ALIGN_CENTER);
			Paragraph pFirmaTit = new Paragraph(txt, fLabel);
			pFirmaTit.setAlignment(Element.ALIGN_CENTER);
			cel.addElement(pFirmaTit);
			Paragraph pFirmaSub = new Paragraph("Firma y sello oficial", fPie);
			pFirmaSub.setAlignment(Element.ALIGN_CENTER);
			cel.addElement(pFirmaSub);
			tFirma.addCell(cel);
		}
		doc.add(tFirma);

		// ── 7. PIE DE PÁGINA ─────────────────────────────────────────────────
		doc.add(new Chunk(new LineSeparator(0.4f, 100, azulBorde, Element.ALIGN_CENTER, 0)));
		Paragraph pPie = new Paragraph(
				"Instituto Tecnológico de Chilpancingo — TecNM  ·  Chilpancingo, Guerrero  ·  "
				+ "Documento generado electrónicamente", fPie);
		pPie.setAlignment(Element.ALIGN_CENTER);
		pPie.setSpacingBefore(5);
		doc.add(pPie);

		doc.close();
	}

	private PdfPCell buildLogoConstancia(String classpathPath, float maxW, float maxH) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.WHITE);
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(6);
		cell.setMinimumHeight(maxH + 12);
		try {
			org.springframework.core.io.ClassPathResource res =
				new org.springframework.core.io.ClassPathResource(classpathPath);
			if (res.exists()) {
				try (java.io.InputStream is = res.getInputStream()) {
					Image img = Image.getInstance(is.readAllBytes());
					img.scaleAbsolute(maxW, maxH);
					img.setAlignment(Element.ALIGN_MIDDLE);
					cell.addElement(img);
				}
			}
		} catch (Exception ignored) {}
		return cell;
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
