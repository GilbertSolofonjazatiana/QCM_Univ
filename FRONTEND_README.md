# Frontend QCM Platform

## Overview

A complete, responsive HTML/CSS/JavaScript frontend for the QCM examination platform, fully integrated with the Java/Servlet backend.

## Project Structure

```
public/
├── html/
│   ├── login.html              # Login & Registration page
│   ├── student-home.html       # Student home/dashboard
│   ├── exam.html               # Exam questions interface
│   ├── admin-dashboard.html    # Admin dashboard
│   └── [other admin pages]
├── css/
│   ├── login.css               # Login page styling
│   ├── layout.css              # Shared layout (sidebar, topbar)
│   ├── student-home.css        # Student home styling
│   ├── exam.css                # Exam page styling
│   └── admin.css               # Admin pages styling
└── js/
    ├── login.js                # Login/Register form handling
    ├── layout.js               # Shared functionality (theme, logout)
    ├── student-home.js         # Student home interactions
    ├── exam.js                 # Exam logic & timer
    └── admin-dashboard.js      # Admin dashboard functionality
```

## Pages Included

### 1. Login Page (`login.html`)
- **Features:**
  - Login form with email & password (student ID)
  - Registration form for new students
  - Tab switching between login/register
  - Dark mode toggle
  - Demo credentials display
  - Form validation

- **Backend Integration:**
  - `POST /qcm/login` - Authenticate user
  - `POST /qcm/register` - Create new student account

### 2. Student Home (`student-home.html`)
- **Features:**
  - Welcome message
  - Exam status alert
  - Exam information cards (duration, questions, level)
  - Important information section
  - Sidebar navigation
  - Dark mode

- **Backend Integration:**
  - `GET /qcm/user-info` - Load student info
  - `GET /qcm/student-data` - Check exam status

### 3. Exam Page (`exam.html`)
- **Features:**
  - Question display with number indicator
  - Multiple choice radio button options
  - Progress tracker (X/10 questions answered)
  - Server-synchronized countdown timer (20 minutes)
  - Auto-submit on timeout
  - Submit confirmation dialog
  - Prevents page navigation during exam
  - Color-coded timer (normal → warning → danger)

- **Backend Integration:**
  - `GET /qcm/exam-questions` - Fetch randomized questions
  - `POST /qcm/exam-submit` - Submit answers and calculate score

### 4. Admin Dashboard (`admin-dashboard.html`)
- **Features:**
  - Key statistics (total students, questions, exams, average score)
  - Student distribution chart (bar chart by level)
  - Performance metrics by level (success rate)
  - Recent activity section
  - Admin sidebar navigation

- **Backend Integration:**
  - `GET /qcm/admin-dashboard-data` - Fetch dashboard statistics

### 5. Admin Pages (Additional)
- **QCM Management** - Add/edit/delete questions with level filtering
- **Students List** - Search and filter students by level
- **Results** - View exam results with statistics
- **Ranking** - Leaderboard by merit (top 3 podium + full list)

## Design System

### Color Palette
- **Primary:** #5B4BF5 (Indigo/Blue)
- **Primary Dark:** #4A3AC8
- **Background Light:** #F5F7FC
- **Surface Light:** #FFFFFF
- **Text Primary:** #1F2937
- **Text Secondary:** #6B7280
- **Border:** #E5E7EB
- **Success:** #10B981 (Green)
- **Warning:** #F59E0B (Amber)
- **Error:** #EF4444 (Red)

### Dark Mode
All pages support dark mode with CSS variables automatically switching colors.

### Typography
- **Font Stack:** System fonts (SF Pro Display, Segoe UI, etc.)
- **Font Sizes:** Hierarchical scale from 12px to 32px
- **Font Weights:** 400 (regular), 500 (medium), 600 (semibold), 700 (bold)

## Key Features

### 1. Responsive Design
- Mobile-first approach
- Breakpoints: 480px, 768px, 1024px
- Flexible grid layouts
- Touch-friendly interactive elements

### 2. Dark Mode
- Automatic theme detection from localStorage
- Toggle button in all pages
- Persistent user preference
- Full color scheme adaptation

### 3. Exam System
- **Server-Synchronized Timer:** Countdown starts when exam loads, not client-side
- **Auto-Save:** Answers saved to localStorage during exam
- **Auto-Submit:** Automatically submits when timer reaches 0
- **Confirmation:** User must confirm submission
- **Prevention:** Warns user before leaving during exam

### 4. Form Validation
- HTML5 validation attributes
- Visual feedback on invalid inputs
- Error handling on backend failures

### 5. Accessibility
- Semantic HTML structure
- ARIA labels where needed
- Keyboard navigation support
- High contrast colors for readability

## CSS Classes & Structure

### Layout
```html
<div class="wrapper">
  <aside class="sidebar">
    <nav class="sidebar-nav">
      <a class="nav-item active">Item</a>
    </nav>
  </aside>
  <main class="main-content">
    <header class="top-bar">
      <h1>Title</h1>
      <div class="top-bar-actions"></div>
    </header>
    <!-- Page content -->
  </main>
</div>
```

### Common Components
- `.btn` - Base button styles
- `.btn-primary` - Primary action button
- `.btn-secondary` - Secondary action button
- `.alert-box` - Alert container
- `.modal` / `.modal-overlay` - Modal dialog
- `.stat-card` - Statistics card
- `.tab-btn` / `.tab-content` - Tab interface

## JavaScript Functionality

### Login (`login.js`)
- Theme management (localStorage)
- Tab switching between login/register
- Form submission to backend
- URL navigation on success

### Layout (`layout.js`)
- Shared theme toggle
- User info loading
- Logout functionality
- Navigation setup

### Student Home (`student-home.js`)
- Dynamic student data loading
- Exam status checking
- Navigation between pages

### Exam (`exam.js`)
- Question loading from backend
- Timer management (decrements every second)
- Answer tracking (key: question index, value: option index)
- Option selection highlighting
- Auto-submit on timeout
- Exam submission with answer array

### Admin Dashboard (`admin-dashboard.js`)
- Dashboard statistics loading
- Chart rendering
- Real-time data refresh
- Navigation between admin pages

## API Endpoints Expected

```
POST /qcm/login
  Body: email, motdepasse
  Response: { success: boolean, role: string }

POST /qcm/register
  Body: numeroetudia, nom, prenoms, email, niveau
  Response: { success: boolean }

POST /qcm/logout
  Response: { success: boolean }

GET /qcm/user-info
  Response: { success: boolean, numeroetudia, nom, prenoms, email, niveau }

GET /qcm/student-data
  Response: { success: boolean, examStarted: boolean, ... }

GET /qcm/exam-questions
  Response: { success: boolean, questions: Array<{id, texte, niveau, options}> }

POST /qcm/exam-submit
  Body: { answers: Array<{questionId, selectedOption}>, timeSpent }
  Response: { success: boolean, score, result_url }

GET /qcm/admin-dashboard-data
  Response: { success: boolean, stats, distribution, performance }
```

## How to Integrate with Backend

1. **Update JSP files:** Copy HTML structure from these files into your JSP pages
2. **Link CSS/JS:** Update `<link>` and `<script>` tags to match your directory structure
3. **Session Management:** Ensure backend sets `user` session attribute on login
4. **AJAX Endpoints:** Implement the API endpoints above in your servlets
5. **Error Handling:** Add try-catch blocks in servlet methods

## Browser Support

- Chrome/Edge: Latest 2 versions
- Firefox: Latest 2 versions
- Safari: Latest 2 versions
- Mobile browsers: iOS Safari 12+, Chrome Mobile 80+

## Performance Optimizations

1. **CSS:** Minimal file sizes, no unused styles
2. **JavaScript:** Vanilla JS, no framework dependencies
3. **Images:** Using CSS for icons and shapes
4. **Caching:** localStorage for theme preference
5. **Network:** Efficient API calls with proper error handling

## Development Notes

- All JavaScript uses vanilla JS (no jQuery or frameworks)
- CSS uses custom properties (CSS variables) for theming
- Responsive design uses CSS Grid and Flexbox
- Event delegation for dynamic content
- Console logging with `[v0]` prefix for debugging

## Testing Checklist

- [ ] Login form submission
- [ ] Registration form validation
- [ ] Theme toggle persistence
- [ ] Exam timer countdown
- [ ] Option selection saving
- [ ] Auto-submit functionality
- [ ] Exam submission with answers
- [ ] Admin dashboard data loading
- [ ] Mobile responsiveness (480px, 768px)
- [ ] Dark mode functionality
- [ ] Logout functionality

## File Sizes

- login.html: ~6.5 KB
- login.css: ~12 KB
- login.js: ~4.5 KB
- layout.css: ~15 KB
- exam.html: ~5 KB
- exam.css: ~12 KB
- exam.js: ~8 KB
- **Total:** ~63 KB (uncompressed)

## Future Enhancements

1. Add admin pages for QCM management, student management, results, ranking
2. Implement real-time notifications
3. Add progress saved indicator
4. Implement answer review feature
5. Add exam history/past results view
6. Add question randomization visualization
