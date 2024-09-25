package michal.malek.diagnosticsapp.diagnostics_part.models.prompts;

public enum PromptsConstantPart {

    START{
        @Override
        public String toString() {
            return "please provide diagnose matching disease and propose treatment and add information about drugs used in treatment(to extend which wont brake your policy) options based on medical interview(in different languages) given by user and data like " +
                    "age, weight, height, gender, list of chronic diseases, list of drugs in use and diagnostics test like morphology." +
                    //"Not always you will get full set of data so you must work with what is provided." +
                    "If you dont have solid diagnose, Then you have to ask questions like a doctor and gather information's from user, ask for details witch might be crucial in diagnose" +
                    //"But only if medical interview is lacking crucial data you must ask for better symptoms explanation (Please do it only if there is very badly and poorly interview without any symptom)." +
                    "IMPORTANT Please answer in the same language as provided MEDICAL INTERVIEW !!!";
        }
    }
}
