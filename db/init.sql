-- Initialize database for Task Management Application

-- Create users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(20) DEFAULT 'USER' -- e.g., admin, user, etc.
);

-- Create task_status table
CREATE TABLE task_status (
    status_id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    color VARCHAR(20) -- For UI representation
);

-- Create task_category table
CREATE TABLE task_category (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    color VARCHAR(20) -- For UI representation
);

-- Create task_priority table
CREATE TABLE task_priority (
    priority_id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    priority_value INTEGER UNIQUE NOT NULL,
    description TEXT,
    color VARCHAR(20),
    display_order INTEGER DEFAULT 0
);

-- Create tasks table
CREATE TABLE tasks (
    task_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    due_date DATE,
    priority_id INTEGER REFERENCES task_priority(priority_id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    status_id INTEGER REFERENCES task_status(status_id) ON DELETE SET NULL,
    category_id INTEGER REFERENCES task_category(category_id) ON DELETE SET NULL
);

-- Create task_comments table
CREATE TABLE task_comments (
    comment_id SERIAL PRIMARY KEY,
    task_id INTEGER REFERENCES tasks(task_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create user_task_assignments table for assigning tasks to multiple users
CREATE TABLE user_task_assignments (
    assignment_id SERIAL PRIMARY KEY,
    task_id INTEGER REFERENCES tasks(task_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (task_id, user_id)
);

-- Create task_attachments table for files attached to tasks
CREATE TABLE task_attachments (
    attachment_id SERIAL PRIMARY KEY,
    task_id INTEGER REFERENCES tasks(task_id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(512) NOT NULL,
    file_size INTEGER,
    file_type VARCHAR(100),
    uploaded_by INTEGER REFERENCES users(user_id) ON DELETE SET NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert default task statuses
INSERT INTO task_status (name, description, color) VALUES 
    ('To Do', 'Tasks that need to be started', '#3498db'),
    ('In Progress', 'Tasks currently being worked on', '#f39c12'),
    ('Review', 'Tasks that need review', '#9b59b6'),
    ('Done', 'Completed tasks', '#2ecc71');

-- Insert default task priorities
INSERT INTO task_priority (name, priority_value, description, color, display_order) VALUES 
    ('Very Low', 1, 'Lowest priority tasks', '#95a5a6', 1),
    ('Low', 2, 'Low priority tasks', '#3498db', 2),
    ('Medium', 3, 'Medium priority tasks', '#f39c12', 3),
    ('High', 4, 'High priority tasks', '#e67e22', 4),
    ('Very High', 5, 'Highest priority tasks', '#e74c3c', 5);

-- Insert default task categories
INSERT INTO task_category (name, description, color) VALUES 
    ('Feature', 'New features or functionality', '#e74c3c'),
    ('Bug', 'Issues that need to be fixed', '#e67e22'),
    ('Documentation', 'Documentation related tasks', '#1abc9c'),
    ('Research', 'Research tasks', '#34495e'),
    ('Maintenance', 'System maintenance tasks', '#7f8c8d');

-- Create indexes for better performance
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status_id ON tasks(status_id);
CREATE INDEX idx_tasks_category_id ON tasks(category_id);
CREATE INDEX idx_task_comments_task_id ON task_comments(task_id);
CREATE INDEX idx_user_task_assignments_task_id ON user_task_assignments(task_id);
CREATE INDEX idx_user_task_assignments_user_id ON user_task_assignments(user_id);
CREATE INDEX idx_task_attachments_task_id ON task_attachments(task_id);

-- Create a function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

-- Create triggers to automatically update the updated_at column
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
