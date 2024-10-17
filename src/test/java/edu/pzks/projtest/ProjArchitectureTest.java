package edu.pzks.projtest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
public class ProjArchitectureTest {

    private JavaClasses applicationClasses;

    @BeforeEach
    void initialize() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("edu.pzks.projtest");
    }

    // 1. Тест архітектури шарів: Контролери, Сервіси, Репозиторії
    @Test
    void shouldFollowLayerArchitecture() {
        Architectures.layeredArchitecture()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(applicationClasses);
    }

    // 2. Контролери повинні бути анотовані @RestController
    @Test
    void controllersShouldBeAnnotatedWithRestController() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RestController.class)
                .check(applicationClasses);
    }

    // 3. Сервіси повинні бути анотовані @Service
    @Test
    void servicesShouldBeAnnotatedWithService() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..service..")
                .should().beAnnotatedWith(Service.class)
                .check(applicationClasses);
    }

    // 4. Репозиторії повинні бути анотовані @Repository
    @Test
    void repositoriesShouldBeAnnotatedWithRepository() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..repository..")
                .should().beAnnotatedWith(Repository.class)
                .check(applicationClasses);
    }

    // 5. Сервісні класи повинні мати суфікс "Service"
    @Test
    void servicesShouldHaveServiceSuffix() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .check(applicationClasses);
    }

    // 6. Контролери повинні мати суфікс "Controller"
    @Test
    void controllersShouldHaveControllerSuffix() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .check(applicationClasses);
    }

    // 7. Репозиторії повинні мати суфікс "Repository"
    @Test
    void repositoriesShouldHaveRepositorySuffix() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .check(applicationClasses);
    }

    // 8. Класи в пакеті "..model.." повинні бути фінальними (final)
    @Test
    void modelClassesShouldBeFinal() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..model..")
                .should().haveModifier(JavaModifier.FINAL)
                .check(applicationClasses);
    }

    // 9. Немає циклічних залежностей між пакетами
    @Test
    void noCyclicDependenciesBetweenPackages() {
        SlicesRuleDefinition.slices()
                .matching("edu.pzks.projtest.(*)..")
                .should().beFreeOfCycles()
                .check(applicationClasses);
    }

    // 10. Жоден клас не повинен залежати від "..util.."
    @Test
    void noClassShouldDependOnUtil() {
        ArchRuleDefinition.noClasses()
                .should().dependOnClassesThat().resideInAPackage("..util..")
                .check(applicationClasses);
    }

    // 11. Контролери не повинні мати публічних полів
    @Test
    void controllersShouldNotHavePublicFields() {
        ArchRuleDefinition.fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().bePrivate()
                .check(applicationClasses);
    }

    // 12. Сервіси не повинні мати публічних полів
    @Test
    void servicesShouldNotHavePublicFields() {
        ArchRuleDefinition.fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..service..")
                .should().bePrivate()
                .check(applicationClasses);
    }

    // 13. Репозиторії повинні мати лише приватні поля
    @Test
    void repositoriesShouldHavePrivateFields() {
        ArchRuleDefinition.fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..repository..")
                .should().bePrivate()
                .check(applicationClasses);
    }

    // 14. Жоден сервісний клас не повинен залежати від контролерів
    @Test
    void servicesShouldNotDependOnControllers() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    // 15. Всі класи в пакеті "controller" повинні мати суфікс "Controller"
    @Test
    void allControllerClassesShouldHaveControllerSuffix() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .check(applicationClasses);
    }

    // 16. Сервісні класи не повинні напряму звертатися до бази даних
    @Test
    void servicesShouldNotDirectlyAccessDatabase() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .check(applicationClasses);
    }

    // 17. Сервіси не повинні мати публічних методів, крім основних
    @Test
    void servicesShouldNotHaveExcessivePublicMethods() {
        ArchRuleDefinition.methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..service..")
                .should().bePrivate()
                .check(applicationClasses);
    }

    // 18. Контролери не повинні напряму звертатися до репозиторіїв
    @Test
    void controllersShouldNotDirectlyAccessRepositories() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .check(applicationClasses);
    }

    // 19. Сервіси повинні викликати лише інші сервіси або репозиторії
    @Test
    void servicesShouldOnlyAccessServicesOrRepositories() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..service..")
                .should().onlyHaveDependentClassesThat()
                .resideInAnyPackage("..service..", "..repository..")
                .check(applicationClasses);
    }

    // 20. Репозиторії повинні мати методи, що починаються з "find", "save" або "delete"
    @Test
    void repositoryMethodsShouldFollowNamingConvention() {
        ArchRuleDefinition.methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..repository..")
                .should().haveNameMatching("^(find|save|delete)[A-Za-z]*")
                .check(applicationClasses);
    }
}
