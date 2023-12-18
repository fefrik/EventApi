package cz.pfeffer.eventapi.it;

import cz.pfeffer.eventapi.domain.ImplementationEnum;
import cz.pfeffer.eventapi.rest.EventController;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
class EventControllerIT {

    public static final String UNIQUE_USERS_ENDPOINT = "/api/events/uniqueUsers";

    @Autowired
    DataSource dataSource;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    EventController eventController;

    @LocalServerPort
    private Integer port;

    public static final String DB_NAME = "test";
    public static final String DB_USERNAME = "test";
    public static final String DB_PASSWD = "test";

    boolean dataLoaded = false;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USERNAME)
            .withPassword(DB_PASSWD);

    @BeforeEach
    public void setUp() throws Exception {
        // Load CSV data into the database only once
        if (!dataLoaded) {
            loadData("test.csv");
            dataLoaded = true;
        }
    }

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Test
    void testGetUniqueUsersByDocIdAndDateRangDb() throws Exception {
        testUniqueUsersPerDay(ImplementationEnum.DB);
    }

    @Test
    void testGetUniqueUsersByDocIdAndDateRangeHash() throws Exception {
        testUniqueUsersPerDay(ImplementationEnum.HASHMAP);
    }

    private void testUniqueUsersPerDay(ImplementationEnum implementation) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(UNIQUE_USERS_ENDPOINT + "/CR10/2023-12-06/2023-12-31").requestAttr("impl", implementation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].date").value("2023-12-06"))
                .andExpect(jsonPath("$[0].userCount").value(2))
                .andExpect(jsonPath("$[1].date").value("2023-12-20"))
                .andExpect(jsonPath("$[1].userCount").value(1));
    }

    private void loadData(String fileName) throws Exception {
        Assertions.assertTrue(JdbcUtils.supportsBatchUpdates(dataSource.getConnection()));

        // Load the CSV file from test resources
        ClassPathResource resource = new ClassPathResource(fileName);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                MediaType.TEXT_PLAIN_VALUE,
                resource.getInputStream().readAllBytes()
        );

        // Perform the upload using MockMvc
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/events/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }
}