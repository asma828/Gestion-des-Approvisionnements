package com.example.demo.mapper;

import com.example.demo.dto.FournisseurDTO;
import com.example.demo.entity.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FournisseurMapper {

    FournisseurDTO toDTO(Fournisseur fournisseur);

    Fournisseur toEntity(FournisseurDTO dto);

    List<FournisseurDTO> toDTOList(List<Fournisseur> fournisseurs);

    void updateEntityFromDTO(FournisseurDTO dto, @MappingTarget Fournisseur fournisseur);
}
