package michal.malek.diagnosticsapp.models;

import jakarta.persistence.*;
import lombok.*;
import michal.malek.diagnosticsapp.auth.models.UserType;

import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uid;
    private String email;
    private String password;
    private boolean enabled;
    private boolean isGoogle;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @OneToOne(fetch = FetchType.EAGER)
    private UserData userData;


    public UserEntity ( String email, String password) {
        uid = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.userType = UserType.STANDARD;
        this.enabled = false;
        this.isGoogle = false;
        userData = null;
    }

    public UserEntity() {}
}
