package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.CriterioBase;
import itch.ac.repository.CriterioBaseRepository;
import itch.ac.service.ICriterioBaseService;

@Primary
@Service
public class CriterioBaseServiceJPA implements ICriterioBaseService {

	@Autowired
	private CriterioBaseRepository criterioBaseRepository;

	@Override
	public List<CriterioBase> buscarTodosCriterios() {
		return criterioBaseRepository.findAll();
	}

	@Override
	public void guardarCriterio(CriterioBase criterio) {
		criterioBaseRepository.save(criterio);
	}

	@Override
	public CriterioBase buscarPorId(Integer id) {
		Optional<CriterioBase> optional = criterioBaseRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public CriterioBase eliminarPorId(Integer id) {
		Optional<CriterioBase> optional = criterioBaseRepository.findById(id);
		if (optional.isPresent()) {
			CriterioBase criterio = optional.get();
			criterioBaseRepository.delete(criterio);
			return criterio;
		}
		return null;
	}

	@Override
	public boolean existePorNombre(String nombre, Integer idExcluir) {
		return criterioBaseRepository.findAll().stream().filter(c -> idExcluir == null || !c.getId().equals(idExcluir))
				.anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre.trim()));
	}

	@Override
	public boolean existePorNumero(Integer numero, Integer idExcluir) {
		return criterioBaseRepository.findAll().stream().filter(c -> idExcluir == null || !c.getId().equals(idExcluir))
				.anyMatch(c -> c.getNumeroCriterio().equals(numero));
	}
}