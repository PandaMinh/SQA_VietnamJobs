USE vietnamjobs;

SET NAMES utf8mb4;

/*
Smoke dataset for UI, E2E, integration, and daily regression.
Run this from the repository root, for example:

  mysql -u root -p vietnamjobs < Database/seed_smoke.sql

This script:
  - loads shared seed procedures
  - removes previous perf_* seeded rows
  - seeds a compact dataset for daily autotest
  - keeps each main table roughly around 50-70 total rows when combined with the base dump
*/

SOURCE Database/seed_data.sql;

CALL cleanup_seeded_performance_data();
CALL seed_performance_dataset(55, 50, 60, 65, 60);

SELECT meta_key, meta_value, updated_at
FROM perf_seed_meta
ORDER BY meta_key;
