SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = :'db_name' AND pid <> pg_backend_pid();

drop database if exists :"db_name";
drop role if exists :"db_user";
