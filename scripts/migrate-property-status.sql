-- Migration Script: Update Property Status from Old String Values to New Enum
-- Created: 2025-01-19
-- Description: Migrates property status from old system (PENDING, PROCESSING, etc.)
--              to new system (PENDENTE, PROCESSANDO_REGISTRO, EM_TRANSFERENCIA, OK)

-- Step 1: Add temporary column for migration
ALTER TABLE properties
ADD COLUMN status_new VARCHAR(30);

-- Step 2: Migrate existing data to new status values
UPDATE properties
SET status_new = CASE
    WHEN status = 'PENDING' THEN 'PENDENTE'
    WHEN status = 'PROCESSING' THEN 'PROCESSANDO_REGISTRO'
    WHEN status = 'PENDING_APPROVALS' THEN 'PROCESSANDO_REGISTRO'
    WHEN status = 'EXECUTED' THEN 'OK'
    WHEN status = 'FAILED' THEN 'PENDENTE'  -- Failed properties go back to PENDENTE for retry
    ELSE 'PENDENTE'  -- Default for any unexpected values
END;

-- Step 3: Verify migration (optional - run this separately to check data)
-- SELECT status AS old_status, status_new AS new_status, COUNT(*)
-- FROM properties
-- GROUP BY status, status_new;

-- Step 4: Drop old status column
ALTER TABLE properties
DROP COLUMN status;

-- Step 5: Rename new column to status
ALTER TABLE properties
RENAME COLUMN status_new TO status;

-- Step 6: Add NOT NULL constraint
ALTER TABLE properties
ALTER COLUMN status SET NOT NULL;

-- Step 7: Add CHECK constraint to enforce enum values (PostgreSQL doesn't have ENUM for Hibernate compatibility)
ALTER TABLE properties
ADD CONSTRAINT check_property_status
CHECK (status IN ('PENDENTE', 'PROCESSANDO_REGISTRO', 'EM_TRANSFERENCIA', 'OK'));

-- Step 8: Create index on status column for performance
CREATE INDEX idx_properties_status ON properties(status);

-- Migration complete!
-- New status flow:
-- Registration: PENDENTE → PROCESSANDO_REGISTRO → OK
-- Transfer: OK → EM_TRANSFERENCIA → OK (with new owner)
