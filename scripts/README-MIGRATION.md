# Property Status Migration Guide

## Overview

This directory contains SQL migration scripts to update the property status system from the old string-based status values to the new enum-based system.

## Status Changes

### Old Status Values
- `PENDING` - Property created, waiting for processing
- `PROCESSING` - Being processed on blockchain
- `PENDING_APPROVALS` - Waiting for approvals
- `EXECUTED` - Completed successfully
- `FAILED` - Processing failed

### New Status Values
- `PENDENTE` - Property created and sent to processing queue
- `PROCESSANDO_REGISTRO` - Being registered on blockchain (waiting for approvals)
- `EM_TRANSFERENCIA` - Property transfer in progress
- `OK` - Registration minted or transfer completed

## Migration Mapping

| Old Status | New Status |
|------------|------------|
| PENDING | PENDENTE |
| PROCESSING | PROCESSANDO_REGISTRO |
| PENDING_APPROVALS | PROCESSANDO_REGISTRO |
| EXECUTED | OK |
| FAILED | PENDENTE (allows retry) |

## How to Run Migration

### Prerequisites

1. **Backup your database** before running any migration:
   ```bash
   docker exec core-orchestrator-postgres pg_dump -U core_user core_db > backup_before_migration.sql
   ```

2. Stop the application to prevent concurrent database access:
   ```bash
   cd /Users/fabiano/college/core-orchestrator-srv
   ./start.sh stop
   ```

### Running the Migration

Execute the migration script using `psql`:

```bash
docker exec -i core-orchestrator-postgres psql -U core_user -d core_db < scripts/migrate-property-status.sql
```

Or connect to PostgreSQL and run manually:

```bash
docker exec -it core-orchestrator-postgres psql -U core_user -d core_db
```

Then paste the contents of `migrate-property-status.sql`.

### Verify Migration

After running the migration, verify the data:

```sql
-- Connect to database
docker exec -it core-orchestrator-postgres psql -U core_user -d core_db

-- Check status distribution
SELECT status, COUNT(*) as count
FROM properties
GROUP BY status;

-- Expected output:
-- status                  | count
-- ------------------------+-------
-- PENDENTE               | X
-- PROCESSANDO_REGISTRO   | Y
-- OK                     | Z
-- (possibly EM_TRANSFERENCIA if any transfers are in progress)
```

### Start Application

After successful migration:

```bash
cd /Users/fabiano/college/core-orchestrator-srv
./start.sh start
```

The application will now use the new enum-based status system.

## Rollback (if needed)

If you need to revert the migration:

```bash
# Stop application
cd /Users/fabiano/college/core-orchestrator-srv
./start.sh stop

# Run rollback script
docker exec -i core-orchestrator-postgres psql -U core_user -d core_db < scripts/rollback-property-status.sql

# Start application
./start.sh start
```

⚠️ **WARNING:** Only rollback if absolutely necessary. After rollback, you must also revert the code changes to use the old status strings instead of the PropertyStatus enum.

## Migration Steps Explained

The migration script performs these operations:

1. **Creates temporary column** `status_new` for safe migration
2. **Migrates data** using CASE statement to map old values to new ones
3. **Drops old column** to remove legacy data
4. **Renames new column** to `status`
5. **Adds NOT NULL constraint** for data integrity
6. **Adds CHECK constraint** to enforce valid enum values
7. **Creates index** on status column for query performance

## Troubleshooting

### Error: "column status_new already exists"

The migration script might have been partially run. Drop the column first:

```sql
ALTER TABLE properties DROP COLUMN IF EXISTS status_new;
```

Then re-run the migration.

### Error: "constraint check_property_status already exists"

The CHECK constraint already exists. Drop it first:

```sql
ALTER TABLE properties DROP CONSTRAINT IF EXISTS check_property_status;
```

Then re-run the migration.

### Data not migrated correctly

If some properties have unexpected status values, you can manually update them:

```sql
-- Find properties with unexpected status
SELECT id, matricula_id, status FROM properties
WHERE status NOT IN ('PENDENTE', 'PROCESSANDO_REGISTRO', 'EM_TRANSFERENCIA', 'OK');

-- Fix manually if needed
UPDATE properties SET status = 'PENDENTE' WHERE id = <property_id>;
```

## Database Schema After Migration

```sql
CREATE TABLE properties (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL UNIQUE,
    folha BIGINT NOT NULL,
    comarca VARCHAR(100) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    metragem BIGINT NOT NULL,
    proprietario BIGINT NOT NULL REFERENCES users(id),
    matricula_origem BIGINT,
    tipo VARCHAR(20) NOT NULL,
    is_regular BOOLEAN NOT NULL,
    blockchain_tx_hash VARCHAR(66),
    request_hash VARCHAR(66),
    approval_status VARCHAR(20),
    status VARCHAR(30) NOT NULL CHECK (status IN ('PENDENTE', 'PROCESSANDO_REGISTRO', 'EM_TRANSFERENCIA', 'OK')),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_properties_status ON properties(status);
```

## Support

If you encounter any issues during migration, check:

1. **Application logs**: `./start.sh logs`
2. **Database connection**: `docker exec -it core-orchestrator-postgres psql -U core_user -d core_db`
3. **Constraint violations**: Check if any application code still uses old status strings

For questions or issues, refer to the project documentation or contact the development team.
