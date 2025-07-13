#!/bin/bash
set -e

echo "Esperando o banco de dados ficar pronto..."
until pg_isready -h db -p 5432 -U postgres; do
  >&2 echo "Banco de dados não está pronto ainda..."
  sleep 2
done

echo "Banco de dados está pronto. Rodando testes em modo DEBUG..."

# Forçando debug manualmente com suspend=y para aguardar conexão do IntelliJ
mvn test -DforkCount=0 -Dmaven.surefire.argLine="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"
