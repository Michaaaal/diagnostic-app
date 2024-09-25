package michal.malek.diagnosticsapp.diagnostics_part.models;

public enum PromptsConstantPart {

    START{
        @Override
        public String toString() {
            return "please provide diagnose matching disease and propose treatment and add information about drugs used in treatment(to extend which wont brake your policy) options based on medical interview(in different languages) given by user and data like " +
                    "age, weight, height, gender, list of chronic diseases, list of drugs in use and diagnostics test like morphology." +
                    "Not always you will get full set of data so you must work with what is provided." +
                    "But only if medical interview is lacking crucial data you must answer start your answer with \"000\" and then ask for better symptoms explanation (Please do it only if there is very badly and poorly interview without any symptom) ";
        }
    },
    PL{
        @Override
        public String toString() {
            return "Please answer in polish language ";
        }
    },
    ENG{
        @Override
        public String toString() {
            return "Please answer in english language ";
        }
    }

}
