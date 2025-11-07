package com.example.demo.mapper;

import com.example.demo.dto.CommandeFournisseurDTO;
import com.example.demo.entity.CommandeFournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LigneCommandeMapper.class})
public interface CommandeFournisseurMapper {

    @Mapping(source = "fournisseur.id", target = "fournisseurId")
    @Mapping(source = "fournisseur.societe", target = "fournisseurSociete")
    CommandeFournisseurDTO toDTO(CommandeFournisseur commande);

    @Mapping(source = "fournisseurId", target = "fournisseur.id")
    @Mapping(target = "fournisseur.societe", ignore = true)
    @Mapping(target = "produits", ignore = true)
    @Mapping(target = "mouvements", ignore = true)
    CommandeFournisseur toEntity(CommandeFournisseurDTO dto);

    List<CommandeFournisseurDTO> toDTOList(List<CommandeFournisseur> commandes);

    @Mapping(target = "fournisseur", ignore = true)
    @Mapping(target = "produits", ignore = true)
    @Mapping(target = "mouvements", ignore = true)
    @Mapping(target = "lignesCommande", ignore = true)
    void updateEntityFromDTO(CommandeFournisseurDTO dto, @MappingTarget CommandeFournisseur commande);
}

