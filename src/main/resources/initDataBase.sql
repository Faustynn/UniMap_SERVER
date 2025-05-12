CREATE OR REPLACE FUNCTION get_default_avatar()
    RETURNS BYTEA AS $$
BEGIN
    RETURN pg_read_binary_file('/Users/faustyn/Developer/UniMap/Uni/src/main/resources/org.main.unimapapi/1.png');
END;
$$ LANGUAGE plpgsql;
-- Create user_data table
CREATE TABLE public.user_data (
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    is_premium BOOLEAN NOT NULL DEFAULT FALSE,
    avatar BYTEA DEFAULT get_default_avatar(),
    avatar_file_name VARCHAR(255)  DEFAULT '1.png'
);

-- Create comments_subjects table
CREATE TABLE public.comments_subjects (
    comment_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    rating VARCHAR(10) NOT NULL,
    levelaccess INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_data(id)
);

-- Create comments_teachers table
CREATE TABLE public.comments_teachers (
    comment_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    teacher_id VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    rating VARCHAR(10) NOT NULL,
    levelaccess INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_data(id)
);

-- Create subjects table
CREATE TABLE public.subjects (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    credits INTEGER NOT NULL,
    study_type VARCHAR(50) NOT NULL,
    semester VARCHAR(50) NOT NULL,
    languages TEXT NOT NULL,
    completion_type VARCHAR(50) NOT NULL,
    student_count INTEGER NOT NULL,
    assesment_methods TEXT,
    learning_outcomes TEXT,
    course_contents TEXT,
    planned_activities TEXT,
    evaluation_methods TEXT
);

-- Create subject_evaluation table
CREATE TABLE public.subject_evaluation (
    id SERIAL PRIMARY KEY,
    subject_code VARCHAR(20) NOT NULL,
    grade VARCHAR(2) NOT NULL,
    percent VARCHAR(10) NOT NULL,
    FOREIGN KEY (subject_code) REFERENCES subjects(code)
);

-- Create teachers table
CREATE TABLE public.teachers (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(65),
    office VARCHAR(50)
);

-- Create teacher_subject_roles table
CREATE TABLE public.teacher_subject_roles (
    id SERIAL PRIMARY KEY,
    teacher_id VARCHAR(20) NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    roles TEXT NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (subject_code) REFERENCES subjects(code)
);

-- Create confirm_codes table
CREATE TABLE public.confirm_codes (
    id SERIAL PRIMARY KEY,
    id_code BIGINT NOT NULL,
    code VARCHAR(255) NOT NULL,
    exp_time TIMESTAMP NOT NULL,
    FOREIGN KEY (id_code) REFERENCES user_data(id)
);

-- Create news table
CREATE TABLE public.news (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    date_of_creation TIMESTAMP NOT NULL
);



-- Create devices table
CREATE TABLE devices (
                         id SERIAL PRIMARY KEY,
                         device_id VARCHAR(255) NOT NULL UNIQUE,
                         push_token VARCHAR(255),
                         platform VARCHAR(50),
                         type VARCHAR(50),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         last_active_at TIMESTAMP
);