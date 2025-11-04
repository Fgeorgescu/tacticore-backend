package com.tacticore.lambda.service;

import com.tacticore.lambda.model.UserEntity;
import com.tacticore.lambda.model.UserRole;
import com.tacticore.lambda.model.dto.UserDto;
import com.tacticore.lambda.model.dto.UserProfileDto;
import com.tacticore.lambda.repository.KillRepository;
import com.tacticore.lambda.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KillRepository killRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Setup común si es necesario
    }

    @Test
    void testCreateOrGetUser_NewUser() {
        // Given
        String name = "newUser";
        String role = "PLAYER";
        when(userRepository.findByName(name)).thenReturn(Optional.empty());
        
        UserEntity newUser = new UserEntity(name, role);
        when(userRepository.save(any(UserEntity.class))).thenReturn(newUser);

        // When
        UserEntity result = userService.createOrGetUser(name, role);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(role, result.getRole());
        verify(userRepository).findByName(name);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testCreateOrGetUser_ExistingUser() {
        // Given
        String name = "existingUser";
        String role = "PLAYER";
        UserEntity existingUser = new UserEntity(name, role);
        when(userRepository.findByName(name)).thenReturn(Optional.of(existingUser));

        // When
        UserEntity result = userService.createOrGetUser(name, role);

        // Then
        assertNotNull(result);
        assertEquals(existingUser, result);
        verify(userRepository).findByName(name);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateOrGetUser_WithRandomRole() {
        // Given
        String name = "newUser";
        when(userRepository.findByName(name)).thenReturn(Optional.empty());
        
        UserEntity newUser = new UserEntity(name, UserRole.getAllDisplayNames()[0]);
        when(userRepository.save(any(UserEntity.class))).thenReturn(newUser);

        // When
        UserEntity result = userService.createOrGetUser(name);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertNotNull(result.getRole());
        verify(userRepository).findByName(name);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testGetUserByName() {
        // Given
        String name = "testUser";
        UserEntity user = new UserEntity(name, "PLAYER");
        when(userRepository.findByName(name)).thenReturn(Optional.of(user));

        // When
        Optional<UserEntity> result = userService.getUserByName(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findByName(name);
    }

    @Test
    void testGetUserByName_NotFound() {
        // Given
        String name = "nonExistentUser";
        when(userRepository.findByName(name)).thenReturn(Optional.empty());

        // When
        Optional<UserEntity> result = userService.getUserByName(name);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByName(name);
    }

    @Test
    void testUserExists() {
        // Given
        String name = "testUser";
        when(userRepository.existsByName(name)).thenReturn(true);

        // When
        boolean result = userService.userExists(name);

        // Then
        assertTrue(result);
        verify(userRepository).existsByName(name);
    }

    @Test
    void testGetAllUsers() {
        // Given
        UserEntity user1 = new UserEntity("user1", "PLAYER");
        UserEntity user2 = new UserEntity("user2", "COACH");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserEntity> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUsersByRole() {
        // Given
        String role = "PLAYER";
        UserEntity user1 = new UserEntity("user1", role);
        UserEntity user2 = new UserEntity("user2", role);
        when(userRepository.findByRole(role)).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserEntity> result = userService.getUsersByRole(role);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByRole(role);
    }

    @Test
    void testSearchUsersByName() {
        // Given
        String searchTerm = "test";
        UserEntity user1 = new UserEntity("testUser1", "PLAYER");
        UserEntity user2 = new UserEntity("testUser2", "PLAYER");
        when(userRepository.findByNameContaining(searchTerm)).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserEntity> result = userService.searchUsersByName(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByNameContaining(searchTerm);
    }

    @Test
    void testGetTopPlayersByScore() {
        // Given
        UserEntity user1 = new UserEntity("user1", "PLAYER");
        user1.setAverageScore(9.5);
        UserEntity user2 = new UserEntity("user2", "PLAYER");
        user2.setAverageScore(8.0);
        when(userRepository.findAllOrderByAverageScoreDesc()).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserEntity> result = userService.getTopPlayersByScore();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAllOrderByAverageScoreDesc();
    }

    @Test
    void testGetTopPlayersByKills() {
        // Given
        UserEntity user1 = new UserEntity("user1", "PLAYER");
        user1.setTotalKills(100);
        UserEntity user2 = new UserEntity("user2", "PLAYER");
        user2.setTotalKills(80);
        when(userRepository.findAllOrderByTotalKillsDesc()).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserEntity> result = userService.getTopPlayersByKills();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAllOrderByTotalKillsDesc();
    }

    @Test
    void testGetTopPlayersByKDR() {
        // Given
        UserEntity user1 = new UserEntity("user1", "PLAYER");
        user1.setTotalKills(50);
        user1.setTotalDeaths(25);
        UserEntity user2 = new UserEntity("user2", "PLAYER");
        user2.setTotalKills(40);
        user2.setTotalDeaths(30);
        when(userRepository.findAllOrderByKDRDesc()).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserEntity> result = userService.getTopPlayersByKDR();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAllOrderByKDRDesc();
    }

    @Test
    void testGetTopPlayersByMatches() {
        // Given
        int minMatches = 5;
        UserEntity user1 = new UserEntity("user1", "PLAYER");
        user1.setTotalMatches(10);
        UserEntity user2 = new UserEntity("user2", "PLAYER");
        user2.setTotalMatches(8);
        when(userRepository.findTopPlayersByMatches(minMatches)).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserEntity> result = userService.getTopPlayersByMatches(minMatches);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findTopPlayersByMatches(minMatches);
    }

    @Test
    void testUpdateUserStats() {
        // Given
        String userName = "testUser";
        int kills = 25;
        int deaths = 15;
        double score = 8.5;
        
        UserEntity user = new UserEntity(userName, "PLAYER");
        when(userRepository.findByName(userName)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // When
        userService.updateUserStats(userName, kills, deaths, score);

        // Then
        assertEquals(kills, user.getTotalKills());
        assertEquals(deaths, user.getTotalDeaths());
        assertEquals(score, user.getAverageScore());
        verify(userRepository).findByName(userName);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserStats_UserNotFound() {
        // Given
        String userName = "nonExistentUser";
        when(userRepository.findByName(userName)).thenReturn(Optional.empty());

        // When
        userService.updateUserStats(userName, 25, 15, 8.5);

        // Then
        verify(userRepository).findByName(userName);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testGetUserStatistics() {
        // Given
        when(userRepository.countAllUsers()).thenReturn(50L);
        when(userRepository.getAverageScoreOfAllUsers()).thenReturn(7.5);
        when(userRepository.getTotalKillsOfAllUsers()).thenReturn(500L);
        when(userRepository.getTotalDeathsOfAllUsers()).thenReturn(450L);
        when(userRepository.getTotalMatchesOfAllUsers()).thenReturn(100L);
        when(userRepository.getUsersCountByRole()).thenReturn(Arrays.asList(
            new Object[]{"PLAYER", 45L},
            new Object[]{"COACH", 5L}
        ));

        // When
        UserService.UserStats result = userService.getUserStatistics();

        // Then
        assertNotNull(result);
        assertEquals(50L, result.getTotalUsers());
        assertEquals(7.5, result.getAverageScore());
        assertEquals(500L, result.getTotalKills());
        assertEquals(450L, result.getTotalDeaths());
        assertEquals(100L, result.getTotalMatches());
        assertNotNull(result.getRoleStats());
    }

    @Test
    void testConvertToDto() {
        // Given
        UserEntity entity = new UserEntity("testUser", "PLAYER");
        entity.setId(1L);
        entity.setAverageScore(8.5);
        entity.setTotalKills(50);
        entity.setTotalDeaths(30);
        entity.setTotalMatches(10);

        // When
        UserDto dto = userService.convertToDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getRole(), dto.getRole());
        assertEquals(entity.getAverageScore(), dto.getAverageScore());
        assertEquals(entity.getTotalKills(), dto.getTotalKills());
        assertEquals(entity.getTotalDeaths(), dto.getTotalDeaths());
        assertEquals(entity.getTotalMatches(), dto.getTotalMatches());
    }

    @Test
    void testConvertToDtoList() {
        // Given
        UserEntity entity1 = new UserEntity("user1", "PLAYER");
        UserEntity entity2 = new UserEntity("user2", "COACH");
        List<UserEntity> entities = Arrays.asList(entity1, entity2);

        // When
        List<UserDto> dtos = userService.convertToDtoList(entities);

        // Then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(entity1.getName(), dtos.get(0).getName());
        assertEquals(entity2.getName(), dtos.get(1).getName());
    }

    @Test
    void testGetAvailableRoles() {
        // When
        String[] roles = userService.getAvailableRoles();

        // Then
        assertNotNull(roles);
        assertTrue(roles.length > 0);
        // Verificar que contiene alguno de los roles disponibles
        assertTrue(Arrays.asList(roles).contains("Entry Fragger") || 
                   Arrays.asList(roles).contains("Francotirador") ||
                   Arrays.asList(roles).contains("Líder en el juego"));
    }

    @Test
    void testEnsureUsersExist() {
        // Given
        List<String> userNames = Arrays.asList("user1", "user2");
        UserEntity user1 = new UserEntity("user1", "PLAYER");
        UserEntity user2 = new UserEntity("user2", "PLAYER");
        
        when(userRepository.findByName("user1")).thenReturn(Optional.empty());
        when(userRepository.findByName("user2")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(user1, user2);

        // When
        List<UserEntity> result = userService.ensureUsersExist(userNames);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(2)).findByName(anyString());
        verify(userRepository, times(2)).save(any(UserEntity.class));
    }

    @Test
    void testGetUserProfile() {
        // Given
        String userName = "testUser";
        UserEntity user = new UserEntity(userName, "PLAYER");
        user.setId(1L);
        user.setTotalKills(50);
        user.setTotalDeaths(30);
        user.setTotalMatches(10);
        user.setAverageScore(8.5);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        when(userRepository.findByName(userName)).thenReturn(Optional.of(user));
        
        List<Object[]> weaponStats = new ArrayList<>();
        weaponStats.add(new Object[]{"ak47", 20L});
        when(killRepository.getWeaponUsageStatsByUser(userName)).thenReturn(weaponStats);
        
        List<Object[]> locationStats = new ArrayList<>();
        locationStats.add(new Object[]{"BombsiteA", 15L});
        when(killRepository.getLocationStatsByUser(userName)).thenReturn(locationStats);

        // When
        UserProfileDto result = userService.getUserProfile(userName);

        // Then
        assertNotNull(result);
        assertEquals(userName, result.getUsername());
        assertEquals(user.getRole(), result.getRole());
        assertNotNull(result.getStats());
        assertNotNull(result.getRecentActivity());
        assertNotNull(result.getPreferences());
        verify(userRepository).findByName(userName);
    }

    @Test
    void testGetUserProfile_UserNotFound() {
        // Given
        String userName = "nonExistentUser";
        when(userRepository.findByName(userName)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> userService.getUserProfile(userName));
        verify(userRepository).findByName(userName);
    }

    @Test
    void testGetKillsUsersDebugInfo() {
        // Given
        when(killRepository.findAllAttackers()).thenReturn(Arrays.asList("player1", "player2"));
        when(killRepository.findAllVictims()).thenReturn(Arrays.asList("player2", "player3"));
        
        UserEntity user1 = new UserEntity("player1", "PLAYER");
        UserEntity user2 = new UserEntity("player2", "PLAYER");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // When
        Map<String, Object> result = userService.getKillsUsersDebugInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("totalKillsUsers"));
        assertTrue(result.containsKey("totalPreloadedUsers"));
        assertTrue(result.containsKey("matchingUsers"));
        assertTrue(result.containsKey("killsOnlyUsers"));
        assertTrue(result.containsKey("preloadedOnlyUsers"));
        verify(killRepository).findAllAttackers();
        verify(killRepository).findAllVictims();
        verify(userRepository).findAll();
    }

    @Test
    void testGetRealUserStatsFromKills() {
        // Given
        String userName = "testUser";
        when(killRepository.countKillsByUser(userName)).thenReturn(50L);
        when(killRepository.countDeathsByUser(userName)).thenReturn(30L);
        when(killRepository.countHeadshotsByUser(userName)).thenReturn(25L);
        
        UserEntity user = new UserEntity(userName, "PLAYER");
        user.setTotalKills(50);
        user.setTotalDeaths(30);
        user.setAverageScore(8.5);
        when(userRepository.findByName(userName)).thenReturn(Optional.of(user));

        // When
        Map<String, Object> result = userService.getRealUserStatsFromKills(userName);

        // Then
        assertNotNull(result);
        assertEquals(userName, result.get("userName"));
        assertEquals(50, result.get("killsFromRepository"));
        assertEquals(30, result.get("deathsFromRepository"));
        assertEquals(25, result.get("headshotsFromRepository"));
        assertTrue(result.containsKey("calculatedKDR"));
        assertTrue(result.containsKey("calculatedScore"));
        verify(killRepository).countKillsByUser(userName);
        verify(killRepository).countDeathsByUser(userName);
        verify(killRepository).countHeadshotsByUser(userName);
    }
}

