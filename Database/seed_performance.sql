USE vietnamjobs;

SET NAMES utf8mb4;

/*
Performance dataset for query and page stress testing.
Run this from the repository root, for example:

  mysql -u root -p vietnamjobs < Database/seed_performance.sql

This script:
  - loads shared seed procedures
  - removes previous perf_* seeded rows
  - seeds a larger dataset for benchmark scenarios
*/

SOURCE Database/seed_data.sql;

CALL cleanup_seeded_performance_data();
CALL seed_performance_dataset(2500, 250, 10000, 25000, 10000);

SELECT meta_key, meta_value, updated_at
FROM perf_seed_meta
ORDER BY meta_key;
