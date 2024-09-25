package michal.malek.diagnosticsapp.diagnostics_part.models.prompts;

import michal.malek.diagnosticsapp.medic_data.models.UserData;

public class PromptUserData extends UserData {

    @Override
    public String toString() {
        return "DATA SET- " +
                "age-" + age +
                ", weight-" + weight +
                ", height-" + height +
                ", gender-'" + gender + '\'' +
                ", chronicDiseaseSet-" + getChronicDiseaseSetPrompt() +
                ", drugSet-" + getDrugsPrompt();

    }

    private String getChronicDiseaseSetPrompt(){
        StringBuilder retVal = new StringBuilder();
        if(!chronicDiseaseSet.isEmpty()){
            for( var elem : chronicDiseaseSet){
                retVal.append(elem.getName()).append(", ");
            }
            return retVal.toString();
        }return "Lack of chronic diseases";
    }

    private String getDrugsPrompt(){
        StringBuilder retVal = new StringBuilder();
        if(!drugSet.isEmpty()){
            for( var elem : drugSet){
                retVal.append(elem.getCommonName()).append("also called ").append(elem.getProductName()).append(", ");
            }
            return retVal.toString();
        }return "Lack of drugs";
    }

}
