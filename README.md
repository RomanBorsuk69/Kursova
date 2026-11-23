# Інформаційна система технічного обслуговування лабораторного обладнання

Курсова робота з дисципліни «Кросплатформене програмування», варіант №3.

## Технології

Java 21, Maven, JavaFX 21, Servlet API 4 (Apache Tomcat 9.x), Jackson, JDBC, MySQL, JUnit 5.

Маршрут даних: `JavaFX → HttpClient → Servlet → Service → DAO → JDBC → MySQL`.

## Структура

```text
lab-maintenance/
├─ database.sql
├─ server/
│  ├─ pom.xml
│  └─ src/main/java/server/   моделі, DBUtil, DAO, Service, Servlet
│  └─ src/main/webapp/WEB-INF/web.xml
│  └─ src/test/java/CourseworkTest.java
└─ client/
   ├─ sample-data/equipment.csv
   ├─ pom.xml
   └─ src/main/java/client/   моделі, HttpLabClient, ImportEquipmentTask, MainApp
```

## Предметна логіка

Життєвий цикл заявки: `NEW → DIAGNOSED → IN_SERVICE → COMPLETED`
(НОВА → ДІАГНОСТОВАНО → ОБСЛУГОВУВАННЯ → ВИКОНАНО).

Інтегральний показник: `I = 0,40·F1 + 0,35·F2 + 0,25·F3`.

- F1, критичність обладнання: `(рівень − 1) / 4 · 100`, рівень 1..5.
- F2, тяжкість несправності: `(рівень − 1) / 4 · 100`, рівень 1..5.
- F3, вплив простою: `години / 72 · 100`, не більше 100.

Приклад: критичність 4, тяжкість 3, простій 36 год → F1 = 75, F2 = 50, F3 = 50, I = 60.

## Запуск

1. Запустити MySQL і виконати `database.sql`. Параметри підключення задані в `DBUtil.java`.
2. Зібрати сервер:

   ```bash
   cd server
   mvn clean package
   ```

3. Скопіювати `server/target/lab-maintenance-server.war` до `webapps` Tomcat 9.x і виконати `startup.bat`.
   Перевірка: <http://localhost:8080/lab-maintenance-server/equipment>.
4. Запустити клієнт:

   ```bash
   chcp 65001
   [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
   $env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
   mvn javafx:run
   ```

## Тести

```bash
cd server

mvn clean  test 
```

Усі тести JUnit 5 розміщені у файлі `CourseworkTest.java`. MySQL, Tomcat і JavaFX для них не потрібні.

## HTTP-маршрути

| URL | GET | POST, параметр `action` |
|---|---|---|
| `/specialists` | список | `add`, `update`, `delete` |
| `/equipment` | список | `add`, `update`, `delete` |
| `/requests` | список | `add`, `update`, `delete`, `transition` |
| `/records` | список | `add`, `update`, `delete` |

## CSV-імпорт

Файл UTF-8 із роздільником `;`, типовий шлях `sample-data/equipment.csv`:

```text
inventory_number;name;category;laboratory;criticality_level;responsible_specialist_id
LAB-0101;Центрифуга ОПН-8;Лабораторне;Лабораторія біохімії;3;2
```

Імпорт запускається на вкладці «Обладнання» через JavaFX Task. Некоректні рядки, дублікати й записи з неіснуючим
фахівцем пропускаються, а причини записуються в журнал. Відсутній файл, неправильний заголовок або недоступний
сервер завершують імпорт помилкою. Для перевірки є `equipment_errors.csv` і `equipment_bad_header.csv`.
