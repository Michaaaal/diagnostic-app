package michal.malek.diagnosticsapp.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class RetrievePasswordOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uid;
    private String userUid;
    private Date date = new Date();

    public RetrievePasswordOperation(String userUid) {
        this.userUid = userUid;
        uid = UUID.randomUUID().toString();
    }

    public RetrievePasswordOperation() {}
}
