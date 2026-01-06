package com.ecommerce.joias.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // --------------------------------------------------------
                        // 1. ROTAS TOTALMENTE PÚBLICAS (Acesso Livre)
                        // --------------------------------------------------------
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll() // Login e Register
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll() // Carregar fotos
                        .requestMatchers("/error").permitAll()

                        // Vitrine da Loja (Qualquer um pode VER produtos e categorias)
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/subcategories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/variants/**").permitAll() // ProductVariantController

                        // --------------------------------------------------------
                        // 2. ROTAS ADMINISTRATIVAS (Gestão da Loja)
                        // --------------------------------------------------------
                        // Mexer no Catálogo (Criar/Editar/Deletar) -> Só ADMIN ou MANAGER
                        .requestMatchers(HttpMethod.POST, "/products/**", "/categories/**", "/subcategories/**", "/variants/**", "/images/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/products/**", "/categories/**", "/subcategories/**", "/variants/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/products/**", "/categories/**", "/subcategories/**", "/variants/**", "/images/**").hasRole("MANAGER")

                        // Gestão de Funcionários -> Só ADMIN manda
                        .requestMatchers("/employees/**").hasRole("MANAGER")

                        // --------------------------------------------------------
                        // 3. ROTAS DE CLIENTE LOGADO (Checkout e Perfil)
                        // --------------------------------------------------------
                        // Para comprar, ver pedidos ou editar endereço, tem que estar logado (qualquer role)
                        .requestMatchers("/orders/**").authenticated()
                        .requestMatchers("/addresses/**").authenticated()
                        .requestMatchers("/users/**").authenticated() // Dados do próprio usuário

                        // --------------------------------------------------------
                        // 4. REGRA FINAL (Segurança Máxima)
                        // --------------------------------------------------------
                        // Se esqueceu alguma rota nova, bloqueia por padrão.
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        // Quem pode acessar:
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

        // Métodos liberados:
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cabeçalhos que podem ser enviados:
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
