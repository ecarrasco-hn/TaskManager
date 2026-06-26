package com.taskmanager.controller.web;

import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskWebFunctionalTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TaskRepository taskRepository;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeAll
    void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
        
        taskRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Debería acceder a la página principal y mostrar lista vacía")
    void testIndexPage() {
        driver.get(baseUrl + "/tasks");
        
        WebElement title = driver.findElement(By.tagName("h1"));
        assertEquals("Task Dashboard", title.getText());
        
        WebElement emptyMessage = driver.findElement(By.xpath("//p[contains(text(), 'No tasks found.')]"));
        assertTrue(emptyMessage.isDisplayed());
    }

    @Test
    @DisplayName("Debería crear una nueva tarea")
    void testCreateTask() {
        driver.get(baseUrl + "/tasks/new");
        
        driver.findElement(By.id("title")).sendKeys("Functional Test Task");
        driver.findElement(By.id("description")).sendKeys("This is a description from Selenium");
        
        // Usar sendKeys con formato que Chrome suele aceptar en headless para input type="date"
        // A veces es "MMddyyyy" o "yyyy-MM-dd" dependiendo de la locale
        driver.findElement(By.id("dueDate")).sendKeys("12312026");
        
        Select statusSelect = new Select(driver.findElement(By.id("status")));
        statusSelect.selectByValue("PENDING");
        
        driver.findElement(By.id("taskForm")).submit();
        
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/tasks"));
        
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
        assertFalse(rows.isEmpty(), "La tabla no debería estar vacía");
        assertTrue(driver.getPageSource().contains("Functional Test Task"));
    }

    @Test
    @DisplayName("Debería editar una tarea existente")
    void testEditTask() {
        Task task = taskRepository.save(Task.builder()
                .title("Task to Edit")
                .description("Original description")
                .dueDate(LocalDate.now())
                .status(TaskStatus.PENDING)
                .build());
        
        driver.get(baseUrl + "/tasks");
        
        WebElement editBtn = driver.findElement(By.xpath("//a[contains(@href, '/tasks/edit/" + task.getId() + "')]"));
        editBtn.click();
        
        WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title")));
        titleInput.clear();
        titleInput.sendKeys("Updated Task Title");
        
        // Re-ingresar la fecha para asegurar validez en el submit
        driver.findElement(By.id("dueDate")).sendKeys("12312026");
        
        driver.findElement(By.id("taskForm")).submit();
        
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/tasks"));
        assertTrue(driver.getPageSource().contains("Updated Task Title"));
    }

    @Test
    @DisplayName("Debería cambiar el estado de una tarea")
    void testChangeTaskStatus() {
        Task task = taskRepository.save(Task.builder()
                .title("Status Change Task")
                .dueDate(LocalDate.now())
                .status(TaskStatus.PENDING)
                .build());
        
        driver.get(baseUrl + "/tasks");
        
        // Abrir dropdown de cambio de estado
        WebElement dropdownBtn = driver.findElement(By.xpath("//button[@title='Change Status']"));
        dropdownBtn.click();
        
        // Seleccionar IN_PROGRESS
        WebElement inProgressOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Set to IN_PROGRESS')]")));
        inProgressOption.click();
        
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/tasks"));
        
        // El badge usa minúsculas en el class: badge-in_progress
        WebElement statusBadge = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(@class, 'badge-in_progress')]")));
        assertEquals("IN_PROGRESS", statusBadge.getText());
    }

    @Test
    @DisplayName("Debería eliminar una tarea")
    void testDeleteTask() {
        Task task = taskRepository.save(Task.builder()
                .title("Task to Delete")
                .dueDate(LocalDate.now())
                .status(TaskStatus.PENDING)
                .build());
        
        driver.get(baseUrl + "/tasks");
        assertTrue(driver.getPageSource().contains("Task to Delete"));
        
        // Selenium headless no maneja bien los confirm() de JS por defecto si no se le indica
        // Pero en este caso, podemos usar ExecuteScript para bypass o aceptar
        WebElement deleteBtn = driver.findElement(By.xpath("//form[contains(@action, '/tasks/delete/" + task.getId() + "')]/button"));
        
        // Bypass confirm()
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.confirm = function(){return true;}");
        deleteBtn.click();
        
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/tasks"));
        assertFalse(driver.getPageSource().contains("Task to Delete"));
        assertTrue(driver.getPageSource().contains("No tasks found."));
    }
}
