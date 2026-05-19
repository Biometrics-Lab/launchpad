CREATE SCHEMA :"db_name" AUTHORIZATION :"db_user";
GRANT ALL ON SCHEMA :"db_name" TO :"db_user";
ALTER ROLE :"db_user" SET search_path TO :"db_name";
