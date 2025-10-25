# 🧪 Payment Load Test Overview

## ⚙️ Key Features

### 🔧 User Variables (configurable at the top)
- `BASE_URL`: `http://localhost:8080` (change to your server)
- `THREADS`: 100 concurrent users
- `RAMP_UP`: 30 seconds to start all users
- `DURATION`: 300 seconds (5 minutes) test duration

### 🔁 Test Flow
1. Creates a payment with a `POST` request
2. Extracts the payment ID from the response
3. Waits 1 second (think time)
4. Retrieves the payment using a `GET` request with the extracted ID

### 🎲 Dynamic Data
- Random amounts between `$10–$500`
- Random payment methods:
    - `NET_BANKING`
    - `CREDIT_CARD`
    - `DEBIT_CARD`
    - `UPI`

### ✅ Assertions
- Verifies `POST` returns `201 Created`
- Verifies `GET` returns `200 OK`
- Checks response contains payment ID
- Validates the retrieved payment ID matches the created one

### 📊 Listeners (for viewing results)
- View Results Tree
- Summary Report
- Graph Results

## 🚀 How to Use
1. Save the file as `payment-load-test.jmx`
2. Open JMeter and load the file
3. Update the `BASE_URL` variable to match your server
4. Adjust thread count, ramp-up, and duration as needed
5. Run the test!
