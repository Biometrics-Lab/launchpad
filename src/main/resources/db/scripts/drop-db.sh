#!/bin/bash

psql -d postgres \
  -v db_name="${DB_NAME}" \
  -v db_user="${DB_USER}" \
  -f drop-db.sql
