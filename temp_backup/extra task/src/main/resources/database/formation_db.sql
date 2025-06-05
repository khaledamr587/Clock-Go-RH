-- Create the database
CREATE DATABASE IF NOT EXISTS formation_db;
USE formation_db;

-- Create formations table
CREATE TABLE IF NOT EXISTS formations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    duration INT NOT NULL COMMENT 'Duration in hours',
    cost DECIMAL(10,2) NOT NULL,
    level ENUM('Beginner', 'Intermediate', 'Advanced') NOT NULL,
    max_participants INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('Planned', 'Ongoing', 'Completed', 'Cancelled') NOT NULL DEFAULT 'Planned',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create participants table
CREATE TABLE IF NOT EXISTS participants (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    company VARCHAR(100),
    position VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create formation_participants (junction table for managing enrollments)
CREATE TABLE IF NOT EXISTS formation_participants (
    formation_id INT,
    participant_id INT,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Enrolled', 'Completed', 'Cancelled', 'In Progress') DEFAULT 'Enrolled',
    completion_date DATE,
    attendance_percentage DECIMAL(5,2) DEFAULT 0.00,
    PRIMARY KEY (formation_id, participant_id),
    FOREIGN KEY (formation_id) REFERENCES formations(id) ON DELETE CASCADE,
    FOREIGN KEY (participant_id) REFERENCES participants(id) ON DELETE CASCADE
);

-- Create trainers table
CREATE TABLE IF NOT EXISTS trainers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    specialization VARCHAR(100),
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create formation_trainers (junction table for formations and trainers)
CREATE TABLE IF NOT EXISTS formation_trainers (
    formation_id INT,
    trainer_id INT,
    role ENUM('Main Trainer', 'Assistant Trainer', 'Guest Speaker') NOT NULL,
    PRIMARY KEY (formation_id, trainer_id),
    FOREIGN KEY (formation_id) REFERENCES formations(id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE
);

-- Create sessions table (for tracking individual training sessions)
CREATE TABLE IF NOT EXISTS sessions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    formation_id INT,
    trainer_id INT,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    topic VARCHAR(200),
    description TEXT,
    room_location VARCHAR(100),
    FOREIGN KEY (formation_id) REFERENCES formations(id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainers(id)
);

-- Create attendance table
CREATE TABLE IF NOT EXISTS attendance (
    session_id INT,
    participant_id INT,
    status ENUM('Present', 'Absent', 'Late', 'Excused') NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    notes TEXT,
    PRIMARY KEY (session_id, participant_id),
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (participant_id) REFERENCES participants(id) ON DELETE CASCADE
);

-- Create evaluations table
CREATE TABLE IF NOT EXISTS evaluations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    formation_id INT,
    participant_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    feedback TEXT,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (formation_id) REFERENCES formations(id) ON DELETE CASCADE,
    FOREIGN KEY (participant_id) REFERENCES participants(id) ON DELETE CASCADE
);

-- Add indexes for better performance
CREATE INDEX idx_formations_status ON formations(status);
CREATE INDEX idx_formations_dates ON formations(start_date, end_date);
CREATE INDEX idx_participants_email ON participants(email);
CREATE INDEX idx_formation_participants_status ON formation_participants(status);
CREATE INDEX idx_sessions_date ON sessions(session_date);
CREATE INDEX idx_attendance_status ON attendance(status); 