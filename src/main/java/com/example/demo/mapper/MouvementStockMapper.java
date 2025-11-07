package com.example.demo.mapper;

import com.example.demo.dto.MouvementStockDTO;
import com.example.demo.entity.MouvementStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MouvementStockMapper {

    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.nom", target = "produitNom")
    @Mapping(source = "commandeFournisseur.id", target = "commandeFournisseurId")
    MouvementStockDTO toDTO(MouvementStock mouvement);

    @Mapping(source = "produitId", target = "produit.id")
    @Mapping(target = "produit.nom", ignore = true)
    @Mapping(source = "commandeFournisseurId", target = "commandeFournisseur.id")
    @Mapping(target = "commandeFournisseur.fournisseur", ignore = true)
    MouvementStock toEntity(MouvementStockDTO dto);

    List<MouvementStockDTO> toDTOList(List<MouvementStock> mouvements);

    @Mapping(target = "produit", ignore = true)
    @Mapping(target = "commandeFournisseur", ignore = true)
    void updateEntityFromDTO(MouvementStockDTO dto, @MappingTarget MouvementStock mouvement);
}
