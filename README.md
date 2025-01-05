# 🎓 Learning Management System (LMS)

## 📌 Overview
The **Learning Management System (LMS)** is a web-based backend application developed using Java Spring Boot and PostgreSQL (deployed on the cloud). It provides robust functionalities for managing online courses, assessments, and tracking performance for students and instructors. The LMS is designed to support various user roles, including Admin, Instructor, and Student, each with tailored access and features.

---

## 💫 Features

### 1. 👥 User Management
- **User Roles:**
  - **Admin**: Manage system settings, create users, and oversee courses.
  - **Instructor**: Create courses, manage content, add assessments, grade students, and manage enrollments.
  - **Student**: Enroll in courses, access content, complete assessments, and track grades.
- **Functionalities:**
  - Role-based user registration and login.
  - Profile management (view/update profile).

### 2. 📚 Course Management
- **Features:**
  - Course creation by instructors with metadata (title, description, duration).
  - Media upload (videos, PDFs, audio) handled via **Cloudinary** for cloud-based storage.
  - Student enrollment and enrollment tracking by instructors/admins.
  - Attendance management via OTP-based validation per lesson.

### 3. 📝 Assessment & Grading
- **Types of Assessments:**
  - Quizzes with various question types (MCQ, true/false, short answers).
  - Assignments with file submissions.
- **Functionalities:**
  - Question bank creation and management for quizzes.
  - Grading and feedback for quizzes and assignments.

### 4. 📊 Performance Tracking
- Monitor student progress (quiz scores, assignment submissions, attendance).
- Generate performance reports in Excel.
- Visualize data with charts.

### 5. 🔔 Notifications
- System and email notifications for:
  - Enrollment confirmations.
  - Graded assignments.
  - Course updates.
- Filter notifications (unread/all).

### 6. 🔐 Security & Access Control
- Implemented via Spring Security.
- Role-based access control to restrict permissions.

### 7. 🛠️ Design Patterns
- Applied **Factory Design Pattern** for creating objects without specifying exact classes.
- Applied **Observer Design Pattern** for implementing notifications and updates.

### 8. ✅ Testing
- **JUnit Testing** is implemented to ensure the reliability and functionality of the application.

---

## 🛠️ Technologies

- **Framework:** Spring Boot
- **Database:** PostgreSQL (cloud-hosted)
- **Authentication:** JWT-based authentication
- **File Storage:** Cloudinary for media uploads
- **APIs:** RESTful endpoints
- **Reports:** Apache POI(Excel reports) and JFreeChart(chart generation)

---

## 📦 Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd Learning-Management-System
   ```

2. Create a `.env` file in the root directory with the following configurations:
   ```env
   DB_URL=<your-database-url>
   DB_USERNAME=<your-database-username>
   DB_PASSWORD=<your-database-password>
   EMAIL_USERNAME=<your-email-username>
   EMAIL_PASSWORD=<your-email-password>
   ```

3. Configure the database:
   - Set up PostgreSQL with cloud hosting.
   - Ensure the `.env` variables are loaded into the application.

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

---

## 🌐 API Endpoints

### 👥 User Management
- **Sign Up (POST):** `/api/signup`
- **Login (POST):** `/api/login`
- **Change Role (PUT):** `/api/admin/role`

### 📚 Course Management
- **Create Course (POST):** `/api/courses/create`
- **Enroll in Course (POST):** `/api/courses/{courseId}/enroll`
- **View Courses (GET):** `/api/courses/view`
- **Remove Student (DELETE):** `/api/courses/{courseId}/remove/{studentId}`
- **Update Course (PUT):** `/api/courses/{courseId}/update`
- **Get Students in Course (GET):** `/api/courses/{courseId}/students`

### 📝 Assignments
- **Create Assignment (POST):** `/api/assignments/create/course/{courseId}`
- **Submit Assignment (POST):** `/api/assignments/submit/{assignmentId}`
- **Get Submissions (GET):** `/api/assignments/get/{assignmentId}/submissions`
- **Grade Assignment (POST):** `/api/assignments/grade/{assignmentId}`
- **Get Assignment Details (GET):** `/api/assignments/get/{assignmentId}`

### 📋 Quiz Management
- **Create Manual Quiz (POST):** `/api/quizzes/create/manual`
- **Create Quiz from Bank (POST):** `/api/quizzes/create/bank`
- **Submit Quiz (POST):** `/api/quizzes/submission/{quizId}`
- **Get Quiz Details (GET):** `/api/quizzes/get/{quizId}`
- **Get Quizzes in a Course (GET):** `/api/quizzes/get/course/{courseId}`
- **Get Quizzes for Student (GET):** `/api/quizzes/get/student/{studentId}`
- **Update Quiz (PUT):** `/api/quizzes/update/{quizId}`
- **Delete Quiz (DELETE):** `/api/quizzes/delete/{quizId}`

### 📖 Quiz Bank
- **Create Question Bank (POST):** `/api/Qbank/create`
- **Add Question to Bank (POST):** `/api/Qbank/{bankId}/addQuestion`
- **Get Questions from Bank (GET):** `/api/Qbank/{bankId}/getquestions`
- **Delete Question from Bank (DELETE):** `/api/Qbank/{bankId}/Question/{questionId}`

### 📊 Performance Tracking
- **Generate Excel Report (GET):** `/api/reports/course/{courseId}/excel`
- **Generate Statistics (GET):** `/api/reports/course/{courseId}/statistics`
- **Track Assignment Performance (GET):** `/api/performance/assignment/{assignmentId}`
- **Track Quiz Performance (GET):** `/api/performance/quiz/{quizId}`
- **Get Attendance for Lecture (GET):** `/api/performance/attendance/{lectureId}`

### 🔔 Notifications
- **View Notifications (GET):** `/api/notifications`

---

## 🔑 Authentication
- **JWT Bearer Token:** Required for all endpoints.
- Token included in `Authorization` header:
  ```bash
  Authorization: Bearer <token>
  ```

---

## 🤝 Contributing

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add your feature"
   ```
4. Push to the branch:
   ```bash
   git push origin feature/your-feature
   ```
5. Create a pull request.

---

## 👥 Contributors

- **Daniel Sameh**
- **Alaa Ashraf**
- **Daniel Raafat**
- **Youssef Ehab**
- **Michael Reda**
