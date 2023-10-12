package br.com.marcosmendes.todolist.users;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity createUser(@RequestBody UserDTO userDTO) {
        var userAlreadyExists = this.userRepository.findByUsername(userDTO.getUsername());
        if (userAlreadyExists != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome de usuário já está sendo utilizado.");
        }

        var passwordHashed = BCrypt.withDefaults().hashToString(12, userDTO.getPassword().toCharArray());
        userDTO.setPassword(passwordHashed);

        var createdUser = this.userRepository.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

}
