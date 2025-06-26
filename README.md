# FitTrack

## Prerequisites
- **JDK 21**
- **Maven**

## Compiling

### Maven
Run from the repository root:

```bash
mvn package
```

This will produce `target/progetto_object-1.0-SNAPSHOT.jar`. Start the application with:

```bash
java -cp target/progetto_object-1.0-SNAPSHOT.jar model.Main
```

### javac
If you prefer a manual compilation, use `javac`:

```bash
mkdir -p out
javac -d out $(find src/main/java -name "*.java")
java -cp out model.Main
```

The application now uses a PostgreSQL database. Configure the connection using the
`DB_URL`, `DB_USER` and `DB_PASS` environment variables if the defaults do not
match your setup.
