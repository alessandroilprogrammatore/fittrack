# FitTrack

## Prerequisites
- **JDK 21**
- **Maven**

## Compiling
Run from the repository root:

```bash
mvn package
```

This produces `target/progetto_object-1.0-SNAPSHOT.jar`.

## Running
Launch the program using:

```bash
java -cp target/progetto_object-1.0-SNAPSHOT.jar model.Main
```

Application state is stored under the `data/` directory so it is preserved
between executions.
