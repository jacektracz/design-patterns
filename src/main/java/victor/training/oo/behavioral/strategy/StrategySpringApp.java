package victor.training.oo.behavioral.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class StrategySpringApp implements CommandLineRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(StrategySpringApp.class)
                .profiles("localProps")
                .run(args);
    }


    private ConfigProvider configProvider = new ConfigFileProvider();

    // TODO [1] Break CustomsService logic into Strategies
    // TODO [2] Convert it to Chain Of Responsibility
    // TODO [3] Wire with Spring
    // TODO [4] ConfigProvider: selected based on environment props, with Spring
    @Autowired
    private CustomsService service;
    public void run(String... args) throws Exception {
        System.out.println("Tax for (RO,100,100) = " + service.computeCustomsTax("RO", 100, 100));
        System.out.println("Tax for (CH,100,100) = " + service.computeCustomsTax("CH", 100, 100));
        System.out.println("Tax for (UK,100,100) = " + service.computeCustomsTax("UK", 100, 100));

        System.out.println("Property: " + configProvider.getProperties().getProperty("someProp"));
    }
}

@Service
class CustomsService {
    @Autowired
    private List<TaxComputer> computers;

    public double computeCustomsTax(String originCountry, double tobacoValue, double regularValue) { // UGLY API we CANNOT change
        TaxComputer computer = getTaxComputer(originCountry);
        return computer.compute(tobacoValue, regularValue);
    }

    private TaxComputer getTaxComputer(String originCountry) {
        return computers.stream()
                .filter(c -> c.accepts(originCountry))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(originCountry));
    }
}

interface TaxComputer {
    double compute(double tobacoValue, double regularValue);
    boolean accepts(String originCountry);
}

@Service
class UKTaxComputer implements TaxComputer {
    public static final String UK = "UK";
    public double compute(double tobacoValue, double regularValue) {
        return tobacoValue / 2 + regularValue / 2;
    }
    public boolean accepts(String originCountry) {
        return UK.equals(originCountry);
    }
}

@Service
class EUTaxComputer implements TaxComputer {
    public double compute(double tobacoValue, double regularValue) {
        return tobacoValue / 3;
    }
    public boolean accepts(String originCountry) {
        return Arrays.asList("RO","ES","FR").contains(originCountry);
    }
}

@Service
class CHTaxComputer implements TaxComputer {
    public double compute(double tobacoValue, double regularValue) {
        return tobacoValue + regularValue;
    }
    public boolean accepts(String originCountry) {
        return "CH".equals(originCountry);
    }
}
