
---
## IPU College Ranking System

This project aims to build a **comprehensive college ranking system for Guru Gobind Singh Indraprastha University (IPU)** based on multiple factors such as placements, faculty, infrastructure, student reviews, and academic performance.

The current module focuses on **placement data parsing and normalization**, which extracts placement statistics from PDF reports of various IPU-affiliated colleges, cleans the data, and stores it in a structured format. This forms the foundation for calculating **placement-based rankings**.

Future modules will include:

* Academic performance analysis
* Infrastructure & facilities evaluation
* Student feedback integration
* Final college ranking dashboard

**Tech Stack:** Java, Spring Boot, MySQL/PostgreSQL, PDFBox, REST API.


---
## High-Level Architecture


```
┌────────────┐
│  Websites  │
│ (HTML + PDF)
└─────┬──────┘
      │ Jsoup (fetch PDF links)
┌─────▼──────┐
│ PDF URLs   │
└─────┬──────┘
      │ PDFBox (extract raw text)
┌─────▼────────────┐
│ Raw PDF Text     │
└─────┬────────────┘
      │ College-wise Parser
┌─────▼────────────┐
│ Normalized Data  │
│ (PlacementRecord)
└─────┬────────────┘
      │ JPA
┌─────▼────────────┐
│   MySQL DB       │
└─────┬────────────┘
      │ REST APIs
┌─────▼────────────┐
│ Frontend (React) │
└──────────────────┘

```
---


