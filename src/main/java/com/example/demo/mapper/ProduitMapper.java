package com.example.demo.mapper;

import com.example.demo.dto.ProduitDTO;
import com.example.demo.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    ProduitDTO toDTO(Produit produit);

    Produit toEntity(ProduitDTO dto);

    List<ProduitDTO> toDTOList(List<Produit> produits);

    void updateEntityFromDTO(ProduitDTO dto, @MappingTarget Produit produit);
}
