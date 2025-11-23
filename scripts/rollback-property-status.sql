-- Rollback Script: Revert Property Status Migration
-- Created: 2025-01-19
-- Description: Reverts property status from new system (PENDENTE, PROCESSANDO_REGISTRO, etc.)
--              back to old system (PENDING, PROCESSING, etc.)
-- WARNING: This should only be used if the migration needs to be rolled back

-- Step 1: Add temporary column for rollback
ALTER TABLE properties
ADD COLUMN status_old VARCHAR(30);

-- Step 2: Map new status values back to old ones
UPDATE properties
SET status_old = CASE
    WHEN status = 'PENDENTE' THEN 'PENDING'
    WHEN status = 'PROCESSANDO_REGISTRO' THEN 'PROCESSING'
    WHEN status = 'EM_TRANSFERENCIA' THEN 'PROCESSING'  -- Map transfer status to PROCESSING
    WHEN status = 'OK' THEN 'EXECUTED'
    ELSE 'PENDING'  -- Default for any unexpected values
END;

-- Step 3: Drop CHECK constraint
ALTER TABLE properties
DROP CONSTRAINT check_property_status;

-- Step 4: Drop index on status column
DROP INDEX IF EXISTS idx_properties_status;

-- Step 5: Drop new status column
ALTER TABLE properties
DROP COLUMN status;

-- Step 6: Rename old column back to status
ALTER TABLE properties
RENAME COLUMN status_old TO status;

-- Step 7: Add NOT NULL constraint
ALTER TABLE properties
ALTER COLUMN status SET NOT NULL;

-- Rollback complete!
-- Status values restored to: PENDING, PROCESSING, PENDING_APPROVALS, EXECUTED, FAILED
