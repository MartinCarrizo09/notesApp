#!/bin/bash

echo "🚀 Starting Ensolvers Notes App..."
echo ""
echo "📋 Prerequisites check:"
echo "  - Java 21+"
echo "  - Maven 3.9+"
echo "  - Node.js 18+"
echo "  - PostgreSQL running on localhost:5432"
echo "  - Database 'notes_db' created"
echo "  - PostgreSQL password: 'tpi' (configured in application.properties)"
echo ""

# Check if PostgreSQL is running
if command -v pg_isready > /dev/null 2>&1; then
    if ! pg_isready -h localhost -p 5432 -U postgres > /dev/null 2>&1; then
        echo "⚠️  Warning: PostgreSQL might not be running. Please ensure it's started."
        echo ""
    fi
else
    echo "⚠️  Note: pg_isready not found. Please ensure PostgreSQL is running."
    echo ""
fi

# Check if database exists (optional check)
echo "📝 Make sure you have created the database:"
echo "   CREATE DATABASE notes_db;"
echo ""

# Start backend in background
echo "🔧 Starting backend (Spring Boot)..."
echo "   This may take 30-60 seconds on first run..."
cd backend
# Create logs directory if it doesn't exist
mkdir -p logs
mvn clean spring-boot:run > logs/backend.log 2>&1 &
BACKEND_PID=$!
cd ..

# Wait a bit for backend to start
sleep 8

# Check if backend started successfully
if command -v curl > /dev/null 2>&1; then
    if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "⏳ Backend is still starting... (check backend/logs/backend.log for progress)"
    else
        echo "✅ Backend is running!"
    fi
else
    echo "⏳ Backend is starting... (check backend/logs/backend.log for details)"
fi

# Start frontend
echo ""
echo "🎨 Starting frontend (React + Vite)..."
cd frontend

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies (this may take a minute)..."
    npm install
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Application is starting!"
echo ""
echo "   🌐 Frontend: http://localhost:5173"
echo "   🔧 Backend:  http://localhost:8080"
echo "   📚 Swagger:  http://localhost:8080/swagger-ui.html"
echo ""
echo "   👤 Login credentials:"
echo "      Username: admin"
echo "      Password: admin123"
echo ""
echo "   Press Ctrl+C to stop both servers"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

npm run dev

# Cleanup on exit
trap "echo ''; echo '🛑 Stopping servers...'; kill $BACKEND_PID 2>/dev/null; exit" EXIT INT TERM
