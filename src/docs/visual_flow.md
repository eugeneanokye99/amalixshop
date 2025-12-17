┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   JAVA FX UI    │────▶│   CONTROLLER    │────▶│     SERVICE     │────▶│       DAO       │
│                 │     │                 │     │                 │     │                 │
│ • TextFields    │     │ • handleClick() │     │ • Validation    │     │ • SQL Queries   │
│ • Buttons       │     │ • Get values    │     │ • Business Logic│     │ • PreparedStmt  │
│ • FXML binding  │     │ • Show alerts   │     │ • Password hash │     │ • ResultSet     │
└─────────────────┘     └─────────────────┘     └─────────────────┘     └────────┬────────┘
│
▼
┌─────────────────┐
│   POSTGRESQL    │
│                 │
│ • customers tbl │
│ • Insert/Select │
└─────────────────┘