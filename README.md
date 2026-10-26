# Bilingual Reading Website 📖

A calm, welcoming web application for readers to discover and read short literary pieces, ghazals, and journal entries in **Hindi and English**. Built with a focus on accessibility, clean typography, and a peaceful reading experience free from social media metrics (no likes, comments, or streaks).

## ✨ Core Features

* **True Bilingual Architecture:** Hindi and English are treated as separate, native browsing experiences rather than side-by-side translations. Fully supports UTF-8 and Devanagari script rendering.
* **Mood-Based Discovery:** Readers can filter content based on their current emotional state (e.g., Peaceful, Hopeful, Nostalgic, Melancholic).
* **Curated Collections:** Organizes related reading pieces into themed collections.
* **Custom Built-in CMS:** A secured admin dashboard that allows the site owner to draft, preview, tag, and publish content without modifying code.
* **Algorithm-Free:** Gentle, static recommendations based on shared tags and collections rather than aggressive data-mining algorithms.

## 🛠️ Tech Stack

* **Backend:** Java 25, Spring Boot 4.1.2, Spring Security
* **Database:** PostgreSQL 18 (with Flyway for automated schema migrations)
* **Frontend:** Thymeleaf (Server-Side Rendering), HTML5, custom CSS (CSS Variables)
* **Testing:** JUnit 5, Mockito
* **Deployment:** Docker, Maven

## 🚀 Running Locally with Docker

The easiest way to run the application is using the provided Docker configuration.

**1. Build the Docker Image:**
```bash
docker build -t readingsite-app .