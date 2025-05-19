FROM python:3.10-slim

# Set working directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    git \
    gcc \
    libffi-dev \
    libpq-dev \
    default-libmysqlclient-dev \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Install DVC
RUN pip install --upgrade pip
RUN pip install dvc[mysql] 

# Copy requirements and install Python packages
COPY requirements.txt .
RUN pip install -r requirements.txt

# Copy the application code
COPY . .

# Default command
CMD ["python", "app.py"]
