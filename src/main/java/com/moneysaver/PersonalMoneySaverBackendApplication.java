package com.moneysaver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class PersonalMoneySaverBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalMoneySaverBackendApplication.class, args);
    }

    @Configuration
    public static class WebConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*");
        }
    }
}

// ------------------------------------------
// 1. TRANSACTION ENTITY, REPOSITORY, CONTROLLER
// ------------------------------------------

@Entity
@Table(name = "transactions")
class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String date;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Transaction() {}

    public Transaction(String title, Double amount, String type, String category, String date, String notes) {
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

@Repository
interface TransactionRepository extends JpaRepository<Transaction, Long> {}

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction details) {
        Optional<Transaction> optionalTx = transactionRepository.findById(id);
        if (optionalTx.isPresent()) {
            Transaction tx = optionalTx.get();
            tx.setTitle(details.getTitle());
            tx.setAmount(details.getAmount());
            tx.setType(details.getType());
            tx.setCategory(details.getCategory());
            tx.setDate(details.getDate());
            tx.setNotes(details.getNotes());
            return ResponseEntity.ok(transactionRepository.save(tx));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

// ------------------------------------------
// 2. SAVINGS GOAL ENTITY, REPOSITORY, CONTROLLER
// ------------------------------------------

@Entity
@Table(name = "savings_goals")
class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double targetAmount;

    @Column(nullable = false)
    private Double currentAmount;

    @Column(nullable = false)
    private String targetDate;

    @Column(nullable = false)
    private String category;

    public SavingsGoal() {}

    public SavingsGoal(String title, Double targetAmount, Double currentAmount, String targetDate, String category) {
        this.title = title;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(Double targetAmount) { this.targetAmount = targetAmount; }

    public Double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(Double currentAmount) { this.currentAmount = currentAmount; }

    public String getTargetDate() { return targetDate; }
    public void setTargetDate(String targetDate) { this.targetDate = targetDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}

@Repository
interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {}

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
class SavingsGoalController {

    private final SavingsGoalRepository goalRepository;

    public SavingsGoalController(SavingsGoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @GetMapping
    public List<SavingsGoal> getAllGoals() {
        return goalRepository.findAll();
    }

    @PostMapping
    public SavingsGoal createGoal(@RequestBody SavingsGoal goal) {
        return goalRepository.save(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoal> updateGoal(@PathVariable Long id, @RequestBody SavingsGoal details) {
        Optional<SavingsGoal> optionalGoal = goalRepository.findById(id);
        if (optionalGoal.isPresent()) {
            SavingsGoal goal = optionalGoal.get();
            goal.setTitle(details.getTitle());
            goal.setTargetAmount(details.getTargetAmount());
            goal.setCurrentAmount(details.getCurrentAmount());
            goal.setTargetDate(details.getTargetDate());
            goal.setCategory(details.getCategory());
            return ResponseEntity.ok(goalRepository.save(goal));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<SavingsGoal> depositToGoal(@PathVariable Long id, @RequestParam Double amount) {
        Optional<SavingsGoal> optionalGoal = goalRepository.findById(id);
        if (optionalGoal.isPresent()) {
            SavingsGoal goal = optionalGoal.get();
            goal.setCurrentAmount(goal.getCurrentAmount() + amount);
            return ResponseEntity.ok(goalRepository.save(goal));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        if (goalRepository.existsById(id)) {
            goalRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
