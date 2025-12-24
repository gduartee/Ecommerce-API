package com.ecommerce.joias.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.response.ImageResponseDto;
import com.ecommerce.joias.entity.Image;
import com.ecommerce.joias.repository.ImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;

    public ImageService(Cloudinary cloudinary, ImageRepository imageRepository) {
        this.cloudinary = cloudinary;
        this.imageRepository = imageRepository;
    }

    @Transactional
    public ImageResponseDto uploadImage(MultipartFile file, Integer parentId, String parentType, Boolean isMain) {
        try {
            String fileName = parentType + "_" + parentId + "_" + UUID.randomUUID();

            // Configurações do Upload
            Map uploadParams = ObjectUtils.asMap(
                    "public_id", fileName, // Nome do arquivo no Cloudinary
                    "folder", "ecommerce_joias" // Pasta para organizar
            );

            // Envia e pega o resultado
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            // Retorna a URL segura (https)
            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            // DTO -> ENTITY
            Image imageEntity = new Image();
            imageEntity.setParentId(parentId);
            imageEntity.setParentType(parentType);
            imageEntity.setUrl(url);
            imageEntity.setPublicId(publicId);
            imageEntity.setMain(isMain);

            var imageSaved = imageRepository.save(imageEntity);

            return new ImageResponseDto(
                    imageSaved.getImageId(),
                    imageSaved.getUrl(),
                    imageSaved.getPublicId(),
                    imageSaved.getParentId(),
                    imageSaved.getParentType(),
                    imageSaved.getMain()
            );

        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload da imagem", e);
        }
    }

    public ImageResponseDto getImageById(Integer imageId) {
        var imageEntity = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

        return new ImageResponseDto(
                imageEntity.getImageId(),
                imageEntity.getUrl(),
                imageEntity.getPublicId(),
                imageEntity.getParentId(),
                imageEntity.getParentType(),
                imageEntity.getMain()
        );
    }

    public ApiResponse<ImageResponseDto> listImages(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);

        var pageData = imageRepository.findAll(pageable);

        // Converte as entidades para dto
        var imagesDto = pageData.getContent().stream().map(image -> new ImageResponseDto(
                image.getImageId(),
                image.getUrl(),
                image.getPublicId(),
                image.getParentId(),
                image.getParentType(),
                image.getMain()
        )).toList();

        return new ApiResponse<>(
                imagesDto,
                pageData.getTotalElements(), // Total real no banco
                pageData.getTotalPages(),    // Total de páginas
                pageData.getNumber(),        // Página atual
                pageData.getSize()           // Limite (limit)
        );
    }

    public ApiResponse<ImageResponseDto> findByParentIdAndParentType(Integer parentId, String parentType, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);

        var pageData = imageRepository.findAllByParentIdAndParentType(parentId, parentType, pageable);

        var imagesDto = pageData.getContent().stream().map(image -> new ImageResponseDto(
                image.getImageId(),
                image.getUrl(),
                image.getPublicId(),
                image.getParentId(),
                image.getParentType(),
                image.getMain()
        )).toList();

        return new ApiResponse<>(
                imagesDto,
                pageData.getTotalElements(), // Total real no banco
                pageData.getTotalPages(),    // Total de páginas
                pageData.getNumber(),        // Página atual
                pageData.getSize()           // Limite (limit)
        );
    }

    @Transactional
    public void deleteImageById(Integer imageId) {
        var image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

        try {
            cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao deletar imagem do Cloudinary", ex);
        }

        imageRepository.deleteById(imageId);
    }

}