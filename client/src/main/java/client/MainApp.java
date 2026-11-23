package client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG=Logger.getLogger(MainApp.class.getName());
    private final HttpLabClient api=new HttpLabClient();
    private final TableView<Equipment> equipment=new TableView<>();
    private final TableView<ServiceRequest> requests=new TableView<>();
    private final TableView<Specialist> specialists=new TableView<>();
    private final TableView<ServiceRecord> records=new TableView<>();

    @Override public void start(Stage stage) {
        TabPane tabs=new TabPane(
            new Tab("Обладнання",equipmentPane()),
            new Tab("Заявки на обслуговування",requestPane()),
            new Tab("Фахівці",specialistPane()),
            new Tab("Сервісні записи",recordPane())
        );
        tabs.getTabs().forEach(t->t.setClosable(false));
        stage.setScene(new Scene(tabs,1150,700)); stage.setTitle("Технічне обслуговування лабораторного обладнання"); stage.show();
        refreshAll();
    }

    private Pane equipmentPane() {
        equipment.getColumns().setAll(col("ID","id"),col("Інв. номер","inventoryNumber"),col("Назва","name"),col("Категорія","category"),col("Лабораторія","laboratory"),col("Критичність","criticalityLevel"),col("ID фахівця","responsibleSpecialistId"));
        TextField number=new TextField(),name=new TextField(),category=new TextField(),laboratory=new TextField(),level=new TextField(),specialistId=new TextField();
        TextField csvPath=new TextField(Path.of("sample-data","equipment.csv").toString());
        ProgressBar progress=new ProgressBar(0); Label status=new Label("CSV не запущено");
        Button add=new Button("Додати"),update=new Button("Змінити"),delete=new Button("Видалити"),refresh=new Button("Оновити"),importCsv=new Button("Імпортувати CSV");
        add.setOnAction(e->safe(()->{ api.addEquipment(readEquipment(null,number,name,category,laboratory,level,specialistId)); refreshEquipment(); }));
        update.setOnAction(e->safe(()->{ Equipment s=selected(equipment); api.updateEquipment(readEquipment(s.getId(),number,name,category,laboratory,level,specialistId)); refreshEquipment(); }));
        delete.setOnAction(e->safe(()->{ api.deleteEquipment(selected(equipment).getId()); refreshAll(); }));
        refresh.setOnAction(e->refreshEquipment());
        equipment.getSelectionModel().selectedItemProperty().addListener((o,a,s)->{ if(s!=null){number.setText(s.getInventoryNumber());name.setText(s.getName());category.setText(s.getCategory());laboratory.setText(s.getLaboratory());level.setText(""+s.getCriticalityLevel());specialistId.setText(""+s.getResponsibleSpecialistId());} });
        importCsv.setOnAction(e->safe(()->{
            ImportEquipmentTask task=new ImportEquipmentTask(Path.of(csvPath.getText().trim()),api);
            importCsv.setDisable(true);
            progress.progressProperty().bind(task.progressProperty()); status.textProperty().bind(task.messageProperty());
            task.setOnSucceeded(x->{ progress.progressProperty().unbind(); status.textProperty().unbind(); importCsv.setDisable(false); status.setText("Готово. Імпортовано: "+task.getValue()+", пропущено: "+task.getSkipped()); refreshEquipment(); });
            task.setOnFailed(x->{ progress.progressProperty().unbind(); status.textProperty().unbind(); importCsv.setDisable(false); status.setText("Помилка імпорту"); showError(task.getException()); });
            Thread thread=new Thread(task,"csv-import"); thread.setDaemon(true); thread.start();
        }));
        VBox form=new VBox(6,new Label("Інвентарний номер"),number,new Label("Назва"),name,new Label("Категорія"),category,new Label("Лабораторія"),laboratory,new Label("Рівень критичності 1..5"),level,new Label("ID відповідального фахівця"),specialistId,new HBox(8,add,update,delete,refresh),new Label("CSV-файл"),csvPath,importCsv,progress,status);
        return layout(equipment,form);
    }

    private Pane requestPane() {
        requests.getColumns().setAll(col("ID","id"),col("Обладнання","equipmentId"),col("Опис","description"),col("Тяжкість","faultSeverity"),col("Простій, год","expectedDowntimeHours"),col("Стан","state"),col("F1","f1"),col("F2","f2"),col("F3","f3"),col("I","priorityIndex"),col("Дата","createdDate"));
        TextField equipmentId=new TextField(),description=new TextField(),severity=new TextField(),downtime=new TextField();
        ComboBox<RequestState> state=new ComboBox<>(FXCollections.observableArrayList(RequestState.values()));
        Button add=new Button("Створити"),update=new Button("Перерахувати"),change=new Button("Змінити стан"),delete=new Button("Видалити"),refresh=new Button("Оновити");
        add.setOnAction(e->safe(()->{api.addRequest(readRequest(null,equipmentId,description,severity,downtime));refreshRequests();}));
        update.setOnAction(e->safe(()->{api.updateRequest(readRequest(selected(requests).getId(),equipmentId,description,severity,downtime));refreshRequests();}));
        change.setOnAction(e->safe(()->{
            if(state.getValue()==null) throw new IllegalArgumentException("Виберіть новий стан");
            api.changeRequestState(selected(requests).getId(),state.getValue());refreshRequests();
        }));
        delete.setOnAction(e->safe(()->{api.deleteRequest(selected(requests).getId());refreshAll();}));
        refresh.setOnAction(e->refreshRequests());
        requests.getSelectionModel().selectedItemProperty().addListener((o,a,r)->{if(r!=null){equipmentId.setText(""+r.getEquipmentId());description.setText(r.getDescription());severity.setText(""+r.getFaultSeverity());downtime.setText(""+r.getExpectedDowntimeHours());}});
        return layout(requests,new VBox(8,new Label("ID обладнання"),equipmentId,new Label("Опис несправності"),description,new Label("Тяжкість несправності 1..5"),severity,new Label("Очікуваний простій, год"),downtime,new Label("Новий стан"),state,new FlowPane(8,8,add,update,change,delete,refresh)));
    }

    private Pane specialistPane() {
        specialists.getColumns().setAll(col("ID","id"),col("ПІБ","fullName"),col("Спеціалізація","specialization"),col("Телефон","phone"));
        TextField fullName=new TextField(),specialization=new TextField(),phone=new TextField();
        Button add=new Button("Додати"),update=new Button("Змінити"),delete=new Button("Видалити"),refresh=new Button("Оновити");
        add.setOnAction(e->safe(()->{ api.addSpecialist(new Specialist(null,fullName.getText(),specialization.getText(),phone.getText())); refreshSpecialists(); }));
        update.setOnAction(e->safe(()->{ Specialist s=selected(specialists); s.setFullName(fullName.getText()); s.setSpecialization(specialization.getText()); s.setPhone(phone.getText()); api.updateSpecialist(s); refreshSpecialists(); }));
        delete.setOnAction(e->safe(()->{ api.deleteSpecialist(selected(specialists).getId()); refreshAll(); }));
        refresh.setOnAction(e->refreshSpecialists());
        specialists.getSelectionModel().selectedItemProperty().addListener((o,a,s)->{ if(s!=null){fullName.setText(s.getFullName());specialization.setText(s.getSpecialization());phone.setText(s.getPhone());} });
        return layout(specialists,new VBox(8,new Label("ПІБ"),fullName,new Label("Спеціалізація"),specialization,new Label("Телефон"),phone,new HBox(8,add,update,delete,refresh)));
    }

    private Pane recordPane() {
        records.getColumns().setAll(col("ID","id"),col("Заявка","requestId"),col("Фахівець","specialistId"),col("Виконані роботи","workDescription"),col("Дата","serviceDate"),col("Години","laborHours"),col("Вартість","cost"));
        TextField requestId=new TextField(),specialistId=new TextField(),work=new TextField(),date=new TextField(),hours=new TextField(),cost=new TextField();
        Button add=new Button("Додати"),update=new Button("Змінити"),delete=new Button("Видалити"),refresh=new Button("Оновити");
        add.setOnAction(e->safe(()->{api.addRecord(readRecord(null,requestId,specialistId,work,date,hours,cost));refreshRecords();}));
        update.setOnAction(e->safe(()->{api.updateRecord(readRecord(selected(records).getId(),requestId,specialistId,work,date,hours,cost));refreshRecords();}));
        delete.setOnAction(e->safe(()->{api.deleteRecord(selected(records).getId());refreshRecords();}));
        refresh.setOnAction(e->refreshRecords());
        records.getSelectionModel().selectedItemProperty().addListener((o,a,r)->{if(r!=null){requestId.setText(""+r.getRequestId());specialistId.setText(""+r.getSpecialistId());work.setText(r.getWorkDescription());date.setText(r.getServiceDate());hours.setText(""+r.getLaborHours());cost.setText(""+r.getCost());}});
        return layout(records,new VBox(8,new Label("ID заявки"),requestId,new Label("ID фахівця"),specialistId,new Label("Виконані роботи"),work,new Label("Дата YYYY-MM-DD"),date,new Label("Трудовитрати, год"),hours,new Label("Вартість, грн"),cost,new HBox(8,add,update,delete,refresh)));
    }

    private Equipment readEquipment(Long id,TextField number,TextField name,TextField category,TextField laboratory,TextField level,TextField specialistId) {
        return new Equipment(id,number.getText(),name.getText(),category.getText(),laboratory.getText(),intField(level,"Рівень критичності"),longField(specialistId,"ID відповідального фахівця"));
    }
    private ServiceRequest readRequest(Long id,TextField equipmentId,TextField description,TextField severity,TextField downtime) {
        return new ServiceRequest(id,longField(equipmentId,"ID обладнання"),description.getText(),intField(severity,"Тяжкість несправності"),doubleField(downtime,"Очікуваний простій"),null,0,0,0,0,null);
    }
    private ServiceRecord readRecord(Long id,TextField requestId,TextField specialistId,TextField work,TextField date,TextField hours,TextField cost) {
        return new ServiceRecord(id,longField(requestId,"ID заявки"),longField(specialistId,"ID фахівця"),work.getText(),date.getText().trim(),doubleField(hours,"Трудовитрати"),doubleField(cost,"Вартість"));
    }
    private long longField(TextField f,String label) {
        String v=f.getText()==null?"":f.getText().trim();
        if(v.isEmpty()) throw new IllegalArgumentException("Заповніть поле «"+label+"»");
        try { return Long.parseLong(v); } catch(NumberFormatException e) { throw new IllegalArgumentException("Поле «"+label+"» має бути цілим числом"); }
    }
    private int intField(TextField f,String label) {
        String v=f.getText()==null?"":f.getText().trim();
        if(v.isEmpty()) throw new IllegalArgumentException("Заповніть поле «"+label+"»");
        try { return Integer.parseInt(v); } catch(NumberFormatException e) { throw new IllegalArgumentException("Поле «"+label+"» має бути цілим числом"); }
    }
    private double doubleField(TextField f,String label) {
        String v=f.getText()==null?"":f.getText().trim();
        if(v.isEmpty()) throw new IllegalArgumentException("Заповніть поле «"+label+"»");
        try { return Double.parseDouble(v.replace(',','.')); } catch(NumberFormatException e) { throw new IllegalArgumentException("Поле «"+label+"» має бути числом"); }
    }
    private void refreshAll() {
        safe(()->{
            equipment.setItems(FXCollections.observableArrayList(api.getEquipment()));
            requests.setItems(FXCollections.observableArrayList(api.getRequests()));
            specialists.setItems(FXCollections.observableArrayList(api.getSpecialists()));
            records.setItems(FXCollections.observableArrayList(api.getRecords()));
        });
    }
    private void refreshEquipment() { safe(()->equipment.setItems(FXCollections.observableArrayList(api.getEquipment()))); }
    private void refreshRequests() { safe(()->requests.setItems(FXCollections.observableArrayList(api.getRequests()))); }
    private void refreshSpecialists() { safe(()->specialists.setItems(FXCollections.observableArrayList(api.getSpecialists()))); }
    private void refreshRecords() { safe(()->records.setItems(FXCollections.observableArrayList(api.getRecords()))); }
    private <T> T selected(TableView<T> table) { T v=table.getSelectionModel().getSelectedItem(); if(v==null) throw new IllegalArgumentException("Виберіть запис"); return v; }
    private void safe(Runnable action) { try{ action.run(); }catch(Exception e){ LOG.warning(e.getMessage()); showError(e); } }
    private void showError(Throwable e) { Alert d=new Alert(Alert.AlertType.ERROR,e==null?"Помилка":e.getMessage(),ButtonType.OK); d.showAndWait(); }
    private Pane layout(TableView<?> table,Pane form) { ScrollPane scroll=new ScrollPane(form); scroll.setFitToWidth(true); SplitPane p=new SplitPane(table,scroll); p.setDividerPositions(0.68); BorderPane root=new BorderPane(p); root.setPadding(new Insets(10)); return root; }
    private <S,T> TableColumn<S,T> col(String title,String property) { TableColumn<S,T> c=new TableColumn<>(title); c.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(property)); return c; }
    public static void main(String[] args) { launch(args); }
}
