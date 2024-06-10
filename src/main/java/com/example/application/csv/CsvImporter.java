package com.example.application.csv;
import com.example.application.model.User;
import com.example.application.model.Todo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.List;
import com.opencsv.CSVReader;

@Service
public class CsvImporter {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void importCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            records.remove(0);
            for (String[] record : records) {
                insertIntoDatabase(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertIntoDatabase(String[] record) {
        try {
            Long todoId = Long.parseLong(record[0]);
            String title = record[1];
            String comment = record[2];
            Boolean completed = Boolean.parseBoolean(record[3]);
            LocalDate expire = LocalDate.parse(record[4]);
            Integer userId = Integer.parseInt(record[5]);

            User user = em.find(User.class, userId);

            if (user != null) {
                Todo todo = em.find(Todo.class, todoId);
                if (todo == null) {
                    todo = new Todo();
                    todo.setTodoId(todoId);
                }
                todo.setTitle(title);
                todo.setComment(comment);
                todo.setCompleted(completed);
                todo.setExpire(expire);
                todo.setUser(user);

                em.merge(todo); // Use merge to handle both new and detached entities
            } else {
                System.out.println("User with id " + userId + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}