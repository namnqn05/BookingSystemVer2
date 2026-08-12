-- Ensure existing null locked_department_id values are updated to 1 before enforcing NOT NULL
UPDATE rooms SET locked_department_id = 1 WHERE locked_department_id IS NULL;

-- Enforce NOT NULL on locked_department_id column in rooms table
ALTER TABLE rooms ALTER COLUMN locked_department_id SET NOT NULL;
