package com.ecommerce.joias.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.joias.dto.response.ImageResponseDto;
import com.ecommerce.joias.entity.Image;
import com.ecommerce.joias.repository.ImageRepository;
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

    // Método auxiliar para gerar nome único se precisar
    private String generateFileName() {
        return UUID.randomUUID().toString();
    }
}