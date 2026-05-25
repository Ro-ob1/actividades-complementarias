package itch.ac.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorarioGrupo {

	private static final Map<String, Integer> ORDEN = new HashMap<>();
	static {
		ORDEN.put("LUNES",    1);
		ORDEN.put("MARTES",   2);
		ORDEN.put("MIERCOLES",3);
		ORDEN.put("JUEVES",   4);
		ORDEN.put("VIERNES",  5);
	}

	private String actividad;
	private String espacio;
	private String horaInicio;
	private String horaFin;
	private List<HorarioItem> items = new ArrayList<>();

	public HorarioGrupo(String actividad, String espacio, String horaInicio, String horaFin) {
		this.actividad  = actividad;
		this.espacio    = espacio;
		this.horaInicio = horaInicio;
		this.horaFin    = horaFin;
	}

	public void addItem(Integer id, String dia) {
		items.add(new HorarioItem(id, dia));
		items.sort((a, b) -> ORDEN.getOrDefault(a.getDia(), 9) - ORDEN.getOrDefault(b.getDia(), 9));
	}

	public String getActividad()  { return actividad; }
	public String getEspacio()    { return espacio; }
	public String getHoraInicio() { return horaInicio; }
	public String getHoraFin()    { return horaFin; }
	public List<HorarioItem> getItems() { return items; }

	public String getIdsString() {
		return items.stream()
				.map(i -> i.getId().toString())
				.collect(java.util.stream.Collectors.joining(","));
	}

	public static class HorarioItem {
		private Integer id;
		private String  dia;

		public HorarioItem(Integer id, String dia) {
			this.id  = id;
			this.dia = dia;
		}

		public Integer getId()  { return id; }
		public String  getDia() { return dia; }

		public String getDiaCorto() {
			switch (dia) {
				case "LUNES":     return "Lun";
				case "MARTES":    return "Mar";
				case "MIERCOLES": return "Mié";
				case "JUEVES":    return "Jue";
				case "VIERNES":   return "Vie";
				default:          return dia;
			}
		}
	}
}
