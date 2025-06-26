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

Application state is stored under the `data/` directory so it is preserved between executions.
No external database is required in this version.
