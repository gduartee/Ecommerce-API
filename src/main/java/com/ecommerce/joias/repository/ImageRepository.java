package com.ecommerce.joias.repository;

import com.ecommerce.joias.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    void deleteByParentIdAndParenType(Integer parentId, String parentType);

    List<Image> findAllByParentIdAndParentType(Integer parentId, String parentType);
}
