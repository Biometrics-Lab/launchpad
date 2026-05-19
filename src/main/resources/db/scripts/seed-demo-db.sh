#!/bin/bash

psql -U "${DB_USER}" -d "${DB_NAME}" -f seed-data.sql
