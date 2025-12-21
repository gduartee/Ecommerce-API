package com.ecommerce.joias.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class CloudinaryConfig {
    @Value("${cloudinary.url}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(cloudinaryUrl);
    }
}
