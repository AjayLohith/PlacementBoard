<div align="left" style="position: relative;">

  <h1>PlacementBoard</h1>

  <p align="left">
    A comprehensive placement and career development portal designed to connect students,
    job seekers, and employers. Features job listings, company profiles, interview experiences,
    and AI-powered career guidance.
  </p>

  

  <p align="left">Built with the tools and technologies:</p>

  <p align="left">
    <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F.svg?style=flat&logo=springboot&logoColor=white" alt="Spring Boot">
    <img src="https://img.shields.io/badge/Java-ED8B00.svg?style=flat&logo=openjdk&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/React-61DAFB.svg?style=flat&logo=react&logoColor=black" alt="React">
    <img src="https://img.shields.io/badge/JavaScript-F7DF1E.svg?style=flat&logo=javascript&logoColor=black" alt="JavaScript">
    <img src="https://img.shields.io/badge/MongoDB-13AA52.svg?style=flat&logo=mongodb&logoColor=white" alt="MongoDB">
    <img src="https://img.shields.io/badge/Vite-646CFF.svg?style=flat&logo=vite&logoColor=white" alt="Vite">
    <img src="https://img.shields.io/badge/Vercel-000000.svg?style=flat&logo=vercel&logoColor=white" alt="Vercel">
    <img src="https://img.shields.io/badge/Render-46E3B7.svg?style=flat&logo=render&logoColor=black" alt="Render">
    <img src="https://img.shields.io/badge/Docker-2496ED.svg?style=flat&logo=docker&logoColor=white" alt="Docker">
    <img src="https://img.shields.io/badge/Groq-FF6B35.svg?style=flat&logo=groq&logoColor=white" alt="Groq AI">
    <img src="https://img.shields.io/badge/JWT-000000.svg?style=flat&logo=jsonwebtokens&logoColor=white" alt="JWT">
  </p>
</div>

<br clear="right">

<hr>

<h2>📍 Overview</h2>

PlacementPedia is an open-source placement and career development platform built to empower students and job seekers.
It features a robust Spring Boot backend with MongoDB persistence, AI-powered career guidance via Groq API, 
and a modern React frontend. Users can explore job opportunities, discover companies, share interview experiences,
and access curated articles for career growth.

<hr>

<h2>👾 Features</h2>

<ul>
  <li>🔍 <strong>Job Listings & Search</strong> – Browse and filter jobs by company, experience level, and audience (Freshers/Experienced).</li>
  <li>🏢 <strong>Company Profiles</strong> – Explore detailed company information, reviews, and opportunities.</li>
  <li>💼 <strong>Interview Experiences</strong> – Share and read real interview experiences from other candidates.</li>
  <li>📚 <strong>Articles & Resources</strong> – Access curated guides and tips for career development, filterable by audience.</li>
  <li>🤖 <strong>AI Career Guidance</strong> – Get personalized career advice powered by Groq's LLaMA 3.3 model.</li>
  <li>🔐 <strong>Secure Authentication</strong> – JWT-based authentication with password reset via email.</li>
  <li>📧 <strong>Email Notifications</strong> – OTP-based password reset via Brevo SMTP.</li>
  <li>🛡️ <strong>Admin Dashboard</strong> – Manage jobs, companies, articles, and user interactions.</li>
  <li>🐳 <strong>Docker Ready</strong> – Easy containerized deployment for both frontend and backend.</li>
</ul>

<hr>

<h2>📁 Project Structure</h2>

<pre><code>
└── PlacementPedia/
    ├── backend           # Spring Boot backend service
    ├── frontend          # React frontend application
    └── README.md
</code></pre>

<hr>

<h2>🚀 Getting Started</h2>

<h3>☑️ Prerequisites</h3>

<ul>
  <li><strong>Java:</strong> 17+</li>
  <li><strong>Node.js & npm:</strong> Latest LTS version</li>
  <li><strong>MongoDB:</strong> Cloud or local instance</li>
  <li><strong>Docker:</strong> (optional, recommended)</li>
  <li><strong>Groq API Key:</strong> For AI features</li>
  <li><strong>Brevo Account:</strong> For email notifications</li>
</ul>

<h3>⚙️ Installation</h3>

<h4>Clone the Repository</h4>

<pre><code>
git clone https://github.com/AjayLohith/PlacementPedia.git
cd PlacementPedia
</code></pre>

<h4>Backend Setup</h4>

<pre><code>
cd backend

# Create .env file with required variables
# DATABASE URL, JWT_SECRET, EMAIL credentials, AI_API_KEY, etc.

./mvnw spring-boot:run
</code></pre>

<h4>Frontend Setup</h4>

<pre><code>
cd frontend

npm install
npm run dev
</code></pre>

<h4>Using Docker</h4>

<pre><code>
# Docker setup coming soon
docker build -t placementpedia-backend ./backend
docker build -t placementpedia-frontend ./frontend
</code></pre>

<hr>

<h3>🤖 Usage</h3>

<ul>
  <li>Access the frontend at <code>http://localhost:5173</code> (or deployed URL)</li>
  <li>Backend API runs at <code>http://localhost:5001</code></li>
  <li>Browse jobs, companies, and articles filtered by audience</li>
  <li>Share your interview experiences</li>
  <li>Get AI-powered career guidance</li>
  <li>Admin users can manage all content and user interactions</li>
</ul>

<hr>

<h3>🧪 Testing</h3>

<pre><code>
cd backend
./mvnw test
</code></pre>

<hr>

<h2>📌 Project Roadmap</h2>

<ul>
  <li>✅ Core job board functionality</li>
  <li>✅ Interview experience sharing</li>
  <li>✅ AI-powered career guidance</li>
  <li>✅ Articles & resources hub</li>
  <li>⬜ Advanced analytics for companies</li>
  <li>⬜ User resume storage and ATS integration</li>
  <li>⬜ Real-time notifications</li>
  <li>⬜ Mobile app (iOS & Android)</li>
</ul>

<hr>

<h2>🔰 Contributing</h2>

<p>
  Contributions are welcome! Feel free to fork the repository, create a feature branch,
  and submit a pull request. Please ensure your code follows the project standards.
</p>

<hr>

<h2>📝 License</h2>

<p>This project is open-source and available under the MIT License.</p>

<hr>

<h2>🙋 Support</h2>

<p>
  For issues, feature requests, or general questions, please open an issue on GitHub.
  Connect with us on social media for updates and announcements.
</p>
