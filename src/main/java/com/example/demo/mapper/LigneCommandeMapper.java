package com.example.demo.mapper;

import com.example.demo.dto.LigneCommandeDTO;
import com.example.demo.entity.LigneCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LigneCommandeMapper {

    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.nom", target = "produitNom")
    LigneCommandeDTO toDTO(LigneCommande ligne);

    @Mapping(source = "produitId", target = "produit.id")
    @Mapping(target = "produit.nom", ignore = true)
    @Mapping(target = "commande", ignore = true)
    LigneCommande toEntity(LigneCommandeDTO dto);

    List<LigneCommandeDTO> toDTOList(List<LigneCommande> lignes);

    @Mapping(target = "produit", ignore = true)
    @Mapping(target = "commande", ignore = true)
    void updateEntityFromDTO(LigneCommandeDTO dto, @MappingTarget LigneCommande ligne);
}
