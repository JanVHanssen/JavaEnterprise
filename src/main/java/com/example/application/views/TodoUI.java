package com.example.application.views;

import com.example.application.dto.TodoDTO;
import com.example.application.jms.JmsSender;
import com.example.application.model.Todo;
import com.example.application.model.User;
import com.example.application.service.TodoService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;


@PageTitle("Todo's")
@Route("todo")
public class TodoUI extends Composite<HorizontalLayout> {

    @Autowired
    private TodoService todoService;
    @Autowired
    private JmsSender jmsSender;

    private Grid<Todo> todoGrid = new Grid<>();
    private TextField titleField = new TextField("Title");
    private TextArea commentField = new TextArea("Comment");
    private Checkbox completedCheckbox = new Checkbox("Completed");
    private DatePicker expireDatepicker = new DatePicker("Expire Date");
    private Button addBtn = new Button("Add");
    private Button updateBtn = new Button("Update");
    private Button deleteBtn = new Button("Delete");
    private Button queueBtn = new Button("Send to Queue");
    private Button soapBtn = new Button("Send with SOAP");
    private Button logoutBtn = new Button("Logout");

    public TodoUI() {
        getContent().setSizeFull();
        getContent().setPadding(false);
        getContent().setMargin(false);
        getContent().setSpacing(false);

        // Left side layout for input fields
        VerticalLayout inputLayout = new VerticalLayout(titleField, commentField, completedCheckbox, expireDatepicker, addBtn, updateBtn, deleteBtn, queueBtn, soapBtn);
        inputLayout.setAlignItems(FlexComponent.Alignment.STRETCH); // Align items vertically
        inputLayout.setWidth("30%"); // Set width to 30%

        // Right side layout for todo grid
        VerticalLayout gridLayout = new VerticalLayout(todoGrid);
        gridLayout.setSizeFull();

        // Add logout button at the bottom
        gridLayout.add(logoutBtn);
        gridLayout.setAlignItems(FlexComponent.Alignment.END); // Align logout button to the end

        // Configure the main layout
        HorizontalLayout mainLayout = new HorizontalLayout(inputLayout, gridLayout);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(1, gridLayout); // Allow the todo grid to take remaining space

        getContent().add(mainLayout);

        configureGrid();
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        // Configure button click listeners
        addBtn.addClickListener(event -> addTodo());
        updateBtn.addClickListener(event -> updateTodo());
        deleteBtn.addClickListener(event -> deleteTodo());
        queueBtn.addClickListener(event -> sendToQueue());
        logoutBtn.addClickListener(event -> logout());

        // Configure grid selection listener
        todoGrid.asSingleSelect().addValueChangeListener(event -> {
            Todo selectedTodo = event.getValue();
            if (selectedTodo != null) {
                fillFields(selectedTodo);
                updateBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
            } else {
                clearFields();
                updateBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
            }
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        refreshGrid();

        User currentUser = (User) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser != null) {
            System.out.println("User is logged in: " + currentUser.getUserId());
            String jwtToken = (String) VaadinSession.getCurrent().getAttribute("jwt");
            if (jwtToken != null) {
                System.out.println("JWT Received: " + jwtToken);
            } else {
                System.out.println("No JWT token found in session.");
            }
        } else {
            System.out.println("Session doesn't have a logged-in user. User might be logged out.");
            UI.getCurrent().navigate("login");
        }
    }

    private void configureGrid() {
        todoGrid.addColumn(Todo::getTitle).setHeader("Title");
        todoGrid.addColumn(Todo::getComment).setHeader("Comment");
        todoGrid.addColumn(Todo::getCompleted).setHeader("Completed");
        todoGrid.addColumn(Todo::getExpire).setHeader("Expire Date");
        todoGrid.setSizeFull();
    }

    private void fillFields(Todo todo) {
        titleField.setValue(todo.getTitle());
        commentField.setValue(todo.getComment());
        completedCheckbox.setValue(todo.getCompleted());
        expireDatepicker.setValue(todo.getExpire());
    }

    private void clearFields() {
        titleField.clear();
        commentField.clear();
        completedCheckbox.clear();
        expireDatepicker.clear();
    }

    private void addTodo() {
        User currentUser = (User) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser != null) {
            Todo todo = new Todo();
            todo.setTitle(titleField.getValue());
            todo.setComment(commentField.getValue());
            todo.setCompleted(completedCheckbox.getValue());
            todo.setExpire(expireDatepicker.getValue());
            todo.setUser(currentUser);

            if (todoService.save(todo) != null) {
                Notification.show("Todo Added");
                refreshGrid();
                clearFields();
            } else {
                Notification.show("Error Adding Todo");
            }
        } else {
            Notification.show("User not logged in.");
        }
    }

    private void updateTodo() {
        Todo selectedTodo = todoGrid.asSingleSelect().getValue();
        if (selectedTodo != null) {
            selectedTodo.setTitle(titleField.getValue());
            selectedTodo.setComment(commentField.getValue());
            selectedTodo.setCompleted(completedCheckbox.getValue());
            selectedTodo.setExpire(expireDatepicker.getValue());
            todoService.update(selectedTodo);
            refreshGrid();
            clearFields();
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
    }

    private void deleteTodo() {
        Todo selectedTodo = todoGrid.asSingleSelect().getValue();
        if (selectedTodo != null) {
            todoService.deleteById(selectedTodo.getTodoId());
            refreshGrid();
            clearFields();
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
    }

    private void refreshGrid() {
        User currentUser = (User) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser != null) {
            System.out.println("Refreshing grid for user: " + currentUser.getUserId());
            todoGrid.setItems(todoService.findAllByUser(currentUser));
        } else {
            System.out.println("Current user is null. Unable to refresh grid.");
        }
    }

    private TodoDTO convertTodoToDTO(Todo todo) {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle(todo.getTitle());
        todoDTO.setComment(todo.getComment());
        todoDTO.setCompleted(todo.getCompleted());
        todoDTO.setExpire(todo.getExpire());
        return todoDTO;
    }

    private void sendToQueue() {
        Todo selectedTodo = todoGrid.asSingleSelect().getValue();
        User currentUser = (User) VaadinSession.getCurrent().getAttribute("user");

        if (selectedTodo != null && currentUser != null) {
            // Create a TodoDTO object from the selected Todo
            TodoDTO todoDTO = convertTodoToDTO(selectedTodo);
            todoDTO.setUserId(currentUser.getUserId()); // Set the user ID

            System.out.println("Sending TodoDTO to queue: " + todoDTO);

            try {
                // Use JmsSender to send the TodoDTO to the queue
                jmsSender.sendMessage(todoDTO);
                Notification.show("Todo sent to queue");
            } catch (Exception e) {
                Notification.show("Error sending todo to queue");
                e.printStackTrace();
            }
        } else {
            Notification.show("No todo selected or user not logged in");
        }
    }

    private void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
    }

}
