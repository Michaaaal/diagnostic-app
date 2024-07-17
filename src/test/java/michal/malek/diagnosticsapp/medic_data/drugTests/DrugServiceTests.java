package michal.malek.diagnosticsapp.medic_data.drugTests;

import michal.malek.diagnosticsapp.medic_data.repositories.DrugRepository;
import michal.malek.diagnosticsapp.medic_data.services.DrugService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DrugServiceTests {

    @Mock
    private DrugRepository drugRepository;

    @InjectMocks
    private DrugService drugService;


    @Test
    public void drugCsvPathTest() {
        Path expectedPath = Path.of("drugs.csv");
        System.out.println(expectedPath);
        assertEquals(expectedPath, drugService.getDrugsCsvPath(), "The drugsCsvPath should be set to 'drugs.csv'");
    }

}



