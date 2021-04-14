# backend-log-preventsenior
# app-log-preventsenior


Criando banco de dados no PostgreSQL via docker:
docker run -t --name db_log_prevent_sr -d -p 5779:5432 -e DB_NAME=db_log_prevent_sr -e DB_USER=postgres -e DB_PASSWD=banco@2004 rafaelspaesleme/imagedatabase:latest