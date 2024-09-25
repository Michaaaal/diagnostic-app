package michal.malek.diagnosticsapp.auth.models;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {
    STANDARD {
        @Override
        public int getRefillTokenAmount() {
            return 1000;
        }
    },PREMIUM{
        @Override
        public int getRefillTokenAmount() {
            return 5000;
        }
    },ULTIMATE {
        @Override
        public int getRefillTokenAmount() {
            return 10000;
        }
    },ADMIN{
        @Override
        public int getRefillTokenAmount() {
            return 100000;
        }
    };

    @Override
    public String getAuthority() {
        return this.toString();
    }

    public abstract int getRefillTokenAmount();
}
