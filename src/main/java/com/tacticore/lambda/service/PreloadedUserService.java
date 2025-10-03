package com.tacticore.lambda.service;

import com.tacticore.lambda.model.UserEntity;
import com.tacticore.lambda.model.UserRole;
import com.tacticore.lambda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PreloadedUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    
    // Lista de usuarios precargados con sus roles (solo usuarios que existen en las demos)
    private static final List<PreloadedUser> PRELOADED_USERS = Arrays.asList(
        new PreloadedUser("flameZ", UserRole.ENTRY_FRAGGER.getDisplayName()),
        new PreloadedUser("Senzu", UserRole.SUPPORT.getDisplayName()),
        new PreloadedUser("bLitz", UserRole.IGL.getDisplayName()),
        new PreloadedUser("ropz", UserRole.ANCHOR.getDisplayName()),
        new PreloadedUser("ZywOo", UserRole.SNIPER.getDisplayName()),
        new PreloadedUser("apEX", UserRole.IGL.getDisplayName()),
        new PreloadedUser("mezii", UserRole.OBSERVER.getDisplayName()),
        new PreloadedUser("sh1ro", UserRole.SNIPER.getDisplayName())
        // Eliminados usuarios fantasma: s1mple, device, EliGE, NAF, Twistzz, Stewie2K, autimatic
        // Estos usuarios no aparecen en ninguna demo real
    );
    
    public void loadPreloadedUsers() {
        System.out.println("Loading preloaded users...");
        
        for (PreloadedUser preloadedUser : PRELOADED_USERS) {
            // Verificar si el usuario ya existe
            if (!userRepository.existsByName(preloadedUser.getName())) {
                UserEntity user = new UserEntity(preloadedUser.getName(), preloadedUser.getRole());
                
                // Asignar estadísticas iniciales realistas
                user.setTotalKills(generateInitialKills());
                user.setTotalDeaths(generateInitialDeaths());
                user.setTotalMatches(generateInitialMatches());
                user.setAverageScore(generateInitialScore());
                
                userRepository.save(user);
                System.out.println("Created preloaded user: " + preloadedUser.getName() + " (" + preloadedUser.getRole() + ")");
            } else {
                System.out.println("User already exists: " + preloadedUser.getName());
            }
        }
        
        System.out.println("Preloaded users loading completed.");
    }
    
    // Generar estadísticas iniciales realistas basadas en el nombre del usuario
    private Integer generateInitialKills() {
        // Usar el hash del nombre para generar valores consistentes
        return 150 + (Math.abs("flameZ".hashCode()) % 200); // Entre 150-350 kills
    }
    
    private Integer generateInitialDeaths() {
        return 100 + (Math.abs("flameZ".hashCode()) % 150); // Entre 100-250 deaths
    }
    
    private Integer generateInitialMatches() {
        return 20 + (Math.abs("flameZ".hashCode()) % 30); // Entre 20-50 matches
    }
    
    private Double generateInitialScore() {
        return 5.0 + (Math.abs("flameZ".hashCode()) % 50) / 10.0; // Entre 5.0-10.0
    }
    
    // Inner class para usuarios precargados
    private static class PreloadedUser {
        private final String name;
        private final String role;
        
        public PreloadedUser(String name, String role) {
            this.name = name;
            this.role = role;
        }
        
        public String getName() { return name; }
        public String getRole() { return role; }
    }
    
    // Método para obtener la lista de usuarios precargados
    public List<String> getPreloadedUserNames() {
        return PRELOADED_USERS.stream()
                .map(PreloadedUser::getName)
                .toList();
    }
    
    // Método para verificar si un usuario está en la lista precargada
    public boolean isPreloadedUser(String name) {
        return PRELOADED_USERS.stream()
                .anyMatch(user -> user.getName().equals(name));
    }
}
