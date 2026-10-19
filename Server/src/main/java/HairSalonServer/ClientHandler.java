package HairSalonServer;

import Database.*;
import POJO.*;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    protected Socket clientSocket = null;
    ObjectInputStream inpStr;
    ObjectOutputStream outStr;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            inpStr = new ObjectInputStream(clientSocket.getInputStream());
            outStr = new ObjectOutputStream(clientSocket.getOutputStream());
            while (true) {
                String choice = inpStr.readObject().toString();
                System.out.println(choice);
                System.out.println("Команда получена");
                switch (choice) {
                    case "registrationClient" -> {
                        System.out.println("Запрос к БД на регистрацию, клиент: " + clientSocket.getInetAddress().toString());
                        Users users = (Users) inpStr.readObject();
                        System.out.println(users.toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            boolean isUserExists = sqlFactory.getUsers().isUserExists(users.getLogin());

                            if (isUserExists) {
                                outStr.writeObject("This user already exists");
                            } else {
                                boolean isRegistered = sqlFactory.getUsers().insert(users);

                                if (isRegistered) {
                                    outStr.writeObject("OK");
                                } else {
                                    outStr.writeObject("Error during registration");
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Class not found error");
                        }
                        break;
                    }
                    case "authorization" -> {
                        System.out.println("Выполняется авторизация пользователя....");
                        Auth auth = (Auth) inpStr.readObject();
                        System.out.println(auth.toString());

                        try {
                            FactoryDB sqlFactory1 = new FactoryDB();
                            AuthDB authDB = sqlFactory1.getRole();

                            Users user = authDB.getRole(auth);

                            if (user != null) {
                                outStr.writeObject("OK");
                                outStr.writeObject(user);
                            } else {
                                outStr.writeObject("There is no data!");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Error occurred during authorization.");
                        }
                        break;
                    }
                    case "addService" -> {
                        System.out.println("Запрос на добавление услуги от клиента: " + clientSocket.getInetAddress().toString());
                        Services service = (Services) inpStr.readObject();
                        System.out.println(service.toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntServiceDB serviceDB = sqlFactory.getServiceDB();
                            boolean isAdded = serviceDB.addService(service);

                            if (isAdded) {
                                outStr.writeObject("Service added successfully");
                            } else {
                                outStr.writeObject("Error while adding service");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while adding service");
                        }
                        break;
                    }
                    case "viewServices" -> {
                        System.out.println("Запрос на просмотр услуг от клиента: " + clientSocket.getInetAddress().toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntServiceDB serviceDB = sqlFactory.getServiceDB();
                            List<Services> servicesList = serviceDB.getAllServices();

                            outStr.writeObject(servicesList);
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Error occurred while retrieving services");
                        }
                        break;
                    }
                    case "viewEmployees" -> {
                        System.out.println("Запрос на просмотр мастеров от клиента: " + clientSocket.getInetAddress().toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntEmployeeDB employeeDB = sqlFactory.getEmployeeDB();
                            List<Employee> employeesList = employeeDB.getAllEmployees();

                            outStr.writeObject(employeesList);
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Error occurred while retrieving services");
                        }
                        break;
                    }
                    case "deleteService" -> {
                        System.out.println("Запрос на удаление услуги от клиента: " + clientSocket.getInetAddress().toString());
                        int serviceId = (int) inpStr.readObject();
                        System.out.println("Удаление услуги с ID: " + serviceId);

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntServiceDB serviceDB = sqlFactory.getServiceDB();
                            boolean isDeleted = serviceDB.deleteService(serviceId);

                            if (isDeleted) {
                                outStr.writeObject("Service deleted successfully");
                            } else {
                                outStr.writeObject("Error while deleting service");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while deleting service");
                        }
                        break;
                    }
                    case "updateService" -> {
                        System.out.println("Запрос на изменение услуги от клиента: " + clientSocket.getInetAddress().toString());
                        Services service = (Services) inpStr.readObject();
                        System.out.println(service.toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntServiceDB serviceDB = sqlFactory.getServiceDB();
                            boolean isUpdated = serviceDB.updateService(service);

                            if (isUpdated) {
                                outStr.writeObject("Service updated successfully");
                            } else {
                                outStr.writeObject("Error while updating service");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while updating service");
                        }
                        break;
                    }
                    case "addClientInfo" -> {
                        System.out.println("Запрос на добавление информации о клиенте от: " + clientSocket.getInetAddress().toString());

                        Clients client = (Clients) inpStr.readObject();
                        System.out.println(client.toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            boolean isClientExists = sqlFactory.getClientsDB().isClientInfoExists(client.getUserId());

                            if (isClientExists) {
                                outStr.writeObject("Client information already exists");
                            } else {
                                boolean isAdded = sqlFactory.getClientsDB().insert(client);

                                if (isAdded) {
                                    outStr.writeObject("Client information added successfully");
                                } else {
                                    outStr.writeObject("Error during adding client information");
                                }
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while adding client information");
                        }
                        break;
                    }
                    case "checkClientExists" -> {
                        System.out.println("Запрос на проверку существования клиента от: " + clientSocket.getInetAddress().toString());

                        try {
                            int clientId = (int) inpStr.readObject();
                            System.out.println("Проверка клиента с ID: " + clientId);

                            FactoryDB sqlFactory = new FactoryDB();

                            boolean isClientExists = sqlFactory.getClientsDB().isClientInfoExists(clientId);

                            if (isClientExists) {
                                outStr.writeObject("Client exists");
                            } else {
                                outStr.writeObject("Client does not exist");
                            }
                        } catch (SQLException | ClassNotFoundException | IOException e) {
                            e.printStackTrace();
                            try {
                                outStr.writeObject("Error during client existence check");
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        break;
                    }

                    case "viewClientInfo" -> {
                        System.out.println("Запрос на просмотр информации о клиенте от: " + clientSocket.getInetAddress().toString());
                        int userId = (int) inpStr.readObject();

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            Clients client = sqlFactory.getClientsDB().getClientByUserId(userId);

                            if (client != null) {
                                outStr.writeObject(client);
                            } else {
                                outStr.writeObject(null);
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject(null);
                        }
                        break;
                    }
                    case "viewEmployeeInfo" -> {
                        System.out.println("Запрос на просмотр информации о клиенте от: " + clientSocket.getInetAddress().toString());
                        int userId = (int) inpStr.readObject();

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            Employee employee = sqlFactory.getEmployeeDB().getEmployeeByUserId(userId);

                            if (employee != null) {
                                outStr.writeObject(employee);
                            } else {
                                outStr.writeObject(null);
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject(null);
                        }
                        break;
                    }
                    case "updateClientInfo" -> {
                        System.out.println("Запрос на обновление информации от клиента: " + clientSocket.getInetAddress().toString());

                        String newLogin = (String) inpStr.readObject();
                        Clients client = (Clients) inpStr.readObject();

                        System.out.println("Получены данные для обновления: Логин = " + newLogin + ", " + client);

                        FactoryDB sqlFactory = new FactoryDB();

                        try {
                            boolean isLoginUpdated = sqlFactory.getClientsDB().updateLogin(client.getUserId(), newLogin);
                            if (isLoginUpdated) {
                                outStr.writeObject("Логин успешно обновлен.");
                            } else {
                                outStr.writeObject("Ошибка при обновлении логина.");
                            }

                            boolean isInfoUpdated = sqlFactory.getClientsDB().updateClientInfo(
                                    client.getUserId(),
                                    client.getLastName(),
                                    client.getFirstName(),
                                    client.getMiddleName(),
                                    client.getPhoneNumber()
                            );

                            if (isInfoUpdated) {
                                outStr.writeObject("Информация о клиенте успешно обновлена.");
                            } else {
                                outStr.writeObject("Ошибка при обновлении информации о клиенте.");
                            }

                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Ошибка базы данных при обновлении информации.");
                        }

                        break;
                    }
                    case "updaEmplInfo" -> {
                        System.out.println("Запрос на обновление информации от клиента: " + clientSocket.getInetAddress().toString());

                        String newLogin = (String) inpStr.readObject();
                        Employee employee = (Employee) inpStr.readObject();

                        System.out.println("Получены данные для обновления: Логин = " + newLogin + ", " + employee);

                        FactoryDB sqlFactory = new FactoryDB();

                        try {
                            boolean isLoginUpdated = sqlFactory.getClientsDB().updateLogin(employee.getUserId(), newLogin);
                            if (isLoginUpdated) {
                                outStr.writeObject("Логин успешно обновлен.");
                            } else {
                                outStr.writeObject("Ошибка при обновлении логина.");
                            }

                            boolean isInfoUpdated = sqlFactory.getEmployeeDB().updateEmployeeInfo(
                                    employee.getUserId(),
                                    employee.getLastName(),
                                    employee.getFirstName(),
                                    employee.getExperienceYears(),
                                    employee.getSpecialization()
                            );

                            if (isInfoUpdated) {
                                outStr.writeObject("Информация о клиенте успешно обновлена.");
                            } else {
                                outStr.writeObject("Ошибка при обновлении информации о клиенте.");
                            }

                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Ошибка базы данных при обновлении информации.");
                        }

                        break;
                    }

                    case "addReview" -> {
                        System.out.println("Запрос на добавление отзыва от клиента: " + clientSocket.getInetAddress().toString());

                        Review review = (Review) inpStr.readObject();
                        System.out.println(review.toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntReviewDB reviewDB = sqlFactory.getReviewDB();

                            boolean isAdded = reviewDB.addReview(review);

                            if (isAdded) {
                                outStr.writeObject("Review added successfully");
                            } else {
                                outStr.writeObject("Error while adding review");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while adding review");
                        }
                        break;
                    }
                    case "viewReviews" -> {
                        System.out.println("Запрос на получение всех отзывов от клиента: " + clientSocket.getInetAddress().toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntReviewDB reviewDB = sqlFactory.getReviewDB();

                            List<Review> reviews = reviewDB.getAllReviews();

                            outStr.writeObject(reviews);
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Error occurred while retrieving reviews");
                        }
                        break;
                    }
                    case "deleteReview" -> {
                        System.out.println("Запрос на удаление отзыва от клиента: " + clientSocket.getInetAddress().toString());
                        int reviewId = (int) inpStr.readObject();
                        System.out.println("Удаление отзыва с ID: " + reviewId);

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntReviewDB reviewDB = sqlFactory.getReviewDB();
                            boolean isDeleted = reviewDB.deleteReview(reviewId);

                            if (isDeleted) {
                                outStr.writeObject("Review deleted successfully");
                            } else {
                                outStr.writeObject("Error while deleting review");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while deleting review");
                        }
                        break;
                    }
                    case "deleteUser" -> {
                        System.out.println("Запрос на удаление пользователя от клиента: " + clientSocket.getInetAddress().toString());
                        String login = (String) inpStr.readObject();
                        System.out.println("Удаление пользователя с логином: " + login);

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            UsersDB usersDB = sqlFactory.getUsers();
                            boolean isDeleted = usersDB.deleteUserByLogin(login);
                            if (isDeleted) {
                                outStr.writeObject("User deleted successfully");
                            } else {
                                outStr.writeObject("Error while deleting user");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while deleting user");
                        }
                        break;
                    }
                    case "updateUserRole" -> {
                        System.out.println("Запрос на изменение роли пользователя от клиента: " + clientSocket.getInetAddress().toString());
                        String login = (String) inpStr.readObject();
                        String newRole = (String) inpStr.readObject();
                        System.out.println("Изменение роли пользователя с логином: " + login + " на " + newRole);

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            UsersDB usersDB = sqlFactory.getUsers();
                            boolean isUpdated = usersDB.updateUserRoleByLogin(login, newRole);

                            if (isUpdated) {
                                outStr.writeObject("User role updated successfully");
                            } else {
                                outStr.writeObject("Error while updating user role");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while updating user role");
                        }
                        break;
                    }
                    case "viewAllUsers" -> {
                        System.out.println("Запрос на получение всех пользователей от клиента: " + clientSocket.getInetAddress().toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            UsersDB usersDB = sqlFactory.getUsers();
                            List<Users> usersList = usersDB.getAllUsers();

                            outStr.writeObject(usersList);
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Error occurred while retrieving all users");
                        }
                        break;
                    }
                    case "addMaster" -> {
                        System.out.println("Запрос на добавление мастера от клиента: " + clientSocket.getInetAddress().toString());
                        AddMasterRequest request = (AddMasterRequest) inpStr.readObject();
                        System.out.println(request.toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            boolean isMasterExists = sqlFactory.getEmployeeDB().isMasterExists(request.getLogin());

                            if (isMasterExists) {
                                outStr.writeObject("Master already exists");
                            } else {
                                int userId = sqlFactory.getEmployeeDB().addUser(request.getLogin(), request.getPassword());

                                if (userId != -1) {
                                    boolean isAdded = sqlFactory.getEmployeeDB().addMaster(userId, request);

                                    if (isAdded) {
                                        outStr.writeObject("Master added successfully");
                                    } else {
                                        outStr.writeObject("Error during master addition");
                                    }
                                } else {
                                    outStr.writeObject("Error during user creation");
                                }
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while adding master");
                        }
                        break;
                    }
                    case "assignServicesToMaster" -> {
                        System.out.println("Запрос на привязку услуг к мастеру от клиента: " + clientSocket.getInetAddress().toString());
                        AssignServicesRequest request = (AssignServicesRequest) inpStr.readObject();
                        System.out.println(request.toString());

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            boolean isAssigned = sqlFactory.getEmployeeDB().assignServicesToMaster(request.getEmployeeId(), request.getServiceIds());

                            if (isAssigned) {
                                outStr.writeObject("Services assigned successfully");
                            } else {
                                outStr.writeObject("Error during service assignment");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Database error occurred while assigning services");
                        }
                        break;
                    }
                    case "createSchedule" -> {
                        System.out.println("Запрос на создание расписания от клиента: " + clientSocket.getInetAddress().toString());
                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            CreateScheduleRequest request = (CreateScheduleRequest) inpStr.readObject();

                            boolean isScheduled = sqlFactory.getScheduleDB().addSchedule(request);

                            if (isScheduled) {
                                outStr.writeObject("Расписание успешно создано");
                            } else {
                                outStr.writeObject("Ошибка при создании расписания");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            outStr.writeObject("Ошибка при создании расписания");
                        }
                        break;
                    }
                    case "getAvailableTimes" -> {
                        System.out.println("Запрос на доступные слоты от клиента: " + clientSocket.getInetAddress().toString());

                        int employeeId = (Integer) inpStr.readObject();
                        int serviceId = (Integer) inpStr.readObject();
                        String date = (String) inpStr.readObject();

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntAppointmentDB scheduleSlotDB = sqlFactory.getAppointmentDB();
                            List<String> availableTimes = scheduleSlotDB.getAvailableTimes(employeeId, date);

                            outStr.writeObject(availableTimes);
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Error occurred while retrieving available times");
                        }
                        break;
                    }
                    case "getClientIdByUserId" -> {
                        System.out.println("Запрос на получение client_id от клиента: " + clientSocket.getInetAddress().toString());

                        int usersId = (Integer) inpStr.readObject();

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntClientsDB clientDB = sqlFactory.getClientsDB();
                            int clientId = clientDB.getClientIdByUserId(usersId);

                            outStr.writeObject(clientId);
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Error occurred while retrieving client_id");
                        }
                        break;
                    }
                    case "createAppointment" -> {
                        System.out.println("Запрос на создание записи от клиента: " + clientSocket.getInetAddress().toString());

                        AppointmentRequest appointmentRequest = (AppointmentRequest) inpStr.readObject();
                        System.out.println(appointmentRequest.toString());
                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntAppointmentDB appointmentDB = sqlFactory.getAppointmentDB();
                            boolean success = appointmentDB.createAppointment(appointmentRequest);

                            if (success) {
                                outStr.writeObject("Appointment created successfully");
                            } else {
                                outStr.writeObject("Failed to create appointment");
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            outStr.writeObject("Error occurred while creating appointment");
                        }
                        break;
                    }
                    case "getServicesByEmployeeId" -> {
                        System.out.println("Получение услуг для мастера");

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntServiceDB serviceDB = sqlFactory.getServiceDB();
                            int employeeId = (int) inpStr.readObject();
                            List<Services> servicesList = serviceDB.getServicesByEmployeeId(employeeId);
                            outStr.writeObject(servicesList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            outStr.writeObject(new ArrayList<>());
                        }
                        break;
                    }
                    case "viewAppointments" -> {
                        System.out.println("Просмотр всех записей на услуги");

                        FactoryDB sqlFactory = new FactoryDB();
                        try {
                            IntAppointmentDB appointmentDB = sqlFactory.getAppointmentDB();
                            List<AppointmentRecord> appointmentRecords = new ArrayList<>();

                            List<Integer> appointmentIds = appointmentDB.getAllAppointmentIds();
                            for (Integer appointmentId : appointmentIds) {
                                AppointmentRecord appointmentRecord = appointmentDB.getAppointmentRecordById(appointmentId);

                                Clients client = appointmentDB.getClientById(appointmentRecord.getClientId());

                                Employee employee = appointmentDB.getEmployeeById(appointmentRecord.getEmployeeId());

                                Services service = appointmentDB.getServiceById(appointmentRecord.getServiseId());

                                String appointmentDate = appointmentRecord.getAppointmentDate();
                                String appointmentTime = appointmentRecord.getAppointmentTime();

                                AppointmentRecord record = new AppointmentRecord(
                                        appointmentRecord.getAppointmentId(),
                                        client.getClientId(),
                                        client.getLastName(),
                                        client.getFirstName(),
                                        client.getPhoneNumber(),
                                        employee.getEmployeeId(),
                                        employee.getLastName(),
                                        employee.getFirstName(),
                                        service.getServiseId(),
                                        service.getName(),
                                        service.getPrice(),
                                        appointmentDate,
                                        appointmentTime
                                );
                                appointmentRecords.add(record);
                            }
                            outStr.writeObject(appointmentRecords);
                        } catch (Exception e) {
                            e.printStackTrace();
                            outStr.writeObject(new ArrayList<>());
                        }
                        break;
                    }
                    case "viewAppointmentsForClient" -> {
                        System.out.println("Просмотр записей на услуги для конкретного клиента");

                        try {
                            String userIdStr = (String) inpStr.readObject();
                            int userId = Integer.parseInt(userIdStr);
                            System.out.println("Получен user_id: " + userId);

                            FactoryDB sqlFactory = new FactoryDB();
                            IntAppointmentDB appointmentDB = sqlFactory.getAppointmentDB();

                            Clients client = appointmentDB.getClientByUserId(userId);
                            if (client != null) {
                                System.out.println("Клиент найден, ID клиента: " + client.getClientId());

                                List<AppointmentRecord> appointmentRecords = new ArrayList<>();
                                List<Integer> appointmentIds = appointmentDB.getAppointmentIdsByClientId(client.getClientId());
                                System.out.println("Найдено записей для клиента: " + appointmentIds.size());

                                for (Integer appointmentId : appointmentIds) {
                                    System.out.println("Обрабатываем запись с ID: " + appointmentId);

                                    AppointmentRecord appointmentRecord = appointmentDB.getAppointmentRecordById(appointmentId);

                                    AppointmentRecord record = new AppointmentRecord(
                                            appointmentRecord.getAppointmentId(),
                                            appointmentRecord.getClientId(),
                                            appointmentRecord.getClientLastName(),
                                            appointmentRecord.getClientFirstName(),
                                            appointmentRecord.getClientPhoneNumber(),
                                            appointmentRecord.getEmployeeId(),
                                            appointmentRecord.getEmployeeLastName(),
                                            appointmentRecord.getEmployeeFirstName(),
                                            appointmentRecord.getServiseId(),
                                            appointmentRecord.getServiceName(),
                                            appointmentRecord.getServicePrice(),
                                            appointmentRecord.getAppointmentDate(),
                                            appointmentRecord.getAppointmentTime()
                                    );

                                    appointmentRecords.add(record);
                                }
                                outStr.writeObject(appointmentRecords);
                                System.out.println("Отправлены все записи для клиента.");
                            } else {
                                System.out.println("Клиент с ID " + userId + " не найден.");
                                outStr.writeObject(new ArrayList<>());
                            }
                        } catch (Exception e) {
                            System.out.println("Ошибка при обработке запроса на просмотр записей: " + e.getMessage());
                            e.printStackTrace();
                            outStr.writeObject(new ArrayList<>());
                        }
                        break;
                    }
                    case "viewAppointmentsForEmployee" -> {
                        System.out.println("Просмотр записей на услуги для конкретного мастера");
                        try {
                            String userIdStr = (String) inpStr.readObject();
                            int userId = Integer.parseInt(userIdStr);
                            System.out.println("Получен user_id: " + userId);

                            FactoryDB sqlFactory = new FactoryDB();
                            IntAppointmentDB appointmentDB = sqlFactory.getAppointmentDB();

                            Employee employee = appointmentDB.getEmployeeByUserId(userId);

                            if (employee != null) {
                                System.out.println("Клиент найден, ID клиента: " + employee.getEmployeeId());

                                List<AppointmentRecord> appointmentRecords = new ArrayList<>();
                                List<Integer> appointmentIds = appointmentDB.getAppointmentIdsByEmployeeId(employee.getEmployeeId());  // Получаем все записи для конкретного клиента
                                System.out.println("Найдено записей для клиента: " + appointmentIds.size());

                                for (Integer appointmentId : appointmentIds) {
                                    System.out.println("Обрабатываем запись с ID: " + appointmentId);

                                    AppointmentRecord appointmentRecord = appointmentDB.getAppointmentRecordById(appointmentId);  // Получаем подробности записи

                                    AppointmentRecord record = new AppointmentRecord(
                                            appointmentRecord.getAppointmentId(),
                                            appointmentRecord.getClientId(),
                                            appointmentRecord.getClientLastName(),
                                            appointmentRecord.getClientFirstName(),
                                            appointmentRecord.getClientPhoneNumber(),
                                            appointmentRecord.getEmployeeId(),
                                            appointmentRecord.getEmployeeLastName(),
                                            appointmentRecord.getEmployeeFirstName(),
                                            appointmentRecord.getServiseId(),
                                            appointmentRecord.getServiceName(),
                                            appointmentRecord.getServicePrice(),
                                            appointmentRecord.getAppointmentDate(),
                                            appointmentRecord.getAppointmentTime()
                                    );

                                    appointmentRecords.add(record);
                                }
                                outStr.writeObject(appointmentRecords);
                                System.out.println("Отправлены все записи для клиента.");
                            } else {
                                System.out.println("Клиент с ID " + userId + " не найден.");
                                outStr.writeObject(new ArrayList<>());
                            }
                        } catch (Exception e) {
                            System.out.println("Ошибка при обработке запроса на просмотр записей: " + e.getMessage());
                            e.printStackTrace();
                            outStr.writeObject(new ArrayList<>());
                        }
                        break;
                    }
                    case "deleteAppointment" -> {
                        System.out.println("Запрос на удаление записи от клиента: " + clientSocket.getInetAddress().toString());
                        try {
                            String appointmentIdStr = (String) inpStr.readObject();
                            int appointmentId = Integer.parseInt(appointmentIdStr);
                            System.out.println("Попытка удалить запись с ID: " + appointmentId);
                            FactoryDB sqlFactory = new FactoryDB();
                            try {
                                IntAppointmentDB appointmentDB = sqlFactory.getAppointmentDB();
                                boolean success = appointmentDB.deleteAppointmentById(appointmentId);
                                if (success) {
                                    System.out.println("Запись с ID " + appointmentId + " успешно удалена.");
                                    outStr.writeObject("Appointment deleted successfully");
                                } else {
                                    System.out.println("Не удалось удалить запись с ID " + appointmentId + ". Запись не найдена.");
                                    outStr.writeObject("Failed to delete appointment");
                                }
                            } catch (SQLException | ClassNotFoundException e) {
                                System.err.println("Ошибка при удалении записи с ID " + appointmentId + ": " + e.getMessage());
                                e.printStackTrace();
                                outStr.writeObject("Error occurred while deleting appointment");
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Ошибка при чтении ID записи на удаление: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    }
                    default -> {
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected.");
        }
    }
}