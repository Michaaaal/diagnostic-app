package michal.malek.diagnosticsapp.medic_data.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.medic_data.models.Drug;
import michal.malek.diagnosticsapp.medic_data.repositories.DrugRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DrugService {

    private final DrugRepository drugRepository;
    @Getter
    private final Path drugsCsvPath = Path.of("src/main/resources/other/drugs.csv");


    public void updateDrugs() throws IOException {
        try (Reader reader = Files.newBufferedReader(drugsCsvPath)) {
            CSVFormat format = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withDelimiter(';')
                    .withIgnoreHeaderCase();

            try (CSVParser parser = new CSVParser(reader, format)) {
                for (CSVRecord record : parser) {
                    String id = record.get("Identyfikator Produktu Leczniczego");
                    String productName = record.get("Nazwa Produktu Leczniczego");
                    String commonName = record.get("Nazwa powszechnie stosowana");
                    String leafletUrl = record.get("Ulotka");
                    String characteristicsUrl = record.get("Charakterystyka");

                    Drug drug = new Drug(id,productName,commonName,leafletUrl,characteristicsUrl);

                    if(drugRepository.findByMedicId(id)==null){
                        drugRepository.saveAndFlush(drug);
                        System.out.println("Drug Added");
                    }else {
                        System.out.println("Already exists");
                    }

                    System.out.println("ID: " + id);
                    System.out.println("Nazwa Produktu: " + productName);
                    System.out.println("Nazwa Powszechna: " + commonName);
                    System.out.println("Ulotka: " + leafletUrl);
                    System.out.println("Charakterystyka: " + characteristicsUrl);
                    System.out.println("-------------------------------\n");
                }
            }
        }
    }


    public Stream<Drug> findDrugs(String str) {
        List<Drug> allByCommonNameContainingIgnoreCase = drugRepository.findAllByCommonNameContainingIgnoreCase(str);
        List<Drug> allByProductNameContainingIgnoreCase = drugRepository.findAllByProductNameContainingIgnoreCase(str);
        Set<Drug> uniqueDrugs = new LinkedHashSet<>();
        uniqueDrugs.addAll(allByCommonNameContainingIgnoreCase);
        uniqueDrugs.addAll(allByProductNameContainingIgnoreCase);
        return uniqueDrugs.stream().sorted(new Comparator<Drug>() {
            LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
            @Override
            public int compare(Drug o1, Drug o2) {

                String query = str.toLowerCase();
                int o1Distance = levenshteinDistance.apply(o1.getCommonName().toLowerCase() , query);
                int o1Distance2 = levenshteinDistance.apply(o1.getProductName().toLowerCase() ,query);
                int o2Distance = levenshteinDistance.apply(o2.getCommonName().toLowerCase() , query);
                int o2Distance2 = levenshteinDistance.apply(o2.getProductName().toLowerCase() , query);
                if(o1Distance>o1Distance2){
                    o1Distance = o1Distance2;
                }
                if(o2Distance>o2Distance2){
                    o2Distance = o2Distance2;
                }

                return o1Distance - o2Distance;
            }
        }).limit(20);
    }
}
