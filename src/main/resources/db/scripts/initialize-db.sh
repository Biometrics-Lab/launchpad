#!/bin/bash

psql -d postgres \
  -v db_name="${DB_NAME}" \
  -v db_user="${DB_USER}" \
  -v db_password="${DB_PASSWORD}" \
  -f initalize-db.sql

psql -d "${DB_NAME}" \
  -v db_name="${DB_NAME}" \
  -v db_user="${DB_USER}" \
  -f initalize-db-scheme.sql
