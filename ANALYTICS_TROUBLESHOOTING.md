# Live Analytics Feature - Troubleshooting Guide

## 🎯 Application URLs

### Main Application
- **Base URL:** `http://localhost:9097`
- **Login Page:** `http://localhost:9097/login`
- **Admin Dashboard:** `http://localhost:9097/admin/dashboard`

### Analytics URLs
- **Analytics Dashboard:** `http://localhost:9097/admin/analytics/dashboard/{electionId}`
- **Live Votes API:** `http://localhost:9097/admin/analytics/api/live-votes/{electionId}`
- **Vote Distribution API:** `http://localhost:9097/admin/analytics/api/vote-distribution/{electionId}`
- **Election Summary API:** `http://localhost:9097/admin/analytics/api/election-summary/{electionId}`

---

## 🔧 Live Analytics Feature Overview

The live analytics dashboard provides **real-time vote tracking** with:
- ✅ **Live Vote Counts** - Updated every 2 seconds
- ✅ **Bar Charts** - Visual representation of vote distribution
- ✅ **Pie Charts** - Percentage-based vote breakdown
- ✅ **Candidate Rankings** - Sorted by vote count with medals
- ✅ **Statistics Cards** - Total votes, leading candidate, etc.

---

## 📊 Why Analytics Might Not Record Data

### Issue 1: No Votes Have Been Cast Yet
**Symptom:** Analytics dashboard shows "No votes yet"

**Solution:**
1. Log in as STUDENT user
2. Go to an OPEN election
3. Cast a vote for a candidate
4. Return to admin dashboard
5. View analytics - data should now appear

### Issue 2: Election Status is Not OPEN
**Symptom:** Students cannot vote, analytics stays empty

**Solution:**
1. Log in as ADMIN
2. Go to **Manage Elections**
3. Find the election
4. Click **"Open"** button to change status from CREATED to OPEN
5. Now students can vote

### Issue 3: No Candidates Added to Election
**Symptom:** Analytics shows candidates but vote counts are 0

**Solution:**
1. Log in as ADMIN
2. Go to **Manage Candidates**
3. Click **"Add Candidate"**
4. Select the election
5. Enter candidate name
6. Click **Add Candidate**
7. Repeat for all candidates needed

### Issue 4: Browser Cache or Session Issues
**Symptom:** Analytics loads but data isn't updating

**Solution:**
1. **Clear browser cache:** Press `Ctrl+Shift+Del` (or `Cmd+Shift+Del` on Mac)
2. **Clear cookies for localhost:9097**
3. **Close and reopen browser**
4. **Log out and log in again**

### Issue 5: Analytics API Returns 403 Forbidden
**Symptom:** Console shows "Access denied" error

**Solution:**
- Ensure you are logged in as ADMIN user
- Session may have expired - log out and log back in
- Check that `role` is set to "ADMIN" in session

---

## 🐛 Debugging Steps

### Step 1: Check Server Logs
```bash
# Watch logs in real-time
tail -f app.log

# Look for [ANALYTICS] messages to track API calls
grep "\[ANALYTICS\]" app.log
```

### Step 2: Check Browser Console
1. Open browser DevTools (`F12`)
2. Go to **Console** tab
3. Look for any JavaScript errors
4. Go to **Network** tab
5. Look for failed requests to `/admin/analytics/api/live-votes/`

### Step 3: Verify Vote Data in Database
```bash
# Connect to MySQL
mysql -u e_voting -p voting_db

# Check votes table
SELECT * FROM vote;

# Check if votes exist for election
SELECT COUNT(*) FROM vote WHERE election_id = 1;

# Check candidates
SELECT * FROM candidate;
```

### Step 4: Test API Directly
```bash
# After logging in, test the API
curl -b "JSESSIONID=your_session_id" \
  http://localhost:9097/admin/analytics/api/live-votes/1

# Should return JSON with vote data
```

---

## 📋 Complete Setup Checklist

- [ ] **Database Created:** `voting_db` exists
- [ ] **Tables Created:** Spring Boot auto-creates them on first run
- [ ] **Admin User Created:** Log in with admin credentials
- [ ] **Election Created:** Go to Manage Elections, create one
- [ ] **Election Opened:** Change status from CREATED to OPEN
- [ ] **Candidates Added:** Add at least 2-3 candidates
- [ ] **Student Votes Cast:** Log in as student, vote in the election
- [ ] **Analytics Loaded:** View analytics dashboard for the election
- [ ] **Data Visible:** Charts and statistics should show vote data

---

## 🚀 How to Use Live Analytics

### Step-by-Step Guide

1. **Log in as Admin**
   - URL: `http://localhost:9097/login`
   - Username: admin
   - Password: admin123

2. **Create Election**
   - Click "Manage Elections"
   - Enter election name (e.g., "Student President")
   - Click "Create Election"

3. **Add Candidates**
   - Click "Manage Candidates"
   - Click "Add Candidate"
   - Select the election
   - Enter candidate name
   - Click "Add Candidate"
   - Repeat for all candidates (at least 2)

4. **Open Election for Voting**
   - Go to "Manage Elections"
   - Find your election
   - Click "Open" button
   - Status should change to "OPEN"

5. **Students Vote**
   - Log in as STUDENT (separate browser/incognito)
   - Click on the election
   - Select a candidate
   - Confirm vote
   - Repeat with different students

6. **View Live Analytics**
   - Go back to ADMIN dashboard
   - Scroll to "Live Analytics" section
   - Select election from dropdown
   - Click "View Analytics"
   - **Data updates automatically every 2 seconds!**

---

## 📊 What the Dashboard Shows

### Statistics Cards
- **Total Votes:** Number of votes cast
- **Candidates:** Total candidates in election
- **Leading Candidate:** Candidate with most votes
- **Highest Vote Count:** Maximum votes any candidate has

### Charts
- **Bar Chart:** Vote count for each candidate
- **Pie Chart:** Percentage distribution of votes

### Candidate Rankings
- Shows ranked list with medals (🥇🥈🥉)
- Sorted by vote count (highest first)
- Real-time updates

---

## 🔴 Common Errors & Solutions

### Error: "No votes yet"
- **Cause:** No votes have been cast
- **Fix:** Have students vote in the open election

### Error: "Access denied"
- **Cause:** Not logged in as ADMIN
- **Fix:** Log in again, ensure role is ADMIN

### Error: "Cannot read property 'data' of undefined"
- **Cause:** API returned error
- **Fix:** Check server logs, verify election exists

### Error: Charts don't update
- **Cause:** JavaScript issue
- **Fix:** Refresh page, clear cache, check console

### Error: "election.status is undefined"
- **Cause:** Election data not loaded properly
- **Fix:** Return to admin dashboard and try again

---

## 📈 Analytics API Endpoints

All require ADMIN role.

### Get Live Vote Data
```
GET /admin/analytics/api/live-votes/{electionId}

Response:
{
  "success": true,
  "data": [
    {"name": "Candidate A", "votes": 5, "id": 1},
    {"name": "Candidate B", "votes": 3, "id": 2}
  ],
  "totalVotes": 8,
  "timestamp": 1674814070000
}
```

### Get Vote Distribution
```
GET /admin/analytics/api/vote-distribution/{electionId}

Response:
{
  "success": true,
  "data": [
    {"name": "Candidate A", "votes": 5, "percentage": 62.5},
    {"name": "Candidate B", "votes": 3, "percentage": 37.5}
  ],
  "totalVotes": 8,
  "candidateCount": 2
}
```

### Get Election Summary
```
GET /admin/analytics/api/election-summary/{electionId}

Response:
{
  "success": true,
  "electionName": "Student President",
  "electionStatus": "OPEN",
  "totalVotes": 8,
  "candidateCount": 2,
  "leadingCandidate": "Candidate A",
  "maxVotes": 5,
  "timestamp": 1674814070000
}
```

---

## 🔍 Server-Side Logging

The updated AnalyticsController includes detailed logging:

```
[ANALYTICS] Dashboard - Election: 1, Candidates: 3, Results: {...}
[ANALYTICS] getLiveVotes called - ElectionId: 1, Role: ADMIN
[ANALYTICS] Election Results: {Candidate A=5, Candidate B=3}
[ANALYTICS] Candidates count: 2
[ANALYTICS] Candidate: Candidate A, Votes: 5
[ANALYTICS] Candidate: Candidate B, Votes: 3
[ANALYTICS] Returning response with 2 candidates, total votes: 8
```

Check these in `app.log` to debug issues.

---

## 🎓 Test Scenario

### Quick Test to Verify Everything Works

1. **Reset Database** (optional)
   ```bash
   # Delete and recreate
   docker-compose down -v
   docker-compose up --build
   ```

2. **Create Test Data**
   - Admin: admin / admin123
   - Student 1: student1 / student123
   - Student 2: student2 / student123

3. **Run Test Flow**
   - Create election: "Test Election"
   - Add candidates: "Alice", "Bob", "Charlie"
   - Open election for voting
   - Student 1 votes for "Alice"
   - Student 2 votes for "Bob"
   - Admin views analytics
   - Should see: Alice 1 vote, Bob 1 vote, Charlie 0 votes

4. **Verify Dashboard Updates**
   - Add another vote from different student
   - Analytics should update within 2 seconds
   - Charts and statistics should refresh

---

## 📞 Still Having Issues?

1. **Check application logs:**
   ```bash
   tail -100 app.log | grep -i analytics
   ```

2. **Verify database has vote data:**
   ```sql
   SELECT e.name, c.name, COUNT(v.id) as votes
   FROM election e
   LEFT JOIN candidate c ON c.election_id = e.id
   LEFT JOIN vote v ON v.candidate_id = c.id
   GROUP BY e.id, c.id
   ORDER BY e.id, votes DESC;
   ```

3. **Test API with curl:**
   ```bash
   # Get admin session cookie first, then:
   curl http://localhost:9097/admin/analytics/api/live-votes/1
   ```

4. **Clear everything and restart:**
   ```bash
   docker-compose down -v  # Delete all data
   docker-compose up --build  # Fresh start
   ```

---

## ✅ Success Indicators

You'll know it's working when:
- ✅ Analytics dashboard loads without errors
- ✅ Charts display with candidate names
- ✅ Vote counts update in real-time
- ✅ Candidate rankings show correct order
- ✅ Statistics cards show total votes
- ✅ No "Access denied" or 403 errors
- ✅ Live indicator shows "Live" with pulsing dot

---

*Last Updated: January 27, 2026*
*Analytics Feature Version: 1.1*
