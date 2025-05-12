package root.authorithationservicestoryline.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import root.authorithationservicestoryline.dto.UserRegistrationDTO;
import root.authorithationservicestoryline.dto.UserUpdateDTO;

import java.util.Map;

@Service
@Slf4j
public class UserManagementService {

    private final WebClient webClient;

    public UserManagementService(WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean checkUsername(String username) {
        try {
            String url = "/usrmng/user/check";

            Mono<Boolean> response = webClient.get().uri(uriBuilder -> uriBuilder.path(url).queryParam("username", username).build()).retrieve().bodyToMono(Boolean.class).onErrorReturn(false);

            return response.block();
        } catch (Exception e) {
            System.err.println("Ошибка при проверке пользователя: " + e.getMessage());
            return false;
        }
    }

    public ResponseEntity<?> registerUser(UserRegistrationDTO userRegistrationDTO) {
        String url = "/usrmng/user/addUser";

        try {
            UserRegistrationDTO response = webClient.post().uri(url).contentType(MediaType.APPLICATION_JSON).bodyValue(userRegistrationDTO).retrieve().onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), innerResponse -> innerResponse.bodyToMono(String.class).flatMap(errorBody -> {
                log.error("Ошибка от первого сервера: статус {} тело {}", innerResponse.statusCode(), errorBody);
                return Mono.error(new RuntimeException(errorBody));
            })).bodyToMono(UserRegistrationDTO.class).block();

            log.info("Пользователь успешно зарегистрирован: {}", response);
            return ResponseEntity.ok(Map.of("message", "User successfully saved"));
        } catch (RuntimeException e) {
            log.warn("Ошибка при регистрации пользователя: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Error during user registration", "errorDetails", e.getMessage()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при регистрации пользователя: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Unknown error during user registration", "errorDetails", e.getMessage()));
        }
    }


    public UserRegistrationDTO getUser(String username) {
        try {
            String url = "/usrmng/user/" + username;

            Mono<UserRegistrationDTO> response = webClient.get().uri(url).retrieve().bodyToMono(UserRegistrationDTO.class).doOnSuccess(res -> log.info("Данные пользователя: " + res)).doOnError(error -> log.error("Ошибка получения данных пользователя: " + error.getMessage()));

            return response.block();
        } catch (Exception e) {
            System.err.println("Ошибка при получении пользователя: " + e.getMessage());
            return null;
        }
    }

    public ResponseEntity<?> updateUser(UserUpdateDTO userUpdateDTO) {
        String url = "/usrmng/user/update";

        try {
            UserRegistrationDTO response = webClient.put().uri(url).contentType(MediaType.APPLICATION_JSON).bodyValue(userUpdateDTO).retrieve().onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), innerResponse -> innerResponse.bodyToMono(String.class).flatMap(errorBody -> {
                log.error("Ошибка от UserManagementService: статус {} тело {}", innerResponse.statusCode(), errorBody);
                return Mono.error(new RuntimeException(errorBody));
            })).bodyToMono(UserRegistrationDTO.class).block();

            log.info("Пользователь успешно обновлен: {}", response);
            return ResponseEntity.ok(Map.of("message", "User successfully updated"));
        } catch (RuntimeException e) {
            log.warn("Ошибка при обновление пользователя: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Error during update", "errorDetails", e.getMessage()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при обновление пользователя: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Unknown error during user updating", "errorDetails", e.getMessage()));
        }
    }

    public ResponseEntity<?> deleteUser(String username) {
        String url = "/usrmng/delete/user/" + username;
        try {
            Mono<String> response = webClient.get().uri(url).retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(res -> log.info("User succesfully deleted: " + res))
                    .doOnError(error -> log.error("Error deleting user: " + error.getMessage()));

            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","User succesfully deleted"));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Unknown error during user deletion"));
        }

    }
}
