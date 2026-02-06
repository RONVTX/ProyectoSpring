#!/bin/bash

# Script para configurar e iniciar la aplicación SaaS Platform

echo "======================================"
echo "  SaaS Platform - Setup & Run"
echo "======================================"
echo

# Colores para mensajes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar Java
echo -e "${YELLOW}Verificando Java...${NC}"
if ! command -v java &> /dev/null
then
    echo "Java no está instalado"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | awk -F'"' '{print $2}')
echo -e "${GREEN}Java $JAVA_VERSION encontrado${NC}"
echo

# Verificar Maven
echo -e "${YELLOW}Verificando Maven...${NC}"
if [ ! -f "./mvnw" ]
then
    echo "Maven wrapper no encontrado. Por favor ejecuta desde la raíz del proyecto."
    exit 1
fi
echo -e "${GREEN}Maven wrapper encontrado${NC}"
echo

# Verificar MySQL
echo -e "${YELLOW}Verificando conexión a MySQL...${NC}"
echo "Por favor asegúrate de que MySQL esté corriendo en localhost:3306"
echo

# Compilar
echo -e "${YELLOW}Compilando proyecto...${NC}"
./mvnw clean install -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Compilación exitosa${NC}"
else
    echo "Error en la compilación"
    exit 1
fi
echo

# Iniciar la aplicación
echo -e "${GREEN}Iniciando aplicación...${NC}"
echo "La aplicación estará disponible en: http://localhost:8080"
echo
./mvnw spring-boot:run
