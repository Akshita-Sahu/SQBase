<div align="center">
  <h1>⚡ SQBase</h1>
  <h3>Your Smartest Database Workspace</h3>
  <p>An AI-powered, production-ready, entirely original database management tool.</p>
</div>

![SQBase UI Demo (Placeholder)](https://via.placeholder.com/1200x600/0b192c/ffffff?text=SQBase+AI+SQL+Demo+GIF)

---

## ✨ Features

- **🧠 AI SQL Assistant**: Natural language to perfect SQL, fully aware of your connected schema. Uses OpenAI with local Ollama fallback.
- **📊 Real-Time Database Dashboard**: Live JFreeChart monitoring for active connections, table sizes, and slow queries.
- **👥 Smart Query Workspace**: Git-style version control for your queries with diff views and tagging.
- **📈 One-Click Data Visualizer**: Turn raw SQL result sets into customizable charts (Bar/Line/Pie) exported as PDFs.
- **🎨 Modern UI**: Deep VS Code-inspired navy/electric blue dark mode, collapsible schema views, and a global Command Palette (`Ctrl+P`).
- **🔐 Enterprise Security**: AES-encrypted connection vaults and full query audit logs.

## 🛠️ Built With

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) 
![Eclipse RCP](https://img.shields.io/badge/Eclipse_RCP-2C2255?style=for-the-badge&logo=eclipseide&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-007396?style=for-the-badge&logo=java&logoColor=white)

## 🚀 Quick Install

```bash
git clone https://github.com/Akshita-Sahu/SQBase.git
cd SQBase
mvn clean install -Ddesktop
```

## 🧠 How it Works

**1. The AI Assistant:**
SQBase runs an embedded routine bridging your JDBC database metadata with our LLM orchestrators. Every prompt dynamically injects your table schemas to guarantee flawless, context-aware SQL execution.

**2. The Live Monitor:**
By aggressively polling internal `pg_stat_activity` and `information_schema` tables securely, SQBase gives you DBA-level insights on query lag before it crashes your backend.

## 🏗️ Architecture

```text
/sqbase-core         → core execution engine, JDBC drivers
/sqbase-ui           → eclipse RCP themes, layout, handlers
/sqbase-ai           → ollama / openai orchestrator
/sqbase-dashboard    → async jfreechart performance metrics
/sqbase-workspace    → collaborative diffs, local history
/sqbase-viz          → data-to-chart processing 
/sqbase-security     → vault, audit log, access control
/sqbase-product      → packaging, installers
```

## 🛣️ Roadmap

- Multi-cloud AWS/GCP native connection profiles
- AI-driven index creation suggestions
- P2P direct query session multiplexing

## 🤝 Contributing

Open a PR or issue!

## 📄 License

MIT License
