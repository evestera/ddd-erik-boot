
### Running

```
SERVER_PORT=5000 SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

### Initialize local dev environment database

```
psql postgres -c "create user ddderikboot with password 'ddderikboot';"
createdb --owner=ddderikboot ddderikboot
```
