package michal.malek.diagnosticsapp.core.models;

import jakarta.persistence.*;
import lombok.*;
import michal.malek.diagnosticsapp.auth.models.UserType;
import michal.malek.diagnosticsapp.medic_data.models.UserData;

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
    @OneToOne(cascade = CascadeType.ALL)
    private UserData userData;
    @Enumerated(EnumType.STRING)
    private UserType userType;


    public UserEntity ( String email, String password) {
        uid = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.userType = UserType.STANDARD;
        this.enabled = false;
        this.isGoogle = false;
        userData = new UserData();
    }

    public UserEntity() {}
}
