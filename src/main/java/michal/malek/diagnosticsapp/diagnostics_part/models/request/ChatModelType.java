package michal.malek.diagnosticsapp.diagnostics_part.models.request;

public enum ChatModelType {
    GPT4_O{
        @Override
        public int getTokenCost() {
            return 250;
        }

        @Override
        public String toString() {
            return "gpt-4o-2024-05-13";
        }
    },
    GPT4_MINI{
        @Override
        public int getTokenCost() {
            return 100;
        }

        @Override
        public String toString() {
            return "gpt-4o-mini";
        }
    },
    GPT35_TURBO{
        @Override
        public int getTokenCost() {
            return 25;
        }

        @Override
        public String toString() {
            return "gpt-3.5-turbo";
        }
    },
    ;

    public abstract int getTokenCost();
}
