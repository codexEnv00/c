package customer.sicredi_regulatoria_cap.mappers.cadoc4500;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cds.gen.sicredi.db.entities.cadoc4500.Estban;
import customer.sicredi_regulatoria_cap.dtos.bacen.Estban4500DTO;

@Mapper
public interface Estban4500Mapper {

	Estban4500Mapper INSTANCE = Mappers.getMapper(Estban4500Mapper.class);
	
	@Mapping(target = "companyExternalCode", source = "company")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "interval", source = "interval")
	@Mapping(target = "exercise", source = "exercise")
	@Mapping(target = "agency", source = "agency")
	@Mapping(target = "account", source = "account")
	@Mapping(target = "balance", source = "balance")
	@Mapping(target = "description", source = "description")
	@Mapping(target = "level", source = "level")
	@Mapping(target = "nodeID", expression = "java(source.getKey())")
	Estban map(Estban4500DTO source);

	default List<Estban> mapAll(List<Estban4500DTO> sources) {
		List<Estban> result = new ArrayList<>();
		Stack<Estban4500DTO> stack = new Stack<>();

		Set<String> childFields = new HashSet<>();

		for (Estban4500DTO estbanDTO : sources) {
			estbanDTO.setLevel(Optional.ofNullable(estbanDTO.getLevel()).orElse(1));
			
			Estban estban = map(estbanDTO);

			int currentLevel = estban.getLevel();

			if (stack.isEmpty() || currentLevel > stack.peek().getLevel()) {
				if (!stack.isEmpty()) {
					estban.setParentKey(stack.peek().getKey());
				}
				stack.push(estbanDTO);
			} else {
				while (!stack.isEmpty() && stack.peek().getLevel() >= currentLevel) {
					stack.pop();
				}
				
				if (!stack.isEmpty()) {
					estban.setParentKey(stack.peek().getKey());
				}
				
				stack.push(estbanDTO);
			}

			childFields.add(estban.getParentKey());
			result.add(estban);

		}

		for (Estban estban : result) {
			if (childFields.contains(estban.getNodeID())) {
				estban.setDrillState("expanded");
			} else {
				estban.setDrillState("leaf");
			}
		}
		return result;
	}

	default Estban create() {
		return Estban.create();
	}
}
