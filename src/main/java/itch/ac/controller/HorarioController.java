package itch.ac.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import itch.ac.model.Actividad;
import itch.ac.model.Espacio;
import itch.ac.model.Horario;
import itch.ac.model.Instructor;
import itch.ac.model.Semestre;
import itch.ac.service.IActividadService;
import itch.ac.service.IEspacioService;
import itch.ac.service.IHorarioService;
import itch.ac.service.IInstructorService;
import itch.ac.service.ISemestreService;
import itch.ac.service.ISesionService;

@Controller
@RequestMapping("/horario")
public class HorarioController {

	@Autowired private IHorarioService horarioService;
	@Autowired private IActividadService actividadService;
	@Autowired private IEspacioService espacioService;
	@Autowired private IInstructorService instructorService;
	@Autowired private ISemestreService semestreService;
	@Autowired private ISesionService sesionService;

	@GetMapping("/horarios")
	public String listar(@RequestParam(required = false) Integer idInstructor,
	                     @RequestParam(required = false) Integer idSemestre,
	                     @RequestParam(required = false) String dia,
	                     Model model) {

		// Sin filtros activos → mostrar el semestre activo por defecto
		if (idInstructor == null && idSemestre == null && (dia == null || dia.isEmpty())) {
			Semestre activo = semestreService.buscarSemestreActivo();
			if (activo != null) idSemestre = activo.getId();
		}

		List<Horario> horarios = horarioService.buscarTodosHorarios().stream()
			.filter(h -> Integer.valueOf(1).equals(h.getActividad().getActivo()))
			.toList();

		if (idInstructor != null) {
			final Integer instrId = idInstructor;
			horarios = horarios.stream()
				.filter(h -> h.getActividad().getInstructor() != null
					&& h.getActividad().getInstructor().getId().equals(instrId))
				.toList();
		}
		if (idSemestre != null) {
			final Integer semId = idSemestre;
			horarios = horarios.stream()
				.filter(h -> h.getActividad().getSemestre() != null
					&& h.getActividad().getSemestre().getId().equals(semId))
				.toList();
		}

		// Capturar antes del filtro de día para el botón PDF
		boolean instructorTieneHorarios = idInstructor != null && !horarios.isEmpty();

		// Agrupar ANTES de filtrar por día, para conservar todos los días del grupo
		Map<String, HorarioGrupo> grupoMap = new LinkedHashMap<>();
		for (Horario h : horarios) {
			String key = h.getActividad().getId() + "|" + h.getEspacio().getId()
					+ "|" + h.getHoraInicio() + "|" + h.getHoraFin();
			if (!grupoMap.containsKey(key)) {
				grupoMap.put(key, new HorarioGrupo(
						h.getActividad().getNombre(),
						h.getEspacio().getNombre(),
						h.getHoraInicio().toString().substring(0, 5),
						h.getHoraFin().toString().substring(0, 5)));
			}
			grupoMap.get(key).addItem(h.getId(), h.getDiaSemana());
		}

		// Filtrar grupos por día: mostrar solo grupos que contengan ese día,
		// pero conservando todos sus días visibles
		List<HorarioGrupo> grupos = new ArrayList<>(grupoMap.values());
		if (dia != null && !dia.isEmpty()) {
			final String diaFinal = dia;
			grupos = grupos.stream()
				.filter(g -> g.getItems().stream().anyMatch(i -> i.getDia().equals(diaFinal)))
				.toList();
		}

		model.addAttribute("horarioGrupos", grupos);
		model.addAttribute("instructores", instructorService.buscarInstructoresActivos());
		model.addAttribute("semestres", semestreService.buscarTodosSemestres());
		model.addAttribute("semestreActivo", semestreService.buscarSemestreActivo());
		model.addAttribute("idInstructor", idInstructor);
		model.addAttribute("idSemestre", idSemestre);
		model.addAttribute("dia", dia);
		model.addAttribute("instructorTieneHorarios", instructorTieneHorarios);
		return "horario/listaHorarios";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		Semestre activo = semestreService.buscarSemestreActivo();
		model.addAttribute("horario", new Horario());
		model.addAttribute("actividades", actividadService.buscarActividadesActivas());
		model.addAttribute("espacios", espacioService.buscarTodosEspacios());
		model.addAttribute("todosHorarios", horariosPorSemestre(activo));
		model.addAttribute("modoGrupo", false);
		model.addAttribute("excludeIds", new ArrayList<Integer>());
		return "horario/formularioHorario";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Horario horario = horarioService.buscarPorId(id);
		Semestre semRef = horario.getActividad() != null ? horario.getActividad().getSemestre() : null;
		List<Integer> excl = new ArrayList<>();
		excl.add(horario.getId());
		model.addAttribute("horario", horario);
		model.addAttribute("actividades", actividadService.buscarActividadesActivas());
		model.addAttribute("espacios", espacioService.buscarTodosEspacios());
		model.addAttribute("todosHorarios", horariosPorSemestre(semRef));
		model.addAttribute("modoGrupo", false);
		model.addAttribute("excludeIds", excl);
		return "horario/formularioHorario";
	}

	@GetMapping("/editarGrupo")
	public String editarGrupo(@RequestParam String ids, Model model) {
		String[] partes = ids.split(",");
		Horario ref = horarioService.buscarPorId(Integer.parseInt(partes[0].trim()));
		Semestre semRef = ref.getActividad() != null ? ref.getActividad().getSemestre() : null;

		Map<String, Integer> diaIdMap = new LinkedHashMap<>();
		for (String idStr : partes) {
			Horario h = horarioService.buscarPorId(Integer.parseInt(idStr.trim()));
			diaIdMap.put(h.getDiaSemana(), h.getId());
		}

		model.addAttribute("horario", ref);
		model.addAttribute("diaIdMap", diaIdMap);
		model.addAttribute("modoGrupo", true);
		model.addAttribute("actividades", actividadService.buscarActividadesActivas());
		model.addAttribute("espacios", espacioService.buscarTodosEspacios());
		model.addAttribute("todosHorarios", horariosPorSemestre(semRef));
		model.addAttribute("excludeIds", new ArrayList<>(diaIdMap.values()));
		return "horario/formularioHorario";
	}

	private List<Horario> horariosPorSemestre(Semestre semestre) {
		final Integer semId = semestre != null ? semestre.getId() : null;
		return horarioService.buscarTodosHorarios().stream()
			.filter(h -> Integer.valueOf(1).equals(h.getActividad().getActivo()))
			.filter(h -> semId == null
				|| (h.getActividad().getSemestre() != null
					&& h.getActividad().getSemestre().getId().equals(semId)))
			.toList();
	}

	@PostMapping("/guardar")
	public String guardar(
	        @RequestParam(required = false) Integer id,
	        @RequestParam("idActividad") Integer idActividad,
	        @RequestParam("idEspacio") Integer idEspacio,
	        @RequestParam(value = "dias", required = false) List<String> dias,
	        @RequestParam(value = "dia", required = false) String dia,
	        @RequestParam(value = "modoGrupo", required = false, defaultValue = "false") boolean modoGrupo,
	        @RequestParam(value = "idLUNES",     required = false) Integer idLUNES,
	        @RequestParam(value = "idMARTES",    required = false) Integer idMARTES,
	        @RequestParam(value = "idMIERCOLES", required = false) Integer idMIERCOLES,
	        @RequestParam(value = "idJUEVES",    required = false) Integer idJUEVES,
	        @RequestParam(value = "idVIERNES",   required = false) Integer idVIERNES,
	        @RequestParam LocalTime horaInicio,
	        @RequestParam LocalTime horaFin,
	        RedirectAttributes attributes) {

		List<String> diasFinal;
		if (modoGrupo) {
			diasFinal = (dias != null) ? dias : new ArrayList<>();
		} else {
			diasFinal = (id != null) ? List.of(dia) : dias;
		}

		if (diasFinal == null || diasFinal.isEmpty()) {
			attributes.addFlashAttribute("msg", "⚠ Debe seleccionar al menos un día.");
			return "redirect:/horario/horarios";
		}
		if (!horaInicio.isBefore(horaFin)) {
			attributes.addFlashAttribute("msg", "⚠ La hora de inicio debe ser anterior a la hora de fin.");
			return "redirect:/horario/horarios";
		}
		if (Duration.between(horaInicio, horaFin).toMinutes() < 60) {
			attributes.addFlashAttribute("msg", "⚠ La duración mínima del horario es de 1 hora.");
			return "redirect:/horario/horarios";
		}
		if (horaInicio.isBefore(LocalTime.of(7, 0)) || horaFin.isAfter(LocalTime.of(18, 0))) {
			attributes.addFlashAttribute("msg", "⚠ El horario debe estar entre 07:00 y 18:00.");
			return "redirect:/horario/horarios";
		}

		Map<String, Integer> existingDayIds = new LinkedHashMap<>();
		if (modoGrupo) {
			if (idLUNES     != null) existingDayIds.put("LUNES",     idLUNES);
			if (idMARTES    != null) existingDayIds.put("MARTES",    idMARTES);
			if (idMIERCOLES != null) existingDayIds.put("MIERCOLES", idMIERCOLES);
			if (idJUEVES    != null) existingDayIds.put("JUEVES",    idJUEVES);
			if (idVIERNES   != null) existingDayIds.put("VIERNES",   idVIERNES);
		}

		// Cargar actividad antes del loop para obtener el semestre de conflictos
		Actividad actividad = actividadService.buscarPorId(idActividad);
		Integer idSemestreConflicto = (actividad != null && actividad.getSemestre() != null)
			? actividad.getSemestre().getId() : null;

		List<String> conflictos = new ArrayList<>();
		for (String d : diasFinal) {
			Integer excludeId = modoGrupo ? existingDayIds.get(d) : id;
			List<Horario> conf = horarioService.buscarConflictos(idEspacio, d, horaInicio, horaFin, excludeId, idSemestreConflicto);
			if (!conf.isEmpty()) {
				conflictos.add(d + " (" + conf.get(0).getActividad().getNombre() + ")");
			}
		}
		if (!conflictos.isEmpty()) {
			attributes.addFlashAttribute("msg",
				"⚠ Conflicto de horario en: " + String.join(", ", conflictos));
			return "redirect:/horario/horarios";
		}

		Espacio espacio = espacioService.buscarPorId(idEspacio);

		if (modoGrupo) {
			Set<String> diasSet = new HashSet<>(diasFinal);

			for (String d : diasFinal) {
				Integer existId = existingDayIds.get(d);
				if (existId != null) {
					Horario h = horarioService.buscarPorId(existId);
					h.setActividad(actividad);
					h.setEspacio(espacio);
					h.setHoraInicio(horaInicio);
					h.setHoraFin(horaFin);
					horarioService.guardarHorario(h);
				} else {
					Horario h = new Horario();
					h.setActividad(actividad);
					h.setEspacio(espacio);
					h.setDiaSemana(d);
					h.setHoraInicio(horaInicio);
					h.setHoraFin(horaFin);
					horarioService.guardarHorario(h);
				}
			}

			for (Map.Entry<String, Integer> entry : existingDayIds.entrySet()) {
				if (!diasSet.contains(entry.getKey())) {
					horarioService.eliminarPorId(entry.getValue());
				}
			}

			attributes.addFlashAttribute("msg", "Horario actualizado correctamente.");
		} else if (id != null) {
			Horario horario = horarioService.buscarPorId(id);
			horario.setActividad(actividad);
			horario.setEspacio(espacio);
			horario.setDiaSemana(diasFinal.get(0));
			horario.setHoraInicio(horaInicio);
			horario.setHoraFin(horaFin);
			horarioService.guardarHorario(horario);
			attributes.addFlashAttribute("msg", "Horario guardado correctamente.");
		} else {
			List<Horario> nuevosHorarios = new ArrayList<>();
			for (String d : diasFinal) {
				Horario h = new Horario();
				h.setActividad(actividad);
				h.setEspacio(espacio);
				h.setDiaSemana(d);
				h.setHoraInicio(horaInicio);
				h.setHoraFin(horaFin);
				horarioService.guardarHorario(h);
				nuevosHorarios.add(h);
			}
			sesionService.generarSesiones(actividad, nuevosHorarios);
			attributes.addFlashAttribute("msg", "Horario guardado correctamente.");
		}

		return "redirect:/horario/horarios";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		model.addAttribute("horario", horarioService.buscarPorId(id));
		return "horario/detalleHorario";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Horario horario = horarioService.eliminarPorId(id);
		if (horario == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar el horario.");
		} else {
			attributes.addFlashAttribute("msg", "Horario eliminado correctamente.");
		}
		return "redirect:/horario/horarios";
	}

	@GetMapping("/eliminarGrupo")
	public String eliminarGrupo(@RequestParam String ids, RedirectAttributes attributes) {
		String[] partes = ids.split(",");
		int eliminados = 0;
		for (String idStr : partes) {
			Horario h = horarioService.eliminarPorId(Integer.parseInt(idStr.trim()));
			if (h != null) eliminados++;
		}
		attributes.addFlashAttribute("msg",
			eliminados > 0 ? "Horario eliminado correctamente." : "No se pudo eliminar el horario.");
		return "redirect:/horario/horarios";
	}

	// ── PDF horario del instructor ────────────────────────────────────────────

	@GetMapping("/instructor/{idInstructor}/pdf")
	public ResponseEntity<byte[]> pdfInstructor(@PathVariable Integer idInstructor) throws Exception {
		Instructor instructor = instructorService.buscarPorId(idInstructor);
		Semestre semestreActivo = semestreService.buscarSemestreActivo();

		List<Horario> horarios = horarioService.buscarTodosHorarios().stream()
			.filter(h -> h.getActividad().getInstructor() != null
				&& h.getActividad().getInstructor().getId().equals(idInstructor))
			.toList();

		if (semestreActivo != null) {
			final Integer semId = semestreActivo.getId();
			horarios = horarios.stream()
				.filter(h -> h.getActividad().getSemestre() != null
					&& h.getActividad().getSemestre().getId().equals(semId))
				.toList();
		}

		String nombre = instructor.getPersona().getNombre() + " " + instructor.getPersona().getApellido();
		byte[] pdf = buildPdfHorario(nombre, semestreActivo, horarios);

		String fileName = "horario_" + instructor.getPersona().getApellido()
			.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
		return ResponseEntity.ok().headers(headers).body(pdf);
	}

	private byte[] buildPdfHorario(String nombreInstructor, Semestre semestre,
	                               List<Horario> horarios) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document doc = new Document(PageSize.A4, 36, 36, 30, 25);
		PdfWriter.getInstance(doc, baos);
		doc.open();

		Color azulTecnm = new Color(13, 41, 116);
		Color azulClaro = new Color(207, 226, 255);
		Color grisClaro = new Color(235, 235, 235);

		com.lowagie.text.Font fTitulo  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  11, azulTecnm);
		com.lowagie.text.Font fSubtit  = FontFactory.getFont(FontFactory.HELVETICA,          8, azulTecnm);
		com.lowagie.text.Font fColHead = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    8, Color.WHITE);
		com.lowagie.text.Font fInfo    = FontFactory.getFont(FontFactory.HELVETICA,          9);
		com.lowagie.text.Font fInfoB   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    9);
		com.lowagie.text.Font fHora    = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    7);
		com.lowagie.text.Font fAct     = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    7);
		com.lowagie.text.Font fSmall   = FontFactory.getFont(FontFactory.HELVETICA,          6);
		com.lowagie.text.Font fFooter  = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE,  7);

		// ── Banner: logo TecNM | título | logo ITCH ──────────────────────────
		PdfPTable banner = new PdfPTable(3);
		banner.setWidthPercentage(100);
		banner.setWidths(new float[]{1.5f, 7f, 1.5f});
		banner.setSpacingAfter(2);

		banner.addCell(buildLogoCelda("static/images/logoTecnm.png", 90, 62));

		PdfPCell celdaTitulo = new PdfPCell();
		celdaTitulo.setBackgroundColor(Color.WHITE);
		celdaTitulo.setBorder(0);
		celdaTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		celdaTitulo.setVerticalAlignment(Element.ALIGN_MIDDLE);
		celdaTitulo.setPadding(8);
		Paragraph pTit = new Paragraph("Instituto Tecnológico de Chilpancingo — TecNM", fTitulo);
		pTit.setAlignment(Element.ALIGN_CENTER);
		celdaTitulo.addElement(pTit);
		Paragraph pSub = new Paragraph("Horario de Actividades Complementarias", fSubtit);
		pSub.setAlignment(Element.ALIGN_CENTER);
		celdaTitulo.addElement(pSub);
		banner.addCell(celdaTitulo);

		banner.addCell(buildLogoCelda("static/images/itch.jpg", 60, 42));

		doc.add(banner);

		// Línea separadora azul
		PdfPTable separador = new PdfPTable(1);
		separador.setWidthPercentage(100);
		separador.setSpacingAfter(8);
		PdfPCell lineaCell = new PdfPCell(new Phrase(" "));
		lineaCell.setBackgroundColor(azulTecnm);
		lineaCell.setFixedHeight(3);
		lineaCell.setBorder(0);
		separador.addCell(lineaCell);
		doc.add(separador);

		// Datos del instructor
		Paragraph infoLine = new Paragraph();
		infoLine.add(new Phrase("Instructor: ", fInfoB));
		infoLine.add(new Phrase(nombreInstructor + "     ", fInfo));
		infoLine.add(new Phrase("Semestre: ", fInfoB));
		infoLine.add(new Phrase(semestre != null ? semestre.getNombre() : "—", fInfo));
		infoLine.setAlignment(Element.ALIGN_CENTER);
		infoLine.setSpacingAfter(8);
		doc.add(infoLine);

		// ── Tabla semanal ─────────────────────────────────────────────────────
		String[] diasLabel = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
		String[] diasKey   = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};
		String[] horas     = {"07:00","08:00","09:00","10:00","11:00",
		                      "12:00","13:00","14:00","15:00","16:00","17:00"};

		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.setWidths(new float[]{0.8f, 1.6f, 1.6f, 1.6f, 1.6f, 1.6f});

		addHeaderCell(table, "Hora", fColHead, azulTecnm);
		for (String d : diasLabel) addHeaderCell(table, d, fColHead, azulTecnm);

		for (String hora : horas) {
			int horaMin = timeToMin(hora);

			PdfPCell horaCell = new PdfPCell(new Phrase(hora, fHora));
			horaCell.setBackgroundColor(grisClaro);
			horaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			horaCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			horaCell.setPadding(4);
			horaCell.setMinimumHeight(24);
			table.addCell(horaCell);

			for (String dKey : diasKey) {
				Optional<Horario> match = horarios.stream()
					.filter(h -> h.getDiaSemana().equals(dKey)
						&& horaMin >= timeToMin(h.getHoraInicio().toString().substring(0, 5))
						&& horaMin <  timeToMin(h.getHoraFin().toString().substring(0, 5)))
					.findFirst();

				if (match.isPresent()) {
					Horario h = match.get();
					String rango = h.getHoraInicio().toString().substring(0, 5)
						+ "–" + h.getHoraFin().toString().substring(0, 5);

					PdfPCell dataCell = new PdfPCell();
					dataCell.setBackgroundColor(azulClaro);
					dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					dataCell.setPadding(3);

					Paragraph pAct = new Paragraph(h.getActividad().getNombre(), fAct);
					pAct.setAlignment(Element.ALIGN_CENTER);
					pAct.setSpacingAfter(1);
					dataCell.addElement(pAct);

					Paragraph pEsp = new Paragraph(h.getEspacio().getNombre(), fSmall);
					pEsp.setAlignment(Element.ALIGN_CENTER);
					pEsp.setSpacingAfter(1);
					dataCell.addElement(pEsp);

					Paragraph pRango = new Paragraph(rango, fSmall);
					pRango.setAlignment(Element.ALIGN_CENTER);
					dataCell.addElement(pRango);

					table.addCell(dataCell);
				} else {
					PdfPCell emptyCell = new PdfPCell();
					emptyCell.setPadding(3);
					emptyCell.setMinimumHeight(24);
					table.addCell(emptyCell);
				}
			}
		}

		doc.add(table);

		Paragraph footer = new Paragraph("Generado el " + LocalDate.now(), fFooter);
		footer.setAlignment(Element.ALIGN_RIGHT);
		footer.setSpacingBefore(6);
		doc.add(footer);

		doc.close();
		return baos.toByteArray();
	}

	private PdfPCell buildLogoCelda(String classpathPath, float maxW, float maxH) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.WHITE);
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(6);
		cell.setMinimumHeight(maxH + 12);
		try {
			ClassPathResource res = new ClassPathResource(classpathPath);
			if (res.exists()) {
				try (InputStream is = res.getInputStream()) {
					com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(is.readAllBytes());
					img.scaleAbsolute(maxW, maxH);
					img.setAlignment(Element.ALIGN_MIDDLE);
					cell.addElement(img);
				}
			}
		} catch (Exception ignored) {}
		return cell;
	}

	private void addHeaderCell(PdfPTable table, String text,
	                           com.lowagie.text.Font font, Color bg) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBackgroundColor(bg);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(5);
		table.addCell(cell);
	}

	private int timeToMin(String time) {
		String[] p = time.split(":");
		return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
	}
}
