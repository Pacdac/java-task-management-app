# Use the official PostgreSQL image as a parent image
FROM postgres:17-alpine

# Set environment variables
ENV POSTGRES_DB=taskdb
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

# Copy initialization scripts
COPY ./init.sql /docker-entrypoint-initdb.d/

# Expose the PostgreSQL port
EXPOSE 5432

# Set the default command to run when starting the container
CMD ["postgres"]