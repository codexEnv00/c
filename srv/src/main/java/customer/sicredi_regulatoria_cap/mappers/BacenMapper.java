package customer.sicredi_regulatoria_cap.mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import cds.gen.balanceservice.Balance;
import cds.gen.sicredi.db.entities.Balances;
import cds.gen.sicredi.db.entities.Cadoc;
import cds.gen.sicredi.db.entities.Companies;
import cds.gen.sicredi.db.entities.OutsideBalance;
import cds.gen.sicredi.db.entities.TerminationDocument;
import customer.sicredi_regulatoria_cap.dtos.bacen.BacenDTO;
import customer.sicredi_regulatoria_cap.dtos.bacen.SendBacenDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceExtractionDTO.BalanceExtractionFieldsDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.OutsideBalancesDTO.OutsideBalancesFieldsDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.TerminationDocumentsDTO.TerminationDocumentsFieldsDTO;
import customer.sicredi_regulatoria_cap.utils.CompaniesWrapper;

@Mapper
public interface BacenMapper {

	BacenMapper INSTANCE = Mappers.getMapper(BacenMapper.class);

	@Mapping(target = "accountCode", source = "cosif")
	@Mapping(target = "balance", source = "cadocBalance")
	SendBacenDTO.AccountDTO mapSendBacen(Balance source);


	@Named("getCompanyOutside")
	public static Companies getCompanyOutside(OutsideBalancesFieldsDTO source, @Context Set<String> companies) {
		return getCompany(source.getEmpresa(), source.getCnpj(), companies);
	}

	@Named("getCompanyTermination")
	public static Companies getCompanyTermination(TerminationDocumentsFieldsDTO source, @Context Set<String> companies) {
		return getCompany(source.getEmpresa(), null, companies);
	}

	@Named("getCompany")
	public static Companies getCompany(String empresa, String cnpj, @Context Set<String> companies) {
		return CompaniesWrapper.createCompany(null, null, cnpj, empresa).getCloneCompany();
	}

	
	default List<TerminationDocument> mapAllTerminationDocuments(BacenDTO bacenDTO, String tpDoc, @Context Set<String> companies) {
		
		
		return bacenDTO.getTab2().getCampos().parallelStream()
				.map(e -> {
					TerminationDocument doc = INSTANCE.toTerminationDocumentsEntity(e, companies);
					doc.setCompanyExternalCode(doc.getCompany().getExternalCode());
					doc.setCadoc(bacenDTO.getCadoc());
					doc.setDtLanc(bacenDTO.getDtLanc());
					doc.setTpDoc(tpDoc);

					return doc;
				}).toList();
	}

	default List<OutsideBalance> mapAllOusideBalances(BacenDTO bacenDTO, @Context Set<String> companies) {
		return bacenDTO.getTab3().getCampos().parallelStream()
			.map(e -> {
				OutsideBalance outside = INSTANCE.toOutsideBalanceEntity(e, bacenDTO, companies);
				outside.setCompanyExternalCode(outside.getCompany().getExternalCode());
				
				return outside;
			}).toList();
	}
	
	default List<Balances> mapAllBalances(BacenDTO bacenDTO, @Context Set<String> companies) {
		Stack<BalanceExtractionFieldsDTO> stack = new Stack<>();
		List<Balances> balances = new ArrayList<>();

		Set<String> childFields = new HashSet<>();
		AtomicInteger order = new AtomicInteger(0);

		for (BalanceExtractionFieldsDTO field : bacenDTO.getTab1().getCampos()) {
			if (field.getEmpresa().isBlank()) {
				continue;
			}
			Balances balance = INSTANCE.toBalancesEntity(field, bacenDTO, order.getAndAdd(1));
			
			CompaniesWrapper wrapper = CompaniesWrapper.createCompany(null, null, field.getCnpj(), field.getEmpresa());
			if (!companies.contains(wrapper.getExternalCode())) {
				System.out.println(wrapper);
			}

			Companies company = Companies.create();
			company.setExternalCode(wrapper.getExternalCode());
				
			balance.setCompanyExternalCode(wrapper.getExternalCode());
			balance.setCompany(company);

			int currentLevel = balance.getLevel();

			if (stack.isEmpty() || currentLevel > stack.peek().getNivel()) {
				if (!stack.isEmpty()) {
					balance.setParentKey(stack.peek().getKey());
				}
				stack.push(field);
			} else {
				while (!stack.isEmpty() && stack.peek().getNivel() >= currentLevel) {
					stack.pop();
				}
				
				if (!stack.isEmpty()) {
					balance.setParentKey(stack.peek().getKey());
				}
				
				stack.push(field);
			}

			childFields.add(balance.getParentKey());
			balances.add(balance);
		}

		for (Balances balance : balances) {
			if (childFields.contains(balance.getNodeID())) {
                balance.setDrillState("expanded");
            } else {
                balance.setDrillState("leaf");
            }
		}

		return balances;
	}

	@Mapping(target = "cosifAccount", source = "fields.contaCosif")
	@Mapping(target = "razaoAccount", source = "fields.contaRazao")
	@Mapping(target = "description", source = "fields.descricao", qualifiedByName = "trim")
	@Mapping(target = "cadocBalance", source = "fields.saldoCadoc")
	@Mapping(target = "level", source = "fields.nivel")
	@Mapping(target = "nodeID", expression = "java(fields.getKey())")
	@Mapping(target = "version", source = "bacenDTO.versao")
	@Mapping(target = "interval", source = "bacenDTO.periodo")
	@Mapping(target = "exercise", source = "bacenDTO.exercicio")
	@Mapping(target = "cadoc", source = "bacenDTO.cadoc")
	@Mapping(target = "order", source = "order")
	Balances toBalancesEntity(BalanceExtractionFieldsDTO fields, BacenDTO bacenDTO, int order);

	@Mapping(target = "document", source = "documento")
	@Mapping(target = "exercise", source = "exercicio")
	@Mapping(target = "interval", source = "periodo")
	@Mapping(target = "item", source = "item")
	@Mapping(target = "amount", source = "montante")
	@Mapping(target = "razao", source = "razao")
	@Mapping(target = "tpDoc", source = "tpDoc")
	@Mapping(target = "company", source = ".", qualifiedByName = "getCompanyTermination")
	TerminationDocument toTerminationDocumentsEntity(TerminationDocumentsFieldsDTO e, @Context Set<String> companies);

	@Mapping(target = "razaoAccount", source = "source.contaRazao")
	@Mapping(target = "description", source = "source.descricao")
	@Mapping(target = "cadocBalance", source = "source.saldoCadoc")
	@Mapping(target = "version", source = "bacenDTO.versao")
	@Mapping(target = "interval", source = "bacenDTO.periodo")
	@Mapping(target = "exercise", source = "bacenDTO.exercicio")
	@Mapping(target = "cadoc", source = "bacenDTO.cadoc")
	@Mapping(target = "company", source = "source", qualifiedByName = "getCompanyOutside")
	OutsideBalance toOutsideBalanceEntity(OutsideBalancesFieldsDTO source, BacenDTO bacenDTO, @Context Set<String> companies);

	default Cadoc createCadoc() {
		return Cadoc.create();
	}

	default Balances createBalances() {
		return Balances.create();
	}

	default TerminationDocument createTerminationDocument() {
		return TerminationDocument.create();
	}

	default OutsideBalance createOutsideBalance() {
		return OutsideBalance.create();
	}

	default Companies createCompanies() {
		return Companies.create();
	}

	@Named("trim")
	public static String trim(final String value) {
		return Optional.ofNullable(value).orElse("").trim();
	}
}
