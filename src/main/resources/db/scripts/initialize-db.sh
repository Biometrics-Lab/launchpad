#!/bin/bash

psql -d postgres -f initalize-db.sql
psql -d biolab -f initalize-db-scheme.sql