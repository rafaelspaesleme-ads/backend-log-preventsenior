
# LOGS PREVENT SENIOR

>Prevent Senior - Desafio Técnico

<a href="https://logs-rpl-prvsr.herokuapp.com/">Acessar Webservices<a/>

>>Aplicação em Java para fazer o upload de um arquivo de logs populando o banco de dados.

>>Backend de CRUD.
* Linguagem de Programação: Java 11;
* Framework: Spring Boot 2.3.9.RELEASE;
    * Spring Security;
    * Spring Data JPA;
    * Spring Data JDBC;
    * Spring Rest Controller;
    * JUnit (testes unitarios);
    * Swagger (Documentação de endpoints);
    * Lombok (Produtividade e Clean Code);

* Banco de dados:
    * H2 (Testes);
    * PostgreSQL (Dev);
        * Configuração do banco de dados com Docker: 
        - `docker run -t --name db_log_prevent_sr -d -p 5779:5432 -e DB_NAME=db_log_prevent_sr -e DB_USER=postgres -e DB_PASSWD=banco@2004 rafaelspaesleme/imagedatabase:latest`


>>Detalhes do back-end

* Definir o modelo de dados no PostgreSQL;
* Definir serviços para envio do arquivo de logs fornecido, e inserção dos logs em massa usando JPA;
* Definir serviços para inserção de logs manuais (CRUD) (não utilizar spring-data-jpa);
* Implementar filtros ou pesquisas de logs;
* Testes Unitários;
* (BÔNUS) Testes de Integração;
