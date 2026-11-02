# ==============================================================================
# Etapa 1: Compilação e empacotamento do .war via Maven Wrapper (JDK 25)
# ==============================================================================
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copia arquivos do Maven primeiro para aproveitar cache de dependências
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copia o código-fonte e gera o pacote .war
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# ==============================================================================
# Etapa 2: Servidor de Aplicação Apache Tomcat 11 (Jakarta EE 11 + JDK 25)
# ==============================================================================
FROM tomcat:11.0-jdk25-temurin
WORKDIR /usr/local/tomcat

# Remove os apps padrão do Tomcat e publica o Efficientia na raiz (/)
RUN rm -rf webapps/*
COPY --from=build /app/target/*.war webapps/ROOT.war

# Porta padrão (Render / Cloud PaaS injeta a variável PORT dinamicamente)
ENV PORT=8080
EXPOSE 8080

# Ajusta a porta HTTP do Tomcat para respeitar $PORT do provedor Cloud e inicia o servidor
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-8080}\\\"/g\" conf/server.xml && catalina.sh run"]
