# WOMPI Automation Test

Automatización de pruebas de integración API para la plataforma WOMPI.

---

## Requisitos

| Herramienta | Versión mínima |
|-------------|----------------|
| Java        | 17 LTS         |
| Maven       | 3.8+           |
| Git         | 2.x            |

---

## Ejecución

```bash
mvn clean test
```

---

## Estructura del proyecto

```
wompi-automation-test/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/wompi/
│   └── test/
│       ├── java/
│       │   └── com/wompi/
│       │       ├── runners/
│       │       ├── stepdefinitions/
│       │       └── utils/
│       └── resources/
│           ├── features/
│           └── log4j2.xml
├── .gitignore
├── pom.xml
└── README.md
```

---

## Documentación

- [Cucumber](https://cucumber.io/docs/cucumber/)
- [REST Assured](https://rest-assured.io/)
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [WOMPI API](https://docs.wompi.co/)
