# Vittiq 📊

<p align="center">
  <img src="assets/vittiq-logo.png" alt="Vittiq Logo" width="300" />
</p>

Vittiq brings all your finances into one place, automatically tracks your spending, and uses AI to surface smart insights, so you always know where your money is going and how to improve it.

Built as a modern, self-hosted financial ecosystem, Vittiq features a native Android client alongside an AI-driven backend powered by an SQL-based agent capable of understanding natural language financial queries.

---

## 🚀 Key Features (Target Roadmap)

- **Multi-Account Aggregation:** Consolidate data across credit cards, cash, and savings accounts in one secure dashboard.
- **Automated & Manual Ingestion:** Intelligent tracking (via local SMS parsing/import) combined with intuitive manual logging.
- **Shared Expense Mapping:** Seamlessly link and map expenses to other users' IDs to track shared or split payments without the clutter.
- **SQL AI Agent:** Chat naturally with your financial data (e.g., _"How much did I spend on dining out last month compared to my budget?"_) to get structured insights instantly.

---

## 🛠 Architecture & Tech Stack

Vittiq is organized as a **monorepo** employing a microservices-based backend architecture to decouple data ingestion, core business logic, and the AI agent framework.

### Client

- **Platform:** Android (Native)
- **UI Framework:** Jetpack Compose
- **Local Storage:** Room database for offline-first capabilities and secure local caching.

### Backend & AI Services

- **Core Services:** Fast, containerized microservices.
- **AI Engine:** SQL-based LLM Agent framework (handling text-to-SQL translation and structured data reasoning).
- **Database:** Relational database optimized for financial transactions and analytical queries.

---

## 📂 Repository Structure

```text
vittiq/
├── android-client/       # Jetpack Compose native Android application
├── backend-services/     # Core microservices (Ingestion, Mapping, Auth)
├── ai-agent/             # SQL-based AI agent logic & LLM pipelines
└── docker-compose.yml    # Local development & orchestration setup
```

---

## 📄 License

This project is open-source but restricted to non-commercial use. It is licensed under the **PolyForm Noncommercial License 1.0.0**.

You are free to use, modify, and distribute this software for personal, educational, and research projects. Production or commercial usage aimed at monetary compensation or commercial advantage is strictly prohibited. For details, please read the full [LICENSE](https://github.com/khandelwalkunal779/vittiq/blob/main/LICENSE) file.
