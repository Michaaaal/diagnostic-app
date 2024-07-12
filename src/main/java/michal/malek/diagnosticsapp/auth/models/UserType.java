package michal.malek.diagnosticsapp.auth.models;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {
    STANDARD,PREMIUM,ULTIMATE;

    @Override
    public String getAuthority() {
        return this.toString();
    }
}
