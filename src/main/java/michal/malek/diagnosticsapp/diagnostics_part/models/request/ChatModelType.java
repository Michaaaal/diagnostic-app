package michal.malek.diagnosticsapp.diagnostics_part.models;

public enum ChatModelType {
    GPT4_O{
        @Override
        public String toString() {
            return "gpt-4o-2024-05-13";
        }
    },
    GPT4_MINI{
        @Override
        public String toString() {
            return "gpt-4o-mini";
        }
    },
    GPT35_TURBO{
        @Override
        public String toString() {
            return "gpt-3.5-turbo";
        }
    },
}
