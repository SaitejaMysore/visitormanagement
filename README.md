# Smart Visitor Management System

A comprehensive visitor management system built with Spring Boot 3.x, PostgreSQL, and Thymeleaf.

## Features

### Core Functionality
- **User Registration & Authentication**: Secure user registration with email validation and BCrypt password hashing
- **Role-Based Access Control**: Separate dashboards for regular users and administrators
- **Visitor Management**: Full CRUD operations for visitor appointments
- **Real-time Notifications**: Notification system for user-admin communication
- **Advanced Filtering**: Filter appointments by date range, purpose, and user
- **Report Generation**: Export visitor data as CSV or PDF reports

### User Features
- Personal dashboard with appointment management
- Calendar-based appointment scheduling
- Real-time notification panel with unread counters
- Search and filter personal appointments
- Responsive design for mobile and desktop

### Admin Features
- Comprehensive admin dashboard with system statistics
- View and manage all users and appointments
- Send notifications to users about their appointments
- Advanced filtering and search capabilities
- Export functionality (CSV/PDF reports)
- Real-time statistics and monitoring

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Database**: PostgreSQL (hosted on Render)
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Security**: BCrypt password encoding, CSRF protection
- **Reports**: OpenCSV, iText PDF
- **Deployment**: Docker-ready with Dockerfile

## Database Schema

### Users Table
- `id`: Primary key (auto-increment)
- `full_name`: User's full name
- `email`: Unique email address
- `password`: BCrypt hashed password
- `role`: USER or ADMIN
- `created_at`, `updated_at`: Timestamps

### Visitors Table
- `id`: Primary key (auto-increment)
- `name`: Visitor's name
- `phone_number`: Contact number
- `purpose`: Visit purpose
- `appointment_date`: Scheduled date
- `created_by`: Foreign key to users table
- `created_at`: Timestamp

### Notifications Table
- `id`: Primary key (auto-increment)
- `user_id`: Foreign key to users
- `visitor_id`: Foreign key to visitors
- `message`: Notification content
- `read`: Read status (boolean)
- `created_at`: Timestamp

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL database

### Database Configuration
The application is pre-configured to connect to the provided PostgreSQL database:
- **URL**: `postgresql://smart_visitor_db_user:7ZOujgDl8KVZFpQG7XiSIjjwM5vTHjaW@dpg-d2e1lfje5dus73fe5kkg-a.oregon-postgres.render.com/smart_visitor_db`
- **Username**: `smart_visitor_db_user`
- **Password**: `7ZOujgDl8KVZFpQG7XiSIjjwM5vTHjaW`

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd smart-visitor-system
   ```

2. **Run with Maven**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the application**
   - Open your browser and navigate to `http://localhost:8080`
   - Register a new account or login with existing credentials

### Docker Deployment

1. **Build Docker image**
   ```bash
   docker build -t smart-visitor-system .
   ```

2. **Run Docker container**
   ```bash
   docker run -p 8080:8080 smart-visitor-system
   ```

## User Guide

### Getting Started
1. Visit the homepage at `http://localhost:8080`
2. Click "Register" to create a new account
3. Fill in your details and submit the form
4. Login with your credentials
5. You'll be redirected to your dashboard based on your role

### User Dashboard
- **Add Appointments**: Click "Add New Appointment" to schedule visitor meetings
- **Manage Appointments**: Edit or delete existing appointments
- **Filter Appointments**: Use the filter options to search by purpose or date range
- **View Notifications**: Click the notification bell to see messages from administrators

### Admin Dashboard
- **System Overview**: View statistics about users, appointments, and daily activities
- **User Management**: Monitor all users and their appointments
- **Send Notifications**: Notify users about their appointments or system updates
- **Generate Reports**: Export data as CSV or PDF for analysis
- **Advanced Filtering**: Filter data by user, date range, or other criteria

## API Endpoints

### Authentication
- `GET /` - Homepage
- `GET /register` - Registration form
- `POST /register` - Process registration
- `GET /login` - Login form
- `POST /login` - Process login
- `GET /logout` - Logout

### User Operations
- `GET /user/dashboard` - User dashboard
- `POST /user/visitors` - Add new visitor
- `GET /user/visitors/{id}/edit` - Get visitor data for editing
- `POST /user/visitors/{id}/update` - Update visitor
- `POST /user/visitors/{id}/delete` - Delete visitor
- `POST /user/notifications/{id}/read` - Mark notification as read
- `POST /user/notifications/mark-all-read` - Mark all notifications as read

### Admin Operations
- `GET /admin/dashboard` - Admin dashboard
- `POST /admin/visitors/{id}/notify` - Send notification to user
- `GET /admin/reports/csv` - Export CSV report
- `GET /admin/reports/pdf` - Export PDF report

## Security Features

- **Password Encryption**: All passwords are hashed using BCrypt
- **Role-Based Access**: Users can only access their own data, admins can access all data
- **CSRF Protection**: Forms are protected against cross-site request forgery
- **Input Validation**: Server-side validation for all user inputs
- **SQL Injection Prevention**: JPA/Hibernate prevents SQL injection attacks

## Future Enhancements

- Email notifications for appointments
- SMS integration for visitor alerts
- Recurring appointment scheduling
- Visitor check-in/check-out system
- Photo capture for visitor identification
- Integration with access control systems
- Mobile application
- Advanced analytics and reporting
- Multi-tenant support

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support or questions, please contact the development team or create an issue in the repository.