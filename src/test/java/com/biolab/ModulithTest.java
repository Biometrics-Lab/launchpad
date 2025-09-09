package com.biolab;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithTest {

    @Test
    void createModuleDocumentation() {
        ApplicationModules modules = ApplicationModules.of(Application.class);
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }

    @Test
    void verifiesModularStructure() {
        ApplicationModules modules = ApplicationModules.of(Application.class);
        modules.verify();
    }
}
